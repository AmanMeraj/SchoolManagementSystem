package com.school.schoolmanagement.Teachers.Adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.school.schoolmanagement.Teachers.Model.AllClassesAttendancereport;
import com.school.schoolmanagement.databinding.RowAllClassAttendanceReportBinding;

import java.util.List;

public class AdapterAllClassesAttendancereport extends RecyclerView.Adapter<AdapterAllClassesAttendancereport.AttendanceViewHolder> {

    private List<AllClassesAttendancereport> attendanceReports;

    public AdapterAllClassesAttendancereport(List<AllClassesAttendancereport> attendanceReports) {
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
        return attendanceReports.size();
    }

    public class AttendanceViewHolder extends RecyclerView.ViewHolder {
        private RowAllClassAttendanceReportBinding binding;

        public AttendanceViewHolder(RowAllClassAttendanceReportBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(AllClassesAttendancereport report) {
            // Set header text
            binding.tvAttendanceHeader.setText("Attendance report " + report.getDate() + " for");
            binding.tvYear.setText(report.getYearName());

            // Set present data
            binding.tvPercentPresent.setText(report.getPresentPercentage() + "%");
            binding.tvCountPresent.setText(String.valueOf(report.getPresentCount()));
            binding.progressPresent.setProgress(report.getPresentPercentage());

            // Set on-leave data
            binding.tvPercentLeave.setText(report.getOnLeavePercentage() + "%");
            binding.tvCountLeave.setText(String.valueOf(report.getOnLeaveCount()));
            binding.progressLeave.setProgress(report.getOnLeavePercentage());

            // Set absent data
            binding.tvPercentAbsent.setText(report.getAbsentPercentage() + "%");
            binding.tvCountAbsent.setText(String.valueOf(report.getAbsentCount()));
            binding.progressAbsent.setProgress(report.getAbsentPercentage());
        }
    }

    public void updateData(List<AllClassesAttendancereport> newData) {
        this.attendanceReports = newData;
        notifyDataSetChanged();
    }
}
