package org.miun.se.backend.init;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.DependsOn;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.miun.se.backend.model.*;
import org.miun.se.backend.model.enums.BatchType;

import java.util.List;

@Singleton
@Startup
@DependsOn({"LoadDiningTables", "LoadEmployees", "LoadMenuItems"})
public class LoadOrders {

    @PersistenceContext
    private EntityManager em;

    @PostConstruct
    public void init() {
        Long existing = em.createQuery("SELECT COUNT(o) FROM CustomerOrder o", Long.class)
                .getSingleResult();

        if (existing != 0L) {
            return;
        }

        List<DiningTable> tables = em.createQuery("SELECT e FROM DiningTable e", DiningTable.class)
                .getResultList();

        List<Employee> employees = em.createQuery("SELECT e FROM Employee e", Employee.class)
                .getResultList();

        // Fetch specific menu items by name (safer than index)
        MenuItem coke = findItem("Coca-Cola");
        MenuItem meatballs = findItem("Köttbullar med potatismos");
        MenuItem renskav = findItem("Renskav med potatis");
        MenuItem garlicBread = findItem("Vitlöksbröd");
        MenuItem oxfile = findItem("Oxfilé med rödvinssås");
        MenuItem cremeBrulee = findItem("Crème brûlée");

        // -------------------------------------------------
        // Drink-only order
        // -------------------------------------------------
        CustomerOrder drinkOrder = new CustomerOrder(tables.get(0), employees.get(0));
        em.persist(drinkOrder);

        OrderBatch drinkBatch = drinkOrder.addBatch(BatchType.DRINK);
        drinkBatch.addItem(coke, 2, "");
        em.persist(drinkBatch);

        // -------------------------------------------------
        // Lunch + Drinks order
        // -------------------------------------------------
        CustomerOrder lunchDrinkOrder = new CustomerOrder(tables.get(1), employees.get(1));
        em.persist(lunchDrinkOrder);

        OrderBatch lunchBatch = lunchDrinkOrder.addBatch(BatchType.MAIN_COURSE);
        lunchBatch.addItem(meatballs, 1, "");
        lunchBatch.addItem(renskav, 1, "extra lingon");
        em.persist(lunchBatch);

        OrderBatch lunchDrinkBatch = lunchDrinkOrder.addBatch(BatchType.DRINK);
        lunchDrinkBatch.addItem(coke, 2, "no ice");
        em.persist(lunchDrinkBatch);



        // -------------------------------------------------
        // A La Carte (Appetizer + Main + Drink)
        // -------------------------------------------------
        CustomerOrder alaCarteOrder = new CustomerOrder(tables.get(2), employees.get(2));
        em.persist(alaCarteOrder);

        OrderBatch alaCarteBatch = alaCarteOrder.addBatch(BatchType.APPETIZER);
        alaCarteBatch.addItem(garlicBread, 1, "");
        em.persist(alaCarteBatch);

        OrderBatch alaCarteMainBatch = alaCarteOrder.addBatch(BatchType.MAIN_COURSE);
        alaCarteMainBatch.addItem(oxfile, 1, "medium rare");
        em.persist(alaCarteMainBatch);

        OrderBatch alaCarteDrinkBatch = alaCarteOrder.addBatch(BatchType.DRINK);
        alaCarteDrinkBatch.addItem(coke, 1, "half ice");
        em.persist(alaCarteDrinkBatch);



        // -------------------------------------------------
        // Dessert-only order
        // -------------------------------------------------
        CustomerOrder dessertOrder = new CustomerOrder(tables.get(3), employees.get(3));
        em.persist(dessertOrder);

        OrderBatch dessertBatch = dessertOrder.addBatch(BatchType.DESSERT);
        dessertBatch.addItem(cremeBrulee, 1, "");
        em.persist(dessertBatch);


        // -------------------------------------------------
        // Appetizer served with Main Course order
        // -------------------------------------------------

        // Create order
        CustomerOrder appetizerOrder = new CustomerOrder(tables.get(4), employees.get(3));
        // Persist order in DB
        em.persist(appetizerOrder);
        // Create batches
        OrderBatch mainAppetizerBatch = appetizerOrder.addBatch(BatchType.MAIN_COURSE);
        // Appetizer
        mainAppetizerBatch.addItem(garlicBread, 1, "no garlic");
        // Main course
        mainAppetizerBatch.addItem(oxfile, 1, "rare");
        // Persist batch in DB (this happens when waiter sends order to kitchen)
        em.persist(mainAppetizerBatch);
    }

    private MenuItem findItem(String name) {
        return em.createQuery(
                        "SELECT m FROM MenuItem m WHERE m.name = :name", MenuItem.class)
                .setParameter("name", name)
                .getSingleResult();
    }
}