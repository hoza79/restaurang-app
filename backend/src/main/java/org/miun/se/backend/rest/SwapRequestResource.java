package org.miun.se.backend.rest;

import org.miun.se.backend.DTO.SwapRequestDto;
import org.miun.se.backend.model.Employee;
import org.miun.se.backend.model.Shift;
import org.miun.se.backend.model.SwapRequest;
import org.miun.se.backend.model.enums.SwapStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

@Path("/swap-requests")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SwapRequestResource {

    @PersistenceContext
    private EntityManager em;

    // POST /api/swap-requests
    // Body: { senderId, receiverId, shiftId }
    @POST
    @Transactional
    public Response create(SwapRequestDto dto) {
        Employee sender = em.find(Employee.class, dto.senderId());
        Employee receiver = em.find(Employee.class, dto.receiverId());
        Shift shift = em.find(Shift.class, dto.shiftId());

        if (sender == null) return Response.status(404).entity("{\"error\":\"Sender not found\"}").build();
        if (receiver == null) return Response.status(404).entity("{\"error\":\"Receiver not found\"}").build();
        if (shift == null) return Response.status(404).entity("{\"error\":\"Shift not found\"}").build();

        SwapRequest swap = new SwapRequest(sender, receiver, shift);
        em.persist(swap);
        return Response.status(Response.Status.CREATED).entity(toDto(swap)).build();
    }

    // GET /api/swap-requests/incoming?receiverId={id}
    @GET
    @Path("/incoming")
    public List<SwapRequestDto> incoming(@QueryParam("receiverId") Integer receiverId) {
        return em.createQuery(
                        "SELECT s FROM SwapRequest s WHERE s.receiverEmployee.employeeId = :rid", SwapRequest.class)
                .setParameter("rid", receiverId)
                .getResultList()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // GET /api/swap-requests/outgoing?senderId={id}
    @GET
    @Path("/outgoing")
    public List<SwapRequestDto> outgoing(@QueryParam("senderId") Integer senderId) {
        return em.createQuery(
                        "SELECT s FROM SwapRequest s WHERE s.senderEmployee.employeeId = :sid", SwapRequest.class)
                .setParameter("sid", senderId)
                .getResultList()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // GET /api/swap-requests/{swapId}
    @GET
    @Path("/{swapId}")
    public Response getSwapRequestById(@PathParam("swapId") Integer swapId) {
        SwapRequest swap = em.find(SwapRequest.class, swapId);
        if (swap == null) return Response.status(404).entity("{\"error\":\"Not found\"}").build();
        return Response.ok(toDto(swap)).build();
    }

    // PUT /api/swap-requests/{swapId}/accept
    @PUT
    @Path("/{swapId}/accept")
    @Transactional
    public Response accept(@PathParam("swapId") Integer swapId) {
        SwapRequest swap = em.find(SwapRequest.class, swapId);
        if (swap == null) return Response.status(404).entity("{\"error\":\"Not found\"}").build();
        if (swap.getSwapStatus() != SwapStatus.PENDING)
            return Response.status(409).entity("{\"error\":\"Not pending\"}").build();

        Shift shift = em.find(Shift.class, swap.getShift());
        Employee newOwner = em.find(Employee.class, swap.getReceiverEmployee());
        shift.setEmployee(newOwner);

        swap.setSwapStatus(SwapStatus.ACCEPTED);
        return Response.ok(toDto(em.merge(swap))).build();
    }

    // PUT /api/swap-requests/{swapId}/reject
    @PUT
    @Path("/{swapId}/reject")
    @Transactional
    public Response reject(@PathParam("swapId") Integer swapId) {
        SwapRequest swap = em.find(SwapRequest.class, swapId);
        if (swap == null) return Response.status(404).entity("{\"error\":\"Not found\"}").build();
        if (swap.getSwapStatus() != SwapStatus.PENDING)
            return Response.status(409).entity("{\"error\":\"Not pending\"}").build();

        swap.setSwapStatus(SwapStatus.REJECTED);
        return Response.ok(toDto(em.merge(swap))).build();
    }

    // DELETE /api/swap-requests/{swapId}
    @DELETE
    @Path("/{swapId}")
    @Transactional
    public Response delete(@PathParam("swapId") Integer swapId) {
        SwapRequest swap = em.find(SwapRequest.class, swapId);
        if (swap == null) return Response.status(404).entity("{\"error\":\"Not found\"}").build();
        em.remove(swap);
        return Response.noContent().build();
    }

    private SwapRequestDto toDto(SwapRequest swapRequest) {
        return new SwapRequestDto(
                swapRequest.getSwapId(),
                swapRequest.getSenderEmployee().getEmployeeId(),
                swapRequest.getReceiverEmployee().getEmployeeId(),
                swapRequest.getShift().getShiftId(),
                swapRequest.getSwapStatus()
        );
    }
}
