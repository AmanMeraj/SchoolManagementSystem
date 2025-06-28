package com.school.schoolmanagement.Admin.Salary.PaySalary;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
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
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.school.schoolmanagement.Adapters.AdapterEmployeeSelection;
import com.school.schoolmanagement.Admin.Model.AllEmployees;
import com.school.schoolmanagement.Admin.Model.Employee2;
import com.school.schoolmanagement.GlobalViewModel.ViewModel;
import com.school.schoolmanagement.R;
import com.school.schoolmanagement.Utils.Utility;
import com.school.schoolmanagement.databinding.ActivityPaySalaryBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ActivityPaySalary extends Utility {

    ActivityPaySalaryBinding binding;
    ViewModel viewModel;
    private boolean isSearchVisible = false;
    int employeeId;
    Employee2 employee2Response;

    // Employee data
    private List<AllEmployees> allEmployeesList;
    private List<AllEmployees> filteredEmployeesList;
    private int selectedEmployeeId = 0;
    private String selectedEmployeeName = "";

    private static final String TAG = "ActivityPaySalary";

    // Calendar instance for date selection
    private Calendar calendar = Calendar.getInstance();
    private AlertDialog currentDialog; // Keep reference to current dialog

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityPaySalaryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        viewModel = new ViewModelProvider(this).get(ViewModel.class);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize employee lists
        allEmployeesList = new ArrayList<>();
        filteredEmployeesList = new ArrayList<>();

        setupClickListeners();

        // Initially show search layout and set orange tint
        showSearchLayout();

        // Initially hide the salary card
        hidePaySalaryCard();
        if(isInternetConnected(this)){
            // Load employees data
            loadEmployees();
        }else{
            Toast.makeText(this, "No Internet Connection!", Toast.LENGTH_SHORT).show();
        }

        binding.toolbar.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void setupClickListeners() {
        // Search button click listener
        binding.searchBtn.setOnClickListener(v -> toggleSearchLayout());

        // Optional: Refresh button click listener
        binding.refreshBtn.setOnClickListener(v -> refreshData());

        // Employee search EditText click listener
        binding.edtSearchStudent.setOnClickListener(v -> showEmployeeSelectionDialog());

        // Disable keyboard input on the EditText, only allow selection through dialog
        binding.edtSearchStudent.setFocusable(false);
        binding.edtSearchStudent.setClickable(true);

        // Set up date picker click listeners
        setupDatePickerListeners();

        // Submit button click listener
        binding.btnSubmit.setOnClickListener(v -> submitSalary());
    }

    private void setupDatePickerListeners() {
        // Fee month date picker
        binding.edtFeeMonth.setOnClickListener(v -> showDatePickerForFeeMonth());
        binding.edtFeeMonth.setFocusable(false);
        binding.edtFeeMonth.setClickable(true);

        // Due date picker
        binding.edtDueDate.setOnClickListener(v -> showDatePickerForDueDate());
        binding.edtDueDate.setFocusable(false);
        binding.edtDueDate.setClickable(true);
    }

    private void showDatePickerForFeeMonth() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    // Format: DD-MM
                    String formattedDate = String.format(Locale.getDefault(), "%02d-%02d", dayOfMonth, month + 1);
                    binding.edtFeeMonth.setText(formattedDate);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void showDatePickerForDueDate() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    // Format: YYYY-MM-DD
                    String formattedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
                    binding.edtDueDate.setText(formattedDate);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }


    private void showLoading(boolean isLoading) {
        if (isLoading) {
            binding.loader.rlLoader.setVisibility(View.VISIBLE);
        } else {
            binding.loader.rlLoader.setVisibility(View.GONE);
        }
    }

    private void getEmployeeDetails() {
        if (employeeId != 0) {
            Log.d(TAG, "getEmployeeDetails: Starting retrieval for employee ID: " + employeeId);
            // Show loading indicator
            showLoading(true);

            try {
                // Get auth token
                String authToken = "Bearer " + pref.getPrefString(this, pref.user_token);

                // Call the ViewModel method to get employee details
                viewModel.getEmployee(authToken, employeeId).observe(this, response -> {
                    // Hide loading indicator
                    showLoading(false);

                    if (response == null) {
                        Log.e(TAG, "API returned null response");
                        Toast.makeText(this, "Network error - null response", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (response.isSuccess && response.data != null) {
                        Log.d(TAG, "getEmployeeDetails: Successful API response with data");

                        // The key change - response.data is the Employee2 object
                        employee2Response = response.data;

                        // Fill the form with employee data
                        fillEmployeeData(employee2Response);
                    } else {
                        String errorMessage = response.message != null ? response.message : "Failed to fetch employee details";
                        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                showLoading(false);
                Log.e(TAG, "Exception in getEmployeeDetails: " + e.getMessage(), e);
                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void fillEmployeeData(Employee2 employee) {
        if (employee == null) {
            Log.e(TAG, "Employee data is null");
            return;
        }

        // Populate employee details in the card
        binding.employeeId.setText(String.valueOf(employee.getData().getEmployeeId()));
        binding.employeeName.setText(employee.getData().getName() != null ? employee.getData().getName() : "");
        binding.employeeRole.setText(employee.getData().getEmployeeRole() != null ? employee.getData().getEmployeeRole() : "");

        // Clear form fields and set default values
        clearFormFields();

        // Set fixed salary from Employee2 response
        if (employee.getData().getMonthlySalary() != 0.0) {
            binding.edtFixedSalary.setText(String.valueOf(employee.getData().getMonthlySalary()));
        }

        // You can populate other fields from Employee2 response as needed
        // For example, if Employee2 has additional fields:
        // binding.someOtherField.setText(employee.getSomeOtherField());

        Log.d(TAG, "Employee data filled successfully for: " + employee.getData().getName());
    }

    private void loadEmployees() {
        // Get auth token
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

        // Create dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Inflate custom layout for dialog
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_employee_selection, null);
        EditText edtSearch = dialogView.findViewById(R.id.edt_search_employee);
        RecyclerView recyclerView = dialogView.findViewById(R.id.rv_employees);

        // Set up RecyclerView
        AdapterEmployeeSelection adapter = new AdapterEmployeeSelection(filteredEmployeesList, employee -> {
            // Handle employee selection
            selectedEmployeeId = employee.getEmployeeId();
            selectedEmployeeName = employee.getName();

            // Store the employee ID for the details API call
            employeeId = employee.getEmployeeId();

            // Update the search EditText with selected employee name
            binding.edtSearchStudent.setText(selectedEmployeeName);

            // Show the pay salary card
            showPaySalaryCard();

            // Call getEmployeeDetails to fetch complete employee data
            getEmployeeDetails();

            Log.d(TAG, "Selected employee: " + selectedEmployeeName + " (ID: " + selectedEmployeeId + ")");

            // Dismiss dialog
            if (currentDialog != null) {
                currentDialog.dismiss();
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Set up search functionality
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
        currentDialog = dialog; // Store reference for dismissal
        dialog.show();
    }

    private void showPaySalaryCard() {
        binding.cardPaySalary.setVisibility(View.VISIBLE);
    }

    private void hidePaySalaryCard() {
        binding.cardPaySalary.setVisibility(View.GONE);
    }

    private void clearFormFields() {
        binding.edtFeeMonth.setText("");
        binding.edtDueDate.setText("");
        binding.edtFixedSalary.setText("");
        binding.edtBonusAmount.setText("");
        binding.edtDeduction.setText("");
    }

    private void submitSalary() {
        String auth = "Bearer " + pref.getPrefString(this, pref.user_token);

        // Validate required fields
        if (selectedEmployeeId == 0) {
            Toast.makeText(this, "Please select an employee first", Toast.LENGTH_SHORT).show();
            return;
        }

        String feeMonth = binding.edtFeeMonth.getText().toString().trim();
        String dueDate = binding.edtDueDate.getText().toString().trim();
        String fixedSalary = binding.edtFixedSalary.getText().toString().trim();

        if (feeMonth.isEmpty()) {
            Toast.makeText(this, "Please select fee month", Toast.LENGTH_SHORT).show();
            return;
        }

        if (dueDate.isEmpty()) {
            Toast.makeText(this, "Please select due date", Toast.LENGTH_SHORT).show();
            return;
        }

        if (fixedSalary.isEmpty()) {
            Toast.makeText(this, "Please enter fixed salary", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate salary amount is numeric and positive
        try {
            double salaryAmount = Double.parseDouble(fixedSalary);
            if (salaryAmount <= 0) {
                Toast.makeText(this, "Please enter a valid salary amount", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter a valid salary amount", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate bonus amount if provided
        String bonusAmount = binding.edtBonusAmount.getText().toString().trim();
        if (!bonusAmount.isEmpty()) {
            try {
                double bonus = Double.parseDouble(bonusAmount);
                if (bonus < 0) {
                    Toast.makeText(this, "Bonus amount cannot be negative", Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Please enter a valid bonus amount", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Validate deduction amount if provided
        String deductionAmount = binding.edtDeduction.getText().toString().trim();
        if (!deductionAmount.isEmpty()) {
            try {
                double deduction = Double.parseDouble(deductionAmount);
                if (deduction < 0) {
                    Toast.makeText(this, "Deduction amount cannot be negative", Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Please enter a valid deduction amount", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Check internet connection
        if (!isInternetConnected(this)) {
            Toast.makeText(this, "No Internet Connection!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show loading indicator
        showLoading(true);

        // Disable submit button to prevent multiple submissions
        binding.btnSubmit.setEnabled(false);

        Log.d(TAG, "Submitting salary for employee: " + selectedEmployeeName);
        Log.d(TAG, "Employee ID: " + employeeId);
        Log.d(TAG, "Fee Month: " + feeMonth);
        Log.d(TAG, "Due Date: " + dueDate);
        Log.d(TAG, "Fixed Salary: " + fixedSalary);
        Log.d(TAG, "Bonus: " + bonusAmount);
        Log.d(TAG, "Deduction: " + deductionAmount);

        try {
            viewModel.paySalary(auth, employeeId, feeMonth, dueDate, fixedSalary, bonusAmount, deductionAmount)
                    .observe(this, response -> {
                        // Hide loading indicator
                        showLoading(false);

                        // Re-enable submit button
                        binding.btnSubmit.setEnabled(true);

                        if (response == null) {
                            Log.e(TAG, "API returned null response");
                            Toast.makeText(this, "Server error. Please try again later.", Toast.LENGTH_LONG).show();
                            return;
                        }

                        if (response.isSuccess) {
                            Log.d(TAG, "Salary payment successful for employee: " + selectedEmployeeName);

                            // Show success message
                            Toast.makeText(this, "Salary paid successfully to " + selectedEmployeeName + "!", Toast.LENGTH_LONG).show();


                            // Reset form after successful submission
                            resetAfterSubmission();

                        } else {
                            // Handle API error
                            String errorMessage = response.message != null && !response.message.isEmpty()
                                    ? response.message
                                    : "Failed to process salary payment";

                            Log.e(TAG, "Salary payment failed: " + errorMessage);
                            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();

                        }
                    });

        } catch (Exception e) {
            // Hide loading and re-enable button in case of exception
            showLoading(false);
            binding.btnSubmit.setEnabled(true);

            Log.e(TAG, "Exception in submitSalary: " + e.getMessage(), e);
            Toast.makeText(this, "An error occurred: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void resetAfterSubmission() {
        // Clear selected employee
        selectedEmployeeId = 0;
        selectedEmployeeName = "";
        employeeId = 0;
        employee2Response = null;
        binding.edtSearchStudent.setText("");

        // Hide salary card
        hidePaySalaryCard();

        // Show search layout
        showSearchLayout();
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

    private void toggleSearchLayout() {
        if (isSearchVisible) {
            hideSearchLayout();
        } else {
            showSearchLayout();
        }
    }

    private void showSearchLayout() {
        binding.relSearch.setVisibility(View.VISIBLE);
        // Set orange tint to search button
        binding.searchBtn.setImageTintList(ContextCompat.getColorStateList(this, R.color.orange));
        isSearchVisible = true;
    }

    private void hideSearchLayout() {
        binding.relSearch.setVisibility(View.GONE);
        // Remove tint completely
        binding.searchBtn.setImageTintList(null);
        isSearchVisible = false;
    }

    private void refreshData() {
        // Reset all fields to default state
        resetAllFields();

        // Show search layout (default state)
        showSearchLayout();

        // Hide salary card
        hidePaySalaryCard();

        // Reload employee data
        loadEmployees();
    }

    private void resetAllFields() {
        // Clear search field and selected employee
        binding.edtSearchStudent.setText("");
        selectedEmployeeId = 0;
        selectedEmployeeName = "";
        employeeId = 0;
        employee2Response = null;

        // Clear all form fields
        clearFormFields();

        // Reset employee details
        if (binding.employeeId != null) {
            binding.employeeId.setText("");
        }
        if (binding.employeeName != null) {
            binding.employeeName.setText("");
        }
        if (binding.employeeRole != null) {
            binding.employeeRole.setText("");
        }
    }

    // Getter methods for selected employee (use these in other methods)
    public int getSelectedEmployeeId() {
        return selectedEmployeeId;
    }

    public String getSelectedEmployeeName() {
        return selectedEmployeeName;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}