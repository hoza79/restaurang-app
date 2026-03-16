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
import org.miun.se.backend.DTO.CommentAddDto;
import org.miun.se.backend.DTO.CommentDto;
import org.miun.se.backend.DTO.MusicDto;
import org.miun.se.backend.DTO.MusicAddDto;
import org.miun.se.backend.model.Comment;
import org.miun.se.backend.model.Event;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
            musicList.add(new MusicDto(event.getEventId(),event.getTitle(), event.getDescription(), event.getEventTime(), event.getImagePath(), event.getLikes()));
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

        String imageFileName = musicEvent.getImagePath();

        File rootFile = new File(System.getProperty("user.dir")).getParentFile();
        File imgFile = new File(rootFile, "/Images/" + imageFileName);

        imgFile.delete();

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
    public Response saveFileReturnName(@FormParam("image") EntityPart image, @FormParam("name") String eventName){

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

            return Response.status(Response.Status.CREATED).entity(fileName).build();
        } catch (IOException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
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


    @GET
    @Path("/images/{imageName}")
    @Produces({"image/jpeg"})
    public Response getImage(@PathParam("imageName") String imageFileName){

        File rootFile = new File(System.getProperty("user.dir")).getParentFile();
        File imgFile = new File(rootFile, "/Images/" + imageFileName);

        if(rootFile.exists()){
            return Response.ok(imgFile).build();
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @GET
    @Path("/{eventId}/comments")
    public Response getComments(@PathParam("eventId") Integer eventId){

        Event event;
        try {
            event = em.createQuery(
                    "SELECT event FROM Event event WHERE event.eventId = :eventId", Event.class)
                    .setParameter("eventId", eventId).getSingleResult();
        } catch (Exception e){
            return Response.status(Response.Status.NOT_FOUND).build();
        }


        List<Comment> comments;
        try {
            comments = em.createQuery(
                            "SELECT comment FROM Comment comment WHERE comment.event.eventId = :eventId", Comment.class)
                    .setParameter("eventId", eventId).getResultList();
        } catch (Exception e){
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        List<CommentDto> commentsList = new ArrayList<>();
        for (Comment com : comments){
            commentsList.add(new CommentDto(com.getCommentId(), com.getName(), com.getMessage(), com.getLikes(), com.getCreatedAt()));
        }
        return Response.ok().entity(commentsList).build();
    }

    @POST
    @Path("/{eventId}/comments")
    @Transactional
    public Response setComment(@PathParam("eventId") Integer eventId, CommentAddDto comDto){
        Event event;
        try {
            event = em.createQuery(
                            "SELECT event FROM Event event WHERE event.eventId = :eventId", Event.class)
                    .setParameter("eventId", eventId)
                    .getSingleResult();
        } catch (Exception e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        Comment newComment = new Comment(event, comDto.name(), comDto.message());
        em.persist(newComment);
        return Response.status(Response.Status.CREATED).build();
    }

    @PUT
    @Path("/comments/{commentId}/like")
    @Transactional
    public Response addCommentLike(@PathParam("commentId") Integer commentId){
        Comment comment;
        try {
            comment = em.createQuery(
                    "SELECT comment FROM Comment comment WHERE comment.commentId = :commentId", Comment.class)
                    .setParameter("commentId", commentId).getSingleResult();
        } catch (Exception e){
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        comment.setLikes(comment.getLikes() + 1);

        return Response.ok().entity(comment.getLikes()).build();

    }

    @PUT
    @Path("/comments/{commentId}/dislike")
    @Transactional
    public Response removeCommentLike(@PathParam("commentId") Integer commentId){
        Comment comment;
        try {
            comment = em.createQuery(
                    "SELECT comment FROM Comment comment WHERE comment.commentId = :commentId", Comment.class)
                    .setParameter("commentId", commentId).getSingleResult();
        } catch (Exception e){
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        comment.setLikes(comment.getLikes() - 1);

        return Response.ok().entity(comment.getLikes()).build();
    }

    @PUT
    @Path("/{eventId}/like")
    @Transactional
    public Response addEventLike(@PathParam("eventId") Integer eventId){
        Event event;
        try {
            event = em.createQuery(
                    "SELECT event FROM Event event WHERE event.eventId = :eventId", Event.class)
                    .setParameter("eventId", eventId).getSingleResult();
        } catch (Exception e){
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        event.setLikes(event.getLikes() + 1);
        return Response.ok().entity(event.getLikes()).build();

    }


    @PUT
    @Path("/{eventId}/dislike")
    @Transactional
    public Response removeEventLike(@PathParam("eventId") Integer eventId){
        Event event;
        try {
            event = em.createQuery(
                    "SELECT event FROM Event event WHERE event.eventId = :eventId", Event.class)
                    .setParameter("eventId", eventId).getSingleResult();
        } catch (Exception e){
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        event.setLikes(event.getLikes() - 1);

        return Response.ok().entity(event.getLikes()).build();
    }



    @DELETE
    @Path("/{eventId}/comments/{commentId}")
    @Transactional
    public Response removeComment(@PathParam("eventId") Integer eventId, @PathParam("commentId") Integer commentId) {

        Comment comment;
        try {
            comment = em.createQuery(
                            "SELECT comment FROM Comment comment WHERE comment.commentId = :commentId", Comment.class)
                    .setParameter("commentId", commentId).getSingleResult();
        } catch (Exception e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        Event event = comment.getEvent();
        if (!Objects.equals(event.getEventId(), eventId)) {
            return Response.status(Response.Status.NOT_FOUND).entity("No such comment in event").build();
        }

        em.remove(comment);

        return Response.ok().build();
    }
}
