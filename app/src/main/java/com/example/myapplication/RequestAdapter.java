package com.example.myapplication;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.RequestViewHolder> {

    private List<ItemRequest1> requestList;
    private OnRequestActionListener actionListener;

    // Interface to communicate clicks back to your Activity class
    public interface OnRequestActionListener {
        void onApprove(ItemRequest1 request);
        void onDecline(ItemRequest1 request, String remarks);
    }

    public RequestAdapter(List<ItemRequest1> requestList, OnRequestActionListener actionListener) {
        this.requestList = requestList;
        this.actionListener = actionListener;
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_resource_request_card, parent, false);
        return new RequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
        ItemRequest1 request = requestList.get(position);

        // 1. Assign names, units, and quantities cleanly
        holder.textItemName.setText(request.item_name);
        holder.textStatusLabel.setText(request.status != null ? request.status : "PENDING");

        // Formats line to display: "Juan Dela Cruz • Valencia City (5.0 kg)"
        String details = (request.employee_name != null ? request.employee_name : "Unknown Employee")
                + " • " + (request.farm_area != null ? request.farm_area : "No Location")
                + " (" + request.quantity + " " + request.unit + ")";
        holder.textRequestDetails.setText(details);

        holder.textPurpose.setText("Purpose: " + (request.purpose != null ? request.purpose : "N/A"));

        String formattedDate = request.getFormattedDateTime();
        if (holder.textDateTime != null) {
            if (formattedDate != null && !formattedDate.trim().isEmpty()) {
                holder.textDateTime.setText("📅 " + formattedDate);
                holder.textDateTime.setVisibility(View.VISIBLE);
            } else {
                holder.textDateTime.setVisibility(View.GONE);
            }
        }

        // 2. Control visibility based on the request state status
        if ("PENDING".equalsIgnoreCase(request.status) || request.status == null) {
            holder.textStatusLabel.setTextColor(Color.parseColor("#F59E0B")); // Amber Orange
            holder.layoutActionControls.setVisibility(View.VISIBLE);
            holder.textRemarksDisplay.setVisibility(View.GONE);
            holder.inputRemarks.setText(""); // Reset input text box
        } else {
            // Already processed (APPROVED or DECLINED)
            holder.layoutActionControls.setVisibility(View.GONE);
            holder.textRemarksDisplay.setVisibility(View.VISIBLE);

            if ("APPROVED".equalsIgnoreCase(request.status)) {
                holder.textStatusLabel.setTextColor(Color.parseColor("#1A8A29")); // Forest Green
                holder.textRemarksDisplay.setText("Approved ✅");
                holder.textRemarksDisplay.setTextColor(Color.parseColor("#1A8A29"));
            } else {
                holder.textStatusLabel.setTextColor(Color.parseColor("#DC2626")); // Crimson Red
                String remarksStr = request.admin_remarks != null ? request.admin_remarks : "No reasons provided.";
                holder.textRemarksDisplay.setText("Declined Reason: " + remarksStr);
                holder.textRemarksDisplay.setTextColor(Color.parseColor("#DC2626"));
            }
        }

        // 3. Connect buttons to actions
        holder.btnApproveAction.setOnClickListener(v -> {
            if (actionListener != null) actionListener.onApprove(request);
        });

        holder.btnDeclineAction.setOnClickListener(v -> {
            String remarks = holder.inputRemarks.getText().toString().trim();
            if (remarks.isEmpty()) {
                Toast.makeText(v.getContext(), "⚠️ You must enter remarks to decline a request!", Toast.LENGTH_LONG).show();
            } else {
                if (actionListener != null) actionListener.onDecline(request, remarks);
            }
        });
    }

    @Override
    public int getItemCount() {
        return requestList != null ? requestList.size() : 0;
    }

    public void updateData(List<ItemRequest1> newList) {
        this.requestList = newList;
        notifyDataSetChanged();
    }

    static class RequestViewHolder extends RecyclerView.ViewHolder {
        TextView textItemName, textStatusLabel, textRequestDetails, textPurpose, textDateTime, textRemarksDisplay;
        EditText inputRemarks;
        Button btnApproveAction, btnDeclineAction;
        LinearLayout layoutActionControls;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            textItemName = itemView.findViewById(R.id.textItemName);
            textStatusLabel = itemView.findViewById(R.id.textStatusLabel);
            textRequestDetails = itemView.findViewById(R.id.textRequestDetails);
            textPurpose = itemView.findViewById(R.id.textPurpose);
            textDateTime = itemView.findViewById(R.id.textDateTime);
            textRemarksDisplay = itemView.findViewById(R.id.textRemarksDisplay);
            inputRemarks = itemView.findViewById(R.id.inputRemarks);
            btnApproveAction = itemView.findViewById(R.id.btnApproveAction);
            btnDeclineAction = itemView.findViewById(R.id.btnDeclineAction);
            layoutActionControls = itemView.findViewById(R.id.layoutActionControls);
        }
    }
}