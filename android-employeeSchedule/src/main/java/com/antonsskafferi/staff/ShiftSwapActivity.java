package com.antonsskafferi.staff;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.antonsskafferi.staff.network.EmployeeDto;
import com.antonsskafferi.staff.network.RetrofitClient;
import com.antonsskafferi.staff.network.ShiftDto;
import com.antonsskafferi.staff.network.SwapRequestDto;
import com.antonsskafferi.staff.network.SwapStatus;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Activity for sending a shift swap request to a coworker.
 */
public class ShiftSwapActivity extends AppCompatActivity {

    private Integer myId;                     // Current user's ID
    private CoworkerAdapter adapter;          // Adapter for coworkers list
    private final List<EmployeeDto> allEmployees = new ArrayList<>();
    private String shiftStartStr;
    private String shiftEndStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shift_swap);

        // --- Back button ---
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // --- Get shift start/end from intent ---
        shiftStartStr = getIntent().getStringExtra("shiftStart");
        shiftEndStr = getIntent().getStringExtra("shiftEnd");

        if (shiftStartStr == null || shiftEndStr == null) { finish(); return; }

        // --- Display shift details as "Day HH:mm - HH:mm" ---
        TextView tvDetails = findViewById(R.id.tvSwapShiftDetails);
        try {
            LocalDateTime start = LocalDateTime.parse(shiftStartStr);
            LocalDateTime end = LocalDateTime.parse(shiftEndStr);

            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEE d MMM", new Locale("sv", "SE"));
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

            String displayText = start.format(dateFormatter) + " " +
                    start.format(timeFormatter) + " - " +
                    end.format(timeFormatter);

            tvDetails.setText(displayText);

        } catch (Exception e) {
            tvDetails.setText("Okänt pass"); // fallback if parsing fails
        }

        // --- Load current user ID from SharedPreferences ---
        SharedPreferences prefs = getSharedPreferences("StaffPrefs", MODE_PRIVATE);
        myId = prefs.getInt("loggedInId", -1);

        if (myId == -1) {
            Toast.makeText(this, "Användare saknas", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // --- Setup RecyclerView for coworkers ---
        RecyclerView rv = findViewById(R.id.rvCoworkers);
        rv.setLayoutManager(new LinearLayoutManager(this));

        adapter = new CoworkerAdapter(allEmployees, this::showConfirmDialog);
        rv.setAdapter(adapter);

        // --- Load data from API ---
        loadData();
    }

    private void loadData() {
        // Load both employees and all shifts to check for conflicts
        RetrofitClient.getApiService().getAllShifts().enqueue(new Callback<List<ShiftDto>>() {
            @Override
            public void onResponse(@NotNull Call<List<ShiftDto>> call, @NotNull Response<List<ShiftDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Set<Integer> busyEmployeeIds = findBusyEmployees(response.body());
                    loadEmployees(busyEmployeeIds);
                } else {
                    Toast.makeText(ShiftSwapActivity.this, "Kunde inte ladda pass", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NotNull Call<List<ShiftDto>> call, @NotNull Throwable t) {
                Toast.makeText(ShiftSwapActivity.this, "Nätverksfel vid laddning av pass", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Set<Integer> findBusyEmployees(List<ShiftDto> allShifts) {
        Set<Integer> busyIds = new HashSet<>();
        try {
            LocalDateTime targetStart = LocalDateTime.parse(shiftStartStr);
            LocalDateTime targetEnd = LocalDateTime.parse(shiftEndStr);

            for (ShiftDto shift : allShifts) {
                LocalDateTime s = LocalDateTime.parse(shift.startTime);
                LocalDateTime e = LocalDateTime.parse(shift.endTime);

                // Check for overlap: (StartA < EndB) and (EndA > StartB)
                if (targetStart.isBefore(e) && targetEnd.isAfter(s)) {
                    busyIds.add(shift.employeeId);
                }
            }
        } catch (Exception e) {
            // If parsing fails, we might not be able to filter correctly
        }
        return busyIds;
    }

    /**
     * Loads all coworkers except the current user and those with conflicting shifts.
     */
    private void loadEmployees(Set<Integer> busyEmployeeIds) {
        RetrofitClient.getApiService().getAllEmployees().enqueue(new retrofit2.Callback<>() {
            @Override
            public void onResponse(@NotNull Call<List<EmployeeDto>> call, @NotNull Response<List<EmployeeDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allEmployees.clear();
                    for (EmployeeDto e : response.body()) {
                        if (!e.employeeId.equals(myId) && !busyEmployeeIds.contains(e.employeeId)) {
                            allEmployees.add(e);
                        }
                    }
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(@NotNull Call<List<EmployeeDto>> call, @NotNull Throwable t) {
                Toast.makeText(ShiftSwapActivity.this, "Kunde inte ladda kollegor", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Shows a confirmation dialog before sending a swap request.
     */
    private void showConfirmDialog(EmployeeDto employee) {
        new AlertDialog.Builder(ShiftSwapActivity.this)
                .setTitle("Skicka förfrågan")
                .setMessage("Vill du skicka en bytesförfrågan till " +
                        employee.firstName + " " + employee.lastName + "?")
                .setPositiveButton("Ja", (dialog, which) -> sendSwapRequest(employee.employeeId))
                .setNegativeButton("Avbryt", null)
                .show();
    }

    /**
     * Sends a shift swap request to the selected coworker via the API.
     */
    private void sendSwapRequest(Integer receiverId) {
        int shiftId = getIntent().getIntExtra("selectedShiftId", -1);

        SwapRequestDto dto = new SwapRequestDto(null, myId, receiverId, shiftId, SwapStatus.PENDING);

        RetrofitClient.getApiService().createSwapRequest(dto).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NotNull Call<SwapRequestDto> call, @NotNull Response<SwapRequestDto> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ShiftSwapActivity.this, "Förfrågan skickad!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(ShiftSwapActivity.this, "Kunde inte skicka förfrågan", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NotNull Call<SwapRequestDto> call, @NotNull Throwable t) {
                Toast.makeText(ShiftSwapActivity.this, "Nätverksfel", Toast.LENGTH_SHORT).show();
            }
        });
    }
}