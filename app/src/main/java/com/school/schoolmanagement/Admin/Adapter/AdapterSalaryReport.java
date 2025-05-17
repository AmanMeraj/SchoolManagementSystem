package com.school.schoolmanagement.Admin.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.school.schoolmanagement.Admin.Model.FeesReportModel;
import com.school.schoolmanagement.Admin.Model.SalaryReportModel;
import com.school.schoolmanagement.databinding.RowFeesReportBinding;
import com.school.schoolmanagement.databinding.RowSalaryReportBinding;

import java.util.List;

public class AdapterSalaryReport extends RecyclerView.Adapter<AdapterSalaryReport.ViewHolder> {
    private Context context;
    private List<SalaryReportModel> feesReportList;

    public AdapterSalaryReport(Context context, List<SalaryReportModel> feesReportList) {
        this.context = context;
        this.feesReportList = feesReportList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RowSalaryReportBinding binding = RowSalaryReportBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SalaryReportModel model = feesReportList.get(position);

        holder.binding.tvId.setText(model.getId());
        holder.binding.tvName.setText(model.getName());
        holder.binding.tvDate.setText(model.getDueDate());
        holder.binding.tvAmount.setText(model.getAmount());
        holder.binding.tvPaid.setText(model.getStatus());
    }

    @Override
    public int getItemCount() {
        return feesReportList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final RowSalaryReportBinding binding;

        public ViewHolder(@NonNull RowSalaryReportBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
