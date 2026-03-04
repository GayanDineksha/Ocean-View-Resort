/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.oceanviewresort.resources;

/**
 *
 * @author User
 */
import com.mycompany.oceanviewresort.dao.RoomTypeDAO;
import com.mycompany.oceanviewresort.model.RoomTypeDTO;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
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

@Path("/room-types")
public class RoomTypeResource {

    private RoomTypeDAO roomTypeDAO = new RoomTypeDAO();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllRoomTypes() {
        try {
            List<RoomTypeDTO> types = roomTypeDAO.getAllRoomTypes();
            return Response.ok(types).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"status\":\"error\", \"message\":\"Failed to fetch room types\"}")
                    .build();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addRoomType(String jsonInput) {
        try {
            JSONObject json = new JSONObject(jsonInput);
            
            RoomTypeDTO type = new RoomTypeDTO();
            type.setRoomTypeName(json.getString("roomTypeName"));
            type.setDescription(json.optString("description", ""));
            type.setBasePrice(json.getDouble("basePrice"));
            type.setMaxOccupancy(json.getInt("maxOccupancy"));
            type.setAmenities(json.optString("amenities", ""));

            if (roomTypeDAO.addRoomType(type)) {
                return Response.ok("{\"status\":\"success\", \"message\":\"Room Type added successfully!\"}").build();
            } else {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"status\":\"error\", \"message\":\"Failed to add room type.\"}")
                        .build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"status\":\"error\", \"message\":\"Invalid request format.\"}")
                    .build();
        }
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateRoomType(@PathParam("id") int typeId, String jsonInput) {
        try {
            JSONObject json = new JSONObject(jsonInput);
            
            RoomTypeDTO type = new RoomTypeDTO();
            type.setRoomTypeId(typeId);
            type.setBasePrice(json.getDouble("basePrice"));
            type.setMaxOccupancy(json.getInt("maxOccupancy"));
            type.setDescription(json.optString("description", ""));
            type.setAmenities(json.optString("amenities", ""));

            if (roomTypeDAO.updateRoomType(type)) {
                return Response.ok("{\"status\":\"success\", \"message\":\"Room Type updated successfully!\"}").build();
            } else {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"status\":\"error\", \"message\":\"Failed to update room type.\"}")
                        .build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"status\":\"error\", \"message\":\"Invalid request format.\"}")
                    .build();
        }
    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteRoomType(@PathParam("id") int typeId) {
        try {
            if (roomTypeDAO.deleteRoomType(typeId)) {
                return Response.ok("{\"status\":\"success\", \"message\":\"Room Type deleted successfully!\"}").build();
            } else {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"status\":\"error\", \"message\":\"Failed to delete room type.\"}")
                        .build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"status\":\"error\", \"message\":\"Server error while deleting.\"}")
                    .build();
        }
    }
}