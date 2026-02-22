package com.antonsskafferi.pos;

import android.view.*;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.VH> {

    public interface OnRemove { void onRemove(int pos); }

    private final List<OrderItem> items;
    private final OnRemove listener;

    public OrderItemAdapter(List<OrderItem> items, OnRemove l) {
        this.items = items; listener = l;
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup p, int t) {
        return new VH(LayoutInflater.from(p.getContext())
                .inflate(android.R.layout.simple_list_item_2, p, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        OrderItem it = items.get(pos);

        // ← FIX: it.dishName (inte it.name)
        h.tv1.setText(it.dishName + "  –  " + String.format("%.0f kr", it.price));

        // ← FIX: it.menuCategory (inte it.category) + courseLabel()
        StringBuilder d = new StringBuilder(it.courseLabel());
        if (it.cooking != null && !it.cooking.isEmpty())          d.append(" · ").append(it.cooking);
        if (it.sides   != null && !it.sides.isEmpty())            d.append(" · ").append(String.join(", ", it.sides));
        if (it.comment != null && !it.comment.isEmpty())          d.append("\n💬 ").append(it.comment);
        if (it.sentAt > 0)                                        d.append("  ✓ skickad");

        h.tv2.setText(d);
        h.itemView.setOnLongClickListener(v -> {
            listener.onRemove(h.getAdapterPosition());
            return true;
        });
    }

    @Override public int getItemCount() { return items.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView tv1, tv2;
        VH(View v) {
            super(v);
            tv1 = v.findViewById(android.R.id.text1);
            tv2 = v.findViewById(android.R.id.text2);
        }
    }
}