package dao;

import dto.UserDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import utils.DBUtils;

public class UserDAO implements IDAO<UserDTO, String>{

    /**
     * 
     * @param entity
     * @return 
     */
    @Override
    public boolean create(UserDTO entity) {
        return false;
    }

    /**
     * 
     * @return 
     */
    @Override
    public List<UserDTO> readAll() {
        List<UserDTO> list = new ArrayList<>();
        String query = "SELECT * FROM tblUsers";
        try {
            Connection conn = DBUtils.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {                
                UserDTO user = new UserDTO(
                        rs.getString("userID"), 
                        rs.getString("fullName"), 
                        rs.getString("roleID"), 
                        rs.getString("password"));
                list.add(user);
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return list;
    }

    /**
     * 
     * @param id
     * @return 
     */
    @Override
    public UserDTO readById(String id) {
        String sql = "SELECT * FROM tblUsers WHERE userID = ?";
        try {
            Connection conn = DBUtils.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {                
                UserDTO user = new UserDTO(
                        rs.getString("userID"), 
                        rs.getString("fullName"), 
                        rs.getString("roleID"), 
                        rs.getString("password"));
                return user;
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * 
     * @param entity
     * @return 
     */
    @Override
    public boolean update(UserDTO entity) {
        String query = "UPDATE tblUsers SET "
                + "fullName = ?,"
                + "roleID = ?,"
                + "password = ?"
                + " WHERE userID = ?";
        try {
            Connection conn = DBUtils.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, entity.getFullName());
            ps.setString(2, entity.getRoleID());
            ps.setString(3, entity.getPassword());
            ps.setString(4, entity.getUserID());
            int i = ps.executeUpdate();
            return i > 0;
        } catch (Exception e) {
        }
        return false;
    }

    /**
     * 
     * @param id
     * @return 
     */
    @Override
    public boolean delete(String id) {
        return false;
    }

    /**
     * 
     * @param searchTerm
     * @return 
     */
    @Override
    public List<UserDTO> search(String searchTerm) {
        return null;
    }
    
}
