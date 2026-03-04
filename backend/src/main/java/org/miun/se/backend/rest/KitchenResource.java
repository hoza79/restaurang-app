package org.miun.se.backend.rest;

import org.miun.se.backend.DTO.KitchenBatchDTO;
import org.miun.se.backend.DTO.KitchenItemDTO;
import org.miun.se.backend.model.OrderBatch;
import org.miun.se.backend.model.enums.BatchStatus;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.PathParam;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.Map;
@Path("/kitchen")
@Produces(MediaType.APPLICATION_JSON)
public class KitchenResource {

    @PersistenceContext
    private EntityManager em;

    @GET
    @Path("/batches")
    public List<KitchenBatchDTO> getProcessingBatches() {

        List<OrderBatch> batches = em.createQuery(
                        "SELECT b FROM OrderBatch b WHERE b.batchStatus = :status ORDER BY b.createdAt ASC",
                        OrderBatch.class)
                .setParameter("status", BatchStatus.PROCESSING)
                .getResultList();

        return batches.stream().map(batch -> new KitchenBatchDTO(
                batch.getBatchId(),
                batch.getBatchType().name(),
                batch.getBatchStatus().name(),
                batch.getCreatedAt(),
                batch.getCustomerOrder().getDiningTable().getTableNumber(),
                batch.getItems().stream().map(item ->
                        new KitchenItemDTO(
                                item.getMenuItem().getName(),
                                item.getQuantity(),
                                item.getNotes()
                        )
                ).toList()
        )).toList();
    }

    @PUT
    @Path("/batches/{id}/complete")
    @Transactional
    public Response markBatchReady(@PathParam("id") Integer id) {

        OrderBatch batch = em.find(OrderBatch.class, id);

        if (batch == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "Batch not found"))
                    .build();
        }

        if (batch.getBatchStatus() != BatchStatus.PROCESSING) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "Batch is not in PROCESSING state"))
                    .build();
        }

        batch.setBatchStatus(BatchStatus.SERVED);

        return Response.ok(Map.of(
                "message", "Batch marked as READY",
                "batchId", id
        )).build();
    }
}