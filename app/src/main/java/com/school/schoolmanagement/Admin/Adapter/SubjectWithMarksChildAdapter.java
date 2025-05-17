package com.school.schoolmanagement.Admin.Adapter;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.school.schoolmanagement.Admin.Model.SubjectModel;
import com.school.schoolmanagement.R;

import java.util.ArrayList;
import java.util.List;

public class SubjectWithMarksChildAdapter extends RecyclerView.Adapter<SubjectWithMarksChildAdapter.ViewHolder> {

    private Context context;
    private List<SubjectModel> subjectList;
    private int maxMarks; // Max marks for that class

    public SubjectWithMarksChildAdapter(Context context, List<SubjectModel> subjectList, int maxMarks) {
        this.context = context;
        this.subjectList = subjectList;
        this.maxMarks = maxMarks;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_classes_with_subject_chart, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SubjectModel subject = subjectList.get(position);

        // Set subject name
        holder.tvSubject.setText(subject.getName());

        // Setup Pie Chart with actual marks and max marks
        setupPieChart(holder.pieChart, subject.getMarks(), 100); // Assuming each subject has max 100 marks
    }

    @Override
    public int getItemCount() {
        return subjectList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvSubject;
        PieChart pieChart;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSubject = itemView.findViewById(R.id.tv_subject);
            pieChart = itemView.findViewById(R.id.pieChart);
        }
    }

    // Method to configure Pie Chart dynamically
    private void setupPieChart(PieChart pieChart, int totalMarks, int maxMarks) {
        pieChart.setUsePercentValues(false);
        pieChart.getDescription().setEnabled(false);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleRadius(90f);
        pieChart.setTransparentCircleRadius(90f);
        pieChart.setDrawEntryLabels(false);
        pieChart.setRotationEnabled(false);
        pieChart.getLegend().setEnabled(false);

        // Create a SpannableString to set different colors for the center text
        String centerText = totalMarks + "\nTotal";
        SpannableString spannableString = new SpannableString(centerText);

        // Set totalMarks (first part) in orange
        spannableString.setSpan(
                new ForegroundColorSpan(context.getResources().getColor(R.color.orange)),
                0, String.valueOf(totalMarks).length(), // Apply color to totalMarks only
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        // Set "Total" in grey
        spannableString.setSpan(
                new ForegroundColorSpan(context.getResources().getColor(R.color.grey)),
                String.valueOf(totalMarks).length(), centerText.length(), // Apply color to "Total"
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        // Set center text
        pieChart.setCenterText(spannableString);
        pieChart.setCenterTextSize(14f);

        // Calculate remaining marks
        int remainingMarks = maxMarks - totalMarks;
        if (remainingMarks < 0) remainingMarks = 0; // Avoid negative values

        // Create Pie Entries
        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(totalMarks, "Marks"));  // Marks obtained
        entries.add(new PieEntry(remainingMarks, ""));   // Remaining marks

        // Create Data Set
        PieDataSet dataSet = new PieDataSet(entries, "");
        List<Integer> colors = new ArrayList<>();
        colors.add(context.getResources().getColor(R.color.orange)); // Marks color
        colors.add(context.getResources().getColor(R.color.grey)); // Remaining color
        dataSet.setColors(colors);
        dataSet.setDrawValues(false); // Hide values on chart

        // Set Data
        PieData data = new PieData(dataSet);
        pieChart.setData(data);
        pieChart.invalidate();
    }
}