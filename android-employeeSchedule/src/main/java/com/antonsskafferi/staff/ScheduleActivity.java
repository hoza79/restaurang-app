package com.antonsskafferi.staff;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ScheduleActivity extends AppCompatActivity {

    private List<SwapRequest> myIncomingRequests = new ArrayList<>();
    private SwapRequestAdapter requestAdapter;
    private ShiftAdapter shiftAdapter;
    private String userEmail;

    // Veckoväljare variabler
    private LocalDate currentMonday;
    private DateTimeFormatter rangeFormatter = DateTimeFormatter.ofPattern("d MMM", new Locale("sv", "SE"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        SharedPreferences prefs = getSharedPreferences("StaffPrefs", MODE_PRIVATE);
        userEmail = prefs.getString("loggedInUser", "anna.berg@antons.se");

        TextView tvWelcome = findViewById(R.id.tvWelcome);
        tvWelcome.setText("Inloggad: " + userEmail);

        // Initiera veckoväljare (starta på nuvarande veckas måndag)
        currentMonday = LocalDate.of(2023, 10, 30); // Demonstrator startar här
        updateWeekUI();

        findViewById(R.id.btnPrevWeek).setOnClickListener(v -> prevWeek());
        findViewById(R.id.btnNextWeek).setOnClickListener(v -> nextWeek());

        findViewById(R.id.btnLogout).setOnClickListener(v -> {
            prefs.edit().remove("loggedInUser").apply();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        setupRequestsList();
        setupScheduleList();
        setupSwiping();
    }

    private void nextWeek() {
        currentMonday = currentMonday.plusWeeks(1);
        updateWeekUI();
        refreshData();
    }

    private void prevWeek() {
        currentMonday = currentMonday.minusWeeks(1);
        updateWeekUI();
        refreshData();
    }

    private void updateWeekUI() {
        LocalDate sunday = currentMonday.plusDays(6);
        TextView tvRange = findViewById(R.id.tvWeekRange);
        String rangeText = currentMonday.format(rangeFormatter) + " - " + sunday.format(rangeFormatter);
        tvRange.setText(rangeText);
    }

    private void setupSwiping() {
        RecyclerView rv = findViewById(R.id.rvSchedule);
        
        GestureDetector gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (Math.abs(velocityX) > Math.abs(velocityY)) {
                    if (velocityX < -500) { // Swipe Left -> Next Week
                        nextWeek();
                        return true;
                    } else if (velocityX > 500) { // Swipe Right -> Prev Week
                        prevWeek();
                        return true;
                    }
                }
                return false;
            }
        });

        rv.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                gestureDetector.onTouchEvent(e);
                return false;
            }
            @Override
            public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {}
            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {}
        });
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
        myIncomingRequests.clear();
        for (SwapRequest r : SwapRequest.allRequests) {
            if (r.receiverName.equalsIgnoreCase(userEmail) && "PENDING".equals(r.status)) {
                myIncomingRequests.add(r);
            }
        }

        LocalDate currentSunday = currentMonday.plusDays(6);
        List<Shift> filteredShifts = MockDatabase.getInstance().getShiftsFor(userEmail, currentMonday, currentSunday);
        
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
        if (shiftAdapter != null) {
            shiftAdapter.setShifts(filteredShifts);
        }
    }
}
