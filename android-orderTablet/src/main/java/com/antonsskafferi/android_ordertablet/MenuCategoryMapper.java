package com.antonsskafferi.android_ordertablet;

import java.util.Locale;

public final class MenuCategoryMapper {

    private MenuCategoryMapper() {}

    /** Backend grouping key / name -> Swedish UI label */
    public static String toUiCategory(String backendCategory) {
        if (backendCategory == null) return "";
        String c = backendCategory.trim().toLowerCase(Locale.ROOT);

        // Accept both list names and category display names
        switch (c) {
            case "drinks":
            case "drink":
                return "Dryck";

            case "appetizers":
            case "appetizer":
                return "Förrätt";

            case "main courses":
            case "maincourses":
            case "main course":
                return "Varmrätt";

            case "desserts":
            case "dessert":
                return "Efterrätt";

            case "lunch":
                return "Lunch";

            default:
                return backendCategory; // fallback
        }
    }

    /** Swedish UI label -> backend enum/database category name (useful later for POST /menu/{category}) */
    public static String toBackendDatabaseName(String uiCategory) {
        if (uiCategory == null) return "";
        String c = uiCategory.trim().toLowerCase(Locale.ROOT);

        switch (c) {
            case "dryck":     return "Drinks";
            case "förrätt":   return "Appetizers";
            case "varmrätt":  return "Main Courses";
            case "efterrätt": return "Desserts";
            case "lunch":     return "Lunch";
            default:          return uiCategory;
        }
    }
}