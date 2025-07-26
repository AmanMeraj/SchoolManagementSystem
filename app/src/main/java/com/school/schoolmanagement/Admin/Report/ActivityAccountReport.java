package com.school.schoolmanagement.Admin.Report;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.school.schoolmanagement.Adapters.AdapterAccountReport;
import com.school.schoolmanagement.Adapters.AdapterFeesCollectionReport;
import com.school.schoolmanagement.Admin.Model.AccountReport;
import com.school.schoolmanagement.Admin.Model.Record;
import com.school.schoolmanagement.GlobalRepository.GlobalRepository;
import com.school.schoolmanagement.GlobalViewModel.ViewModel;
import com.school.schoolmanagement.HelperClasses.DataExportHelper;
import com.school.schoolmanagement.R;
import com.school.schoolmanagement.Utils.Utility;
import com.school.schoolmanagement.databinding.ActivityAccountReportBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class ActivityAccountReport extends Utility {

    private static final String TAG = "ActivityAccountReport";

    ActivityAccountReportBinding binding;

    // ViewModel and Adapter
    AdapterAccountReport adapter;
    private ViewModel viewModel;
    private ArrayList<Record> recordList;

    // Pagination variables
    private int currentPage = 0;
    private final int pageLimit = 20;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private String startDate;
    private String endDate;
    private String authToken;
    private Calendar startCalendar = Calendar.getInstance();
    private Calendar endCalendar = Calendar.getInstance();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityAccountReportBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize components
        initializeComponents();
        setupClickListeners();
    }

    private void initializeComponents() {
        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(ViewModel.class);

        // Get auth token
        authToken = getAuthToken();

        // Initialize RecyclerView and Adapter
        setupRecyclerView();

        Log.d(TAG, "Components initialized successfully");
    }

    private void setupRecyclerView() {
        recordList = new ArrayList<>();
        adapter = new AdapterAccountReport(this, recordList);

        // Set LayoutManager
        binding.rcAccountReport.setLayoutManager(new LinearLayoutManager(this));
        binding.rcAccountReport.setAdapter(adapter);

        Log.d(TAG, "RecyclerView setup complete with LayoutManager");
    }

    private void setupClickListeners() {
        // Date EditText click listener
        binding.edtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateRangePicker();
            }
        });
        setupExportButtons();

        // Generate Report Button click listener (assuming you have a button with id btnGenerateReport)
        // Replace 'btnGenerateReport' with your actual button ID
        if (binding.btnCreatePaper != null) {
            binding.btnCreatePaper.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    generateReport();
                }
            });
        }

        // Back button or toolbar navigation
        if (binding.toolbar != null && binding.toolbar.backBtn != null) {
            binding.toolbar.backBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }

    private void showDateRangePicker() {
        // Show start date picker first
        DatePickerDialog startDatePicker = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        startCalendar.set(Calendar.YEAR, year);
                        startCalendar.set(Calendar.MONTH, month);
                        startCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        startDate = dateFormat.format(startCalendar.getTime());

                        // After start date is selected, show end date picker
                        showEndDatePicker();
                    }
                },
                startCalendar.get(Calendar.YEAR),
                startCalendar.get(Calendar.MONTH),
                startCalendar.get(Calendar.DAY_OF_MONTH)
        );

        startDatePicker.setTitle("Select Start Date");
        startDatePicker.show();
    }

    private void showEndDatePicker() {
        // Set minimum date for end date picker to be start date
        DatePickerDialog endDatePicker = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        endCalendar.set(Calendar.YEAR, year);
                        endCalendar.set(Calendar.MONTH, month);
                        endCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        endDate = dateFormat.format(endCalendar.getTime());

                        // Validate date range
                        if (endCalendar.before(startCalendar)) {
                            showToast("❌ End date cannot be before start date");
                            endDate = "";
                            return;
                        }

                        // Update the EditText with the selected date range
                        String dateRange = startDate + " - " + endDate;
                        binding.edtDate.setText(dateRange);
                        showToast("✅ Date range selected: " + dateRange);
                    }
                },
                endCalendar.get(Calendar.YEAR),
                endCalendar.get(Calendar.MONTH),
                endCalendar.get(Calendar.DAY_OF_MONTH)
        );

        endDatePicker.setTitle("Select End Date");
        // Set minimum date to start date
        endDatePicker.getDatePicker().setMinDate(startCalendar.getTimeInMillis());
        endDatePicker.show();
    }

    private void generateReport() {
        // Check internet connectivity first
        if (!isInternetConnected(this)) {
            showToast("❌ No internet connection. Please check your network.");
            return;
        }

        // Validate date range
        if (!isValidDateRange()) {
            showToast("❌ Please select a valid date range");
            return;
        }

        // Reset pagination for new search
        currentPage = 0;
        isLastPage = false;

        // Clear existing data
        if (adapter != null) {
            adapter.clearRecords();
        }

        // Call API to fetch account report data
        fetchAccountReportData();
    }

    private void fetchAccountReportData() {
        if (isLoading) return;

        // Check internet connectivity before making API call
        if (!isInternetConnected(this)) {
            showToast("❌ No internet connection");
            return;
        }

        isLoading = true;
        showProgressBar(true);

        Log.d(TAG, "Fetching data - Page: " + currentPage + ", StartDate: " + startDate + ", EndDate: " + endDate);

        // Call the API through ViewModel
        viewModel.getAccountReport(authToken, startDate, endDate, currentPage, pageLimit)
                .observe(this, accountReport -> {
                    isLoading = false;
                    showProgressBar(false);

                    if (accountReport != null) {
                        // Check status
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

    private void handleSuccessResponse(GlobalRepository.ApiResponse<AccountReport> accountReport) {
        try {
            Log.d(TAG, "handleSuccessResponse called");

            if (accountReport != null && accountReport.data != null) {
                // Get records from the response
                ArrayList<Record> newRecords = accountReport.data.data.getRecords();
                Log.d(TAG, "Records retrieved: " + (newRecords != null ? newRecords.size() + " records" : "null"));

                if (newRecords != null && !newRecords.isEmpty()) {
                    if (currentPage == 0) {
                        // First page - update list
                        adapter.updateList(newRecords);
                        showToast("✅ Report loaded successfully");
                        Log.d(TAG, "Adapter updated with " + newRecords.size() + " records");
                    } else {
                        // Subsequent pages - add to existing list
                        adapter.addRecords(newRecords);
                        Log.d(TAG, "Added " + newRecords.size() + " more records to adapter");
                    }

                    // Check if this is the last page
                    isLastPage = newRecords.size() < pageLimit;
                    currentPage++;

                    Log.d(TAG, "Adapter item count after update: " + adapter.getItemCount());
                } else {
                    if (currentPage == 0) {
                        showToast("📄 No records found for the selected date range");
                        Log.d(TAG, "No records found");
                    }
                    isLastPage = true;
                }
            } else {
                Log.e(TAG, "AccountReport or AccountReport.data is null");
                showToast("❌ Invalid response from server");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error processing response: " + e.getMessage(), e);
            showToast("❌ Error processing data");
        }
    }

    private void handleErrorResponse(String errorMessage) {
        Log.e(TAG, "API Error: " + errorMessage);
        showToast("❌ " + (errorMessage != null ? errorMessage : "Failed to load data"));

        if (currentPage == 0) {
            // Show empty state for first page error
            if (adapter != null) {
                adapter.clearRecords();
            }
        }
    }

    private void showProgressBar(boolean show) {
        if (binding.loader != null && binding.loader.rlLoader != null) {
            binding.loader.rlLoader.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    private boolean isValidDateRange() {
        return startDate != null && !startDate.isEmpty() &&
                endDate != null && !endDate.isEmpty();
    }

    private String getAuthToken() {
        return "Bearer " + pref.getPrefString(this, pref.user_token);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    // Method to refresh data
    public void refreshData() {
        if (!isInternetConnected(this)) {
            showToast("❌ No internet connection");
            return;
        }

        currentPage = 0;
        isLastPage = false;
        if (adapter != null) {
            adapter.clearRecords();
        }
        fetchAccountReportData();
    }

    // Method to load more data (for pagination)
    public void loadMoreData() {
        if (!isLoading && !isLastPage && isInternetConnected(this)) {
            fetchAccountReportData();
        } else if (!isInternetConnected(this)) {
            showToast("❌ No internet connection");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up resources
        if (viewModel != null) {
            viewModel = null;
        }
        binding = null;
    }

    // Add this method to your ActivityAccountReport class
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
            showToast("No data to export");
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
            showToast("Export failed: " + e.getMessage());
        }
    }

    // Generate dynamic filename based on report type
    private String generateDynamicFileName() {
        String fileName = "account_report";

        // Include date range in filename
        if (startDate != null && endDate != null) {
            fileName += "_" + startDate.replace("-", "_") + "_to_" + endDate.replace("-", "_");
        }

        // Add timestamp for uniqueness
        fileName += "_" + System.currentTimeMillis();

        return fileName;
    }

    // Prepare table data for export
    private ArrayList<ArrayList<String>> prepareTableData() {
        ArrayList<ArrayList<String>> tableData = new ArrayList<>();

        // Add header row based on your XML layout
        ArrayList<String> headers = new ArrayList<>();
        headers.add("Sr");
        headers.add("ID");
        headers.add("Description");
        headers.add("Date");
        headers.add("Paid");
        tableData.add(headers);

        int count = 1;
        // Add data rows from recordList
        for (Record record : recordList) {
            ArrayList<String> row = new ArrayList<>();
            row.add(String.valueOf(count));

            // Adjust these field names based on your Record model structure
            row.add(record.getId() != -1 ? String.valueOf(record.getId()) : "");
            row.add(record.getDescription() != null ? record.getDescription() : "");
            row.add(record.getDate() != null ? record.getDate() : "");
            row.add(record.getAmount() != -1 ? String.valueOf(record.getAmount()) : "0");

            tableData.add(row);
            count++;
        }

        return tableData;
    }
}