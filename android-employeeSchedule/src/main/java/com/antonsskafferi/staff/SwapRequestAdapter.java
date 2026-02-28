package com.antonsskafferi.staff;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class SwapRequestAdapter extends RecyclerView.Adapter<SwapRequestAdapter.ViewHolder> {

    private List<SwapRequest> requests;
    private OnSwapRequestListener listener;

    public interface OnSwapRequestListener {
        void onAccept(SwapRequest request);
        void onDeny(SwapRequest request);
    }

    public SwapRequestAdapter(List<SwapRequest> requests, OnSwapRequestListener listener) {
        this.requests = requests;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_swap_request, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SwapRequest req = requests.get(position);
        holder.tvSender.setText(req.senderName + " vill byta pass!");
        holder.tvDetails.setText(req.shift.date + " | " + req.shift.time);
        
        holder.btnAccept.setOnClickListener(v -> listener.onAccept(req));
        holder.btnDeny.setOnClickListener(v -> listener.onDeny(req));
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

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
