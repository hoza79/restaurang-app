package org.miun.se.backend.rest;

import org.miun.se.backend.model.Employee;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/employees")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EmployeeResource {

    @PersistenceContext
    private EntityManager em;

    // GET /api/employees/login?email={email}
    @GET
    @Path("/login")
    public Response login(@QueryParam("email") String email) {
        if (email == null || email.isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"email is required\"}").build();
        }
        TypedQuery<Employee> q = em.createQuery(
                "SELECT e FROM Employee e WHERE e.email = :email", Employee.class);
        q.setParameter("email", email);
        List<Employee> results = q.getResultList();
        if (results.isEmpty()) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\":\"No employee found\"}").build();
        }
        return Response.ok(results.get(0)).build();
    }

    // GET /api/employees
    @GET
    public List<Employee> getAllEmployees() {
        return em.createQuery("SELECT e FROM Employee e", Employee.class).getResultList();
    }

    // GET /api/employees/{employeeId}
    @GET
    @Path("/{employeeId}")
    public Response getById(@PathParam("employeeId") Long id) {
        Employee e = em.find(Employee.class, id);
        if (e == null) return Response.status(Response.Status.NOT_FOUND)
                .entity("{\"error\":\"Employee not found\"}").build();
        return Response.ok(e).build();
    }
}