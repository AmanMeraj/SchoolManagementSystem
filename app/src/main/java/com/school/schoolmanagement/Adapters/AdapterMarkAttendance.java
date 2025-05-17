package com.school.schoolmanagement.Adapters;


import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.school.schoolmanagement.Model.MarkAttendance;
import com.school.schoolmanagement.R;
import com.school.schoolmanagement.databinding.RowMarkAttendanceBinding;

import java.util.List;

public class AdapterMarkAttendance extends RecyclerView.Adapter<AdapterMarkAttendance.AttendanceViewHolder> {

    private final List<MarkAttendance> studentList;
    private final Context context;
    private final OnAttendanceChangeListener listener;

    // Interface for attendance change callback
    public interface OnAttendanceChangeListener {
        void onAttendanceChanged(int position, int status);
    }

    public AdapterMarkAttendance(Context context, List<MarkAttendance> studentList, OnAttendanceChangeListener listener) {
        this.context = context;
        this.studentList = studentList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AttendanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RowMarkAttendanceBinding binding = RowMarkAttendanceBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new AttendanceViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AttendanceViewHolder holder, int position) {
        MarkAttendance student = studentList.get(position);

        // Set student data
        holder.binding.tvStudentId.setText(student.getStudentId());
        holder.binding.tvStudentName.setText(student.getStudentName());

        // Reset all buttons appearance
        resetButtonsAppearance(holder);

        // Highlight the current attendance status
        highlightSelectedStatus(holder, student.getAttendanceStatus());

        // Set click listeners for attendance options
        holder.binding.btnPresent.setOnClickListener(v -> {
            updateAttendanceStatus(position, MarkAttendance.STATUS_PRESENT);
            resetButtonsAppearance(holder);
            highlightSelectedStatus(holder, MarkAttendance.STATUS_PRESENT);
        });

        holder.binding.btnLeave.setOnClickListener(v -> {
            updateAttendanceStatus(position, MarkAttendance.STATUS_LEAVE);
            resetButtonsAppearance(holder);
            highlightSelectedStatus(holder, MarkAttendance.STATUS_LEAVE);
        });

        holder.binding.btnAbsent.setOnClickListener(v -> {
            updateAttendanceStatus(position, MarkAttendance.STATUS_ABSENT);
            resetButtonsAppearance(holder);
            highlightSelectedStatus(holder, MarkAttendance.STATUS_ABSENT);
        });
    }

    private void resetButtonsAppearance(AttendanceViewHolder holder) {
        // Reset Present button
        holder.binding.btnPresent.setBackgroundResource(R.drawable.bg_circle_present);
        holder.binding.btnPresent.setTextColor(ContextCompat.getColor(context, R.color.light_blue));

        // Reset Leave button
        holder.binding.btnLeave.setBackgroundResource(R.drawable.bg_circle_leave);
        holder.binding.btnLeave.setTextColor(ContextCompat.getColor(context, R.color.light_blue));

        // Reset Absent button
        holder.binding.btnAbsent.setBackgroundResource(R.drawable.bg_circle_absent);
        holder.binding.btnAbsent.setTextColor(ContextCompat.getColor(context, R.color.light_red));
    }

    private void highlightSelectedStatus(AttendanceViewHolder holder, int status) {
        switch (status) {
            case MarkAttendance.STATUS_PRESENT:
                highlightButton(holder.binding.btnPresent, R.color.light_blue, R.color.white);
                break;

            case MarkAttendance.STATUS_LEAVE:
                highlightButton(holder.binding.btnLeave, R.color.light_blue, R.color.white);
                break;

            case MarkAttendance.STATUS_ABSENT:
                highlightButton(holder.binding.btnAbsent, R.color.light_red, R.color.white);
                break;

            default:
                // No status selected
                break;
        }
    }

    private void highlightButton(TextView button, int backgroundColor, int textColor) {
        // Set selected state
        button.setBackgroundTintList(ColorStateList.valueOf(
                ContextCompat.getColor(context, backgroundColor)));
        button.setTextColor(ContextCompat.getColor(context, textColor));
    }

    private void updateAttendanceStatus(int position, int status) {
        MarkAttendance student = studentList.get(position);
        int oldStatus = student.getAttendanceStatus();

        // Toggle status if already selected
        if (oldStatus == status) {
            status = MarkAttendance.STATUS_NONE;
        }

        student.setAttendanceStatus(status);
        listener.onAttendanceChanged(position, status);
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    // ViewHolder class
    public static class AttendanceViewHolder extends RecyclerView.ViewHolder {
        private final RowMarkAttendanceBinding binding;

        public AttendanceViewHolder(RowMarkAttendanceBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    // Method to update the list
    public void updateStudentList(List<MarkAttendance> newList) {
        this.studentList.clear();
        this.studentList.addAll(newList);
        notifyDataSetChanged();
    }
}