package org.miun.se.backend.init;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.DependsOn;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.miun.se.backend.model.Booking;
import org.miun.se.backend.model.Customer;
import org.miun.se.backend.model.DiningTable;

import java.time.LocalDateTime;
import java.util.List;

@Singleton
@Startup
@DependsOn({"LoadCustomers", "LoadDiningTables"})
public class LoadBookings {

    @PersistenceContext
    private EntityManager em;

    @PostConstruct
    public void init() {

        Long existing = em.createQuery(
                        "SELECT COUNT(b) FROM Booking b", Long.class)
                .getSingleResult();

        if (existing != 0L) {
            return;
        }

        List<Customer> customers =
                em.createQuery("SELECT c FROM Customer c", Customer.class)
                        .getResultList();

        List<DiningTable> tables =
                em.createQuery("SELECT t FROM DiningTable t", DiningTable.class)
                        .getResultList();

        if (customers.isEmpty() || tables.isEmpty()) {
            return;
        }

        Booking b1 = new Booking(
                customers.get(0),
                tables.get(1),
                LocalDateTime.of(2026, 3, 1, 18, 0),
                4
        );

        Booking b2 = new Booking(
                customers.get(1),
                tables.get(0),
                LocalDateTime.of(2026, 3, 2, 19, 0),
                2
        );

        em.persist(b1);
        em.persist(b2);
    }
}