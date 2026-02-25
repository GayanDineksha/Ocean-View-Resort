/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.oceanviewresort.resources;

import com.mycompany.oceanviewresort.dao.UserDAO;
import com.mycompany.oceanviewresort.model.User;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.json.JSONObject;
/**
 *
 * @author User
 */
@Path("/auth")
public class AuthResource {
    
    private UserDAO userDAO = new UserDAO();

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(String jsonInput) {
        try {
            JSONObject json = new JSONObject(jsonInput);
            String username = json.getString("username");
            String password = json.getString("password");

            // Calling the DAO layer
            User user = userDAO.authenticateUser(username, password);

            if (user != null) {
                // Login Success
                JSONObject responseJson = new JSONObject();
                responseJson.put("status", "success");
                responseJson.put("message", "Login successful");
                responseJson.put("userId", user.getUserId());
                responseJson.put("fullName", user.getFullName());
                responseJson.put("role", user.getRole());
                
                return Response.ok(responseJson.toString()).build();
            } else {
                // Login Failed
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity("{\"status\":\"error\", \"message\":\"Invalid username or password\"}")
                        .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"status\":\"error\", \"message\":\"Invalid request format\"}")
                    .build();
        }
    }
}
