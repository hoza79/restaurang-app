package com.antonsskafferi.android_ordertablet;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import android.util.Log;
import com.antonsskafferi.android_ordertablet.net.ApiClient;
import com.antonsskafferi.android_ordertablet.net.KitchenBatchDto;
import com.antonsskafferi.android_ordertablet.net.KitchenItemDto;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class KitchenActivity extends AppCompatActivity {

    private static final String TAG = "KitchenActivity";
    private KitchenOrderAdapter adapter;
    private List<KitchenOrder> activeOrders = new ArrayList<>();
    private TextView tvClock, tvCount, tvEmpty;
    private RecyclerView rv;
    private final Handler clockHandler = new Handler();
    private final Handler pollHandler  = new Handler();

    @Override
    protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_kitchen);

        // ← BACK-knapp
        findViewById(R.id.btnKitchenBack).setOnClickListener(v -> finish());

        tvClock = findViewById(R.id.tvClock);
        tvCount = findViewById(R.id.tvOrderCount);
        tvEmpty = findViewById(R.id.tvEmpty);
        rv      = findViewById(R.id.rvKitchenOrders);
        rv.setLayoutManager(new LinearLayoutManager(this));

        adapter = new KitchenOrderAdapter(activeOrders, (pos, fullyDone) -> {
            if (pos < 0 || pos >= activeOrders.size()) return;

            KitchenOrder order = activeOrders.get(pos);
            int batchId = order.orderId; // we stored batchId here

            ApiClient.api().completeKitchenBatch(batchId).enqueue(new Callback<Map<String, Object>>() {
                @Override
                public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> resp) {
                    if (resp.isSuccessful()) {
                        loadOrders();
                    } else {
                        Toast.makeText(KitchenActivity.this,
                                "Kunde inte markera klar (" + resp.code() + ")", Toast.LENGTH_SHORT).show();
                        adapter.notifyItemChanged(pos);
                    }
                }

                @Override
                public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                    Toast.makeText(KitchenActivity.this,
                            "Ingen kontakt med servern", Toast.LENGTH_SHORT).show();
                    adapter.notifyItemChanged(pos);
                }
            });
        });
        rv.setAdapter(adapter);

        startClock();
        startPolling();
        loadOrders();
    }

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

                    // ✅ filter out bar batches
                    if (b.batchType != null && b.batchType.trim().equalsIgnoreCase("DRINK")) {
                        continue;
                    }

                    KitchenOrder o = new KitchenOrder(b.batchId, b.tableNumber);

                    // Convert items -> dish strings your adapter expects
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

                    // If you later parse createdAt, set createdAt properly; for now System.currentTimeMillis() is ok
                    o.addCourse(new KitchenOrder.Course(slot, dishes, System.currentTimeMillis()));

                    fresh.add(o);
                }

                // sort: slot order then oldest first
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

    private int mapBatchTypeToSlot(String batchType) {
        if (batchType == null) return 2;
        switch (batchType.trim().toUpperCase(java.util.Locale.ROOT)) {
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

    /** Pollar var 5:e sekund (ersätts med WebSocket när backend är klar). */
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
        loadOrders(); // ladda om direkt när vi kommer hit
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clockHandler.removeCallbacksAndMessages(null);
        pollHandler.removeCallbacksAndMessages(null);
    }
}