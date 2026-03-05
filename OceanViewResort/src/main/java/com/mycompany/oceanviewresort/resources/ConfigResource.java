/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.oceanviewresort.resources;

/**
 *
 * @author User
 */
import com.mycompany.oceanviewresort.dao.ConfigDAO;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.json.JSONObject;

@Path("/config")
public class ConfigResource {
    private ConfigDAO configDAO = new ConfigDAO();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllConfigs() {
        return Response.ok(configDAO.getAllConfigs()).build();
    }

    @PUT
    @Path("/{key}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateConfig(@PathParam("key") String key, String jsonInput) {
        try {
            JSONObject json = new JSONObject(jsonInput);
            String value = json.getString("configValue");
            long userId = json.optLong("updatedBy", 1); // Default to user 1 if not provided

            if (configDAO.updateConfig(key, value, userId)) {
                return Response.ok("{\"status\":\"success\"}").build();
            } else {
                return Response.status(Response.Status.BAD_REQUEST).entity("{\"status\":\"error\"}").build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"status\":\"error\"}").build();
        }
    }
}
