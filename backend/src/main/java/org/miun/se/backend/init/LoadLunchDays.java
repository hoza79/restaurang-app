package org.miun.se.backend.init;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.DependsOn;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.miun.se.backend.model.LunchAvailability;
import org.miun.se.backend.model.MenuItem;

import java.util.List;

@Singleton
@Startup
@DependsOn("LoadLunchItems")
public class LoadLunchDays {

    private static final String LUNCH_CATEGORY_NAME = "Lunch";
    private static final int MONDAY = 1;

    @PersistenceContext
    private EntityManager em;

    @PostConstruct
    public void init() {
        // Only insert if empty
        Long existing = em.createQuery("SELECT COUNT(a) FROM LunchAvailability a", Long.class)
                .getSingleResult();

        if (existing != 0L) {
            return;
        }

        List<MenuItem> lunchItems = em.createQuery(
                        "SELECT i FROM MenuItem i WHERE i.category.categoryName = :name",
                        MenuItem.class
                )
                .setParameter("name", LUNCH_CATEGORY_NAME)
                .getResultList();

        for (MenuItem item : lunchItems) {
            em.persist(new LunchAvailability(item, MONDAY));
        }
    }
}