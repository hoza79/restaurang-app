package com.antonsskafferi.staff;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class ShiftSwapActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shift_swap);

        Shift shiftToSwap = (Shift) getIntent().getSerializableExtra("selectedShift");
        TextView tvDetails = findViewById(R.id.tvSwapShiftDetails);
        if (shiftToSwap != null) {
            tvDetails.setText("Pass: " + shiftToSwap.date + "\n" +
                             "Tid: " + shiftToSwap.time + "\n" +
                             "Roll: " + shiftToSwap.role);
        }

        SharedPreferences prefs = getSharedPreferences("StaffPrefs", MODE_PRIVATE);
        String myEmail = prefs.getString("loggedInUser", "anna.berg@antons.se");

        RecyclerView rv = findViewById(R.id.rvCoworkers);
        rv.setLayoutManager(new LinearLayoutManager(this));

        // DEMONSTRATOR: Endast de två kontona
        List<String> coworkers = Arrays.asList("anna.berg@antons.se", "erik.sten@antons.se");
        
        rv.setAdapter(new CoworkerAdapter(coworkers, new CoworkerAdapter.OnCoworkerClickListener() {
            @Override
            public void onClick(String email) {
                if (email.equalsIgnoreCase(myEmail)) {
                    Toast.makeText(ShiftSwapActivity.this, "Du kan inte byta med dig själv!", Toast.LENGTH_SHORT).show();
                    return;
                }

                new AlertDialog.Builder(ShiftSwapActivity.this)
                    .setTitle("Skicka förfrågan")
                    .setMessage("Vill du skicka en bytesförfrågan till " + email + "?")
                    .setPositiveButton("Ja", (dialog, which) -> {
                        SwapRequest req = new SwapRequest(
                            UUID.randomUUID().toString(),
                            myEmail,
                            email,
                            shiftToSwap
                        );
                        SwapRequest.allRequests.add(req);
                        
                        Toast.makeText(ShiftSwapActivity.this, "Förfrågan skickad till " + email, Toast.LENGTH_LONG).show();
                        finish();
                    })
                    .setNegativeButton("Avbryt", null)
                    .show();
            }
        }));
    }
}
