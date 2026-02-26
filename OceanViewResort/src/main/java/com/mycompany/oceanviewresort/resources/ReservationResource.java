/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.oceanviewresort.resources;
import com.mycompany.oceanviewresort.dao.ReservationDAO;
import com.mycompany.oceanviewresort.model.Guest;
import com.mycompany.oceanviewresort.model.Reservation;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.json.JSONObject;
import java.sql.Date;
/**
 *
 * @author User
 */
@Path("/reservations")
public class ReservationResource {
    
    private ReservationDAO reservationDAO = new ReservationDAO();

    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createReservation(String jsonInput) {
        try {
            JSONObject json = new JSONObject(jsonInput);            
       
            Guest guest = new Guest();
            guest.setGuestName(json.getString("guestName"));
            guest.setNicPassport(json.getString("nicPassport"));
            guest.setContactNumber(json.getString("contactNumber"));
            guest.setEmail(json.getString("email"));
            
            int roomId = json.getInt("roomId");
            Date checkIn = Date.valueOf(json.getString("checkInDate")); 
            Date checkOut = Date.valueOf(json.getString("checkOutDate"));
            int adults = json.getInt("adults");
            int children = json.getInt("children");
            
            boolean isAvailable = reservationDAO.checkRoomAvailability(roomId, checkIn, checkOut);
            
            if (!isAvailable) {
                return Response.status(Response.Status.CONFLICT)
                        .entity("{\"status\":\"error\", \"message\":\"Room is not available for the selected dates.\"}")
                        .build();
            }
            

            int guestId = reservationDAO.findOrCreateGuest(guest);
            
            if (guestId <= 0) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("{\"status\":\"error\", \"message\":\"Failed to process guest information.\"}")
                        .build();
            }
            
           
            Reservation res = new Reservation();
            res.setGuestId(guestId);
            res.setRoomId(roomId);
            res.setCheckInDate(checkIn);
            res.setCheckOutDate(checkOut);
            res.setAdults(adults);
            res.setChildren(children);
            
            boolean success = reservationDAO.createReservation(res);
            
            if (success) {
                
                return Response.ok("{\"status\":\"success\", \"message\":\"Reservation created successfully!\"}").build();
            } else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("{\"status\":\"error\", \"message\":\"Failed to save reservation.\"}")
                        .build();
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"status\":\"error\", \"message\":\"Invalid request format or missing data.\"}")
                    .build();
        }
    }
}