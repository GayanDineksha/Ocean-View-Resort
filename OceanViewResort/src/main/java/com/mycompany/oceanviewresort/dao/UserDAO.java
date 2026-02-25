/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.oceanviewresort.dao;

import com.mycompany.oceanviewresort.model.User;
import com.mycompany.oceanviewresort.util.DB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
/**
 *
 * @author User
 */
public class UserDAO {
    
    public User authenticateUser(String username, String password) {
        User user = null;
        // Query to check username, password and if the user is active
        String query = "SELECT user_id, username, full_name, role FROM users WHERE username = ? AND password_hash = ? AND is_active = TRUE";
        
        try (Connection con = DB.getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {
            
            pst.setString(1, username);
            pst.setString(2, password); // Note: For excellent marks, we will later upgrade this to check hashed passwords!
            
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    user = new User(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("full_name"),
                        rs.getString("role")
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }
}
