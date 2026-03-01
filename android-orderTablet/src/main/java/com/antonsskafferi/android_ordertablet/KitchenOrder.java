package com.antonsskafferi.android_ordertablet;

import java.util.ArrayList;
import java.util.List;

public class KitchenOrder {

    public static class Course {
        public int slot;               // 0=Dryck 1=Förrätt 2=Varmrätt 3=Efterrätt
        public List<String> dishes;
        public long createdAt;
        public long doneAt;            // 0 = ej klar

        public Course(int slot, List<String> dishes, long createdAt) {
            this.slot      = slot;
            this.dishes    = dishes;
            this.createdAt = createdAt;
        }

        public boolean isDone()      { return doneAt > 0; }
        public long elapsedSeconds() { return (System.currentTimeMillis() - createdAt) / 1000; }
        public long elapsedMinutes() { return elapsedSeconds() / 60; }

        public String slotLabel() {
            switch (slot) {
                case 0:  return "Dryck";
                case 1:  return "Förrätt";
                case 2:  return "Varmrätt";
                case 3:  return "Efterrätt";
                default: return "?";
            }
        }
    }

    public int orderId;
    public int tableNumber;
    public List<Course> courses = new ArrayList<>();

    public KitchenOrder(int orderId, int tableNumber) {
        this.orderId     = orderId;
        this.tableNumber = tableNumber;
    }

    public void addCourse(Course c) {
        courses.add(c);
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
        return currentCourse() == null;
    }

    public boolean isFullyDone() {
        return currentCourse() == null;
    }

    public int remainingCourses() {
        int n = 0;
        for (Course c : courses) if (!c.isDone()) n++;
        return n;
    }
}