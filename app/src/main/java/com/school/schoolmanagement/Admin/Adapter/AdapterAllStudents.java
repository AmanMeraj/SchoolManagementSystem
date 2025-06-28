package com.school.schoolmanagement.Admin.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.school.schoolmanagement.Admin.Model.StudentModel;
import com.school.schoolmanagement.Model.StudentDetails;
import com.school.schoolmanagement.databinding.RowAllStudentsBinding;

import java.util.List;

// Adapter for RecyclerView
public class AdapterAllStudents extends RecyclerView.Adapter<AdapterAllStudents.ViewHolder> {
    private Context context;
    private List<StudentDetails> studentList;
    private OnStudentClickListener listener;

    public AdapterAllStudents(Context context, List<StudentDetails> studentList, OnStudentClickListener listener) {
        this.context = context;
        this.studentList = studentList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RowAllStudentsBinding binding = RowAllStudentsBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StudentDetails student = studentList.get(position);
        holder.binding.nameTv.setText(student.getStudentName());
        holder.binding.numberTv.setText(student.getMobileNumber());

        holder.binding.eye.setOnClickListener(v -> listener.onViewClick(student));
        holder.binding.editSm.setOnClickListener(v -> listener.onEditClick(student));
        holder.binding.deleteSm.setOnClickListener(v -> listener.onDeleteClick(student));
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        RowAllStudentsBinding binding;

        public ViewHolder(RowAllStudentsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface OnStudentClickListener {
        void onViewClick(StudentDetails student);
        void onEditClick(StudentDetails student);
        void onDeleteClick(StudentDetails student);
    }
}

