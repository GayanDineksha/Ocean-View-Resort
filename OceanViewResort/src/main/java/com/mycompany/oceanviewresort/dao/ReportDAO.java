/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.oceanviewresort.dao;

/**
 *
 * @author User
 */
import com.mycompany.oceanviewresort.model.MonthlyRevenueDTO;
import com.mycompany.oceanviewresort.util.DB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;

public class ReportDAO {

    public List<MonthlyRevenueDTO> getMonthlyRevenue() {
        List<MonthlyRevenueDTO> list = new ArrayList<>();
        String query = "SELECT revenue_month, total_revenue, total_paid, total_outstanding FROM vw_monthly_revenue ORDER BY revenue_month LIMIT 6";
        
        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                MonthlyRevenueDTO dto = new MonthlyRevenueDTO();
                dto.setMonth(rs.getString("revenue_month"));
                dto.setTotalRevenue(rs.getDouble("total_revenue"));
                dto.setTotalPaid(rs.getDouble("total_paid"));
                dto.setTotalOutstanding(rs.getDouble("total_outstanding"));
                list.add(dto);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public JSONObject getDashboardSummary() {
        JSONObject summary = new JSONObject();
        
        try (Connection con = DB.getConnection()) {
            String roomQuery = "SELECT " +
                               "(SELECT COUNT(*) FROM rooms WHERE is_active = TRUE) AS total_rooms, " +
                               "(SELECT COUNT(*) FROM rooms WHERE status = 'occupied' AND is_active = TRUE) AS occupied_rooms";
            try(PreparedStatement ps = con.prepareStatement(roomQuery); ResultSet rs = ps.executeQuery()) {
                if(rs.next()) {
                    summary.put("totalRooms", rs.getInt("total_rooms"));
                    summary.put("occupiedRooms", rs.getInt("occupied_rooms"));
                }
            }

            String billQuery = "SELECT COALESCE(SUM(balance_due), 0) AS total_due FROM vw_outstanding_bills";
            try(PreparedStatement ps = con.prepareStatement(billQuery); ResultSet rs = ps.executeQuery()) {
                if(rs.next()) {
                    summary.put("totalOutstanding", rs.getDouble("total_due"));
                }
            }
            
            String guestQuery = "SELECT COUNT(*) AS inhouse_count FROM vw_inhouse_guests";
            try(PreparedStatement ps = con.prepareStatement(guestQuery); ResultSet rs = ps.executeQuery()) {
                if(rs.next()) {
                    summary.put("inhouseGuests", rs.getInt("inhouse_count"));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return summary;
    }
}