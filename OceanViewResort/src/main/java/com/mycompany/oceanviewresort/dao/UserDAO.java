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
import com.mycompany.oceanviewresort.model.User;
import com.mycompany.oceanviewresort.model.UserDTO;
import com.mycompany.oceanviewresort.util.DB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    
    // Authenticates user for login
    public User authenticateUser(String username, String password) {
        User user = null;
        String query = "SELECT user_id, username, full_name, role FROM users WHERE username = ? AND password_hash = ? AND is_active = TRUE";
        
        try (Connection con = DB.getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {
            
            pst.setString(1, username);
            pst.setString(2, password); 
            
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

    // Fetches all users for Admin management
    public List<UserDTO> getAllUsers() {
        List<UserDTO> users = new ArrayList<>();
        String query = "SELECT * FROM users ORDER BY created_at DESC";
        
        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                UserDTO user = new UserDTO();
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setFullName(rs.getString("full_name"));
                user.setEmail(rs.getString("email"));
                user.setPhone(rs.getString("phone"));
                user.setRole(rs.getString("role"));
                user.setActive(rs.getBoolean("is_active"));
                users.add(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return users;
    }

    // Adds a new system user
    public boolean addUser(UserDTO user) {
        String sql = "INSERT INTO users (username, password_hash, full_name, email, phone, role, is_active) VALUES (?, ?, ?, ?, ?, ?, TRUE)";
        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword()); 
            ps.setString(3, user.getFullName());
            ps.setString(4, user.getEmail());
            ps.setString(5, user.getPhone());
            ps.setString(6, user.getRole());
            
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Soft deletes a user by setting is_active to FALSE
    public boolean deactivateUser(int userId) {
        String sql = "UPDATE users SET is_active = FALSE WHERE user_id = ?";
        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
}