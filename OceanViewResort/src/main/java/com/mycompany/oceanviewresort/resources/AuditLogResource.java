/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.oceanviewresort.resources;

/**
 *
 * @author User
 */
import com.mycompany.oceanviewresort.dao.AuditLogDAO;
import com.mycompany.oceanviewresort.model.AuditLogDTO;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/audit-logs")
public class AuditLogResource {

    private AuditLogDAO auditLogDAO = new AuditLogDAO();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAuditLogs() {
        try {
            List<AuditLogDTO> logs = auditLogDAO.getAllLogs();
            return Response.ok(logs).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"status\":\"error\", \"message\":\"Failed to fetch audit logs\"}")
                    .build();
        }
    }
}