/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.oceanviewresort.dao;

/**
 *
 * @author User
 */
import com.mycompany.oceanviewresort.model.ReservationServiceDTO;
import com.mycompany.oceanviewresort.model.ServiceDTO;
import com.mycompany.oceanviewresort.util.DB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ServiceDAO {

    
    public List<ServiceDTO> getAllActiveServices() {
        List<ServiceDTO> list = new ArrayList<>();
        String query = "SELECT service_id, service_name, price FROM services WHERE is_active = TRUE";
        
        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
             
            while (rs.next()) {
                ServiceDTO dto = new ServiceDTO();
                dto.setServiceId(rs.getInt("service_id"));
                dto.setServiceName(rs.getString("service_name"));
                dto.setPrice(rs.getDouble("price"));
                list.add(dto);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }


    public boolean addServiceToReservation(ReservationServiceDTO dto) {
        String query = "INSERT INTO reservation_services (reservation_id, service_id, quantity, total_price, added_by) " +
                       "VALUES (?, ?, ?, (? * (SELECT price FROM services WHERE service_id = ?)), ?)";
                       
        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
             
            ps.setLong(1, dto.getReservationId());
            ps.setInt(2, dto.getServiceId());
            ps.setInt(3, dto.getQuantity());
            ps.setInt(4, dto.getQuantity()); // Total Price එක හදන්න Quantity එක යවනවා
            ps.setInt(5, dto.getServiceId()); // Total Price එක හදන්න Service ID එක යවනවා
            ps.setLong(6, dto.getAddedBy());
            
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
