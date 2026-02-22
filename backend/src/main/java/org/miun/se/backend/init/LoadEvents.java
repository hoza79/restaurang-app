package org.miun.se.backend.init;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.miun.se.backend.model.Event;

import java.time.LocalDateTime;

@Singleton
@Startup
public class LoadEvents {

    @PersistenceContext
    private EntityManager em;

    @PostConstruct
    public void init() {

        Long existing = em.createQuery(
                        "SELECT COUNT(e) FROM Event e", Long.class)
                .getSingleResult();

        if (existing != 0L) {
            return;
        }

        Event wine = new Event(
                "Wine Tasting Evening",
                "Exclusive Italian wine tasting experience.",
                LocalDateTime.of(2026, 3, 10, 19, 0)
        );
        wine.setImagePath("/images/events/wine.jpg");

        Event jazz = new Event(
                "Live Jazz Night",
                "Dinner and live jazz performance.",
                LocalDateTime.of(2026, 3, 15, 20, 0)
        );
        jazz.setImagePath("/images/events/jazz.jpg");

        Event metal = new Event(
                "Rock n roll week",
                "Brutal death metal from morning til night all week.",
                LocalDateTime.of(2026, 3, 20, 17, 0)
        );
        metal.setImagePath("/images/events/seafood.jpg");

        em.persist(wine);
        em.persist(jazz);
        em.persist(metal);
    }
}