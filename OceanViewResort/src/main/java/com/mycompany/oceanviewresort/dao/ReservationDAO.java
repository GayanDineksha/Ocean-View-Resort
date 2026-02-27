/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.oceanviewresort.dao;

import com.mycompany.oceanviewresort.model.Guest;
import com.mycompany.oceanviewresort.model.Reservation;
import com.mycompany.oceanviewresort.util.DB;
import java.sql.*;
/**
 *
 * @author User
 */
public class ReservationDAO {

    
    public int findOrCreateGuest(Guest guest) {
        int guestId = -1;
        String checkQuery = "SELECT guest_id FROM guests WHERE nic_passport = ?";
        
        try (Connection con = DB.getConnection();
             PreparedStatement checkStmt = con.prepareStatement(checkQuery)) {
            
            checkStmt.setString(1, guest.getNicPassport());
            ResultSet rs = checkStmt.executeQuery();
            
            if (rs.next()) {
                
                guestId = rs.getInt("guest_id");
            } else {
                
                String insertQuery = "INSERT INTO guests (guest_name, nic_passport, contact_number, email) VALUES (?, ?, ?, ?)";
                try (PreparedStatement insertStmt = con.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
                    insertStmt.setString(1, guest.getGuestName());
                    insertStmt.setString(2, guest.getNicPassport());
                    insertStmt.setString(3, guest.getContactNumber());
                    insertStmt.setString(4, guest.getEmail());
                    
                    insertStmt.executeUpdate();
                    ResultSet keys = insertStmt.getGeneratedKeys();
                    if (keys.next()) {
                        guestId = keys.getInt(1); 
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return guestId;
    }

    
    public boolean checkRoomAvailability(int roomId, Date checkIn, Date checkOut) {
        boolean isAvailable = false;
       
        String query = "{CALL sp_check_room_availability(?, ?, ?)}"; 
        
        try (Connection con = DB.getConnection();
             CallableStatement cs = con.prepareCall(query)) {
            
            cs.setInt(1, roomId);
            cs.setDate(2, checkIn);
            cs.setDate(3, checkOut);
            
            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) {
                    isAvailable = rs.getBoolean(1); // 1 = Available, 0 = Not Available
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isAvailable;
    }

   
    public boolean createReservation(Reservation res) {
        String sql = "INSERT INTO reservations (reservation_number, guest_id, room_id, check_in_date, check_out_date, adults, children, special_requests, reservation_status, created_by) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, 1)";        
        try (Connection con = DB.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) { // මෙතන 'query' එක 'sql' කරා
            
            String resNo = "RES-" + System.currentTimeMillis(); 
            
            pst.setString(1, resNo);
            pst.setInt(2, res.getGuestId());
            pst.setInt(3, res.getRoomId());
            pst.setDate(4, res.getCheckInDate());
            pst.setDate(5, res.getCheckOutDate());
            pst.setInt(6, res.getAdults());
            pst.setInt(7, res.getChildren());
            pst.setString(8, "None");       // 8 වෙනියට special_requests දැම්මා
            pst.setString(9, "pending");    // 9 වෙනියට reservation_status දැම්මා
            
            int rows = pst.executeUpdate();
            return rows > 0; 
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
