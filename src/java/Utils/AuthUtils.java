package utils;

import dao.UserDAO;
import dto.UserDTO;
import javax.servlet.http.HttpSession;

public class AuthUtils {
    public static final String ADMIN_ROLE = "AD";
    public static final String USER_ROLE = "US";
    
    
    public static UserDTO getUserDTO(String strUserID) {
        UserDAO udao = new UserDAO();
        UserDTO user = udao.readById(strUserID);
        return user;
    }
    
    public static UserDTO getUserDTO(HttpSession session){
//        if (session.getAttribute("user") != null) {
//            return (UserDTO) session.getAttribute("user");
//        }else{
//            return null;
//        }
        Object obj = session.getAttribute("user");
        return (obj != null) ? (UserDTO) obj : null;
    }

    
    public static boolean isValidLogin(String strUserID, String strPassword) {
        UserDTO user = getUserDTO(strUserID);
        return user != null && PasswordUtils.checkPassword(strPassword, user.getPassword());
    }
    
    /**
     * 
     * @param session
     * @return 
     */
    
    public static boolean isLoggedIn(HttpSession session){
        return session.getAttribute("user") != null;
    }
    
    /**
     * 
     * @param session
     * @return 
     */
    
    public static boolean isAdmin(HttpSession session){
        if (!isLoggedIn(session)) {
            return false;
        }
        UserDTO user = (UserDTO) session.getAttribute("user");
        return user.getRoleID().equals(ADMIN_ROLE);
    }
}
