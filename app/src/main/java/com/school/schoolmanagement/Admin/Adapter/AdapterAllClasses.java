package com.school.schoolmanagement.Admin.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.school.schoolmanagement.Admin.Model.ClassModel;
import com.school.schoolmanagement.R;
import com.school.schoolmanagement.databinding.RowAllClassesBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AdapterAllClasses extends RecyclerView.Adapter<AdapterAllClasses.ViewHolder> {
    private List<ClassModel> classList;
    private Context context;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onEditClick(int position);
        void onDeleteClick(int position);
    }

    public AdapterAllClasses(Context context, List<ClassModel> classList, OnItemClickListener listener) {
        this.context = context;
        this.classList = classList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RowAllClassesBinding binding = RowAllClassesBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ClassModel item = classList.get(position);

        // Set class name and total students
        holder.binding.className.setText(item.getClassName());
        holder.binding.totalStudentsNumber.setText(String.valueOf(item.getTotalStudents()));

        // Setup PieCharts with percentage inside the circle
        setupPieChart(holder.binding.chartBoys, item.getBoysPercentage(), context.getResources().getColor(R.color.orange));
        setupPieChart(holder.binding.chartGirls, item.getGirlsPercentage(), context.getResources().getColor(R.color.light_blue));
        setupPieChart(holder.binding.chartNA, item.getNaPercentage(), context.getResources().getColor(R.color.grey));

        // Set count text
        holder.binding.textCountBoys.setText(String.valueOf(item.getBoysCount()));
        holder.binding.textCountGirls.setText(String.valueOf(item.getGirlsCount()));
        holder.binding.textCountNA.setText(String.valueOf(item.getNaCount()));

        // Click Listeners
        holder.binding.editBtn.setOnClickListener(v -> listener.onEditClick(position));
        holder.binding.deleteBtn.setOnClickListener(v -> listener.onDeleteClick(position));
    }

    // Function to configure PieChart


    @Override
    public int getItemCount() {
        return classList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final RowAllClassesBinding binding;

        public ViewHolder(RowAllClassesBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    // Helper function to configure PieChart
    private void setupPieChart(PieChart pieChart, float percentage, int color) {
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleRadius(90f); // White hole inside
        pieChart.setTransparentCircleRadius(90f);
        pieChart.setDrawEntryLabels(false);
        pieChart.setRotationEnabled(false);
        pieChart.getLegend().setEnabled(false);

        pieChart.setCenterText(String.format(Locale.getDefault(), "%.1f%%", percentage));
        pieChart.setCenterTextSize(14f);

        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(percentage, ""));
        entries.add(new PieEntry(100 - percentage, ""));

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setDrawValues(false); // Hide labels on slices

        // Fix: Use integer color directly
        dataSet.setColors(color, Color.parseColor("#E0E0E0"));
        pieChart.setCenterTextColor(color);

        PieData data = new PieData(dataSet);
        pieChart.setData(data);
        pieChart.invalidate(); // Refresh chart
    }



}

