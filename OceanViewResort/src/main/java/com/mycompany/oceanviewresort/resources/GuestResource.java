/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.oceanviewresort.resources;

/**
 *
 * @author User
 */
import com.mycompany.oceanviewresort.dao.GuestDAO;
import com.mycompany.oceanviewresort.model.GuestDTO;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/guests")
public class GuestResource {

    private GuestDAO guestDAO = new GuestDAO();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllActiveGuests() {
        try {
            List<GuestDTO> guests = guestDAO.getAllActiveGuests();
            return Response.ok(guests).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"status\":\"error\", \"message\":\"Failed to fetch guests\"}")
                    .build();
        }
    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response archiveGuest(@PathParam("id") int guestId) {
        try {
            if (guestDAO.archiveGuest(guestId)) {
                return Response.ok("{\"status\":\"success\", \"message\":\"Guest archived successfully!\"}").build();
            } else {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"status\":\"error\", \"message\":\"Failed to archive guest.\"}")
                        .build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"status\":\"error\", \"message\":\"Server error while archiving.\"}")
                    .build();
        }
    }
}
