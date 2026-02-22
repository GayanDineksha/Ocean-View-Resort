/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.oceanviewresort.resources;

/**
 *
 * @author User
 */
import com.mycompany.oceanviewresort.util.DB;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

import java.sql.Connection;

@Path("test-db")
public class DBTestResource {

    @GET
    public Response testDB() {
        try (Connection con = DB.getConnection()) {
            return Response.ok("DB Connected! " + con.getMetaData().getURL()).build();
        } catch (Exception e) {
            return Response.serverError()
                    .entity("DB Connection Failed: " + e.getMessage())
                    .build();
        }
    }
}