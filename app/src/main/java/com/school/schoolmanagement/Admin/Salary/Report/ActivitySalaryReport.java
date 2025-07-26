package com.school.schoolmanagement.Admin.Salary.Report;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.school.schoolmanagement.Adapters.AdapterEmployeeSelection;
import com.school.schoolmanagement.Admin.Adapter.AdapterSalaryReport;
import com.school.schoolmanagement.Admin.Model.AllEmployees;
import com.school.schoolmanagement.GlobalViewModel.ViewModel;
import com.school.schoolmanagement.HelperClasses.DataExportHelper;
import com.school.schoolmanagement.Model.SalaryPaidResponse;
import com.school.schoolmanagement.R;
import com.school.schoolmanagement.Utils.Utility;
import com.school.schoolmanagement.databinding.ActivitySalaryReportBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ActivitySalaryReport extends Utility {

    private static final String TAG = "ActivitySalaryReport";

    ActivitySalaryReportBinding binding;
    ViewModel viewModel;

    private AdapterSalaryReport adapter;
    private List<SalaryPaidResponse.Datum> salaryDataList; // Changed to use actual response data

    // Employee data
    private List<AllEmployees> allEmployeesList;
    private List<AllEmployees> filteredEmployeesList;
    private int selectedEmployeeId = 0;
    private String selectedEmployeeName = "";

    // Date variables
    private String selectedStartDate = "";
    private String selectedEndDate = "";
    private String selectedFeesMonth = "";

    // Dialog reference
    private AlertDialog currentDialog;
    private ProgressDialog progressDialog;

    // Search card visibility state
    private boolean isSearchCardVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivitySalaryReportBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(ViewModel.class);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeData();
        setupRecyclerView();
        setupClickListeners();
        setupProgressDialog();
        setupExportButtons();

        // Load employees data if internet is available
        if (isInternetConnected(this)) {
            loadEmployees();
        } else {
            Toast.makeText(this, "No Internet Connection!", Toast.LENGTH_SHORT).show();
        }

        // Back button click listener
        binding.toolbar.backBtn.setOnClickListener(view -> finish());
    }

    private void initializeData() {
        salaryDataList = new ArrayList<>(); // Changed to use actual response data
        allEmployeesList = new ArrayList<>();
        filteredEmployeesList = new ArrayList<>();
    }

    private void setupRecyclerView() {
        adapter = new AdapterSalaryReport(this, salaryDataList); // Changed to use actual response data
        binding.rcFeesReport.setLayoutManager(new LinearLayoutManager(this));
        binding.rcFeesReport.setAdapter(adapter);
    }

    private void setupProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
    }

    private void setupClickListeners() {
        // Search button click to toggle search card visibility
        binding.btnSearch.setOnClickListener(v -> {
            if (isSearchCardVisible) {
                // If card is visible, perform search
                performSearch();
            } else {
                // If card is hidden, show it
                toggleSearchCard();
            }
        });

        // Employee search EditText click listener
        binding.edtSearchEmployee.setOnClickListener(v -> showEmployeeSelectionDialog());
        binding.edtSearchEmployee.setFocusable(false);
        binding.edtSearchEmployee.setClickable(true);

        // Date range EditText click listener
        binding.edtDateRange.setOnClickListener(v -> showDateRangeDialog());
        binding.edtDateRange.setFocusable(false);
        binding.edtDateRange.setClickable(true);

        // Fees month EditText click listener
        binding.edtFeesMonth.setOnClickListener(v -> showFeesMonthDialog());
        binding.edtFeesMonth.setFocusable(false);
        binding.edtFeesMonth.setClickable(true);

        // Clear selection buttons
        setupClearButtons();
    }

    private void setupClearButtons() {
        // Add clear employee selection
        binding.edtSearchEmployee.setOnLongClickListener(v -> {
            clearEmployeeSelection();
            return true;
        });

        // Add clear date range selection
        binding.edtDateRange.setOnLongClickListener(v -> {
            clearDateRange();
            return true;
        });

        // Add clear fees month selection
        binding.edtFeesMonth.setOnLongClickListener(v -> {
            clearFeesMonth();
            return true;
        });
    }

    private void clearEmployeeSelection() {
        selectedEmployeeId = 0;
        selectedEmployeeName = "";
        binding.edtSearchEmployee.setText("");
        binding.edtSearchEmployee.setHint("Select Employee (Optional)");
    }

    private void clearDateRange() {
        selectedStartDate = "";
        selectedEndDate = "";
        binding.edtDateRange.setText("");
        binding.edtDateRange.setHint("Select Date Range (Optional)");
    }

    private void clearFeesMonth() {
        selectedFeesMonth = "";
        binding.edtFeesMonth.setText("");
        binding.edtFeesMonth.setHint("Select Salary Month (Required)");
    }

    private void toggleSearchCard() {
        if (isSearchCardVisible) {
            binding.cardSelectInfo.setVisibility(View.GONE);
            isSearchCardVisible = false;
        } else {
            binding.cardSelectInfo.setVisibility(View.VISIBLE);
            isSearchCardVisible = true;
        }
    }

    private void loadEmployees() {
        String authToken = "Bearer " + pref.getPrefString(this, pref.user_token);

        if (authToken == null || authToken.isEmpty()) {
            Toast.makeText(this, "Authentication error. Please login again.", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "Loading employees...");

        viewModel.getAllEmployees(authToken).observe(this, response -> {
            if (response == null) {
                Toast.makeText(this, "Server error. Please try again later.", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Response object is null");
                return;
            }

            if (response.isSuccess) {
                List<AllEmployees> employees = response.data;
                Log.d(TAG, "Employees loaded: " + (employees != null ? employees.size() : 0));

                if (employees != null && !employees.isEmpty()) {
                    allEmployeesList.clear();
                    allEmployeesList.addAll(employees);
                    filteredEmployeesList.clear();
                    filteredEmployeesList.addAll(employees);
                    Log.d(TAG, "Employee data loaded successfully");
                } else {
                    Log.d(TAG, "Empty employee list returned");
                    Toast.makeText(this, "No employees found", Toast.LENGTH_SHORT).show();
                }
            } else {
                String errorMsg = response.message != null ? response.message : "Unknown error occurred";
                Log.e(TAG, "Error loading employees: " + errorMsg);
                Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showEmployeeSelectionDialog() {
        if (allEmployeesList.isEmpty()) {
            Toast.makeText(this, "No employees available. Please try again later.", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_employee_selection, null);
        EditText edtSearch = dialogView.findViewById(R.id.edt_search_employee);
        RecyclerView recyclerView = dialogView.findViewById(R.id.rv_employees);

        AdapterEmployeeSelection adapter = new AdapterEmployeeSelection(filteredEmployeesList, employee -> {
            selectedEmployeeId = employee.getEmployeeId();
            selectedEmployeeName = employee.getName();

            binding.edtSearchEmployee.setText(selectedEmployeeName);

            Log.d(TAG, "Selected employee: " + selectedEmployeeName + " (ID: " + selectedEmployeeId + ")");

            if (currentDialog != null) {
                currentDialog.dismiss();
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterEmployees(s.toString());
                adapter.updateList(filteredEmployeesList);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        builder.setView(dialogView);
        builder.setTitle("Select Employee");
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        currentDialog = dialog;
        dialog.show();
    }

    private void filterEmployees(String query) {
        filteredEmployeesList.clear();

        if (query.isEmpty()) {
            filteredEmployeesList.addAll(allEmployeesList);
        } else {
            String lowerCaseQuery = query.toLowerCase().trim();
            for (AllEmployees employee : allEmployeesList) {
                if (employee.getName() != null && employee.getName().toLowerCase().contains(lowerCaseQuery) ||
                        employee.getEmployeeRole() != null && employee.getEmployeeRole().toLowerCase().contains(lowerCaseQuery) ||
                        String.valueOf(employee.getEmployeeId()).contains(lowerCaseQuery)) {
                    filteredEmployeesList.add(employee);
                }
            }
        }

        Log.d(TAG, "Filtered employees: " + filteredEmployeesList.size() + " out of " + allEmployeesList.size());
    }

    private void showDateRangeDialog() {
        // Show start date picker first
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog startDatePicker = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    Calendar startCalendar = Calendar.getInstance();
                    startCalendar.set(year, month, dayOfMonth);

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    selectedStartDate = sdf.format(startCalendar.getTime());

                    // Now show end date picker
                    showEndDatePicker(startCalendar);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        startDatePicker.setTitle("Select Start Date");
        startDatePicker.show();
    }

    private void showEndDatePicker(Calendar minDate) {
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog endDatePicker = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    Calendar endCalendar = Calendar.getInstance();
                    endCalendar.set(year, month, dayOfMonth);

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    selectedEndDate = sdf.format(endCalendar.getTime());

                    // Display the selected date range
                    String dateRangeText = formatDateRange(selectedStartDate, selectedEndDate);
                    binding.edtDateRange.setText(dateRangeText);

                    Log.d(TAG, "Selected date range: " + selectedStartDate + " to " + selectedEndDate);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        // Set minimum date to the selected start date
        endDatePicker.getDatePicker().setMinDate(minDate.getTimeInMillis());
        endDatePicker.setTitle("Select End Date");
        endDatePicker.show();
    }

    private String formatDateRange(String startDate, String endDate) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("MMM d, yyyy", Locale.getDefault());

            String formattedStart = outputFormat.format(inputFormat.parse(startDate));
            String formattedEnd = outputFormat.format(inputFormat.parse(endDate));

            return formattedStart + " - " + formattedEnd;
        } catch (Exception e) {
            Log.e(TAG, "Error formatting date range: " + e.getMessage());
            return startDate + " - " + endDate;
        }
    }

    private void showFeesMonthDialog() {
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePicker = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    Calendar selectedCalendar = Calendar.getInstance();
                    selectedCalendar.set(year, month, dayOfMonth);

                    // Store in MM-yyyy format instead of yyyy-MM-dd
                    SimpleDateFormat format = new SimpleDateFormat("MM-yyyy", Locale.getDefault());
                    selectedFeesMonth = format.format(selectedCalendar.getTime());

                    // Also display in the same format
                    binding.edtFeesMonth.setText(selectedFeesMonth);

                    Log.d(TAG, "Selected fees month: " + selectedFeesMonth);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        datePicker.setTitle("Select Salary Month");
        datePicker.show();
    }


    private void performSearch() {
        // Only validate mandatory field - salary month
        if (selectedFeesMonth.isEmpty()) {
            Toast.makeText(this, "Please select salary month", Toast.LENGTH_SHORT).show();
            return;
        }

        // Employee and date range are optional, so no validation needed
        // Perform the search with the selected criteria
        searchSalaryReport();
    }

    private void searchSalaryReport() {
        if (!isInternetConnected(this)) {
            Toast.makeText(this, "No Internet Connection!", Toast.LENGTH_SHORT).show();
            return;
        }

        String authToken = "Bearer " + pref.getPrefString(this, pref.user_token);

        // Prepare optional parameters
        Integer employeeId = selectedEmployeeId == 0 ? null : selectedEmployeeId;
        String dateRange = (selectedStartDate.isEmpty() || selectedEndDate.isEmpty()) ?
                null : selectedStartDate + " to " + selectedEndDate;

        Log.d(TAG, "Searching salary report with parameters:");
        Log.d(TAG, "Salary Month (mandatory): " + selectedFeesMonth);
        Log.d(TAG, "Employee ID (optional): " + employeeId);
        Log.d(TAG, "Date Range (optional): " + dateRange);

        // Show loading indicator
        showProgressDialog("Loading salary report...");

        // Call the API
        viewModel.getSalaryReport(authToken, selectedFeesMonth, employeeId, dateRange)
                .observe(this, response -> {
                    // Hide loading indicator
                    hideProgressDialog();

                    if (response == null) {
                        Toast.makeText(this, "Server error. Please try again later.", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Response object is null");
                        return;
                    }

                    if (response.isSuccess && response.data != null) {
                        // Directly use the response data instead of converting
                        List<SalaryPaidResponse.Datum> salaryDataFromResponse = response.data.getData();

                        if (salaryDataFromResponse != null && !salaryDataFromResponse.isEmpty()) {
                            salaryDataList.clear();
                            salaryDataList.addAll(salaryDataFromResponse);
                            adapter.updateList(salaryDataList);

                            Log.d(TAG, "Salary report loaded successfully: " + salaryDataFromResponse.size() + " records");
                            Toast.makeText(this, "Found " + salaryDataFromResponse.size() + " salary records", Toast.LENGTH_SHORT).show();
                            binding.cardReport.setVisibility(View.VISIBLE);
                        } else {
                            salaryDataList.clear();
                            adapter.updateList(salaryDataList);
                            Toast.makeText(this, "No salary records found for the selected criteria", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        String errorMsg = response.message != null ? response.message : "Failed to load salary report";
                        Log.e(TAG, "Error loading salary report: " + errorMsg);
                        Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showProgressDialog(String message) {
        if (progressDialog != null) {
            progressDialog.setMessage(message);
            progressDialog.show();
        }
    }

    private void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    // Getter methods for accessing selected values
    public int getSelectedEmployeeId() {
        return selectedEmployeeId;
    }

    public String getSelectedEmployeeName() {
        return selectedEmployeeName;
    }

    public String getSelectedStartDate() {
        return selectedStartDate;
    }

    public String getSelectedEndDate() {
        return selectedEndDate;
    }

    public String getSelectedFeesMonth() {
        return selectedFeesMonth;
    }
    // Add these methods to your ActivitySalaryReport class

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

            // Generate dynamic filename based on selected criteria
            String fileName = generateDynamicFileName();

            // Use the exportData method with dynamic filename
            exportHelper.exportData(tableData, action, fileName);

        } catch (Exception e) {
            Log.e(TAG, "Export error: " + e.getMessage());
            Toast.makeText(this, "Export failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private String generateDynamicFileName() {
        String fileName = "salary_report";

        // Include salary month if selected
        if (!selectedFeesMonth.isEmpty()) {
            fileName += "_" + selectedFeesMonth.replace("-", "_");
        }

        // Include employee name if selected
        if (!selectedEmployeeName.isEmpty()) {
            fileName += "_" + selectedEmployeeName.replace(" ", "_");
        }

        // Include date range if selected
        if (!selectedStartDate.isEmpty() && !selectedEndDate.isEmpty()) {
            fileName += "_" + selectedStartDate.replace("-", "_") + "_to_" + selectedEndDate.replace("-", "_");
        }

        return fileName;
    }

    private ArrayList<ArrayList<String>> prepareTableData() {
        ArrayList<ArrayList<String>> tableData = new ArrayList<>();

        // Add header row
        ArrayList<String> headers = new ArrayList<>();
        headers.add("Sr");
        headers.add("ID");
        headers.add("Name");
        headers.add("Date");
        headers.add("Amount");
        headers.add("Paid");
        tableData.add(headers);

        int count = 1;
        // Add data rows from your salaryDataList
        for (SalaryPaidResponse.Datum salaryData : salaryDataList) {
            ArrayList<String> row = new ArrayList<>();
            row.add(String.valueOf(count));
            row.add(salaryData.getEmployeeId() != -1 ? String.valueOf(salaryData.getEmployeeId()) : "");
            row.add(salaryData.getEmployeeName() != null ? salaryData.getEmployeeName() : "");
            row.add(salaryData.getDateOfReceiving() != null ? salaryData.getDateOfReceiving() : "");
            row.add(salaryData.getTotalAmount() != 0 ? String.valueOf(salaryData.getTotalAmount()) : "");
            row.add(salaryData.getNetPaid() != 0 ? String.valueOf(salaryData.getNetPaid()) : "");
            tableData.add(row);
            count++;
        }

        return tableData;
    }

// Add this method call in your onCreate() method after setupClickListeners():
// setupExportButtons();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (currentDialog != null && currentDialog.isShowing()) {
            currentDialog.dismiss();
        }
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        binding = null;
    }
}