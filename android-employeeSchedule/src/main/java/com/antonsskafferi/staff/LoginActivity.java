package com.antonsskafferi.staff;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText etName = findViewById(R.id.etEmployeeName);
        Button btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            if (name.isEmpty()) {
                Toast.makeText(this, "Vänligen fyll i ditt namn", Toast.LENGTH_SHORT).show();
            } else {
                // Spara namnet lokalt för att veta vem som är inloggad
                SharedPreferences prefs = getSharedPreferences("StaffPrefs", MODE_PRIVATE);
                prefs.edit().putString("loggedInUser", name).apply();

                Intent intent = new Intent(this, ScheduleActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
