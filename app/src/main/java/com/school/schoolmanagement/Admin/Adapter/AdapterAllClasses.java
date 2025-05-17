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

    private List<ClassModel.Data> classList;
    private Context context;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onEditClick(int position);
        void onDeleteClick(int position);
    }

    public AdapterAllClasses(Context context, List<ClassModel.Data> classList, OnItemClickListener listener) {
        this.context = context;
        this.classList = classList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RowAllClassesBinding binding = RowAllClassesBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ClassModel.Data item = classList.get(position);

        // Get the counts
        int boysCount = item.getBoysCount();
        int girlsCount = item.getGirlsCount();
        int naCount = item.getNaCount();
        int totalStudents = item.getTotalStudents();

        // Calculate percentages for boys, girls, and NA
        float boysPercentage = (totalStudents > 0) ? (boysCount / (float) totalStudents) * 100 : 0;
        float girlsPercentage = (totalStudents > 0) ? (girlsCount / (float) totalStudents) * 100 : 0;
        float naPercentage = (totalStudents > 0) ? (naCount / (float) totalStudents) * 100 : 0;

        // Set class name and total students
        holder.binding.className.setText(item.getClassName());
        holder.binding.totalStudentsNumber.setText(String.valueOf(totalStudents));

        // Setup PieCharts with calculated percentages
        setupPieChart(holder.binding.chartBoys, boysPercentage, context.getResources().getColor(R.color.orange));
        setupPieChart(holder.binding.chartGirls, girlsPercentage, context.getResources().getColor(R.color.light_blue));
        setupPieChart(holder.binding.chartNA, naPercentage, context.getResources().getColor(R.color.grey));

        // Set count text for boys, girls, and NA
        holder.binding.textCountBoys.setText(String.valueOf(boysCount));
        holder.binding.textCountGirls.setText(String.valueOf(girlsCount));
        holder.binding.textCountNA.setText(String.valueOf(naCount));

        // Click Listeners
        holder.binding.editBtn.setOnClickListener(v -> listener.onEditClick(position));
        holder.binding.deleteBtn.setOnClickListener(v -> listener.onDeleteClick(position));
    }


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

    private void setupPieChart(PieChart pieChart, float percentage, int color) {
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleRadius(90f);
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
        dataSet.setDrawValues(false);
        dataSet.setColors(color, Color.parseColor("#E0E0E0"));
        pieChart.setCenterTextColor(color);

        PieData data = new PieData(dataSet);
        pieChart.setData(data);
        pieChart.invalidate(); // Refresh chart
    }
}
