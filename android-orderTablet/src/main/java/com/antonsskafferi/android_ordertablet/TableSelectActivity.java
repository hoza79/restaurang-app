package com.antonsskafferi.android_ordertablet;

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
import com.antonsskafferi.android_ordertablet.net.ApiClient;
import com.antonsskafferi.android_ordertablet.net.DiningTableDto;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.util.Log;

public class TableSelectActivity extends AppCompatActivity {
    private final List<DiningTableDto> tables = new ArrayList<>();
    private boolean loadingTables = false;

    private static final String TAG = "TableSelectActivity";
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
        refreshTables(); // fetch + then buildGrid()
    }

    private void refreshTables() {
        if (loadingTables) return;
        loadingTables = true;

        ApiClient.api().getTables().enqueue(new Callback<List<DiningTableDto>>() {
            @Override
            public void onResponse(Call<List<DiningTableDto>> call, Response<List<DiningTableDto>> resp) {
                loadingTables = false;

                if (!resp.isSuccessful() || resp.body() == null) {
                    Toast.makeText(TableSelectActivity.this,
                            "Kunde inte hämta bord (" + resp.code() + ")", Toast.LENGTH_SHORT).show();
                    buildGrid(); // builds using whatever is currently in `tables`
                    return;
                }

                tables.clear();
                tables.addAll(resp.body());
                Log.d(TAG, "Fetched tables: " + tables.size());
                for (DiningTableDto t : tables) {
                    Log.d(TAG, "Table " + t.tableNumber + " id=" + t.tableId + " status=" + t.tableStatus);
                }

                // Sort by tableNumber (so grid is stable)
                tables.sort((a, b) -> {
                    int an = a.tableNumber != null ? a.tableNumber : 0;
                    int bn = b.tableNumber != null ? b.tableNumber : 0;
                    return Integer.compare(an, bn);
                });

                buildGrid();
            }

            @Override
            public void onFailure(Call<List<DiningTableDto>> call, Throwable t) {
                loadingTables = false;
                Log.e(TAG, "Failed to fetch tables", t);
                Toast.makeText(TableSelectActivity.this,
                        "Ingen kontakt med servern", Toast.LENGTH_SHORT).show();
                buildGrid();
            }
        });
    }

    private void buildGrid() {
        GridLayout grid = findViewById(R.id.tableGrid);
        grid.removeAllViews();

        if (tables.isEmpty()) {
            // Fallback: show 10 “unknown status” tables if API failed
            for (int i = 0; i < 10; i++) {
                int num = i + 1;
                addTableCard(grid, num, null, "UNKNOWN");
            }
        } else {
            for (DiningTableDto t : tables) {
                if (t.tableNumber == null) continue;
                addTableCard(grid, t.tableNumber, t.tableId, t.tableStatus);
            }
        }
    }

    private void addTableCard(GridLayout grid, int tableNumber, Integer tableId, String tableStatus) {

        boolean hasNota = Cart.hasOpenSession(tableNumber);

        boolean available = tableStatus != null && tableStatus.equalsIgnoreCase("AVAILABLE");
        boolean occ = !available; // treat anything non-AVAILABLE as occupied for now

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setGravity(Gravity.CENTER);

        int bgColor = hasNota
                ? Color.parseColor("#3D2E00")   // active note
                : occ
                ? Color.parseColor("#2A1A0E")   // occupied-ish
                : Color.parseColor("#252525");  // free

        card.setBackgroundColor(bgColor);

        GridLayout.LayoutParams p = new GridLayout.LayoutParams();
        p.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        p.rowSpec    = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        p.setMargins(8, 8, 8, 8);
        p.width = 0;
        p.height = dp(100);
        card.setLayoutParams(p);

        TextView tvNum = new TextView(this);
        tvNum.setText(String.valueOf(tableNumber));
        tvNum.setTextSize(26);
        tvNum.setTypeface(null, Typeface.BOLD);
        tvNum.setTextColor(Color.parseColor("#EEEEEE"));

        TextView tvSt = new TextView(this);
        tvSt.setTextSize(11);

        if (hasNota) {
            tvSt.setText("Öppen nota");
            tvSt.setTextColor(Color.parseColor("#C9A961"));
        } else if (!available) {
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
            Cart.CartSession s = Cart.openTable(tableNumber, tableId);

            if (s.orderId != null) {
                startActivity(new Intent(this, OrderActivity.class));
                return;
            }

            final int EMPLOYEE_ID = 1; // fixed waiter

            ApiClient.api().createOrder(new com.antonsskafferi.android_ordertablet.net.CreateOrderRequest(EMPLOYEE_ID, tableId))
                    .enqueue(new retrofit2.Callback<java.util.Map<String, Object>>() {
                        @Override
                        public void onResponse(retrofit2.Call<java.util.Map<String, Object>> call,
                                               retrofit2.Response<java.util.Map<String, Object>> resp) {
                            if (!resp.isSuccessful() || resp.body() == null) {
                                Toast.makeText(TableSelectActivity.this,
                                        "Kunde inte skapa order (" + resp.code() + ")", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            Object idObj = resp.body().get("orderId");
                            if (idObj instanceof Number) {
                                s.orderId = ((Number) idObj).intValue();
                            }
                            startActivity(new Intent(TableSelectActivity.this, OrderActivity.class));
                        }

                        @Override
                        public void onFailure(retrofit2.Call<java.util.Map<String, Object>> call, Throwable t) {
                            Toast.makeText(TableSelectActivity.this,
                                    "Ingen kontakt med servern (order)", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        grid.addView(card);
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