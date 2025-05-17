package com.school.schoolmanagement.Admin.Subjects.CreateSubject;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.school.schoolmanagement.Admin.Adapter.CreateSubjectAdapter;
import com.school.schoolmanagement.Admin.Model.ClassData;
import com.school.schoolmanagement.Admin.Model.ClassListResponse;
import com.school.schoolmanagement.Admin.Model.ClassModel;
import com.school.schoolmanagement.Admin.Model.SubjectModel;
import com.school.schoolmanagement.Admin.Model.SubjectUpdateRequest;
import com.school.schoolmanagement.GlobalViewModel.ViewModel;
import com.school.schoolmanagement.R;
import com.school.schoolmanagement.Utils.Utility;
import com.school.schoolmanagement.databinding.ActivityCreateSubjectsBinding;

import java.util.ArrayList;
import java.util.List;


public class ActivityCreateSubjects extends Utility {
    ActivityCreateSubjectsBinding binding;
    private CreateSubjectAdapter createSubjectAdapter;
    private Integer selectedClassId = null;
    private ArrayList<ClassModel.Data> classList = new ArrayList<>();
    private ViewModel viewModel;
    // Add this flag to track edit mode
    private boolean isEditMode = false;
    // List to store original subjects for edit mode
    private ArrayList<SubjectModel> originalSubjects;

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

        // Initialize ViewModel using the global ViewModel
        viewModel = new ViewModelProvider(this).get(ViewModel.class);

        // Initialize RecyclerView and adapter FIRST
        initRecyclerView();

        // THEN check if data was passed for editing
        if (getIntent() != null && getIntent().hasExtra("is_edit")) {
            isEditMode = getIntent().getBooleanExtra("is_edit", false);
            if (isEditMode) {
                selectedClassId = getIntent().getIntExtra("class_id", -1);
                String className = getIntent().getStringExtra("class_name");
                binding.edtClassName.setText(className);
                binding.edtClassName.setEnabled(false); // Disable editing class in edit mode

                // Change button text to "Update" in edit mode
                binding.btnText.setText("Update");

                originalSubjects = (ArrayList<SubjectModel>) getIntent().getSerializableExtra("subject_list");
                if (originalSubjects != null) {
                    createSubjectAdapter.setSubjectsFromModel(originalSubjects);
                }
            }
        }

        // Set up button listeners
        setupButtonListeners();

        // Set up class selection click listener
        setupClassSelectionUI();

        // Load classes data
        loadClassesData();

        binding.toolbar.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void loadClassesData() {
        // Show loading
        binding.loader.rlLoader.setVisibility(View.VISIBLE);

        // Get auth token from preferences
        String authToken = "Bearer " + pref.getPrefString(this, pref.user_token);

        // Fetch classes from API
        viewModel.getAllClasses(authToken).observe(this, response -> {
            // Hide loading
            binding.loader.rlLoader.setVisibility(View.GONE);

            if (response.isSuccess && response.data != null) {
                // Update the class list
                classList = response.data.getData();
            } else {
                // Show error message
                Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupClassSelectionUI() {
        // Make the edit text not focusable so it doesn't show keyboard
        binding.edtClassName.setFocusable(false);

        // Set click listener on the relative layout containing the EditText
        View classNameContainer = (View) binding.edtClassName.getParent();
        classNameContainer.setOnClickListener(v -> {
            // Only show dialog if not in edit mode
            if (!isEditMode) {
                showClassSelectionDialog();
            }
        });

        // Alternative: set click listener directly on EditText
        binding.edtClassName.setOnClickListener(v -> {
            // Only show dialog if not in edit mode
            if (!isEditMode) {
                showClassSelectionDialog();
            }
        });
    }

    private void showClassSelectionDialog() {
        if (classList.isEmpty()) {
            Toast.makeText(this, "Loading classes...", Toast.LENGTH_SHORT).show();
            loadClassesData(); // Try to fetch again
            return;
        }

        // Create string array of class names for the dialog
        String[] classNames = new String[classList.size()];
        for (int i = 0; i < classList.size(); i++) {
            classNames[i] = classList.get(i).getClassName();
        }

        new MaterialAlertDialogBuilder(this)
                .setTitle("Select Class")
                .setItems(classNames, (dialog, which) -> {
                    // Set the selected class name and ID
                    ClassModel.Data selectedClass = classList.get(which);
                    selectedClassId = selectedClass.getClassId();
                    binding.edtClassName.setText(selectedClass.getClassName());
                })
                .setNegativeButton("Cancel", null)
                .show();
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

        // Create/Update button click listener
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
            Toast.makeText(this, "Please select a class", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedClassId == null) {
            Toast.makeText(this, "Please select a valid class", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get all subjects data
        List<CreateSubjectAdapter.SubjectItem> subjectItems = createSubjectAdapter.getAllSubjects();

        // Validate subject items
        if (subjectItems.isEmpty()) {
            Toast.makeText(this, "Please add at least one subject", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check each subject item individually for better error messages
        for (int i = 0; i < subjectItems.size(); i++) {
            CreateSubjectAdapter.SubjectItem item = subjectItems.get(i);

            // Trim whitespace to handle spaces-only input
            String subjectName = item.getSubjectName().trim();
            String totalMarks = item.getTotalMarks().trim();

            // Update the items with trimmed values
            item.setSubjectName(subjectName);
            item.setTotalMarks(totalMarks);

            // Check for empty subject name
            if (subjectName.isEmpty()) {
                Toast.makeText(this, "Subject name cannot be empty (Row " + (i+1) + ")", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check for empty total marks
            if (totalMarks.isEmpty()) {
                Toast.makeText(this, "Total marks cannot be empty (Row " + (i+1) + ")", Toast.LENGTH_SHORT).show();

                // Try to scroll to and focus on the problematic field
                binding.rcSubjects.scrollToPosition(i);

                // Optional: Try to focus on the totalMarks EditText
                try {
                    RecyclerView.ViewHolder viewHolder = binding.rcSubjects.findViewHolderForAdapterPosition(i);
                    if (viewHolder instanceof CreateSubjectAdapter.SubjectViewHolder) {
                        ((CreateSubjectAdapter.SubjectViewHolder) viewHolder).focusOnTotalMarks();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return;
            }

            // Validate that total marks is a valid number
            try {
                int marks = Integer.parseInt(totalMarks);
                // Check if marks is a positive number
                if (marks <= 0) {
                    Toast.makeText(this, "Total marks must be greater than 0 (Row " + (i+1) + ")", Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Total marks must be a valid number (Row " + (i+1) + ")", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // All validations passed, process the data with class ID
        if (isEditMode) {
            updateSubjectsData(selectedClassId, subjectItems);
        } else {
            saveSubjectsData(selectedClassId, className, subjectItems);
        }
    }

    private void saveSubjectsData(Integer classId, String className, List<CreateSubjectAdapter.SubjectItem> subjects) {
        // Show loading indicator
        binding.loader.rlLoader.setVisibility(View.VISIBLE);

        // Get auth token from preferences
        String authToken = "Bearer " + pref.getPrefString(this, pref.user_token);

        // Call API to save subjects for this class
        viewModel.createSubjects(authToken, classId, subjects).observe(this, response -> {
            // Hide loading
            binding.loader.rlLoader.setVisibility(View.GONE);

            if (response.isSuccess) {
                // Show success message
                Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show();

                // Clear fields after successful creation
                binding.edtClassName.setText("");
                selectedClassId = null;

                // Reset adapter with a new empty row - safer than clearing and adding
                createSubjectAdapter = new CreateSubjectAdapter();
                binding.rcSubjects.setAdapter(createSubjectAdapter);

                // Optionally finish the activity
                finish();
            } else {
                // Show error message
                Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show();
                Log.d("TAG", "saveSubjectsData: "+ response.message);
            }
        });
    }

    // New method for updating subjects
    private void updateSubjectsData(Integer classId, List<CreateSubjectAdapter.SubjectItem> subjects) {
        // Show loading indicator
        binding.loader.rlLoader.setVisibility(View.VISIBLE);

        // Get auth token from preferences
        String authToken = "Bearer " + pref.getPrefString(this, pref.user_token);

        // Convert subject items to SubjectUpdateRequest format
        List<SubjectUpdateRequest> updateRequests = new ArrayList<>();

        for (int i = 0; i < subjects.size(); i++) {
            CreateSubjectAdapter.SubjectItem item = subjects.get(i);
            int subjectId = -1;

            // If this is an edited subject from the original list, get its ID
            if (originalSubjects != null && i < originalSubjects.size()) {
                subjectId = originalSubjects.get(i).getId();
            }

            // If we have a valid ID, this is an existing subject being updated
            if (subjectId != -1) {
                updateRequests.add(new SubjectUpdateRequest(
                        subjectId,
                        item.getSubjectName(),
                        Integer.parseInt(item.getTotalMarks())
                ));
            } else {
                // This is a new subject being added during edit mode
                // We'll use ID of -1 to indicate a new subject
                updateRequests.add(new SubjectUpdateRequest(
                        -1,
                        item.getSubjectName(),
                        Integer.parseInt(item.getTotalMarks())
                ));
            }
        }

        // Call API to update subjects for this class
        viewModel.updateSubjects(authToken, classId, updateRequests).observe(this, response -> {
            // Hide loading
            binding.loader.rlLoader.setVisibility(View.GONE);

            if (response.isSuccess) {
                // Show success message
                Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show();

                // Finish the activity to return to previous screen
                finish();
            } else {
                // Show error message
                Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show();
                Log.d("TAG", "updateSubjectsData: "+ response.message);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clear all references
        createSubjectAdapter = null;
        binding = null;
    }
}