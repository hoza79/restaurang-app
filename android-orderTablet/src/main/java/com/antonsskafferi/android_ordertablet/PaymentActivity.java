package com.antonsskafferi.android_ordertablet;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import com.antonsskafferi.android_ordertablet.net.ApiClient;
import com.antonsskafferi.android_ordertablet.net.PayRequest;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentActivity extends AppCompatActivity {
    private static final String TAG = "PaymentActivity";

    private static final int BG      = 0xFF121212;
    private static final int SURFACE = 0xFF1E1E1E;
    private static final int GOLD    = 0xFFC9A961;
    private static final int WHITE   = 0xFFEEEEEE;
    private static final int GREY    = 0xFF888888;
    private static final int GREEN   = 0xFF4ECDC4;

    @Override
    protected void onCreate(Bundle s) {
        super.onCreate(s);

        double total    = getIntent().getDoubleExtra("total", 0);
        int    tableNum = Cart.getActiveTable();

        // ── Bygg UI programmatiskt (inget extra XML behövs) ──────────
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(BG);
        root.setPadding(dp(24), dp(32), dp(24), dp(32));
        setContentView(root);

        // Header
        TextView tvTitle = new TextView(this);
        tvTitle.setText("Betala – Bord " + tableNum);
        tvTitle.setTextSize(22); tvTitle.setTextColor(WHITE);
        tvTitle.setTypeface(null, android.graphics.Typeface.BOLD);
        root.addView(tvTitle);

        // Totalsumma
        TextView tvTotal = new TextView(this);
        tvTotal.setText(String.format("%.0f kr", total));
        tvTotal.setTextSize(48); tvTotal.setTextColor(GOLD);
        tvTotal.setTypeface(null, android.graphics.Typeface.BOLD);
        tvTotal.setGravity(android.view.Gravity.CENTER);
        LinearLayout.LayoutParams tp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        tp.setMargins(0, dp(40), 0, dp(48));
        tvTotal.setLayoutParams(tp);
        root.addView(tvTotal);

        // Kort-knapp
        Button btnKort = payBtn("Betala med Kort", 0xFF1565C0);
        btnKort.setOnClickListener(v -> confirmPayment("Kortbetalning", tableNum));
        root.addView(btnKort);

        // Swish-knapp
        View spacer = new View(this);
        spacer.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(16)));
        root.addView(spacer);

        Button btnSwish = payBtn("Betala med Swish", 0xFF1B5E20);
        btnSwish.setOnClickListener(v -> confirmPayment("Swish", tableNum));
        root.addView(btnSwish);

        // Avbryt
        Button btnCancel = new Button(this);
        btnCancel.setText("Avbryt");
        btnCancel.setTextColor(GREY); btnCancel.setTextSize(14);
        btnCancel.setBackgroundColor(0);
        LinearLayout.LayoutParams cp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(52));
        cp.setMargins(0, dp(24), 0, 0);
        btnCancel.setLayoutParams(cp);
        btnCancel.setOnClickListener(v -> finish());
        root.addView(btnCancel);
    }

    private void confirmPayment(String method, int tableNum) {

        Integer tableId = Cart.getActiveTableId();
        if (tableId == null) {
            Toast.makeText(this,
                    "Saknar tableId från backend. Öppna bordet igen.", Toast.LENGTH_LONG).show();
            return;
        }

        // Optional: disable buttons here to prevent double-tap
        Toast.makeText(this, "Skickar betalning...", Toast.LENGTH_SHORT).show();

        ApiClient.api().payOrder(new PayRequest(tableId, method)).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> resp) {

                if (resp.isSuccessful()) {
                    // now do local cleanup
                    Cart.closeTable(tableNum);

                    Toast.makeText(PaymentActivity.this,
                            method + " klar – Bord " + tableNum + " är ledigt!",
                            Toast.LENGTH_LONG).show();

                    Intent i = new Intent(PaymentActivity.this, TableSelectActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(i);
                    finish();
                    return;
                }

                // Not successful: show backend error
                String msg = "Betalning nekad (" + resp.code() + ")";
                try {
                    if (resp.errorBody() != null) {
                        msg = resp.errorBody().string();
                    }
                } catch (Exception ignored) {}

                Log.e(TAG, "payOrder failed: HTTP " + resp.code() + " " + msg);

                // Common case: 400 when kitchen still processing
                Toast.makeText(PaymentActivity.this,
                        "Kan inte betala än: " + msg,
                        Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Log.e(TAG, "payOrder network failure", t);
                Toast.makeText(PaymentActivity.this,
                        "Ingen kontakt med servern", Toast.LENGTH_LONG).show();
            }
        });
    }

    private Button payBtn(String label, int color) {
        Button b = new Button(this);
        b.setText(label); b.setTextSize(18);
        b.setTextColor(0xFFFFFFFF);
        b.setBackgroundColor(color);
        b.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(64)));
        return b;
    }

    private int dp(int v) {
        return Math.round(v * getResources().getDisplayMetrics().density);
    }
}
