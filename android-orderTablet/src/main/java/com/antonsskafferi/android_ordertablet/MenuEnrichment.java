package com.antonsskafferi.android_ordertablet;

import java.text.Normalizer;
import java.util.*;

public final class MenuEnrichment {

    private MenuEnrichment() {}

    public static final class Extras {
        public final boolean hasCookingOptions;
        public final List<String> sides;
        public final List<String> sauces;

        public Extras(boolean hasCookingOptions, List<String> sides, List<String> sauces) {
            this.hasCookingOptions = hasCookingOptions;
            this.sides = sides;
            this.sauces = sauces;
        }
    }

    private static final Map<String, Extras> BY_NAME = new HashMap<>();

    static {
        // Added the hardcoded menu items so only those works.

        put("Entrecôte 250g",
                new Extras(true,
                        Arrays.asList("Pommes frites", "Potatismos", "Grönsaker"),
                        Arrays.asList("Bearnaisesås", "Rödvinssås", "Pepparsås")));

        put("Viltkött",
                new Extras(true,
                        Arrays.asList("Potatismos", "Lingon", "Grönsaker"),
                        Arrays.asList("Rödvinssås", "Viltfond")));

        put("Laxfilé",
                new Extras(false,
                        Arrays.asList("Kokt potatis", "Ugnsrostad potatis", "Grönsaker"),
                        Arrays.asList("Citronsmör", "Hollandaisesås")));

        // Add more as needed…
    }

    private static void put(String dishName, Extras extras) {
        BY_NAME.put(normalize(dishName), extras);
    }

    /** Apply extras onto your existing MenuItem model (in-place). */
    public static void apply(MenuItem item) {
        if (item == null || item.name == null) return;
        Extras ex = BY_NAME.get(normalize(item.name));
        if (ex == null) return;

        item.hasCookingOptions = ex.hasCookingOptions;
        item.sides = ex.sides;
        item.sauces = ex.sauces;
    }

    /** Normalize names to make matching robust across accents/casing/spaces. */
    public static String normalize(String s) {
        if (s == null) return "";
        String x = s.trim();

        // Remove diacritics: é -> e, å -> a, etc.
        x = Normalizer.normalize(x, Normalizer.Form.NFD);
        x = x.replaceAll("\\p{M}+", "");

        // Lowercase and normalize whitespace
        x = x.toLowerCase(Locale.ROOT);
        x = x.replaceAll("\\s+", " ");

        // Optional: remove some punctuation you might see inconsistently
        x = x.replace("–", "-").replace("—", "-");
        return x;
    }
}