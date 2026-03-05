package org.miun.se.backend.rest;

import org.miun.se.backend.model.Shift;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ShiftResource {

    @PersistenceContext
    private EntityManager em;

    // GET /api/employees/{employeeId}/shifts
    @GET
    @Path("/employees/{employeeId}/shifts")
    public List<Shift> getByEmployee(@PathParam("employeeId") Long empId) {
        return em.createQuery(
                        "SELECT s FROM Shift s WHERE s.employee.id = :empId", Shift.class)
                .setParameter("empId", empId)
                .getResultList();
    }

    // GET /api/shifts/{shiftId}
    @GET
    @Path("/shifts/{shiftId}")
    public Response getById(@PathParam("shiftId") Long id) {
        Shift s = em.find(Shift.class, id);
        if (s == null) return Response.status(Response.Status.NOT_FOUND)
                .entity("{\"error\":\"Shift not found\"}").build();
        return Response.ok(s).build();
    }
}