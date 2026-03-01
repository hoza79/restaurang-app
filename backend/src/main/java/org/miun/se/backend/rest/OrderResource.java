package org.miun.se.backend.rest;

import org.miun.se.backend.DTO.KitchenOrderDto;
import org.miun.se.backend.model.CustomerOrder;
import org.miun.se.backend.model.Employee;
import org.miun.se.backend.model.DiningTable;
import org.miun.se.backend.model.OrderBatch;
import org.miun.se.backend.model.enums.BatchStatus;
import org.miun.se.backend.model.enums.OrderStatus;
import org.miun.se.backend.model.Employee;
import org.miun.se.backend.model.DiningTable;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.persistence.PersistenceContext;

import jakarta.ws.rs.PUT;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.miun.se.backend.model.enums.TableStatus;
import org.miun.se.backend.DTO.PayRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Path("/orders")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OrderResource {

    @PersistenceContext
    private EntityManager em;

    @GET
    public Map<String, String> getTest() {
        return Map.of("message", "Orders endpoint works");
    }



    @POST
    @Transactional
    public Response createOrder(Map<String, Object> request) {

        Number employeeNumber = (Number) request.get("employeeId");
        Number tableNumber = (Number) request.get("tableId");

        if (employeeNumber == null || tableNumber == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "employeeId and tableId are required"))
                    .build();
        }

        Integer employeeId = employeeNumber.intValue();
        Integer tableId = tableNumber.intValue();

        Employee employee = em.find(Employee.class, employeeId);
        DiningTable table = em.find(DiningTable.class, tableId);

        if (employee == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "Employee not found"))
                    .build();
        }

        if (table == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "Table not found"))
                    .build();
        }

        CustomerOrder order = new CustomerOrder(table, employee);

        em.persist(order);
        em.flush();

        return Response.status(Response.Status.CREATED)
                .entity(Map.of(
                        "status", "Order created",
                        "orderId", order.getOrderId()
                ))
                .build();
    }


    @PUT
    @Path("/{id}/complete")
    @Transactional
    public Response completeOrder(@PathParam("id") Integer id) {

        CustomerOrder order = em.find(CustomerOrder.class, id);

        if (order == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "Order not found"))
                    .build();
        }

        order.setOrderStatus(OrderStatus.COMPLETE);

        return Response.ok(
                Map.of(
                        "message", "Order marked as DONE",
                        "orderId", order.getOrderId()
                )
        ).build();
    }






    @POST
    @Path("/pay")
    @Transactional
    public Response payOrder(PayRequest request) {

        Integer tableId = request.tableId();

        DiningTable table = em.find(DiningTable.class, tableId);

        if (table == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "Table not found"))
                    .build();
        }

        // 1️⃣ Kontrollera om det finns PROCESSING batches
        Long processingCount = em.createQuery(
                        "SELECT COUNT(b) FROM OrderBatch b " +
                                "WHERE b.customerOrder.diningTable.tableId = :tableId " +
                                "AND b.customerOrder.orderStatus = :orderStatus " +
                                "AND b.batchStatus = :status",
                        Long.class)
                .setParameter("tableId", tableId)
                .setParameter("orderStatus", OrderStatus.IN_PROGRESS)
                .setParameter("status", BatchStatus.PROCESSING)
                .getSingleResult();

        if (processingCount > 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "Cannot pay. Kitchen still processing orders."))
                    .build();
        }

        // 2️⃣ Markera alla aktiva orders som COMPLETE
        List<CustomerOrder> activeOrders = em.createQuery(
                        "SELECT o FROM CustomerOrder o WHERE o.diningTable.tableId = :tableId AND o.orderStatus = :status",
                        CustomerOrder.class)
                .setParameter("tableId", tableId)
                .setParameter("status", OrderStatus.IN_PROGRESS)
                .getResultList();

        for (CustomerOrder order : activeOrders) {
            order.setOrderStatus(OrderStatus.COMPLETE);
        }

        table.setTableStatus(TableStatus.AVAILABLE);

        return Response.ok(Map.of(
                "message", "Payment completed",
                "tableId", tableId
        )).build();
    }
}