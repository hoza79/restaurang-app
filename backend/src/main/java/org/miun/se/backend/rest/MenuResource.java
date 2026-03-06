package org.miun.se.backend.rest;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.Consumes;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.MediaType;
import org.miun.se.backend.DTO.MenuDto;
import org.miun.se.backend.DTO.MenuCarteItemDto;
import org.miun.se.backend.DTO.CarteAddDto;
import org.miun.se.backend.rest.enums.MenuCartCategory;
import org.miun.se.backend.model.MenuItem;
import java.util.ArrayList;
import java.util.List;
import org.miun.se.backend.model.MenuCategory;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.PUT;


@Path("/menu")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)


public class MenuResource{

    @PersistenceContext
    private EntityManager em;


    @GET
    public MenuDto getMenu(){

        List<MenuItem> items = em.createQuery(
                "SELECT item FROM MenuItem item " + "JOIN FETCH item.category category " +
                "WHERE category.categoryName IN ('Appetizers', 'Main Courses', 'Desserts', 'Drinks') "
                + "ORDER BY category.categoryName, item.name", MenuItem.class)
                .getResultList();


        List<MenuCarteItemDto> appet = new ArrayList<>();
        List<MenuCarteItemDto> mainCourses = new ArrayList<>();
        List<MenuCarteItemDto> desserts = new ArrayList<>();
        List<MenuCarteItemDto> drinks = new ArrayList<>();

        for(MenuItem item : items){
            MenuCarteItemDto itemDto = new MenuCarteItemDto(item.getName(), item.getMenuItemId(),
                    item.getDescription(), item.getPrice(), item.getOptions());

            switch (item.getCategory().getCategoryName()){
                case "Appetizers":
                    appet.add(itemDto);
                    break;
                case  "Main Courses":
                    mainCourses.add(itemDto);
                    break;
                case  "Desserts":
                    desserts.add(itemDto);
                    break;
                case  "Drinks":
                    drinks.add(itemDto);
                    break;
            }

        }
        MenuDto menu = new MenuDto(appet, mainCourses, desserts, drinks);
        return menu;


    }

    @POST
    @Path("/{category}")
    @Transactional
    public Response addCarteMeal(@PathParam("category") MenuCartCategory category, CarteAddDto carteDto){
        String databaseName = category.getDatabaseName();
        MenuCategory menu;
        try {
            menu = em.createQuery(
                    "SELECT menu FROM MenuCategory menu WHERE menu.categoryName = :name",
                    MenuCategory.class).setParameter("name", databaseName)
                    .getSingleResult();

        } catch (Exception e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        MenuItem item = new MenuItem(menu, carteDto.name(),
                carteDto.description(), carteDto.price());
        if(carteDto.options()){
            item.setOptions(carteDto.options());
        }
        em.persist(item);
        return Response.ok().build();
    }

    @DELETE
    @Path("/{itemId}")
    @Transactional
    public Response deleteCarteMeal(@PathParam("itemId") Integer itemId){
        MenuItem item;
        try {
            item = em.createQuery(
                    "SELECT item FROM MenuItem item WHERE item.menuItemId = :id", MenuItem.class)
                    .setParameter("id", itemId)
                    .getSingleResult();

        } catch (Exception e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        em.remove(item);
        return Response.ok().build();
    }

    @PUT
    @Path("/{itemId}")
    @Transactional
    public Response editMeal(@PathParam("itemId") Integer itemId, MenuCarteItemDto mealDto){
        MenuItem item;
        try {
            item = em.createQuery(
                    "SELECT item FROM MenuItem item WHERE item.menuItemId = :id", MenuItem.class)
                    .setParameter("id", itemId).getSingleResult();
        } catch (Exception e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        item.setName(mealDto.name());
        item.setDescription(mealDto.description());
        item.setPrice(mealDto.price());
        item.setOptions(mealDto.options());

        return Response.ok().build();
    }
}