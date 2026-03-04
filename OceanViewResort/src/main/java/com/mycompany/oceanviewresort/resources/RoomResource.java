/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.oceanviewresort.resources;

/**
 *
 * @author User
 */
import com.mycompany.oceanviewresort.dao.RoomDAO;
import com.mycompany.oceanviewresort.model.RoomDTO;
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

@Path("/rooms")
public class RoomResource {

    private RoomDAO roomDAO = new RoomDAO();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllRooms() {
        try {
            List<RoomDTO> rooms = roomDAO.getAllRooms();
            return Response.ok(rooms).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"status\":\"error\", \"message\":\"Failed to fetch rooms\"}")
                    .build();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addRoom(String jsonInput) {
        try {
            JSONObject json = new JSONObject(jsonInput);
            
            RoomDTO room = new RoomDTO();
            room.setRoomNumber(json.getString("roomNumber"));
            room.setRoomTypeId(json.getInt("roomTypeId"));
            room.setFloorNumber(json.getInt("floorNumber"));
            room.setViewType(json.getString("viewType"));

            if (roomDAO.addRoom(room)) {
                return Response.ok("{\"status\":\"success\", \"message\":\"Room added successfully!\"}").build();
            } else {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"status\":\"error\", \"message\":\"Failed to add room. Number might already exist.\"}")
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
    @Path("/{id}/status")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateRoomStatus(@PathParam("id") int roomId, String jsonInput) {
        try {
            JSONObject json = new JSONObject(jsonInput);
            String status = json.getString("status");

            if (roomDAO.updateRoomStatus(roomId, status)) {
                return Response.ok("{\"status\":\"success\", \"message\":\"Room status updated successfully!\"}").build();
            } else {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"status\":\"error\", \"message\":\"Failed to update room status.\"}")
                        .build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"status\":\"error\", \"message\":\"Invalid request format.\"}")
                    .build();
        }
    }
}