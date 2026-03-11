package com.antonsskafferi.staff;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.antonsskafferi.staff.network.EmployeeDto;
import com.antonsskafferi.staff.network.RetrofitClient;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Handles employee login via QR code.
 * Supports auto-login if user session exists in SharedPreferences.
 */
public class LoginActivity extends AppCompatActivity {

    private final ActivityResultLauncher<ScanOptions> qrCodeLauncher = registerForActivityResult(new ScanContract(), result -> {
        if (result.getContents() != null) {
            String scannedEmail = result.getContents();
            SharedPreferences prefs = getSharedPreferences("StaffPrefs", MODE_PRIVATE);
            performLogin(scannedEmail, prefs);
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // --- Auto-login if user is already logged in ---
        SharedPreferences prefs = getSharedPreferences("StaffPrefs", MODE_PRIVATE);
        if (prefs.contains("loggedInId")) {
            navigateToSchedule();
            return;
        }

        setContentView(R.layout.activity_login);

        Button btnScanQr = findViewById(R.id.btnScanQr);

        // QR Scan button click handler
        btnScanQr.setOnClickListener(v -> {
            ScanOptions options = new ScanOptions();
            options.setPrompt("Skanna din inloggnings-QR");
            options.setBeepEnabled(true);
            options.setOrientationLocked(true);
            options.setCaptureActivity(CaptureActivityPortrait.class);
            qrCodeLauncher.launch(options);
        });
    }

    /**
     * Performs login via API and saves session data in SharedPreferences
     */
    private void performLogin(String email, SharedPreferences prefs) {
        RetrofitClient.getApiService().login(email).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<EmployeeDto> call, @NonNull Response<EmployeeDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    saveUserSession(response.body(), prefs);
                    navigateToSchedule();
                } else {
                    showToast("Inloggning misslyckades: Ogiltig QR-kod");
                }
            }

            @Override
            public void onFailure(@NonNull Call<EmployeeDto> call, @NonNull Throwable t) {
                showToast("Nätverksfel: " + t.getMessage());
            }
        });
    }

    /**
     * Save logged-in user info in SharedPreferences
     */
    private void saveUserSession(EmployeeDto emp, SharedPreferences prefs) {
        prefs.edit()
                .putInt("loggedInId", emp.employeeId)
                .putString("loggedInEmail", emp.emailAddress)
                .putString("loggedInName", emp.firstName + " " + emp.lastName)
                .apply();
    }

    /** Navigate to ScheduleActivity and finish LoginActivity */
    private void navigateToSchedule() {
        startActivity(new Intent(this, ScheduleActivity.class));
        finish();
    }

    /** Helper to show short Toast messages */
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}