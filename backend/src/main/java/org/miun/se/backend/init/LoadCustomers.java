package org.miun.se.backend.init;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.miun.se.backend.model.Customer;

@Singleton
@Startup
public class LoadCustomers {

    @PersistenceContext
    private EntityManager em;

    @PostConstruct
    public void init() {

        Long existing = em.createQuery(
                        "SELECT COUNT(c) FROM Customer c", Long.class)
                .getSingleResult();

        if (existing != 0L) {
            return;
        }

        em.persist(new Customer("Emma", "Johansson", "0701234567"));
        em.persist(new Customer("Liam", "Andersson", "0702345678"));
        em.persist(new Customer("Olivia", "Karlsson", "0703456789"));
        em.persist(new Customer("Noah", "Nilsson", "0704567890"));
        em.persist(new Customer("Alice", "Eriksson", "0705678901"));
    }
}