package com.mycompany.oceanviewresort.dao;

import com.mycompany.oceanviewresort.model.Guest;
import com.mycompany.oceanviewresort.model.Reservation;
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
        } catch (Exception e) { e.printStackTrace(); }
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
                if (rs.next()) isAvailable = rs.getBoolean(1); 
            }
        } catch (Exception e) { e.printStackTrace(); }
        return isAvailable;
    }

    public boolean createReservation(Reservation res) {
        String sql = "INSERT INTO reservations (reservation_number, guest_id, room_id, check_in_date, check_out_date, adults, children, special_requests, reservation_status, created_by) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, 1)";        
        try (Connection con = DB.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) { 
            pst.setString(1, "RES-" + System.currentTimeMillis());
            pst.setInt(2, res.getGuestId());
            pst.setInt(3, res.getRoomId());
            pst.setDate(4, res.getCheckInDate());
            pst.setDate(5, res.getCheckOutDate());
            pst.setInt(6, res.getAdults());
            pst.setInt(7, res.getChildren());
            pst.setString(8, "None");       
            pst.setString(9, "pending");    
            return pst.executeUpdate() > 0; 
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    public List<ReservationDTO> getAllReservations() {
        List<ReservationDTO> list = new ArrayList<>();
        String query = "SELECT r.reservation_id, r.reservation_number, r.adults, r.children, " +
                       "g.guest_id, g.guest_name, g.nic_passport, g.contact_number, g.email, " +
                       "rm.room_number, rt.room_type_name, r.check_in_date, r.check_out_date, r.reservation_status " +
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
                dto.setReservationId(rs.getInt("reservation_id"));
                dto.setReservationNumber(rs.getString("reservation_number"));
                dto.setGuestId(rs.getInt("guest_id"));
                dto.setGuestName(rs.getString("guest_name"));
                dto.setGuestNic(rs.getString("nic_passport"));
                dto.setGuestPhone(rs.getString("contact_number"));
                dto.setGuestEmail(rs.getString("email"));
                dto.setRoomDetails(rs.getString("room_number") + " (" + rs.getString("room_type_name") + ")");
                dto.setCheckIn(rs.getString("check_in_date"));
                dto.setCheckOut(rs.getString("check_out_date"));
                dto.setAdults(rs.getInt("adults"));
                dto.setChildren(rs.getInt("children"));
                
                String rawStatus = rs.getString("reservation_status");
                dto.setStatus(rawStatus.replace("_", " ").substring(0, 1).toUpperCase() + rawStatus.replace("_", " ").substring(1));
                list.add(dto);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
    
    public boolean updateReservationStatus(int reservationId, String newStatus) {
        String query = "UPDATE reservations SET reservation_status = ? WHERE reservation_id = ?";
        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, newStatus);
            ps.setInt(2, reservationId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

  
    public boolean updateBookingAndGuestDetails(ReservationDTO dto) {
        Connection con = null;
        try {
            con = DB.getConnection();
            con.setAutoCommit(false); 

            // 1. Guest Update 
            String updateGuest = "UPDATE guests SET guest_name=?, nic_passport=?, contact_number=?, email=? WHERE guest_id=?";
            try (PreparedStatement ps1 = con.prepareStatement(updateGuest)) {
                ps1.setString(1, dto.getGuestName());
                ps1.setString(2, dto.getGuestNic());
                ps1.setString(3, dto.getGuestPhone());
                ps1.setString(4, dto.getGuestEmail());
                ps1.setInt(5, dto.getGuestId());
                ps1.executeUpdate();
            }

            // 2. Reservation  Update Check-out Date, Adults, Children)
            String updateRes = "UPDATE reservations SET check_out_date=?, adults=?, children=? WHERE reservation_id=?";
            try (PreparedStatement ps2 = con.prepareStatement(updateRes)) {
                ps2.setDate(1, Date.valueOf(dto.getCheckOut()));
                ps2.setInt(2, dto.getAdults());
                ps2.setInt(3, dto.getChildren());
                ps2.setInt(4, dto.getReservationId());
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