/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.oceanviewresort.resources;

/**
 *
 * @author User
 */
import com.mycompany.oceanviewresort.dao.UserDAO;
import com.mycompany.oceanviewresort.model.UserDTO;
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

@Path("/users")
public class UserResource {

    private UserDAO userDAO = new UserDAO();

    // Fetch all system users
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllUsers() {
        try {
            List<UserDTO> users = userDAO.getAllUsers();
            return Response.ok(users).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"status\":\"error\", \"message\":\"Failed to fetch users\"}")
                    .build();
        }
    }

    // Add a new system user
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addUser(String jsonInput) {
        try {
            JSONObject json = new JSONObject(jsonInput);
            
            UserDTO user = new UserDTO();
            user.setUsername(json.getString("username"));
            user.setPassword(json.getString("password")); 
            user.setFullName(json.getString("fullName"));
            user.setEmail(json.getString("email"));
            user.setPhone(json.getString("phone"));
            user.setRole(json.getString("role"));

            if (userDAO.addUser(user)) {
                return Response.ok("{\"status\":\"success\", \"message\":\"User added successfully!\"}").build();
            } else {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"status\":\"error\", \"message\":\"Failed to add user. Username or email might already exist.\"}")
                        .build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"status\":\"error\", \"message\":\"Invalid request format.\"}")
                    .build();
        }
    }

    // Deactivate (soft delete) a user account
    @PUT
    @Path("/{id}/deactivate")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deactivateUser(@PathParam("id") int userId) {
        try {
            if (userDAO.deactivateUser(userId)) {
                return Response.ok("{\"status\":\"success\", \"message\":\"User deactivated successfully!\"}").build();
            } else {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"status\":\"error\", \"message\":\"Failed to deactivate user.\"}")
                        .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"status\":\"error\", \"message\":\"Server error occurred.\"}")
                    .build();
        }
    }
}