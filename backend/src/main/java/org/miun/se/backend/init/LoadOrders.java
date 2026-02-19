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
        // 1️⃣ Drink-only order
        // -------------------------------------------------
        CustomerOrder drinkOrder = new CustomerOrder(employees.get(0));
        em.persist(drinkOrder);

        OrderBatch drinkBatch = new OrderBatch(drinkOrder, BatchType.DRINKS);
        em.persist(drinkBatch);

        OrderItem drinkItem = new OrderItem(coke, drinkBatch, "");
        em.persist(drinkItem);

        // -------------------------------------------------
        // 2️⃣ Lunch + Drinks order
        // -------------------------------------------------
        CustomerOrder lunchDrinkOrder = new CustomerOrder(employees.get(1));
        em.persist(lunchDrinkOrder);

        OrderBatch lunchBatch = new OrderBatch(lunchDrinkOrder, BatchType.MAIN_COURSES);
        em.persist(lunchBatch);

        em.persist(new OrderItem(meatballs, lunchBatch, ""));
        em.persist(new OrderItem(renskav, lunchBatch, "extra lingon"));

        OrderBatch lunchDrinkBatch = new OrderBatch(lunchDrinkOrder, BatchType.DRINKS);
        em.persist(lunchDrinkBatch);

        em.persist(new OrderItem(coke, lunchDrinkBatch, ""));

        // -------------------------------------------------
        // 3️⃣ A La Carte (Appetizer + Main)
        // -------------------------------------------------
        CustomerOrder alaCarteOrder = new CustomerOrder(employees.get(2));
        em.persist(alaCarteOrder);

        OrderBatch appetizerBatch = new OrderBatch(alaCarteOrder, BatchType.APPETIZERS);
        em.persist(appetizerBatch);

        em.persist(new OrderItem(garlicBread, appetizerBatch, ""));

        OrderBatch mainBatch = new OrderBatch(alaCarteOrder, BatchType.MAIN_COURSES);
        em.persist(mainBatch);

        em.persist(new OrderItem(oxfile, mainBatch, "medium rare"));

        // -------------------------------------------------
        // 4️⃣ Dessert-only order
        // -------------------------------------------------
        CustomerOrder dessertOrder = new CustomerOrder(employees.get(3));
        em.persist(dessertOrder);

        OrderBatch dessertBatch = new OrderBatch(dessertOrder, BatchType.DESSERTS);
        em.persist(dessertBatch);

        em.persist(new OrderItem(cremeBrulee, dessertBatch, ""));
    }

    private MenuItem findItem(String name) {
        return em.createQuery(
                        "SELECT m FROM MenuItem m WHERE m.name = :name", MenuItem.class)
                .setParameter("name", name)
                .getSingleResult();
    }
}