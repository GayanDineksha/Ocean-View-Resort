/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.oceanviewresort.dao;

import com.mycompany.oceanviewresort.model.BillDTO;
import com.mycompany.oceanviewresort.util.DB;
import java.sql.*;
/**
 *
 * @author User
 */
public class BillingDAO {

    public boolean generateBill(long reservationId, long userId) {
        String query = "{CALL sp_generate_bill(?, ?, ?, ?)}";
        try (Connection con = DB.getConnection();
             CallableStatement cs = con.prepareCall(query)) {
            cs.setLong(1, reservationId);
            cs.setLong(2, userId);
            cs.registerOutParameter(3, Types.BIGINT);
            cs.registerOutParameter(4, Types.VARCHAR);
            cs.execute();
            return true;
        } catch (SQLException e) {
            System.err.println("Error generating bill: " + e.getMessage());
            return false;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public BillDTO getBillByReservationId(long reservationId) {
        BillDTO bill = null;
        String query = "SELECT b.*, r.reservation_number, g.guest_name, g.contact_number, g.email " +
                       "FROM bills b " +
                       "JOIN reservations r ON b.reservation_id = r.reservation_id " +
                       "JOIN guests g ON r.guest_id = g.guest_id " +
                       "WHERE b.reservation_id = ?";
                       
        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setLong(1, reservationId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    bill = new BillDTO();
                    bill.setBillId(rs.getLong("bill_id"));
                    bill.setBillNumber(rs.getString("bill_number"));
                    bill.setReservationId(rs.getLong("reservation_id"));
                    bill.setGuestName(rs.getString("guest_name"));
                    bill.setGuestPhone(rs.getString("contact_number")); 
                    bill.setGuestEmail(rs.getString("email")); 
                    bill.setReservationNumber(rs.getString("reservation_number"));
                    
                    bill.setRoomCharges(rs.getDouble("room_charges"));
                    bill.setServiceCharges(rs.getDouble("service_charges"));
                    bill.setTaxAmount(rs.getDouble("tax_amount"));
                    bill.setDiscountAmount(rs.getDouble("discount_amount"));
                    bill.setTotalAmount(rs.getDouble("total_amount"));
                    bill.setAmountPaid(rs.getDouble("amount_paid"));
                    bill.setBalanceDue(rs.getDouble("balance_due"));
                    
                    String status = rs.getString("payment_status");
                    bill.setPaymentStatus(status.substring(0, 1).toUpperCase() + status.substring(1));
                    
                    // Format Date
                    Timestamp ts = rs.getTimestamp("bill_date");
                    bill.setBillDate(ts != null ? ts.toString() : "");
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return bill;
    }

    public boolean addPayment(long billId, String method, double amount, String ref, long userId) {
        String query = "{CALL sp_add_payment(?, ?, ?, ?, NULL, NULL, 'Payment Added via System', ?)}";
        try (Connection con = DB.getConnection();
             CallableStatement cs = con.prepareCall(query)) {
            cs.setLong(1, billId);
            cs.setString(2, method);
            cs.setDouble(3, amount);
            cs.setString(4, ref);
            cs.setLong(5, userId);
            cs.execute();
            return true;
        } catch (Exception e) { return false; }
    }
}