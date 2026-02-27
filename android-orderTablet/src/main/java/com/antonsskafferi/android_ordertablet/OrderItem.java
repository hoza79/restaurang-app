package com.antonsskafferi.android_ordertablet;

import java.util.List;

public class OrderItem {

    public enum Destination { BAR, KITCHEN }

    public String dishName, menuCategory, cooking, comment;
    public double price;
    public List<String> sides;
    public int courseSlot;      // 0=Dryck 1=Förrätt 2=Varmrätt 3=Efterrätt
    public int quantity;        // antal
    public long sentAt;         // 0 = ej skickad ännu
    public Destination destination;

    public OrderItem(String dishName, double price, String menuCategory, int courseSlot) {
        this.dishName     = dishName;
        this.price        = price;
        this.menuCategory = menuCategory;
        this.courseSlot   = courseSlot;
        this.quantity     = 1;
        // Dryck → BAR, allt annat → KITCHEN
        this.destination  = (courseSlot == 0) ? Destination.BAR : Destination.KITCHEN;
    }

    public String courseLabel() {
        switch (courseSlot) {
            case 0: return "Dryck";
            case 1: return "Förrätt";
            case 2: return "Varmrätt";
            case 3: return "Efterrätt";
            default: return "?";
        }
    }

    public double totalPrice() { return price * quantity; }
}