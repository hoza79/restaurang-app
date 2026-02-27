package com.antonsskafferi.android_ordertablet;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class PaymentActivity extends AppCompatActivity {

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
        Button btnKort = payBtn("💳  Betala med Kort", 0xFF1565C0);
        btnKort.setOnClickListener(v -> confirmPayment("Kortbetalning", tableNum));
        root.addView(btnKort);

        // Swish-knapp
        View spacer = new View(this);
        spacer.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(16)));
        root.addView(spacer);

        Button btnSwish = payBtn("📱  Betala med Swish", 0xFF1B5E20);
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
        // Frigör bord
        KitchenDataStore.getInstance().removeOrder(tableNum);
        Cart.closeTable(tableNum);

        Toast.makeText(this,
                "✓ " + method + " klar – Bord " + tableNum + " är ledigt!",
                Toast.LENGTH_LONG).show();

        // TODO: POST /api/orders/{id}/pay  { method, tableId }

        // Gå tillbaka till bordsöversikten, rensa stack
        Intent i = new Intent(this, TableSelectActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(i);
        finish();
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
