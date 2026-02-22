package com.antonsskafferi.pos;

import android.graphics.Color;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

/**
 * KÖKSVY – ett kort per bord.
 * Visar ENBART den aktuella kursen som köket ska laga just nu.
 * ETT TRYCK PÅ KORTET = kurs klar.
 *
 * Tidslogik:
 *   < 10 min  → grön  (#4ECDC4)
 *   10-20 min → orange (#FFB347)
 *   ≥ 20 min  → röd   (#FF6B6B)
 */
public class KitchenOrderAdapter extends RecyclerView.Adapter<KitchenOrderAdapter.VH> {

    public interface OnCourseDone { void onCourseDone(int position, boolean orderFullyDone); }

    private final List<KitchenOrder> orders;
    private final OnCourseDone listener;

    public KitchenOrderAdapter(List<KitchenOrder> orders, OnCourseDone l) {
        this.orders = orders;
        this.listener = l;
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup p, int t) {
        return new VH(LayoutInflater.from(p.getContext()).inflate(R.layout.item_kitchen_order, p, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        KitchenOrder order = orders.get(pos);
        KitchenOrder.Course cur = order.currentCourse();
        if (cur == null) return;

        // --- Rubrik ---
        h.tvTable.setText("Bord " + order.tableNumber);

        // --- Kursbadge: vilken kurs + hur många kvar ---
        String badge = cur.slotLabel();
        if (order.remainingCourses() > 1)
            badge += "  (" + order.remainingCourses() + " kurser kvar)";
        h.tvCourseBadge.setText(badge);

        // --- Tidsbadge ---
        long mins = cur.elapsedMinutes();
        h.tvTime.setText(mins + " min");
        if      (mins < 10) h.tvTime.setBackgroundColor(Color.parseColor("#4ECDC4"));
        else if (mins < 20) h.tvTime.setBackgroundColor(Color.parseColor("#FFB347"));
        else                h.tvTime.setBackgroundColor(Color.parseColor("#FF6B6B"));

        // --- Rätter ---
        h.llDishes.removeAllViews();
        for (String dish : cur.dishes) {
            TextView tv = new TextView(h.itemView.getContext());
            tv.setText("• " + dish);
            tv.setTextColor(Color.parseColor("#DDDDDD"));
            tv.setTextSize(15);
            tv.setPadding(0, dp(h, 4), 0, dp(h, 4));
            h.llDishes.addView(tv);
        }

        // --- ETT TRYCK = klar ---
        h.card.setOnClickListener(v -> {
            boolean fullyDone = order.completeCurrentCourse();
            listener.onCourseDone(h.getAdapterPosition(), fullyDone);
        });

        // Visuell hint
        h.tvHint.setText(order.remainingCourses() == 1
                ? "Tryck för att markera som KLAR"
                : "Tryck för att markera kursen som klar");
    }

    @Override public int getItemCount() { return orders.size(); }

    private int dp(VH h, int v) {
        return Math.round(v * h.itemView.getResources().getDisplayMetrics().density);
    }

    static class VH extends RecyclerView.ViewHolder {
        LinearLayout card, llDishes;
        TextView tvTable, tvCourseBadge, tvTime, tvHint;

        VH(View v) {
            super(v);
            card         = v.findViewById(R.id.orderCard);
            tvTable      = v.findViewById(R.id.tvTableNum);
            tvCourseBadge= v.findViewById(R.id.tvCourseBadge);
            tvTime       = v.findViewById(R.id.tvTimeBadge);
            llDishes     = v.findViewById(R.id.llDishList);
            tvHint       = v.findViewById(R.id.tvTapHint);
        }
    }
}