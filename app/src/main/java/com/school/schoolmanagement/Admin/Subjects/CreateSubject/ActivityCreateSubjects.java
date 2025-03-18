package com.school.schoolmanagement.Admin.Subjects.CreateSubject;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.school.schoolmanagement.Admin.Subjects.CreateSubject.CreateSubjectAdapter;

import com.school.schoolmanagement.R;
import com.school.schoolmanagement.databinding.ActivityCreateSubjectsBinding;

import java.util.List;

public class ActivityCreateSubjects extends AppCompatActivity {
    ActivityCreateSubjectsBinding binding;
    private CreateSubjectAdapter createSubjectAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityCreateSubjectsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize RecyclerView and adapter
        initRecyclerView();

        // Set up button listeners
        setupButtonListeners();
    }

    private void initRecyclerView() {
        createSubjectAdapter = new CreateSubjectAdapter();
        binding.rcSubjects.setAdapter(createSubjectAdapter);
        // Disable animation to avoid potential crashes
        binding.rcSubjects.setItemAnimator(null);
    }

    private void setupButtonListeners() {
        // Add more button click listener
        binding.addSubjectBtn.setOnClickListener(v -> {
            try {
                createSubjectAdapter.addSubject();
                // Use post to ensure the RecyclerView has updated before scrolling
                binding.rcSubjects.post(() -> {
                    if (createSubjectAdapter.getItemCount() > 0) {
                        binding.rcSubjects.scrollToPosition(createSubjectAdapter.getItemCount() - 1);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Error adding subject", Toast.LENGTH_SHORT).show();
            }
        });

        // Remove button click listener
        binding.removeBtn.setOnClickListener(v -> {
            try {
                createSubjectAdapter.removeLastSubject();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Error removing subject", Toast.LENGTH_SHORT).show();
            }
        });

        // Create button click listener
        binding.createBtn.setOnClickListener(v -> {
            try {
                validateAndSaveSubjects();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Error processing data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void validateAndSaveSubjects() {
        // Get the class name
        String className = binding.edtClassName.getText().toString().trim();

        if (className.isEmpty()) {
            Toast.makeText(this, "Please enter class name", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get all subjects data
        List<CreateSubjectAdapter.SubjectItem> subjectItems = createSubjectAdapter.getAllSubjects();

        // Validate subject items
        boolean isValid = true;
        for (CreateSubjectAdapter.SubjectItem item : subjectItems) {
            if (item.getSubjectName().isEmpty() || item.getTotalMarks().isEmpty()) {
                isValid = false;
                break;
            }
        }

        if (!isValid) {
            Toast.makeText(this, "Please fill all subject details", Toast.LENGTH_SHORT).show();
            return;
        }

        // Process the data - you can add your API call or database operations here
        saveSubjectsData(className, subjectItems);
    }

    private void saveSubjectsData(String className, List<CreateSubjectAdapter.SubjectItem> subjects) {
        // Implement your data saving logic here
        // This could be a database operation or API call

        // Example: Show success message
        Toast.makeText(this, "Class and subjects created successfully", Toast.LENGTH_SHORT).show();

        // Clear fields after successful creation
        binding.edtClassName.setText("");

        // Reset adapter with a new empty row - safer than clearing and adding
        createSubjectAdapter = new CreateSubjectAdapter();
        binding.rcSubjects.setAdapter(createSubjectAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clear all references
        createSubjectAdapter = null;
        binding = null;
    }
}