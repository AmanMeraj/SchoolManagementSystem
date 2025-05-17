package com.school.schoolmanagement.Admin.Adapter;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.school.schoolmanagement.Admin.Model.SubjectModel;
import com.school.schoolmanagement.databinding.RowCreateSubjectsBinding;

import java.util.ArrayList;
import java.util.List;

public class CreateSubjectAdapter extends RecyclerView.Adapter<CreateSubjectAdapter.SubjectViewHolder> {

    private final ArrayList<SubjectItem> subjectsList = new ArrayList<>();





    // Model class to hold subject information
    public static class SubjectItem {
        private Integer id; // Subject ID (nullable, useful for update)
        private String subjectName;
        private String totalMarks;

        public SubjectItem() {
            this.id = null;
            this.subjectName = "";
            this.totalMarks = "";
        }

        public SubjectItem(Integer id, String subjectName, String totalMarks) {
            this.id = id;
            this.subjectName = subjectName;
            this.totalMarks = totalMarks;
        }

        // Getters and setters
        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
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

        // Remove any existing text watchers to prevent duplicate listeners
        if (holder.subjectNameWatcher != null) {
            holder.binding.edtSubjectName.removeTextChangedListener(holder.subjectNameWatcher);
        }
        if (holder.totalMarksWatcher != null) {
            holder.binding.edtTotalExamMarks.removeTextChangedListener(holder.totalMarksWatcher);
        }

        // Set existing values if any
        holder.binding.edtSubjectName.setText(item.getSubjectName());
        holder.binding.edtTotalExamMarks.setText(item.getTotalMarks());

        // Create and set new text watchers for real-time updates
        holder.subjectNameWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (position < subjectsList.size()) {
                    item.setSubjectName(s.toString());
                }
            }
        };

        holder.totalMarksWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (position < subjectsList.size()) {
                    item.setTotalMarks(s.toString());
                }
            }
        };

        // Add the text watchers
        holder.binding.edtSubjectName.addTextChangedListener(holder.subjectNameWatcher);
        holder.binding.edtTotalExamMarks.addTextChangedListener(holder.totalMarksWatcher);
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

    // Function to remove the last subject row
    public void removeLastSubject() {
        if (subjectsList.size() > 1) {
            int lastIndex = subjectsList.size() - 1;
            // Remove the item
            subjectsList.remove(lastIndex);
            notifyItemRemoved(lastIndex);
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
            notifyDataSetChanged();
        }
    }



    public class SubjectViewHolder extends RecyclerView.ViewHolder {
        private final RowCreateSubjectsBinding binding;
        TextWatcher subjectNameWatcher;
        TextWatcher totalMarksWatcher;

        public SubjectViewHolder(RowCreateSubjectsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        // Method to help focus on total marks field when validation fails
        public void focusOnTotalMarks() {
            binding.edtTotalExamMarks.requestFocus();
        }



    }
    public void setSubjectsFromModel(List<SubjectModel> subjectModels) {
        subjectsList.clear();
        for (SubjectModel model : subjectModels) {
            subjectsList.add(new SubjectItem(model.getId(), model.getName(), String.valueOf(model.getMarks())));
        }
        notifyDataSetChanged();
    }
}