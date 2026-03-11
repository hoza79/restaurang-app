package com.antonsskafferi.android_ordertablet;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import android.util.Log;
import com.antonsskafferi.android_ordertablet.net.ApiClient;
import com.antonsskafferi.android_ordertablet.net.MenuDto;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderActivity extends AppCompatActivity {

    private static final String[] CATS  = {"Dryck", "Förrätt", "Varmrätt", "Efterrätt"};
    private static final int[]    SLOTS = {0, 1, 2, 3};

    private TextView tvCartSummary, tvOrderCount;
    private static final String TAG = "OrderActivity";

    @Override
    protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_order);

        int table = Cart.getActiveTable();
        ((TextView) findViewById(R.id.tvTableNumber)).setText("Bord " + table);

        tvOrderCount  = findViewById(R.id.tvOrderCount);
        tvCartSummary = findViewById(R.id.tvCartSummary);
        updateCartBar();

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        ViewPager2 pager = findViewById(R.id.viewPager);
        TabLayout tabLayout = findViewById(R.id.tabLayout);

        loadMenuThenSetupPager(pager, tabLayout);

        // Granska / Kundkorg
        findViewById(R.id.btnReviewOrder).setOnClickListener(v -> {
            if (Cart.current().itemCount() == 0) {
                Toast.makeText(this, "Lägg till rätter först!", Toast.LENGTH_SHORT).show();
                return;
            }
            startActivity(new Intent(this, ReviewOrderActivity.class));
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCartBar();
    }

    private void updateCartBar() {
        Cart.CartSession session = Cart.current();
        int pending = session.pendingCount();
        int total   = session.itemCount();
        tvOrderCount.setText(pending > 0
                ? pending + " osänd" + (pending > 1 ? "a" : "")
                : total + " rätter");
        tvCartSummary.setText("Bord " + Cart.getActiveTable()
                + " - " + total + " rätter - "
                + String.format("%.0f kr", session.total()));
    }

    class Adapter extends FragmentStateAdapter {
        Adapter(FragmentActivity fa) { super(fa); }
        @Override public int getItemCount() { return CATS.length; }
        @Override public Fragment createFragment(int pos) {
            return MenuCategoryFragment.newInstance(CATS[pos], SLOTS[pos]);
        }
    }

    private void loadMenuThenSetupPager(ViewPager2 pager, TabLayout tabLayout) {
        // If already loaded once, don't refetch every time
        if (MenuData.hasApiMenu()) {
            setupPager(pager, tabLayout);
            return;
        }

        Toast.makeText(this, "Laddar meny...", Toast.LENGTH_SHORT).show();

        ApiClient.api().getMenu().enqueue(new Callback<MenuDto>() {
            @Override
            public void onResponse(Call<MenuDto> call, Response<MenuDto> resp) {
                if (resp.isSuccessful() && resp.body() != null) {
                    MenuData.setFromApi(resp.body());
                    Log.d(TAG, "Menu loaded from API: " + MenuData.getAll().size() + " items");
                } else {
                    Log.e(TAG, "Menu request failed: HTTP " + resp.code());
                    Toast.makeText(OrderActivity.this,
                            "Kunde inte hämta meny (" + resp.code() + ")", Toast.LENGTH_SHORT).show();
                }
                setupPager(pager, tabLayout);
            }

            @Override
            public void onFailure(Call<MenuDto> call, Throwable t) {
                Log.e(TAG, "Menu fetch failed", t);
                Toast.makeText(OrderActivity.this,
                        "Ingen kontakt med servern (meny)", Toast.LENGTH_SHORT).show();
                setupPager(pager, tabLayout);
            }
        });
    }

    private void setupPager(ViewPager2 pager, TabLayout tabLayout) {
        pager.setAdapter(new Adapter(this));
        new TabLayoutMediator(tabLayout, pager,
                (tab, pos) -> tab.setText(CATS[pos])
        ).attach();
    }
}
