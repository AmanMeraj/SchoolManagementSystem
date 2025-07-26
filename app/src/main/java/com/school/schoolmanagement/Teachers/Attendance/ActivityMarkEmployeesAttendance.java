package com.school.schoolmanagement.Teachers.Attendance;

import android.annotation.SuppressLint;
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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.school.schoolmanagement.Adapters.AdapterMarkEmployeeAttendance;
import com.school.schoolmanagement.GlobalViewModel.ViewModel;
import com.school.schoolmanagement.Model.GetEmployeeAttendance;
import com.school.schoolmanagement.Model.PostEmployeeAttendance;
import com.school.schoolmanagement.Model.EmployeeAttendance;
import com.school.schoolmanagement.R;
import com.school.schoolmanagement.Utils.Utility;
import com.school.schoolmanagement.databinding.ActivityMarkEmployeesAttendanceBinding;

import java.util.ArrayList;
import java.util.List;

public class ActivityMarkEmployeesAttendance extends AppCompatActivity implements AdapterMarkEmployeeAttendance.OnAttendanceChangeListener {

    private static final String TAG = "ActivityMarkEmployeesAttendance";

    private ActivityMarkEmployeesAttendanceBinding binding;
    private AdapterMarkEmployeeAttendance adapter;
    private List<GetEmployeeAttendance.Datum> employeeList;
    private String selectedDate;
    private String from="";
    private ViewModel viewModel;
    private boolean isAllEmployeesNotMarked = false;
    private Utility utility; // Create utility instance

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate called");

        EdgeToEdge.enable(this);
        binding = ActivityMarkEmployeesAttendanceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Log.d(TAG, "View binding completed");

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize utility
        utility = new Utility();

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(ViewModel.class);

        // Get data from intent
        selectedDate = getIntent().getStringExtra("selected_date");
        from = getIntent().getStringExtra("from");

        Log.d(TAG, "Intent data:");
        Log.d(TAG, "selectedDate: " + selectedDate);
        Log.d(TAG, "from: " + from);

        // Validate required data
        if (selectedDate == null || selectedDate.isEmpty()) {
            Log.e(TAG, "Selected date is null or empty");
            Toast.makeText(this, "Invalid date selected", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Log.d(TAG, "All required data validated successfully");

        setupToolbar();
        initEmployeeList();
        setupRecyclerView();
        setupSubmitButton();

        // Load attendance data from API
        loadAttendanceData();
    }

    private void setupToolbar() {
        Log.d(TAG, "Setting up toolbar");

        try {
                binding.toolbar.backBtn.setOnClickListener(v -> {
                    Log.d(TAG, "Back button clicked");
                    onBackPressed();
                });
                Log.d(TAG, "Back button setup completed");

        } catch (Exception e) {
            Log.e(TAG, "Error setting up toolbar: " + e.getMessage(), e);
        }
    }

    private void initEmployeeList() {
        Log.d(TAG, "Initializing employee list");
        employeeList = new ArrayList<>();
        Log.d(TAG, "Employee list initialized with size: " + employeeList.size());
    }

    private void setupRecyclerView() {
        Log.d(TAG, "Setting up RecyclerView");

        try {

            // Create adapter
            adapter = new AdapterMarkEmployeeAttendance(this, employeeList, this);

            // Set layout manager
            binding.rcMarkAttendance.setLayoutManager(new LinearLayoutManager(this));
            Log.d(TAG, "Layout manager set");

            // Set adapter
            binding.rcMarkAttendance.setAdapter(adapter);
            Log.d(TAG, "Adapter set to RecyclerView");
                binding.yearDate.setText(selectedDate);
                Log.d(TAG, "Header updated: " + selectedDate);


        } catch (Exception e) {
            Log.e(TAG, "Error setting up RecyclerView: " + e.getMessage(), e);
        }
    }

    private void setupSubmitButton() {
        Log.d(TAG, "Setting up submit button");

        try {
                binding.submitBtn.setOnClickListener(v -> {
                    Log.d(TAG, "Submit button clicked");
                    if (validateAttendance()) {
                        saveAttendanceRecords();
                    } else {
                        Log.w(TAG, "Attendance validation failed");
                    }
                });
                Log.d(TAG, "Submit button setup completed");
        } catch (Exception e) {
            Log.e(TAG, "Error setting up submit button: " + e.getMessage(), e);
        }
    }

    private void showLoading(String message) {
        Log.d(TAG, "Showing loading: " + message);

        try {
                binding.loader.rlLoader.setVisibility(View.VISIBLE);
                Log.d(TAG, "Loading indicator shown");
            runOnUiThread(() -> Toast.makeText(this, message, Toast.LENGTH_SHORT).show());
        } catch (Exception e) {
            Log.e(TAG, "Error showing loading: " + e.getMessage(), e);
        }
    }

    private void hideLoading() {
        Log.d(TAG, "Hiding loading");

        try {
                binding.loader.rlLoader.setVisibility(View.GONE);
                Log.d(TAG, "Loading indicator hidden");

        } catch (Exception e) {
            Log.e(TAG, "Error hiding loading: " + e.getMessage(), e);
        }
    }

    private void loadAttendanceData() {
        Log.d(TAG, "Loading attendance data");

        // Show loading indicator
        showLoading("Loading employees...");

        // Get auth token
        String authToken = "Bearer " + getAuthToken();

        if (authToken.equals("Bearer null") || authToken.equals("Bearer ")) {
            Log.e(TAG, "Auth token is null or empty!");
            hideLoading();
            Toast.makeText(this, "Authentication token not found", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "API call parameters:");
        Log.d(TAG, "authToken: " + authToken);
        Log.d(TAG, "selectedDate: " + selectedDate);

        // Call API through ViewModel
        try {
            viewModel.getEmployeeAttendance(authToken, selectedDate)
                    .observe(this, response -> {
                        Log.d(TAG, "API response received");
                        hideLoading();

                        if (response != null) {
                            Log.d(TAG, "Response is not null");
                            Log.d(TAG, "Response status: " + response.isSuccess);
                            Log.d(TAG, "Response message: " + response.message);
                            Log.d(TAG, "Response data: " + (response.data != null ? "not null" : "null"));

                            if (response.code == 200 && response.data != null) {
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
        if (utility != null && utility.pref != null) {
            return utility.pref.getPrefString(this, utility.pref.user_token);
        }
        return null;
    }

    private void handleAttendanceResponse(GetEmployeeAttendance attendanceResponse) {
        Log.d(TAG, "Handling attendance response");

        if (attendanceResponse == null) {
            Log.e(TAG, "Attendance response is null!");
            return;
        }

        List<GetEmployeeAttendance.Datum> responseData = attendanceResponse.getData();
        Log.d(TAG, "Response data: " + (responseData != null ? "not null" : "null"));

        if (responseData != null) {
            Log.d(TAG, "Response data size: " + responseData.size());

            // Check if all employees are NOT MARKED
            isAllEmployeesNotMarked = checkIfAllEmployeesNotMarked(responseData);
            Log.d(TAG, "All employees NOT MARKED: " + isAllEmployeesNotMarked);

            // Log individual employee data
            for (int i = 0; i < responseData.size(); i++) {
                GetEmployeeAttendance.Datum employee = responseData.get(i);
                if (employee != null) {
                    Log.d(TAG, "Employee " + i + ": " + employee.getEmployeeName() +
                            " (ID: " + employee.getEmployeeId() + ", Status: " + employee.getStatus() + ")");
                } else {
                    Log.w(TAG, "Employee at index " + i + " is null!");
                }
            }
        }

        if (responseData != null && !responseData.isEmpty()) {
            Log.d(TAG, "Updating employee list with " + responseData.size() + " employees");

            // Update employee list with API response data
            employeeList.clear();
            employeeList.addAll(responseData);

            Log.d(TAG, "Employee list updated. New size: " + employeeList.size());

            // Update adapter
            if (adapter != null) {
                Log.d(TAG, "Updating adapter with new data");
                adapter.updateEmployeeList(employeeList);
                Log.d(TAG, "Adapter updated successfully");
            } else {
                Log.e(TAG, "Adapter is null!");
            }

            // Update attendance statistics
            updateAttendanceStats();

        } else {
            Log.w(TAG, "No employees found in response");
            Toast.makeText(this, "No employees found for this department", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkIfAllEmployeesNotMarked(List<GetEmployeeAttendance.Datum> employees) {
        if (employees == null || employees.isEmpty()) {
            return false;
        }

        for (GetEmployeeAttendance.Datum employee : employees) {
            if (employee != null && employee.getStatus() != null) {
                if (!"NOT MARKED".equals(employee.getStatus())) {
                    Log.d(TAG, "Employee " + employee.getEmployeeName() + " has status: " + employee.getStatus());
                    return false;
                }
            }
        }

        Log.d(TAG, "All employees have NOT MARKED status");
        return true;
    }

    private void updateAttendanceStats() {
        Log.d(TAG, "Updating attendance statistics");

        if (employeeList == null || employeeList.isEmpty()) {
            Log.w(TAG, "Employee list is null or empty, cannot update stats");
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
        int totalEmployees = employeeList.size();

        for (GetEmployeeAttendance.Datum employee : employeeList) {
            if (adapter.isPresent(employee)) {
                presentCount++;
            } else if (adapter.isAbsent(employee)) {
                absentCount++;
            } else if (adapter.isOnLeave(employee)) {
                leaveCount++;
            }
        }

        Log.d(TAG, "Attendance statistics updated:");
        Log.d(TAG, "Total: " + totalEmployees);
        Log.d(TAG, "Present: " + presentCount);
        Log.d(TAG, "Absent: " + absentCount);
        Log.d(TAG, "Leave: " + leaveCount);
    }

    private void saveAttendanceRecords() {
        Log.d(TAG, "Saving attendance records");

        showLoading("Saving attendance...");

        // Create attendance request model
        PostEmployeeAttendance attendanceRequest = createAttendanceRequest();

        if (attendanceRequest == null || attendanceRequest.getAttendances().isEmpty()) {
            Log.e(TAG, "Failed to create attendance request or no attendance data");
            hideLoading();
            Toast.makeText(this, "No attendance data to save", Toast.LENGTH_SHORT).show();
            return;
        }

        // Log attendance statistics before saving
        logAttendanceStatistics(attendanceRequest.getAttendances());

        // Decide which API to use based on initial status
        if (isAllEmployeesNotMarked) {
            Log.d(TAG, "Using POST API - All employees were NOT MARKED initially");
            saveAttendanceToDatabase(attendanceRequest);
        } else {
            Log.d(TAG, "Using PATCH API - Some employees were already marked");
            updateAttendanceInDatabase(attendanceRequest);
        }
    }

    private PostEmployeeAttendance createAttendanceRequest() {
        Log.d(TAG, "Creating attendance request model");

        if (employeeList == null || employeeList.isEmpty()) {
            Log.e(TAG, "Employee list is null or empty");
            return null;
        }

        if (adapter == null) {
            Log.e(TAG, "Adapter is null");
            return null;
        }

        // Create the main request object
        PostEmployeeAttendance attendanceRequest = new PostEmployeeAttendance();
        attendanceRequest.setAttendanceDate(selectedDate);
        ArrayList<EmployeeAttendance> attendanceList = new ArrayList<>();

        // Process each employee
        for (GetEmployeeAttendance.Datum employee : employeeList) {
            EmployeeAttendance employeeAttendance = new EmployeeAttendance();
            employeeAttendance.setEmployeeId(employee.getEmployeeId());

            // Determine status based on adapter state
            String status = getEmployeeAttendanceStatus(employee);
            employeeAttendance.setStatus(status);

            attendanceList.add(employeeAttendance);

            Log.d(TAG, "Added employee: ID=" + employee.getEmployeeId() +
                    ", Name=" + employee.getEmployeeName() +
                    ", Status=" + status);
        }

        attendanceRequest.setAttendances(attendanceList);

        Log.d(TAG, "Attendance request created successfully");
        Log.d(TAG, "AttendanceDate: " + attendanceRequest.getAttendanceDate());
        Log.d(TAG, "Total employees: " + attendanceList.size());

        return attendanceRequest;
    }

    private String getEmployeeAttendanceStatus(GetEmployeeAttendance.Datum employee) {
        if (adapter.isPresent(employee)) {
            return "P"; // Present
        } else if (adapter.isAbsent(employee)) {
            return "A"; // Absent
        } else if (adapter.isOnLeave(employee)) {
            return "L"; // Leave
        } else {
            // Default to absent if no status is set
            Log.w(TAG, "No status set for employee: " + employee.getEmployeeName() + ", defaulting to Absent");
            return "A";
        }
    }

    private void logAttendanceStatistics(ArrayList<EmployeeAttendance> attendances) {
        int presentCount = 0;
        int absentCount = 0;
        int leaveCount = 0;

        for (EmployeeAttendance attendance : attendances) {
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

    private void saveAttendanceToDatabase(PostEmployeeAttendance attendanceRequest) {
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
            viewModel.saveEmployeeAttendance(authToken, attendanceRequest)
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

    private void updateAttendanceInDatabase(PostEmployeeAttendance attendanceRequest) {
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
            viewModel.updateEmployeeAttendance(authToken, attendanceRequest)
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

    private void handleSuccessfulSave(ArrayList<EmployeeAttendance> attendances) {
        // Calculate and show statistics
        int presentCount = 0, absentCount = 0, leaveCount = 0;

        for (EmployeeAttendance attendance : attendances) {
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
        @SuppressLint("DefaultLocale") String successMessage = String.format(
                "Attendance %s successfully!\nPresent: %d, Absent: %d, Leave: %d",
                isAllEmployeesNotMarked ? "saved" : "updated",
                presentCount, absentCount, leaveCount
        );

        Toast.makeText(this, successMessage, Toast.LENGTH_LONG).show();

        // Return to previous screen
        finish();
    }

    private void handleFailedSave(Object response) {
        String errorMessage = isAllEmployeesNotMarked ?
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

    private boolean validateAttendance() {
        Log.d(TAG, "Validating attendance");

        if (employeeList == null || employeeList.isEmpty()) {
            Log.w(TAG, "Employee list is null or empty");
            Toast.makeText(this, "No employees found to mark attendance", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (adapter == null) {
            Log.e(TAG, "Adapter is null during validation");
            Toast.makeText(this, "Error: Adapter not initialized", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Check if all employees have attendance marked
        ArrayList<String> unmarkedEmployees = new ArrayList<>();

        for (GetEmployeeAttendance.Datum employee : employeeList) {
            if (adapter.hasNoStatus(employee)) {
                unmarkedEmployees.add(employee.getEmployeeName());
            }
        }

        if (!unmarkedEmployees.isEmpty()) {
            Log.d(TAG, "Validation failed: " + unmarkedEmployees.size() + " employees unmarked");

            // Show specific employees that are unmarked (limit to first 3 for UI)
            String message = "Please mark attendance for: ";
            if (unmarkedEmployees.size() <= 3) {
                message += String.join(", ", unmarkedEmployees);
            } else {
                message += String.join(", ", unmarkedEmployees.subList(0, 3)) +
                        " and " + (unmarkedEmployees.size() - 3) + " more";
            }

            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            return false;
        }

        Log.d(TAG, "Validation successful: All employees have attendance marked");
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
        if (employeeList == null || employeeList.isEmpty() || adapter == null) {
            return false;
        }

        for (GetEmployeeAttendance.Datum employee : employeeList) {
            if (!adapter.hasNoStatus(employee)) {
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
                        Toast.makeText(this, "Please mark attendance for all employees before saving",
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

        if (employeeList != null) {
            employeeList.clear();
            employeeList = null;
        }

        if (utility != null) {
            utility = null;
        }
    }
}