package com.antonsskafferi.staff;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MockDatabase {
    private static MockDatabase instance;
    private List<Shift> allShifts = new ArrayList<>();

    private MockDatabase() {
        // Vecka 1 (Nuvarande vecka för demonstratorn)
        allShifts.add(new Shift("1", LocalDate.of(2023, 10, 30), "Måndag 30 Okt", "16:00 - 22:00", "Servis", "anna.berg@antons.se"));
        allShifts.add(new Shift("2", LocalDate.of(2023, 11, 1), "Onsdag 1 Nov", "11:00 - 16:00", "Servis", "anna.berg@antons.se"));
        allShifts.add(new Shift("3", LocalDate.of(2023, 11, 4), "Lördag 4 Nov", "17:00 - 00:00", "Bar", "anna.berg@antons.se"));
        allShifts.add(new Shift("4", LocalDate.of(2023, 10, 31), "Tisdag 31 Okt", "10:00 - 15:00", "Kök", "erik.sten@antons.se"));
        allShifts.add(new Shift("5", LocalDate.of(2023, 11, 2), "Torsdag 2 Nov", "16:00 - 22:00", "Kök", "erik.sten@antons.se"));

        // Vecka 2 (Nästa vecka)
        allShifts.add(new Shift("6", LocalDate.of(2023, 11, 6), "Måndag 6 Nov", "16:00 - 22:00", "Servis", "anna.berg@antons.se"));
        allShifts.add(new Shift("7", LocalDate.of(2023, 11, 8), "Onsdag 8 Nov", "11:00 - 16:00", "Servis", "erik.sten@antons.se"));

        // Vecka 3 (Veckan efter det)
        allShifts.add(new Shift("8", LocalDate.of(2023, 11, 13), "Måndag 13 Nov", "09:00 - 15:00", "Kök", "erik.sten@antons.se"));
        allShifts.add(new Shift("9", LocalDate.of(2023, 11, 15), "Onsdag 15 Nov", "16:00 - 22:00", "Servis", "anna.berg@antons.se"));
    }

    public static MockDatabase getInstance() {
        if (instance == null) instance = new MockDatabase();
        return instance;
    }

    public List<Shift> getShiftsFor(String email, LocalDate start, LocalDate end) {
        List<Shift> result = new ArrayList<>();
        for (Shift s : allShifts) {
            if (s.employeeName.equalsIgnoreCase(email)) {
                if (!s.fullDate.isBefore(start) && !s.fullDate.isAfter(end)) {
                    result.add(s);
                }
            }
        }
        return result;
    }

    public void transferShift(String shiftId, String newOwnerEmail) {
        for (Shift s : allShifts) {
            if (s.id.equals(shiftId)) {
                s.employeeName = newOwnerEmail;
                break;
            }
        }
    }
}
