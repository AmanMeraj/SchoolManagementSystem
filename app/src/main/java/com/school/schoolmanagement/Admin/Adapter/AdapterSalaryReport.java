package com.school.schoolmanagement.Admin.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.school.schoolmanagement.Model.SalaryPaidResponse;
import com.school.schoolmanagement.databinding.RowSalaryReportBinding;

import java.util.List;

public class AdapterSalaryReport extends RecyclerView.Adapter<AdapterSalaryReport.ViewHolder> {
    private Context context;
    private List<SalaryPaidResponse.Datum> salaryDataList; // Changed to use actual response data

    public AdapterSalaryReport(Context context, List<SalaryPaidResponse.Datum> salaryDataList) {
        this.context = context;
        this.salaryDataList = salaryDataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RowSalaryReportBinding binding = RowSalaryReportBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SalaryPaidResponse.Datum salaryData = salaryDataList.get(position);

        // Bind data from SalaryPaidResponse.SalaryData to views
        // Adjust these method calls based on your actual SalaryPaidResponse.SalaryData structure
        holder.binding.tvId.setText(String.valueOf(salaryData.getEmployeeId()));
        holder.binding.tvName.setText(salaryData.getEmployeeName());
        holder.binding.tvDate.setText(salaryData.getDateOfReceiving());
        holder.binding.tvAmount.setText(String.valueOf(salaryData.getTotalAmount()));
        holder.binding.tvPaid.setText(salaryData.getStatus());
    }

    @Override
    public int getItemCount() {
        return salaryDataList != null ? salaryDataList.size() : 0;
    }

    // Method to update the list
    public void updateList(List<SalaryPaidResponse.Datum> newList) {
        this.salaryDataList = newList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final RowSalaryReportBinding binding;

        public ViewHolder(@NonNull RowSalaryReportBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}