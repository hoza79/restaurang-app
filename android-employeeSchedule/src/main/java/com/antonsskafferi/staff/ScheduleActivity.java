package com.antonsskafferi.staff;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.antonsskafferi.staff.network.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScheduleActivity extends AppCompatActivity {

    private final List<SwapRequestDto> incomingRequests = new ArrayList<>();
    private final Map<Integer, ShiftDto> shiftMap = new HashMap<>();
    private final Map<Integer, EmployeeDto> employeeMap = new HashMap<>();
    private List<ShiftDto> allMyShifts = new ArrayList<>();
    private SwapRequestAdapter requestAdapter;
    private ShiftAdapter shiftAdapter;

    private Integer userId;
    private LocalDate currentMonday;
    private final DateTimeFormatter rangeFormatter = DateTimeFormatter.ofPattern("d MMM", new Locale("sv", "SE"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        SharedPreferences prefs = getSharedPreferences("StaffPrefs", MODE_PRIVATE);
        userId = prefs.getInt("loggedInId", -1);
        String userName = prefs.getString("loggedInName", "Anställd");

        if (userId == -1) { finish(); return; }

        TextView tvWelcome = findViewById(R.id.tvWelcome);
        String welcomeText = "Välkommen, " + userName;
        tvWelcome.setText(welcomeText);

        currentMonday = LocalDate.now().with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
        updateWeekUI();

        findViewById(R.id.btnPrevWeek).setOnClickListener(v -> { currentMonday = currentMonday.minusWeeks(1); updateWeekUI(); filterAndDisplayShifts(); });
        findViewById(R.id.btnNextWeek).setOnClickListener(v -> { currentMonday = currentMonday.plusWeeks(1); updateWeekUI(); filterAndDisplayShifts(); });
        
        findViewById(R.id.btnLogout).setOnClickListener(v -> {
            prefs.edit().clear().apply();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        fetchEmployees();
        setupRecyclerViews();
        setupSwipeNavigation();
    }

    private void updateWeekUI() {
        LocalDate sunday = currentMonday.plusDays(6);
        TextView tvRange = findViewById(R.id.tvWeekRange);
        String rangeText = currentMonday.format(rangeFormatter) + " - " + sunday.format(rangeFormatter);
        tvRange.setText(rangeText);
    }

    private void setupRecyclerViews() {
        RecyclerView rvRequests = findViewById(R.id.rvSwapRequests);
        rvRequests.setLayoutManager(new LinearLayoutManager(this));
        requestAdapter = new SwapRequestAdapter(incomingRequests, shiftMap, employeeMap, new SwapRequestAdapter.OnSwapRequestListener() {
            @Override public void onAccept(SwapRequestDto req) { respondToSwap(req, true); }
            @Override public void onDeny(SwapRequestDto req) { respondToSwap(req, false); }
        });
        rvRequests.setAdapter(requestAdapter);

        RecyclerView rvShifts = findViewById(R.id.rvSchedule);
        rvShifts.setLayoutManager(new LinearLayoutManager(this));
        shiftAdapter = new ShiftAdapter(new ArrayList<>(), new ShiftAdapter.OnShiftSwapListener() {
            @Override public void onSwapClick(ShiftDto shift) {
                Intent i = new Intent(ScheduleActivity.this, ShiftSwapActivity.class);
                i.putExtra("selectedShiftId", shift.shiftId);
                i.putExtra("shiftStart", shift.startTime);
                i.putExtra("shiftEnd", shift.endTime);
                startActivity(i);
            }
            @Override public void onRetractClick(SwapRequestDto req) {
                RetrofitClient.getApiService().deleteSwapRequest(req.swapId).enqueue(new Callback<>() {
                    @Override public void onResponse(@NonNull Call<Void> c, @NonNull Response<Void> r) { refreshData(); }
                    @Override public void onFailure(@NonNull Call<Void> c, @NonNull Throwable t) {}
                });
            }
        });
        rvShifts.setAdapter(shiftAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshData();
    }

    private void refreshData() {
        fetchAllShifts(); // Fetch all shifts first to populate shiftMap
        fetchIncomingSwaps();
        fetchOutgoingSwaps();
        fetchMyShifts();
    }

    private void fetchAllShifts() {
        RetrofitClient.getApiService().getAllShifts().enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<ShiftDto>> call, @NonNull Response<List<ShiftDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (ShiftDto s : response.body()) {
                        shiftMap.put(s.shiftId, s);
                    }
                    if (requestAdapter != null) requestAdapter.notifyDataSetChanged();
                }
            }
            @Override public void onFailure(@NonNull Call<List<ShiftDto>> call, @NonNull Throwable t) {}
        });
    }

    private void fetchIncomingSwaps() {
        RetrofitClient.getApiService().getIncomingSwaps(userId).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<SwapRequestDto>> call, @NonNull Response<List<SwapRequestDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    incomingRequests.clear();
                    for (SwapRequestDto req : response.body()) {
                        if (req.swapStatus == SwapStatus.PENDING) {
                            incomingRequests.add(req);
                        }
                    }
                    requestAdapter.setRequests(incomingRequests);
                    findViewById(R.id.tvRequestsHeader).setVisibility(incomingRequests.isEmpty() ? View.GONE : View.VISIBLE);
                    findViewById(R.id.rvSwapRequests).setVisibility(incomingRequests.isEmpty() ? View.GONE : View.VISIBLE);
                }
            }
            @Override public void onFailure(@NonNull Call<List<SwapRequestDto>> call, @NonNull Throwable t) {}
        });
    }

    private void fetchOutgoingSwaps() {
        RetrofitClient.getApiService().getOutgoingSwaps(userId).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<SwapRequestDto>> call, @NonNull Response<List<SwapRequestDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    shiftAdapter.setOutgoingRequests(response.body());
                }
            }
            @Override public void onFailure(@NonNull Call<List<SwapRequestDto>> call, @NonNull Throwable t) {}
        });
    }

    private void fetchMyShifts() {
        RetrofitClient.getApiService().getEmployeeShifts(userId).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<ShiftDto>> call, @NonNull Response<List<ShiftDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allMyShifts = response.body();
                    filterAndDisplayShifts();
                }
            }
            @Override public void onFailure(@NonNull Call<List<ShiftDto>> call, @NonNull Throwable t) {}
        });
    }

    private void fetchEmployees() {
        RetrofitClient.getApiService().getAllEmployees().enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<EmployeeDto>> call, @NonNull Response<List<EmployeeDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    employeeMap.clear();
                    for (EmployeeDto e : response.body()) {
                        employeeMap.put(e.employeeId, e);
                    }
                    if (requestAdapter != null) requestAdapter.notifyDataSetChanged();
                }
            }
            @Override public void onFailure(@NonNull Call<List<EmployeeDto>> call, @NonNull Throwable t) {}
        });
    }

    private void filterAndDisplayShifts() {
        List<ShiftDto> filtered = new ArrayList<>();
        LocalDate sunday = currentMonday.plusDays(6);
        for (ShiftDto s : allMyShifts) {
            try {
                LocalDate shiftDate = LocalDateTime.parse(s.startTime).toLocalDate();
                if (!shiftDate.isBefore(currentMonday) && !shiftDate.isAfter(sunday)) {
                    filtered.add(s);
                }
            } catch (Exception e) { filtered.add(s); }
        }
        filtered.sort((s1, s2) -> {
            try { return LocalDateTime.parse(s1.startTime).compareTo(LocalDateTime.parse(s2.startTime)); }
            catch (Exception e) { return 0; }
        });
        shiftAdapter.setShifts(filtered);
    }

    private void respondToSwap(SwapRequestDto request, boolean accept) {
        Call<SwapRequestDto> call = accept ? RetrofitClient.getApiService().acceptSwap(request.swapId) : RetrofitClient.getApiService().rejectSwap(request.swapId);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<SwapRequestDto> call, @NonNull Response<SwapRequestDto> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ScheduleActivity.this, accept ? "Byte accepterat!" : "Byte nekat.", Toast.LENGTH_SHORT).show();
                    refreshData();
                }
            }
            @Override public void onFailure(@NonNull Call<SwapRequestDto> call, @NonNull Throwable t) {}
        });
    }

    private void setupSwipeNavigation() {
        GestureDetector gd = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, @NonNull MotionEvent e2, float vX, float vY) {
                if (Math.abs(vX) > Math.abs(vY)) {
                    if (vX < -500) { currentMonday = currentMonday.plusWeeks(1); }
                    else if (vX > 500) { currentMonday = currentMonday.minusWeeks(1); }
                    else { return false; }
                    updateWeekUI(); filterAndDisplayShifts(); return true;
                }
                return false;
            }
        });
        RecyclerView rv = findViewById(R.id.rvSchedule);
        rv.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) { gd.onTouchEvent(e); return false; }
            @Override public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {}
            @Override public void onRequestDisallowInterceptTouchEvent(boolean b) {}
        });
    }
}
