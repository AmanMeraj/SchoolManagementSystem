package com.school.schoolmanagement.Admin.Adapter;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.school.schoolmanagement.Admin.Model.StudentInsertMarks;
import com.school.schoolmanagement.Admin.Model.Subject;
import com.school.schoolmanagement.R;

import java.util.List;

public class MarksEntryAdapter extends RecyclerView.Adapter<MarksEntryAdapter.ViewHolder> {
    private List<Subject> subjects;
    private StudentInsertMarks student;
    private MarksChangedListener listener;

    public interface MarksChangedListener {
        void onMarkChanged(StudentInsertMarks student, String subjectId, String mark);
    }

    public MarksEntryAdapter(List<Subject> subjects, StudentInsertMarks student, MarksChangedListener listener) {
        this.subjects = subjects;
        this.student = student;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_input_edit_text, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Subject subject = subjects.get(position);
        holder.editText.setTag(position);

        // Remove existing text watchers to avoid duplication
        if (holder.textWatcher != null) {
            holder.editText.removeTextChangedListener(holder.textWatcher);
        }

        holder.textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                String newMark = editable.toString();
                if (listener != null) {
                    listener.onMarkChanged(student, subject.getSubjectId(), newMark);
                }
            }
        };

        holder.editText.addTextChangedListener(holder.textWatcher);
    }

    @Override
    public int getItemCount() {
        return subjects.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        EditText editText;
        TextWatcher textWatcher;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            editText = itemView.findViewById(R.id.edt_input_marks);
        }
    }
}
