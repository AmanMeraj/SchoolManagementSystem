package com.school.schoolmanagement.Teachers.Attendance;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.school.schoolmanagement.Adapters.AdapterMarkAttendance;
import com.school.schoolmanagement.GlobalViewModel.ViewModel;
import com.school.schoolmanagement.Model.GetAttendanceResponse;
import com.school.schoolmanagement.Model.PostStudentAttendance;
import com.school.schoolmanagement.Model.StudentAttendance;
import com.school.schoolmanagement.R;
import com.school.schoolmanagement.Utils.Utility;
import com.school.schoolmanagement.databinding.ActivityMarkAttendanceBinding;

import java.util.ArrayList;
import java.util.List;

public class ActivityMarkAttendance extends Utility implements AdapterMarkAttendance.OnAttendanceChangeListener {

    private static final String TAG = "ActivityMarkAttendance";

    ActivityMarkAttendanceBinding binding;
    private AdapterMarkAttendance adapter;
    private List<GetAttendanceResponse.Datum> studentList;
    private String selectedDate;
    private String selectedClass;
    private int selectedClassId;
    private String from;
    private ViewModel viewModel;
    private boolean isAllStudentsNotMarked = false; // Flag to determine API choice

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate called");

        EdgeToEdge.enable(this);
        binding = ActivityMarkAttendanceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Log.d(TAG, "View binding completed");

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(ViewModel.class);
        Log.d(TAG, "ViewModel initialized: " + (viewModel != null ? "not null" : "null"));

        // Get data from intent
        selectedDate = getIntent().getStringExtra("selected_date");
        selectedClass = getIntent().getStringExtra("selected_class");
        selectedClassId = getIntent().getIntExtra("selected_class_id", -1);
        from = getIntent().getStringExtra("from");

        Log.d(TAG, "Intent data:");
        Log.d(TAG, "selectedDate: " + selectedDate);
        Log.d(TAG, "selectedClass: " + selectedClass);
        Log.d(TAG, "selectedClassId: " + selectedClassId);
        Log.d(TAG, "from: " + from);

        if(from != null && from.matches("EMPLOYEE")){
            Log.d(TAG, "Handling EMPLOYEE case");
            // Handle employee case - could disable certain UI elements or change behavior
            handleEmployeeMode();
        } else {
            Log.d(TAG, "Handling non-employee case");
        }

        // Validate received data
        if (selectedDate == null || selectedClass == null || selectedClassId == -1) {
            Log.e(TAG, "Missing required data - finishing activity");
            Toast.makeText(this, "Missing required data", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Log.d(TAG, "All required data validated successfully");

        setupToolbar();
        initStudentList();
        setupRecyclerView();
        setupSubmitButton();

        // Load attendance data from API
        loadAttendanceData();
    }

    private void handleEmployeeMode() {
        Log.d(TAG, "Setting up employee mode");

    }

    private void setupToolbar() {
        Log.d(TAG, "Setting up toolbar");

        try {
            View backButton = binding.toolbar.backBtn.findViewById(R.id.back_btn);
            if (backButton != null) {
                backButton.setOnClickListener(v -> {
                    Log.d(TAG, "Back button clicked");
                    onBackPressed();
                });
                Log.d(TAG, "Back button setup completed");
            } else {
                Log.w(TAG, "Back button not found");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error setting up toolbar: " + e.getMessage(), e);
        }
    }

    private void initStudentList() {
        Log.d(TAG, "Initializing student list");
        studentList = new ArrayList<>();
        Log.d(TAG, "Student list initialized with size: " + studentList.size());
    }

    private void setupRecyclerView() {
        Log.d(TAG, "Setting up RecyclerView");

        try {
            // Check if RecyclerView exists
            if (binding.rcMarkAttendance == null) {
                Log.e(TAG, "RecyclerView is null!");
                return;
            }

            // Create adapter
            adapter = new AdapterMarkAttendance(this, studentList, this);
            Log.d(TAG, "Adapter created: " + (adapter != null ? "not null" : "null"));

            // Set layout manager
            binding.rcMarkAttendance.setLayoutManager(new LinearLayoutManager(this));
            Log.d(TAG, "Layout manager set");

            // Set adapter
            binding.rcMarkAttendance.setAdapter(adapter);
            Log.d(TAG, "Adapter set to RecyclerView");

            // Update header information
            if (binding.yearDate != null) {
                String headerText = selectedClass + " " + selectedDate;
                binding.yearDate.setText(headerText);
                Log.d(TAG, "Header updated: " + headerText);
            } else {
                Log.w(TAG, "yearDate TextView is null");
            }

        } catch (Exception e) {
            Log.e(TAG, "Error setting up RecyclerView: " + e.getMessage(), e);
        }
    }

    private void setupSubmitButton() {
        Log.d(TAG, "Setting up submit button");

        try {
            if (binding.submitBtn != null) {
                binding.submitBtn.setOnClickListener(v -> {
                    Log.d(TAG, "Submit button clicked");
                    if (validateAttendance()) {
                        saveAttendanceRecords();
                    } else {
                        Log.w(TAG, "Attendance validation failed");
                        Toast.makeText(this, "Please mark attendance for all students", Toast.LENGTH_SHORT).show();
                    }
                });
                Log.d(TAG, "Submit button setup completed");
            } else {
                Log.w(TAG, "Submit button is null");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error setting up submit button: " + e.getMessage(), e);
        }
    }

    private void showLoading(String message) {
        Log.d(TAG, "Showing loading: " + message);

        try {
            if (binding.loader != null && binding.loader.rlLoader != null) {
                binding.loader.rlLoader.setVisibility(View.VISIBLE);
                Log.d(TAG, "Loading indicator shown");
            } else {
                Log.w(TAG, "Loading indicator views are null");
            }

            runOnUiThread(() -> Toast.makeText(this, message, Toast.LENGTH_SHORT).show());
        } catch (Exception e) {
            Log.e(TAG, "Error showing loading: " + e.getMessage(), e);
        }
    }

    private void hideLoading() {
        Log.d(TAG, "Hiding loading");

        try {
            if (binding.loader != null && binding.loader.rlLoader != null) {
                binding.loader.rlLoader.setVisibility(View.GONE);
                Log.d(TAG, "Loading indicator hidden");
            } else {
                Log.w(TAG, "Loading indicator views are null");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error hiding loading: " + e.getMessage(), e);
        }
    }

    private void loadAttendanceData() {
        Log.d(TAG, "Loading attendance data");

        // Show loading indicator
        showLoading("Loading students...");

        // Get auth token
        String authToken = "Bearer " + getAuthToken();
        Log.d(TAG, "Auth token: " + (authToken != null ? "not null" : "null"));

        if (authToken.equals("Bearer null") || authToken.equals("Bearer ")) {
            Log.e(TAG, "Auth token is null or empty!");
            hideLoading();
            Toast.makeText(this, "Authentication token not found", Toast.LENGTH_SHORT).show();
            return;
        }

        // Convert selectedClassId to int
        int classId = selectedClassId;
        Log.d(TAG, "API call parameters:");
        Log.d(TAG, "authToken: " + authToken);
        Log.d(TAG, "selectedDate: " + selectedDate);
        Log.d(TAG, "classId: " + classId);

        // Call API through ViewModel
        try {
            viewModel.getStudentsAttendance(authToken, selectedDate, selectedDate, classId)
                    .observe(this, response -> {
                        Log.d(TAG, "API response received");
                        hideLoading();

                        if (response != null) {
                            Log.d(TAG, "Response is not null");
                            Log.d(TAG, "Response success: " + response.isSuccess);
                            Log.d(TAG, "Response message: " + response.message);
                            Log.d(TAG, "Response data: " + (response.data != null ? "not null" : "null"));

                            if (response.isSuccess && response.data != null) {
                                Log.d(TAG, "API call successful");
                                handleAttendanceResponse(response.data);
                            } else {
                                Log.e(TAG, "API call failed");
                                String errorMessage = response.message != null ?
                                        response.message : "Failed to load attendance data";
                                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.e(TAG, "Response is null");
                            Toast.makeText(this, "No response received", Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (Exception e) {
            Log.e(TAG, "Exception during API call: " + e.getMessage(), e);
            hideLoading();
            Toast.makeText(this, "Error loading data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private String getAuthToken() {
        return pref.getPrefString(this,pref.user_token);
    }

    private void handleAttendanceResponse(GetAttendanceResponse attendanceResponse) {
        Log.d(TAG, "Handling attendance response");

        if (attendanceResponse == null) {
            Log.e(TAG, "Attendance response is null!");
            return;
        }

        List<GetAttendanceResponse.Datum> responseData = attendanceResponse.getData();
        Log.d(TAG, "Response data: " + (responseData != null ? "not null" : "null"));

        if (responseData != null) {
            Log.d(TAG, "Response data size: " + responseData.size());

            // Check if all students are NOT MARKED
            isAllStudentsNotMarked = checkIfAllStudentsNotMarked(responseData);
            Log.d(TAG, "All students NOT MARKED: " + isAllStudentsNotMarked);

            // Log individual student data
            for (int i = 0; i < responseData.size(); i++) {
                GetAttendanceResponse.Datum student = responseData.get(i);
                if (student != null) {
                    Log.d(TAG, "Student " + i + ": " + student.getStudentName() +
                            " (ID: " + student.getStudentId() + ", Status: " + student.getStatus() + ")");
                } else {
                    Log.w(TAG, "Student at index " + i + " is null!");
                }
            }
        }

        if (responseData != null && !responseData.isEmpty()) {
            Log.d(TAG, "Updating student list with " + responseData.size() + " students");

            // Update student list with API response data
            studentList.clear();
            studentList.addAll(responseData);

            Log.d(TAG, "Student list updated. New size: " + studentList.size());

            // Update adapter
            if (adapter != null) {
                Log.d(TAG, "Updating adapter with new data");
                adapter.updateStudentList(studentList);
                Log.d(TAG, "Adapter updated successfully");
            } else {
                Log.e(TAG, "Adapter is null!");
            }

            // Update attendance statistics
            updateAttendanceStats();

        } else {
            Log.w(TAG, "No students found in response");
            Toast.makeText(this, "No students found for this class", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Check if all students have status "NOT MARKED"
     */
    private boolean checkIfAllStudentsNotMarked(List<GetAttendanceResponse.Datum> students) {
        if (students == null || students.isEmpty()) {
            return false;
        }

        for (GetAttendanceResponse.Datum student : students) {
            if (student != null && student.getStatus() != null) {
                if (!"NOT MARKED".equals(student.getStatus())) {
                    Log.d(TAG, "Student " + student.getStudentName() + " has status: " + student.getStatus());
                    return false;
                }
            }
        }

        Log.d(TAG, "All students have NOT MARKED status");
        return true;
    }

    private void updateAttendanceStats() {
        Log.d(TAG, "Updating attendance statistics");

        if (studentList == null || studentList.isEmpty()) {
            Log.w(TAG, "Student list is null or empty, cannot update stats");
            return;
        }

        if (adapter == null) {
            Log.w(TAG, "Adapter is null, cannot update stats");
            return;
        }

        // Count attendance statistics
        int presentCount = 0;
        int absentCount = 0;
        int leaveCount = 0;
        int totalStudents = studentList.size();

        for (GetAttendanceResponse.Datum student : studentList) {
            if (adapter.isPresent(student)) {
                presentCount++;
            } else if (adapter.isAbsent(student)) {
                absentCount++;
            } else if (adapter.isOnLeave(student)) {
                leaveCount++;
            }
        }

        Log.d(TAG, "Attendance statistics updated:");
        Log.d(TAG, "Total: " + totalStudents);
        Log.d(TAG, "Present: " + presentCount);
        Log.d(TAG, "Absent: " + absentCount);
        Log.d(TAG, "Leave: " + leaveCount);
    }

    private void saveAttendanceRecords() {
        Log.d(TAG, "Saving attendance records");

        showLoading("Saving attendance...");

        // Create attendance request model
        PostStudentAttendance attendanceRequest = createAttendanceRequest();

        if (attendanceRequest == null || attendanceRequest.getAttendances().isEmpty()) {
            Log.e(TAG, "Failed to create attendance request or no attendance data");
            hideLoading();
            Toast.makeText(this, "No attendance data to save", Toast.LENGTH_SHORT).show();
            return;
        }

        // Log attendance statistics before saving
        logAttendanceStatistics(attendanceRequest.getAttendances());

        // Decide which API to use based on initial status
        if (isAllStudentsNotMarked) {
            Log.d(TAG, "Using POST API - All students were NOT MARKED initially");
            saveAttendanceToDatabase(attendanceRequest);
        } else {
            Log.d(TAG, "Using PATCH API - Some students were already marked");
            updateAttendanceInDatabase(attendanceRequest);
        }
    }

    private PostStudentAttendance createAttendanceRequest() {
        Log.d(TAG, "Creating attendance request model");

        if (studentList == null || studentList.isEmpty()) {
            Log.e(TAG, "Student list is null or empty");
            return null;
        }

        if (adapter == null) {
            Log.e(TAG, "Adapter is null");
            return null;
        }

        // Create the main request object
        PostStudentAttendance attendanceRequest = new PostStudentAttendance();
        attendanceRequest.setClassId(selectedClassId);
        attendanceRequest.setAttendanceDate(selectedDate);

        // Create attendance list
        ArrayList<StudentAttendance> attendanceList = new ArrayList<>();

        // Process each student
        for (GetAttendanceResponse.Datum student : studentList) {
            StudentAttendance studentAttendance = new StudentAttendance();
            studentAttendance.setStudentId(student.getStudentId());

            // Determine status based on adapter state
            String status = getStudentAttendanceStatus(student);
            studentAttendance.setStatus(status);

            attendanceList.add(studentAttendance);

            Log.d(TAG, "Added student: ID=" + student.getStudentId() +
                    ", Name=" + student.getStudentName() +
                    ", Status=" + status);
        }

        attendanceRequest.setAttendances(attendanceList);

        Log.d(TAG, "Attendance request created successfully");
        Log.d(TAG, "ClassId: " + attendanceRequest.getClassId());
        Log.d(TAG, "AttendanceDate: " + attendanceRequest.getAttendanceDate());
        Log.d(TAG, "Total students: " + attendanceList.size());

        return attendanceRequest;
    }

    private String getStudentAttendanceStatus(GetAttendanceResponse.Datum student) {
        if (adapter.isPresent(student)) {
            return "P"; // Present
        } else if (adapter.isAbsent(student)) {
            return "A"; // Absent
        } else if (adapter.isOnLeave(student)) {
            return "L"; // Leave
        } else {
            // Default to absent if no status is set
            Log.w(TAG, "No status set for student: " + student.getStudentName() + ", defaulting to Absent");
            return "A";
        }
    }

    private void logAttendanceStatistics(ArrayList<StudentAttendance> attendances) {
        int presentCount = 0;
        int absentCount = 0;
        int leaveCount = 0;

        for (StudentAttendance attendance : attendances) {
            switch (attendance.getStatus()) {
                case "P":
                    presentCount++;
                    break;
                case "A":
                    absentCount++;
                    break;
                case "L":
                    leaveCount++;
                    break;
            }
        }

        Log.d(TAG, "Attendance statistics:");
        Log.d(TAG, "Present: " + presentCount);
        Log.d(TAG, "Absent: " + absentCount);
        Log.d(TAG, "Leave: " + leaveCount);
        Log.d(TAG, "Total: " + attendances.size());
    }

    private void saveAttendanceToDatabase(PostStudentAttendance attendanceRequest) {
        Log.d(TAG, "Saving attendance to database using POST API");

        // Get auth token
        String authToken = "Bearer " + getAuthToken();

        if (authToken.equals("Bearer null") || authToken.equals("Bearer ")) {
            Log.e(TAG, "Auth token is null or empty!");
            hideLoading();
            Toast.makeText(this, "Authentication token not found", Toast.LENGTH_SHORT).show();
            return;
        }

        // Call POST API to save attendance
        try {
            viewModel.saveStudentAttendance(authToken, attendanceRequest)
                    .observe(this, response -> {
                        hideLoading();

                        if (response != null && response.isSuccess) {
                            Log.d(TAG, "Attendance saved successfully to database via POST");
                            handleSuccessfulSave(attendanceRequest.getAttendances());
                        } else {
                            Log.e(TAG, "Failed to save attendance to database via POST");
                            handleFailedSave(response);
                        }
                    });
        } catch (Exception e) {
            Log.e(TAG, "Exception during save attendance via POST: " + e.getMessage(), e);
            hideLoading();
            Toast.makeText(this, "Error saving attendance: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void updateAttendanceInDatabase(PostStudentAttendance attendanceRequest) {
        Log.d(TAG, "Updating attendance in database using PATCH API");

        // Get auth token
        String authToken = "Bearer " + getAuthToken();

        if (authToken.equals("Bearer null") || authToken.equals("Bearer ")) {
            Log.e(TAG, "Auth token is null or empty!");
            hideLoading();
            Toast.makeText(this, "Authentication token not found", Toast.LENGTH_SHORT).show();
            return;
        }

        // Call PATCH API to update attendance
        try {
            viewModel.updateStudentAttendance(authToken, attendanceRequest)
                    .observe(this, response -> {
                        hideLoading();

                        if (response != null && response.isSuccess) {
                            Log.d(TAG, "Attendance updated successfully in database via PATCH");
                            handleSuccessfulSave(attendanceRequest.getAttendances());
                        } else {
                            Log.e(TAG, "Failed to update attendance in database via PATCH");
                            handleFailedSave(response);
                        }
                    });
        } catch (Exception e) {
            Log.e(TAG, "Exception during update attendance via PATCH: " + e.getMessage(), e);
            hideLoading();
            Toast.makeText(this, "Error updating attendance: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void handleSuccessfulSave(ArrayList<StudentAttendance> attendances) {
        // Calculate and show statistics
        int presentCount = 0, absentCount = 0, leaveCount = 0;

        for (StudentAttendance attendance : attendances) {
            switch (attendance.getStatus()) {
                case "P":
                    presentCount++;
                    break;
                case "A":
                    absentCount++;
                    break;
                case "L":
                    leaveCount++;
                    break;
            }
        }

        // Show success message with statistics
        String successMessage = String.format(
                "Attendance %s successfully!\nPresent: %d, Absent: %d, Leave: %d",
                isAllStudentsNotMarked ? "saved" : "updated",
                presentCount, absentCount, leaveCount
        );

        Toast.makeText(this, successMessage, Toast.LENGTH_LONG).show();

        // Return to previous screen
        finish();
    }

    private void handleFailedSave(Object response) {
        String errorMessage = isAllStudentsNotMarked ?
                "Failed to save attendance" : "Failed to update attendance";

        // Try to extract error message from response
        if (response != null) {
            try {
                // Assuming your response has a message field
                java.lang.reflect.Field messageField = response.getClass().getDeclaredField("message");
                messageField.setAccessible(true);
                String responseMessage = (String) messageField.get(response);
                if (responseMessage != null && !responseMessage.isEmpty()) {
                    errorMessage = responseMessage;
                }
            } catch (Exception e) {
                Log.w(TAG, "Could not extract error message from response", e);
            }
        }

        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    // Enhanced validation method
    private boolean validateAttendance() {
        Log.d(TAG, "Validating attendance");

        if (studentList == null || studentList.isEmpty()) {
            Log.w(TAG, "Student list is null or empty");
            Toast.makeText(this, "No students found to mark attendance", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (adapter == null) {
            Log.e(TAG, "Adapter is null during validation");
            Toast.makeText(this, "Error: Adapter not initialized", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Check if all students have attendance marked
        ArrayList<String> unmarkedStudents = new ArrayList<>();

        for (GetAttendanceResponse.Datum student : studentList) {
            if (adapter.hasNoStatus(student)) {
                unmarkedStudents.add(student.getStudentName());
            }
        }

        if (!unmarkedStudents.isEmpty()) {
            Log.d(TAG, "Validation failed: " + unmarkedStudents.size() + " students unmarked");

            // Show specific students that are unmarked (limit to first 3 for UI)
            String message = "Please mark attendance for: ";
            if (unmarkedStudents.size() <= 3) {
                message += String.join(", ", unmarkedStudents);
            } else {
                message += String.join(", ", unmarkedStudents.subList(0, 3)) +
                        " and " + (unmarkedStudents.size() - 3) + " more";
            }

            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            return false;
        }

        Log.d(TAG, "Validation successful: All students have attendance marked");
        return true;
    }

    @Override
    public void onAttendanceChanged(int position, int status) {
        Log.d(TAG, "Attendance changed - Position: " + position + ", Status: " + status);

        // Update attendance statistics when any attendance is changed
        updateAttendanceStats();

        // Optional: Auto-save functionality
        // You can implement auto-save here if needed
        // autoSaveAttendance();
    }

    @Override
    public void onBackPressed() {
        // Check if there are unsaved changes
        if (hasUnsavedChanges()) {
            showUnsavedChangesDialog();
        } else {
            super.onBackPressed();
        }
    }

    private boolean hasUnsavedChanges() {
        // Check if any attendance has been marked
        if (studentList == null || studentList.isEmpty() || adapter == null) {
            return false;
        }

        for (GetAttendanceResponse.Datum student : studentList) {
            if (!adapter.hasNoStatus(student)) {
                return true; // Changes detected
            }
        }

        return false;
    }

    private void showUnsavedChangesDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Unsaved Changes")
                .setMessage("You have unsaved attendance changes. Do you want to save before leaving?")
                .setPositiveButton("Save", (dialog, which) -> {
                    if (validateAttendance()) {
                        saveAttendanceRecords();
                    } else {
                        Toast.makeText(this, "Please mark attendance for all students before saving",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Discard", (dialog, which) -> {
                    super.onBackPressed();
                })
                .setNeutralButton("Cancel", null)
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy called");

        // Clean up resources
        if (binding != null) {
            binding = null;
        }

        if (adapter != null) {
            adapter = null;
        }

        if (studentList != null) {
            studentList.clear();
            studentList = null;
        }
    }
}