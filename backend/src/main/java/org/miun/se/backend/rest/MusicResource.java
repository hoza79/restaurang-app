package org.miun.se.backend.rest;

import jakarta.ws.rs.Produces;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.miun.se.backend.DTO;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.PUT;

@Path("/music")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)

public class MusicResource{
    @PersistenceContext
    private EntityManager em;

    @GET
    public List<MusicDto> getMusicEvents(){
        List<MusicDto> musicList = em.createQuery(
                "SELECT event FROM Event even", Event.class).getResultList();

        List<MusicDto >



    }


}
