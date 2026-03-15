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
import android.util.Log;
import com.antonsskafferi.android_ordertablet.net.ApiClient;
import com.antonsskafferi.android_ordertablet.net.CreateBatchRequest;
import com.antonsskafferi.android_ordertablet.net.CreateBatchItemRequest;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReviewOrderActivity extends AppCompatActivity {
    private static final String TAG = "ReviewOrderActivity";

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

            final int fs = slot;
            boolean allSent    = slotItems.stream().allMatch(i -> i.sentAt > 0);
            boolean hasPending = session.hasPendingForSlot(slot);
            if (hasPending) anyPending = true;

            // Rubrik
            TextView tvHdr = new TextView(this);
            tvHdr.setText(SLOT_LABELS[slot].toUpperCase()
                    + (slot == 0 ? "  ->  BAR" : "  ->  KÖK"));
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

                // Spec-rad
                StringBuilder spec = new StringBuilder();
                if (item.cooking != null && !item.cooking.isEmpty()) spec.append(item.cooking);
                if (item.sides != null && !item.sides.isEmpty()) {
                    if (spec.length() > 0) spec.append(" - ");
                    spec.append(String.join(", ", item.sides));
                }
                if (item.comment != null && !item.comment.isEmpty()) {
                    if (spec.length() > 0) spec.append("\n");
                    spec.append(item.comment);
                }
                if (spec.length() > 0) {
                    TextView tvSpec = new TextView(this);
                    tvSpec.setText(spec); tvSpec.setTextSize(12); tvSpec.setTextColor(GREY);
                    col.addView(tvSpec);
                }

                // Lång-klick = redigera kommentar
                if (!sent) tvName.setOnLongClickListener(v -> {
                    showCommentDialog(item); return true; });

                TextView tvPrice = new TextView(this);
                tvPrice.setText(String.format("%.0f kr", item.totalPrice()));
                tvPrice.setTextSize(14); tvPrice.setTextColor(sent ? GREY : GOLD);
                tvPrice.setMinWidth(dp(60));
                tvPrice.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);

                Button btnDel = new Button(this);
                btnDel.setText("X"); btnDel.setTextSize(13); btnDel.setTextColor(RED);
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

            // Skicka-knapp (per slot)
            if (hasPending) {
                String dest = (slot == 0) ? "baren " : "köket ";
                Button btn = new Button(this);
                btn.setText("Skicka " + SLOT_LABELS[slot] + " till " + dest);
                btn.setBackgroundColor(slot == 0 ? 0xFF1565C0 : 0xFF2E2E2E);
                btn.setTextColor(GOLD); btn.setTextSize(14);
                LinearLayout.LayoutParams bp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, dp(52));
                bp.setMargins(0, dp(10), 0, 0);
                btn.setLayoutParams(bp);

                btn.setOnClickListener(v -> {
                    if (session.orderId == null) {
                        Toast.makeText(this,
                                "Saknar orderId (backend). Gå tillbaka och öppna bordet igen.",
                                Toast.LENGTH_LONG).show();
                        return;
                    }

                    List<OrderItem> pending = session.getPendingForSlot(fs);
                    if (pending.isEmpty()) return;

                    // Slot 0 = Dryck -> BAR (do not send to kitchen backend)
                    if (fs == 0) {
                        session.markSlotSent(fs);
                        Toast.makeText(ReviewOrderActivity.this,
                                SLOT_LABELS[fs] + " skickad till baren!",
                                Toast.LENGTH_SHORT).show();
                        renderSections();
                        return;
                    }

                    String batchType = slotToBatchType(fs);

                    List<CreateBatchItemRequest> items = new ArrayList<>();
                    for (OrderItem it : pending) {
                        items.add(new CreateBatchItemRequest(
                                it.menuItemId,
                                it.quantity,
                                buildNotes(it)
                        ));
                    }

                    ApiClient.api().createBatch(session.orderId, new CreateBatchRequest(batchType, items))
                            .enqueue(new Callback<Map<String, Object>>() {
                                @Override
                                public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> resp) {
                                    if (resp.isSuccessful()) {
                                        session.markSlotSent(fs);
                                        Toast.makeText(ReviewOrderActivity.this,
                                                SLOT_LABELS[fs] + " skickad!",
                                                Toast.LENGTH_SHORT).show();
                                        renderSections();
                                    } else {
                                        Toast.makeText(ReviewOrderActivity.this,
                                                "Kunde inte skicka (" + resp.code() + ")",
                                                Toast.LENGTH_LONG).show();
                                        Log.e(TAG, "createBatch failed: HTTP " + resp.code());
                                    }
                                }

                                @Override
                                public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                                    Toast.makeText(ReviewOrderActivity.this,
                                            "Ingen kontakt med servern",
                                            Toast.LENGTH_LONG).show();
                                    Log.e(TAG, "createBatch network failure", t);
                                }
                            });
                });

                card.addView(btn);
            } else if (allSent) {
                // Visa "Skickad" om hela sloten är klar
                TextView tvS = new TextView(this);
                tvS.setText("Skickad");
                tvS.setTextColor(SENT); tvS.setTextSize(12);
                tvS.setPadding(0, dp(10), 0, 0);
                card.addView(tvS);
            }
        }

// Empty cart
        if (!anyItems) {
            TextView tvE = new TextView(this);
            tvE.setText("Korgen är tom");
            tvE.setTextColor(GREY); tvE.setGravity(Gravity.CENTER);
            tvE.setPadding(0, dp(48), 0, 0);
            llSections.addView(tvE);
        }

        tvTotal.setText(String.format("%.0f kr", session.total()));

// Gemensam "Skicka beställning"-knapp (skickar alla pending slots)
        Button btnSend = findViewById(R.id.btnSendOrder);
        if (anyPending) {
            btnSend.setVisibility(View.VISIBLE);
            btnSend.setOnClickListener(v -> sendAll(session));
        } else {
            btnSend.setVisibility(View.GONE);
        }

// Betala-knapp
        Button btnPay = findViewById(R.id.btnSendToKitchen);
        btnPay.setText("Betala");
        btnPay.setVisibility(View.VISIBLE);
        btnPay.setOnClickListener(v -> {
            Intent i = new Intent(this, PaymentActivity.class);
            i.putExtra("total", session.total());
            startActivity(i);
        });
    }

    private void sendAll(Cart.CartSession session) {
        if (session.orderId == null) {
            Toast.makeText(this,
                    "Saknar orderId (backend). Gå tillbaka och öppna bordet igen.",
                    Toast.LENGTH_LONG).show();
            return;
        }

        // Collect pending by slot
        List<Integer> slotsToSend = new ArrayList<>();
        for (int slot = 0; slot <= 3; slot++) {
            if (!session.getPendingForSlot(slot).isEmpty()) slotsToSend.add(slot);
        }

        if (slotsToSend.isEmpty()) return;

        // Handle DRINK slot locally (bar)
        if (slotsToSend.contains(0)) {
            session.markSlotSent(0);
            slotsToSend.remove((Integer) 0);
        }

        // If only drinks were pending
        if (slotsToSend.isEmpty()) {
            Toast.makeText(this, "Skickad till baren!", Toast.LENGTH_SHORT).show();
            renderSections();
            return;
        }

        // Send remaining slots as backend batches (parallel)
        java.util.concurrent.atomic.AtomicInteger remaining = new java.util.concurrent.atomic.AtomicInteger(slotsToSend.size());
        java.util.concurrent.atomic.AtomicBoolean anyFail = new java.util.concurrent.atomic.AtomicBoolean(false);

        for (int slot : slotsToSend) {
            List<OrderItem> pending = session.getPendingForSlot(slot);
            String batchType = slotToBatchType(slot);

            List<CreateBatchItemRequest> items = new ArrayList<>();
            for (OrderItem it : pending) {
                items.add(new CreateBatchItemRequest(
                        it.menuItemId,
                        it.quantity,
                        buildNotes(it)
                ));
            }

            ApiClient.api().createBatch(session.orderId, new CreateBatchRequest(batchType, items))
                    .enqueue(new Callback<Map<String, Object>>() {
                        @Override
                        public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> resp) {
                            if (resp.isSuccessful()) {
                                session.markSlotSent(slot);
                            } else {
                                anyFail.set(true);
                                Log.e(TAG, "sendAll createBatch failed for slot " + slot + ": HTTP " + resp.code());
                            }

                            if (remaining.decrementAndGet() == 0) {
                                if (anyFail.get()) {
                                    Toast.makeText(ReviewOrderActivity.this,
                                            "Vissa delar kunde inte skickas. Försök igen.",
                                            Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(ReviewOrderActivity.this,
                                            "Beställning skickad!",
                                            Toast.LENGTH_SHORT).show();
                                }
                                renderSections();
                            }
                        }

                        @Override
                        public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                            anyFail.set(true);
                            Log.e(TAG, "sendAll createBatch network failure for slot " + slot, t);

                            if (remaining.decrementAndGet() == 0) {
                                Toast.makeText(ReviewOrderActivity.this,
                                        "Ingen kontakt med servern. Försök igen.",
                                        Toast.LENGTH_LONG).show();
                                renderSections();
                            }
                        }
                    });
        }
    }

    private String slotToBatchType(int slot) {
        switch (slot) {
            case 0: return "DRINK";
            case 1: return "APPETIZER";
            case 2: return "MAIN_COURSE";
            case 3: return "DESSERT";
            default: return "MAIN_COURSE";
        }
    }

    private String buildNotes(OrderItem it) {
        StringBuilder sb = new StringBuilder();

        if (it.cooking != null && !it.cooking.trim().isEmpty()) {
            sb.append(it.cooking.trim());
        }
        if (it.sides != null && !it.sides.isEmpty()) {
            if (sb.length() > 0) sb.append(" - ");
            sb.append(String.join(", ", it.sides));
        }
        if (it.comment != null && !it.comment.trim().isEmpty()) {
            if (sb.length() > 0) sb.append("\n");
            sb.append(it.comment.trim());
        }

        return sb.length() == 0 ? null : sb.toString();
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
