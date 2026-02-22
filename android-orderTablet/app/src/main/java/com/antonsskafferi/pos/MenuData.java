package com.antonsskafferi.pos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MenuData {

    public static List<MenuItem> getAll() {
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

    public static List<MenuItem> getByCategory(String category) {
        List<MenuItem> result = new ArrayList<>();
        for (MenuItem m : getAll())
            if (m.category.equals(category)) result.add(m);
        return result;
    }
}