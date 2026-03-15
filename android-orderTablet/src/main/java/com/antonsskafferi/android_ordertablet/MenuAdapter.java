package com.antonsskafferi.android_ordertablet;

import android.app.AlertDialog;
import android.content.Context;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.VH> {

    private static final int SURFACE = 0xFF1E1E1E;
    private static final int GOLD    = 0xFFC9A961;
    private static final int WHITE   = 0xFFEEEEEE;
    private static final int GREY    = 0xFF888888;

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

        // Byt ut plus mot tre prickar
        h.btnAdd.setText("•••");
        h.btnAdd.setVisibility(View.VISIBLE);

        // Tryck på hela kortet
        h.itemView.setOnClickListener(v -> handleTap(v.getContext(), m));

        // Tre prickar öppnar alltid popup (med eller utan tillagning)
        h.btnAdd.setOnClickListener(v -> showOptionsDialog(v.getContext(), m));
    }

    /** Tryck på rätten: popup om tillagningsval finns, annars lägg till direkt */
    private void handleTap(Context ctx, MenuItem m) {
        boolean hasExtras = m.hasCookingOptions
                || (m.sides != null && !m.sides.isEmpty())
                || (m.sauces != null && !m.sauces.isEmpty());

        if (hasExtras) {
            showOptionsDialog(ctx, m);
        } else {
            Cart.current().addItem(new OrderItem(m.name, m.price, m.category, defaultSlot, m.id));
            Toast.makeText(ctx, m.name + " tillagd", Toast.LENGTH_SHORT).show();
        }
    }

    /** Popup med tillagningsgrad (om applicable) + notes-fält */
    private void showOptionsDialog(Context ctx, MenuItem m) {
        LinearLayout layout = new LinearLayout(ctx);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setBackgroundColor(SURFACE);
        layout.setPadding(dp(ctx, 20), dp(ctx, 16), dp(ctx, 20), dp(ctx, 8));

        // Tillagningsgrad – bara om hasCookingOptions
        final RadioGroup rg = new RadioGroup(ctx);
        if (m.hasCookingOptions) {
            TextView tvCooking = label(ctx, "Tillagning");
            layout.addView(tvCooking);

            String[] options = {"Rare", "Medium Rare", "Medium", "Done", "Well done"};
            for (int i = 0; i < options.length; i++) {
                RadioButton rb = new RadioButton(ctx);
                rb.setId(View.generateViewId()); // unikt ID så RadioGroup fungerar
                rb.setText(options[i]);
                rb.setTextColor(WHITE);
                rb.setTextSize(14);
                if (i == 2) rb.setChecked(true); // Medium default
                rg.addView(rb);
            }
            layout.addView(rg);

            View sep = new View(ctx);
            sep.setBackgroundColor(0xFF333333);
            LinearLayout.LayoutParams sp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 1);
            sp.setMargins(0, dp(ctx, 12), 0, dp(ctx, 12));
            sep.setLayoutParams(sp);
            layout.addView(sep);
        }

        // Notes – alltid synligt
        layout.addView(label(ctx, "Notes"));
        EditText etNote = new EditText(ctx);
        etNote.setHint("T.ex. utan is, extra sås...");
        etNote.setHintTextColor(GREY);
        etNote.setTextColor(WHITE);
        etNote.setBackgroundColor(0xFF2A2A2A);
        etNote.setPadding(dp(ctx, 12), dp(ctx, 10), dp(ctx, 12), dp(ctx, 10));
        etNote.setInputType(android.text.InputType.TYPE_CLASS_TEXT
                | android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        etNote.setMinHeight(dp(ctx, 72));
        etNote.setGravity(Gravity.TOP | Gravity.START);
        layout.addView(etNote);

        new AlertDialog.Builder(ctx)
                .setTitle(m.name)
                .setView(layout)
                .setPositiveButton("Lägg till", (d, w) -> {
                    OrderItem item = new OrderItem(m.name, m.price, m.category, defaultSlot, m.id);

                    if (m.hasCookingOptions) {
                        int selId = rg.getCheckedRadioButtonId();
                        if (selId != -1)
                            item.cooking = ((RadioButton) rg.findViewById(selId)).getText().toString();
                    }

                    String note = etNote.getText().toString().trim();
                    if (!note.isEmpty()) item.comment = note;

                    Cart.current().addItem(item);
                    Toast.makeText(ctx, m.name + " tillagd", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Avbryt", null)
                .show();
    }

    private TextView label(Context ctx, String text) {
        TextView tv = new TextView(ctx);
        tv.setText(text);
        tv.setTextColor(GOLD);
        tv.setTextSize(12);
        tv.setTypeface(null, android.graphics.Typeface.BOLD);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, dp(ctx, 8), 0, dp(ctx, 6));
        tv.setLayoutParams(lp);
        return tv;
    }

    private int dp(Context ctx, int v) {
        return Math.round(v * ctx.getResources().getDisplayMetrics().density);
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