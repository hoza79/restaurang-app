package org.miun.se.backend.rest;

import org.miun.se.backend.DTO.EmployeeDto;
import org.miun.se.backend.DTO.KitchenOrderDto;
import org.miun.se.backend.DTO.ShiftDto;
import org.miun.se.backend.model.Employee;
import org.miun.se.backend.model.Shift;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
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

    // POST /api/employees
    @POST
    @Transactional
    public Response create(EmployeeDto dto) {
        Employee employee = new Employee(
                dto.firstName(),
                dto.lastName(),
                dto.role(),
                dto.phoneNumber(),
                dto.emailAddress()
        );
        em.persist(employee);
        return Response.status(Response.Status.CREATED).entity(toDto(employee)).build();
    }

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
        return Response.ok(toDto(results.getFirst())).build();
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

    // PUT /api/employees/{employeeId}
    @PUT
    @Path("/{employeeId}")
    @Transactional
    public Response update(@PathParam("employeeId") Integer id, EmployeeDto dto) {
        Employee employee = em.find(Employee.class, id);
        if (employee == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("{\"error\":\"Employee not found\"}").build();
        }
        if (dto.firstName() != null) employee.setFirstName(dto.firstName());
        if (dto.lastName() != null) employee.setLastName(dto.lastName());
        if (dto.role() != null) employee.setRole(dto.role());
        if (dto.phoneNumber() != null) employee.setPhoneNumber(dto.phoneNumber());
        if (dto.emailAddress() != null) employee.setEmailAddress(dto.emailAddress());

        em.merge(employee);
        return Response.ok(toDto(employee)).build();
    }

    // DELETE /api/employees/{employeeId}
    @DELETE
    @Path("/{employeeId}")
    @Transactional
    public Response delete(@PathParam("employeeId") Integer id) {
        Employee employee = em.find(Employee.class, id);
        if (employee == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("{\"error\":\"Employee not found\"}").build();
        }
        em.remove(employee);
        return Response.noContent().build();
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
