package com.antonsskafferi.staff;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.antonsskafferi.staff.network.ShiftDto;
import com.antonsskafferi.staff.network.SwapRequestDto;
import com.antonsskafferi.staff.network.SwapStatus;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * RecyclerView adapter for displaying shifts and managing swap requests.
 */
public class ShiftAdapter extends RecyclerView.Adapter<ShiftAdapter.ViewHolder> {

    private List<ShiftDto> shifts;
    private List<SwapRequestDto> outgoingRequests = new ArrayList<>();
    private final OnShiftSwapListener listener;

    // Swedish date/time formatting
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEEE d MMM", new Locale("sv", "SE"));
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    /**
     * Listener interface for shift swap actions
     */
    public interface OnShiftSwapListener {
        void onSwapClick(ShiftDto shift);
        void onRetractClick(SwapRequestDto request);
    }

    public ShiftAdapter(List<ShiftDto> shifts, OnShiftSwapListener listener) {
        this.shifts = shifts;
        this.listener = listener;
    }

    /** Updates the shift list and refreshes UI */
    public void setShifts(List<ShiftDto> newShifts) {
        this.shifts = newShifts;
        notifyDataSetChanged();
    }

    /** Updates the outgoing swap requests and refreshes UI */
    public void setOutgoingRequests(List<SwapRequestDto> requests) {
        this.outgoingRequests = requests;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shift, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ShiftDto shift = shifts.get(position);

        // Format and display shift date and time
        try {
            LocalDateTime start = LocalDateTime.parse(shift.startTime);
            LocalDateTime end = LocalDateTime.parse(shift.endTime);
            holder.tvDate.setText(start.format(dateFormatter));
            String startEndTime = start.format(timeFormatter) + " - " + end.format(timeFormatter);
            holder.tvTime.setText(startEndTime);
        } catch (Exception e) {
            holder.tvDate.setText(shift.startTime);
            holder.tvTime.setText("Tid ej tillgänglig");
        }

        //holder.tvRole.setText("Pass #" + shift.shiftId);

        // Check if there is a pending outgoing swap request for this shift
        SwapRequestDto pendingReq = null;
        for (SwapRequestDto r : outgoingRequests) {
            if (r.shiftId.equals(shift.shiftId) && r.swapStatus == SwapStatus.PENDING) {
                pendingReq = r;
                break;
            }
        }

        // Configure swap button
        if (pendingReq != null) {
            final SwapRequestDto finalReq = pendingReq; // make effectively final
            holder.btnSwap.setText("Ångra");
            holder.btnSwap.setOnClickListener(v ->
                    new AlertDialog.Builder(v.getContext())
                            .setTitle("Ångra förfrågan")
                            .setMessage("Vill du verkligen ångra din bytesförfrågan?")
                            .setPositiveButton("Ja", (dialog, which) -> listener.onRetractClick(finalReq))
                            .setNegativeButton("Nej", null)
                            .show()
            );
        } else {
            holder.btnSwap.setText("Byt pass");
            holder.btnSwap.setOnClickListener(v -> listener.onSwapClick(shift));
        }

        holder.btnSwap.setEnabled(true);
    }

    @Override
    public int getItemCount() {
        return shifts.size();
    }

    /** ViewHolder for shift items */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView tvDate, tvTime, tvRole;
        final Button btnSwap;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvShiftDate);
            tvTime = itemView.findViewById(R.id.tvShiftTime);
            tvRole = itemView.findViewById(R.id.tvShiftRole);
            btnSwap = itemView.findViewById(R.id.btnSwapShift);
        }
    }
}