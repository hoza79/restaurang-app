package org.miun.se.backend.rest;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.miun.se.backend.model.LunchAvailability;
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
        List<LunchAvailability> meals = em.createQuery(
                "SELECT item FROM LunchAvailability item " + "JOIN FETCH item.menuItem " + "WHERE item.menuItem.category.categoryName ='Lunch'" + "ORDER BY item.availableDate", LunchAvailability.class)
                .getResultList();

        List<MenuItemDto> itemsDto = new ArrayList<>();
        for(LunchAvailability meal : meals){
            MenuItemDto mealDto = new MenuItemDto(meal.getMenuItem().getName(), meal.getMenuItem().getMenuItemId(),
                    meal.getMenuItem().getDescription(), meal.getMenuItem().getPrice(), meal.getMenuItem().getAvailable(), meal.getAvailableDate());
            itemsDto.add(mealDto);
        }

        Map<String, Object> results = new HashMap<>();
        results.put("Category", "Lunch");
        results.put("Items", itemsDto);

        return results;
    }

}
