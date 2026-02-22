package com.antonsskafferi.pos;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.*;

public class DishDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_dish_detail);

        int dishId     = getIntent().getIntExtra("dishId", -1);
        int defaultSlot = getIntent().getIntExtra("defaultSlot", 2); // 2 = Varmrätt om inget skickas

        MenuItem dish = findDish(dishId);
        if (dish == null) { finish(); return; }

        ((TextView) findViewById(R.id.tvDetailDishName)).setText(dish.name);
        ((TextView) findViewById(R.id.tvDetailPrice)).setText(String.format("%.0f kr", dish.price));

        RadioGroup rg = findViewById(R.id.rgCooking);
        if (!dish.hasCookingOptions) {
            rg.setVisibility(View.GONE);
            findViewById(R.id.tvCookingLabel).setVisibility(View.GONE);
        }

        LinearLayout llSides  = findViewById(R.id.llSides);
        LinearLayout llSauces = findViewById(R.id.llSauces);
        if (dish.sides   != null) for (int i = 0; i < dish.sides.size();   i++) addCB(llSides,  dish.sides.get(i),   i == 0);
        if (dish.sauces  != null) for (int i = 0; i < dish.sauces.size();  i++) addCB(llSauces, dish.sauces.get(i),  i == 0);

        EditText etComment = findViewById(R.id.etComment);

        findViewById(R.id.btnAddToOrder).setOnClickListener(v -> {
            // ← FIX: 4-args konstruktor med defaultSlot
            OrderItem item = new OrderItem(dish.name, dish.price, dish.category, defaultSlot);

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

    private void addCB(LinearLayout ll, String text, boolean checked) {
        CheckBox cb = new CheckBox(this);
        cb.setText(text); cb.setTextSize(14); cb.setChecked(checked);
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