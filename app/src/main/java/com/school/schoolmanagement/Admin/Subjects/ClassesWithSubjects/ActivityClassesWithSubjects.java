package com.school.schoolmanagement.Admin.Subjects.ClassesWithSubjects;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import com.school.schoolmanagement.Admin.Adapter.ClassesWithSubjectsParentAdapter;
import com.school.schoolmanagement.Admin.Model.ClassesWithSubjectParentModel;
import com.school.schoolmanagement.Admin.Model.SubjectModel;
import com.school.schoolmanagement.Admin.Subjects.CreateSubject.ActivityCreateSubjects;
import com.school.schoolmanagement.GlobalViewModel.ViewModel;
import com.school.schoolmanagement.R;
import com.school.schoolmanagement.Utils.Utility;
import com.school.schoolmanagement.databinding.ActivityClassesWithSubjectsBinding;

import java.util.ArrayList;
import java.util.List;

public class ActivityClassesWithSubjects extends Utility {
    private ActivityClassesWithSubjectsBinding binding;
    private List<ClassesWithSubjectParentModel> classList;
    private ClassesWithSubjectsParentAdapter adapter;
    private ViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityClassesWithSubjectsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize empty list
        classList = new ArrayList<>();

        binding.toolbar.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        binding.cardAddClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActivityClassesWithSubjects.this, ActivityCreateSubjects.class);
                startActivity(intent);
            }
        });

        // Initialize adapter with empty list
        adapter =
                new ClassesWithSubjectsParentAdapter(this, classList, new ClassesWithSubjectsParentAdapter.OnEditSubjectsListener() {
                    @Override
                    public void onEditSubjects(int classId, List<SubjectModel> subjects) {
                        // ✅ Handle the data here (e.g., open edit screen or call PATCH API)
                        Log.d("EditSubjects", "Class ID: " + classId);
                        for (SubjectModel subject : subjects) {
                            Log.d("EditSubjects", "Subject: " + subject.getName() + ", Marks: " + subject.getMarks() + ", ID: " + subject.getId());
                        }
                        Intent intent = new Intent(ActivityClassesWithSubjects.this, ActivityCreateSubjects.class);
                        intent.putExtra("is_edit", true);
                        intent.putExtra("class_id", classId);
                        intent.putExtra("class_name", getClassNameFromId(classId)); // optional helper
                        intent.putExtra("subject_list", new ArrayList<>(subjects)); // SubjectModel must implement Serializable
                        startActivity(intent);
                    }
                });

        binding.rcClasses.setAdapter(adapter);

        // Initialize ViewModel
        setupViewModel();

        // Load data from API
        loadClassesWithSubjects();
    }

    private String getClassNameFromId(int classId) {
        for (ClassesWithSubjectParentModel model : classList) {
            if (model.getClassId() == classId) {
                return model.getClassName();
            }
        }
        return "";
    }

    private void setupViewModel() {
        // Create API service
        viewModel = new ViewModelProvider(this).get(ViewModel.class);
    }

    private void loadClassesWithSubjects() {
        // Show loading
        binding.loader.rlLoader.setVisibility(View.VISIBLE);

        // Get auth token from preferences
        String authToken = "Bearer "+pref.getPrefString(this,pref.user_token);

        // Fetch data from API
        viewModel.getClassesWithSubjects(authToken).observe(this, response -> {
            // Hide loading
            binding.loader.rlLoader.setVisibility(View.GONE);

            if (response.isSuccess && response.data != null) {
                // Update UI with data
                updateUI(response.data.getData());
            } else {
                binding.loader.rlLoader.setVisibility(View.GONE);
                // Show error message
                Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI(List<ClassesWithSubjectParentModel> classes) {
        if (classes != null && !classes.isEmpty()) {
            // Clear previous data
            classList.clear();

            // Add new data
            classList.addAll(classes);

            // Notify adapter
            adapter.notifyDataSetChanged();

            // Show recycler view and hide empty state
            binding.rcClasses.setVisibility(View.VISIBLE);

        } else {
            // Show empty state
            binding.rcClasses.setVisibility(View.GONE);

        }
    }
}