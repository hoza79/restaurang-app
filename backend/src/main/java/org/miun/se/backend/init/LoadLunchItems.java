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

            em.persist(new MenuItem(lunch, "Köttbullar med potatismos", "Skafferiets hemlagade köttbullar med potatismos och lingonsylt från urskogen.", 100));
            em.persist(new MenuItem(lunch, "Renskav med potatis", "Lyxig renskav a la Anton med smörslungad potatis och ljuvlig gräddsås.", 120));
            em.persist(new MenuItem(lunch, "Potatis och lök soppa", "Köksmästarens egna legendariska potatis och löksoppa med äkta franskt ursprung.", 80));
            em.persist(new MenuItem(lunch, "Kycklinggryta med ris", "Krämig kycklinggryta med paprika och serveras med jasminris.", 105));
            em.persist(new MenuItem(lunch, "Pasta bolognese", "Pasta med långkokt köttfärssås och riven ost.", 95));
            em.persist(new MenuItem(lunch, "Panerad torsk med potatis", "Panerad torskfilé med kokt potatis och remouladsås.", 115));
            em.persist(new MenuItem(lunch, "Lasagne al forno", "Klassisk lasagne med bechamel och tomatsås.", 110));
            em.persist(new MenuItem(lunch, "Vegetarisk curry", "Mild curry med kikärtor, blomkål och ris.", 90));
            em.persist(new MenuItem(lunch, "Fläskfilé med pepparsås", "Stekt fläskfilé med ugnsrostad potatis och pepparsås.", 125));
            em.persist(new MenuItem(lunch, "Fiskgratäng", "Vit fisk i dill- och citronsås med potatismos.", 120));
            em.persist(new MenuItem(lunch, "Chili con carne", "Mustig chili på högrev, bönor och ris.", 100));
            em.persist(new MenuItem(lunch, "Svamprisotto", "Krämig risotto med champinjoner och parmesan.", 98));
            em.persist(new MenuItem(lunch, "Kåldolmar med sås", "Kåldolmar med gräddsås, potatis och lingon.", 112));
            em.persist(new MenuItem(lunch, "Kyckling schnitzel", "Kyckling schnitzel med rostad potatis och örtsås.", 108));
            em.persist(new MenuItem(lunch, "Linsgryta med bröd", "Varm linsgryta med tomat, vitlök och nybakat bröd.", 85));

        }
    }
}
