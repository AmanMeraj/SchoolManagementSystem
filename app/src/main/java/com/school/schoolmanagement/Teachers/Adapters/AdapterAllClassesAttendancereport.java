package com.school.schoolmanagement.Teachers.Adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.school.schoolmanagement.Admin.Model.ClassWiseAttendanceReport;
import com.school.schoolmanagement.databinding.RowAllClassAttendanceReportBinding;

import java.util.List;

public class AdapterAllClassesAttendancereport extends RecyclerView.Adapter<AdapterAllClassesAttendancereport.AttendanceViewHolder> {

    private List<ClassWiseAttendanceReport.Datum> attendanceReports;

    public AdapterAllClassesAttendancereport(List<ClassWiseAttendanceReport.Datum> attendanceReports) {
        this.attendanceReports = attendanceReports;
    }

    @NonNull
    @Override
    public AttendanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RowAllClassAttendanceReportBinding binding = RowAllClassAttendanceReportBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new AttendanceViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AttendanceViewHolder holder, int position) {
        holder.bind(attendanceReports.get(position));
    }

    @Override
    public int getItemCount() {
        return attendanceReports != null ? attendanceReports.size() : 0;
    }

    public class AttendanceViewHolder extends RecyclerView.ViewHolder {
        private RowAllClassAttendanceReportBinding binding;

        public AttendanceViewHolder(RowAllClassAttendanceReportBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(ClassWiseAttendanceReport.Datum report) {
            // Set header text
            binding.tvAttendanceHeader.setText("Attendance report " + report.getDate() + " for");
            binding.tvYear.setText(report.myclass);
            Log.d("AdapterDebug", "Class: " + report.getMyclass());

            // Calculate percentages
            int total = report.getTotal();
            int presentPercentage = total > 0 ? (report.getPresent() * 100) / total : 0;
            int leavePercentage = total > 0 ? (report.getLeave() * 100) / total : 0;
            int absentPercentage = total > 0 ? (report.getAbsent() * 100) / total : 0;

            // Set present data
            binding.tvPercentPresent.setText(presentPercentage + "%");
            binding.tvCountPresent.setText(String.valueOf(report.getPresent()));
            binding.progressPresent.setProgress(presentPercentage);

            // Set on-leave data
            binding.tvPercentLeave.setText(leavePercentage + "%");
            binding.tvCountLeave.setText(String.valueOf(report.getLeave()));
            binding.progressLeave.setProgress(leavePercentage);

            // Set absent data
            binding.tvPercentAbsent.setText(absentPercentage + "%");
            binding.tvCountAbsent.setText(String.valueOf(report.getAbsent()));
            binding.progressAbsent.setProgress(absentPercentage);
        }
    }

    public void updateData(List<ClassWiseAttendanceReport.Datum> newData) {
        this.attendanceReports = newData;
        notifyDataSetChanged();
    }
}