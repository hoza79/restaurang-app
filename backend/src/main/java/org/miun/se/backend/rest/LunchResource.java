package org.miun.se.backend.rest;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.miun.se.backend.DTO.LunchAddDto;
import org.miun.se.backend.DTO.MenuLunchItemDto;
import org.miun.se.backend.model.LunchAvailability;
import org.miun.se.backend.model.MenuCategory;
import org.miun.se.backend.model.MenuItem;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.PUT;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Path("/lunch")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)

public class LunchResource {

    @PersistenceContext
    private EntityManager em;

    //Hela veckans lunch
    @GET
    public Map<String, Object> getWeekMeals(){
        List<LunchAvailability> meals = em.createQuery(
                "SELECT item FROM LunchAvailability item " +
                        "JOIN FETCH item.menuItem " +
                        "WHERE item.menuItem.category.categoryName = 'Lunch' " +
                        "ORDER BY item.availableDate",
                LunchAvailability.class)
                .getResultList();

        List<MenuLunchItemDto> itemsDto = new ArrayList<>();
        for(LunchAvailability meal : meals){
            MenuLunchItemDto mealDto = new MenuLunchItemDto(meal.getMenuItem().getName(), meal.getMenuItem().getMenuItemId(),
                    meal.getMenuItem().getDescription(), meal.getMenuItem().getPrice(), meal.getMenuItem().getAvailable(), meal.getAvailableDate());
            itemsDto.add(mealDto);
        }

        Map<String, Object> results = new HashMap<>();
        results.put("Category", "Lunch");
        results.put("Items", itemsDto);

        return results;
    }

    @POST
    @Transactional
    public Response addLunch(LunchAddDto lunchDto){

        MenuCategory lunch = em.createQuery(
                "SELECT category FROM MenuCategory category WHERE category.categoryName = :name",
                MenuCategory.class)
                .setParameter("name","Lunch")
                .getSingleResult();

        MenuItem item = new MenuItem(
                lunch,
                lunchDto.name(),
                lunchDto.description(),
                lunchDto.price()
        );

        em.persist(item);
        LunchAvailability availability = new LunchAvailability(
                item,
                lunchDto.availableDate()
        );
        em.persist(availability);

        return Response.status(Response.Status.CREATED).build();
    }


    @DELETE
    @Path("/{itemId}")
    @Transactional
    public Response deleteLunch(@PathParam("itemId") Integer menuItemId) {
        MenuItem lunchMeal;
        try {
            lunchMeal = em.createQuery(
                            "SELECT meal FROM MenuItem meal " + "JOIN FETCH meal.category category "
                                    + "WHERE meal.menuItemId = :id AND category.categoryName = 'Lunch'",
                            MenuItem.class).setParameter("id", menuItemId)
                    .getSingleResult();
        }catch (Exception e){
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        em.createQuery("DELETE FROM LunchAvailability row WHERE row.menuItem.menuItemId = :id")
                .setParameter("id", menuItemId)
                .executeUpdate();
        em.remove(lunchMeal);
        return Response.ok().build();
    }

    @PUT
    @Path("/{itemId}")
    @Transactional
    public Response editLunch(@PathParam("itemId") Integer itemId, LunchAddDto lunchDto){
        MenuItem lunchMeal;
        try {
            lunchMeal = em.createQuery(
                    "SELECT meal FROM MenuItem meal " + "JOIN FETCH meal.category category " +
                    "WHERE meal.menuItemId = :id AND category.categoryName = 'Lunch'",
                    MenuItem.class).setParameter("id", itemId)
                    .getSingleResult();

        } catch (Exception e){
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        lunchMeal.setName(lunchDto.name());
        lunchMeal.setPrice(lunchDto.price());
        lunchMeal.setDescription(lunchDto.description());

        LunchAvailability lunch;
        try {
            lunch = em.createQuery(
                    "SELECT lunch FROM LunchAvailability lunch WHERE lunch.menuItem.menuItemId = :id",
                    LunchAvailability.class).setParameter("id", itemId)
                    .getSingleResult();


        }  catch(Exception e){
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        lunch.setAvailableDate(lunchDto.availableDate());
        return Response.ok().build();
    }
}
