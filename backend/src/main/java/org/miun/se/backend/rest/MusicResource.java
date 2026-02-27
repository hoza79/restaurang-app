package org.miun.se.backend.rest;

import jakarta.transaction.TransactionScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.miun.se.backend.DTO.MusicDto;
import org.miun.se.backend.DTO.MusicAddDto;
import org.miun.se.backend.model.Event;

import java.util.ArrayList;
import java.util.List;

@Path("/music")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)

public class MusicResource{
    @PersistenceContext
    private EntityManager em;

    @GET
    public List<MusicDto> getMusicEvents(){
        List<Event> eventList = em.createQuery(
                "SELECT eventObj FROM Event eventObj", Event.class).getResultList();

        List<MusicDto> musicList = new ArrayList<>();
        for(Event event: eventList){
            musicList.add(new MusicDto(event.getEventId(),event.getTitle(), event.getDescription(), event.getEventTime(), event.getImagePath()));
        }

        return musicList;
    }


    @POST
    @Transactional
    public Response addMusic(MusicAddDto music){

        Event musicEvent = new Event(music.title(), music.description(), music.date());
        musicEvent.setImagePath(music.imgPath());

        em.persist(musicEvent);

        return Response.status(Response.Status.CREATED).build();
    }

    @DELETE
    @Path("/{eventId}")
    @Transactional
    public Response deleteMusic(@PathParam("eventId") Integer eventId){
        Event musicEvent;
        try {
            musicEvent = em.createQuery(
                    "SELECT eventObj FROM Event eventObj WHERE eventObj.eventId = :eventId", Event.class)
                    .setParameter("eventId", eventId).getSingleResult();
        } catch (Exception e){
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        em.remove(musicEvent);

        return Response.ok().build();
    }

    @PUT
    @Path("/{eventId}")
    @Transactional
    public Response editMusicEvent(@PathParam("eventId") Integer eventId, MusicAddDto musicEventDto){
        Event musicEvent;
        try {
            musicEvent = em.createQuery(
                    "SELECT eventObj FROM Event eventObj WHERE eventObj.eventId = :eventId", Event.class)
                    .setParameter("eventId", eventId).getSingleResult();
        } catch (Exception e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        musicEvent.setTitle(musicEventDto.title());
        musicEvent.setDescription(musicEventDto.description());
        musicEvent.setEventTime(musicEventDto.date());
        musicEvent.setImagePath(musicEventDto.imgPath());

        return Response.ok().build();
    }


}
