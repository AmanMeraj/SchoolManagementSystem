package com.school.schoolmanagement.Students.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.school.schoolmanagement.Students.Model.StudentTestReport;
import com.school.schoolmanagement.databinding.RowClassTestReportBinding;

import java.util.List;

public class AdapterStudentsTsetReport extends RecyclerView.Adapter<AdapterStudentsTsetReport.ViewHolder> {

    private Context context;
    private List<StudentTestReport> reportList;

    public AdapterStudentsTsetReport(Context context, List<StudentTestReport> reportList) {
        this.context = context;
        this.reportList = reportList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        RowClassTestReportBinding binding;

        public ViewHolder(RowClassTestReportBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @NonNull
    @Override
    public AdapterStudentsTsetReport.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RowClassTestReportBinding binding = RowClassTestReportBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterStudentsTsetReport.ViewHolder holder, int position) {
        StudentTestReport report = reportList.get(position);
        holder.binding.tvSubjectName.setText(report.getSubjectName());
        holder.binding.ivSubjectIcon.setImageResource(report.getIconResId());
        holder.binding.progressScore.setProgress(report.getProgress());
        holder.binding.tvScoreValue.setText(report.getProgress() + "%");
        holder.binding.tvTestScore1.setText("Total Test Score: (" + report.getTotalScore() + ")");
        holder.binding.tvTestScore2.setText("Total Score Obtained: (" + report.getScoreObtained() + ")");
        holder.binding.tvTestScore3.setText("Test Average Score: (" + report.getAverageScore() + ")");
    }

    @Override
    public int getItemCount() {
        return reportList.size();
    }
}

