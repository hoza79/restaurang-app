package org.miun.se.backend.rest;

import jakarta.mail.Multipart;
import jakarta.transaction.TransactionScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.ws.rs.core.EntityPart;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.miun.se.backend.DTO.MusicDto;
import org.miun.se.backend.DTO.MusicAddDto;
import org.miun.se.backend.model.Event;

import java.io.*;
import java.nio.file.Paths;
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

    @POST
    @Path("/images")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.TEXT_PLAIN)
    @Transactional
    public Response saveFileReturnPath(@FormParam("image") EntityPart image, @FormParam("name") String eventName){

        if(image == null || eventName == null){
            return Response.status(Response.Status.NO_CONTENT).build();
        }

        int randomNr = (int)(Math.random() * 1001);
        String fileName = eventName + randomNr + ".jpg";

        File rootFile = new File(System.getProperty("user.dir")).getParentFile();
        File directory = new File(rootFile, "/Images");

        if(!directory.exists() && !directory.mkdirs()){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }

        InputStream in = null;
        FileOutputStream out = null;
        File file = new File(directory, fileName);

        try {
            in = image.getContent();
            out = new FileOutputStream(file);

            in.transferTo(out);
            String path = "domains/domain1/Images/" + fileName;

            return Response.status(Response.Status.CREATED).entity(path).build();

        } catch (IOException e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } finally {
            if(in != null){
                try {
                    in.close();
                } catch (Exception e) {}
            }
            if (out != null){
                try {
                    out.close();
                } catch (Exception e){}
            }
        }

    }

}
