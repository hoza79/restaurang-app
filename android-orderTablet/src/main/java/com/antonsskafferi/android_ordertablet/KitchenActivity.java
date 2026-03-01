package com.antonsskafferi.android_ordertablet;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import androidx.annotation.NonNull;

public class KitchenActivity extends AppCompatActivity {

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

        findViewById(R.id.btnKitchenBack).setOnClickListener(v -> finish());

        tvClock = findViewById(R.id.tvClock);
        tvCount = findViewById(R.id.tvOrderCount);
        tvEmpty = findViewById(R.id.tvEmpty);
        rv      = findViewById(R.id.rvKitchenOrders);
        rv.setLayoutManager(new LinearLayoutManager(this));

        adapter = new KitchenOrderAdapter(activeOrders, (pos, fullyDone) -> {
            if (fullyDone) {
                KitchenOrder done = activeOrders.get(pos);
                KitchenDataStore.getInstance().removeOrder(done.tableNumber);
            }
            loadOrders(); // ladda om hela listan oavsett
        });
        rv.setAdapter(adapter);

        // Tryck var som helst på skärmen = markera nästa kurs som klar
        rv.addOnItemTouchListener(new RecyclerView.SimpleOnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull android.view.MotionEvent e) {
                if (e.getAction() == android.view.MotionEvent.ACTION_UP && !activeOrders.isEmpty()) {
                    KitchenOrder first = activeOrders.get(0);
                    KitchenOrder.Course next = first.currentCourse();
                    if (next != null) {
                        next.doneAt = System.currentTimeMillis();
                        if (first.isFullyDone()) {
                            KitchenDataStore.getInstance().removeOrder(first.tableNumber);
                        }
                        loadOrders();
                    }
                }
                return false; // false = låt RecyclerView hantera resten normalt
            }
        });

        startClock();
        startPolling();
        loadOrders();
    }

    /**
     * Hämtar ordrar från KitchenDataStore och sorterar på aktiv kurs:
     * slot 0 (Dryck/Bar) → 1 (Förrätt) → 2 (Varmrätt) → 3 (Efterrätt).
     * Inom samma slot hamnar äldst överst (createdAt ASC).
     */
    private void loadOrders() {
        List<KitchenOrder> fresh = KitchenDataStore.getInstance().getActiveOrders();

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

    /** Pollar var 5:e sekund – ersätts med backend-anrop när API är klart. */
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