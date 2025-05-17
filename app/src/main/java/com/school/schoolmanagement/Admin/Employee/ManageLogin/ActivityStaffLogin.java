package com.school.schoolmanagement.Admin.Employee.ManageLogin;

import static android.content.ContentValues.TAG;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.school.schoolmanagement.Admin.Adapter.AdapterStudentPassword;
import com.school.schoolmanagement.Admin.Model.AllEmployees;
import com.school.schoolmanagement.Admin.Model.StudentPassword;
import com.school.schoolmanagement.GlobalViewModel.ViewModel;
import com.school.schoolmanagement.R;
import com.school.schoolmanagement.Utils.Utility;
import com.school.schoolmanagement.databinding.ActivityStaffLoginBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityStaffLogin extends Utility {
    ActivityStaffLoginBinding binding;
    private List<StudentPassword> userList = new ArrayList<>();
    private AdapterStudentPassword userAdapter;
    private List<AllEmployees> employeeList = new ArrayList<>();
    private List<AllEmployees> filteredEmployeeList = new ArrayList<>();
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

    private void showEmployeeSelectionDialog(List<AllEmployees> employees) {
        List<String> employeeNames = new ArrayList<>();
        for (AllEmployees emp : employees) {
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
            AllEmployees selectedEmployee = employees.get(position);
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

        for (AllEmployees emp : employeeList) {
            String name = emp.getName();
            if (name != null && name.toLowerCase().contains(lowerQuery)) {
                filteredEmployeeList.add(emp);
            }
        }
    }

    private void applyFilters() {
        // Apply both employee ID filter and role filter
        List<AllEmployees> filteredList = new ArrayList<>();

        for (AllEmployees emp : employeeList) {
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

    private void updateUserList(List<AllEmployees> filteredEmployees) {
        userList.clear();
        for (AllEmployees emp : filteredEmployees) {
            StudentPassword user = new StudentPassword(
                    String.valueOf(emp.getEmployeeId()),
                    emp.getName(),
                    emp.getEmployeeRole(),
                    emp.getEmailAddress(),
                    emp.getPassword()
            );
            userList.add(user);
        }

        if (userAdapter == null) {
            userAdapter = new AdapterStudentPassword(this, userList);
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

        viewModel.getAllEmployees(authToken).observe(this, response -> {
            binding.loader.rlLoader.setVisibility(View.GONE);

            if (response == null) {
                Toast.makeText(this, "Server error. Please try again later.", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Response object is null");
                return;
            }

            if (response.isSuccess) {
                List<AllEmployees> employees = response.data;

                if (employees != null && !employees.isEmpty()) {
                    employeeList.clear();
                    employeeList.addAll(employees);

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
}