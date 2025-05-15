package Utils;

import dao.UserDAO;
import dto.UserDTO;
import java.security.MessageDigest;
import java.util.List;

public class PasswordUtils {
     public static String hashPassword(String plainPassword) {
        try {
            
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            
            byte[] messageDigest = md.digest(plainPassword.getBytes());

            
            StringBuilder hexString = new StringBuilder();
            for (byte b : messageDigest) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        String newHashedPassword = hashPassword(plainPassword);
        return newHashedPassword != null && newHashedPassword.equals(hashedPassword);
    }

    public static void migratePasswords() {
        UserDAO dao = new UserDAO();
        List<UserDTO> users = dao.readAll(); 

        for (UserDTO user : users) {
            
            String plainPassword = user.getPassword();

            
            String hashedPassword = PasswordUtils.hashPassword(plainPassword);

            
            user.setPassword(hashedPassword);

           
            dao.update(user);
        }

        System.out.println("Di chuyển mật khẩu sang MD5 hoàn tất");
    }
    
    public static void main(String[] args) {
        migratePasswords();
    }
}
