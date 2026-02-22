package com.antonsskafferi.pos;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.*;

public class TableSelectActivity extends AppCompatActivity {

    // I verkligt läge: hämta från backend
    private final boolean[] occupied = {false,true,true,false,false,true,false,false,true,false};
    private final Handler clockHandler = new Handler();
    private TextView tvTime;

    @Override
    protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_table_select);
        tvTime = findViewById(R.id.tvCurrentTime);
        startClock();

        findViewById(R.id.btnKitchenView).setOnClickListener(v ->
                startActivity(new Intent(this, KitchenActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        buildGrid(); // rita om varje gång vi kommer tillbaka – uppdaterar öppna notor
    }

    private void buildGrid() {
        GridLayout grid = findViewById(R.id.tableGrid);
        grid.removeAllViews();

        for (int i = 0; i < 10; i++) {
            final int num = i + 1;
            boolean occ      = occupied[i];
            boolean hasNota  = Cart.hasOpenSession(num);

            LinearLayout card = new LinearLayout(this);
            card.setOrientation(LinearLayout.VERTICAL);
            card.setGravity(Gravity.CENTER);

            // Färg: mörkgrå = ledig | mörk orange = upptaget | guld = öppen nota
            int bgColor = hasNota
                    ? Color.parseColor("#3D2E00")   // guld-ton = aktiv nota
                    : occ
                    ? Color.parseColor("#2A1A0E") // mörk orange = upptaget
                    : Color.parseColor("#252525"); // mörk grå = ledig

            card.setBackgroundColor(bgColor);

            GridLayout.LayoutParams p = new GridLayout.LayoutParams();
            p.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            p.rowSpec    = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            p.setMargins(8, 8, 8, 8);
            p.width = 0;
            p.height = dp(100);
            card.setLayoutParams(p);

            // Bordsnummer
            TextView tvNum = new TextView(this);
            tvNum.setText(String.valueOf(num));
            tvNum.setTextSize(26);
            tvNum.setTypeface(null, Typeface.BOLD);
            tvNum.setTextColor(Color.parseColor("#EEEEEE"));

            // Status-text
            TextView tvSt = new TextView(this);
            tvSt.setTextSize(11);
            if (hasNota) {
                tvSt.setText("Öppen nota");
                tvSt.setTextColor(Color.parseColor("#C9A961"));
            } else if (occ) {
                tvSt.setText("Upptaget");
                tvSt.setTextColor(Color.parseColor("#FF6B6B"));
            } else {
                tvSt.setText("Ledig");
                tvSt.setTextColor(Color.parseColor("#4ECDC4"));
            }

            card.addView(tvNum);
            card.addView(tvSt);
            card.setClickable(true);
            card.setFocusable(true);
            card.setOnClickListener(v -> {
                Cart.openTable(num); // öppnar eller återhämtar befintlig session
                startActivity(new Intent(this, OrderActivity.class));
            });

            grid.addView(card);
        }
    }

    private void startClock() {
        clockHandler.post(new Runnable() {
            public void run() {
                tvTime.setText(new SimpleDateFormat("HH:mm",
                        Locale.getDefault()).format(new Date()));
                clockHandler.postDelayed(this, 30_000);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clockHandler.removeCallbacksAndMessages(null);
    }

    private int dp(int v) {
        return Math.round(v * getResources().getDisplayMetrics().density);
    }
}