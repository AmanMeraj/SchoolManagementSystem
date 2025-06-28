package com.school.schoolmanagement.Admin.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.school.schoolmanagement.Model.StudentDetails;
import com.school.schoolmanagement.R;

import java.util.ArrayList;
import java.util.List;

public class AdapterPromoteStudents extends RecyclerView.Adapter<AdapterPromoteStudents.StudentViewHolder> {

    private List<StudentDetails> studentList;
    private boolean isSelectAllChecked = false;
    private OnSelectionChangeListener onSelectionChangeListener;

    // Interface for selection change callback
    public interface OnSelectionChangeListener {
        void onSelectionChanged(List<Integer> selectedIds);
    }

    public AdapterPromoteStudents(List<StudentDetails> studentList) {
        this.studentList = studentList;
    }

    public void setOnSelectionChangeListener(OnSelectionChangeListener listener) {
        this.onSelectionChangeListener = listener;
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_promote_studnets, parent, false);
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        StudentDetails student = studentList.get(position);

        // Set student data
        holder.tvStudentId.setText(String.valueOf(student.getStudentId()));
        holder.tvStudentName.setText(student.getStudentName());
        holder.tvStudentClass.setText(student.getClassName());

        // Handle checkbox state - use student's individual selection state
        holder.checkBox.setChecked(student.isSelected());

        // Checkbox selection logic
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            student.setSelected(isChecked);

            // Update select all checkbox state based on individual selections
            updateSelectAllState();

            // Optional: Notify parent activity about selection change
            if (onSelectionChangeListener != null) {
                onSelectionChangeListener.onSelectionChanged(getSelectedStudentIds());
            }
        });
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    public void selectAll(boolean selectAll) {
        isSelectAllChecked = selectAll;

        // Update all students' selection state
        for (StudentDetails student : studentList) {
            student.setSelected(selectAll);
        }

        notifyDataSetChanged();

        // Notify parent activity about selection change
        if (onSelectionChangeListener != null) {
            onSelectionChangeListener.onSelectionChanged(getSelectedStudentIds());
        }
    }

    // Method to check if all students are selected
    private void updateSelectAllState() {
        boolean allSelected = true;
        for (StudentDetails student : studentList) {
            if (!student.isSelected()) {
                allSelected = false;
                break;
            }
        }
        isSelectAllChecked = allSelected;
    }

    // Method to get selected student IDs
    public List<Integer> getSelectedStudentIds() {
        List<Integer> selectedIds = new ArrayList<>();
        for (StudentDetails student : studentList) {
            if (student.isSelected()) {
                selectedIds.add(student.getStudentId());
            }
        }
        return selectedIds;
    }

    // Method to get selected students count
    public int getSelectedCount() {
        return getSelectedStudentIds().size();
    }

    // Method to check if select all is checked
    public boolean isSelectAllChecked() {
        return isSelectAllChecked;
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