package com.school.schoolmanagement.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.school.schoolmanagement.Model.AttendanceEntries;
import com.school.schoolmanagement.databinding.ItemAttendanceRowBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.AttendanceViewHolder> {

    private List<AttendanceEntries> studentList;
    private Context context;
    private int totalDays;

    public AttendanceAdapter(Context context, List<AttendanceEntries> studentList, int totalDays) {
        this.context = context;
        this.studentList = studentList != null ? studentList : new ArrayList<>();
        this.totalDays = totalDays;
    }

    @NonNull
    @Override
    public AttendanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        ItemAttendanceRowBinding binding = ItemAttendanceRowBinding.inflate(inflater, parent, false);
        return new AttendanceViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AttendanceViewHolder holder, int position) {
        AttendanceEntries student = studentList.get(position);

        // Set student basic info
        holder.binding.tvName.setText(student.getStudentName());
        holder.binding.tvRoll.setText(String.valueOf(student.getStudentId()));

        // Set attendance summary
        if (student.getAttendanceSummary() != null) {
            holder.binding.tvP.setText(String.valueOf(student.getAttendanceSummary().getPresentDays()));
            holder.binding.tvL.setText(String.valueOf(student.getAttendanceSummary().getLateDays()));
            holder.binding.tvA.setText(String.valueOf(student.getAttendanceSummary().getAbsentDays()));
            holder.binding.tvH.setText(String.valueOf(student.getAttendanceSummary().getHolidayDays()));
            holder.binding.tvF.setText(String.valueOf(student.getAttendanceSummary().getHalfDays()));
            holder.binding.tvPercent.setText(String.format(Locale.getDefault(), "%.2f%%",
                    student.getAttendanceSummary().getAttendancePercentage()));
        } else {
            // Handle null attendance summary
            holder.binding.tvP.setText("0");
            holder.binding.tvL.setText("0");
            holder.binding.tvA.setText("0");
            holder.binding.tvH.setText("0");
            holder.binding.tvF.setText("0");
            holder.binding.tvPercent.setText("0.00%");
        }

        // Parse compressed daily attendance (e.g., "AAPPLH...")
        List<Character> dailyAttendance = new ArrayList<>();
        String compressedAttendance = student.getDailyAttendanceCompressed();

        if (compressedAttendance != null && !compressedAttendance.isEmpty()) {
            for (char c : compressedAttendance.toCharArray()) {
                dailyAttendance.add(c);
            }
        } else {
            // If no compressed attendance data, fill with default values
            dailyAttendance = new ArrayList<>(Collections.nCopies(totalDays, 'A'));
        }

        // Ensure the list has the correct number of days
        while (dailyAttendance.size() < totalDays) {
            dailyAttendance.add('A'); // Default to absent if missing
        }

        // Trim if somehow we have more days than expected
        if (dailyAttendance.size() > totalDays) {
            dailyAttendance = dailyAttendance.subList(0, totalDays);
        }

        // Setup inner horizontal RecyclerView for daily attendance
        DayStatusAdapter dayStatusAdapter = new DayStatusAdapter(context, dailyAttendance);
        holder.binding.rvDayAttendance.setLayoutManager(
                new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        holder.binding.rvDayAttendance.setAdapter(dayStatusAdapter);
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    // Method to update the adapter data
    public void updateData(List<AttendanceEntries> newStudentList) {
        this.studentList = newStudentList != null ? newStudentList : new ArrayList<>();
        notifyDataSetChanged();
    }
    public List<AttendanceEntries> getStudentList() {
        return studentList;
    }

    // Method to add more data (for pagination)
    public void addData(List<AttendanceEntries> moreStudents) {
        if (moreStudents != null && !moreStudents.isEmpty()) {
            int previousSize = studentList.size();
            studentList.addAll(moreStudents);
            notifyItemRangeInserted(previousSize, moreStudents.size());
        }
    }

    // Method to clear all data
    public void clearData() {
        studentList.clear();
        notifyDataSetChanged();
    }

    static class AttendanceViewHolder extends RecyclerView.ViewHolder {
        ItemAttendanceRowBinding binding;

        public AttendanceViewHolder(ItemAttendanceRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}