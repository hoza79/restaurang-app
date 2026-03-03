package com.antonsskafferi.android_ordertablet;

import android.graphics.Color;
import android.graphics.Typeface;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class KitchenOrderAdapter extends RecyclerView.Adapter<KitchenOrderAdapter.VH> {

    public interface OnCourseDone { void onCourseDone(int position, boolean orderFullyDone); }

    private static final int DIVIDER = Color.parseColor("#333333");
    private static final int WHITE   = Color.parseColor("#EEEEEE");
    private static final int GOLD    = Color.parseColor("#C9A961");
    private static final SimpleDateFormat SDF = new SimpleDateFormat("HH:mm", Locale.getDefault());

    private final List<KitchenOrder> orders;
    private final OnCourseDone listener;

    public KitchenOrderAdapter(List<KitchenOrder> orders, OnCourseDone l) {
        this.orders = orders;
        this.listener = l;
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup p, int t) {
        return new VH(LayoutInflater.from(p.getContext())
                .inflate(R.layout.item_kitchen_order, p, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        KitchenOrder order = orders.get(pos);
        h.tvTable.setText("Bord " + order.tableNumber);
        h.llDishes.removeAllViews();
        h.tvTime.setVisibility(View.GONE);
        h.tvCourseBadge.setVisibility(View.GONE);
        h.tvHint.setVisibility(View.GONE);

        // Bygg kurssektioner
        boolean first = true;
        for (KitchenOrder.Course course : order.courses) {
            if (course.isDone()) continue;

            if (!first) {
                View sep = new View(h.itemView.getContext());
                sep.setBackgroundColor(DIVIDER);
                LinearLayout.LayoutParams sp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, 1);
                sp.setMargins(0, dp(h, 10), 0, dp(h, 10));
                sep.setLayoutParams(sp);
                h.llDishes.addView(sep);
            }
            first = false;

            LinearLayout section = new LinearLayout(h.itemView.getContext());
            section.setOrientation(LinearLayout.VERTICAL);
            section.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));

            // Header: kursnamn + tidsstämpel + elapsed
            LinearLayout header = new LinearLayout(h.itemView.getContext());
            header.setOrientation(LinearLayout.HORIZONTAL);
            header.setGravity(Gravity.CENTER_VERTICAL);
            LinearLayout.LayoutParams hlp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            hlp.setMargins(0, 0, 0, dp(h, 6));
            header.setLayoutParams(hlp);

            TextView tvLabel = new TextView(h.itemView.getContext());
            tvLabel.setText(course.slotLabel().toUpperCase());
            tvLabel.setTextColor(GOLD);
            tvLabel.setTextSize(11);
            tvLabel.setTypeface(null, Typeface.BOLD);
            tvLabel.setLayoutParams(new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
            header.addView(tvLabel);

            TextView tvStamp = new TextView(h.itemView.getContext());
            tvStamp.setText(SDF.format(new Date(course.createdAt)));
            tvStamp.setTextColor(Color.parseColor("#AAAAAA"));
            tvStamp.setTextSize(12);
            tvStamp.setPadding(0, 0, dp(h, 8), 0);
            header.addView(tvStamp);

            long mins = course.elapsedMinutes();
            TextView tvElapsed = new TextView(h.itemView.getContext());
            tvElapsed.setText(mins + " min");
            tvElapsed.setTextColor(Color.BLACK);
            tvElapsed.setTextSize(12);
            tvElapsed.setPadding(dp(h, 8), dp(h, 2), dp(h, 8), dp(h, 2));
            if      (mins < 10) tvElapsed.setBackgroundColor(Color.parseColor("#4ECDC4"));
            else if (mins < 20) tvElapsed.setBackgroundColor(Color.parseColor("#FFB347"));
            else                tvElapsed.setBackgroundColor(Color.parseColor("#FF6B6B"));
            header.addView(tvElapsed);

            section.addView(header);

            for (String dish : course.dishes) {
                TextView tv = new TextView(h.itemView.getContext());
                tv.setText("• " + dish);
                tv.setTextColor(WHITE);
                tv.setTextSize(15);
                tv.setPadding(dp(h, 4), dp(h, 3), 0, dp(h, 3));
                section.addView(tv);
            }

            h.llDishes.addView(section);
        }

        // Overlay is no longer responsible for completion; KitchenActivity handles "tap anywhere"
        h.touchOverlay.setOnClickListener(null);
        h.touchOverlay.setClickable(false);
    }

    @Override public int getItemCount() { return orders.size(); }

    private int dp(VH h, int v) {
        return Math.round(v * h.itemView.getResources().getDisplayMetrics().density);
    }

    static class VH extends RecyclerView.ViewHolder {
        LinearLayout card, llDishes;
        TextView tvTable, tvCourseBadge, tvTime, tvHint;
        View touchOverlay;

        VH(View v) {
            super(v);
            card          = v.findViewById(R.id.orderCard);
            tvTable       = v.findViewById(R.id.tvTableNum);
            tvCourseBadge = v.findViewById(R.id.tvCourseBadge);
            tvTime        = v.findViewById(R.id.tvTimeBadge);
            llDishes      = v.findViewById(R.id.llDishList);
            tvHint        = v.findViewById(R.id.tvTapHint);
            touchOverlay  = v.findViewById(R.id.touchOverlay);
        }
    }
}