package com.school.schoolmanagement.Admin.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.school.schoolmanagement.Admin.Model.FeesReportModel;
import com.school.schoolmanagement.databinding.RowFeesReportBinding;

import java.util.List;

public class AdapterFeesReport extends RecyclerView.Adapter<AdapterFeesReport.ViewHolder> {
    private Context context;
    private List<FeesReportModel> feesReportList;

    public AdapterFeesReport(Context context, List<FeesReportModel> feesReportList) {
        this.context = context;
        this.feesReportList = feesReportList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RowFeesReportBinding binding = RowFeesReportBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FeesReportModel model = feesReportList.get(position);

        holder.binding.tvId.setText(model.getId());
        holder.binding.tvName.setText(model.getName());
        holder.binding.tvDueDate.setText(model.getDueDate());
        holder.binding.tvAmount.setText(model.getAmount());
        holder.binding.tvPaid.setText(model.getStatus());
    }

    @Override
    public int getItemCount() {
        return feesReportList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final RowFeesReportBinding binding;

        public ViewHolder(@NonNull RowFeesReportBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
