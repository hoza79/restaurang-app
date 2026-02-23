package org.miun.se.backend.rest;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.core.MediaType;
import org.miun.se.backend.DTO.MenuDto;
import org.miun.se.backend.DTO.MenuCarteItemDto;
import org.miun.se.backend.model.MenuItem;
import java.util.ArrayList;
import java.util.List;

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
                    item.getDescription(), item.getPrice(), item.getAvailable());

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
}