/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.oceanviewresort.dao;

/**
 *
 * @author User
 */
import com.mycompany.oceanviewresort.model.RoomDTO;
import com.mycompany.oceanviewresort.util.DB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class RoomDAO {

    // Fetch all rooms with their type details using a JOIN query
    public List<RoomDTO> getAllRooms() {
        List<RoomDTO> rooms = new ArrayList<>();
        String query = "SELECT r.room_id, r.room_number, r.room_type_id, r.floor_number, r.view_type, r.status, r.is_active, " +
                       "rt.room_type_name, rt.base_price, rt.max_occupancy " +
                       "FROM rooms r JOIN room_types rt ON r.room_type_id = rt.room_type_id " +
                       "ORDER BY r.room_number ASC";
        
        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                RoomDTO room = new RoomDTO();
                room.setRoomId(rs.getInt("room_id"));
                room.setRoomNumber(rs.getString("room_number"));
                room.setRoomTypeId(rs.getInt("room_type_id"));
                room.setFloorNumber(rs.getInt("floor_number"));
                room.setViewType(rs.getString("view_type"));
                room.setStatus(rs.getString("status"));
                room.setActive(rs.getBoolean("is_active"));
                room.setRoomTypeName(rs.getString("room_type_name"));
                room.setBasePrice(rs.getDouble("base_price"));
                room.setMaxOccupancy(rs.getInt("max_occupancy"));
                rooms.add(room);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rooms;
    }

    // Add a new room to the system
    public boolean addRoom(RoomDTO room) {
        String sql = "INSERT INTO rooms (room_number, room_type_id, floor_number, view_type, status, is_active) VALUES (?, ?, ?, ?, 'available', TRUE)";
        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, room.getRoomNumber());
            ps.setInt(2, room.getRoomTypeId());
            ps.setInt(3, room.getFloorNumber());
            ps.setString(4, room.getViewType());
            
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Update only the room status (e.g., to maintenance)
    public boolean updateRoomStatus(int roomId, String status) {
        String query = "UPDATE rooms SET status = ? WHERE room_id = ?";
        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            
            ps.setString(1, status);
            ps.setInt(2, roomId);
            
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}