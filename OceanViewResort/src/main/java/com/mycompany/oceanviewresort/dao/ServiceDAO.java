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


    // RECEPTIONIST(Active Services)

    public List<ServiceDTO> getAllActiveServices() {
        List<ServiceDTO> list = new ArrayList<>();
        // Database එකේ column names: unit_price, service_category
        String query = "SELECT service_id, service_name, unit_price, service_category FROM services WHERE is_active = TRUE";
        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ServiceDTO dto = new ServiceDTO();
                dto.setServiceId(rs.getInt("service_id"));
                dto.setServiceName(rs.getString("service_name"));
                dto.setPrice(rs.getDouble("unit_price")); // Map unit_price to price
                dto.setCategory(rs.getString("service_category"));
                list.add(dto);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }


    public boolean addServiceToReservation(ReservationServiceDTO dto) {
        String query = "INSERT INTO reservation_services (reservation_id, service_id, quantity, unit_price, total_price, service_date, is_billed) " +
                       "SELECT ?, ?, ?, unit_price, (? * unit_price), CURDATE(), FALSE FROM services WHERE service_id = ?";
                       
        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setLong(1, dto.getReservationId());
            ps.setInt(2, dto.getServiceId());
            ps.setInt(3, dto.getQuantity());
            ps.setInt(4, dto.getQuantity()); 
            ps.setInt(5, dto.getServiceId()); 
            return ps.executeUpdate() > 0;
        } catch (Exception e) { 
            e.printStackTrace(); 
            return false; 
        }
    }


    // MANAGER (CRUD - Manage Services)
    public List<ServiceDTO> getAllServicesForManager() {
        List<ServiceDTO> list = new ArrayList<>();
        String query = "SELECT service_id, service_name, unit_price, service_category, is_active FROM services ORDER BY service_id DESC";
        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ServiceDTO dto = new ServiceDTO();
                dto.setServiceId(rs.getInt("service_id"));
                dto.setServiceName(rs.getString("service_name"));
                dto.setPrice(rs.getDouble("unit_price"));
                dto.setCategory(rs.getString("service_category"));
                dto.setActive(rs.getBoolean("is_active"));
                list.add(dto);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public boolean addService(ServiceDTO dto) {
        String query = "INSERT INTO services (service_name, unit_price, service_category, is_active) VALUES (?, ?, ?, ?)";
        try (Connection con = DB.getConnection(); PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, dto.getServiceName());
            ps.setDouble(2, dto.getPrice());
            ps.setString(3, dto.getCategory() != null ? dto.getCategory() : "other");
            ps.setBoolean(4, dto.isActive());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { 
            e.printStackTrace(); 
            return false; 
        }
    }

    public boolean updateService(ServiceDTO dto) {
        String query = "UPDATE services SET service_name=?, unit_price=?, service_category=?, is_active=? WHERE service_id=?";
        try (Connection con = DB.getConnection(); PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, dto.getServiceName());
            ps.setDouble(2, dto.getPrice());
            ps.setString(3, dto.getCategory() != null ? dto.getCategory() : "other");
            ps.setBoolean(4, dto.isActive());
            ps.setInt(5, dto.getServiceId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { 
            e.printStackTrace(); 
            return false; 
        }
    }

    public boolean deleteService(int id) {
        String query = "UPDATE services SET is_active=FALSE WHERE service_id=?"; 
        try (Connection con = DB.getConnection(); PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }
}