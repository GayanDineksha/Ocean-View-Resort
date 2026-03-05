/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.oceanviewresort.resources;

import com.mycompany.oceanviewresort.dao.BillingDAO;
import com.mycompany.oceanviewresort.model.BillDTO;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.json.JSONObject;
/**
 *
 * @author User
 */
@Path("/billing")
public class BillingResource {

    private BillingDAO billingDAO = new BillingDAO();

    @POST
    @Path("/generate/{reservationId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response generateBill(@PathParam("reservationId") long reservationId) {
        // Fallback to user ID 2 (Receptionist) for testing
        boolean success = billingDAO.generateBill(reservationId, 2); 
        
        if (success) {
            return Response.ok("{\"status\":\"success\", \"message\":\"Bill generated successfully!\"}").build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"status\":\"error\", \"message\":\"Failed to generate bill. It might already exist or dates are invalid.\"}")
                    .build();
        }
    }

    @GET
    @Path("/reservation/{reservationId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBillByReservationId(@PathParam("reservationId") long reservationId) {
        BillDTO bill = billingDAO.getBillByReservationId(reservationId);
        
        if (bill != null) {
            return Response.ok(bill).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"status\":\"error\", \"message\":\"Bill not found for this reservation.\"}")
                    .build();
        }
    }

    @POST
    @Path("/pay")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response makePayment(String jsonInput) {
        try {
            JSONObject json = new JSONObject(jsonInput);
            
            long billId = json.getLong("billId");
            String method = json.getString("method"); 
            double amount = json.getDouble("amount");
            String ref = json.optString("reference", "N/A"); 
            long userId = json.optLong("userId", 2); // Getting User ID from Frontend
            
            boolean success = billingDAO.addPayment(billId, method, amount, ref, userId);
            
            if (success) {
                return Response.ok("{\"status\":\"success\", \"message\":\"Payment added successfully!\"}").build();
            } else {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"status\":\"error\", \"message\":\"Failed to process payment.\"}")
                        .build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"status\":\"error\", \"message\":\"Server error processing payment.\"}")
                    .build();
        }
    }
}