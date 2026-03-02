package org.miun.se.backend.rest.enums;

public enum MenuCartCategory {


    appetizers("Appetizers"),
    maincourses("Main Courses"),
    desserts("Desserts"),
    drinks("Drinks");

    private String databaseName;
    MenuCartCategory(String databaseName) {
        this.databaseName = databaseName;
    }
    public String getDatabaseName(){ return databaseName; }
}