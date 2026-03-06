package com.antonsskafferi.android_ordertablet;

import java.util.*;

public class KitchenDataStore {

    private static KitchenDataStore instance;
    private final List<KitchenOrder> orders = new ArrayList<>();
    private int nextOrderId = 1;

    private KitchenDataStore() {}

    public static KitchenDataStore getInstance() {
        if (instance == null) instance = new KitchenDataStore();
        return instance;
    }

    public void addOrder(int tableNumber, int courseSlot, List<OrderItem> items) {
        KitchenOrder existing = findByTable(tableNumber);
        if (existing == null) {
            existing = new KitchenOrder(nextOrderId++, tableNumber);
            orders.add(existing);
        }

        List<String> dishes = new ArrayList<>();
        for (OrderItem item : items) {
            StringBuilder sb = new StringBuilder(item.dishName);
            if (item.quantity > 1) sb.append(" x").append(item.quantity);
            if (item.cooking != null && !item.cooking.isEmpty())
                sb.append(" · ").append(item.cooking);
            if (item.sides != null && !item.sides.isEmpty())
                sb.append(" · ").append(String.join(", ", item.sides));
            if (item.comment != null && !item.comment.isEmpty())
                sb.append("\n   💬 ").append(item.comment);
            dishes.add(sb.toString());
        }

        // batchId = 0 för lokala ordrar (används ej för API-completion)
        existing.addCourse(new KitchenOrder.Course(0, courseSlot, dishes,
                System.currentTimeMillis()));
    }

    public List<KitchenOrder> getActiveOrders() {
        List<KitchenOrder> active = new ArrayList<>();
        for (KitchenOrder o : orders)
            if (!o.isFullyDone()) active.add(o);
        active.sort((a, b) -> {
            KitchenOrder.Course ca = a.currentCourse();
            KitchenOrder.Course cb = b.currentCourse();
            if (ca == null) return 1;
            if (cb == null) return -1;
            return Long.compare(ca.createdAt, cb.createdAt);
        });
        return active;
    }

    public void removeOrder(int tableNumber) {
        orders.removeIf(o -> o.tableNumber == tableNumber);
    }

    private KitchenOrder findByTable(int tableNumber) {
        for (KitchenOrder o : orders)
            if (o.tableNumber == tableNumber && !o.isFullyDone()) return o;
        return null;
    }
}