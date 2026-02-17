package org.miun.se.backend;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.miun.se.backend.model.MenuItem;
import org.miun.se.backend.DTO.MenuItemDto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Path("/lunch")
@Produces(MediaType.APPLICATION_JSON)

public class LunchResource {

    @PersistenceContext
    private EntityManager em;

    //Hela veckans lunch
    @GET
    public Map<String, Object> getWeekMeals(){
        List<MenuItem> meals = em.createQuery(
                "SELECT item FROM MenuItem item WHERE item.category.categoryName ='Lunch'", MenuItem.class)
                .getResultList();

        List<MenuItemDto> itemsDto = new ArrayList<>();
        for(MenuItem meal : meals){
            MenuItemDto mealDto = new MenuItemDto(meal.getName(), meal.getMenuItemId(), meal.getDescription(), meal.getPrice(), meal.getAvailable());
            itemsDto.add(mealDto);
        }

        Map<String, Object> results = new HashMap<>();
        results.put("Category", "Lunch");
        results.put("Items", itemsDto);

        return results;
    }

}
