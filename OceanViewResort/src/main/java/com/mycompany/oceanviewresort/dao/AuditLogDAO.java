/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.oceanviewresort.dao;

/**
 *
 * @author User
 */
import com.mycompany.oceanviewresort.model.AuditLogDTO;
import com.mycompany.oceanviewresort.util.DB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class AuditLogDAO {

    public List<AuditLogDTO> getAllLogs() {
        List<AuditLogDTO> logs = new ArrayList<>();
        // LEFT JOIN used in case a user was deleted or action was system-generated
        String query = "SELECT a.log_id, a.action_type, a.table_name, a.record_id, " +
                       "a.old_values, a.new_values, a.action_timestamp, u.username " +
                       "FROM audit_logs a LEFT JOIN users u ON a.user_id = u.user_id " +
                       "ORDER BY a.action_timestamp DESC";

        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            while (rs.next()) {
                AuditLogDTO log = new AuditLogDTO();
                log.setLogId(rs.getLong("log_id"));
                
                String uname = rs.getString("username");
                log.setUsername(uname != null ? uname : "System/Unknown");
                
                log.setActionType(rs.getString("action_type"));
                log.setTableName(rs.getString("table_name"));
                log.setRecordId(rs.getLong("record_id"));
                log.setOldValues(rs.getString("old_values"));
                log.setNewValues(rs.getString("new_values"));

                java.sql.Timestamp ts = rs.getTimestamp("action_timestamp");
                log.setActionTimestamp(ts != null ? sdf.format(ts) : "");

                logs.add(log);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return logs;
    }
}
