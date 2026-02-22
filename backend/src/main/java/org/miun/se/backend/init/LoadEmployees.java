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

        em.persist(new Employee("Anna", "Svensson", EmployeeRole.MANAGER, "070-1234567", "anna.svensson@restaurang.se"));
        em.persist(new Employee("Erik", "Lindberg", EmployeeRole.WAITER, "070-2345678", "erik.lindberg@restaurang.se"));
        em.persist(new Employee("Sara", "Johansson", EmployeeRole.WAITER, "070-3456789", "sara.johansson@restaurang.se"));
        em.persist(new Employee("Oskar", "Nilsson", EmployeeRole.WAITER, "070-4567890", "oskar.nilsson@restaurang.se"));
    }
}