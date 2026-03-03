package com.antonsskafferi.android_ordertablet;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.*;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.antonsskafferi.android_ordertablet.net.ApiClient;
import com.antonsskafferi.android_ordertablet.net.KitchenBatchDto;
import com.antonsskafferi.android_ordertablet.net.KitchenItemDto;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.Date;
import java.util.TimeZone;

public class KitchenActivity extends AppCompatActivity {

    private static final String TAG = "KitchenActivity";

    private KitchenOrderAdapter adapter;
    private List<KitchenOrder> activeOrders = new ArrayList<>();
    private TextView tvClock, tvCount, tvEmpty;
    private RecyclerView rv;
    private final Handler clockHandler = new Handler();
    private final Handler pollHandler  = new Handler();

    // Minimal guard to avoid spamming PUT calls on repeated taps
    private boolean completing = false;

    @Override
    protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_kitchen);

        findViewById(R.id.btnKitchenBack).setOnClickListener(v -> finish());

        tvClock = findViewById(R.id.tvClock);
        tvCount = findViewById(R.id.tvOrderCount);
        tvEmpty = findViewById(R.id.tvEmpty);
        rv      = findViewById(R.id.rvKitchenOrders);
        rv.setLayoutManager(new LinearLayoutManager(this));

        adapter = new KitchenOrderAdapter(activeOrders, (pos, fullyDone) -> {
            // Intentionally unused: completion is handled by "tap anywhere"
        });
        rv.setAdapter(adapter);

        // Tryck var som helst på skärmen = ta bort (markera klar) FÖRSTA batchen
        rv.addOnItemTouchListener(new RecyclerView.SimpleOnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull android.view.MotionEvent e) {
                if (e.getAction() == android.view.MotionEvent.ACTION_UP && !activeOrders.isEmpty()) {
                    if (!completing) completeFirstBatch();
                    return true; // always consume
                }
                return false;
            }
        });

        startClock();
        startPolling();
        loadOrders();
    }

    /** Backend: mark a batch as complete (SERVED) and reload list */
    private void completeFirstBatch() {
        if (activeOrders.isEmpty()) return;
        completeBatch(activeOrders.get(0).orderId); // orderId holds batchId
    }

    private void completeBatch(int batchId) {
        if (completing) return;
        completing = true;

        ApiClient.api().completeKitchenBatch(batchId).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> resp) {
                completing = false;
                if (resp.isSuccessful()) {
                    loadOrders();
                } else {
                    Toast.makeText(KitchenActivity.this,
                            "Kunde inte markera klar (" + resp.code() + ")",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                completing = false;
                Toast.makeText(KitchenActivity.this,
                        "Ingen kontakt med servern", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Hämtar ordrar från backend (/kitchen/batches) och sorterar på aktiv kurs:
     * slot 0 (Dryck/Bar) → 1 (Förrätt) → 2 (Varmrätt) → 3 (Efterrätt).
     * Inom samma slot hamnar äldst överst (createdAt ASC).
     */
    private void loadOrders() {
        ApiClient.api().getKitchenBatches().enqueue(new Callback<List<KitchenBatchDto>>() {
            @Override
            public void onResponse(Call<List<KitchenBatchDto>> call, Response<List<KitchenBatchDto>> resp) {
                if (!resp.isSuccessful() || resp.body() == null) {
                    Log.e(TAG, "Kitchen batches failed: HTTP " + resp.code());
                    updateUI();
                    return;
                }

                List<KitchenOrder> fresh = new ArrayList<>();

                for (KitchenBatchDto b : resp.body()) {
                    if (b == null || b.batchId == null || b.tableNumber == null) continue;

                    // Filter out bar batches from kitchen view
                    if (b.batchType != null && b.batchType.trim().equalsIgnoreCase("DRINK")) {
                        continue;
                    }

                    // Reuse KitchenOrder model: orderId stores batchId
                    KitchenOrder o = new KitchenOrder(b.batchId, b.tableNumber);

                    List<String> dishes = new ArrayList<>();
                    if (b.items != null) {
                        for (KitchenItemDto it : b.items) {
                            if (it == null || it.name == null) continue;

                            StringBuilder sb = new StringBuilder(it.name);
                            if (it.quantity != null && it.quantity > 1) sb.append(" x").append(it.quantity);
                            if (it.notes != null && !it.notes.trim().isEmpty())
                                sb.append("\n   💬 ").append(it.notes.trim());
                            dishes.add(sb.toString());
                        }
                    }

                    int slot = mapBatchTypeToSlot(b.batchType);

                    // Minimal: use 'createdAt' from database for sorting
                    long createdAtMs = parseCreatedAtMillis(b.createdAt);
                    o.addCourse(new KitchenOrder.Course(slot, dishes, createdAtMs));
                    fresh.add(o);
                }

                completing = false;

                fresh.sort((a, b) -> {
                    KitchenOrder.Course ca = a.currentCourse();
                    KitchenOrder.Course cb = b.currentCourse();
                    int slotA = ca != null ? ca.slot : 99;
                    int slotB = cb != null ? cb.slot : 99;
                    if (slotA != slotB) return Integer.compare(slotA, slotB);
                    long tA = ca != null ? ca.createdAt : Long.MAX_VALUE;
                    long tB = cb != null ? cb.createdAt : Long.MAX_VALUE;
                    return Long.compare(tA, tB);
                });

                activeOrders.clear();
                activeOrders.addAll(fresh);
                adapter.notifyDataSetChanged();
                updateUI();
            }

            @Override
            public void onFailure(Call<List<KitchenBatchDto>> call, Throwable t) {
                Log.e(TAG, "Kitchen batches fetch failed", t);
                updateUI();
            }
        });
    }

    private long parseCreatedAtMillis(String createdAt) {
        if (createdAt == null) return System.currentTimeMillis();
        String s = createdAt.trim();
        if (s.isEmpty()) return System.currentTimeMillis();

        // Backend may send fractional seconds: 2026-03-03T12:34:56.123456
        boolean hasFraction = s.contains(".");
        if (hasFraction) {
            String[] parts = s.split("\\.", 2);
            String base = parts[0];
            String frac = parts.length > 1 ? parts[1] : "";

            // remove any timezone part if it exists (unlikely for LocalDateTime, but safe)
            frac = frac.replaceAll("[^0-9].*$", "");

            // keep only first 3 digits for SSS
            if (frac.length() > 3) frac = frac.substring(0, 3);
            while (frac.length() < 3) frac = frac + "0";

            s = base + "." + frac;
        }

        try {
            SimpleDateFormat fmt = new SimpleDateFormat(
                    hasFraction ? "yyyy-MM-dd'T'HH:mm:ss.SSS" : "yyyy-MM-dd'T'HH:mm:ss",
                    Locale.US
            );
            fmt.setLenient(false);
            fmt.setTimeZone(TimeZone.getTimeZone("Europe/Stockholm"));
            Date d = fmt.parse(s);
            return d != null ? d.getTime() : System.currentTimeMillis();
        } catch (Exception ignored) {
            return System.currentTimeMillis();
        }
    }

    private int mapBatchTypeToSlot(String batchType) {
        if (batchType == null) return 2;
        switch (batchType.trim().toUpperCase(Locale.ROOT)) {
            case "DRINK":       return 0;
            case "APPETIZER":   return 1;
            case "MAIN_COURSE": return 2;
            case "DESSERT":     return 3;
            default:            return 2;
        }
    }

    private void updateUI() {
        tvCount.setText(activeOrders.size() + " aktiva bord");
        tvEmpty.setVisibility(activeOrders.isEmpty() ? View.VISIBLE : View.GONE);
        rv.setVisibility(activeOrders.isEmpty() ? View.GONE : View.VISIBLE);
    }

    private void startClock() {
        clockHandler.post(new Runnable() {
            public void run() {
                tvClock.setText(new SimpleDateFormat("HH:mm",
                        Locale.getDefault()).format(new java.util.Date()));
                adapter.notifyDataSetChanged(); // uppdatera tidsbadges
                clockHandler.postDelayed(this, 60_000);
            }
        });
    }

    /** Pollar var 5:e sekund. */
    private void startPolling() {
        pollHandler.postDelayed(new Runnable() {
            public void run() {
                loadOrders();
                pollHandler.postDelayed(this, 5_000);
            }
        }, 5_000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadOrders();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clockHandler.removeCallbacksAndMessages(null);
        pollHandler.removeCallbacksAndMessages(null);
    }
}