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
@DependsOn({"LoadEmployees", "LoadMenuItems"})
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
        CustomerOrder drinkOrder = new CustomerOrder(employees.get(0));

        OrderBatch drinkBatch = drinkOrder.addBatch(BatchType.BAR);
        drinkBatch.addItem(coke, 2, "");

        em.persist(drinkOrder);

        // -------------------------------------------------
        // Lunch + Drinks order
        // -------------------------------------------------
        CustomerOrder lunchDrinkOrder = new CustomerOrder(employees.get(1));

        OrderBatch lunchBatch = lunchDrinkOrder.addBatch(BatchType.KITCHEN);
        lunchBatch.addItem(meatballs, 1, "");
        lunchBatch.addItem(renskav, 1, "extra lingon");

        OrderBatch lunchDrinkBatch = lunchDrinkOrder.addBatch(BatchType.BAR);
        lunchDrinkBatch.addItem(coke, 2, "no ice");

        em.persist(lunchDrinkOrder);

        // -------------------------------------------------
        // A La Carte (Appetizer + Main + Drink)
        // -------------------------------------------------
        CustomerOrder alaCarteOrder = new CustomerOrder(employees.get(2));

        OrderBatch alaCarteBatch = alaCarteOrder.addBatch(BatchType.KITCHEN);
        alaCarteBatch.addItem(garlicBread, 1, "");
        alaCarteBatch.addItem(oxfile, 1, "medium rare");

        OrderBatch alaCarteDrinkBatch = alaCarteOrder.addBatch(BatchType.BAR);
        alaCarteDrinkBatch.addItem(coke, 1, "half ice");

        em.persist(alaCarteOrder);

        // -------------------------------------------------
        // Dessert-only order
        // -------------------------------------------------
        CustomerOrder dessertOrder = new CustomerOrder(employees.get(3));

        OrderBatch dessertBatch = dessertOrder.addBatch(BatchType.KITCHEN);
        dessertBatch.addItem(cremeBrulee, 1, "");

        em.persist(dessertOrder);

        // -------------------------------------------------
        // Appetizer server with Main Course order
        // -------------------------------------------------
        CustomerOrder appetizerOrder = new CustomerOrder(employees.get(3));

        OrderBatch mainAppetizerBatch = appetizerOrder.addBatch(BatchType.KITCHEN);
        mainAppetizerBatch.addItem(garlicBread, 1, "no garlic");
        mainAppetizerBatch.addItem(oxfile, 1, "rare");
        mainAppetizerBatch.setServeTogether(true);
        em.persist(appetizerOrder);
    }

    private MenuItem findItem(String name) {
        return em.createQuery(
                        "SELECT m FROM MenuItem m WHERE m.name = :name", MenuItem.class)
                .setParameter("name", name)
                .getSingleResult();
    }
}