package org.miun.se.backend.init;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.miun.se.backend.model.DiningTable;
import org.miun.se.backend.model.enums.TableStatus;

@Singleton
@Startup
public class LoadDiningTables {

    @PersistenceContext
    private EntityManager em;

    @PostConstruct
    public void init() {

        Long existing = em.createQuery(
                        "SELECT COUNT(t) FROM DiningTable t", Long.class)
                .getSingleResult();

        if (existing != 0L) {
            return;
        }

        DiningTable t1 = new DiningTable(1, 2);
        DiningTable t2 = new DiningTable(2, 4);
        DiningTable t3 = new DiningTable(3, 4);
        DiningTable t4 = new DiningTable(4, 6);
        DiningTable t5 = new DiningTable(5, 8);
        DiningTable t6 = new DiningTable(6, 2);
        DiningTable t7 = new DiningTable(7, 4);
        DiningTable t8 = new DiningTable(8, 4);
        DiningTable t9 = new DiningTable(9, 6);
        DiningTable t10 = new DiningTable(10, 4);

        t5.setTableStatus(TableStatus.AVAILABLE);

        em.persist(t1);
        em.persist(t2);
        em.persist(t3);
        em.persist(t4);
        em.persist(t5);
        em.persist(t6);
        em.persist(t7);
        em.persist(t8);
        em.persist(t9);
        em.persist(t10);
    }
}