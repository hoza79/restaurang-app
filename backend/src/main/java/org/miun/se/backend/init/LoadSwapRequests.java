package org.miun.se.backend.init;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.DependsOn;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.miun.se.backend.model.Employee;
import org.miun.se.backend.model.Shift;
import org.miun.se.backend.model.SwapRequest;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Singleton
@Startup
@DependsOn({"LoadEmployees", "LoadShifts"})
public class LoadSwapRequests {

    @PersistenceContext
    private EntityManager em;

    @PostConstruct
    public void init() {
        Long existing = em.createQuery("SELECT COUNT(s) FROM SwapRequest s", Long.class)
                .getSingleResult();

        if (existing != 0L) {
            return;
        }

        List<Employee> employees = em.createQuery("SELECT e FROM Employee e", Employee.class)
                .getResultList();

        List<Shift> shifts = em.createQuery("SELECT s FROM Shift s", Shift.class)
                .getResultList();

        LocalDate monday = LocalDate.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.MONDAY));

        em.persist(new SwapRequest(employees.get(0), employees.get(1), shifts.get(1)));

    }
}