package com.antonsskafferi.android_ordertablet;

import java.util.List;

public class MenuItem {
    public int    id;
    public String name, description, category;
    public double price;
    public boolean hasCookingOptions;
    public List<String> sides, sauces;

    public MenuItem(int id, String name, String description, double price,
                    String category, boolean hasCookingOptions,
                    List<String> sides, List<String> sauces) {
        this.id                = id;
        this.name              = name;
        this.description       = description;
        this.price             = price;
        this.category          = category;
        this.hasCookingOptions = hasCookingOptions;
        this.sides             = sides;
        this.sauces            = sauces;
    }
}