package com.school.schoolmanagement.Admin.Adapter;
import android.content.Context;
import android.graphics.Color;
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
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.school.schoolmanagement.Admin.Model.ClassesWithSubjectParentModel;
import com.school.schoolmanagement.Admin.Model.DummyDataGenerator;
import com.school.schoolmanagement.Admin.Model.SubjectModel;
import com.school.schoolmanagement.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ClassesWithSubjectsParentAdapter extends RecyclerView.Adapter<ClassesWithSubjectsParentAdapter.ViewHolder>{

        private final Context context;
        private final List<ClassesWithSubjectParentModel> classList;

        public ClassesWithSubjectsParentAdapter(Context context, List<ClassesWithSubjectParentModel> classList) {
            this.context = context;
            this.classList = classList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.row_all_classes_with_subjects, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            ClassesWithSubjectParentModel classModel = classList.get(position);
            holder.className.setText(classModel.getClassName());
            holder.totalSubjects.setText(String.valueOf(classModel.getTotalSubjects()));

            // Set PieChart
            setupPieChart(holder.pieChart, classModel.getTotalMarks());
            List<SubjectModel> subjectList = DummyDataGenerator.getDummySubjects();
            int maxMarks = DummyDataGenerator.getMaxMarks(); // Dynamically calculated

            SucbjectWithMarksChildAdapter adapter = new SucbjectWithMarksChildAdapter(context, subjectList, maxMarks);
            holder.rcSubjects.setAdapter(adapter);
        }

        @Override
        public int getItemCount() {
            return classList.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            TextView className, totalSubjects;
            PieChart pieChart;
            RecyclerView rcSubjects;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                className = itemView.findViewById(R.id.tv_class_name);
                totalSubjects = itemView.findViewById(R.id.tv_total_subjects);
                pieChart = itemView.findViewById(R.id.pieChart);
                rcSubjects=itemView.findViewById(R.id.rc_subjects);
            }
        }

    private void setupPieChart(PieChart pieChart, int totalMarks) {
        pieChart.setUsePercentValues(false);
        pieChart.getDescription().setEnabled(false);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleRadius(90f);
        pieChart.setTransparentCircleRadius(90f);
        pieChart.setDrawEntryLabels(false);
        pieChart.setRotationEnabled(false);
        pieChart.getLegend().setEnabled(false);

        // Create a SpannableString to set different colors
        String centerText = totalMarks + "\nTotal";
        SpannableString spannableString = new SpannableString(centerText);

        // Set totalMarks to blue
        spannableString.setSpan(
                new ForegroundColorSpan(context.getResources().getColor(R.color.light_blue)),
                0, String.valueOf(totalMarks).length(), // Apply color to totalMarks only
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        // Set "Total" to grey
        spannableString.setSpan(
                new ForegroundColorSpan(context.getResources().getColor(R.color.grey)),
                String.valueOf(totalMarks).length(), centerText.length(), // Apply color to "Total"
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        // Set center text
        pieChart.setCenterText(spannableString);
        pieChart.setCenterTextSize(15f);

        // Always make the pie chart fully filled
        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(100, ""));

        // Create Data Set
        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setDrawValues(false);
        dataSet.setColor(context.getResources().getColor(R.color.light_blue)); // Marks in blue

        // Set Data
        PieData data = new PieData(dataSet);
        pieChart.setData(data);
        pieChart.invalidate();
    }


}



