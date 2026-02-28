package com.antonsskafferi.staff;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ShiftAdapter extends RecyclerView.Adapter<ShiftAdapter.ViewHolder> {

    private List<Shift> shifts;
    private String currentUserName;
    private OnShiftSwapListener listener;

    public interface OnShiftSwapListener {
        void onSwapClick(Shift shift);
    }

    public ShiftAdapter(List<Shift> shifts, String currentUserName, OnShiftSwapListener listener) {
        this.shifts = shifts;
        this.currentUserName = currentUserName;
        this.listener = listener;
    }

    public void setShifts(List<Shift> newShifts) {
        this.shifts = newShifts;
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
        Shift shift = shifts.get(position);
        holder.tvDate.setText(shift.date);
        holder.tvTime.setText(shift.time);
        holder.tvRole.setText(shift.role);

        // Hitta eventuell pågående förfrågan för detta pass
        SwapRequest foundReq = null;
        for (SwapRequest r : SwapRequest.allRequests) {
            if (r.shift.id.equals(shift.id) && "PENDING".equals(r.status)) {
                foundReq = r;
                break;
            }
        }
        
        // Skapa en final referens för användning i lambda
        final SwapRequest pendingReq = foundReq;

        if (pendingReq != null) {
            // Om JAG skickade förfrågan, tillåt att ångra
            if (pendingReq.senderName.equalsIgnoreCase(currentUserName)) {
                holder.btnSwap.setEnabled(true);
                holder.btnSwap.setText("Ångra");
                holder.btnSwap.setOnClickListener(v -> {
                    new AlertDialog.Builder(v.getContext())
                        .setTitle("Ångra förfrågan")
                        .setMessage("Vill du verkligen ångra din bytesförfrågan?")
                        .setPositiveButton("Ja", (dialog, which) -> {
                            SwapRequest.allRequests.remove(pendingReq);
                            notifyItemChanged(holder.getBindingAdapterPosition());
                        })
                        .setNegativeButton("Nej", null)
                        .show();
                });
            } else {
                // Någon annan har skickat en förfrågan på detta pass
                holder.btnSwap.setEnabled(false);
                holder.btnSwap.setText("Upptaget");
            }
        } else {
            // Inget pågående byte, visa knappen som vanligt
            holder.btnSwap.setEnabled(true);
            holder.btnSwap.setText("Byt pass");
            holder.btnSwap.setOnClickListener(v -> listener.onSwapClick(shift));
        }
    }

    @Override
    public int getItemCount() {
        return shifts.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvTime, tvRole;
        Button btnSwap;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvShiftDate);
            tvTime = itemView.findViewById(R.id.tvShiftTime);
            tvRole = itemView.findViewById(R.id.tvShiftRole);
            btnSwap = itemView.findViewById(R.id.btnSwapShift);
        }
    }
}
