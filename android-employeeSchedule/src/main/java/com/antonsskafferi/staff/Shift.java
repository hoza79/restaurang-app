package com.antonsskafferi.staff;

import java.io.Serializable;
import java.time.LocalDate;

public class Shift implements Serializable {
    public String id;
    public LocalDate fullDate; // Ny för datumfiltrering
    public String date; // Behålls för visning "Måndag 30 Okt"
    public String time;
    public String role;
    public String employeeName;

    public Shift(String id, LocalDate fullDate, String date, String time, String role, String employeeName) {
        this.id = id;
        this.fullDate = fullDate;
        this.date = date;
        this.time = time;
        this.role = role;
        this.employeeName = employeeName;
    }
}
