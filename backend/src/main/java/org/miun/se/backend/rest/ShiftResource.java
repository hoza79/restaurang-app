package org.miun.se.backend.rest;

import org.miun.se.backend.DTO.ShiftDto;
import org.miun.se.backend.model.Employee;
import org.miun.se.backend.model.Shift;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/shifts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ShiftResource {

    @PersistenceContext
    private EntityManager em;

    // POST /api/shifts
    @POST
    @Transactional
    public Response create(ShiftDto dto) {
        Employee employee = em.find(Employee.class, dto.employeeId());
        if (employee == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("{\"error\":\"Employee not found\"}").build();
        }
        Shift shift = new Shift(employee, dto.startTime(), dto.endTime());
        em.persist(shift);
        return Response.status(Response.Status.CREATED).entity(toDto(shift)).build();
    }

    // GET /api/shifts/{shiftId}
    @GET
    @Path("/{shiftId}")
    public Response getById(@PathParam("shiftId") Integer id) {
        Shift s = em.find(Shift.class, id);
        if (s == null) return Response.status(Response.Status.NOT_FOUND)
                .entity("{\"error\":\"Shift not found\"}").build();
        return Response.ok(toDto(s)).build();
    }

    // PUT /api/shifts/{shiftId}
    @PUT
    @Path("/{shiftId}")
    @Transactional
    public Response update(@PathParam("shiftId") Integer id, ShiftDto dto) {
        Shift shift = em.find(Shift.class, id);
        if (shift == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("{\"error\":\"Shift not found\"}").build();
        }
        if (dto.employeeId() != null) {
            Employee employee = em.find(Employee.class, dto.employeeId());
            if (employee == null) {
                return Response.status(Response.Status.NOT_FOUND).entity("{\"error\":\"Employee not found\"}").build();
            }
            shift.setEmployee(employee);
        }
        if (dto.startTime() != null) {
            shift.setStartTime(dto.startTime());
        }
        if (dto.endTime() != null) {
            shift.setEndTime(dto.endTime());
        }
        if (dto.shiftStatus() != null) {
            shift.setShiftStatus(dto.shiftStatus());
        }
        em.merge(shift);
        return Response.ok(toDto(shift)).build();
    }

    // DELETE /api/shifts/{shiftId}
    @DELETE
    @Path("/{shiftId}")
    @Transactional
    public Response delete(@PathParam("shiftId") Integer id) {
        Shift shift = em.find(Shift.class, id);
        if (shift == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("{\"error\":\"Shift not found\"}").build();
        }
        em.remove(shift);
        return Response.noContent().build();
    }

    private ShiftDto toDto(Shift shift) {
        return new ShiftDto(
                shift.getShiftId(),
                shift.getEmployee().getEmployeeId(),
                shift.getStartTime(),
                shift.getEndTime(),
                shift.getShiftStatus()
        );
    }
}
