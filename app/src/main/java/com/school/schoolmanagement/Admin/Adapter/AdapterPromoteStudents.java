package com.school.schoolmanagement.Admin.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.school.schoolmanagement.Admin.Model.PromoteStudents;
import com.school.schoolmanagement.R;
import java.util.List;

public class AdapterPromoteStudents extends RecyclerView.Adapter<AdapterPromoteStudents.StudentViewHolder>{


        private List<PromoteStudents> studentList;
        private boolean isSelectAllChecked = false;

        public AdapterPromoteStudents(List<PromoteStudents> studentList) {
            this.studentList = studentList;
        }

        @NonNull
        @Override
        public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_promote_studnets, parent, false);
            return new StudentViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
            PromoteStudents student = studentList.get(position);
            holder.tvStudentId.setText(student.getId());
            holder.tvStudentName.setText(student.getName());
            holder.tvStudentClass.setText(student.getStudentClass());

            // Handle checkbox state
            holder.checkBox.setChecked(isSelectAllChecked);

            // Checkbox selection logic (if needed)
            holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                student.setSelected(isChecked);
            });
        }

        @Override
        public int getItemCount() {
            return studentList.size();
        }

        public void selectAll(boolean selectAll) {
            isSelectAllChecked = selectAll;
            for (PromoteStudents student : studentList) {
                student.setSelected(selectAll);
            }
            notifyDataSetChanged();
        }

        public static class StudentViewHolder extends RecyclerView.ViewHolder {
            TextView tvStudentId, tvStudentName, tvStudentClass;
            MaterialCheckBox checkBox;

            public StudentViewHolder(@NonNull View itemView) {
                super(itemView);
                tvStudentId = itemView.findViewById(R.id.tv_student_id);
                tvStudentName = itemView.findViewById(R.id.tv_student_name);
                tvStudentClass = itemView.findViewById(R.id.tv_student_class);
                checkBox = itemView.findViewById(R.id.checkBox);
            }
        }
    }


