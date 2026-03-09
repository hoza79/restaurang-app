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
import com.antonsskafferi.staff.network.SwapRequestDto;
import com.antonsskafferi.staff.network.SwapStatus;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shift_swap);

        // --- Back button ---
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // --- Get shift start/end from intent (instead of shift ID) ---
        String shiftStart = getIntent().getStringExtra("shiftStart");
        String shiftEnd = getIntent().getStringExtra("shiftEnd");

        if (shiftStart == null || shiftEnd == null) { finish(); return; }

        // --- Display shift details as "Day HH:mm - HH:mm" ---
        TextView tvDetails = findViewById(R.id.tvSwapShiftDetails);
        try {
            LocalDateTime start = LocalDateTime.parse(shiftStart);
            LocalDateTime end = LocalDateTime.parse(shiftEnd);

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

        // --- Setup RecyclerView for coworkers ---
        RecyclerView rv = findViewById(R.id.rvCoworkers);
        rv.setLayoutManager(new LinearLayoutManager(this));

        adapter = new CoworkerAdapter(allEmployees, this::showConfirmDialog);
        rv.setAdapter(adapter);

        // --- Load coworkers from API ---
        loadEmployees();
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
     * Loads all coworkers except the current user and updates the adapter.
     */
    private void loadEmployees() {
        RetrofitClient.getApiService().getAllEmployees().enqueue(new retrofit2.Callback<>() {
            @Override
            public void onResponse(@NotNull Call<List<EmployeeDto>> call, @NotNull Response<List<EmployeeDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allEmployees.clear();
                    for (EmployeeDto e : response.body()) {
                        if (!e.employeeId.equals(myId)) {
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
     * Sends a shift swap request to the selected coworker via the API.
     */
    private void sendSwapRequest(Integer receiverId) {
        // Now we need the shift ID too, still get from intent
        int shiftId = getIntent().getIntExtra("selectedShiftId", -1);

        SwapRequestDto dto = new SwapRequestDto(null, myId, receiverId, shiftId, SwapStatus.PENDING);

        RetrofitClient.getApiService().createSwapRequest(dto).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NotNull Call<SwapRequestDto> call, @NotNull Response<SwapRequestDto> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ShiftSwapActivity.this, "Förfrågan skickad!", Toast.LENGTH_SHORT).show();
                    finish(); // Close activity after sending
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