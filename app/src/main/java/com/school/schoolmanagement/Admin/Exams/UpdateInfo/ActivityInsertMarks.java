package com.school.schoolmanagement.Admin.Exams.UpdateInfo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.school.schoolmanagement.Admin.Adapter.StudentRowAdapter;
import com.school.schoolmanagement.Admin.Adapter.SubjectHeaderAdapter;
import com.school.schoolmanagement.Admin.Model.StudentInsertMarks;
import com.school.schoolmanagement.Admin.Model.StudentMarksData;
import com.school.schoolmanagement.Admin.Model.StudentMarksRequest;
import com.school.schoolmanagement.Admin.Model.Subject;
import com.school.schoolmanagement.Admin.Model.SubjectMark;
import com.school.schoolmanagement.Admin.Model.SubjectModel;
import com.school.schoolmanagement.HelperClasses.StudentMarksApiHelper;
import com.school.schoolmanagement.HelperClasses.SubjectApiHelper;
import com.school.schoolmanagement.R;
import com.school.schoolmanagement.databinding.ActivityInsertMarksBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityInsertMarks extends AppCompatActivity implements StudentRowAdapter.MarksChangedListener {

    private static final String TAG = "ActivityInsertMarks";

    // Intent Constants
    public static final String EXTRA_EXAM_ID = "examId";
    public static final String EXTRA_CLASS_ID = "classId";
    public static final String EXTRA_REGISTRATION_NUMBER = "registrationNumber";

    ActivityInsertMarksBinding binding;
    private RecyclerView rcSubjects;
    private RecyclerView rcStudents;
    private List<Subject> subjects;
    private List<StudentInsertMarks> students;
    private Map<String, Map<String, String>> marksData;

    // Helper class instances
    private StudentMarksApiHelper apiHelper;
    private SubjectApiHelper subjectApiHelper;

    // Intent data
    private int examId = -1;
    private int selectedClassId = -1;
    private int registrationNumber = -1;

    // Subject data from SubjectApiHelper
    private ArrayList<SubjectModel> subjectModelList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityInsertMarksBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.toolbar.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Initialize helper classes
        apiHelper = new StudentMarksApiHelper(this);
        subjectApiHelper = new SubjectApiHelper(this);

        // Initialize the marks data structure
        marksData = new HashMap<>();
        subjectModelList = new ArrayList<>();

        // Initialize RecyclerViews
        initializeRecyclerViews();

        // Get intent extras with detailed debugging
        if (!processIntentData()) {
            return; // Exit if required data is missing
        }

        // Set up click listeners
        setupClickListeners();

        // Load subjects first, then load marks data
        loadSubjectsAndMarksData();
    }

    private void initializeRecyclerViews() {
        // Set up RecyclerViews with LinearLayoutManager
        binding.rcSubjects.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.rcRowInsertMarks.setLayoutManager(new LinearLayoutManager(this));
    }

    private boolean processIntentData() {
        Intent intent = getIntent();

        // Check if intent is null
        if (intent == null) {
            Log.e(TAG, "Intent is null!");
            showErrorAndFinish("No data received. Please try again.");
            return false;
        }

        // Check if intent has extras
        Bundle extras = intent.getExtras();
        if (extras == null) {
            Log.e(TAG, "No extras found in intent!");
            showErrorAndFinish("Required data missing. Please try again.");
            return false;
        }

        // Log all available keys in the intent for debugging
        Log.d(TAG, "Available keys in intent:");
        for (String key : extras.keySet()) {
            Object value = extras.get(key);
            Log.d(TAG, "Key: " + key + ", Value: " + value + ", Type: " + (value != null ? value.getClass().getSimpleName() : "null"));
        }

        // Get exam ID properly as integer
        examId = intent.getIntExtra(EXTRA_EXAM_ID, -1);
        if (examId == -1) {
            examId = intent.getIntExtra("examId", -1); // Try alternative key
        }

        // Get class ID as integer
        selectedClassId = intent.getIntExtra(EXTRA_CLASS_ID, -1);
        if (selectedClassId == -1) {
            selectedClassId = intent.getIntExtra("classId", -1); // Try alternative key
        }
        registrationNumber = intent.getIntExtra(EXTRA_REGISTRATION_NUMBER, -1);
        if (registrationNumber == -1) {
            registrationNumber = intent.getIntExtra("registrationNumber",-1); // Try alternative key
        }

        // Log the received data for debugging
        Log.d(TAG, "Received examId: " + examId);
        Log.d(TAG, "Received classId: " + selectedClassId);
        Log.d(TAG, "Received registrationNumber: " + registrationNumber);

        // Validate required fields
        if (examId == -1) {
            Log.e(TAG, "examId is -1 (invalid)!");
            showErrorAndFinish("Exam ID is missing or invalid. Cannot proceed.");
            return false;
        }

        if (selectedClassId == -1) {
            Log.e(TAG, "classId is -1 (invalid)!");
            showErrorAndFinish("Class ID is missing or invalid. Cannot proceed.");
            return false;
        }

        return true;
    }

    private void setupClickListeners() {
        // Add save button click listener if you have one in your layout
        // binding.btnSave.setOnClickListener(v -> saveAllMarks());
        binding.btnSave.setOnClickListener(v -> saveAllMarks());

        // Add refresh button click listener if you have one
        // binding.btnRefresh.setOnClickListener(v -> refreshData());
    }

    private void showErrorAndFinish(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        finish();
    }

    // NEW METHOD: Load subjects first, then marks data
    private void loadSubjectsAndMarksData() {
        Log.d(TAG, "Loading subjects for classId: " + selectedClassId);

        // First, fetch subjects for the class
        subjectApiHelper.fetchSubjectsForClass(selectedClassId, new SubjectApiHelper.SubjectListCallback() {
            @Override
            public void onSubjectsSuccess(ArrayList<SubjectModel> subjectList) {
                Log.d(TAG, "Successfully loaded " + subjectList.size() + " subjects");

                // Store the subject list
                subjectModelList = subjectList;

                // Convert SubjectModel to Subject for compatibility with existing code
                convertSubjectModelsToSubjects();

                // Now load marks data
                loadMarksData();
            }

            @Override
            public void onSubjectsError(String errorMessage) {
                Log.e(TAG, "Error loading subjects: " + errorMessage);
                Toast.makeText(ActivityInsertMarks.this, "Error loading subjects: " + errorMessage, Toast.LENGTH_LONG).show();

                // Initialize empty subjects and continue with marks loading
                subjects = new ArrayList<>();
                subjectModelList = new ArrayList<>();
                loadMarksData();
            }

            @Override
            public void onSubjectsLoading(boolean isLoading) {
                if (isLoading) {
                    showLoadingIndicator();
                } else {
                    // Don't hide loading here, let marks loading handle it
                }
            }
        });
    }

    // NEW METHOD: Convert SubjectModel to Subject for compatibility
    private void convertSubjectModelsToSubjects() {
        subjects = new ArrayList<>();

        for (SubjectModel subjectModel : subjectModelList) {
            // Create Subject object from SubjectModel
            // Assuming Subject has constructor or setters for id and name
            Subject subject = new Subject();
            subject.setSubjectId(String.valueOf(subjectModel.getId())); // Convert int to String if needed
            subject.setSubjectName(subjectModel.getName());

            // Add any other required fields mapping here
            subjects.add(subject);
        }

        Log.d(TAG, "Converted " + subjectModelList.size() + " SubjectModels to Subjects");
    }

    private void loadMarksData() {
        // Check if we're loading for a specific student or entire class
        if (registrationNumber != -1) {
            // Load specific student data
            loadStudentData();
        } else {
            // Load all students for the class
            loadClassData();
        }
    }

    private void loadStudentData() {
        Log.d(TAG, "Loading data for student: " + registrationNumber);

        // Convert registration number to string for API call
        String regNo = String.valueOf(registrationNumber);

        // Add validation to ensure registrationNumber is valid
        if (registrationNumber <= 0) {
            Log.e(TAG, "Invalid registration number: " + registrationNumber);
            Toast.makeText(this, "Invalid student registration number", Toast.LENGTH_LONG).show();
            return;
        }

        Log.d(TAG, "Making API call with - ExamId: " + examId + ", ClassId: " + selectedClassId + ", RegNo: " + regNo);

        apiHelper.fetchStudentMarks(String.valueOf(examId), String.valueOf(selectedClassId), regNo, new StudentMarksApiHelper.StudentMarksCallback() {
            @Override
            public void onSuccess(StudentMarksApiHelper.StudentMarksResponse response) {
                runOnUiThread(() -> {
                    try {
                        // Get all students from response
                        List<StudentInsertMarks> allStudents = response.getStudents();
                        Log.d(TAG, "API returned " + (allStudents != null ? allStudents.size() : 0) + " students");

                        // Initialize students list
                        students = new ArrayList<>();

                        if (allStudents != null && !allStudents.isEmpty()) {
                            String targetRegNo = String.valueOf(registrationNumber);
                            boolean studentFound = false;

                            for (StudentInsertMarks student : allStudents) {
                                // Log student details for debugging
                                Log.d(TAG, "Checking student: " + student.getName() +
                                        ", ID: " + student.getId());

                                // Get student ID and handle null values
                                String studentId = student.getId();
                                if (studentId != null) {
                                    studentId = studentId.trim();

                                    // Try multiple comparison methods
                                    if (targetRegNo.equals(studentId) ||
                                            targetRegNo.equalsIgnoreCase(studentId)) {
                                        students.add(student);
                                        studentFound = true;
                                        Log.d(TAG, "Found matching student: " + student.getName() +
                                                " with registration: " + studentId);
                                        break;
                                    }
                                }
                            }

                            if (!studentFound) {
                                Log.w(TAG, "No student found with registration number: " + registrationNumber);

                                // Log all available students for debugging
                                Log.d(TAG, "Available students in response:");
                                for (StudentInsertMarks student : allStudents) {
                                    Log.d(TAG, "- " + student.getName() + " (ID: " + student.getId() + ")");
                                }

                                Toast.makeText(ActivityInsertMarks.this,
                                        "Student with registration number " + registrationNumber + " not found in this class",
                                        Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Log.w(TAG, "API returned empty or null student list");
                            Toast.makeText(ActivityInsertMarks.this,
                                    "No students found for this class and exam",
                                    Toast.LENGTH_LONG).show();
                        }

                        // Map marks with subject names
                        mapMarksWithSubjectNames();

                        // Set up adapters with the filtered data
                        setupAdapters();

                        // Hide loading indicator
                        hideLoadingIndicator();

                    } catch (Exception e) {
                        Log.e(TAG, "Error processing student data: " + e.getMessage(), e);
                        handleDataError("Error processing student data: " + e.getMessage());
                    }
                });
            }

            @Override
            public void onError(String errorMessage) {
                runOnUiThread(() -> {
                    Log.e(TAG, "API Error loading student data: " + errorMessage);

                    // Check if it's a 404 error specifically
                    if (errorMessage.contains("404")) {
                        String message = "Student with registration number " + registrationNumber +
                                " not found. Please check:\n" +
                                "1. Registration number is correct\n" +
                                "2. Student is enrolled in this class\n" +
                                "3. Student is registered for this exam";
                        Toast.makeText(ActivityInsertMarks.this, message, Toast.LENGTH_LONG).show();
                    } else {
                        handleDataError("Error loading student data: " + errorMessage);
                    }

                    hideLoadingIndicator();
                });
            }

            @Override
            public void onLoading(boolean isLoading) {
                runOnUiThread(() -> {
                    if (isLoading) {
                        showLoadingIndicator();
                    } else {
                        hideLoadingIndicator();
                    }
                });
            }
        });
    }

    private void loadClassData() {
        Log.d(TAG, "Loading data for class: " + selectedClassId);

        apiHelper.fetchClassMarks(String.valueOf(examId), String.valueOf(selectedClassId), new StudentMarksApiHelper.StudentMarksCallback() {
            @Override
            public void onSuccess(StudentMarksApiHelper.StudentMarksResponse response) {
                runOnUiThread(() -> {
                    try {
                        // Don't override subjects from API response, use the ones we fetched from SubjectApiHelper
                        students = response.getStudents();

                        Log.d(TAG, "Successfully loaded " + students.size() + " students");

                        // Validate data
                        if (students == null) students = new ArrayList<>();

                        // Map marks with subject names
                        mapMarksWithSubjectNames();

                        // Set up adapters with the fetched data
                        setupAdapters();

                        // Hide loading indicator
                        hideLoadingIndicator();
                    } catch (Exception e) {
                        Log.e(TAG, "Error processing class data: " + e.getMessage());
                        handleDataError("Error processing class data");
                    }
                });
            }

            @Override
            public void onError(String errorMessage) {
                runOnUiThread(() -> {
                    Log.e(TAG, "Error loading class data: " + errorMessage);
                    handleDataError("Error: " + errorMessage);
                });
            }

            @Override
            public void onLoading(boolean isLoading) {
                runOnUiThread(() -> {
                    if (isLoading) {
                        showLoadingIndicator();
                    } else {
                        hideLoadingIndicator();
                    }
                });
            }
        });
    }

    // NEW METHOD: Map marks with subject names from SubjectApiHelper
    private void mapMarksWithSubjectNames() {
        if (students == null || subjects == null || subjectModelList == null) {
            Log.w(TAG, "Cannot map marks - students, subjects, or subjectModelList is null");
            return;
        }

        Log.d(TAG, "Mapping marks with subject names for " + students.size() + " students and " + subjects.size() + " subjects");

        // Create a map of subject ID to subject name for quick lookup
        Map<String, String> subjectIdToNameMap = new HashMap<>();
        for (SubjectModel subjectModel : subjectModelList) {
            subjectIdToNameMap.put(String.valueOf(subjectModel.getId()), subjectModel.getName());
        }

        // For each student, ensure their marks are mapped correctly with subject names
        for (StudentInsertMarks student : students) {
            if (student.getAllSubjects() != null) {
                HashMap<String, String> studentMarks = student.getAllSubjects();

                // Log existing marks for debugging
                Log.d(TAG, "Student " + student.getName() + " existing marks: " + studentMarks.toString());

                // Ensure all subjects are represented in student's marks
                for (Subject subject : subjects) {
                    String subjectId = subject.getSubjectId();
                    String subjectName = subject.getSubjectName();

                    // If student doesn't have this subject, add it with empty mark
                    if (!studentMarks.containsKey(subjectId)) {
                        studentMarks.put(subjectId, "");
                        Log.d(TAG, "Added empty mark for subject " + subjectName + " (ID: " + subjectId + ") for student " + student.getName());
                    }
                }
            }
        }

        Log.d(TAG, "Marks mapping completed successfully");
    }

    private void handleDataError(String errorMessage) {
        Toast.makeText(ActivityInsertMarks.this, errorMessage, Toast.LENGTH_LONG).show();

        // Initialize with empty data to prevent crashes
        initEmptyData();
        setupAdapters();

        // Hide loading indicator
        hideLoadingIndicator();
    }

    private void initEmptyData() {
        if (subjects == null) subjects = new ArrayList<>();
        if (students == null) students = new ArrayList<>();
        if (subjectModelList == null) subjectModelList = new ArrayList<>();
    }

    private void setupAdapters() {
        try {
            // Check empty states first
            updateSubjectsEmptyState();
            updateStudentsEmptyState();

            // Set up subject headers adapter if subjects exist (regardless of students)
            if (subjects != null && !subjects.isEmpty()) {
                SubjectHeaderAdapter subjectAdapter = new SubjectHeaderAdapter(subjects);
                binding.rcSubjects.setAdapter(subjectAdapter);
                Log.d(TAG, "Subject adapter set up successfully with " + subjects.size() + " subjects");
            }

            // Set up student rows adapter only if both subjects and students exist
            if (subjects != null && !subjects.isEmpty() && students != null && !students.isEmpty()) {
                StudentRowAdapter studentAdapter = new StudentRowAdapter(students, subjects, this);
                binding.rcRowInsertMarks.setAdapter(studentAdapter);
                Log.d(TAG, "Student adapter set up successfully with " + students.size() + " students");
            }

            Log.d(TAG, "Adapters setup completed - Subjects: " + (subjects != null ? subjects.size() : "null") +
                    ", Students: " + (students != null ? students.size() : "null"));

        } catch (Exception e) {
            Log.e(TAG, "Error setting up adapters: " + e.getMessage());
            Toast.makeText(this, "Error displaying data", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateSubjectsEmptyState() {
        boolean isSubjectsEmpty = (subjects == null || subjects.isEmpty());

        // Show/hide subjects empty state
        if (isSubjectsEmpty) {
            binding.tvNoSubjects.setVisibility(View.VISIBLE);
            binding.rcSubjects.setVisibility(View.GONE);
        } else {
            binding.tvNoSubjects.setVisibility(View.GONE);
            binding.rcSubjects.setVisibility(View.VISIBLE);
        }

        Log.d(TAG, "Subjects empty state updated - isEmpty: " + isSubjectsEmpty);
    }

    private void updateStudentsEmptyState() {
        boolean isStudentsEmpty = (students == null || students.isEmpty());
        boolean isSubjectsEmpty = (subjects == null || subjects.isEmpty());

        // Only show students empty state if subjects exist but students don't
        if (isStudentsEmpty && !isSubjectsEmpty) {
            binding.emptyStateContainer.setVisibility(View.VISIBLE);
            binding.rcRowInsertMarks.setVisibility(View.GONE);
        } else if (!isStudentsEmpty) {
            binding.emptyStateContainer.setVisibility(View.GONE);
            binding.rcRowInsertMarks.setVisibility(View.VISIBLE);
        } else {
            // If no subjects, hide both empty state and recycler view
            binding.emptyStateContainer.setVisibility(View.GONE);
            binding.rcRowInsertMarks.setVisibility(View.GONE);
        }

        Log.d(TAG, "Students empty state updated - isStudentsEmpty: " + isStudentsEmpty +
                ", isSubjectsEmpty: " + isSubjectsEmpty);
    }
    // Combined method to update both empty states at once
    private void updateAllEmptyStates() {
        updateSubjectsEmptyState();
        updateStudentsEmptyState();
    }

    // Method to handle overall empty state (when both are empty)
    private void updateOverallEmptyState() {
        boolean isBothEmpty = (subjects == null || subjects.isEmpty()) &&
                (students == null || students.isEmpty());

        // Hide individual empty states when showing overall empty state
        if (isBothEmpty) {
            if (binding.tvNoSubjects != null) {
                binding.tvNoSubjects.setVisibility(View.GONE);
            }
        }
    }

    private void showLoadingIndicator() {
        // Show your loading indicator here
        try {
            // Example implementations (uncomment based on your layout):
            // binding.progressBar.setVisibility(View.VISIBLE);
            // binding.layoutContent.setVisibility(View.GONE);
            Log.d(TAG, "Showing loading indicator");
        } catch (Exception e) {
            Log.e(TAG, "Error showing loading indicator: " + e.getMessage());
        }
    }

    private void hideLoadingIndicator() {
        // Hide your loading indicator here
        try {
            // Example implementations (uncomment based on your layout):
            // binding.progressBar.setVisibility(View.GONE);
            // binding.layoutContent.setVisibility(View.VISIBLE);
            Log.d(TAG, "Hiding loading indicator");
        } catch (Exception e) {
            Log.e(TAG, "Error hiding loading indicator: " + e.getMessage());
        }
    }

    @Override
    public void onMarkChanged(StudentInsertMarks student, String subjectId, String mark) {
        try {
            // This gets called whenever a mark is changed
            Log.d(TAG, "Student " + student.getName() + " got " + mark + " in subject " + subjectId);

            // Validate the mark using helper class
            if (!apiHelper.isValidMark(mark)) {
                Toast.makeText(this, "Invalid mark entered. Please enter a value between 0-100.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Update the student's mark
            student.setSubjectMark(subjectId, mark);

            // Calculate and display total marks and percentage
            double totalMarks = apiHelper.calculateTotalMarks(student);
            double percentage = apiHelper.calculatePercentage(student, subjects.size());
            String grade = apiHelper.getGrade(percentage);

            // Get subject name for logging
            String subjectName = getSubjectNameById(subjectId);
            Log.d(TAG, "Student " + student.getName() + " - Subject: " + subjectName + " (" + subjectId + "), Mark: " + mark + ", Total: " + totalMarks + ", Percentage: " + percentage + "%, Grade: " + grade);

            // Update marks data structure
            updateMarksData(student.getId(), subjectId, mark);

        } catch (Exception e) {
            Log.e(TAG, "Error in onMarkChanged: " + e.getMessage());
            Toast.makeText(this, "Error updating mark", Toast.LENGTH_SHORT).show();
        }
    }

    // NEW METHOD: Get subject name by ID
    private String getSubjectNameById(String subjectId) {
        try {
            for (SubjectModel subjectModel : subjectModelList) {
                if (String.valueOf(subjectModel.getId()).equals(subjectId)) {
                    return subjectModel.getName();
                }
            }
            // If not found in SubjectModel list, try the Subject list
            for (Subject subject : subjects) {
                if (String.valueOf(subject.getSubjectId()).equals(subjectId)) {
                    return subject.getSubjectName();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting subject name by ID: " + e.getMessage());
        }
        return "Unknown Subject";
    }

    private void updateMarksData(String studentRegNo, String subjectId, String mark) {
        try {
            if (!marksData.containsKey(studentRegNo)) {
                marksData.put(studentRegNo, new HashMap<>());
            }
            marksData.get(studentRegNo).put(subjectId, mark);

            // Log the update for debugging
            String subjectName = getSubjectNameById(subjectId);
            Log.d(TAG, "Updated marks data - Student: " + studentRegNo + ", Subject: " + subjectName + " (" + subjectId + "), Mark: " + mark);
        } catch (Exception e) {
            Log.e(TAG, "Error updating marks data: " + e.getMessage());
        }
    }


    // Method to refresh data
    private void refreshData() {
        Log.d(TAG, "Refreshing data...");
        try {
            // Clear existing data
            if (subjects != null) subjects.clear();
            if (students != null) students.clear();
            if (marksData != null) marksData.clear();
            if (subjectModelList != null) subjectModelList.clear();

            // Reload data (subjects first, then marks)
            loadSubjectsAndMarksData();
        } catch (Exception e) {
            Log.e(TAG, "Error refreshing data: " + e.getMessage());
            Toast.makeText(this, "Error refreshing data", Toast.LENGTH_SHORT).show();
        }
    }

    // Method to handle back button press
    @Override
    public void onBackPressed() {
        // Check if there are unsaved changes
        if (hasUnsavedChanges()) {
            // Show confirmation dialog
            showUnsavedChangesDialog();
        } else {
            super.onBackPressed();
        }
    }

    // Static method with proper parameter types
    public static void startActivity(android.content.Context context, int examId, int classId, String registrationNumber) {
        Intent intent = new Intent(context, ActivityInsertMarks.class);
        intent.putExtra(EXTRA_EXAM_ID, examId);  // Pass as integer
        intent.putExtra(EXTRA_CLASS_ID, classId);  // Pass as integer
        if (registrationNumber != null && !registrationNumber.trim().isEmpty()) {
            intent.putExtra(EXTRA_REGISTRATION_NUMBER, registrationNumber);  // Pass as string
        }
        context.startActivity(intent);
    }

    // Overloaded method for class-wide marks (no specific student)
    public static void startActivity(android.content.Context context, int examId, int classId) {
        startActivity(context, examId, classId, null);
    }

    // LEGACY: Keep old methods for backward compatibility (if needed)
    public static void startActivity(android.content.Context context, String examId, String classId, String registrationNumber) {
        try {
            int examIdInt = Integer.parseInt(examId);
            int classIdInt = Integer.parseInt(classId);
            startActivity(context, examIdInt, classIdInt, registrationNumber);
        } catch (NumberFormatException e) {
            Log.e("ActivityInsertMarks", "Invalid examId or classId format: " + examId + ", " + classId);
            Toast.makeText(context, "Invalid exam ID or class ID format", Toast.LENGTH_SHORT).show();
        }
    }

    public static void startActivity(android.content.Context context, String examId, String classId) {
        startActivity(context, examId, classId, null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            // Clean up resources
            apiHelper = null;
            subjectApiHelper = null;
            if (subjects != null) subjects.clear();
            if (students != null) students.clear();
            if (marksData != null) marksData.clear();
            if (subjectModelList != null) subjectModelList.clear();
            subjects = null;
            students = null;
            marksData = null;
            subjectModelList = null;
        } catch (Exception e) {
            Log.e(TAG, "Error in onDestroy: " + e.getMessage());
        }
    }


    private void saveAllMarks() {
        Log.d(TAG, "Saving all marks...");

        try {
            // Show loading indicator
            showLoadingIndicator();

            // Validate all marks before saving
            if (!validateAllMarks()) {
                hideLoadingIndicator();
                return;
            }

            // Create the request object
            StudentMarksRequest request = createStudentMarksRequest();

            if (request == null || request.getStudentMarks().isEmpty()) {
                hideLoadingIndicator();
                Toast.makeText(this, "No marks data to save", Toast.LENGTH_SHORT).show();
                return;
            }

            // Log the request for debugging
            Gson gson = new Gson();
            String jsonRequest = gson.toJson(request);
            Log.d(TAG, "Sending request: " + jsonRequest);

            // Send to API using your helper class
            apiHelper.saveStudentMarks(request, new StudentMarksApiHelper.SaveMarksCallback() {
                @Override
                public void onSuccess(String message) {
                    runOnUiThread(() -> {
                        hideLoadingIndicator();
                        Toast.makeText(ActivityInsertMarks.this, "Marks saved successfully!", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Marks saved successfully: " + message);

                        // Clear unsaved changes flag
                        clearMarksData();

                        // Optionally finish activity or refresh data
                        finish();
                    });
                }

                @Override
                public void onError(String errorMessage) {
                    runOnUiThread(() -> {
                        hideLoadingIndicator();
                        Toast.makeText(ActivityInsertMarks.this, "Error saving marks: " + errorMessage, Toast.LENGTH_LONG).show();
                        Log.e(TAG, "Error saving marks: " + errorMessage);
                    });
                }

                @Override
                public void onLoading(boolean isLoading) {
                    runOnUiThread(() -> {
                        if (isLoading) {
                            showLoadingIndicator();
                        } else {
                            hideLoadingIndicator();
                        }
                    });
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "Error in saveAllMarks: " + e.getMessage(), e);
            hideLoadingIndicator();
            Toast.makeText(this, "Error preparing marks data: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    // New method to create StudentMarksRequest from current data
    private StudentMarksRequest createStudentMarksRequest() {
        try {
            StudentMarksRequest request = new StudentMarksRequest(examId, selectedClassId, new ArrayList<>());

            if (students == null || students.isEmpty()) {
                Log.w(TAG, "No students data available");
                return request;
            }

            for (StudentInsertMarks student : students) {
                StudentMarksData studentMarksData = createStudentMarksData(student);
                if (studentMarksData != null && !studentMarksData.getSubjectMarks().isEmpty()) {
                    request.addStudentMarks(studentMarksData);
                }
            }

            Log.d(TAG, "Created request with " + request.getStudentMarks().size() + " students");
            return request;

        } catch (Exception e) {
            Log.e(TAG, "Error creating StudentMarksRequest: " + e.getMessage(), e);
            return null;
        }
    }

    // New method to create StudentMarksData from StudentInsertMarks
    private StudentMarksData createStudentMarksData(StudentInsertMarks student) {
        try {
            // Parse student ID from string to int
            int studentId;
            try {
                studentId = Integer.parseInt(student.getId());
            } catch (NumberFormatException e) {
                Log.e(TAG, "Invalid student ID format: " + student.getId());
                return null;
            }

            StudentMarksData studentMarksData = new StudentMarksData(studentId, new ArrayList<>());

            // Get all subject marks for this student
            HashMap<String, String> allSubjects = student.getAllSubjects();
            if (allSubjects == null || allSubjects.isEmpty()) {
                Log.w(TAG, "No subject marks for student: " + student.getName());
                return studentMarksData;
            }

            // Convert marks to SubjectMark objects
            for (Map.Entry<String, String> entry : allSubjects.entrySet()) {
                String subjectId = entry.getKey();
                String markStr = entry.getValue();

                // Skip empty marks
                if (markStr == null || markStr.trim().isEmpty()) {
                    continue;
                }

                try {
                    // Get subject name from subjectId
                    String subjectName = getSubjectNameById(subjectId);

                    // Parse marks to integer
                    int marks = Integer.parseInt(markStr.trim());

                    // Create SubjectMark object
                    SubjectMark subjectMark = new SubjectMark(subjectName, marks);
                    studentMarksData.addSubjectMark(subjectMark);

                    Log.d(TAG, "Added mark - Student: " + student.getName() +
                            ", Subject: " + subjectName + ", Marks: " + marks);

                } catch (NumberFormatException e) {
                    Log.e(TAG, "Invalid marks format for student " + student.getName() +
                            ", subject " + subjectId + ": " + markStr);
                    // Skip this subject mark but continue with others
                    continue;
                }
            }

            return studentMarksData;

        } catch (Exception e) {
            Log.e(TAG, "Error creating StudentMarksData for student " + student.getName() + ": " + e.getMessage(), e);
            return null;
        }
    }

    // New method to clear marks data (for unsaved changes tracking)
    private void clearMarksData() {
        try {
            if (marksData != null) {
                marksData.clear();
            }
            Log.d(TAG, "Marks data cleared");
        } catch (Exception e) {
            Log.e(TAG, "Error clearing marks data: " + e.getMessage());
        }
    }

    // Enhanced validation method
    private boolean validateAllMarks() {
        try {
            if (students == null || students.isEmpty()) {
                Toast.makeText(this, "No student data to save", Toast.LENGTH_SHORT).show();
                return false;
            }

            boolean hasAnyMarks = false;

            // Validate all marks before saving
            for (StudentInsertMarks student : students) {
                HashMap<String, String> allMarks = student.getAllSubjects();
                if (allMarks == null) continue;

                for (Map.Entry<String, String> entry : allMarks.entrySet()) {
                    String subjectId = entry.getKey();
                    String mark = entry.getValue();

                    if (mark != null && !mark.trim().isEmpty()) {
                        hasAnyMarks = true;

                        // Validate mark format and range
                        if (!apiHelper.isValidMark(mark)) {
                            String subjectName = getSubjectNameById(subjectId);
                            Toast.makeText(this, "Invalid mark found for student " + student.getName() +
                                    " in subject " + subjectName + ": " + mark, Toast.LENGTH_LONG).show();
                            return false;
                        }
                    }
                }
            }

            if (!hasAnyMarks) {
                Toast.makeText(this, "Please enter at least one mark before saving", Toast.LENGTH_SHORT).show();
                return false;
            }

            return true;

        } catch (Exception e) {
            Log.e(TAG, "Error validating marks: " + e.getMessage(), e);
            Toast.makeText(this, "Error validating marks: " + e.getMessage(), Toast.LENGTH_LONG).show();
            return false;
        }
    }

    // Enhanced hasUnsavedChanges method
    private boolean hasUnsavedChanges() {
        try {
            if (students == null || students.isEmpty()) {
                return false;
            }

            // Check if there are any non-empty marks
            for (StudentInsertMarks student : students) {
                HashMap<String, String> allMarks = student.getAllSubjects();
                if (allMarks != null) {
                    for (String mark : allMarks.values()) {
                        if (mark != null && !mark.trim().isEmpty()) {
                            return true;
                        }
                    }
                }
            }

            // Also check marksData if it exists
            if (marksData != null && !marksData.isEmpty()) {
                for (Map<String, String> studentMarks : marksData.values()) {
                    for (String mark : studentMarks.values()) {
                        if (mark != null && !mark.trim().isEmpty()) {
                            return true;
                        }
                    }
                }
            }

        } catch (Exception e) {
            Log.e(TAG, "Error checking unsaved changes: " + e.getMessage());
        }
        return false;
    }

    // Enhanced showUnsavedChangesDialog method
    private void showUnsavedChangesDialog() {
        try {
            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Unsaved Changes")
                    .setMessage("You have unsaved marks. Do you want to save them before leaving?")
                    .setPositiveButton("Save", (dialog, which) -> {
                        saveAllMarks();
                    })
                    .setNegativeButton("Discard", (dialog, which) -> {
                        clearMarksData();
                        finish();
                    })
                    .setNeutralButton("Cancel", (dialog, which) -> {
                        dialog.dismiss();
                    })
                    .setCancelable(false)
                    .show();
        } catch (Exception e) {
            Log.e(TAG, "Error showing unsaved changes dialog: " + e.getMessage());
            // Fallback to simple toast
            Toast.makeText(this, "You have unsaved changes. Use save button to save your work.", Toast.LENGTH_LONG).show();
            super.onBackPressed();
        }
    }

    // Add this method to handle API response debugging
    private void logStudentMarksData(StudentMarksRequest request) {
        try {
            Log.d(TAG, "=== STUDENT MARKS REQUEST DEBUG ===");
            Log.d(TAG, "Exam ID: " + request.getExamId());
            Log.d(TAG, "Class ID: " + request.getClassId());
            Log.d(TAG, "Number of students: " + request.getStudentMarks().size());

            for (StudentMarksData studentData : request.getStudentMarks()) {
                Log.d(TAG, "Student ID: " + studentData.getStudentId());
                Log.d(TAG, "Number of subjects: " + studentData.getSubjectMarks().size());

                for (SubjectMark subjectMark : studentData.getSubjectMarks()) {
                    Log.d(TAG, "  - Subject: " + subjectMark.getSubject() + ", Marks: " + subjectMark.getMarks());
                }
            }
            Log.d(TAG, "=== END DEBUG ===");
        } catch (Exception e) {
            Log.e(TAG, "Error logging student marks data: " + e.getMessage());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data when activity resumes if needed
        // refreshData(); // Uncomment if needed
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Save draft or handle any cleanup if needed
    }
}