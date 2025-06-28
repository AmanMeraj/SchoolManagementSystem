package com.school.schoolmanagement.Admin.Adapter;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;// Change package name accordingly

import com.school.schoolmanagement.Admin.Model.AllEmployees;
import com.school.schoolmanagement.Admin.Model.IdCard;
import com.school.schoolmanagement.Model.StudentDetails;
import com.school.schoolmanagement.databinding.RowIdCardBinding;

import java.util.List;
public class AdapterStudentsIdCard extends RecyclerView.Adapter<AdapterStudentsIdCard.StudentViewHolder> {
    private List<StudentDetails> students;

    public AdapterStudentsIdCard(List<StudentDetails> students) {
        this.students = students;
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RowIdCardBinding binding = RowIdCardBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new StudentViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        StudentDetails student = students.get(position);
        holder.binding.name.setText(student.getStudentName());
        holder.binding.id.setText(String.valueOf(student.getStudentId()));
        holder.binding.classs.setText(student.getClassName());
        // QR Code remains static
    }

    @Override
    public int getItemCount() {
        return students.size();
    }

    public static class StudentViewHolder extends RecyclerView.ViewHolder {
        RowIdCardBinding binding;

        public StudentViewHolder(RowIdCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}


