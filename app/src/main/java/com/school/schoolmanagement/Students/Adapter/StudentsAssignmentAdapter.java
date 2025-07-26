package com.school.schoolmanagement.Students.Adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.school.schoolmanagement.Students.Model.StudentHomeworkResponse;
import com.school.schoolmanagement.databinding.RowAssingmentsBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class StudentsAssignmentAdapter extends RecyclerView.Adapter<StudentsAssignmentAdapter.ViewHolder> {

    private final ArrayList<StudentHomeworkResponse.Datum> list;

    public StudentsAssignmentAdapter(ArrayList<StudentHomeworkResponse.Datum> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public StudentsAssignmentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RowAssingmentsBinding binding = RowAssingmentsBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentsAssignmentAdapter.ViewHolder holder, int position) {
        StudentHomeworkResponse.Datum item = list.get(position);

        holder.binding.tvTeacher.setText("Teacher");
        holder.binding.tvClass.setText("Class");
        holder.binding.tvSubject.setText("Subject");
        holder.binding.tvAssignment.setText("Assignment");

        holder.binding.tvHomeworkDesc.setText(item.getDetails());
        holder.binding.teacherName.setText(item.getTeacherName());
        holder.binding.className.setText(item.getClassName());
        holder.binding.tvDate.setText(getDayNumber(item.getHomeworkDate()));
        holder.binding.tvDay.setText(getDayName(item.getHomeworkDate()));
        holder.binding.subject.setText(item.getSubjectName());
        holder.binding.date.setText(getMonthYear(item.getHomeworkDate()));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        RowAssingmentsBinding binding;

        public ViewHolder(@NonNull RowAssingmentsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    // Helper to get day name from date
    private String getDayName(String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date date = sdf.parse(dateStr);
            return new SimpleDateFormat("EEEE", Locale.getDefault()).format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }
    private String getMonthYear(String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date date = sdf.parse(dateStr);
            return new SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(date); // Example: June 2025
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }


    // Helper to get day number
    private String getDayNumber(String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date date = sdf.parse(dateStr);
            return new SimpleDateFormat("dd", Locale.getDefault()).format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }
}

