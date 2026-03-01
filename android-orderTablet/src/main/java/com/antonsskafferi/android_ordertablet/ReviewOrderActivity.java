package com.antonsskafferi.android_ordertablet;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class ReviewOrderActivity extends AppCompatActivity {

    private static final String[] SLOT_LABELS = {"Dryck", "Förrätt", "Varmrätt", "Efterrätt"};
    private static final int BG      = 0xFF121212;
    private static final int SURFACE = 0xFF1E1E1E;
    private static final int GOLD    = 0xFFC9A961;
    private static final int WHITE   = 0xFFEEEEEE;
    private static final int GREY    = 0xFF888888;
    private static final int SENT    = 0xFF4ECDC4;
    private static final int RED     = 0xFFFF6B6B;

    private LinearLayout llSections;
    private TextView tvTotal;

    @Override
    protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_review_order);
        getWindow().getDecorView().setBackgroundColor(BG);

        ((TextView) findViewById(R.id.tvReviewTable))
                .setText("Bord " + Cart.getActiveTable());
        findViewById(R.id.btnBackToMenu).setOnClickListener(v -> finish());

        llSections = findViewById(R.id.llCourseSections);
        tvTotal    = findViewById(R.id.tvTotal);
        renderSections();
    }

    private void renderSections() {
        llSections.removeAllViews();
        Cart.CartSession session = Cart.current();
        boolean anyItems   = false;
        boolean anyPending = false;

        for (int slot = 0; slot <= 3; slot++) {
            List<OrderItem> slotItems = new ArrayList<>();
            for (OrderItem it : session.items)
                if (it.courseSlot == slot) slotItems.add(it);
            if (slotItems.isEmpty()) continue;
            anyItems = true;

            boolean allSent = slotItems.stream().allMatch(i -> i.sentAt > 0);
            if (!allSent) anyPending = true;

            // Rubrik
            TextView tvHdr = new TextView(this);
            tvHdr.setText(SLOT_LABELS[slot].toUpperCase()
                    + (slot == 0 ? "  →  BAR" : "  →  KÖK"));
            tvHdr.setTextSize(11); tvHdr.setTextColor(GREY);
            LinearLayout.LayoutParams hp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            hp.setMargins(0, dp(14), 0, dp(4));
            tvHdr.setLayoutParams(hp);
            llSections.addView(tvHdr);

            // Kort
            LinearLayout card = new LinearLayout(this);
            card.setOrientation(LinearLayout.VERTICAL);
            card.setBackgroundColor(SURFACE);
            card.setPadding(dp(12), dp(10), dp(12), dp(12));
            llSections.addView(card);

            for (int i = 0; i < slotItems.size(); i++) {
                OrderItem item = slotItems.get(i);
                final int itemIdx = session.items.indexOf(item);
                boolean sent = item.sentAt > 0;

                LinearLayout row = new LinearLayout(this);
                row.setOrientation(LinearLayout.HORIZONTAL);
                row.setGravity(Gravity.CENTER_VERTICAL);
                row.setPadding(0, dp(6), 0, dp(6));
                row.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));

                Button btnM = qtyBtn("−", sent);
                btnM.setOnClickListener(v -> { session.decreaseQty(itemIdx); renderSections(); });
                TextView tvQty = new TextView(this);
                tvQty.setText(String.valueOf(item.quantity));
                tvQty.setTextSize(15); tvQty.setTextColor(sent ? GREY : WHITE);
                tvQty.setGravity(Gravity.CENTER); tvQty.setMinWidth(dp(28));
                Button btnP = qtyBtn("+", sent);
                btnP.setOnClickListener(v -> { session.increaseQty(itemIdx); renderSections(); });

                LinearLayout col = new LinearLayout(this);
                col.setOrientation(LinearLayout.VERTICAL);
                col.setLayoutParams(new LinearLayout.LayoutParams(
                        0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
                col.setPadding(dp(8), 0, dp(8), 0);

                TextView tvName = new TextView(this);
                tvName.setText(item.dishName);
                tvName.setTextSize(15); tvName.setTextColor(sent ? GREY : WHITE);
                if (sent) tvName.setPaintFlags(tvName.getPaintFlags()
                        | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
                col.addView(tvName);

                StringBuilder spec = new StringBuilder();
                if (item.cooking != null && !item.cooking.isEmpty()) spec.append(item.cooking);
                if (item.sides != null && !item.sides.isEmpty()) {
                    if (spec.length() > 0) spec.append(" · ");
                    spec.append(String.join(", ", item.sides));
                }
                if (item.comment != null && !item.comment.isEmpty()) {
                    if (spec.length() > 0) spec.append("\n");
                    spec.append("💬 ").append(item.comment);
                }
                if (spec.length() > 0) {
                    TextView tvSpec = new TextView(this);
                    tvSpec.setText(spec); tvSpec.setTextSize(12); tvSpec.setTextColor(GREY);
                    col.addView(tvSpec);
                }

                if (!sent) tvName.setOnLongClickListener(v -> {
                    showCommentDialog(item); return true; });

                TextView tvPrice = new TextView(this);
                tvPrice.setText(String.format("%.0f kr", item.totalPrice()));
                tvPrice.setTextSize(14); tvPrice.setTextColor(sent ? GREY : GOLD);
                tvPrice.setMinWidth(dp(60));
                tvPrice.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);

                Button btnDel = new Button(this);
                btnDel.setText("✕"); btnDel.setTextSize(13); btnDel.setTextColor(RED);
                btnDel.setBackgroundColor(0); btnDel.setPadding(dp(8), 0, dp(4), 0);
                btnDel.setVisibility(sent ? View.GONE : View.VISIBLE);
                btnDel.setOnClickListener(v -> { session.removeItem(itemIdx); renderSections(); });

                row.addView(btnM); row.addView(tvQty); row.addView(btnP);
                row.addView(col); row.addView(tvPrice); row.addView(btnDel);
                card.addView(row);

                if (i < slotItems.size() - 1) {
                    View sep = new View(this);
                    sep.setBackgroundColor(0xFF333333);
                    sep.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, 1));
                    card.addView(sep);
                }
            }

            // Visa "✓ Skickad" om hela sloten är klar
            if (allSent) {
                TextView tvS = new TextView(this);
                tvS.setText("✓ Skickad");
                tvS.setTextColor(SENT); tvS.setTextSize(12);
                tvS.setPadding(0, dp(10), 0, 0);
                card.addView(tvS);
            }
        }

        if (!anyItems) {
            TextView tvE = new TextView(this);
            tvE.setText("Korgen är tom");
            tvE.setTextColor(GREY); tvE.setGravity(Gravity.CENTER);
            tvE.setPadding(0, dp(48), 0, 0);
            llSections.addView(tvE);
        }

        tvTotal.setText(String.format("%.0f kr", session.total()));

        // En gemensam "Skicka beställning"-knapp
        Button btnSend = findViewById(R.id.btnSendOrder);
        if (anyPending) {
            btnSend.setVisibility(View.VISIBLE);
            btnSend.setOnClickListener(v -> sendAll(session));
        } else {
            btnSend.setVisibility(View.GONE);
        }

        // Betala-knapp
        Button btnPay = findViewById(R.id.btnSendToKitchen);
        btnPay.setText("💳  Betala");
        btnPay.setVisibility(View.VISIBLE);
        btnPay.setOnClickListener(v -> {
            Intent i = new Intent(this, PaymentActivity.class);
            i.putExtra("total", session.total());
            startActivity(i);
        });
    }

    private void sendAll(Cart.CartSession session) {
        boolean anything = false;
        for (int slot = 0; slot <= 3; slot++) {
            List<OrderItem> pending = session.getPendingForSlot(slot);
            if (pending.isEmpty()) continue;
            if (slot != 0) { // slot 0 = dryck → bar, läggs ej i KitchenDataStore
                KitchenDataStore.getInstance().addOrder(
                        Cart.getActiveTable(), slot, pending);
            }
            session.markSlotSent(slot);
            anything = true;
        }
        if (anything) {
            Toast.makeText(this, "✓ Beställning skickad!", Toast.LENGTH_SHORT).show();
            renderSections();
        }
    }

    private void showCommentDialog(OrderItem item) {
        EditText et = new EditText(this);
        et.setText(item.comment);
        et.setHint("Specialönskemål...");
        et.setPadding(dp(16), dp(12), dp(16), dp(12));
        new AlertDialog.Builder(this)
                .setTitle("Redigera kommentar")
                .setView(et)
                .setPositiveButton("Spara", (d, w) -> {
                    item.comment = et.getText().toString().trim();
                    renderSections();
                })
                .setNegativeButton("Avbryt", null).show();
    }

    private Button qtyBtn(String label, boolean disabled) {
        Button b = new Button(this);
        b.setText(label); b.setTextSize(18);
        b.setTextColor(disabled ? GREY : WHITE);
        b.setBackgroundColor(0);
        b.setPadding(dp(4), 0, dp(4), 0);
        b.setLayoutParams(new LinearLayout.LayoutParams(dp(40), dp(40)));
        b.setEnabled(!disabled);
        return b;
    }

    private int dp(int v) {
        return Math.round(v * getResources().getDisplayMetrics().density);
    }
}