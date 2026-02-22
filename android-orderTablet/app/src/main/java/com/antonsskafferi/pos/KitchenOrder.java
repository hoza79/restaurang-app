package com.antonsskafferi.pos;

import java.util.ArrayList;
import java.util.List;

/**
 * Representerar ett bords AKTIVA order i köket.
 *
 * En order kan ha upp till 4 kurser (courseSlot 0-3).
 * Köket ser bara den AKTUELLA kursen; ett tryck = kurs klar → nästa kurs aktiveras.
 */
public class KitchenOrder {

    public static class Course {
        public int slot;               // 0=Dryck 1=Förrätt 2=Varmrätt 3=Efterrätt
        public List<String> dishes;   // "Entrecôte x2 · Medium"
        public long createdAt;        // när kursen skickades från POS
        public long doneAt;           // 0 = ej klar

        public Course(int slot, List<String> dishes, long createdAt) {
            this.slot = slot;
            this.dishes = dishes;
            this.createdAt = createdAt;
        }

        public boolean isDone()          { return doneAt > 0; }
        public long elapsedSeconds()     { return (System.currentTimeMillis() - createdAt) / 1000; }
        public long elapsedMinutes()     { return elapsedSeconds() / 60; }

        public String slotLabel() {
            switch (slot) {
                case 0: return "Dryck";
                case 1: return "Förrätt";
                case 2: return "Varmrätt";
                case 3: return "Efterrätt";
                default: return "?";
            }
        }
    }

    public int orderId;
    public int tableNumber;
    public List<Course> courses = new ArrayList<>();  // sorterade efter slot
    private int currentCourseIndex = 0;

    public KitchenOrder(int orderId, int tableNumber) {
        this.orderId     = orderId;
        this.tableNumber = tableNumber;
    }

    public void addCourse(Course c) {
        courses.add(c);
        // håll sorterade efter slot så ordningen alltid är Förrätt→Varmrätt→Efterrätt
        courses.sort((a, b) -> a.slot - b.slot);
    }

    /** Den kurs köket ska tillaga nu. null = allt klart. */
    public Course currentCourse() {
        for (Course c : courses)
            if (!c.isDone()) return c;
        return null;
    }

    /** Markerar nuvarande kurs som klar. Returnerar true om HELA ordern är klar. */
    public boolean completeCurrentCourse() {
        Course cur = currentCourse();
        if (cur == null) return true;
        cur.doneAt = System.currentTimeMillis();
        return currentCourse() == null; // true om inga fler kurser
    }

    public boolean isFullyDone() {
        return currentCourse() == null;
    }

    /** Hur många kurser är kvar (ej klara)? */
    public int remainingCourses() {
        int n = 0;
        for (Course c : courses) if (!c.isDone()) n++;
        return n;
    }
}