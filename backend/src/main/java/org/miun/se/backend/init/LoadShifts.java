package org.miun.se.backend.init;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.DependsOn;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.miun.se.backend.model.Employee;
import org.miun.se.backend.model.Shift;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Singleton
@Startup
@DependsOn("LoadEmployees")
public class LoadShifts {

    @PersistenceContext
    private EntityManager em;

    @PostConstruct
    public void init() {
        Long existing = em.createQuery("SELECT COUNT(s) FROM Shift s", Long.class)
                .getSingleResult();

        if (existing != 0L) {
            return;
        }

        List<Employee> employees = em.createQuery("SELECT e FROM Employee e", Employee.class)
                .getResultList();

        LocalDate monday = LocalDate.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.MONDAY));

        // Monday shifts
        em.persist(new Shift(employees.get(0), monday.atTime(8, 0), monday.atTime(16, 0)));
        em.persist(new Shift(employees.get(1), monday.atTime(10, 0), monday.atTime(18, 0)));
        em.persist(new Shift(employees.get(2), monday.atTime(12, 0), monday.atTime(20, 0)));

        // Tuesday shifts
        LocalDate tuesday = monday.plusDays(1);
        em.persist(new Shift(employees.get(1), tuesday.atTime(8, 0), tuesday.atTime(16, 0)));
        em.persist(new Shift(employees.get(2), tuesday.atTime(10, 0), tuesday.atTime(18, 0)));
        em.persist(new Shift(employees.get(3), tuesday.atTime(12, 0), tuesday.atTime(20, 0)));

        // Wednesday shifts
        LocalDate wednesday = monday.plusDays(2);
        em.persist(new Shift(employees.get(0), wednesday.atTime(8, 0), wednesday.atTime(16, 0)));
        em.persist(new Shift(employees.get(3), wednesday.atTime(10, 0), wednesday.atTime(18, 0)));
        em.persist(new Shift(employees.get(1), wednesday.atTime(12, 0), wednesday.atTime(20, 0)));

        // Thursday shifts
        LocalDate thursday = monday.plusDays(3);
        em.persist(new Shift(employees.get(2), thursday.atTime(8, 0), thursday.atTime(16, 0)));
        em.persist(new Shift(employees.get(0), thursday.atTime(10, 0), thursday.atTime(18, 0)));
        em.persist(new Shift(employees.get(3), thursday.atTime(12, 0), thursday.atTime(20, 0)));

        // Friday shifts
        LocalDate friday = monday.plusDays(4);
        em.persist(new Shift(employees.get(1), friday.atTime(8, 0), friday.atTime(16, 0)));
        em.persist(new Shift(employees.get(2), friday.atTime(10, 0), friday.atTime(18, 0)));
        em.persist(new Shift(employees.get(3), friday.atTime(12, 0), friday.atTime(20, 0)));
    }
}