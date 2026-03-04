package com.antonsskafferi.android_ordertablet;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.*;

public class DishDetailActivity extends AppCompatActivity {

    private static final int BG      = 0xFF121212;
    private static final int SURFACE = 0xFF1E1E1E;
    private static final int GOLD    = 0xFFC9A961;
    private static final int WHITE   = 0xFFEEEEEE;
    private static final int GREY    = 0xFF888888;

    @Override
    protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_dish_detail);

        // Dark mode
        getWindow().getDecorView().setBackgroundColor(BG);
        applyDarkColors();

        int dishId      = getIntent().getIntExtra("dishId", -1);
        int defaultSlot = getIntent().getIntExtra("defaultSlot", 2);

        MenuItem dish = findDish(dishId);
        if (dish == null) { finish(); return; }

        ((TextView) findViewById(R.id.tvDetailDishName)).setText(dish.name);
        ((TextView) findViewById(R.id.tvDetailDishName)).setTextColor(WHITE);
        ((TextView) findViewById(R.id.tvDetailPrice)).setText(String.format("%.0f kr", dish.price));
        ((TextView) findViewById(R.id.tvDetailPrice)).setTextColor(GOLD);

        // Tillagning – bara för rätter med cooking-val
        RadioGroup rg = findViewById(R.id.rgCooking);
        TextView tvCookingLabel = findViewById(R.id.tvCookingLabel);
        if (dish.hasCookingOptions) {
            tvCookingLabel.setTextColor(WHITE);
            styleRadioGroup(rg);
        } else {
            rg.setVisibility(View.GONE);
            tvCookingLabel.setVisibility(View.GONE);
        }

        // Sidorätter & såser – om rätten har dem definierade
        LinearLayout llSides  = findViewById(R.id.llSides);
        LinearLayout llSauces = findViewById(R.id.llSauces);
        TextView tvSidesLabel  = findViewById(R.id.tvSidesLabel);
        TextView tvSaucesLabel = findViewById(R.id.tvSaucesLabel);

        if (dish.sides != null && !dish.sides.isEmpty()) {
            tvSidesLabel.setTextColor(WHITE);
            for (int i = 0; i < dish.sides.size(); i++) addCB(llSides, dish.sides.get(i), i == 0);
        } else {
            llSides.setVisibility(View.GONE);
            tvSidesLabel.setVisibility(View.GONE);
        }

        if (dish.sauces != null && !dish.sauces.isEmpty()) {
            tvSaucesLabel.setTextColor(WHITE);
            for (int i = 0; i < dish.sauces.size(); i++) addCB(llSauces, dish.sauces.get(i), i == 0);
        } else {
            llSauces.setVisibility(View.GONE);
            tvSaucesLabel.setVisibility(View.GONE);
        }

        // Kommentar – alltid synlig för ALLA rätter
        TextView tvCommentLabel = findViewById(R.id.tvCommentLabel);
        tvCommentLabel.setTextColor(WHITE);
        EditText etComment = findViewById(R.id.etComment);
        etComment.setBackgroundColor(SURFACE);
        etComment.setTextColor(WHITE);
        etComment.setHintTextColor(GREY);

        findViewById(R.id.btnAddToOrder).setOnClickListener(v -> {
            // ← FIX: 5-args konstruktor med defaultSlot
            OrderItem item = new OrderItem(dish.name, dish.price, dish.category, defaultSlot, dish.id);

            if (dish.hasCookingOptions) {
                int selId = rg.getCheckedRadioButtonId();
                if (selId != -1) item.cooking = ((RadioButton) rg.findViewById(selId)).getText().toString();
            }

            List<String> chosen = new ArrayList<>();
            addChecked(llSides,  chosen);
            addChecked(llSauces, chosen);
            item.sides   = chosen;
            item.comment = etComment.getText().toString().trim();

            Cart.current().addItem(item);
            Toast.makeText(this, dish.name + " tillagd!", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    /** Sätt mörka färger på alla label-vyer i layouten. */
    private void applyDarkColors() {
        findViewById(R.id.scrollRoot).setBackgroundColor(BG); // om du har en ScrollView-rot
    }

    private void styleRadioGroup(RadioGroup rg) {
        for (int i = 0; i < rg.getChildCount(); i++) {
            View child = rg.getChildAt(i);
            if (child instanceof RadioButton)
                ((RadioButton) child).setTextColor(WHITE);
        }
    }

    private void addCB(LinearLayout ll, String text, boolean checked) {
        CheckBox cb = new CheckBox(this);
        cb.setText(text);
        cb.setTextColor(WHITE);
        cb.setTextSize(14);
        cb.setChecked(checked);
        ll.addView(cb);
    }

    private void addChecked(LinearLayout ll, List<String> out) {
        for (int i = 0; i < ll.getChildCount(); i++) {
            CheckBox cb = (CheckBox) ll.getChildAt(i);
            if (cb.isChecked()) out.add(cb.getText().toString());
        }
    }

    private MenuItem findDish(int id) {
        for (MenuItem m : MenuData.getAll()) if (m.id == id) return m;
        return null;
    }
}