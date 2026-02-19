package org.miun.se.backend.init;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.DependsOn;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.miun.se.backend.model.LunchAvailability;
import org.miun.se.backend.model.MenuItem;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Singleton
@Startup
@DependsOn("LoadMenuItems")
public class LoadLunchDays {

    private static final String LUNCH_CATEGORY_NAME = "Lunch";

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

        // Start from next Monday
        LocalDate monday = LocalDate.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.MONDAY));

        // Måndag (1): 0,1,2
        em.persist(new LunchAvailability(lunchItems.get(0), monday));
        em.persist(new LunchAvailability(lunchItems.get(1), monday));
        em.persist(new LunchAvailability(lunchItems.get(2), monday));

        // Tisdag (2): 3,4,5
        LocalDate tuesday = monday.plusDays(1);
        em.persist(new LunchAvailability(lunchItems.get(3), tuesday));
        em.persist(new LunchAvailability(lunchItems.get(4), tuesday));
        em.persist(new LunchAvailability(lunchItems.get(5), tuesday));

        // Onsdag (3): 6,7,8
        LocalDate wednesday = monday.plusDays(2);
        em.persist(new LunchAvailability(lunchItems.get(6), wednesday));
        em.persist(new LunchAvailability(lunchItems.get(7), wednesday));
        em.persist(new LunchAvailability(lunchItems.get(8), wednesday));

        // Torsdag (4): 9,10,11
        LocalDate thursday = monday.plusDays(3);
        em.persist(new LunchAvailability(lunchItems.get(9), thursday));
        em.persist(new LunchAvailability(lunchItems.get(10), thursday));
        em.persist(new LunchAvailability(lunchItems.get(11), thursday));

        // Fredag (5): 12,13,14
        LocalDate friday = monday.plusDays(4);
        em.persist(new LunchAvailability(lunchItems.get(12), friday));
        em.persist(new LunchAvailability(lunchItems.get(13), friday));
        em.persist(new LunchAvailability(lunchItems.get(14), friday));
    }
}