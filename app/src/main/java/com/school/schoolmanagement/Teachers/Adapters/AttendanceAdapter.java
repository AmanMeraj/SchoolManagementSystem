package com.school.schoolmanagement.Teachers.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

import com.school.schoolmanagement.R;
import com.school.schoolmanagement.Teachers.Model.AttendanceReport;
import com.school.schoolmanagement.databinding.RowStudentsAttendanceReportBinding;

public class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.AttendanceViewHolder> {

    private final List<AttendanceReport> attendanceList;
    private final Context context;

    public AttendanceAdapter(Context context, List<AttendanceReport> attendanceList) {
        this.context = context;
        this.attendanceList = attendanceList;
    }

    @NonNull
    @Override
    public AttendanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RowStudentsAttendanceReportBinding binding = RowStudentsAttendanceReportBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new AttendanceViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AttendanceViewHolder holder, int position) {
        AttendanceReport record = attendanceList.get(position);

        // Set data to views
        holder.binding.tvDateValue.setText(record.getDate());
        holder.binding.tvDayValue.setText(record.getDay());
        holder.binding.tvIdValue.setText(record.getId());
        holder.binding.tvStudentValue.setText(record.getStudentName());
        holder.binding.tvClassValue.setText(record.getClassName());
        holder.binding.tvStatusValue.setText(record.getStatus());

        // Set status color based on attendance status
        if (record.getStatus().equals("P")) {
            holder.binding.tvStatusValue.setTextColor(ContextCompat.getColor(context, R.color.light_green3));
        } else if (record.getStatus().equals("A")) {
            holder.binding.tvStatusValue.setTextColor(ContextCompat.getColor(context, R.color.dark_red));
        } else {
            holder.binding.tvStatusValue.setTextColor(ContextCompat.getColor(context, R.color.light_blue));
        }

        // Add alternating row background for better readability (optional)
        if (position % 2 == 0) {
            holder.binding.linearRow.setBackgroundColor(ContextCompat.getColor(context, R.color.grey));
        } else {
            holder.binding.linearRow.setBackgroundColor(ContextCompat.getColor(context, android.R.color.white));
        }
    }

    @Override
    public int getItemCount() {
        return attendanceList.size();
    }

    public static class AttendanceViewHolder extends RecyclerView.ViewHolder {
        private final RowStudentsAttendanceReportBinding binding;

        public AttendanceViewHolder(RowStudentsAttendanceReportBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}