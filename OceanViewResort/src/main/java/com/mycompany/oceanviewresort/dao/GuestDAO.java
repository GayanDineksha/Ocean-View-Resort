/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.oceanviewresort.dao;

/**
 *
 * @author User
 */
import com.mycompany.oceanviewresort.model.GuestDTO;
import com.mycompany.oceanviewresort.util.DB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class GuestDAO {

    // Fetch all active guests (not deleted)
    public List<GuestDTO> getAllActiveGuests() {
        List<GuestDTO> guests = new ArrayList<>();
        String query = "SELECT * FROM guests WHERE is_deleted = FALSE ORDER BY guest_name ASC";
        
        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                GuestDTO guest = new GuestDTO();
                guest.setGuestId(rs.getInt("guest_id"));
                guest.setGuestName(rs.getString("guest_name"));
                guest.setNicPassport(rs.getString("nic_passport"));
                guest.setContactNumber(rs.getString("contact_number"));
                guest.setEmail(rs.getString("email"));
                guest.setGuestType(rs.getString("guest_type"));
                guests.add(guest);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return guests;
    }

    // Soft delete (Archive) a guest
    public boolean archiveGuest(int guestId) {
        String query = "UPDATE guests SET is_deleted = TRUE, deleted_at = CURRENT_TIMESTAMP WHERE guest_id = ?";
        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            
            ps.setInt(1, guestId);
            return ps.executeUpdate() > 0;
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
