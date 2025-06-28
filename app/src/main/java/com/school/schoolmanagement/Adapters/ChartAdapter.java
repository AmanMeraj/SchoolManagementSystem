package com.school.schoolmanagement.Adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.school.schoolmanagement.Admin.Model.AccountGet;
import com.school.schoolmanagement.databinding.RowChartOfAccountBinding;

import java.util.ArrayList;
import java.util.List;

public class ChartAdapter extends RecyclerView.Adapter<ChartAdapter.ChartViewHolder> {

    private List<AccountGet.Datum> chartList;
    private ChartItemClickListener clickListener;

    public ChartAdapter(List<AccountGet.Datum> chartList, ChartItemClickListener clickListener) {
        this.chartList = chartList != null ? chartList : new ArrayList<>();
        this.clickListener = clickListener;
    }

    public class ChartViewHolder extends RecyclerView.ViewHolder {
        private RowChartOfAccountBinding binding;

        public ChartViewHolder(RowChartOfAccountBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(AccountGet.Datum item, int position) {
            // Set data - Fixed: Use the item directly, not item.getData().get(position)
            binding.name.setText(item.getChartName());
            binding.type.setText(item.getChartType());

            // Set click listeners with proper position handling
            binding.editBtn.setOnClickListener(v -> {
                if (clickListener != null) {
                    int adapterPosition = getAdapterPosition();
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        clickListener.onEditClick(item, adapterPosition);
                    }
                }
            });

            binding.deleteBtn.setOnClickListener(v -> {
                if (clickListener != null) {
                    int adapterPosition = getAdapterPosition();
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        clickListener.onDeleteClick(item, adapterPosition);
                    }
                }
            });
        }
    }

    @NonNull
    @Override
    public ChartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RowChartOfAccountBinding binding = RowChartOfAccountBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new ChartViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ChartViewHolder holder, int position) {
        holder.bind(chartList.get(position), position);
    }

    @Override
    public int getItemCount() {
        return chartList.size();
    }

    // Method to update the entire list
    public void updateList(List<AccountGet.Datum> newList) {
        this.chartList.clear();
        if (newList != null) {
            this.chartList.addAll(newList);
        }
        notifyDataSetChanged();
    }

    // Method to add single item
    public void addItem(AccountGet.Datum item) {
        chartList.add(item);
        notifyItemInserted(chartList.size() - 1);
    }

    // Method to remove item
    public void removeItem(int position) {
        if (position >= 0 && position < chartList.size()) {
            chartList.remove(position);
            notifyItemRemoved(position);
        }
    }

    // Method to update single item
    public void updateItem(int position, AccountGet.Datum item) {
        if (position >= 0 && position < chartList.size()) {
            chartList.set(position, item);
            notifyItemChanged(position);
        }
    }

    // Get item at position
    public AccountGet.Datum getItem(int position) {
        if (position >= 0 && position < chartList.size()) {
            return chartList.get(position);
        }
        return null;
    }
}