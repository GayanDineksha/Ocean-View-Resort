/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.oceanviewresort.resources;

/**
 *
 * @author User
 */
import com.mycompany.oceanviewresort.dao.ServiceDAO;
import com.mycompany.oceanviewresort.model.ReservationServiceDTO;
import com.mycompany.oceanviewresort.model.ServiceDTO;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.json.JSONObject;
import java.util.List;

@Path("/services")
public class ServiceResource {
    
    private ServiceDAO serviceDAO = new ServiceDAO();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllServices() {
        try {
            List<ServiceDTO> list = serviceDAO.getAllActiveServices();
            return Response.ok(list).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"status\":\"error\", \"message\":\"Failed to fetch services\"}").build();
        }
    }

    @POST
    @Path("/add-to-bill")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addServiceToBill(String jsonInput) {
        try {
            JSONObject json = new JSONObject(jsonInput);
            
            ReservationServiceDTO dto = new ReservationServiceDTO();
            dto.setReservationId(json.getLong("reservationId"));
            dto.setServiceId(json.getInt("serviceId"));
            dto.setQuantity(json.getInt("quantity"));
            dto.setAddedBy(json.optLong("addedBy", 2)); // Default to Receptionist
            
            if (serviceDAO.addServiceToReservation(dto)) {
                return Response.ok("{\"status\":\"success\", \"message\":\"Service added to the bill successfully!\"}").build();
            } else {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"status\":\"error\", \"message\":\"Failed to add service to bill. Check if Reservation ID is valid.\"}").build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"status\":\"error\", \"message\":\"Invalid request format.\"}").build();
        }
    }
}
