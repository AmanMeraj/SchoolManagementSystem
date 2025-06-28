package com.school.schoolmanagement.Admin.ClassTest;

import static android.widget.Toast.LENGTH_SHORT;

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

import com.school.schoolmanagement.Adapters.AdapterAddOrUpdateTestMarks;
import com.school.schoolmanagement.Admin.Model.ClassModel;
import com.school.schoolmanagement.Admin.Model.GetStudentTest;
import com.school.schoolmanagement.Admin.Model.TestMarkModel;
import com.school.schoolmanagement.GlobalViewModel.ViewModel;
import com.school.schoolmanagement.HelperClasses.StudentHelper;
import com.school.schoolmanagement.HelperClasses.TestApiHelper;
import com.school.schoolmanagement.Model.EmployeeResponse;
import com.school.schoolmanagement.Model.StudentDetails;
import com.school.schoolmanagement.R;
import com.school.schoolmanagement.Utils.Utility;
import com.school.schoolmanagement.databinding.ActivityCreateNewClassTest2Binding;

import java.util.ArrayList;
import java.util.List;

public class ActivityCreateNewClassTest2 extends Utility implements
        StudentHelper.StudentHelperCallback, TestApiHelper.TestApiCallback {

    private ActivityCreateNewClassTest2Binding binding;
    private AdapterAddOrUpdateTestMarks adapter;
    private StudentHelper studentHelper;
    private TestApiHelper testApiHelper;
    private ViewModel viewModel;

    // Intent data
    private int receivedClassId;
    private int receivedSubjectId;
    private String receivedDate;
    private String receivedClassName;
    private String receivedSubjectName;
    private String authToken;

    // Test data
    private GetStudentTest existingTestData;
    private List<TestMarkModel> currentTestMarks;
    private boolean isEditMode = false;
    private int totalMarks = 100; // Default total marks, you can make this configurable

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityCreateNewClassTest2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeComponents();
        setupClickListeners();
        receiveDataFromIntent();
        loadTestData();
    }

    private void initializeComponents() {
        authToken = "Bearer " + pref.getPrefString(this, pref.user_token);
        viewModel = new ViewModelProvider(this).get(ViewModel.class);
        testApiHelper = new TestApiHelper(this);
    }

    private void setupClickListeners() {
        binding.toolbar.backBtn.setOnClickListener(v -> finish());

        // Create/Save button click listener
        binding.btnCreateTest.setOnClickListener(v -> handleSubmit());

        // Edit button click listener
        binding.btnEdit.setOnClickListener(v -> handleEdit());

        // Cancel/Delete button click listener
        binding.btnCancel.setOnClickListener(v -> handleDelete());
    }

    private void receiveDataFromIntent() {
        if (getIntent() != null) {
            receivedClassId = getIntent().getIntExtra(ActivityCreateNewClassTest.EXTRA_CLASS_ID, -1);
            receivedSubjectId = getIntent().getIntExtra(ActivityCreateNewClassTest.EXTRA_SUBJECT_ID, -1);
            receivedDate = getIntent().getStringExtra(ActivityCreateNewClassTest.EXTRA_DATE);
            receivedClassName = getIntent().getStringExtra(ActivityCreateNewClassTest.EXTRA_CLASS_NAME);
            receivedSubjectName = getIntent().getStringExtra(ActivityCreateNewClassTest.EXTRA_SUBJECT_NAME);

            if (receivedClassId == -1 || receivedSubjectId == -1 ||
                    receivedDate == null || receivedClassName == null ||
                    receivedSubjectName == null) {
                showToast("Error: Invalid data received");
                finish();
                return;
            }

            // Update UI with received data
            updateUIWithReceivedData();
        } else {
            showToast("Error: No data received");
            finish();
        }
    }

    private void updateUIWithReceivedData() {
        // Update toolbar title or other UI elements
        // binding.toolbar.title.setText(receivedClassName + " - " + receivedSubjectName);
        // binding.tvTestDate.setText(receivedDate);
    }

    private void loadTestData() {
        // First, try to get existing test marks
        testApiHelper.getTestMarks(receivedClassId, receivedSubjectId, receivedDate, this);
    }

    // TestApiHelper.TestApiCallback implementation
    @Override
    public void onTestMarksLoaded(GetStudentTest testData, List<TestMarkModel> mappedTestMarks) {
        this.existingTestData = testData;
        this.currentTestMarks = mappedTestMarks;
        this.isEditMode = true;

        if (testData.getData() != null) {
            this.totalMarks = testData.getData().getTotalMarks();
        }

        showToast("Existing test marks loaded successfully!");
        binding.edtTotalMark.setText(String.valueOf(testData.data.totalMarks));

        // Set up adapter with existing test marks
        setupAdapterWithTestMarks(mappedTestMarks);

        // Show test summary
        String summary = testApiHelper.getTestMarksSummary(mappedTestMarks, totalMarks);
        // binding.tvTestSummary.setText(summary);
        Log.d("TestSummary", summary);

        // Switch to edit mode UI
        switchToEditMode();
    }

    @Override
    public void onTestMarksSubmitted(EmployeeResponse response) {
        if(response.getMessage()!=null){
            showToast(response.getMessage());
        }
        // You might want to finish the activity or navigate back
        finish();
    }

    @Override
    public void onError(String errorMessage) {
        showToast(errorMessage);

        // If getting test marks failed, load students to create new test
        if (!isEditMode) {
            initStudentHelper();
            // Switch to create mode UI
            switchToCreateMode();
        }
    }

    @Override
    public void onLoading(boolean isLoading) {
        // Show/hide progress indicator
        // binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onLoadingStateChanged(boolean isLoading) {
        // Show/hide loader for student loading
    }

    @Override
    public void onClassesLoaded(ArrayList<ClassModel.Data> classes) {
        // Not used here
    }

    private void initStudentHelper() {
        if (studentHelper == null) {
            studentHelper = new StudentHelper(this, viewModel, authToken, this, this);
        }
        studentHelper.loadStudentsByClass(receivedClassId);
    }

    private void setupAdapterWithTestMarks(List<TestMarkModel> testMarks) {
        if (testMarks != null && !testMarks.isEmpty()) {
            // Use factory method for TestMarkModel list
            adapter = AdapterAddOrUpdateTestMarks.fromTestMarks(this, testMarks);
            binding.rcTestResult.setAdapter(adapter);
        }
    }

    private List<TestMarkModel> convertStudentsToTestMarks(List<StudentDetails> students) {
        List<TestMarkModel> testMarks = new ArrayList<>();

        for (StudentDetails student : students) {
            TestMarkModel testMark = new TestMarkModel();
            testMark.setStudentId(student.getStudentId());
            testMark.setStudentName(student.getStudentName());
            testMark.setObtainedMarks(0); // Default to 0 for new test
            testMark.setTotal(totalMarks);

            testMarks.add(testMark);
        }

        return testMarks;
    }

    // Alternative: If you want to directly create adapter from StudentDetails
    @Override
    public void onStudentsLoaded(List<StudentDetails> students, int totalCount) {
        // Option 1: Convert to TestMarkModel first (recommended)
        List<TestMarkModel> newTestMarks = convertStudentsToTestMarks(students);
        this.currentTestMarks = newTestMarks;
        this.isEditMode = false;
        setupAdapterWithTestMarks(newTestMarks);

        // Switch to create mode UI
        switchToCreateMode();

        // Option 2: Use factory method directly
        // adapter = AdapterAddOrUpdateTestMarks.fromStudentDetails(this, students);
        // binding.rcTestResult.setAdapter(adapter);
    }

    private void switchToCreateMode() {
        isEditMode = false;

        // Show create button, hide edit buttons
        binding.btnCreateTest.setVisibility(View.VISIBLE);
        binding.editBtnLinear.setVisibility(View.GONE);

        // Update UI text for create mode
        binding.tvCreateTest.setText("Create New Test");

        // Enable total marks editing
        binding.edtTotalMark.setEnabled(true);
        binding.edtTotalMark.setText(String.valueOf(totalMarks));
    }

    private void switchToEditMode() {
        isEditMode = true;

        // Hide create button, show edit buttons
        binding.btnCreateTest.setVisibility(View.GONE);
        binding.editBtnLinear.setVisibility(View.VISIBLE);

        // Update UI text for edit mode
        binding.tvCreateTest.setText("Edit Test Marks");

        // Update button texts
        binding.editTxt.setText("Update");
        binding.cancelTxt.setText("Delete Test");

        // ENABLE total marks editing in edit mode (FIXED)
        binding.edtTotalMark.setEnabled(true);
    }

    private void handleEdit() {
        if (adapter == null) {
            showToast("No data to update");
            return;
        }

        // Get total marks from EditText
        String totalMarksStr = binding.edtTotalMark.getText().toString().trim();
        if (totalMarksStr.isEmpty()) {
            showToast("Please enter total marks");
            return;
        }

        try {
            int newTotalMarks = Integer.parseInt(totalMarksStr);
            if (newTotalMarks <= 0) {
                showToast("Total marks must be greater than 0");
                return;
            }

            // UPDATE the totalMarks variable (FIXED)
            this.totalMarks = newTotalMarks;

        } catch (NumberFormatException e) {
            showToast("Please enter valid total marks");
            return;
        }

        // Validate all marks and get updated list
        adapter.validateAllMarks(); // Ensures empty = 0
        List<TestMarkModel> updatedList = adapter.getUpdatedList();

        // Check if list is empty
        if (updatedList == null || updatedList.isEmpty()) {
            showToast("No student marks to update");
            return;
        }

        // Update total marks in all test marks with the NEW total marks (FIXED)
        for (TestMarkModel testMark : updatedList) {
            testMark.setTotal(totalMarks);
        }

        // Validate required parameters
        if (!testApiHelper.validateTestParameters(receivedClassId, receivedSubjectId, receivedDate, totalMarks)) {
            showToast("Invalid test parameters! Please check class, subject, date and total marks");
            return;
        }

        // Validate test marks
        if (!testApiHelper.validateTestMarks(updatedList, totalMarks)) {
            showToast("Invalid marks detected! Please check marks are between 0 and " + totalMarks);
            return;
        }

        // Update existing test
        testApiHelper.updateTestMarks(receivedClassId, receivedSubjectId, receivedDate,
                totalMarks, updatedList, this);
    }

    private void handleSubmit() {
        if (adapter == null) {
            showToast("No data to submit");
            return;
        }

        // Get total marks from EditText
        String totalMarksStr = binding.edtTotalMark.getText().toString().trim();
        if (totalMarksStr.isEmpty()) {
            showToast("Please enter total marks");
            return;
        }

        try {
            totalMarks = Integer.parseInt(totalMarksStr);
            if (totalMarks <= 0) {
                showToast("Total marks must be greater than 0");
                return;
            }
        } catch (NumberFormatException e) {
            showToast("Please enter valid total marks");
            return;
        }

        // Validate all marks and get updated list
        adapter.validateAllMarks(); // Ensures empty = 0
        List<TestMarkModel> updatedList = adapter.getUpdatedList();

        // Check if list is empty
        if (updatedList == null || updatedList.isEmpty()) {
            showToast("No student marks to submit");
            return;
        }

        // Update total marks in all test marks
        for (TestMarkModel testMark : updatedList) {
            testMark.setTotal(totalMarks);
        }

        // Validate required parameters
        if (!testApiHelper.validateTestParameters(receivedClassId, receivedSubjectId, receivedDate, totalMarks)) {
            showToast("Invalid test parameters! Please check class, subject, date and total marks");
            return;
        }

        // Validate test marks
        if (!testApiHelper.validateTestMarks(updatedList, totalMarks)) {
            showToast("Invalid marks detected! Please check marks are between 0 and " + totalMarks);
            return;
        }

        // Create new test (this method is only called in create mode)
        testApiHelper.submitTestMarks(receivedClassId, receivedSubjectId, receivedDate,
                totalMarks, updatedList, this);
    }

    private void handleDelete() {
        // Check if we have existing test data (edit mode) and test ID
        if (!isEditMode || existingTestData == null || existingTestData.getData() == null) {
            showToast("Cannot delete test: No test data found");
            return;
        }

        // Get the test ID from existing test data
        int testId = existingTestData.getData().getTestId(); // Assuming getTestId() method exists

        // Validate test ID
        if (!testApiHelper.validateTestId(testId)) {
            showToast("Invalid test ID");
            return;
        }

        // Show confirmation dialog before deleting
        showConfirmationDialog("Delete Test",
                "Are you sure you want to delete this test? This action cannot be undone.",
                () -> {
                    // Call delete API with proper test ID
                    testApiHelper.deleteTest(testId, this);
                    finish();
                });
    }

    private void showConfirmationDialog(String title, String message, Runnable onConfirm) {
        // You can implement a custom dialog or use AlertDialog
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Yes", (dialog, which) -> {
                    onConfirm.run();
                    dialog.dismiss();
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, LENGTH_SHORT).show();
    }

    // Optional: Add method to handle total marks change
    private void updateTotalMarks(int newTotalMarks) {
        this.totalMarks = newTotalMarks;

        // Update all test marks with new total
        if (currentTestMarks != null) {
            for (TestMarkModel testMark : currentTestMarks) {
                testMark.setTotal(newTotalMarks);
            }

            // Refresh adapter
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        }
    }

    // Optional: Add method to get test statistics
    private void showTestStatistics() {
        if (currentTestMarks != null) {
            String summary = testApiHelper.getTestMarksSummary(currentTestMarks, totalMarks);
            showToast(summary);
        }
    }
}