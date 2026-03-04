/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.oceanviewresort.dao;

/**
 *
 * @author User
 */
import com.mycompany.oceanviewresort.model.RoomTypeDTO;
import com.mycompany.oceanviewresort.util.DB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class RoomTypeDAO {

    public List<RoomTypeDTO> getAllRoomTypes() {
        List<RoomTypeDTO> roomTypes = new ArrayList<>();
        String query = "SELECT * FROM room_types WHERE is_active = TRUE ORDER BY room_type_name ASC";
        
        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                RoomTypeDTO type = new RoomTypeDTO();
                type.setRoomTypeId(rs.getInt("room_type_id"));
                type.setRoomTypeName(rs.getString("room_type_name"));
                type.setDescription(rs.getString("description"));
                type.setBasePrice(rs.getDouble("base_price"));
                type.setMaxOccupancy(rs.getInt("max_occupancy"));
                type.setAmenities(rs.getString("amenities"));
                type.setActive(rs.getBoolean("is_active"));
                roomTypes.add(type);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return roomTypes;
    }

    public boolean addRoomType(RoomTypeDTO type) {
        String sql = "INSERT INTO room_types (room_type_name, description, base_price, max_occupancy, amenities, is_active) VALUES (?, ?, ?, ?, ?, TRUE)";
        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, type.getRoomTypeName());
            ps.setString(2, type.getDescription());
            ps.setDouble(3, type.getBasePrice());
            ps.setInt(4, type.getMaxOccupancy());
            ps.setString(5, type.getAmenities());
            
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateRoomType(RoomTypeDTO type) {
        String query = "UPDATE room_types SET base_price = ?, max_occupancy = ?, description = ?, amenities = ? WHERE room_type_id = ?";
        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            
            ps.setDouble(1, type.getBasePrice());
            ps.setInt(2, type.getMaxOccupancy());
            ps.setString(3, type.getDescription());
            ps.setString(4, type.getAmenities());
            ps.setInt(5, type.getRoomTypeId());
            
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteRoomType(int typeId) {
        String query = "UPDATE room_types SET is_active = FALSE WHERE room_type_id = ?";
        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            
            ps.setInt(1, typeId);
            
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}