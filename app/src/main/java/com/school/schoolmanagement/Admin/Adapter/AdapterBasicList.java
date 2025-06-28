package com.school.schoolmanagement.Admin.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.school.schoolmanagement.Admin.Model.BasicList;
import com.school.schoolmanagement.Admin.Model.Student;
import com.school.schoolmanagement.Model.StudentDetails;
import com.school.schoolmanagement.databinding.RowBasicLsitBinding;

import java.util.List;
public class AdapterBasicList extends RecyclerView.Adapter<AdapterBasicList.ViewHolder>{
        private Context context;
        private List<StudentDetails> studentList;

        public AdapterBasicList(Context context, List<StudentDetails> studentList) {
            this.context = context;
            this.studentList = studentList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            RowBasicLsitBinding binding = RowBasicLsitBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            StudentDetails student = studentList.get(position);
            holder.binding.tvSr.setText(String.valueOf(position+1));
            holder.binding.tvId.setText(String.valueOf(student.getStudentId()));
            holder.binding.tvName.setText(student.getStudentName());
            holder.binding.tvClass.setText(student.getClassName());
            holder.binding.tvRemainingFees.setText("$ " + student.getRemainingFees());
            holder.binding.tvPhone.setText(student.getMobileNumber());
        }

        @Override
        public int getItemCount() {
            return studentList.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            private final RowBasicLsitBinding binding;

            public ViewHolder(RowBasicLsitBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }

}
