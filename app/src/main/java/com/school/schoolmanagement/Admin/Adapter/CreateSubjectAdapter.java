package com.school.schoolmanagement.Admin.Subjects.CreateSubject;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.school.schoolmanagement.databinding.RowCreateSubjectsBinding;

import java.util.ArrayList;
import java.util.List;

public class CreateSubjectAdapter extends RecyclerView.Adapter<CreateSubjectAdapter.SubjectViewHolder> {

    private final ArrayList<SubjectItem> subjectsList = new ArrayList<>();

    // Model class to hold subject information
    public static class SubjectItem {
        private String subjectName;
        private String totalMarks;

        public SubjectItem() {
            this.subjectName = "";
            this.totalMarks = "";
        }

        public String getSubjectName() {
            return subjectName;
        }

        public void setSubjectName(String subjectName) {
            this.subjectName = subjectName;
        }

        public String getTotalMarks() {
            return totalMarks;
        }

        public void setTotalMarks(String totalMarks) {
            this.totalMarks = totalMarks;
        }
    }

    // Initialize with one empty row
    public CreateSubjectAdapter() {
        addSubject();
    }

    @NonNull
    @Override
    public SubjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RowCreateSubjectsBinding binding = RowCreateSubjectsBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new SubjectViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SubjectViewHolder holder, int position) {
        SubjectItem item = subjectsList.get(position);

        // Set existing values if any
        holder.binding.edtSubjectName.setText(item.getSubjectName());
        holder.binding.edtTotalExamMarks.setText(item.getTotalMarks());

        // Update data when text changes
        holder.binding.edtSubjectName.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus && position < subjectsList.size()) {
                item.setSubjectName(holder.binding.edtSubjectName.getText().toString());
            }
        });

        holder.binding.edtTotalExamMarks.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus && position < subjectsList.size()) {
                item.setTotalMarks(holder.binding.edtTotalExamMarks.getText().toString());
            }
        });
    }

    @Override
    public int getItemCount() {
        return subjectsList.size();
    }

    // Function to add a new subject row
    public void addSubject() {
        subjectsList.add(new SubjectItem());
        notifyItemInserted(subjectsList.size() - 1);
    }

    // Function to remove the last subject row - fixed to avoid crashes
    public void removeLastSubject() {
        if (subjectsList.size() > 1) {
            int lastIndex = subjectsList.size() - 1;
            // Remove the item
            subjectsList.remove(lastIndex);

            // Notify with the correct method
            notifyDataSetChanged(); // Using this instead of notifyItemRemoved to avoid animation issues
        }
    }

    // Function to get all subject data
    public List<SubjectItem> getAllSubjects() {
        return new ArrayList<>(subjectsList);
    }

    // Clear all subjects
    public void clearSubjects() {
        int size = subjectsList.size();
        subjectsList.clear();
        if (size > 0) {
            notifyDataSetChanged(); // More reliable than notifyItemRangeRemoved for complete clearing
        }
    }

    static class SubjectViewHolder extends RecyclerView.ViewHolder {
        private final RowCreateSubjectsBinding binding;

        public SubjectViewHolder(RowCreateSubjectsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}