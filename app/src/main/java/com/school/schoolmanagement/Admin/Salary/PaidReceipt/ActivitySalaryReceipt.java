package com.school.schoolmanagement.Admin.Salary.PaidReceipt;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
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

import com.airbnb.lottie.LottieAnimationView;
import com.google.gson.Gson;
import com.school.schoolmanagement.Adapters.AdapterEmployeeSelection;
import com.school.schoolmanagement.Adapters.AdapterPaidSalary;
import com.school.schoolmanagement.Admin.Model.AllEmployees;
import com.school.schoolmanagement.Admin.Model.Employee2;
import com.school.schoolmanagement.GlobalViewModel.ViewModel;
import com.school.schoolmanagement.Model.SalaryPaidResponse;
import com.school.schoolmanagement.R;
import com.school.schoolmanagement.Utils.Utility;
import com.school.schoolmanagement.databinding.ActivitySalaryRecieptBinding;

import java.util.ArrayList;
import java.util.List;

public class ActivitySalaryReceipt extends Utility implements AdapterPaidSalary.OnItemClickListener {

    ActivitySalaryRecieptBinding binding;
    ViewModel viewModel;
    private boolean isSearchVisible = false;
    int employeeId;
    Employee2 employee2Response;

    // Employee data
    private List<AllEmployees> allEmployeesList;
    private List<AllEmployees> filteredEmployeesList;
    private int selectedEmployeeId = 0;
    private String selectedEmployeeName = "";

    // Salary data
    private ArrayList<SalaryPaidResponse.Datum> salaryList;
    private AdapterPaidSalary salaryAdapter;

    // Lottie Animation View (assuming it's in your layout)
    private LottieAnimationView lottieAnimationView;

    private static final String TAG = "ActivitySalaryReceipt";
    private AlertDialog currentDialog; // Keep reference to current dialog

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivitySalaryRecieptBinding.inflate(getLayoutInflater());
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

        // Initialize salary list and adapter
        salaryList = new ArrayList<>();
        setupRecyclerView();

        // Initialize Lottie Animation View
        initializeLottieAnimation();

        setupClickListeners();

        // Initially show search layout and set orange tint
        showSearchLayout();

        // Show empty state initially
        updateEmptyState();

        if (isInternetConnected(this)) {
            // Load employees data
            loadEmployees();
        } else {
            Toast.makeText(this, "No Internet Connection!", Toast.LENGTH_SHORT).show();
        }

        // Back button click listener
        binding.toolbar.backBtn.setOnClickListener(view -> finish());
    }

    private void initializeLottieAnimation() {
        // Assuming you have a LottieAnimationView in your layout with id: lottie_empty_state
        // Replace this with your actual Lottie view ID from the layout
        lottieAnimationView = binding.lotty; // or findViewById(R.id.lottie_empty_state);

        if (lottieAnimationView != null) {
            // Set up the Lottie animation
            lottieAnimationView.setAnimationFromUrl("https://lottie.host/ff208ff9-c570-4d80-b262-d7a9c953efae/u33pm7wu0i.lottie");
            lottieAnimationView.setRepeatCount(-1); // Loop infinitely
            lottieAnimationView.setSpeed(3f);
            lottieAnimationView.playAnimation();
        }
    }

    private void setupRecyclerView() {
        salaryAdapter = new AdapterPaidSalary(this, salaryList);
        salaryAdapter.setOnItemClickListener(this);
        binding.rcListOfPayment.setAdapter(salaryAdapter);
    }

    private void updateEmptyState() {
        if (salaryList == null || salaryList.isEmpty()) {
            // Show Lottie animation and hide RecyclerView
            binding.rcListOfPayment.setVisibility(View.GONE);
            if (lottieAnimationView != null) {
                binding.lottieEmptyState.setVisibility(View.VISIBLE);
                lottieAnimationView.playAnimation();
            }
        } else {
            // Show RecyclerView and hide Lottie animation
            binding.rcListOfPayment.setVisibility(View.VISIBLE);
            if (lottieAnimationView != null) {
                binding.lottieEmptyState.setVisibility(View.GONE);
                lottieAnimationView.pauseAnimation();
            }
        }
    }

    private void setupClickListeners() {
        // Search button click listener
        binding.searchBtn.setOnClickListener(v -> toggleSearchLayout());

        // Refresh button click listener
        binding.refreshBtn.setOnClickListener(v -> refreshData());

        // Employee search EditText click listener
        binding.edtSearchStudent.setOnClickListener(v -> showEmployeeSelectionDialog());

        // Disable keyboard input on the EditText, only allow selection through dialog
        binding.edtSearchStudent.setFocusable(false);
        binding.edtSearchStudent.setClickable(true);
    }

    private void showLoading(boolean isLoading) {
        // Add your loading implementation here if you have a loader in the layout
        // For now, we'll use a simple approach
        if (isLoading) {
            // Show loading indicator
            // binding.loader.rlLoader.setVisibility(View.VISIBLE);
        } else {
            // Hide loading indicator
            // binding.loader.rlLoader.setVisibility(View.GONE);
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

                        // Store employee response
                        employee2Response = response.data;

                        // Fill the receipt with employee data
                        fillEmployeeReceiptData(employee2Response);

                        // Load salary data for the selected employee
                        loadSalaryData(employeeId);
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

    private void loadSalaryData(int employeeId) {
        // Get auth token
        String authToken = "Bearer " + pref.getPrefString(this, pref.user_token);

        if (authToken == null || authToken.isEmpty()) {
            Toast.makeText(this, "Authentication error. Please login again.", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "Loading salary data for employee ID: " + employeeId);

        // Call your ViewModel method to get salary data for the employee
        viewModel.salaryDetails(authToken, employeeId).observe(this, response -> {
            if (response == null) {
                Toast.makeText(this, "Failed to load salary data", Toast.LENGTH_SHORT).show();
                // Clear salary list and update empty state
                salaryList.clear();
                salaryAdapter.notifyDataSetChanged();
                updateEmptyState();
                return;
            }

            if (response.isSuccess && response.data != null) {
                Log.d(TAG, "Salary data loaded successfully: " + response.data.data.size() + " records");

                // Clear existing data and add new data
                salaryList.clear();
                salaryList.addAll(response.data.data);
                salaryAdapter.notifyDataSetChanged();

                // Update empty state based on data
                updateEmptyState();
            } else {
                String errorMsg = response.message != null ? response.message : "Failed to load salary data";
                Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error loading salary data: " + errorMsg);

                // Clear salary list and show empty state
                salaryList.clear();
                salaryAdapter.notifyDataSetChanged();
                updateEmptyState();
            }
        });
    }

    private void fillEmployeeReceiptData(Employee2 employee) {
        if (employee == null) {
            Log.e(TAG, "Employee data is null");
            return;
        }

        // Note: These TextView IDs might need to be adjusted based on your actual layout
        // I'm using placeholder names as they weren't in the provided XML
        // You'll need to replace these with your actual TextView IDs from the receipt layout

        Log.d(TAG, "Employee receipt data filled successfully for: " + employee.getData().getName());
    }

    @Override
    public void onItemClick(SalaryPaidResponse.Datum salaryData, int position) {
        // Navigate to salary details activity
        Intent intent = new Intent(this, ActivityPaidReceiptDetails.class);

        // Pass the serialized salary data to the details activity
        intent.putExtra("SALARY_DATA", salaryData); // This will work if SalaryPaidResponse.Datum implements Serializable
        intent.putExtra("EMPLOYEE_NAME", selectedEmployeeName);
        intent.putExtra("EMPLOYEE_ID", selectedEmployeeId);
        intent.putExtra("POSITION", position);

        startActivity(intent);
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

            // Call getEmployeeDetails to fetch complete employee data and show receipt
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

        // Clear salary data
        salaryList.clear();
        salaryAdapter.notifyDataSetChanged();

        // Update empty state after clearing data
        updateEmptyState();

        // Reload employee data
        if (isInternetConnected(this)) {
            loadEmployees();
        } else {
            Toast.makeText(this, "No Internet Connection!", Toast.LENGTH_SHORT).show();
        }
    }

    private void resetAllFields() {
        // Clear search field and selected employee
        binding.edtSearchStudent.setText("");
        selectedEmployeeId = 0;
        selectedEmployeeName = "";
        employeeId = 0;
        employee2Response = null;
    }
    // Getter methods for selected employee
    public int getSelectedEmployeeId() {
        return selectedEmployeeId;
    }

    public String getSelectedEmployeeName() {
        return selectedEmployeeName;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Pause Lottie animation to prevent memory leaks
        if (lottieAnimationView != null) {
            lottieAnimationView.pauseAnimation();
        }
        binding = null;
    }
}