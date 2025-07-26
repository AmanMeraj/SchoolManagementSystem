package com.school.schoolmanagement.Students.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.school.schoolmanagement.R;
import com.school.schoolmanagement.Students.Model.StudentDashboardApiResponse2;
import com.school.schoolmanagement.Students.Model.StudentDashboardApiResponse3;
import com.school.schoolmanagement.Students.Model.StudentTestReport;
import com.school.schoolmanagement.databinding.RowClassTestReportBinding;

import java.util.List;

public class AdapterStudentsTsetReport extends RecyclerView.Adapter<AdapterStudentsTsetReport.ViewHolder> {

    private Context context;
    private List<StudentDashboardApiResponse2.ClassTestReport> reportList;

    public AdapterStudentsTsetReport(Context context, List<StudentDashboardApiResponse2.ClassTestReport> reportList) {
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
        StudentDashboardApiResponse2.ClassTestReport report = reportList.get(position);
        holder.binding.tvSubjectName.setText(report.getSubjectName());
        holder.binding.ivSubjectIcon.setImageResource(R.drawable.book2);
        int totalMarks = report.getTotalMarks();
        int obtainedMarks = report.getObtainedMarks();
        int percentage = 0;

        if (totalMarks > 0) {
            percentage = (obtainedMarks * 100) / totalMarks;
        }

        // Set progress and score value with calculated percentage
        holder.binding.progressScore.setProgress(percentage);
        holder.binding.tvScoreValue.setText(percentage + "%");
        holder.binding.tvTestName.setText(report.getTestName());
        holder.binding.tvTestScore1.setText("Total Test Score: (" + report.getTotalMarks() + ")");
        holder.binding.tvTestScore2.setText("Total Score Obtained: (" + report.getObtainedMarks() + ")");
        holder.binding.tvTestScore3.setText("Test Average Score: (" + report.getClassAverageMarks() + ")");
    }

    @Override
    public int getItemCount() {
        return reportList.size();
    }
}

