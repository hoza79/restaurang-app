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

            MenuCategory lunch = em.createQuery("SELECT c FROM MenuCategory c WHERE c.categoryName = :name",
                    MenuCategory.class
            )
            .setParameter("name", "Lunch")
            .getSingleResult();

            em.persist(new MenuItem(lunch, "KÃ¶ttbullar med potatismos", "Skafferiets hemlagade kÃ¶ttbullar med potatismos och lingonsylt frÃ¥n urskogen.", 100, 2));
            em.persist(new MenuItem(lunch, "Renskav med potatis", "Lyxig renskav a la Anton med smÃ¶rslungad potatis och ljuvlig grÃ¤ddsÃ¥s.", 120, 2));
            em.persist(new MenuItem(lunch, "Potatis och lÃ¶k soppa", "KÃ¶ksmÃ¤starens egna legendariska potatis och lÃ¶ksoppa med Ã¤kta franskt ursprung.", 80, 2));
            em.persist(new MenuItem(lunch, "Kycklinggryta med ris", "KrÃ¤mig kycklinggryta med paprika och serveras med jasminris.", 105, 2));
            em.persist(new MenuItem(lunch, "Pasta bolognese", "Pasta med lÃ¥ngkokt kÃ¶ttfÃ¤rssÃ¥s och riven ost.", 95, 2));
            em.persist(new MenuItem(lunch, "Panerad torsk med potatis", "Panerad torskfilÃ© med kokt potatis och remouladsÃ¥s.", 115, 2));
            em.persist(new MenuItem(lunch, "Lasagne al forno", "Klassisk lasagne med bechamel och tomatsÃ¥s.", 110, 2));
            em.persist(new MenuItem(lunch, "Vegetarisk curry", "Mild curry med kikÃ¤rtor, blomkÃ¥l och ris.", 90, 2));
            em.persist(new MenuItem(lunch, "FlÃ¤skfilÃ© med pepparsÃ¥s", "Stekt flÃ¤skfilÃ© med ugnsrostad potatis och pepparsÃ¥s.", 125, 2));
            em.persist(new MenuItem(lunch, "FiskgratÃ¤ng", "Vit fisk i dill- och citronsÃ¥s med potatismos.", 120, 2));
            em.persist(new MenuItem(lunch, "Chili con carne", "Mustig chili pÃ¥ hÃ¶grev, bÃ¶nor och ris.", 100, 2));
            em.persist(new MenuItem(lunch, "Svamprisotto", "KrÃ¤mig risotto med champinjoner och parmesan.", 98, 2));
            em.persist(new MenuItem(lunch, "KÃ¥ldolmar med sÃ¥s", "KÃ¥ldolmar med grÃ¤ddsÃ¥s, potatis och lingon.", 112, 2));
            em.persist(new MenuItem(lunch, "Kyckling schnitzel", "Kyckling schnitzel med rostad potatis och Ã¶rtsÃ¥s.", 108, 2));
            em.persist(new MenuItem(lunch, "Linsgryta med brÃ¶d", "Varm linsgryta med tomat, vitlÃ¶k och nybakat brÃ¶d.", 85, 2));

        }
    }
}


