/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.oceanviewresort.dao;

/**
 *
 * @author User
 */
import com.mycompany.oceanviewresort.model.ConfigDTO;
import com.mycompany.oceanviewresort.util.DB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ConfigDAO {

    public List<ConfigDTO> getAllConfigs() {
        List<ConfigDTO> list = new ArrayList<>();
        String query = "SELECT config_key, config_value, description FROM system_config";
        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ConfigDTO dto = new ConfigDTO();
                dto.setConfigKey(rs.getString("config_key"));
                dto.setConfigValue(rs.getString("config_value"));
                dto.setDescription(rs.getString("description"));
                list.add(dto);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public boolean updateConfig(String key, String value, long updatedBy) {
        String query = "UPDATE system_config SET config_value = ?, updated_by = ? WHERE config_key = ?";
        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, value);
            ps.setLong(2, updatedBy);
            ps.setString(3, key);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { 
            e.printStackTrace(); 
            return false; 
        }
    }
}