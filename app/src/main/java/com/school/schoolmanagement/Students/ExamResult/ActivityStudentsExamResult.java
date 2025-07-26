package com.school.schoolmanagement.Students.ExamResult;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.school.schoolmanagement.Admin.Model.ExamModel;
import com.school.schoolmanagement.GlobalViewModel.ViewModel;
import com.school.schoolmanagement.HelperClasses.ExamApiHelper;
import com.school.schoolmanagement.HelperClasses.ExamResultPdfGenerator;
import com.school.schoolmanagement.Model.StudentExamListResponse;
import com.school.schoolmanagement.R;
import com.school.schoolmanagement.Students.Model.StudentsExamResultResponse;
import com.school.schoolmanagement.Utils.Utility;
import com.school.schoolmanagement.databinding.ActivityStudentsExamResultBinding;

import java.util.ArrayList;

public class ActivityStudentsExamResult extends Utility {

    private static final String TAG = "StudentsExamResult";

    private ActivityStudentsExamResultBinding binding;
    private ViewModel viewModel;
    private ExamApiHelper examApiHelper;
    private ArrayList<StudentExamListResponse.Datum> examList;
    private ArrayList<String> examNames;
    private ArrayAdapter<String> examAdapter;
    private int selectedExamId = -1;
    private StudentExamListResponse.Datum selectedExam = null;
    private ExamResultPdfGenerator examResultPdfGenerator;
    private StudentsExamResultResponse.Data currentExamResult;
    private ProgressDialog progressDialog;
    private String auth;
    private boolean isDataLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: Starting activity");

        EdgeToEdge.enable(this);
        binding = ActivityStudentsExamResultBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeComponents();
        setupClickListeners();
        fetchExamsData();
        initializeViews();
    }

    private void initializeComponents() {
        viewModel = new ViewModelProvider(this).get(ViewModel.class);
        examApiHelper = new ExamApiHelper(this);
        examList = new ArrayList<>();
        examNames = new ArrayList<>();

        // Add this line
        examResultPdfGenerator = new ExamResultPdfGenerator(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        String token = pref.getPrefString(this,pref.user_token);
        auth = "Bearer " + token;

        // Initialize adapter with empty list
        examAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, examNames);
        binding.edtHomeworkDate.setAdapter(examAdapter);

        // Hide exam result info initially
        binding.addChatCard.setVisibility(View.GONE);

        // Initially disable the search button
        binding.updateBtn.setEnabled(false);
        binding.updateBtn.setAlpha(0.5f);
    }

    private void setupClickListeners() {
        binding.toolbar.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        // Handle exam selection
        binding.edtHomeworkDate.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                handleExamSelection(position);
            }
        });

        // Handle search button click
        binding.updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleSearchClick();
            }
        });

        // Handle print button click
        binding.createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handlePrintClick();
            }
        });

        // Handle dropdown click to show options
        binding.edtHomeworkDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isDataLoaded && !examNames.isEmpty()) {
                    binding.edtHomeworkDate.showDropDown();
                } else if (!isDataLoaded) {
                    Toast.makeText(ActivityStudentsExamResult.this, "Loading exams...", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ActivityStudentsExamResult.this, "No exams available", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void fetchExamsData() {
        showLoading(true);

        examApiHelper.fetchStudentExams(new ExamApiHelper.StudentExamListCallback() {

            @Override
            public void onStudentExamSuccess(ArrayList<StudentExamListResponse.Datum> examList) {
                runOnUiThread(() -> {
                    handleExamsFetchSuccess(examList);
                });
            }

            @Override
            public void onStudentError(String errorMessage) {
                runOnUiThread(() -> {
                    handleExamsFetchError(errorMessage);
                });
            }

            @Override
            public void onStudentLoading(boolean isLoading) {
                runOnUiThread(() -> {
                    showLoading(isLoading);
                });
            }
        });
    }

    private void handleExamsFetchSuccess(ArrayList<StudentExamListResponse.Datum> exams) {
        this.examList = exams;
        this.examNames = examApiHelper.getValidStudentsExamNames(exams);

        // Update adapter
        examAdapter.clear();
        examAdapter.addAll(examNames);
        examAdapter.notifyDataSetChanged();

        isDataLoaded = true;
        showLoading(false);

        if (examNames.size() > 0) {
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
            binding.updateBtn.setEnabled(false);
            binding.updateBtn.setAlpha(0.5f);
            binding.addChatCard.setVisibility(View.GONE);
            return;
        }

        // Get selected exam
        selectedExamId = examApiHelper.getValidStudentExamIdByPosition(examList, position);
        selectedExam = examApiHelper.getStudentExamById(examList, selectedExamId);

        if (selectedExam != null) {
            // Enable search button
            binding.updateBtn.setEnabled(true);
            binding.updateBtn.setAlpha(1.0f);

            Toast.makeText(this, "Selected: " + selectedExam.getExaminationName(), Toast.LENGTH_SHORT).show();
        } else {
            // Handle error case
            selectedExamId = -1;
            selectedExam = null;
            binding.updateBtn.setEnabled(false);
            binding.updateBtn.setAlpha(0.5f);
            binding.addChatCard.setVisibility(View.GONE);
            Toast.makeText(this, "Error selecting exam", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleSearchClick() {
        if (selectedExam == null || selectedExamId == -1) {
            Toast.makeText(this, "Please select an exam first", Toast.LENGTH_SHORT).show();
            return;
        }

        // Fetch exam result
        fetchExamResult();
    }

    private void fetchExamResult() {
        Log.d(TAG, "fetchExamResult: Fetching result for exam ID: " + selectedExamId);

        progressDialog.setMessage("Loading exam result...");
        progressDialog.show();

        viewModel.getStudentExamResult(auth, selectedExamId).observe(this, response -> {
            progressDialog.dismiss();

            Log.d(TAG, "fetchExamResult: Response received. Success: " + (response != null ? response.isSuccess : "null response"));

            if (response != null && response.isSuccess && response.data != null) {
                Log.d(TAG, "fetchExamResult: Successfully received exam result data");
                // Populate exam result data
                populateExamResult(response.data);
                binding.addChatCard.setVisibility(View.VISIBLE);
            } else {
                String errorMessage = response != null && response.message != null
                        ? response.message
                        : "Failed to fetch exam result";
                Log.e(TAG, "fetchExamResult: Error: " + errorMessage);
                Toast.makeText(this, "Error: " + errorMessage, Toast.LENGTH_LONG).show();
                binding.addChatCard.setVisibility(View.GONE);
            }
        });
    }

    private void populateExamResult(StudentsExamResultResponse examResult) {
        Log.d(TAG, "populateExamResult: Populating exam result data");

        // Populate student information
        if (examResult != null) {
            StudentsExamResultResponse.Data student = examResult.getData();

            // Store current exam result for PDF generation
            currentExamResult = student;

            if (student != null) {
                Log.d(TAG, "populateExamResult: Student data - ID: " + student.getId() +
                        ", Name: " + student.getName() + ", Class: " + student.getStudentClass());

                binding.rowId.label.setText("Registration/ID");
                binding.rowId.value.setText(String.valueOf(student.getId()));

                binding.rowClass.label.setText("Class");
                binding.rowClass.value.setText(student.getStudentClass() != null ?
                        student.getStudentClass() : "N/A");

                binding.rowName.label.setText("Name");
                binding.rowName.value.setText(student.getName() != null ?
                        student.getName() : "N/A");

                // Update student name in header
                if (student.getName() != null && !student.getName().isEmpty()) {
                    binding.tvStudentsName.setText(student.getName());
                }

                // Populate exam result information
                binding.rowExamCount.label.setText("Subject Counts");
                binding.rowExamCount.value.setText(String.valueOf(student.getSubjectCount()));

                binding.rowTotalMarks.label.setText("Total Marks");
                binding.rowTotalMarks.value.setText(String.valueOf(student.getTotalMarks()));

                binding.rowObtainedMarks.label.setText("Obtained Marks");
                binding.rowObtainedMarks.value.setText(String.valueOf(student.getObtainedMarks()));

                binding.rowPercentage.label.setText("Percentage");
                binding.rowPercentage.value.setText(String.format("%.1f%%", student.getPercentage()));

                Log.d(TAG, "populateExamResult: All data populated successfully");
            } else {
                Log.e(TAG, "populateExamResult: Student data is null");
            }
        } else {
            Log.e(TAG, "populateExamResult: Exam result is null");
        }
    }

    private void handlePrintClick() {
        Log.d(TAG, "handlePrintClick: Print button clicked");

        if (selectedExam == null) {
            Toast.makeText(this, "Please select and search for an exam result first", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if exam result is visible (meaning search was performed)
        if (binding.addChatCard.getVisibility() != View.VISIBLE) {
            Toast.makeText(this, "Please search for exam result first", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if we have current exam result data
        if (currentExamResult == null) {
            Toast.makeText(this, "No exam result data available for printing", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Show loading dialog
            ProgressDialog printProgressDialog = new ProgressDialog(this);
            printProgressDialog.setMessage("Generating PDF...");
            printProgressDialog.setCancelable(false);
            printProgressDialog.show();

            // Generate PDF in background thread
            new Thread(() -> {
                try {
                    String examName = selectedExam.getExaminationName();
                    examResultPdfGenerator.generateExamResultSlip(currentExamResult, examName);

                    // Dismiss progress dialog on UI thread
                    runOnUiThread(() -> {
                        printProgressDialog.dismiss();
                    });

                } catch (Exception e) {
                    Log.e(TAG, "Error generating PDF: " + e.getMessage());
                    runOnUiThread(() -> {
                        printProgressDialog.dismiss();
                        Toast.makeText(this, "Error generating PDF: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
                }
            }).start();

        } catch (Exception e) {
            Log.e(TAG, "handlePrintClick: Error initiating PDF generation", e);
            Toast.makeText(this, "Error starting PDF generation", Toast.LENGTH_SHORT).show();
        }
    }

    private void showLoading(boolean isLoading) {
        if (isLoading) {
            // Show loading state
            binding.edtHomeworkDate.setHint("Loading exams...");
            binding.edtHomeworkDate.setEnabled(false);
        } else {
            // Hide loading state
            binding.edtHomeworkDate.setHint("Select Exam");
            binding.edtHomeworkDate.setEnabled(true);
        }
    }

    private void initializeViews() {
        Log.d(TAG, "initializeViews: Initializing views");

        // Set student name if available from preferences
        String studentName = pref.getPrefString(this, pref.user_name);
        Log.d(TAG, "initializeViews: Student name from prefs: " + studentName);

        if (studentName != null && !studentName.isEmpty()) {
            binding.tvStudentsName.setText(studentName);
        } else {
            binding.tvStudentsName.setText("Student Name");
        }

        // Load profile image if available
        loadProfileImage();
    }

    private void loadProfileImage() {
        String base64Image = pref.getPrefString(this, "student_profile_image");
        Log.d(TAG, "loadProfileImage: Profile image exists: " + (base64Image != null && !base64Image.isEmpty()));

        if (base64Image != null && !base64Image.isEmpty()) {
            try {
                // Remove data:image/jpeg;base64, prefix if present
                if (base64Image.startsWith("data:image")) {
                    base64Image = base64Image.substring(base64Image.indexOf(",") + 1);
                }

                byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                if (decodedByte != null) {
                    Log.d(TAG, "loadProfileImage: Profile image decoded successfully");
                    // Assuming there's an ImageView in the avatar layout
                    // You might need to find the correct ImageView reference
                    // binding.avatarImageView.setImageBitmap(decodedByte);
                } else {
                    Log.e(TAG, "loadProfileImage: Failed to decode profile image");
                }
            } catch (Exception e) {
                Log.e(TAG, "loadProfileImage: Exception decoding image", e);
                // Keep default avatar image
            }
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
        Log.d(TAG, "onDestroy: Activity being destroyed");
        super.onDestroy();
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        // Clean up
        if (examAdapter != null) {
            examAdapter.clear();
        }
        binding = null;
    }
}