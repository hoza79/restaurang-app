package com.antonsskafferi.pos;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.*;

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

        // ← BACK-knapp
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
                activeOrders.remove(pos);
                adapter.notifyItemRemoved(pos);
            } else {
                adapter.notifyItemChanged(pos);
            }
            updateUI();
        });
        rv.setAdapter(adapter);

        startClock();
        startPolling();
        loadOrders();
    }

    /** Hämtar färska ordrar från KitchenDataStore och uppdaterar listan. */
    private void loadOrders() {
        activeOrders.clear();
        activeOrders.addAll(KitchenDataStore.getInstance().getActiveOrders());
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
                        Locale.getDefault()).format(new Date()));
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