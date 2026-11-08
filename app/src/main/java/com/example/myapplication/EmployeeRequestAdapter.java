
package com.example.myapplication;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class EmployeeRequestAdapter extends
        RecyclerView.Adapter<EmployeeRequestAdapter.RequestViewHolder> {

    private List<ItemRequest1> requestList;

    public EmployeeRequestAdapter(List<ItemRequest1> requestList) {
        this.requestList = requestList != null
                ? requestList : new ArrayList<>();
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_employee_request, parent, false);

        return new RequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull RequestViewHolder holder, int position) {

        ItemRequest1 request = requestList.get(position);

        holder.textItemName.setText(
                request.item_name != null
                        ? request.item_name : "Unknown Item");

        String details = "Quantity: " + request.quantity
                + " " + (request.unit != null ? request.unit : "");

        holder.textRequestDetails.setText(details);

        holder.textPurpose.setText(
                "Purpose: " + (request.purpose != null
                        ? request.purpose : "N/A"));

        String formattedDate = request.getFormattedDateTime();
        if (holder.textDateTime != null) {
            if (formattedDate != null && !formattedDate.trim().isEmpty()) {
                holder.textDateTime.setText("📅 " + formattedDate);
                holder.textDateTime.setVisibility(View.VISIBLE);
            } else {
                holder.textDateTime.setVisibility(View.GONE);
            }
        }

        String status = request.status != null
                ? request.status.trim().toUpperCase(Locale.ROOT)
                : "PENDING";

        holder.textStatusLabel.setText(status);

        holder.textRemarksDisplay.setVisibility(View.GONE);

        switch (status) {

            case "APPROVED":
                holder.textStatusLabel.setTextColor(
                        Color.parseColor("#16A34A"));

                showRemarks(holder, request.admin_remarks,
                        "Admin Remarks: ");

                break;

            case "DECLINED":
                holder.textStatusLabel.setTextColor(
                        Color.parseColor("#DC2626"));

                showRemarks(holder, request.admin_remarks,
                        "Declined Reason: ");

                break;

            default:
                holder.textStatusLabel.setText("PENDING");

                holder.textStatusLabel.setTextColor(
                        Color.parseColor("#F59E0B"));

                break;
        }
    }

    private void showRemarks(
            RequestViewHolder holder,
            String remarks,
            String label) {

        if (remarks != null && !remarks.trim().isEmpty()) {
            holder.textRemarksDisplay.setText(label + remarks);
            holder.textRemarksDisplay.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    public void updateData(List<ItemRequest1> newList) {
        requestList = newList != null
                ? newList : new ArrayList<>();

        notifyDataSetChanged();
    }

    static class RequestViewHolder extends RecyclerView.ViewHolder {

        TextView textItemName;
        TextView textStatusLabel;
        TextView textRequestDetails;
        TextView textPurpose;
        TextView textDateTime;
        TextView textRemarksDisplay;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);

            textItemName = itemView.findViewById(R.id.textItemName);
            textStatusLabel = itemView.findViewById(R.id.textStatusLabel);
            textRequestDetails = itemView.findViewById(R.id.textRequestDetails);
            textPurpose = itemView.findViewById(R.id.textPurpose);
            textDateTime = itemView.findViewById(R.id.textDateTime);
            textRemarksDisplay = itemView.findViewById(R.id.textRemarksDisplay);
        }
    }
}
