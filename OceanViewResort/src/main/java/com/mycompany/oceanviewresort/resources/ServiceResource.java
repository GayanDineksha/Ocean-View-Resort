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
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.json.JSONObject;
import java.util.List;

@Path("/services")
public class ServiceResource {
    
    private ServiceDAO serviceDAO = new ServiceDAO();

    // For Receptionist (Only Active)
    @GET
    @Path("/active")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllActiveServices() {
        return Response.ok(serviceDAO.getAllActiveServices()).build();
    }

    // Add to Bill (Receptionist)
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
            dto.setAddedBy(json.optLong("addedBy", 2));
            
            if (serviceDAO.addServiceToReservation(dto)) {
                return Response.ok("{\"status\":\"success\", \"message\":\"Service added to the bill!\"}").build();
            } else {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"status\":\"error\", \"message\":\"Failed to add service.\"}").build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"status\":\"error\"}").build();
        }
    }

    // ----------------------------------------------------
    // MANAGER API ENDPOINTS
    // ----------------------------------------------------

    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllServicesForManager() {
        return Response.ok(serviceDAO.getAllServicesForManager()).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addService(ServiceDTO dto) {
        if (serviceDAO.addService(dto)) {
            return Response.ok("{\"status\":\"success\"}").build();
        }
        return Response.status(Response.Status.BAD_REQUEST).entity("{\"status\":\"error\"}").build();
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateService(@PathParam("id") int id, ServiceDTO dto) {
        dto.setServiceId(id);
        if (serviceDAO.updateService(dto)) {
            return Response.ok("{\"status\":\"success\"}").build();
        }
        return Response.status(Response.Status.BAD_REQUEST).entity("{\"status\":\"error\"}").build();
    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteService(@PathParam("id") int id) {
        if (serviceDAO.deleteService(id)) {
            return Response.ok("{\"status\":\"success\"}").build();
        }
        return Response.status(Response.Status.BAD_REQUEST).entity("{\"status\":\"error\"}").build();
    }
}