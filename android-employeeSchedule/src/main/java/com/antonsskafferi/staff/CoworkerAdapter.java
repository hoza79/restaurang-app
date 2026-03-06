package com.antonsskafferi.staff;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.antonsskafferi.staff.network.EmployeeDto;

import java.util.List;

/**
 * RecyclerView adapter for displaying a list of coworkers.
 */
public class CoworkerAdapter extends RecyclerView.Adapter<CoworkerAdapter.ViewHolder> {

    private final List<EmployeeDto> employees;
    private final OnCoworkerClickListener listener;

    /**
     * Listener interface for handling coworker item clicks
     */
    public interface OnCoworkerClickListener {
        void onClick(EmployeeDto employee);
    }

    public CoworkerAdapter(List<EmployeeDto> employees, OnCoworkerClickListener listener) {
        this.employees = employees;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate a simple list item layout for each employee
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        EmployeeDto employee = employees.get(position);
        String fullName = employee.firstName + " " + employee.lastName;

        // Set employee name
        holder.tvName.setText(fullName);

        // Set click listener for the item
        holder.itemView.setOnClickListener(v -> listener.onClick(employee));
    }

    @Override
    public int getItemCount() {
        return employees.size();
    }

    /**
     * ViewHolder class for coworker item
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        final TextView tvName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(android.R.id.text1);
        }
    }
}