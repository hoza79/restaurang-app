package org.miun.se.backend.rest;

import org.miun.se.backend.DTO.EmployeeDto;
import org.miun.se.backend.DTO.KitchenOrderDto;
import org.miun.se.backend.DTO.ShiftDto;
import org.miun.se.backend.model.CustomerOrder;
import org.miun.se.backend.model.Employee;
import org.miun.se.backend.model.Shift;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

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
                "SELECT e FROM Employee e WHERE e.emailAddress = :email", Employee.class);
        q.setParameter("email", email);
        List<Employee> results = q.getResultList();
        if (results.isEmpty()) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\":\"No employee found\"}").build();
        }
        return Response.ok(toDto(results.get(0))).build();
    }

    // GET /api/employees
    @GET
    public List<EmployeeDto> getAllEmployees() {
        return em.createQuery("SELECT e FROM Employee e", Employee.class)
                .getResultList()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // GET /api/employees/{employeeId}
    @GET
    @Path("/{employeeId}")
    public Response getById(@PathParam("employeeId") Integer id) {
        Employee e = em.find(Employee.class, id);
        if (e == null) return Response.status(Response.Status.NOT_FOUND)
                .entity("{\"error\":\"Employee not found\"}").build();
        return Response.ok(toDto(e)).build();
    }

    // GET /api/employees/{employeeId}/shifts
    @GET
    @Path("/{employeeId}/shifts")
    public List<ShiftDto> getShiftsByEmployee(@PathParam("employeeId") Integer empId) {
        return em.createQuery(
                        "SELECT s FROM Shift s WHERE s.employee.employeeId = :empId", Shift.class)
                .setParameter("empId", empId)
                .getResultList()
                .stream()
                .map(s -> new ShiftDto(s.getShiftId(), s.getEmployee().getEmployeeId(), s.getStartTime(), s.getEndTime(), s.getShiftStatus()))
                .collect(Collectors.toList());
    }

    private EmployeeDto toDto(Employee employee) {
        List<ShiftDto> shifts = employee.getShifts().stream()
                .map(s -> new ShiftDto(s.getShiftId(), s.getEmployee().getEmployeeId(), s.getStartTime(), s.getEndTime(), s.getShiftStatus()))
                .collect(Collectors.toList());

        List<KitchenOrderDto> orders = employee.getOrders().stream()
                .map(o -> new KitchenOrderDto(o.getOrderId(), o.getDiningTable().getTableId(), o.getCreatedAt(), o.getTotalPrice(), o.getOrderStatus().name()))
                .collect(Collectors.toList());

        return new EmployeeDto(
                employee.getEmployeeId(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getRole(),
                employee.getPhoneNumber(),
                employee.getEmailAddress(),
                shifts,
                orders
        );
    }
}
