package com.mycompany.oceanviewresort.dao;

import com.mycompany.oceanviewresort.model.ReservationDTO;
import com.mycompany.oceanviewresort.util.DB;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author User
 */



public class ReservationDAO {

    // 1. Find existing guest by NIC/Passport, or create a new one
    public long findOrCreateGuest(ReservationDTO dto) {
        long guestId = -1;
        String checkQuery = "SELECT guest_id FROM guests WHERE nic_passport = ?";
        
        try (Connection con = DB.getConnection()) {
            try (PreparedStatement checkStmt = con.prepareStatement(checkQuery)) {
                checkStmt.setString(1, dto.getNicPassport());
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next()) {
                    guestId = rs.getLong("guest_id");
                } else {
                    String insertQuery = "INSERT INTO guests (guest_name, nic_passport, contact_number, email) VALUES (?, ?, ?, ?)";
                    try (PreparedStatement insertStmt = con.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
                        insertStmt.setString(1, dto.getGuestName());
                        insertStmt.setString(2, dto.getNicPassport());
                        insertStmt.setString(3, dto.getContactNumber());
                        insertStmt.setString(4, dto.getEmail());
                        insertStmt.executeUpdate();
                        ResultSet keys = insertStmt.getGeneratedKeys();
                        if (keys.next()) {
                            guestId = keys.getLong(1); 
                        }
                    }
                }
            }
        } catch (Exception e) { 
            e.printStackTrace(); 
        }
        return guestId;
    }

    // 2. Create Reservation using Stored Procedure
    public ReservationDTO createReservation(ReservationDTO dto) {
        long guestId = findOrCreateGuest(dto);
        if (guestId == -1) return null;
        dto.setGuestId(guestId);

        String query = "{CALL sp_create_reservation(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}"; 
        
        try (Connection con = DB.getConnection();
             CallableStatement cs = con.prepareCall(query)) {
             
            cs.setLong(1, dto.getGuestId());
            cs.setLong(2, dto.getRoomId());
            cs.setDate(3, Date.valueOf(dto.getCheckInDate()));
            cs.setDate(4, Date.valueOf(dto.getCheckOutDate()));
            cs.setInt(5, dto.getAdults());
            cs.setInt(6, dto.getChildren());
            cs.setString(7, dto.getSpecialRequests() != null ? dto.getSpecialRequests() : "None");
            cs.setLong(8, dto.getCreatedBy());
            
            cs.registerOutParameter(9, Types.BIGINT);
            cs.registerOutParameter(10, Types.VARCHAR);
            
            cs.execute();
            
            dto.setReservationId(cs.getLong(9));
            dto.setReservationNumber(cs.getString(10));
            
            return dto;
            
        } catch (SQLException e) { 
            System.err.println("Reservation failed: " + e.getMessage());
            e.printStackTrace(); 
            return null;
        }
    }

    // 3. Get all active reservations (With Room Details Join)
    public List<ReservationDTO> getAllReservations() {
        List<ReservationDTO> list = new ArrayList<>();
        String query = "SELECT r.reservation_id, r.reservation_number, r.adults, r.children, r.special_requests, " +
                       "g.guest_id, g.guest_name, g.nic_passport, g.contact_number, g.email, " +
                       "rm.room_id, rm.room_number, rt.room_type_name, r.check_in_date, r.check_out_date, r.reservation_status " +
                       "FROM reservations r " +
                       "JOIN guests g ON r.guest_id = g.guest_id " +
                       "JOIN rooms rm ON r.room_id = rm.room_id " +
                       "JOIN room_types rt ON rm.room_type_id = rt.room_type_id " +
                       "WHERE r.is_deleted = FALSE ORDER BY r.check_in_date DESC";

        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
             
            while (rs.next()) {
                ReservationDTO dto = new ReservationDTO();
                dto.setReservationId(rs.getLong("reservation_id"));
                dto.setReservationNumber(rs.getString("reservation_number"));
                dto.setGuestId(rs.getLong("guest_id"));
                dto.setGuestName(rs.getString("guest_name"));
                dto.setNicPassport(rs.getString("nic_passport"));
                dto.setContactNumber(rs.getString("contact_number"));
                dto.setEmail(rs.getString("email"));
                dto.setRoomId(rs.getLong("room_id"));
                
                // Set the formatted room details (e.g., "101 (Deluxe Ocean View)")
                dto.setRoomDetails(rs.getString("room_number") + " (" + rs.getString("room_type_name") + ")");
                
                dto.setCheckInDate(rs.getString("check_in_date"));
                dto.setCheckOutDate(rs.getString("check_out_date"));
                dto.setAdults(rs.getInt("adults"));
                dto.setChildren(rs.getInt("children"));
                dto.setSpecialRequests(rs.getString("special_requests"));
                
                String rawStatus = rs.getString("reservation_status");
                // Capitalize the status (e.g., "checked_in" -> "Checked in")
                dto.setReservationStatus(rawStatus.replace("_", " ").substring(0, 1).toUpperCase() + rawStatus.replace("_", " ").substring(1)); 
                list.add(dto);
            }
        } catch (Exception e) { 
            e.printStackTrace(); 
        }
        return list;
    }
    
    // 4. Update Status (Check-in, Check-out, Cancelled)
    public boolean updateReservationStatus(long reservationId, String newStatus) {
        String query = "UPDATE reservations SET reservation_status = ? WHERE reservation_id = ?";
        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, newStatus);
            ps.setLong(2, reservationId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { 
            e.printStackTrace(); 
            return false; 
        }
    }

    // 5. Update Booking and Guest Details
    public boolean updateBookingAndGuestDetails(ReservationDTO dto) {
        Connection con = null;
        try {
            con = DB.getConnection();
            con.setAutoCommit(false); 

            // Update Guest
            String updateGuest = "UPDATE guests SET guest_name=?, nic_passport=?, contact_number=?, email=? WHERE guest_id=?";
            try (PreparedStatement ps1 = con.prepareStatement(updateGuest)) {
                ps1.setString(1, dto.getGuestName());
                ps1.setString(2, dto.getNicPassport());
                ps1.setString(3, dto.getContactNumber());
                ps1.setString(4, dto.getEmail());
                ps1.setLong(5, dto.getGuestId());
                ps1.executeUpdate();
            }

            // Update Reservation
            String updateRes = "UPDATE reservations SET check_out_date=?, adults=?, children=? WHERE reservation_id=?";
            try (PreparedStatement ps2 = con.prepareStatement(updateRes)) {
                ps2.setDate(1, Date.valueOf(dto.getCheckOutDate()));
                ps2.setInt(2, dto.getAdults());
                ps2.setInt(3, dto.getChildren());
                ps2.setLong(4, dto.getReservationId());
                ps2.executeUpdate();
            }

            con.commit(); 
            return true;
            
        } catch (Exception e) {
            if (con != null) try { con.rollback(); } catch (Exception ex) {} 
            e.printStackTrace();
            return false;
        } finally {
            if (con != null) try { con.setAutoCommit(true); con.close(); } catch (Exception ex) {}
        }
    }
}