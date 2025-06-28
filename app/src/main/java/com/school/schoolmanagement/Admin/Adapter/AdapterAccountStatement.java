package com.school.schoolmanagement.Admin.Adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.school.schoolmanagement.Admin.Model.AccountStatementModel;
import com.school.schoolmanagement.R;
import com.school.schoolmanagement.databinding.RowAccountStatementBinding;

import java.util.ArrayList;
import java.util.List;

public class AdapterAccountStatement extends RecyclerView.Adapter<AdapterAccountStatement.ViewHolder> {

    private List<AccountStatementModel> statementList;
    private List<AccountStatementModel> statementListFiltered; // For search functionality

    public AdapterAccountStatement() {
        this.statementList = new ArrayList<>();
        this.statementListFiltered = new ArrayList<>();
    }

    public void setData(List<AccountStatementModel> statementList) {
        this.statementList = statementList != null ? statementList : new ArrayList<>();
        this.statementListFiltered = new ArrayList<>(this.statementList); // Copy original list
        notifyDataSetChanged();
    }

    // Search/Filter method
    public void filter(String searchText) {
        statementListFiltered.clear();

        if (searchText.isEmpty()) {
            // If search is empty, show all items
            statementListFiltered.addAll(statementList);
        } else {
            String searchLower = searchText.toLowerCase().trim();

            for (AccountStatementModel item : statementList) {
                // Search in all 6 fields
                if (item.getDate().toLowerCase().contains(searchLower) ||
                        item.getStatus().toLowerCase().contains(searchLower) ||
                        item.getAmount1().toLowerCase().contains(searchLower) ||
                        item.getAmount2().toLowerCase().contains(searchLower) ||
                        item.getAmount3().toLowerCase().contains(searchLower) ||
                        item.getPaymentMethod().toLowerCase().contains(searchLower)) {

                    statementListFiltered.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RowAccountStatementBinding binding = RowAccountStatementBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AccountStatementModel item = statementListFiltered.get(position); // Use filtered list
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return statementListFiltered.size(); // Use filtered list size
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final RowAccountStatementBinding binding;

        public ViewHolder(RowAccountStatementBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(AccountStatementModel item) {
            // Set date
            binding.textDate.setText(item.getDate());

            // Set type/status with color coding
            binding.textStatus.setText(item.getStatus());
            if ("Income".equals(item.getStatus())) {
                binding.textStatus.setTextColor(ContextCompat.getColor(itemView.getContext(), android.R.color.holo_green_dark));
            } else if ("Expense".equals(item.getStatus())) {
                binding.textStatus.setTextColor(ContextCompat.getColor(itemView.getContext(), android.R.color.holo_red_dark));
            } else {
                binding.textStatus.setTextColor(ContextCompat.getColor(itemView.getContext(), android.R.color.black));
            }

            // Set amount (now represents the monetary value)
            binding.textAmount1.setText(item.getAmount1());
            if ("Income".equals(item.getStatus())) {
                binding.textAmount2.setTextColor(ContextCompat.getColor(itemView.getContext(), android.R.color.holo_green_dark));
            } else if ("Expense".equals(item.getStatus())) {
                binding.textAmount1.setTextColor(ContextCompat.getColor(itemView.getContext(), android.R.color.holo_red_dark));
            }

            // Set payment method (now in amount2 field)
            binding.textAmount2.setText(item.getAmount2());

            // Set description (now in amount3 field)
            binding.textAmount3.setText(item.getAmount3());
            binding.textPaymentMethod.setText(item.getPaymentMethod());
        }
    }
}