/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.oceanviewresort.resources;

import com.mycompany.oceanviewresort.dao.ReservationDAO;
import com.mycompany.oceanviewresort.model.ReservationDTO;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.json.JSONObject;
import java.util.List;

@Path("/reservations")
public class ReservationResource {
    
    private ReservationDAO reservationDAO = new ReservationDAO();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createReservation(String jsonInput) {
        try {
            JSONObject json = new JSONObject(jsonInput);
            
            ReservationDTO dto = new ReservationDTO();
            // Set Guest Details
            dto.setGuestName(json.getString("guestName"));
            dto.setNicPassport(json.getString("nicPassport"));
            dto.setContactNumber(json.getString("contactNumber"));
            dto.setEmail(json.optString("email", null));
            
            // Set Booking Details
            dto.setRoomId(json.getLong("roomId"));
            dto.setCheckInDate(json.getString("checkInDate"));
            dto.setCheckOutDate(json.getString("checkOutDate"));
            dto.setAdults(json.getInt("adults"));
            dto.setChildren(json.getInt("children"));
            dto.setSpecialRequests(json.optString("specialRequests", "None"));
            dto.setCreatedBy(json.optLong("createdBy", 2)); // 2 is fallback Receptionist ID
            
            // Process the reservation
            ReservationDTO createdRes = reservationDAO.createReservation(dto);
            
            if (createdRes != null && createdRes.getReservationNumber() != null) {
                return Response.ok("{\"status\":\"success\", \"message\":\"Reservation created successfully!\", \"reservationNumber\":\"" + createdRes.getReservationNumber() + "\"}").build();
            } else {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"status\":\"error\", \"message\":\"Failed to create reservation. Room might be unavailable or double-booked.\"}")
                        .build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"status\":\"error\", \"message\":\"Invalid request format.\"}")
                    .build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllReservations() {
        try {
            List<ReservationDTO> list = reservationDAO.getAllReservations();
            return Response.ok(list).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                           .entity("{\"status\":\"error\", \"message\":\"Failed to fetch reservations\"}").build();
        }
    }

    @PUT
    @Path("/{id}/status")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateStatus(@PathParam("id") long id, String jsonInput) {
        try {
            JSONObject json = new JSONObject(jsonInput);
            String newStatus = json.getString("status");
            
            if (reservationDAO.updateReservationStatus(id, newStatus)) {
                return Response.ok("{\"status\":\"success\", \"message\":\"Status updated successfully!\"}").build();
            } else {
                return Response.status(Response.Status.BAD_REQUEST)
                               .entity("{\"status\":\"error\", \"message\":\"Failed to update status\"}").build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                           .entity("{\"status\":\"error\", \"message\":\"Server error occurred\"}").build();
        }
    }
}