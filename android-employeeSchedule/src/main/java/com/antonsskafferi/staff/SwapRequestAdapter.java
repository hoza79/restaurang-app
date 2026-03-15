package com.antonsskafferi.staff;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.antonsskafferi.staff.network.EmployeeDto;
import com.antonsskafferi.staff.network.ShiftDto;
import com.antonsskafferi.staff.network.SwapRequestDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Adapter for displaying incoming swap requests in a RecyclerView.
 */
public class SwapRequestAdapter extends RecyclerView.Adapter<SwapRequestAdapter.ViewHolder> {

    private List<SwapRequestDto> requests; // List of swap requests
    private final Map<Integer, ShiftDto> shiftMap; // Map of shifts -> ShiftDto
    private final Map<Integer, EmployeeDto> employeeMap; // Map of employees -> EmployeeDto
    private final OnSwapRequestListener listener;    // Callback listener for accept/deny actions

    /**
     * Listener interface for handling accept/deny actions on a swap request.
     */
    public interface OnSwapRequestListener {
        void onAccept(SwapRequestDto request);
        void onDeny(SwapRequestDto request);
    }

    public SwapRequestAdapter(List<SwapRequestDto> requests, Map<Integer, ShiftDto> shiftMap, Map<Integer, EmployeeDto> employeeMap, OnSwapRequestListener listener) {
        this.requests = requests;
        this.shiftMap = shiftMap;
        this.employeeMap = employeeMap;
        this.listener = listener;
    }

    /**
     * Updates the list of swap requests and refreshes the RecyclerView.
     */
    public void setRequests(List<SwapRequestDto> newRequests) {
        this.requests = newRequests;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_swap_request, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SwapRequestDto req = requests.get(position);

        // Display basic info about the swap request
        holder.tvSender.setText("Förfrågan om byte");

        String shiftInfo = "Okänt pass";
        ShiftDto shift = shiftMap.get(req.shiftId);
        if (shift != null) {
            try {
                LocalDateTime start = LocalDateTime.parse(shift.startTime);
                LocalDateTime end = LocalDateTime.parse(shift.endTime);

                DateTimeFormatter dayFmt = DateTimeFormatter.ofPattern("EEEE d MMM", new Locale("sv", "SE"));
                DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm");

                shiftInfo = start.format(dayFmt) + " " + start.format(timeFmt) + " - " + end.format(timeFmt);
            } catch (Exception ignored) {}
        }

        // Show colleague name
        String colleagueName = "Okänd kollega";
        EmployeeDto employee = employeeMap.get(req.senderId); // sender for incoming request
        if (employee != null) {
            colleagueName = employee.firstName + " " + employee.lastName;
        }

        holder.tvSender.setText("Förfrågan om byte");
        String shiftInfoCollName = shiftInfo + " från " + colleagueName;
        holder.tvDetails.setText(shiftInfoCollName);

        holder.btnAccept.setOnClickListener(v -> listener.onAccept(req));
        holder.btnDeny.setOnClickListener(v -> listener.onDeny(req));


        // Setup buttons with callbacks
        holder.btnAccept.setOnClickListener(v -> listener.onAccept(req));
        holder.btnDeny.setOnClickListener(v -> listener.onDeny(req));
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    /**
     * ViewHolder class for swap request item.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvSender, tvDetails;
        Button btnAccept, btnDeny;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSender = itemView.findViewById(R.id.tvRequestSender);
            tvDetails = itemView.findViewById(R.id.tvRequestDetails);
            btnAccept = itemView.findViewById(R.id.btnAcceptSwap);
            btnDeny = itemView.findViewById(R.id.btnDenySwap);
        }
    }
}