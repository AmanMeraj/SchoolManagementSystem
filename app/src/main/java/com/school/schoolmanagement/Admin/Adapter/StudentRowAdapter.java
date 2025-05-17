package com.school.schoolmanagement.Admin.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.school.schoolmanagement.Admin.Model.StudentInsertMarks;
import com.school.schoolmanagement.Admin.Model.Subject;
import com.school.schoolmanagement.R;

import java.util.List;

public class StudentRowAdapter extends RecyclerView.Adapter<StudentRowAdapter.ViewHolder> implements MarksEntryAdapter.MarksChangedListener {
    private List<StudentInsertMarks> students;
    private List<Subject> subjects;
    private MarksChangedListener listener;

    public interface MarksChangedListener {
        void onMarkChanged(StudentInsertMarks student, String subjectId, String mark);
    }

    public StudentRowAdapter(List<StudentInsertMarks> students, List<Subject> subjects, MarksChangedListener listener) {
        this.students = students;
        this.subjects = subjects;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_insert_marks, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StudentInsertMarks student = students.get(position);

        holder.tvId.setText(student.getId());
        holder.tvName.setText(student.getName());
        holder.tvParent.setText(student.getParentName());

        // Set up marks recycler view
        MarksEntryAdapter marksAdapter = new MarksEntryAdapter(subjects, student, this);
        holder.rcMarks.setAdapter(marksAdapter);
    }

    @Override
    public int getItemCount() {
        return students.size();
    }

    @Override
    public void onMarkChanged(StudentInsertMarks student, String subjectId, String mark) {
        if (listener != null) {
            listener.onMarkChanged(student, subjectId, mark);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvId, tvName, tvParent;
        RecyclerView rcMarks;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvId = itemView.findViewById(R.id.tv_id_row);
            tvName = itemView.findViewById(R.id.tv_s_name_row);
            tvParent = itemView.findViewById(R.id.tv_s_parent_row);
            rcMarks = itemView.findViewById(R.id.rc_edt_marks);

            // Make sure we have a horizontal layout manager
            if (rcMarks.getLayoutManager() == null) {
                rcMarks.setLayoutManager(new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL, false));
            }
        }
    }
}