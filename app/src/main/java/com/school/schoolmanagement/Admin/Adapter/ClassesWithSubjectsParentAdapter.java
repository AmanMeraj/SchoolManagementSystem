package com.school.schoolmanagement.Admin.Adapter;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.school.schoolmanagement.Admin.Model.ClassesWithSubjectParentModel;
import com.school.schoolmanagement.Admin.Model.SubjectModel;
import com.school.schoolmanagement.R;

import java.util.ArrayList;
import java.util.List;

public class ClassesWithSubjectsParentAdapter extends RecyclerView.Adapter<ClassesWithSubjectsParentAdapter.ViewHolder> {

    private final Context context;
    private final List<ClassesWithSubjectParentModel> classList;
    private final OnEditSubjectsListener listener;

    public ClassesWithSubjectsParentAdapter(Context context, List<ClassesWithSubjectParentModel> classList, OnEditSubjectsListener listener) {
        this.context = context;
        this.classList = classList;
        this.listener = listener;
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

        // Setup Pie Chart
        setupPieChart(holder.pieChart, classModel.getTotalMarks());

        // Setup Subject Adapter
        List<SubjectModel> subjectList = classModel.getSubjects();
        int maxMarks = subjectList.size() * 100;
        SubjectWithMarksChildAdapter adapter = new SubjectWithMarksChildAdapter(context, subjectList, maxMarks);
        holder.rcSubjects.setAdapter(adapter);

        // Edit button logic
        holder.editImage.setOnClickListener(view -> {
            new MaterialAlertDialogBuilder(context)
                    .setTitle("Edit Subjects")
                    .setMessage("Are you sure you want to edit subjects for this class?")
                    .setPositiveButton("Proceed", (dialog, which) -> {
                        if (listener != null) {
                            listener.onEditSubjects(classModel.getClassId(), subjectList);
                        }
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return classList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView className, totalSubjects;
        PieChart pieChart;
        RecyclerView rcSubjects;
        ImageView editImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            editImage = itemView.findViewById(R.id.btn_edit);
            className = itemView.findViewById(R.id.tv_class_name);
            totalSubjects = itemView.findViewById(R.id.tv_total_subjects);
            pieChart = itemView.findViewById(R.id.pieChart);
            rcSubjects = itemView.findViewById(R.id.rc_subjects);
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

        String centerText = totalMarks + "\nTotal";
        SpannableString spannableString = new SpannableString(centerText);
        spannableString.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.light_blue)), 0,
                String.valueOf(totalMarks).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.grey)),
                String.valueOf(totalMarks).length(), centerText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        pieChart.setCenterText(spannableString);
        pieChart.setCenterTextSize(15f);

        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(100, ""));
        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setDrawValues(false);
        dataSet.setColor(context.getResources().getColor(R.color.light_blue));
        PieData data = new PieData(dataSet);
        pieChart.setData(data);
        pieChart.invalidate();
    }

    // Internal Listener Interface
    public interface OnEditSubjectsListener {
        void onEditSubjects(int classId, List<SubjectModel> subjects);
    }
}
