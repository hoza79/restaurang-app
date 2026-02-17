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

        // Måndag (1): 0,1,2
        em.persist(new LunchAvailability(lunchItems.get(0), 1));
        em.persist(new LunchAvailability(lunchItems.get(1), 1));
        em.persist(new LunchAvailability(lunchItems.get(2), 1));

        // Tisdag (2): 3,4,5
        em.persist(new LunchAvailability(lunchItems.get(3), 2));
        em.persist(new LunchAvailability(lunchItems.get(4), 2));
        em.persist(new LunchAvailability(lunchItems.get(5), 2));

        // Onsdag (3): 6,7,8
        em.persist(new LunchAvailability(lunchItems.get(6), 3));
        em.persist(new LunchAvailability(lunchItems.get(7), 3));
        em.persist(new LunchAvailability(lunchItems.get(8), 3));

        // Torsdag (4): 9,10,11
        em.persist(new LunchAvailability(lunchItems.get(9), 4));
        em.persist(new LunchAvailability(lunchItems.get(10), 4));
        em.persist(new LunchAvailability(lunchItems.get(11), 4));

        // Fredag (5): 12,13,14
        em.persist(new LunchAvailability(lunchItems.get(12), 5));
        em.persist(new LunchAvailability(lunchItems.get(13), 5));
        em.persist(new LunchAvailability(lunchItems.get(14), 5));
    }
}