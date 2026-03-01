package com.antonsskafferi.android_ordertablet;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MenuData {

    private static List<MenuItem> cachedAll = null;

    public static synchronized boolean hasApiMenu() {
        return cachedAll != null && !cachedAll.isEmpty();
    }

    public static List<MenuItem> getAll() {
        Log.d("MenuData", "getAll() cachedAll=" + (cachedAll == null ? "null" : cachedAll.size()));
        if (cachedAll != null && !cachedAll.isEmpty()) {
            return cachedAll;
        }
        // fallback until API loads (or if API fails)
        return buildHardcoded();
    }

    public static List<MenuItem> getByCategory(String category) {
        List<MenuItem> result = new ArrayList<>();
        for (MenuItem m : getAll())
            if (m.category.equals(category)) result.add(m);
        return result;
    }

    public static synchronized void setFromApi(com.antonsskafferi.android_ordertablet.net.MenuDto dto) {
        if (dto == null) return;

        List<MenuItem> out = new ArrayList<>();

        // Convert each category list -> Swedish UI category
        addItems(out, dto.drinks,     "Dryck");
        addItems(out, dto.appetizers, "Förrätt");
        addItems(out, dto.mainCourses,"Varmrätt");
        addItems(out, dto.desserts,   "Efterrätt");

        cachedAll = out;
    }

    private static void addItems(List<MenuItem> out,
                                 List<com.antonsskafferi.android_ordertablet.net.MenuCarteItemDto> items,
                                 String uiCategory) {
        if (items == null) return;

        for (com.antonsskafferi.android_ordertablet.net.MenuCarteItemDto it : items) {
            if (it == null || it.menuItemId == null || it.name == null) continue;

            // Optional: hide unavailable items
            if (it.available != null && !it.available) continue;

            double price = (it.price != null) ? it.price : 0.0;

            MenuItem ui = new MenuItem(
                    it.menuItemId,
                    it.name,
                    it.description != null ? it.description : "",
                    price,
                    uiCategory,
                    false,
                    null,
                    null
            );

            // apply local extras (cooking/sides/sauces) by name
            MenuEnrichment.apply(ui);

            out.add(ui);
        }
    }

    private static List<MenuItem> buildHardcoded() {
        Log.d("MenuData", "Hardcoding Menu");
        List<MenuItem> list = new ArrayList<>();
        // Dryck
        list.add(new MenuItem(1,  "Mineralvatten",    "Fortfarande / kolsyrat",    29,  "Dryck",     false, null, null));
        list.add(new MenuItem(2,  "Lättöl",           "0,5%",                      45,  "Dryck",     false, null, null));
        list.add(new MenuItem(3,  "Husets rödvin",    "Glas 15cl",                115,  "Dryck",     false, null, null));
        list.add(new MenuItem(4,  "Husets vitvin",    "Glas 15cl",                115,  "Dryck",     false, null, null));
        list.add(new MenuItem(5,  "Kaffe",            "Bryggkaffe",                35,  "Dryck",     false, null, null));

        // Förrätt
        list.add(new MenuItem(6,  "Toast Skagen",     "Räkor, majonnäs, dill",    135,  "Förrätt",   false, null, null));
        list.add(new MenuItem(7,  "Löjromssallad",    "Löjrom, gräddfil, rödlök", 145,  "Förrätt",   false, null, null));
        list.add(new MenuItem(8,  "Vitlöksräkor",     "Smör, vitlök, baguette",   125,  "Förrätt",   false, null, null));

        // Varmrätt
        list.add(new MenuItem(9,  "Entrecôte 250g",   "Bearnaise, pommes frites", 285,  "Varmrätt",  true,
                Arrays.asList("Pommes frites", "Potatismos", "Grönsaker"),
                Arrays.asList("Bearnaisesås", "Rödvinssås", "Pepparsås")));
        list.add(new MenuItem(10, "Laxfilé",          "Citronsmör, haricots verts",245, "Varmrätt",  false,
                Arrays.asList("Kokt potatis", "Ugnsrostad potatis", "Grönsaker"),
                Arrays.asList("Citronsmör", "Hollandaisesås")));
        list.add(new MenuItem(11, "Vegetarisk pasta", "Pesto, soltorkad tomat",   185,  "Varmrätt",  false, null, null));
        list.add(new MenuItem(12, "Viltkött",         "Veckans jägarrätt",        265,  "Varmrätt",  true,
                Arrays.asList("Potatismos", "Lingon", "Grönsaker"),
                Arrays.asList("Rödvinssås", "Viltfond")));

        // Efterrätt
        list.add(new MenuItem(13, "Crème brûlée",     "Vanilj, bär",               95,  "Efterrätt", false, null, null));
        list.add(new MenuItem(14, "Chokladfondant",   "Varm kärna, vaniljglass",   105,  "Efterrätt", false, null, null));
        list.add(new MenuItem(15, "Ostkaka",          "Sylt, grädde",               85,  "Efterrätt", false, null, null));

        return list;
    }
}