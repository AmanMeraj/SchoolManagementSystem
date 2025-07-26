package com.school.schoolmanagement.Admin.Exams.AddUpdateMarks;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.school.schoolmanagement.Admin.Model.ExamModel;
import com.school.schoolmanagement.HelperClasses.ExamApiHelper;
import com.school.schoolmanagement.R;
import com.school.schoolmanagement.databinding.ActivityAddUpdateMarksBinding;

import java.util.ArrayList;

public class ActivityAddUpdateMarks extends AppCompatActivity {

    private ActivityAddUpdateMarksBinding binding;
    private ExamApiHelper examApiHelper;
    private ArrayList<ExamModel.Datum> examList;
    private ArrayList<String> examNames;
    private ArrayAdapter<String> examAdapter;
    private int selectedExamId = -1;
    private ExamModel.Datum selectedExam = null;
    private boolean isDataLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityAddUpdateMarksBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeComponents();
        setupClickListeners();
        fetchExamsData();
    }

    private void initializeComponents() {
        examApiHelper = new ExamApiHelper(this);
        examList = new ArrayList<>();
        examNames = new ArrayList<>();

        // Initialize adapter with empty list
        examAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, examNames);
        binding.edtEndDate.setAdapter(examAdapter);

        // Initially disable the search button
        binding.btnSearch.setEnabled(false);
        binding.btnSearch.setAlpha(0.5f);
    }

    private void setupClickListeners() {
        // Handle exam selection
        binding.edtEndDate.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                handleExamSelection(position);
            }
        });
        binding.toolbar.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Handle search button click
        binding.btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleSearchClick();
            }
        });

        // Handle dropdown click to show options
        binding.edtEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isDataLoaded && !examNames.isEmpty()) {
                    binding.edtEndDate.showDropDown();
                } else if (!isDataLoaded) {
                    Toast.makeText(ActivityAddUpdateMarks.this, "Loading exams...", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ActivityAddUpdateMarks.this, "No exams available", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void fetchExamsData() {
        showLoading(true);

        examApiHelper.fetchAllExams(new ExamApiHelper.ExamListCallback() {

            @Override
            public void onExamSuccess(ArrayList<ExamModel.Datum> examList) {
                runOnUiThread(() -> {
                    handleExamsFetchSuccess(examList);
                });
            }

            @Override
            public void onError(String errorMessage) {
                runOnUiThread(() -> {
                    handleExamsFetchError(errorMessage);
                });
            }

            @Override
            public void onLoading(boolean isLoading) {
                runOnUiThread(() -> {
                    showLoading(isLoading);
                });
            }
        });
    }

    private void handleExamsFetchSuccess(ArrayList<ExamModel.Datum> exams) {
        this.examList = exams;
        this.examNames = examApiHelper.getValidExamNames(exams);

        // Update adapter
        examAdapter.clear();
        examAdapter.addAll(examNames);
        examAdapter.notifyDataSetChanged();

        isDataLoaded = true;
        showLoading(false);

        if (examNames.size()>0) { // More than just "Select Exam"
            Toast.makeText(this, "Exams loaded successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No exams available", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleExamsFetchError(String errorMessage) {
        showLoading(false);
        isDataLoaded = false;
        Toast.makeText(this, "Error loading exams: " + errorMessage, Toast.LENGTH_LONG).show();

        // Add default option for retry
        examNames.clear();
        examNames.add("Select Exam");
        examAdapter.notifyDataSetChanged();
    }

    private void handleExamSelection(int position) {
        if (position == 0) {
            // "Select Exam" option selected
            selectedExamId = -1;
            selectedExam = null;
            binding.btnSearch.setEnabled(false);
            binding.btnSearch.setAlpha(0.5f);
            return;
        }

        // Get selected exam
        selectedExamId = examApiHelper.getValidExamIdByPosition(examList, position);
        selectedExam = examApiHelper.getExamById(examList, selectedExamId);

        if (selectedExam != null) {
            // Enable search button
            binding.btnSearch.setEnabled(true);
            binding.btnSearch.setAlpha(1.0f);

            Toast.makeText(this, "Selected: " + selectedExam.getDisplayName(), Toast.LENGTH_SHORT).show();
        } else {
            // Handle error case
            selectedExamId = -1;
            selectedExam = null;
            binding.btnSearch.setEnabled(false);
            binding.btnSearch.setAlpha(0.5f);
            Toast.makeText(this, "Error selecting exam", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleSearchClick() {
        if (selectedExam == null || selectedExamId == -1) {
            Toast.makeText(this, "Please select an exam first", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create intent and pass exam details
        Intent intent = new Intent(ActivityAddUpdateMarks.this, ActivitySelectClassorStudent.class);

        // FIXED: Pass exam ID as integer consistently
        intent.putExtra("examId", selectedExamId);  // Use consistent key
        intent.putExtra("exam_id", selectedExamId); // Also add alternative key for compatibility
        intent.putExtra("exam_name", selectedExam.getExaminationName());
        intent.putExtra("exam_display_name", selectedExam.getDisplayName());
        Log.d("TAG", "ExamId: "+selectedExamId);

        // You can pass the entire exam object if your ExamModel implements Serializable or Parcelable
        // intent.putExtra("exam_object", selectedExam);

        startActivity(intent);
    }

    private void showLoading(boolean isLoading) {
        if (isLoading) {
            // Show loading state
            binding.edtEndDate.setHint("Loading exams...");
            binding.edtEndDate.setEnabled(false);
        } else {
            // Hide loading state
            binding.edtEndDate.setHint("Select Exam");
            binding.edtEndDate.setEnabled(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Optionally refresh data when returning to this activity
        if (!isDataLoaded) {
            fetchExamsData();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up
        if (examAdapter != null) {
            examAdapter.clear();
        }
        binding = null;
    }
}