package com.school.schoolmanagement.Admin.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.school.schoolmanagement.Admin.Model.FeesReportModel;
import com.school.schoolmanagement.databinding.RowFeesReportBinding;

import java.util.ArrayList;
import java.util.List;

public class AdapterFeesReport extends RecyclerView.Adapter<AdapterFeesReport.ViewHolder> {
    private Context context;
    private List<FeesReportModel> feesReportList;
    private List<FeesReportModel> filteredList;

    public AdapterFeesReport(Context context, List<FeesReportModel> feesReportList) {
        this.context = context;
        this.feesReportList = feesReportList;
        this.filteredList = feesReportList; // Use the same reference, not new ArrayList(feesReportList)
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("AdapterFeesReport", "onCreateViewHolder called");
        RowFeesReportBinding binding = RowFeesReportBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public int getItemCount() {
        int count = filteredList.size();
        Log.d("AdapterFeesReport", "getItemCount: " + count);
        return count;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d("AdapterFeesReport", "onBindViewHolder called for position: " + position);
        try {
            FeesReportModel model = filteredList.get(position);
            Log.d("AdapterFeesReport", "Binding data: " + model.getName());

            holder.binding.tvId.setText(model.getId());
            holder.binding.tvName.setText(model.getName());
            holder.binding.tvDueDate.setText(model.getDueDate());
            holder.binding.tvAmount.setText(model.getAmount());
            holder.binding.tvDeposited.setText(model.getDepositedAmount());
            holder.binding.tvDueBalance.setText(model.getDueAmount());

            String status = model.getStatus();
            holder.binding.tvPaid.setText(status);

            if ("Paid".equalsIgnoreCase(status)) {
                holder.binding.tvPaid.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
            } else {
                holder.binding.tvPaid.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
            }

        } catch (Exception e) {
            Log.e("AdapterFeesReport", "Error in onBindViewHolder: " + e.getMessage(), e);
        }
    }

    // Method to update the data
    public void updateData(List<FeesReportModel> newData) {
        this.feesReportList.clear();
        this.feesReportList.addAll(newData);
        this.filteredList.clear();
        this.filteredList.addAll(newData);
        notifyDataSetChanged();
    }

    // Method for filtering
    public void filter(String query) {
        filteredList.clear();

        if (query == null || query.isEmpty()) {
            filteredList.addAll(feesReportList);
        } else {
            String lowerCaseQuery = query.toLowerCase();
            for (FeesReportModel item : feesReportList) {
                if (item.getName().toLowerCase().contains(lowerCaseQuery) ||
                        item.getId().toLowerCase().contains(lowerCaseQuery)) {
                    filteredList.add(item);
                }
            }
        }

        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final RowFeesReportBinding binding;

        public ViewHolder(@NonNull RowFeesReportBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}