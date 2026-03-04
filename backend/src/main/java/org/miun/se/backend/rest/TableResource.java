package org.miun.se.backend.rest;

import org.miun.se.backend.DTO.DiningTableDto;
import org.miun.se.backend.model.DiningTable;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path("/tables")
@Produces(MediaType.APPLICATION_JSON)
public class TableResource {

    @PersistenceContext
    private EntityManager em;

    @GET
    public List<DiningTableDto> getAllTables() {
        List<DiningTable> tables = em.createQuery("SELECT t FROM DiningTable t", DiningTable.class)
                .getResultList();

        return tables.stream()
                .map(t -> new DiningTableDto(
                        t.getTableId(),
                        t.getTableNumber(),
                        t.getCapacity(),
                        t.getTableStatus()
                ))
                .toList();
    }
}