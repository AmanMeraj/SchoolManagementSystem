package com.school.schoolmanagement.Admin.Exams.EditOrDelete;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.school.schoolmanagement.Admin.Exams.AddExam.ActivityAddNewExamination;
import com.school.schoolmanagement.Admin.Model.ExamModel;
import com.school.schoolmanagement.GlobalViewModel.ViewModel;
import com.school.schoolmanagement.HelperClasses.ExamApiHelper;
import com.school.schoolmanagement.R;
import android.app.ProgressDialog;
import com.school.schoolmanagement.Utils.Utility;
import com.school.schoolmanagement.databinding.ActivityEditOrDeleteExamBinding;

import java.util.ArrayList;

public class ActivityEditOrDeleteExam extends AppCompatActivity {

    private ActivityEditOrDeleteExamBinding binding;
    private ExamApiHelper examApiHelper;
    private ArrayList<ExamModel.Datum> examList;
    private ArrayList<String> examNames;
    private ArrayAdapter<String> examAdapter;
    private int selectedExamId = -1;
    private ExamModel.Datum selectedExam = null;
    private ProgressDialog progressDialog;
    private ViewModel viewModel;
    private Utility utility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityEditOrDeleteExamBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize components
        initializeComponents();

        // Setup UI
        setupUI();

        // Load exams
        loadExams();
    }

    private void initializeComponents() {
        examApiHelper = new ExamApiHelper(this);
        examList = new ArrayList<>();
        examNames = new ArrayList<>();
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        viewModel = new ViewModelProvider(this).get(ViewModel.class);
        utility = new Utility();

        // Initialize with default option
        examNames.add("Select Exam");
        examAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, examNames);
        binding.edtEndDate.setAdapter(examAdapter);
    }

    private void setupUI() {
        // Setup dropdown click listener
        binding.edtEndDate.setOnClickListener(v -> {
            if (examList.isEmpty()) {
                Toast.makeText(this, "No exams available", Toast.LENGTH_SHORT).show();
                return;
            }
            binding.edtEndDate.showDropDown();
        });

        // Setup item selection listener
        binding.edtEndDate.setOnItemClickListener((parent, view, position, id) -> {
            if (position == 0) {
                // "Select Exam" option selected
                selectedExamId = -1;
                selectedExam = null;
                updateButtonStates(false);
            } else {
                // Get selected exam
                selectedExamId = examApiHelper.getExamIdByPosition(examList, position);
                selectedExam = examApiHelper.getExamById(examList, selectedExamId);
                updateButtonStates(true);

                if (selectedExam != null) {
                    Toast.makeText(this, "Selected: " + selectedExam.getDisplayName(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Setup Update button click listener
        binding.btnUpdate.setOnClickListener(v -> handleUpdateClick());

        // Setup Delete button click listener
        binding.btnDelete.setOnClickListener(v -> handleDeleteClick());

        // Initially disable buttons
        updateButtonStates(false);
    }

    private void loadExams() {
        examApiHelper.fetchAllExams(new ExamApiHelper.ExamListCallback() {
            @Override
            public void onSuccess(ArrayList<ExamModel.Datum> examListResponse) {
                examList.clear();
                examList.addAll(examListResponse);

                // Update exam names for dropdown
                examNames.clear();
                examNames.addAll(examApiHelper.getExamNames(examList));
                examAdapter.notifyDataSetChanged();

                Toast.makeText(ActivityEditOrDeleteExam.this,
                        "Loaded " + examList.size() + " exams", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(ActivityEditOrDeleteExam.this,
                        "Error loading exams: " + errorMessage, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onLoading(boolean isLoading) {
                if (isLoading) {
                    progressDialog.setMessage("Loading exams...");
                    progressDialog.show();
                } else {
                    progressDialog.dismiss();
                }
            }
        });
    }

    private void handleUpdateClick() {
        if (selectedExam == null) {
            Toast.makeText(this, "Please select an exam first", Toast.LENGTH_SHORT).show();
            return;
        }

        // Intent to next activity for editing
        Intent intent = new Intent(this, ActivityAddNewExamination.class); // Replace with your actual edit activity

        // Pass exam details to edit activity
        intent.putExtra("exam_id", selectedExam.getExamId());
        intent.putExtra("exam_name", selectedExam.getExaminationName());
        intent.putExtra("start_date", selectedExam.getStartDate());
        intent.putExtra("end_date", selectedExam.getEndDate());
        intent.putExtra("from", "EDIT");

        // If you have a Serializable or Parcelable ExamModel, you can pass the entire object
        // intent.putExtra("selected_exam", selectedExam);

        startActivityForResult(intent, 100); // Request code for update
    }

    private void handleDeleteClick() {
        if (selectedExam == null) {
            Toast.makeText(this, "Please select an exam first", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show confirmation dialog
        new AlertDialog.Builder(this)
                .setTitle("Delete Exam")
                .setMessage("Are you sure you want to delete '" + selectedExam.getDisplayName() + "'?\n\nThis action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> deleteExam())
                .setNegativeButton("Cancel", null)
                .setIcon(R.drawable.delete_sm)
                .show();
    }

    private void deleteExam() {
        if (!utility.isInternetConnected(this)) {
            Toast.makeText(this, "No Internet Connection!", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Deleting exam...");
        progressDialog.show();

        String auth = "Bearer " + utility.pref.getPrefString(this, utility.pref.user_token);

        viewModel.deleteExam(auth, selectedExamId).observe(this, response -> {
            progressDialog.dismiss();

            if (response != null && response.code == 200) {
                Toast.makeText(this, "Exam deleted successfully", Toast.LENGTH_SHORT).show();

                // Reset selection
                selectedExamId = -1;
                selectedExam = null;
                binding.edtEndDate.setText("");
                updateButtonStates(false);

                // Reload exams list
                loadExams();
            } else {
                String errorMessage = response != null && response.message != null
                        ? response.message
                        : "Failed to delete exam";
                Toast.makeText(this, "Error: " + errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateButtonStates(boolean isExamSelected) {
        binding.btnUpdate.setEnabled(isExamSelected);
        binding.btnDelete.setEnabled(isExamSelected);

        // Update button appearance based on state
        float alpha = isExamSelected ? 1.0f : 0.5f;
        binding.btnUpdate.setAlpha(alpha);
        binding.btnDelete.setAlpha(alpha);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK) {
            // Exam was updated successfully, reload the list
            Toast.makeText(this, "Exam updated successfully", Toast.LENGTH_SHORT).show();

            // Reset selection
            selectedExamId = -1;
            selectedExam = null;
            binding.edtEndDate.setText("");
            updateButtonStates(false);

            // Reload exams
            loadExams();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Optionally reload exams when returning to this activity
         loadExams();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}