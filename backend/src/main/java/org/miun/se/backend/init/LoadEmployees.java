package org.miun.se.backend.init;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.miun.se.backend.model.Employee;
import org.miun.se.backend.model.enums.EmployeeRole;

@Singleton
@Startup
public class LoadEmployees {

    @PersistenceContext
    private EntityManager em;

    @PostConstruct
    public void init() {
        Long existing = em.createQuery("SELECT COUNT(e) FROM Employee e", Long.class)
                .getSingleResult();

        if (existing != 0L) {
            return;
        }

        em.persist(new Employee("Anna", "Berg", EmployeeRole.WAITER, "070-1234567", "anna.berg@antons.se"));
        em.persist(new Employee("Erik", "Sten", EmployeeRole.WAITER, "070-2345678", "erik.sten@antons.se"));
        em.persist(new Employee("Sara", "Grus", EmployeeRole.WAITER, "070-3456789", "sara.grus@antons.se"));
    }
}