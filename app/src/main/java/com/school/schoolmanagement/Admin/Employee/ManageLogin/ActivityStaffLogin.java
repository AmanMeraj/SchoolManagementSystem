package com.school.schoolmanagement.Admin.Employee.ManageLogin;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.school.schoolmanagement.Adapters.AdapterEmployeePassword;
import com.school.schoolmanagement.Admin.Model.AllEmployees;
import com.school.schoolmanagement.Admin.Model.StudentPassword;
import com.school.schoolmanagement.GlobalViewModel.ViewModel;
import com.school.schoolmanagement.HelperClasses.DataExportHelper;
import com.school.schoolmanagement.Model.ManageLoginEmployee;
import com.school.schoolmanagement.R;
import com.school.schoolmanagement.Utils.Utility;
import com.school.schoolmanagement.databinding.ActivityStaffLoginBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityStaffLogin extends Utility {
    ActivityStaffLoginBinding binding;
    private List<StudentPassword> userList = new ArrayList<>();
    private AdapterEmployeePassword userAdapter;
    private static final int EXPORT_REQUEST_CODE = 1001;
    private List<ManageLoginEmployee.Datum> employeeList = new ArrayList<ManageLoginEmployee.Datum>();
    private List<ManageLoginEmployee.Datum> filteredEmployeeList = new ArrayList<ManageLoginEmployee.Datum>();
    private String selectedRole = "";
    private String selectedEmployeeId = "";

    private boolean isUserInput = true;
    ViewModel viewModel;

    // Predefined list of roles
    private final String[] roles = {"TEACHER", "PRINCIPAL", "VICE PRINCIPAL", "ACCOUNTANT", "LIBRARIAN", "ADMIN STAFF", "SUPPORT STAFF"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityStaffLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(ViewModel.class);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupClickListeners();
        setupSearchListener();


        if (isInternetConnected(this)) {
            loadEmployees();
        } else {
            Toast.makeText(this, "No Internet Connection!", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupClickListeners() {
        setupExportButtons();
        binding.toolbar.backBtn.setOnClickListener(view -> finish());

        // Setup select role field click listener
        binding.edtSelectRole.setOnClickListener(view -> showRoleSelectionDialog());

        // Setup reload functionality
        binding.tvReloadAll.setOnClickListener(view -> {
            // Clear selections and reload all employees
            selectedRole = "";
            selectedEmployeeId = "";

            isUserInput = false;
            binding.edtSelectRole.setText("");
            binding.edtSearchEmployee.setText("");
            isUserInput = true;

            if (isInternetConnected(ActivityStaffLogin.this)) {
                loadEmployees();
            } else {
                Toast.makeText(ActivityStaffLogin.this, "No Internet Connection!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupSearchListener() {
        binding.edtSearchEmployee.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String query = s.toString().trim();
                if (query.length() >= 3) {
                    filterEmployees(query);

                    // Show employee selection dialog with filtered results
                    if (filteredEmployeeList.size() > 0) {
                        showEmployeeSelectionDialog(filteredEmployeeList);
                    }
                }
            }
        });
    }

    private void showRoleSelectionDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle("Choose Role");

        ListView listView = new ListView(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, roles);
        listView.setAdapter(adapter);

        builder.setView(listView);

        AlertDialog dialog = builder.create();

        listView.setOnItemClickListener((parent, view, position, id) -> {
            selectedRole = roles[position];
            binding.edtSelectRole.setText(selectedRole);
            dialog.dismiss();

            // Apply role filter to the employee list
            applyFilters();
        });

        dialog.show();
    }

    private void showEmployeeSelectionDialog(List<ManageLoginEmployee.Datum> employees) {
        List<String> employeeNames = new ArrayList<>();
        for (ManageLoginEmployee.Datum emp : employees) {
            employeeNames.add(emp.getName());
        }

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle("Select Employee");

        ListView listView = new ListView(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, employeeNames);
        listView.setAdapter(adapter);

        builder.setView(listView);

        AlertDialog dialog = builder.create();

        listView.setOnItemClickListener((parent, view, position, id) -> {
            ManageLoginEmployee.Datum selectedEmployee = employees.get(position);
            selectedEmployeeId = String.valueOf(selectedEmployee.getEmployeeId());
            binding.edtSearchEmployee.setText(selectedEmployee.getName());

            // Apply both filters (role and employee)
            applyFilters();

            dialog.dismiss();
        });

        dialog.show();
    }

    private void filterEmployees(String query) {
        filteredEmployeeList.clear();
        String lowerQuery = query.toLowerCase();

        for (ManageLoginEmployee.Datum emp : employeeList) {
            String name = emp.getName();
            if (name != null && name.toLowerCase().contains(lowerQuery)) {
                filteredEmployeeList.add(emp);
            }
        }
    }

    private void applyFilters() {
        // Apply both employee ID filter and role filter
        List<ManageLoginEmployee.Datum> filteredList = new ArrayList<>();

        for (ManageLoginEmployee.Datum emp : employeeList) {
            boolean matchesEmployeeId = selectedEmployeeId.isEmpty() ||
                    String.valueOf(emp.getEmployeeId()).equals(selectedEmployeeId);

            boolean matchesRole = selectedRole.isEmpty() ||
                    (emp.getEmployeeRole() != null && emp.getEmployeeRole().equals(selectedRole));

            if (matchesEmployeeId && matchesRole) {
                filteredList.add(emp);
            }
        }

        // Update the recycler view with filtered results
        updateUserList(filteredList);
    }

    private void updateUserList(List<ManageLoginEmployee.Datum> filteredEmployees) {
        userList.clear();
        for (ManageLoginEmployee.Datum emp : filteredEmployees) {
            String employeeId = String.valueOf(emp.getEmployeeId());
            String employeeName = emp.getName() != null ? emp.getName() : "Unknown";
            String employeeRole = emp.getEmployeeRole() != null ? emp.getEmployeeRole() : "No Role";
            String username = emp.getUsername() != null ? emp.getUsername() : "No Username";
            String password = emp.getPassword() != null ? emp.getPassword() : "No Password";

            // Add debug logging
            Log.d(TAG, "Employee mapping - ID: " + employeeId + ", Name: " + employeeName +
                    ", Role: " + employeeRole + ", Username: " + username + ", Password: " + password);

            StudentPassword user = new StudentPassword(
                    employeeId,
                    employeeName,
                    employeeRole,
                    username,
                    password
            );
            userList.add(user);
        }

        if (userAdapter == null) {
            userAdapter = new AdapterEmployeePassword(this, userList);
            binding.rcPromoteStudent.setAdapter(userAdapter);
        } else {
            userAdapter.notifyDataSetChanged();
        }
    }

    private void loadEmployees() {
        binding.loader.rlLoader.setVisibility(View.VISIBLE);
        binding.rcPromoteStudent.setVisibility(View.GONE);

        String authToken = "Bearer " + pref.getPrefString(this, pref.user_token);
        if (authToken == null || authToken.isEmpty()) {
            binding.loader.rlLoader.setVisibility(View.GONE);
            Toast.makeText(this, "Authentication error. Please login again.", Toast.LENGTH_SHORT).show();
            return;
        }

        viewModel.getEmployee2(authToken).observe(this, response -> {
            binding.loader.rlLoader.setVisibility(View.GONE);

            if (response == null) {
                Toast.makeText(this, "Server error. Please try again later.", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Response object is null");
                return;
            }

            if (response.isSuccess) {
                List<ManageLoginEmployee.Datum> employees = response.data.getData();

                if (employees != null && !employees.isEmpty()) {
                    employeeList.clear();
                    employeeList.addAll(employees);

                    // Add debug logging for each employee
                    for (ManageLoginEmployee.Datum emp : employees) {
                        Log.d(TAG, "Loaded employee - ID: " + emp.getEmployeeId() +
                                ", Name: " + emp.getName() +
                                ", Role: " + emp.getEmployeeRole() +
                                ", Username: " + emp.getUsername() +
                                ", Password: " + emp.getPassword());
                    }

                    // Apply any existing filters
                    if (!selectedRole.isEmpty() || !selectedEmployeeId.isEmpty()) {
                        applyFilters();
                    } else {
                        updateUserList(employeeList);
                    }

                    binding.rcPromoteStudent.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(this, "No employees found", Toast.LENGTH_SHORT).show();
                }
            } else {
                String errorMsg = response.message != null ? response.message : "Unknown error occurred";
                Log.e(TAG, "Error loading employees: " + errorMsg);
                Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to filter employees by role for API call if needed
    private void loadEmployeesByFilter() {
        binding.loader.rlLoader.setVisibility(View.VISIBLE);
        binding.rcPromoteStudent.setVisibility(View.GONE);

        String authToken = "Bearer " + pref.getPrefString(this, pref.user_token);
        if (authToken == null || authToken.isEmpty()) {
            binding.loader.rlLoader.setVisibility(View.GONE);
            Toast.makeText(this, "Authentication error. Please login again.", Toast.LENGTH_SHORT).show();
            return;
        }

        // You might need to implement this API call in your ViewModel
        // This is just a placeholder showing how you would structure the API call with filters
        Map<String, String> filters = new HashMap<>();
        if (!selectedRole.isEmpty()) {
            filters.put("role", selectedRole);
        }
        if (!selectedEmployeeId.isEmpty()) {
            filters.put("employeeId", selectedEmployeeId);
        }

        // Example of how you might call a filtered API
        // viewModel.getFilteredEmployees(authToken, filters).observe(this, response -> { ... });

        // For now, we'll just use the client-side filtering approach with applyFilters()
        loadEmployees();
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

            // Generate dynamic filename based on selected class
            String fileName = generateDynamicFileName();

            // Use the exportData method with dynamic filename
            exportHelper.exportData(tableData, action, fileName);

        } catch (Exception e) {
            Log.e(TAG, "Export error: " + e.getMessage());
            Toast.makeText(this, "Export failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    private String generateDynamicFileName() {
        String fileName = "students_basic_list";

        // Get the selected class name from the EditText
        String selectedClassName = binding.edtSelectRole.getText().toString();

        if (selectedClassName != null && !selectedClassName.isEmpty() && !selectedClassName.equals("")) {
            // If a specific class is selected, include it in the filename
            fileName = "employee_login_details_list_" + selectedClassName.replace(" ", "_");
        } else if (selectedClassName != null && selectedClassName.equals("")) {
            // If "All Classes" is selected, use a general filename
            fileName = "employee_login_details_list";
        }

        return fileName;
    }
    private ArrayList<ArrayList<String>> prepareTableData() {
        ArrayList<ArrayList<String>> tableData = new ArrayList<>();

        // Add header row
        ArrayList<String> headers = new ArrayList<>();
        headers.add("Sr");
        headers.add("ID");
        headers.add("S Name");
        headers.add("Role");
        headers.add("User ID");
        headers.add("Password");
        tableData.add(headers);
        int count=1;
        // Add data rows from your userList
        for (StudentPassword user : userList) {

            ArrayList<String> row = new ArrayList<>();
            row.add(String.valueOf(count));
            row.add(user.getId() != null ? user.getId() : "");
            row.add(user.getSName() != null ? user.getSName() : "");
            row.add(user.getClassName() != null ? user.getClassName() : "");
            row.add(user.getUserId() != null ? user.getUserId() : "");
            row.add(user.getPassword() != null ? user.getPassword() : "");
            tableData.add(row);
            count++;
        }

        return tableData;
    }


}