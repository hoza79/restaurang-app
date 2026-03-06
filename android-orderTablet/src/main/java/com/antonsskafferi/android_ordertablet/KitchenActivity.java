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
import java.util.LinkedHashMap;
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

    private boolean completing = false;

    /**
     * Lokal cache av avklarade kurser per bord (tableNumber → lista av klara kurser).
     * Rensas när bordet försvinner från backenden helt.
     */
    private final Map<Integer, List<KitchenOrder.Course>> doneCache = new LinkedHashMap<>();

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

        adapter = new KitchenOrderAdapter(activeOrders, (pos, fullyDone) -> { });
        rv.setAdapter(adapter);

        rv.addOnItemTouchListener(new RecyclerView.SimpleOnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv,
                                                 @NonNull android.view.MotionEvent e) {
                if (e.getAction() == android.view.MotionEvent.ACTION_UP
                        && !activeOrders.isEmpty()) {
                    if (!completing) completeFirstBatch();
                    return true;
                }
                return false;
            }
        });

        startClock();
        startPolling();
        loadOrders();
    }

    /**
     * Markerar den globalt högst prioriterade aktiva kursen som klar.
     * Det är alltid currentCourse() hos det första kortet i listan
     * (listan är sorterad på slot → createdAt).
     */
    private void completeFirstBatch() {
        if (activeOrders.isEmpty()) return;

        KitchenOrder first = activeOrders.get(0);
        KitchenOrder.Course cur = first.currentCourse();
        if (cur == null) return;

        // Markera lokalt direkt så UI uppdateras snabbt
        cur.doneAt = System.currentTimeMillis();
        doneCache.computeIfAbsent(first.tableNumber, k -> new ArrayList<>()).add(cur);
        adapter.notifyDataSetChanged();

        completeBatch(cur.batchId); // ← använder kursens eget batchId
    }

    private void completeBatch(int batchId) {
        if (completing) return;
        completing = true;

        ApiClient.api().completeKitchenBatch(batchId).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call,
                                   Response<Map<String, Object>> resp) {
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

    private void loadOrders() {
        ApiClient.api().getKitchenBatches().enqueue(new Callback<List<KitchenBatchDto>>() {
            @Override
            public void onResponse(Call<List<KitchenBatchDto>> call,
                                   Response<List<KitchenBatchDto>> resp) {
                if (!resp.isSuccessful() || resp.body() == null) {
                    Log.e(TAG, "Kitchen batches failed: HTTP " + resp.code());
                    updateUI();
                    return;
                }

                // Gruppera aktiva batchar per bord, filtrera DRINK
                Map<Integer, List<KitchenBatchDto>> byTable = new LinkedHashMap<>();
                for (KitchenBatchDto b : resp.body()) {
                    if (b == null || b.batchId == null || b.tableNumber == null) continue;
                    if (b.batchType != null
                            && b.batchType.trim().equalsIgnoreCase("DRINK")) continue;
                    byTable.computeIfAbsent(b.tableNumber, k -> new ArrayList<>()).add(b);
                }

                // Rensa doneCache för bord som försvunnit från backenden
                doneCache.keySet().retainAll(byTable.keySet());

                List<KitchenOrder> fresh = new ArrayList<>();

                for (Map.Entry<Integer, List<KitchenBatchDto>> entry : byTable.entrySet()) {
                    int tableNum = entry.getKey();
                    List<KitchenBatchDto> batches = entry.getValue();

                    // Använd första batchId bara som kortkortets id (spelar ingen roll längre)
                    KitchenOrder o = new KitchenOrder(batches.get(0).batchId, tableNum);

                    // Lägg till ALLA kurser från backenden (ej klara än)
                    for (KitchenBatchDto b : batches) {
                        List<String> dishes = new ArrayList<>();
                        if (b.items != null) {
                            for (KitchenItemDto it : b.items) {
                                if (it == null || it.name == null) continue;
                                StringBuilder sb = new StringBuilder(it.name);
                                if (it.quantity != null && it.quantity > 1)
                                    sb.append(" x").append(it.quantity);
                                if (it.notes != null && !it.notes.trim().isEmpty())
                                    sb.append("\n   💬 ").append(it.notes.trim());
                                dishes.add(sb.toString());
                            }
                        }
                        int  slot      = mapBatchTypeToSlot(b.batchType);
                        long createdAt = parseCreatedAtMillis(b.createdAt);
                        // batchId skickas med i Course så completion vet vilket anrop
                        o.addCourse(new KitchenOrder.Course(b.batchId, slot, dishes, createdAt));
                    }

                    // Lägg till avklarade kurser från lokal cache
                    List<KitchenOrder.Course> done = doneCache.get(tableNum);
                    if (done != null) {
                        for (KitchenOrder.Course dc : done) o.addCourse(dc);
                    }

                    fresh.add(o);
                }

                completing = false;

                // Sortera korten: lägst aktiv slot → äldst createdAt
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

        boolean hasFraction = s.contains(".");
        if (hasFraction) {
            String[] parts = s.split("\\.", 2);
            String base = parts[0];
            String frac = parts.length > 1 ? parts[1] : "";
            frac = frac.replaceAll("[^0-9].*$", "");
            if (frac.length() > 3) frac = frac.substring(0, 3);
            while (frac.length() < 3) frac = frac + "0";
            s = base + "." + frac;
        }

        try {
            SimpleDateFormat fmt = new SimpleDateFormat(
                    hasFraction ? "yyyy-MM-dd'T'HH:mm:ss.SSS" : "yyyy-MM-dd'T'HH:mm:ss",
                    Locale.US);
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
                adapter.notifyDataSetChanged();
                clockHandler.postDelayed(this, 60_000);
            }
        });
    }

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