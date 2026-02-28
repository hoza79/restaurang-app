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
        String myName = prefs.getString("loggedInUser", "Anställd");

        RecyclerView rv = findViewById(R.id.rvCoworkers);
        rv.setLayoutManager(new LinearLayoutManager(this));

        List<String> coworkers = Arrays.asList("Anna", "Erik", "Linda", "Olof", "Kalle");
        
        rv.setAdapter(new CoworkerAdapter(coworkers, name -> {
            // --- KRAV: MAN KAN INTE BYTA MED SIG SJÄLV ---
            if (name.equalsIgnoreCase(myName)) {
                Toast.makeText(this, "Du kan inte byta med dig själv!", Toast.LENGTH_SHORT).show();
                return;
            }

            new AlertDialog.Builder(this)
                .setTitle("Skicka förfrågan")
                .setMessage("Vill du skicka en bytesförfrågan till " + name + "?")
                .setPositiveButton("Ja", (dialog, which) -> {
                    // --- KRAV: SKAPA SPECIFIK FÖRFRÅGAN ---
                    SwapRequest req = new SwapRequest(
                        UUID.randomUUID().toString(),
                        myName,
                        name,
                        shiftToSwap
                    );
                    SwapRequest.allRequests.add(req);

                    Toast.makeText(this, "Förfrågan skickad till " + name, Toast.LENGTH_LONG).show();
                    finish();
                })
                .setNegativeButton("Avbryt", null)
                .show();
        }));
    }
}
