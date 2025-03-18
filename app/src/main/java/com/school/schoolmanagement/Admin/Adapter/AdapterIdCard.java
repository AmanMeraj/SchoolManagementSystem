package com.school.schoolmanagement.Admin.Adapter;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;// Change package name accordingly

import com.school.schoolmanagement.Admin.Model.IdCard;
import com.school.schoolmanagement.databinding.RowIdCardBinding;

import java.util.List;

public class AdapterIdCard extends RecyclerView.Adapter<AdapterIdCard.StudentViewHolder> {
    private List<IdCard> students;

    public AdapterIdCard(List<IdCard> students) {
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
            IdCard student = students.get(position);
            holder.binding.name.setText(student.getName());
            holder.binding.id.setText(student.getId());
            holder.binding.classs.setText(student.getClasss());
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


