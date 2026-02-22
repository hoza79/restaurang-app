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
@DependsOn("LoadMenuCategories")
public class LoadMenuItems {
    @PersistenceContext
    private EntityManager em;

    @PostConstruct
    public void init() {
        // Only insert if empty
        if (em.createQuery("SELECT c FROM MenuItem c", MenuItem.class)
                .getResultList().isEmpty()) {

            MenuCategory drinks = em.createQuery("SELECT c FROM MenuCategory c WHERE c.categoryName = :name",
                            MenuCategory.class
                    )
                    .setParameter("name", "Drinks")
                    .getSingleResult();

            MenuCategory lunch = em.createQuery("SELECT c FROM MenuCategory c WHERE c.categoryName = :name",
                    MenuCategory.class
            )
                    .setParameter("name", "Lunch")
                    .getSingleResult();

            MenuCategory appetizers = em.createQuery("SELECT c FROM MenuCategory c WHERE c.categoryName = :name",
                            MenuCategory.class
                    )
                    .setParameter("name", "Appetizers")
                    .getSingleResult();

            MenuCategory mainCourses = em.createQuery("SELECT c FROM MenuCategory c WHERE c.categoryName = :name",
                            MenuCategory.class
                    )
                    .setParameter("name", "Main Courses")
                    .getSingleResult();

            MenuCategory desserts = em.createQuery("SELECT c FROM MenuCategory c WHERE c.categoryName = :name",
                            MenuCategory.class
                    )
                    .setParameter("name", "Desserts")
                    .getSingleResult();

            // Load Drinks

            em.persist(new MenuItem(drinks, "Coca-Cola",
                    "33cl kyld Coca-Cola.", 35));

            em.persist(new MenuItem(drinks, "Pepsi Max",
                    "33cl Pepsi Max.", 35));

            em.persist(new MenuItem(drinks, "Apelsinjuice",
                    "Färskpressad apelsinjuice 25cl.", 45));

            em.persist(new MenuItem(drinks, "Mineralvatten",
                    "Kolsyrat eller stilla vatten 33cl.", 30));

            em.persist(new MenuItem(drinks, "Lättöl",
                    "50cl svensk lättöl.", 55));

            em.persist(new MenuItem(drinks, "Husets rödvin",
                    "Glas 15cl.", 95));

            em.persist(new MenuItem(drinks, "Husets vitvin",
                    "Glas 15cl.", 95));

            em.persist(new MenuItem(drinks, "IPA 50cl",
                    "Lokalt bryggd IPA.", 89));

            em.persist(new MenuItem(drinks, "Cappuccino",
                    "Espresso med varm mjölk och mjölkskum.", 42));

            em.persist(new MenuItem(drinks, "Espresso",
                    "Dubbel espresso.", 35));

            //Load lunch

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

            // Load A la Carte

            // Appetizers
            em.persist(new MenuItem(appetizers, "Vitlöksbröd",
                    "Grillat surdegsbröd med vitlökssmör och persilja.", 65));

            em.persist(new MenuItem(appetizers, "Bruschetta",
                    "Rostat bröd med tomat, basilika och olivolja.", 75));

            em.persist(new MenuItem(appetizers, "Skagenröra",
                    "Handskalade räkor med majonnäs, dill och citron på toast.", 115));

            em.persist(new MenuItem(appetizers, "Caprese",
                    "Buffelmozzarella med tomat, basilika och balsamico.", 95));

            em.persist(new MenuItem(appetizers, "Charkbricka",
                    "Utvalda italienska charkuterier med oliver och bröd.", 145));

            em.persist(new MenuItem(appetizers, "Friterad halloumi",
                    "Krispig halloumi med chilimajonnäs.", 89));

            em.persist(new MenuItem(appetizers, "Lökringar",
                    "Frasiga lökringar med aioli.", 69));

            // Main courses
            em.persist(new MenuItem(mainCourses, "Oxfilé med rödvinssås",
                    "Grillad oxfilé med potatisgratäng och rödvinssås.", 295));

            em.persist(new MenuItem(mainCourses, "Entrecôte",
                    "Saftig entrecôte med pommes frites och bearnaisesås.", 265));

            em.persist(new MenuItem(mainCourses, "Hamburgare Deluxe",
                    "200g högrevsburgare med cheddar, bacon och tryffelmajonnäs.", 195));

            em.persist(new MenuItem(mainCourses, "Caesarsallad med kyckling",
                    "Romansallad, grillad kyckling, parmesan och caesardressing.", 175));

            em.persist(new MenuItem(mainCourses, "Grillad lax",
                    "Grillad laxfilé med hollandaisesås och säsongens grönsaker.", 225));

            em.persist(new MenuItem(mainCourses, "Vegetarisk burgare",
                    "Burgare med halloumi, avokado och sötpotatispommes.", 185));

            em.persist(new MenuItem(mainCourses, "Plankstek",
                    "Oxfilé på planka med duchessepotatis och rödvinssky.", 285));

            em.persist(new MenuItem(mainCourses, "Moules frites",
                    "Blåmusslor i vitvinssås med pommes frites.", 210));

            // Desserts
            em.persist(new MenuItem(desserts, "Crème brûlée",
                    "Klassisk vaniljcrème med karamelliserat socker.", 95));

            em.persist(new MenuItem(desserts, "Chokladfondant",
                    "Varm chokladkaka med rinnande kärna och vaniljglass.", 105));

            em.persist(new MenuItem(desserts, "Tiramisu",
                    "Italiensk dessert med mascarpone och espresso.", 98));

            em.persist(new MenuItem(desserts, "Glass med chokladsås",
                    "Tre kulor vaniljglass med varm chokladsås.", 75));

            em.persist(new MenuItem(desserts, "Cheesecake",
                    "New York cheesecake med bärkompott.", 92));

            em.persist(new MenuItem(desserts, "Äppelpaj",
                    "Varm äppelpaj med vaniljsås.", 85));

            em.persist(new MenuItem(desserts, "Pannacotta",
                    "Vaniljpannacotta med hallonspegel.", 88));

        }
    }
}
