package com.antonsskafferi.android_ordertablet;

import java.util.*;

/**
 * Notan är ALLTID öppen per bord tills betalning markeras.
 * CartSession håller alla items för ett bord – skickade och oskickade.
 */
public class Cart {

    // En session per bordsnummer
    private static final Map<Integer, CartSession> sessions = new HashMap<>();
    private static int activeTable = -1;

    // ── Session-klass ──────────────────────────────────────────────
    public static class CartSession {
        public final int tableNumber;
        // NEW: DB primary key for DiningTable
        public Integer tableId;
        public final List<OrderItem> items = new ArrayList<>();
        public boolean paid = false;

        CartSession(int table) { this.tableNumber = table; }

        public void addItem(OrderItem i) { items.add(i); }

        public void removeItem(int idx) {
            if (idx >= 0 && idx < items.size()) items.remove(idx);
        }

        public void increaseQty(int idx) {
            if (idx >= 0 && idx < items.size()) items.get(idx).quantity++;
        }

        public void decreaseQty(int idx) {
            if (idx < 0 || idx >= items.size()) return;
            if (items.get(idx).quantity > 1) items.get(idx).quantity--;
            else items.remove(idx);
        }

        /** Items som INTE skickats ännu. */
        public List<OrderItem> getPending() {
            List<OrderItem> r = new ArrayList<>();
            for (OrderItem i : items) if (i.sentAt == 0) r.add(i);
            return r;
        }

        /** Items för ett specifikt slot som inte skickats. */
        public List<OrderItem> getPendingForSlot(int slot) {
            List<OrderItem> r = new ArrayList<>();
            for (OrderItem i : items)
                if (i.courseSlot == slot && i.sentAt == 0) r.add(i);
            return r;
        }

        public boolean hasPendingForSlot(int slot) {
            return !getPendingForSlot(slot).isEmpty();
        }

        public void markSlotSent(int slot) {
            long now = System.currentTimeMillis();
            for (OrderItem i : items)
                if (i.courseSlot == slot && i.sentAt == 0) i.sentAt = now;
        }

        public int pendingCount() {
            int c = 0;
            for (OrderItem i : items) if (i.sentAt == 0) c++;
            return c;
        }

        public double total() {
            double t = 0;
            for (OrderItem i : items) t += i.totalPrice();
            return t;
        }

        public int itemCount() { return items.size(); }
    }

    // ── Statiska hjälpmetoder ─────────────────────────────────────

    /** Öppnar (eller återhämtar) en session för ett bord. */
    public static CartSession openTable(int tableNumber) {
        return openTable(tableNumber, null);
    }

    public static CartSession openTable(int tableNumber, Integer tableId) {
        activeTable = tableNumber;
        if (!sessions.containsKey(tableNumber))
            sessions.put(tableNumber, new CartSession(tableNumber));

        CartSession s = sessions.get(tableNumber);
        if (tableId != null) s.tableId = tableId; // keep the known id
        return s;
    }

    /** If you fetched tableId later, call this after openTable(). */
    public static void setActiveTableId(Integer tableId) {
        CartSession s = current();
        if (s.tableNumber >= 0) s.tableId = tableId;
    }

    /** Hämtar aktiv session (kraschar aldrig – skapar om null). */
    public static CartSession current() {
        if (activeTable < 0 || !sessions.containsKey(activeTable))
            return new CartSession(-1); // fallback
        return sessions.get(activeTable);
    }

    public static int getActiveTable() { return activeTable; }

    public static Integer getActiveTableId() {
        CartSession s = current();
        return s.tableId;
    }

    /** Markera bord som betalt – stänger sessionen. */
    public static void closeTable(int tableNumber) {
        sessions.remove(tableNumber);
        if (activeTable == tableNumber) activeTable = -1;
    }

    /** Har bordet en öppen nota? */
    public static boolean hasOpenSession(int tableNumber) {
        CartSession s = sessions.get(tableNumber);
        return s != null && !s.paid;
    }

    /** Alla bord med öppen nota. */
    public static Set<Integer> openTables() {
        Set<Integer> open = new HashSet<>();
        for (Map.Entry<Integer, CartSession> e : sessions.entrySet())
            if (!e.getValue().paid) open.add(e.getKey());
        return open;
    }
}