package com.antonsskafferi.staff;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CoworkerAdapter extends RecyclerView.Adapter<CoworkerAdapter.ViewHolder> {

    private List<String> names;
    private OnCoworkerClickListener listener;

    public interface OnCoworkerClickListener {
        void onClick(String name);
    }

    public CoworkerAdapter(List<String> names, OnCoworkerClickListener listener) {
        this.names = names;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String name = names.get(position);
        holder.tvName.setText(name);
        holder.itemView.setOnClickListener(v -> listener.onClick(name));
    }

    @Override
    public int getItemCount() {
        return names.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(android.R.id.text1);
        }
    }
}
