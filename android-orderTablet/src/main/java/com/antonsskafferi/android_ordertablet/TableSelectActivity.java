package com.antonsskafferi.android_ordertablet;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.antonsskafferi.android_ordertablet.net.ApiClient;
import com.antonsskafferi.android_ordertablet.net.BookingDto;
import com.antonsskafferi.android_ordertablet.net.CreateOrderRequest;
import com.antonsskafferi.android_ordertablet.net.DiningTableDto;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.text.SimpleDateFormat;
import java.util.*;

public class TableSelectActivity extends AppCompatActivity {

    private final List<DiningTableDto> tables = new ArrayList<>();

    private boolean pendingTables   = false;
    private boolean pendingBookings = false;

    private final List<BookingDto> todayBookings = new ArrayList<>();

    private final Handler clockHandler = new Handler();
    private TextView tvTime;
    private TextView tvBookingSummary; // ny rad i headern

    @Override
    protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_table_select);
        tvTime           = findViewById(R.id.tvCurrentTime);
        tvBookingSummary = findViewById(R.id.tvBookingSummary);
        startClock();
        findViewById(R.id.btnKitchenView).setOnClickListener(v ->
                startActivity(new Intent(this, KitchenActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchAll();
    }

    private void fetchAll() {
        pendingTables   = true;
        pendingBookings = true;
        fetchTables();
        fetchBookings();
    }

    private void fetchTables() {
        ApiClient.api().getTables().enqueue(new Callback<List<DiningTableDto>>() {
            @Override
            public void onResponse(Call<List<DiningTableDto>> c, Response<List<DiningTableDto>> r) {
                pendingTables = false;
                if (r.isSuccessful() && r.body() != null) {
                    tables.clear();
                    tables.addAll(r.body());
                    tables.sort((a, b) -> Integer.compare(
                            a.tableNumber != null ? a.tableNumber : 0,
                            b.tableNumber != null ? b.tableNumber : 0));
                }
                buildIfReady();
            }
            @Override
            public void onFailure(Call<List<DiningTableDto>> c, Throwable t) {
                pendingTables = false;
                buildIfReady();
            }
        });
    }

    private void fetchBookings() {
        ApiClient.api().getBookings().enqueue(new Callback<List<BookingDto>>() {
            @Override
            public void onResponse(Call<List<BookingDto>> c, Response<List<BookingDto>> r) {
                pendingBookings = false;
                todayBookings.clear();
                if (r.isSuccessful() && r.body() != null) {
                    String today = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());
                    for (BookingDto b : r.body()) {
                        if (b.date != null && b.date.startsWith(today))
                            todayBookings.add(b);
                    }
                }
                buildIfReady();
            }
            @Override
            public void onFailure(Call<List<BookingDto>> c, Throwable t) {
                pendingBookings = false;
                buildIfReady();
            }
        });
    }

    private void buildIfReady() {
        if (pendingTables || pendingBookings) return;

        // Uppdatera bokningssammanfattning i headern
        if (!todayBookings.isEmpty()) {
            StringBuilder sb = new StringBuilder("Antal bokningar idag: " + todayBookings.size() + "\n\n");
            for (int i = 0; i < todayBookings.size(); i++) {
                BookingDto b = todayBookings.get(i);
                String tid = b.date != null && b.date.length() >= 16
                        ? b.date.substring(11, 16) : "";
                sb.append(b.firstName).append(" ").append(b.lastName)
                        .append("  ·  ").append(b.guestCount).append(" personer")
                        .append("  ·  kl ").append(tid);
                if (i < todayBookings.size() - 1) sb.append("\n");
            }
            tvBookingSummary.setText(sb.toString());
            tvBookingSummary.setVisibility(android.view.View.VISIBLE);
        } else {
            tvBookingSummary.setVisibility(android.view.View.GONE);
        }

        buildGrid();
    }

    private void buildGrid() {
        GridLayout grid = findViewById(R.id.tableGrid);
        grid.removeAllViews();
        if (tables.isEmpty()) {
            for (int i = 1; i <= 10; i++) addCard(grid, i, null, "UNKNOWN");
        } else {
            for (DiningTableDto t : tables)
                if (t.tableNumber != null) addCard(grid, t.tableNumber, t.tableId, t.tableStatus);
        }
    }

    private void addCard(GridLayout grid, int tableNum, Integer tableId, String status) {
        boolean hasNota = Cart.hasOpenSession(tableNum);

        String st = status == null ? "UNKNOWN" : status.trim().toUpperCase(Locale.ROOT);
        boolean isOccupied = !st.equals("AVAILABLE") && !st.equals("UNKNOWN") && !st.equals("NULL") && !st.isEmpty();
        boolean isUnknown  = st.equals("UNKNOWN") || st.equals("NULL") || st.isEmpty();

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setGravity(Gravity.CENTER);
        card.setBackgroundColor(hasNota
                ? Color.parseColor("#3D2E00")
                : isOccupied ? Color.parseColor("#2A1A0E")
                : Color.parseColor("#252525"));

        GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
        lp.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        lp.rowSpec    = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        lp.setMargins(8, 8, 8, 8);
        lp.width  = 0;
        lp.height = dp(110);
        card.setLayoutParams(lp);

        TextView tvNum = new TextView(this);
        tvNum.setText(String.valueOf(tableNum));
        tvNum.setTextSize(26);
        tvNum.setTypeface(null, Typeface.BOLD);
        tvNum.setTextColor(Color.parseColor("#EEEEEE"));
        tvNum.setGravity(Gravity.CENTER);
        tvNum.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        TextView tvSt = new TextView(this);
        tvSt.setTextSize(11);
        tvSt.setGravity(Gravity.CENTER);
        tvSt.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        if (hasNota) {
            tvSt.setText("Öppen nota");
            tvSt.setTextColor(Color.parseColor("#C9A961"));
        } else if (isOccupied) {
            tvSt.setText("Upptaget");
            tvSt.setTextColor(Color.parseColor("#FF6B6B"));
        } else if (isUnknown) {
            tvSt.setText("Okänd");
            tvSt.setTextColor(Color.parseColor("#888888"));
        } else {
            tvSt.setText("Ledig");
            tvSt.setTextColor(Color.parseColor("#4ECDC4"));
        }

        card.addView(tvNum);
        card.addView(tvSt);

        card.setClickable(true);
        card.setFocusable(true);
        card.setOnClickListener(v -> {
            if (tableId == null) {
                Toast.makeText(this, "Saknar tableId från backend", Toast.LENGTH_SHORT).show();
                return;
            }
            Cart.CartSession sess = Cart.openTable(tableNum, tableId);
            if (sess.orderId != null) {
                startActivity(new Intent(this, OrderActivity.class));
                return;
            }
            ApiClient.api().createOrder(new CreateOrderRequest(2, tableId))
                    .enqueue(new Callback<Map<String, Object>>() {
                        @Override
                        public void onResponse(Call<Map<String, Object>> c,
                                               Response<Map<String, Object>> r) {
                            if (!r.isSuccessful() || r.body() == null) {
                                Toast.makeText(TableSelectActivity.this,
                                        "Kunde inte skapa order (" + r.code() + ")",
                                        Toast.LENGTH_SHORT).show();
                                return;
                            }
                            Object id = r.body().get("orderId");
                            if (id instanceof Number) sess.orderId = ((Number) id).intValue();
                            startActivity(new Intent(TableSelectActivity.this, OrderActivity.class));
                        }
                        @Override
                        public void onFailure(Call<Map<String, Object>> c, Throwable t) {
                            Toast.makeText(TableSelectActivity.this,
                                    "Ingen kontakt med servern", Toast.LENGTH_SHORT).show();
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
