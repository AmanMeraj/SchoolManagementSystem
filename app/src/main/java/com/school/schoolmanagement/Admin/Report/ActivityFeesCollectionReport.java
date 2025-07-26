package com.school.schoolmanagement.Admin.Report;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.school.schoolmanagement.Adapters.AdapterFeesCollectionReport;
import com.school.schoolmanagement.Admin.Model.Record;
import com.school.schoolmanagement.GlobalRepository.GlobalRepository;
import com.school.schoolmanagement.GlobalViewModel.ViewModel;
import com.school.schoolmanagement.HelperClasses.DataExportHelper;
import com.school.schoolmanagement.Model.FeesCollectionEntries;
import com.school.schoolmanagement.Model.FeesCollectionReport;
import com.school.schoolmanagement.R;
import com.school.schoolmanagement.Utils.Utility;
import com.school.schoolmanagement.databinding.ActivityFeesCollectionReportBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class ActivityFeesCollectionReport extends Utility {
ActivityFeesCollectionReportBinding binding;
    private AdapterFeesCollectionReport adapter;
    private ArrayList<FeesCollectionEntries> recordList;

    // Pagination variables
    private int currentPage = 0;
    private final int pageLimit = 20;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private ViewModel viewModel;

    // Intent data variables
    private String reportType;
    private String startDate;
    private String endDate;
    private int classId;
    private int studentId;
    private String authToken;

    // Loading states
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding=ActivityFeesCollectionReportBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        viewModel = new ViewModelProvider(this).get(ViewModel.class);

        // Initialize RecyclerView and Adapter
        setupRecyclerView();

        // Get intent data and determine report type
        receiveIntentData();

        // Setup UI based on report type
        setupReportUI();

        // Generate the appropriate report
        generateReport();
        setupExportButtons();
    }

    private void setupRecyclerView() {
        recordList = new ArrayList<>();
        adapter = new AdapterFeesCollectionReport(this, recordList);

        // CRITICAL: Set LayoutManager - this is likely missing
        binding.rcAccountReport.setLayoutManager(new LinearLayoutManager(this));
        binding.rcAccountReport.setAdapter(adapter);

        // Optional: Add item decorations for better spacing
        // binding.rcAccountReport.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        Log.d(TAG, "RecyclerView setup complete with LayoutManager");
    }


    private void receiveIntentData() {
        Intent intent = getIntent();
        if (intent != null) {
            // Get common data
            reportType = intent.getStringExtra("from");
            startDate = intent.getStringExtra("startDate");
            endDate = intent.getStringExtra("endDate");

            // Get auth token (you might get this from SharedPreferences or intent)
            authToken = getAuthToken(); // Implement this method to get your auth token

            // Check report type and get specific data
            if ("wholeClass".equals(reportType)) {
                classId = intent.getIntExtra("classId",0);
                Log.d(TAG, "Received whole class report request - ClassID: " + classId +
                        ", DateRange: " + startDate + " - " + endDate);
            } else if ("singleStudent".equals(reportType)) {
                studentId = intent.getIntExtra("studentId",0);
                Log.d(TAG, "Received single student report request - StudentID: " + studentId +
                        ", DateRange: " + startDate + " - " + endDate);
            }
        }
    }

    private void setupReportUI() {
        if ("wholeClass".equals(reportType)) {
            setupWholeClassUI();
        } else if ("singleStudent".equals(reportType)) {
            setupSingleStudentUI();
        } else {
            showToast("❌ Invalid report type");
            finish();
        }
    }

    private void setupWholeClassUI() {
        // Set title for whole class report
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Class Fees Collection Report");
        }

        showToast("📊 Loading class fees collection report...");
        Log.d(TAG, "Setting up UI for whole class report");
    }

    private void setupSingleStudentUI() {
        // Set title for single student report
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Student Fees Collection Report");
        }

        showToast("📊 Loading student fees collection report...");
        Log.d(TAG, "Setting up UI for single student report");
    }

    private void generateReport() {
        if ("wholeClass".equals(reportType)) {
            generateWholeClassReport();
        } else if ("singleStudent".equals(reportType)) {
            generateSingleStudentReport();
        }
    }

    private void generateWholeClassReport() {
        // Validate required data
        if (classId == 0 || classId == -1) {
            showToast("❌ Class ID is missing");
            finish();
            return;
        }

        if (!isValidDateRange()) {
            showToast("❌ Invalid date range");
            finish();
            return;
        }

        Log.d(TAG, "Generating whole class fees collection report...");
        Log.d(TAG, "Parameters - ClassID: " + classId + ", Start: " + startDate + ", End: " + endDate);

        // Call API to fetch account report data
       fetchClassFeesCollectionData();
    }

    private void generateSingleStudentReport() {
        // Validate required data
        if (studentId == 0 || studentId ==-1) {
            showToast("❌ Student ID is missing");
            finish();
            return;
        }

        if (!isValidDateRange()) {
            showToast("❌ Invalid date range");
            finish();
            return;
        }

        Log.d(TAG, "Generating single student fees collection report...");
        Log.d(TAG, "Parameters - StudentID: " + studentId + ", Start: " + startDate + ", End: " + endDate);

        // Call API to fetch account report data
       fetchStudentFeesCollectionData();
    }

    private void fetchClassFeesCollectionData() {
        if (isLoading) return;

        isLoading = true;
        showProgressBar(true);

        // Call the API through ViewModel
        viewModel.getClassFeesCollectionReport(authToken, startDate, endDate, currentPage, pageLimit,classId)
                .observe(this, accountReport -> { // Changed from 'response' to 'accountReport'
                    isLoading = false;
                    showProgressBar(false);

                    if (accountReport != null) {
                        // Check status instead of isSuccess
                        if (accountReport.code == 200 && accountReport.data != null) {
                            handleSuccessResponse(accountReport);
                        } else {
                            handleErrorResponse(accountReport.message);
                        }
                    } else {
                        showToast("❌ Failed to fetch data");
                        Log.e(TAG, "AccountReport response is null");
                    }
                });
    }
    private void fetchStudentFeesCollectionData() {
        if (isLoading) return;

        isLoading = true;
        showProgressBar(true);

        // Call the API through ViewModel
        viewModel.getStudentFeesCollectionReport(authToken, startDate, endDate, currentPage, pageLimit,studentId)
                .observe(this, accountReport -> { // Changed from 'response' to 'accountReport'
                    isLoading = false;
                    showProgressBar(false);

                    if (accountReport != null) {
                        // Check status instead of isSuccess
                        if (accountReport.code == 200 && accountReport.data != null) {
                            handleSuccessResponse(accountReport);
                        } else {
                            handleErrorResponse(accountReport.message);
                        }
                    } else {
                        showToast("❌ Failed to fetch data");
                        Log.e(TAG, "AccountReport response is null");
                    }
                });
    }

    private void handleSuccessResponse(GlobalRepository.ApiResponse<FeesCollectionReport> accountReport) {
        try {
            Log.d(TAG, "handleSuccessResponse called");
            Log.d(TAG, "AccountReport object: " + (accountReport != null ? "not null" : "null"));

            if (accountReport != null && accountReport.data != null) {
                Log.d(TAG, "AccountReport.data is not null");

                // Check if getRecords() method exists and returns data
                ArrayList<FeesCollectionEntries> newRecords = accountReport.data.data.getEntries();
                Log.d(TAG, "Records retrieved: " + (newRecords != null ? newRecords.size() + " records" : "null"));

                if (newRecords != null) {
                    for (int i = 0; i < newRecords.size(); i++) {
                        FeesCollectionEntries record = newRecords.get(i);
                        Log.d(TAG, "Record " + i + ": ID=" + record.getId() +
                                ", Description=" + record.getStudentName() +
                                ", Amount=" + record.getPaidAmount() +
                                ", Date=" + record.getDate());
                    }
                }

                if (newRecords != null && !newRecords.isEmpty()) {
                    if (currentPage == 0) { // ISSUE 1: You're checking currentPage == 1 but initializing it as 0
                        adapter.updateList(newRecords);
                        showToast("✅ Report loaded successfully");
                        Log.d(TAG, "Adapter updated with " + newRecords.size() + " records");
                    } else {
                        adapter.addRecords(newRecords);
                        Log.d(TAG, "Added " + newRecords.size() + " more records to adapter");
                    }

                    isLastPage = newRecords.size() < pageLimit;
                    currentPage++; // Move this increment to after successful processing

                    // ISSUE 2: Add this to verify adapter has data
                    Log.d(TAG, "Adapter item count after update: " + adapter.getItemCount());
                } else {
                    if (currentPage == 0) {
                        showToast("📄 No records found for the selected criteria");
                        Log.d(TAG, "No records found");
                    }
                    isLastPage = true;
                }
            } else {
                Log.e(TAG, "AccountReport or AccountReport.data is null");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error processing response: " + e.getMessage(), e);
            showToast("❌ Error processing data");
        }
    }

    private void handleErrorResponse(String errorMessage) {
        Log.e(TAG, "API Error: " + errorMessage);
        showToast("❌ " + (errorMessage != null ? errorMessage : "Failed to load data"));

        if (currentPage == 1) {
            // Show empty state or retry option
            adapter.clearRecords();
        }
    }

    private void showProgressBar(boolean show) {

        binding.loader.rlLoader.setVisibility(show ? View.VISIBLE : View.GONE);

    }

    private boolean isValidDateRange() {
        return startDate != null && !startDate.isEmpty() &&
                endDate != null && !endDate.isEmpty();
    }

    private String getAuthToken() {
        return "Bearer "+pref.getPrefString(this,pref.user_token); // Replace with actual implementation
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    // Optional: Method to refresh data
    public void refreshData() {
        currentPage = 0;
        isLastPage = false;
        adapter.clearRecords();
        if ("wholeClass".equals(reportType)) {
            fetchClassFeesCollectionData();
        } else if ("singleStudent".equals(reportType)) {
            fetchStudentFeesCollectionData();
        }
    }

    // Optional: Method to load more data (for pagination)
    public void loadMoreData() {
        if ("wholeClass".equals(reportType)) {
            fetchClassFeesCollectionData();
        } else if ("singleStudent".equals(reportType)) {
            fetchStudentFeesCollectionData();
        }
    }

    // Add these methods to your ActivityFeesCollectionReport class

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

    // Handle export functionality
    private void handleExport(ArrayList<ArrayList<String>> tableData, String action) {
        if (tableData.size() <= 1) { // Only headers, no data
            Toast.makeText(this, "No data to export", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Create DataExportHelper instance with context
            DataExportHelper exportHelper = new DataExportHelper(this);

            // Generate dynamic filename based on report type and selected criteria
            String fileName = generateDynamicFileName();

            // Use the exportData method with dynamic filename
            exportHelper.exportData(tableData, action, fileName);

        } catch (Exception e) {
            Log.e(TAG, "Export error: " + e.getMessage());
            Toast.makeText(this, "Export failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    // Generate dynamic filename based on report type
    private String generateDynamicFileName() {
        String fileName = "fees_collection_report";

        // Include report type in filename
        if ("wholeClass".equals(reportType)) {
            fileName += "_class_report";
            // You can add class name if you have it stored
            // fileName += "_" + className.replace(" ", "_");
        } else if ("singleStudent".equals(reportType)) {
            fileName += "_student_report";
            // You can add student name if you have it stored
            // fileName += "_" + studentName.replace(" ", "_");
        }

        // Include date range in filename
        if (startDate != null && endDate != null) {
            fileName += "_" + startDate.replace("/", "-") + "_to_" + endDate.replace("/", "-");
        }

        // Add timestamp for uniqueness
        fileName += "_" + System.currentTimeMillis();

        return fileName;
    }

    // Prepare table data for export
    private ArrayList<ArrayList<String>> prepareTableData() {
        ArrayList<ArrayList<String>> tableData = new ArrayList<>();

        // Add header row
        ArrayList<String> headers = new ArrayList<>();
        headers.add("Sr");
        headers.add("ID");
        headers.add("Student Name");
        headers.add("Date");
        headers.add("Paid Amount");
        headers.add("Payment Method"); // Add if available in your model
        headers.add("Status"); // Add if available in your model
        tableData.add(headers);

        int count = 1;
        // Add data rows from recordList
        for (FeesCollectionEntries record : recordList) {
            ArrayList<String> row = new ArrayList<>();
            row.add(String.valueOf(count));
            row.add(record.getId() != -1 ? String.valueOf(record.getId()) : "");
            row.add(record.getStudentName() != null ? record.getStudentName() : "");
            row.add(record.getDate() != null ? record.getDate() : "");
            row.add(record.getPaidAmount() != -1 ? String.valueOf(record.getPaidAmount()) : "0");

            // Add these fields if they exist in your FeesCollectionEntries model
            // row.add(record.getPaymentMethod() != null ? record.getPaymentMethod() : "");
            // row.add(record.getReceiptNumber() != null ? record.getReceiptNumber() : "");
            // row.add(record.getStatus() != null ? record.getStatus() : "");

            // If the above fields don't exist, you can add placeholder values or remove these lines
            row.add("Cash"); // Default payment method
            row.add("Paid"); // Default status

            tableData.add(row);
            count++;
        }

        return tableData;
    }

// Call this method in your onCreate() after setupRecyclerView()
// Add this line in your onCreate method:
// setupExportButtons();
}