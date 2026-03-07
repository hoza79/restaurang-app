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
import java.util.Optional;

@Singleton
@Startup
@DependsOn("LoadEmployees")
public class LoadShifts {

    @PersistenceContext
    private EntityManager em;

    @PostConstruct
    public void init() {
        // Check if shifts already exist
        if (em.createQuery("SELECT COUNT(s) FROM Shift s", Long.class).getSingleResult() > 0) {
            return;
        }

        // Get all employees
        List<Employee> employees = em.createQuery("SELECT e FROM Employee e", Employee.class).getResultList();

        // If no employees, do nothing
        if (employees.isEmpty()) {
            return;
        }

        // Find Anna and Erik by their first names
        Optional<Employee> annaOpt = employees.stream().filter(e -> "Anna".equals(e.getFirstName())).findFirst();
        Optional<Employee> erikOpt = employees.stream().filter(e -> "Erik".equals(e.getFirstName())).findFirst();

        if (annaOpt.isEmpty() || erikOpt.isEmpty()) {
            System.err.println("Warning: Could not find both Anna and Erik. No shifts will be generated.");
            return;
        }

        Employee anna = annaOpt.get();
        Employee erik = erikOpt.get();

        // Start from the next or same Monday
        LocalDate startDate = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        // Generate shifts for 2 weeks (14 days)
        for (int i = 0; i < 14; i++) {
            LocalDate currentDate = startDate.plusDays(i);

            Employee dayShiftEmployee;
            Employee eveningShiftEmployee;

            // Alternate shifts each day: if the day index is even, Anna works day, otherwise Erik works day
            if (i % 2 == 0) {
                dayShiftEmployee = anna;
                eveningShiftEmployee = erik;
            } else {
                dayShiftEmployee = erik;
                eveningShiftEmployee = anna;
            }

            // Day shift: 08:00 - 16:00
            em.persist(new Shift(dayShiftEmployee, currentDate.atTime(8, 0), currentDate.atTime(16, 0)));

            // Evening shift: 16:00 - 23:00
            em.persist(new Shift(eveningShiftEmployee, currentDate.atTime(16, 0), currentDate.atTime(23, 0)));
        }
    }
}
