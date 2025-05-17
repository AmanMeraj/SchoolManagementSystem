package com.school.schoolmanagement.Teachers.AddChapter;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.school.schoolmanagement.R;
import com.school.schoolmanagement.SuperAdmin.Adapter.AdapterAddChapter;
import com.school.schoolmanagement.SuperAdmin.Model.AddChapterModel;
import com.school.schoolmanagement.databinding.ActivityAddNewChapterBinding;

import java.util.ArrayList;
import java.util.List;

public class ActivityAddNewChapter extends AppCompatActivity implements AdapterAddChapter.OnSubjectClickListener{
ActivityAddNewChapterBinding binding;
    private AdapterAddChapter adapter;
    private List<AddChapterModel> subjectList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding=ActivityAddNewChapterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupRecyclerView();
        loadSubjects();

    }

    private void setupRecyclerView() {
        subjectList = new ArrayList<>();
        adapter = new AdapterAddChapter(this, subjectList, this);
        binding.rcRowAttendanceReport.setAdapter(adapter);
    }

    private void loadSubjects() {
        // In a real app, this would come from a database or API
        // For demo purposes, we'll add some sample subjects
        subjectList.clear();
        subjectList.add(new AddChapterModel(1, "Math", "1st"));
        subjectList.add(new AddChapterModel(2, "Biology", "2nd"));
        subjectList.add(new AddChapterModel(3, "Physics", "3rd"));
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onEditClick(AddChapterModel subject, int position) {
        showAddEditSubjectDialog(subject, position);
    }

    @Override
    public void onDeleteClick(AddChapterModel subject, int position) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Subject")
                .setMessage("Are you sure you want to delete " + subject.getName() + "?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    // In a real app, you would delete from database first
                    subjectList.remove(position);
                    adapter.notifyItemRemoved(position);
                    adapter.notifyItemRangeChanged(position, subjectList.size());
                    Toast.makeText(this, subject.getName() + " deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showAddEditSubjectDialog(AddChapterModel subject, int position) {
        // In a real app, this would be a custom dialog with fields for subject name and position
        // For simplicity, this is just a placeholder
        String title = (subject == null) ? "Add Subject" : "Edit Subject";

        Toast.makeText(this, title + " dialog would show here", Toast.LENGTH_SHORT).show();

        // After adding/editing, you would:
        // If adding: subjectList.add(newSubject); adapter.notifyItemInserted(subjectList.size() - 1);
        // If editing: subjectList.set(position, updatedSubject); adapter.notifyItemChanged(position);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}