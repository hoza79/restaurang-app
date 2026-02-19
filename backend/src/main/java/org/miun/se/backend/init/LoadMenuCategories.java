package org.miun.se.backend.init;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.miun.se.backend.model.MenuCategory;

@Singleton
@Startup
public class LoadMenuCategories {
    @PersistenceContext
    private EntityManager em;

    @PostConstruct
    public void init() {
        // Only insert if empty
        if (em.createQuery("SELECT c FROM MenuCategory c", MenuCategory.class)
                .getResultList().isEmpty()) {

            em.persist(new MenuCategory("Lunch"));
            em.persist(new MenuCategory("A La Carte"));
            em.persist(new MenuCategory("Drinks"));
        }
    }
}
