package com.antonsskafferi.staff;

import java.util.ArrayList;
import java.util.List;

public class MockDatabase {
    private static MockDatabase instance;
    private List<Shift> allShifts = new ArrayList<>();

    private MockDatabase() {
        // Initialisera demonstratordata
        allShifts.add(new Shift("1", "Måndag 30 Okt", "16:00 - 22:00", "Servis", "anna.berg@antons.se"));
        allShifts.add(new Shift("2", "Onsdag 1 Nov", "11:00 - 16:00", "Servis", "anna.berg@antons.se"));
        allShifts.add(new Shift("3", "Lördag 4 Nov", "17:00 - 00:00", "Bar", "anna.berg@antons.se"));
        
        allShifts.add(new Shift("4", "Tisdag 31 Okt", "10:00 - 15:00", "Kök", "erik.sten@antons.se"));
        allShifts.add(new Shift("5", "Torsdag 2 Nov", "16:00 - 22:00", "Kök", "erik.sten@antons.se"));
    }

    public static MockDatabase getInstance() {
        if (instance == null) instance = new MockDatabase();
        return instance;
    }

    public List<Shift> getShiftsFor(String email) {
        List<Shift> result = new ArrayList<>();
        for (Shift s : allShifts) {
            if (s.employeeName.equalsIgnoreCase(email)) {
                result.add(s);
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
