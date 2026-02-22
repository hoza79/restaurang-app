package com.antonsskafferi.pos;

import android.os.Bundle;
import android.view.*;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

/**
 * Varje flik visar:
 *   1. Flikens egna rätter högst upp (standard)
 *   2. Övriga matkategorier nedanför (för flexibel beställning)
 *
 * Dryck-fliken visar bara dryck.
 * Förrätt-fliken: förrätter → varmrätter → efterrätter
 * Varmrätt-fliken: varmrätter → förrätter → efterrätter
 * Efterrätt-fliken: efterrätter → (inga fler)
 */
public class MenuCategoryFragment extends Fragment {

    private static final String ARG_CAT  = "category";
    private static final String ARG_SLOT = "slot";

    public static MenuCategoryFragment newInstance(String category, int slot) {
        MenuCategoryFragment f = new MenuCategoryFragment();
        Bundle b = new Bundle();
        b.putString(ARG_CAT, category);
        b.putInt(ARG_SLOT, slot);
        f.setArguments(b);
        return f;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inf, ViewGroup c, Bundle s) {
        View v = inf.inflate(R.layout.fragment_menu_category, c, false);

        String category = getArguments() != null ? getArguments().getString(ARG_CAT) : "Varmrätt";
        int slot        = getArguments() != null ? getArguments().getInt(ARG_SLOT, 2) : 2;

        List<MenuItem> sorted = buildSortedList(category);

        RecyclerView rv = v.findViewById(R.id.rvMenuItems);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(new MenuAdapter(sorted, slot));
        return v;
    }

    /**
     * Bygger lista där flikens kategori kommer först,
     * sedan övriga matkategorier (INTE dryck i matflikar).
     */
    private List<MenuItem> buildSortedList(String primaryCategory) {
        List<MenuItem> primary   = MenuData.getByCategory(primaryCategory);
        List<MenuItem> secondary = new ArrayList<>();

        // Dryck-fliken visar bara dryck
        if (primaryCategory.equals("Dryck")) return primary;

        // Övriga matflikar: lägg till de andra matkategorierna
        String[] matKategorier = {"Förrätt", "Varmrätt", "Efterrätt"};
        for (String kat : matKategorier) {
            if (!kat.equals(primaryCategory)) {
                // Lägg till en avdelare + rätterna
                secondary.addAll(MenuData.getByCategory(kat));
            }
        }

        List<MenuItem> result = new ArrayList<>(primary);

        // Avdelare (null-objekt som adaptern ritar som sektion)
        // Enklare: lägg bara till dem direkt, adaptern visar kategorinamn
        result.addAll(secondary);
        return result;
    }
}