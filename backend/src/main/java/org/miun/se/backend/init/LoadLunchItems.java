package org.miun.se.backend.init;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.DependsOn;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.miun.se.backend.model.MenuCategory;
import org.miun.se.backend.model.MenuItem;

@Singleton
@Startup
@DependsOn("LoadMenuCategory")
public class LoadLunchItems {
    @PersistenceContext
    private EntityManager em;

    @PostConstruct
    public void init() {
        // Only insert if empty
        if (em.createQuery("SELECT c FROM MenuItem c", MenuItem.class)
                .getResultList().isEmpty()) {

            // Fetch the MenuCategory objects by ID
            MenuCategory lunch = em.find(MenuCategory.class, 1); // Lunch category

            em.persist(new MenuItem(lunch, "Köttbullar med potatismos", "Skafferiets hemlagade köttbullar med potatismos och lingonsylt från urskogen.", 100));
            em.persist(new MenuItem(lunch, "Renskav med potatis", "Lyxig renskav a la Anton med smörslungad potatis och ljuvlig gräddsås.", 120));
            em.persist(new MenuItem(lunch, "Potatis och lök soppa", "Köksmästarens egna legendariska potatis och löksoppa med äkta franskt ursprung.", 90));
        }
    }
}
