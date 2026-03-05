/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.oceanviewresort.resources;

/**
 *
 * @author User
 */
import com.mycompany.oceanviewresort.dao.ReportDAO;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/reports")
public class ReportResource {

    private ReportDAO reportDAO = new ReportDAO();

    @GET
    @Path("/revenue")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMonthlyRevenue() {
        return Response.ok(reportDAO.getMonthlyRevenue()).build();
    }

    @GET
    @Path("/summary")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDashboardSummary() {
        return Response.ok(reportDAO.getDashboardSummary().toString()).build();
    }
}