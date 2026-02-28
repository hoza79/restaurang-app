package com.antonsskafferi.staff;

import java.io.Serializable;

public class Shift implements Serializable {
    public String id;
    public String date;
    public String time;
    public String role;
    public String employeeName;

    public Shift(String id, String date, String time, String role, String employeeName) {
        this.id = id;
        this.date = date;
        this.time = time;
        this.role = role;
        this.employeeName = employeeName;
    }
}
