package com.antonsskafferi.android_ordertablet;

import android.content.Intent;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

/**
 * Slot = den flik servitören befinner sig i.
 * Ingen dialog – rätten läggs direkt med flikens courseSlot.
 *
 * Exempel: Entrecôte i "Förrätt"-fliken → courseSlot=1 → köket
 * ser den som förrätt och tillagar den med övriga förrätter.
 */
public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.VH> {

    private static final String[] SLOT_LABELS = {"Dryck", "Förrätt", "Varmrätt", "Efterrätt"};

    private final List<MenuItem> items;
    private final int defaultSlot; // 0=Dryck 1=Förrätt 2=Varmrätt 3=Efterrätt

    public MenuAdapter(List<MenuItem> items, int defaultSlot) {
        this.items = items;
        this.defaultSlot = defaultSlot;
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup p, int t) {
        return new VH(LayoutInflater.from(p.getContext())
                .inflate(R.layout.item_menu_dish, p, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        MenuItem m = items.get(pos);
        h.tvName.setText(m.name);
        h.tvDesc.setText(m.description);
        h.tvPrice.setText(String.format("%.0f kr", m.price));

        h.btnAdd.setOnClickListener(v -> {
            if (m.hasCookingOptions || (m.sides != null && !m.sides.isEmpty())) {
                // Kräver val → gå till DishDetailActivity
                Intent i = new Intent(v.getContext(), DishDetailActivity.class);
                i.putExtra("dishId", m.id);
                i.putExtra("defaultSlot", defaultSlot); // flikens slot följer med
                v.getContext().startActivity(i);
            } else {
                // Lägg direkt med flikens slot – ingen dialog
                Cart.current().addItem(
                        new OrderItem(m.name, m.price, m.category, defaultSlot));
                Toast.makeText(v.getContext(),
                        m.name + " → " + SLOT_LABELS[defaultSlot],
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override public int getItemCount() { return items.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvName, tvDesc, tvPrice;
        Button btnAdd;
        VH(View v) {
            super(v);
            tvName  = v.findViewById(R.id.tvDishName);
            tvDesc  = v.findViewById(R.id.tvDishDesc);
            tvPrice = v.findViewById(R.id.tvDishPrice);
            btnAdd  = v.findViewById(R.id.btnAddDish);
        }
    }
}