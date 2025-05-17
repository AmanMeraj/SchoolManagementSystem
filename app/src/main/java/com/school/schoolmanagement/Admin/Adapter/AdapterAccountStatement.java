package com.school.schoolmanagement.Admin.Adapter;


import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.school.schoolmanagement.Admin.Model.AccountStatementModel;
import com.school.schoolmanagement.databinding.RowAccountStatementBinding;

import java.util.ArrayList;
import java.util.List;

public class AdapterAccountStatement extends RecyclerView.Adapter<AdapterAccountStatement.ViewHolder> {

    private List<AccountStatementModel> statementList;

    public AdapterAccountStatement() {
        this.statementList = new ArrayList<>();
    }

    public void setData(List<AccountStatementModel> statementList) {
        this.statementList = statementList;
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
        AccountStatementModel item = statementList.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return statementList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final RowAccountStatementBinding binding;

        public ViewHolder(RowAccountStatementBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(AccountStatementModel item) {
            binding.textDate.setText(item.getDate());
            binding.textStatus.setText(item.getStatus());
            binding.textAmount1.setText(item.getAmount1());
            binding.textAmount2.setText(item.getAmount2());
            binding.textAmount3.setText(item.getAmount3());
        }
    }
}