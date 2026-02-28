package com.antonsskafferi.staff;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SwapRequest implements Serializable {
    public String id;
    public String senderName;
    public String receiverName;
    public Shift shift;
    public String status; // "PENDING", "ACCEPTED", "REJECTED"

    // Simulerad lagring för rudimentär implementering
    public static List<SwapRequest> allRequests = new ArrayList<>();

    public SwapRequest(String id, String senderName, String receiverName, Shift shift) {
        this.id = id;
        this.senderName = senderName;
        this.receiverName = receiverName;
        this.shift = shift;
        this.status = "PENDING";
    }
}
