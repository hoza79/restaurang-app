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

        EditText etEmail = findViewById(R.id.etEmployeeEmail);
        Button btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            if (email.isEmpty()) {
                Toast.makeText(this, "Vänligen fyll i din e-postadress", Toast.LENGTH_SHORT).show();
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Vänligen ange en giltig e-postadress", Toast.LENGTH_SHORT).show();
            } else {
                // Spara e-post lokalt för att veta vem som är inloggad
                SharedPreferences prefs = getSharedPreferences("StaffPrefs", MODE_PRIVATE);
                prefs.edit().putString("loggedInUser", email).apply();

                Intent intent = new Intent(this, ScheduleActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
