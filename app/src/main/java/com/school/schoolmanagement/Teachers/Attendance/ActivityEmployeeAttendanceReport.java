package com.school.schoolmanagement.Teachers.Attendance;

import static android.content.ContentValues.TAG;

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
import androidx.core.util.Pair;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import com.school.schoolmanagement.GlobalViewModel.ViewModel;
import com.school.schoolmanagement.HelperClasses.DataExportHelper;
import com.school.schoolmanagement.Model.StudentDetails;
import com.school.schoolmanagement.R;
import com.school.schoolmanagement.Adapters.EmployeeAttendanceAdapter;
import com.school.schoolmanagement.Model.EmployeeAttendanceReport;
import com.school.schoolmanagement.Utils.Utility;
import com.school.schoolmanagement.databinding.ActivityEmployeeAttendanceReport2Binding;
import com.school.schoolmanagement.GlobalRepository.GlobalRepository;

public class ActivityEmployeeAttendanceReport extends Utility {

    private static final String TAG = "EmployeeAttendanceReport";

    private ArrayList<EmployeeAttendanceReport.Datum> attendanceList;
    private EmployeeAttendanceAdapter adapter;
    private ViewModel viewModel;

    // Store current date range
    private String currentStartDate;
    private String currentEndDate;
    private String authToken;

    private ActivityEmployeeAttendanceReport2Binding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityEmployeeAttendanceReport2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(ViewModel.class);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize current month date range
        initializeCurrentMonthDateRange();

        // Initialize UI
        initializeUI();

        // Set up click listeners
        setupClickListeners();

        // Initialize RecyclerView
        setupRecyclerView();

        // Set up observers once
        setupObservers();
        setupExportButtons();

        // Load data from API
        loadAttendanceDataFromAPI();
    }

    private void initializeCurrentMonthDateRange() {
        Calendar calendar = Calendar.getInstance();

        // Set to first day of current month
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        Date startDate = calendar.getTime();

        // Set to last day of current month
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        Date endDate = calendar.getTime();

        // Format dates for API
        SimpleDateFormat apiFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        currentStartDate = apiFormat.format(startDate);
        currentEndDate = apiFormat.format(endDate);

        // Get auth token from SharedPreferences
        authToken = getAuthToken();

        Log.d(TAG, "Initialized date range: " + currentStartDate + " to " + currentEndDate);
    }

    private void initializeUI() {
        // Format dates for display
        SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        SimpleDateFormat apiFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        try {
            Date startDate = apiFormat.parse(currentStartDate);
            Date endDate = apiFormat.parse(currentEndDate);

            if (startDate != null && endDate != null) {
                binding.dateRangeText.setText("From: " + displayFormat.format(startDate) + " To: " + displayFormat.format(endDate));
            }
        } catch (Exception e) {
            Log.e(TAG, "Error formatting dates for display: " + e.getMessage());
            binding.dateRangeText.setText("From: " + currentStartDate + " To: " + currentEndDate);
        }

        // Set up toolbar if you have one
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Employee Attendance Report");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupClickListeners() {
        // Set click listener on the date range card
        binding.dateRangeCard.setOnClickListener(v -> showDateRangePicker());
        binding.toolbar.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void setupRecyclerView() {
        attendanceList = new ArrayList<>();
        adapter = new EmployeeAttendanceAdapter(this, attendanceList);
        binding.rcRowAttendanceReport.setAdapter(adapter);
    }

    private void setupObservers() {
        // This method is called once to set up observers
        // We'll observe dynamically when making API calls
    }

    private void loadAttendanceDataFromAPI() {
        showLoading();

        // Make the API call and observe the response
        viewModel.getEmployeeAttendanceReport(authToken, currentStartDate, currentEndDate)
                .observe(this, apiResponse -> {
                    // Always hide loading first
                    hideLoading();

                    if (apiResponse != null) {
                        if (apiResponse.isSuccess && apiResponse.data != null) {
                            handleSuccessResponse(apiResponse.data);
                        } else {
                            handleErrorResponse(apiResponse.message, apiResponse.code);
                        }
                    } else {
                        showError("Unknown error occurred");
                    }
                });
    }

    private void handleSuccessResponse(EmployeeAttendanceReport attendanceReport) {
        if (attendanceReport.getStatus() == 200) {
            // Clear existing data
            attendanceList.clear();

            // Add new data
            if (attendanceReport.getData() != null && !attendanceReport.getData().isEmpty()) {
                attendanceList.addAll(attendanceReport.getData());

                // Sort the data and refresh the adapter
                adapter.sortAndRefresh();

                // Update UI to show data loaded
                binding.rcRowAttendanceReport.setVisibility(View.VISIBLE);
                // Hide empty state if you have one
                // binding.emptyStateLayout.setVisibility(View.GONE);

                showSuccess("Employee attendance data loaded successfully");

                Log.d(TAG, "Successfully loaded " + attendanceList.size() + " attendance records");
            } else {
                showEmptyState();
            }
        } else {
            showError(attendanceReport.getMessage());
        }
    }

    private void handleErrorResponse(String errorMessage, int responseCode) {
        Log.e(TAG, "API Error - Code: " + responseCode + ", Message: " + errorMessage);

        String userMessage = "Failed to load employee attendance data";
        if (errorMessage != null && !errorMessage.isEmpty()) {
            userMessage = errorMessage;
        }

        showError(userMessage);
    }

    private void showDateRangePicker() {
        // Create the date range picker
        MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
        builder.setTitleText("Select Date Range");

        // Set selection to current dates if available
        if (currentStartDate != null && currentEndDate != null) {
            try {
                SimpleDateFormat apiFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Date startDate = apiFormat.parse(currentStartDate);
                Date endDate = apiFormat.parse(currentEndDate);

                if (startDate != null && endDate != null) {
                    builder.setSelection(new Pair<>(startDate.getTime(), endDate.getTime()));
                }
            } catch (Exception e) {
                Log.e(TAG, "Error parsing current dates: " + e.getMessage());
            }
        }

        // Create the picker
        MaterialDatePicker<Pair<Long, Long>> picker = builder.build();

        // Add positive button click listener
        picker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
            @Override
            public void onPositiveButtonClick(Pair<Long, Long> selection) {
                handleDateRangeSelection(selection);
            }
        });

        // Show the picker
        picker.show(getSupportFragmentManager(), picker.toString());
    }

    private void handleDateRangeSelection(Pair<Long, Long> selection) {
        // Format the dates for display and API
        SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        SimpleDateFormat apiFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        displayFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        apiFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        Date startDate = new Date(selection.first);
        Date endDate = new Date(selection.second);

        // Update current date range for API calls
        currentStartDate = apiFormat.format(startDate);
        currentEndDate = apiFormat.format(endDate);

        // Update the TextView with the selected date range
        binding.dateRangeText.setText("From: " + displayFormat.format(startDate) + " To: " + displayFormat.format(endDate));

        Log.d(TAG, "Date range changed to: " + currentStartDate + " - " + currentEndDate);

        // Load new data with updated date range
        loadAttendanceDataFromAPI();
    }

    private void showLoading() {
        // Show loading indicator
        if (binding.loader != null) {
            binding.loader.rlLoader.setVisibility(View.VISIBLE);
        }
        // Disable date picker while loading
        binding.dateRangeCard.setEnabled(false);

        Log.d(TAG, "Loading started");
    }

    private void hideLoading() {
        // Hide loading indicator
        if (binding.loader != null) {
            binding.loader.rlLoader.setVisibility(View.GONE);
        }
        // Enable date picker
        binding.dateRangeCard.setEnabled(true);

        Log.d(TAG, "Loading finished");
    }

    private void showSuccess(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void showEmptyState() {
        // Show empty state
        // binding.emptyStateLayout.setVisibility(View.VISIBLE);
        binding.rcRowAttendanceReport.setVisibility(View.GONE);
        showError("No employee attendance data found for the selected period");
    }

    private String getAuthToken() {
        return "Bearer " + pref.getPrefString(this, pref.user_token);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clear the binding reference to avoid memory leaks
        binding = null;
    }

    private void setupExportButtons() {
        // Copy button
        binding.tvCopy.setOnClickListener(view -> {
            ArrayList<ArrayList<String>> tableData = prepareTableData();
            handleExport(tableData, "copy");
        });

        // CSV button
        binding.tvCsv.setOnClickListener(view -> {
            ArrayList<ArrayList<String>> tableData = prepareTableData();
            handleExport(tableData, "csv");
        });

        // Excel button
        binding.tvExcel.setOnClickListener(view -> {
            ArrayList<ArrayList<String>> tableData = prepareTableData();
            handleExport(tableData, "excel");
        });

        // PDF button
        binding.tvPdf.setOnClickListener(view -> {
            ArrayList<ArrayList<String>> tableData = prepareTableData();
            handleExport(tableData, "pdf");
        });

        // Print button
        binding.tvPrint.setOnClickListener(view -> {
            ArrayList<ArrayList<String>> tableData = prepareTableData();
            handleExport(tableData, "print");
        });
    }

    private void handleExport(ArrayList<ArrayList<String>> tableData, String action) {
        if (tableData.size() <= 1) { // Only headers, no data
            Toast.makeText(this, "No data to export", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Create DataExportHelper instance with context
            DataExportHelper exportHelper = new DataExportHelper(this);

            // Generate dynamic filename based on selected date range
            String fileName = generateDynamicFileName();

            // Use the exportData method with dynamic filename
            exportHelper.exportData(tableData, action, fileName);

        } catch (Exception e) {
            Log.e(TAG, "Export error: " + e.getMessage());
            Toast.makeText(this, "Export failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private String generateDynamicFileName() {
        // Format the date range for filename
        String fileName = "employee_attendance_report";

        if (currentStartDate != null && currentEndDate != null) {
            // Convert dates to a more readable format for filename
            try {
                SimpleDateFormat apiFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                SimpleDateFormat fileFormat = new SimpleDateFormat("yyyy_MM_dd", Locale.getDefault());

                Date startDate = apiFormat.parse(currentStartDate);
                Date endDate = apiFormat.parse(currentEndDate);

                if (startDate != null && endDate != null) {
                    fileName = "employee_attendance_" + fileFormat.format(startDate) + "_to_" + fileFormat.format(endDate);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error formatting filename dates: " + e.getMessage());
                fileName = "employee_attendance_report";
            }
        }

        return fileName;
    }

    private ArrayList<ArrayList<String>> prepareTableData() {
        ArrayList<ArrayList<String>> tableData = new ArrayList<>();

        // Add header row matching your XML table headers
        ArrayList<String> headers = new ArrayList<>();
        headers.add("Sr");
        headers.add("Date");
        headers.add("Day");
        headers.add("ID");
        headers.add("Name");
        headers.add("Status");
        tableData.add(headers);

        // Add data rows from your attendanceList
        for (int i = 0; i < attendanceList.size(); i++) {
            EmployeeAttendanceReport.Datum attendance = attendanceList.get(i);
            ArrayList<String> row = new ArrayList<>();

            // Date
            row.add(String.valueOf(i+1));
            row.add(attendance.getDate() != null ? attendance.getDate() : "");

            // Day - you may need to calculate this or get from your data model
            row.add(attendance.getDay() != null ? attendance.getDay() : "");

            // ID
            row.add(attendance.getEmployeeId() != null ? String.valueOf(attendance.getEmployeeId()) : "");

            // Name
            row.add(attendance.getEmployeeName() != null ? attendance.getEmployeeName() : "");

            // Status
            row.add(attendance.getStatus() != null ? attendance.getStatus() : "");

            tableData.add(row);
        }

        return tableData;
    }
}