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
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        SharedPreferences prefs = getSharedPreferences("StaffPrefs", MODE_PRIVATE);
        userName = prefs.getString("loggedInUser", "Anställd");

        TextView tvWelcome = findViewById(R.id.tvWelcome);
        tvWelcome.setText("Välkommen, " + userName + "!");

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
                Toast.makeText(ScheduleActivity.this, "Du har accepterat passet!", Toast.LENGTH_LONG).show();
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

        List<Shift> myShifts = getMockShifts(userName);
        shiftAdapter = new ShiftAdapter(myShifts, userName, shift -> {
            Intent intent = new Intent(this, ShiftSwapActivity.class);
            intent.putExtra("selectedShift", shift);
            startActivity(intent);
        });
        rv.setAdapter(shiftAdapter);
    }

    private void refreshData() {
        myIncomingRequests.clear();
        for (SwapRequest r : SwapRequest.allRequests) {
            if (r.receiverName.equalsIgnoreCase(userName) && "PENDING".equals(r.status)) {
                myIncomingRequests.add(r);
            }
        }

        TextView tvHeader = findViewById(R.id.tvRequestsHeader);
        RecyclerView rvReq = findViewById(R.id.rvSwapRequests);
        
        if (myIncomingRequests.isEmpty()) {
            tvHeader.setVisibility(View.GONE);
            rvReq.setVisibility(View.GONE);
        } else {
            tvHeader.setVisibility(View.VISIBLE);
            rvReq.setVisibility(View.VISIBLE);
        }

        if (requestAdapter != null) requestAdapter.notifyDataSetChanged();
        if (shiftAdapter != null) shiftAdapter.notifyDataSetChanged();
    }

    private List<Shift> getMockShifts(String user) {
        List<Shift> list = new ArrayList<>();
        list.add(new Shift("1", "Måndag 30 Okt", "16:00 - 22:00", "Servis", user));
        list.add(new Shift("2", "Onsdag 1 Nov", "11:00 - 16:00", "Servis", user));
        list.add(new Shift("3", "Lördag 4 Nov", "17:00 - 00:00", "Bar", user));
        return list;
    }
}
