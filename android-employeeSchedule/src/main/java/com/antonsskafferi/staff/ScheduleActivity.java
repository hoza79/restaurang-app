package com.antonsskafferi.staff;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class ScheduleActivity extends AppCompatActivity {

    private List<SwapRequest> myIncomingRequests = new ArrayList<>();
    private SwapRequestAdapter requestAdapter;
    private ShiftAdapter shiftAdapter;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        SharedPreferences prefs = getSharedPreferences("StaffPrefs", MODE_PRIVATE);
        userEmail = prefs.getString("loggedInUser", "anna.berg@antons.se");

        TextView tvWelcome = findViewById(R.id.tvWelcome);
        tvWelcome.setText("Inloggad: " + userEmail);

        findViewById(R.id.btnLogout).setOnClickListener(v -> {
            prefs.edit().remove("loggedInUser").apply();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        setupRequestsList();
        setupScheduleList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshData();
    }

    private void setupRequestsList() {
        RecyclerView rvRequests = findViewById(R.id.rvSwapRequests);
        rvRequests.setLayoutManager(new LinearLayoutManager(this));

        requestAdapter = new SwapRequestAdapter(myIncomingRequests, new SwapRequestAdapter.OnSwapRequestListener() {
            @Override
            public void onAccept(SwapRequest request) {
                // GENOMFÖR BYTET I DATABASEN
                MockDatabase.getInstance().transferShift(request.shift.id, request.receiverName);
                
                Toast.makeText(ScheduleActivity.this, "Passet är nu ditt!", Toast.LENGTH_LONG).show();
                request.status = "ACCEPTED";
                refreshData();
            }

            @Override
            public void onDeny(SwapRequest request) {
                Toast.makeText(ScheduleActivity.this, "Förfrågan nekad", Toast.LENGTH_SHORT).show();
                request.status = "REJECTED";
                refreshData();
            }
        });
        rvRequests.setAdapter(requestAdapter);
    }

    private void setupScheduleList() {
        RecyclerView rv = findViewById(R.id.rvSchedule);
        rv.setLayoutManager(new LinearLayoutManager(this));

        shiftAdapter = new ShiftAdapter(new ArrayList<>(), userEmail, shift -> {
            Intent intent = new Intent(this, ShiftSwapActivity.class);
            intent.putExtra("selectedShift", shift);
            startActivity(intent);
        });
        rv.setAdapter(shiftAdapter);
    }

    private void refreshData() {
        // Uppdatera inkommande förfrågningar
        myIncomingRequests.clear();
        for (SwapRequest r : SwapRequest.allRequests) {
            if (r.receiverName.equalsIgnoreCase(userEmail) && "PENDING".equals(r.status)) {
                myIncomingRequests.add(r);
            }
        }

        // Uppdatera schemat från MockDatabase
        List<Shift> myShifts = MockDatabase.getInstance().getShiftsFor(userEmail);
        
        TextView tvHeader = findViewById(R.id.tvRequestsHeader);
        RecyclerView rvReq = findViewById(R.id.rvSwapRequests);
        
        if (myIncomingRequests.isEmpty()) {
            tvHeader.setVisibility(View.GONE);
            rvReq.setVisibility(View.GONE);
        } else {
            tvHeader.setVisibility(View.VISIBLE);
            rvReq.setVisibility(View.VISIBLE);
        }

        // Uppdatera adaptrarna
        if (requestAdapter != null) requestAdapter.notifyDataSetChanged();
        if (shiftAdapter != null) {
            shiftAdapter.setShifts(myShifts);
        }
    }
}
