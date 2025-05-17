package com.school.schoolmanagement.Admin.Employee.StaffIdCard;

import static android.content.ContentValues.TAG;

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
import com.school.schoolmanagement.Admin.Adapter.AdapterIdCard;
import com.school.schoolmanagement.Admin.Model.AllEmployees;
import com.school.schoolmanagement.GlobalViewModel.ViewModel;
import com.school.schoolmanagement.R;
import com.school.schoolmanagement.Utils.Utility;
import com.school.schoolmanagement.databinding.ActivityStaffIdCardBinding;

import java.util.ArrayList;
import java.util.List;

public class ActivityStaffIdCard extends Utility {

    ActivityStaffIdCardBinding binding;
    private AdapterIdCard adapter;
    private List<AllEmployees> employeesList;
    private List<AllEmployees> originalEmployeesList; // Store original list for filtering
    ViewModel viewModel;

    private String selectedRole = "";
    private AlertDialog employeeDialog = null;
    private boolean isSearchDialogShowing = false;
    private boolean isBackspacePressed = false;
    private String lastQuery = "";

    // Predefined list of roles
    private final String[] roles = {"TEACHER", "PRINCIPAL", "VICE PRINCIPAL", "ACCOUNTANT", "LIBRARIAN", "ADMIN STAFF", "SUPPORT STAFF"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityStaffIdCardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        viewModel = new ViewModelProvider(this).get(ViewModel.class);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize the empty lists
        employeesList = new ArrayList<>();
        originalEmployeesList = new ArrayList<>();

        // Initialize adapter with empty list
        adapter = new AdapterIdCard(employeesList);
        binding.rcIdCards.setAdapter(adapter);

        setupUI();

        // Load employees from API
        loadEmployees();
    }

    private void setupUI() {
        // Setup toolbar back button
        binding.toolbar.backBtn.setOnClickListener(view -> finish());

        // Setup search button
        binding.searchBtn.setOnClickListener(new View.OnClickListener() {
            private boolean isOrange = false; // Flag to track the current color state

            @Override
            public void onClick(View view) {
                if (isOrange) {
                    binding.searchBtn.setColorFilter(null); // Reset to default color
                    binding.relSearch.setVisibility(View.GONE);
                    // Reset search filters
                    resetFilters();
                } else {
                    binding.searchBtn.setColorFilter(getResources().getColor(R.color.orange)); // Set to orange
                    binding.relSearch.setVisibility(View.VISIBLE);
                }
                isOrange = !isOrange; // Toggle state
            }
        });

        // Setup role selection
        binding.edtSelectRole.setOnClickListener(view -> showRoleSelectionDialog());

        // Setup search field
        setupSearchListener();
    }

    private void setupSearchListener() {
        binding.edtSearchStudent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Check if backspace is pressed (text will be shorter)
                isBackspacePressed = after < count;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Not used
            }

            @Override
            public void afterTextChanged(Editable s) {
                String query = s.toString().trim();

                // Only show dialog if actively typing (not backspacing) and query meets minimum length
                if (query.length() >= 3 && !isBackspacePressed && !query.equals(lastQuery)) {
                    filterAndShowEmployees(query);
                } else if (query.isEmpty()) {
                    // If search field is cleared, reset to show all employees with role filter still applied
                    applyRoleFilter();
                } else {
                    // Just apply the current filters without showing dialog
                    applyNameFilter(query);
                }

                lastQuery = query;
            }
        });
    }

    private void filterAndShowEmployees(String query) {
        if (isSearchDialogShowing) {
            return; // Don't show another dialog if one is already showing
        }

        List<AllEmployees> filteredList = new ArrayList<>();
        String lowerQuery = query.toLowerCase();

        for (AllEmployees employee : originalEmployeesList) {
            String name = employee.getName();
            if (name != null && name.toLowerCase().contains(lowerQuery)) {
                filteredList.add(employee);
            }
        }

        if (!filteredList.isEmpty() && filteredList.size() < originalEmployeesList.size()) {
            showEmployeeSelectionDialog(filteredList);
        }
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

            // Apply role filter
            applyRoleFilter();
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

        // Dismiss on cancel
        builder.setOnCancelListener(dialog -> {
            isSearchDialogShowing = false;
        });

        AlertDialog dialog = builder.create();
        employeeDialog = dialog;
        isSearchDialogShowing = true;

        listView.setOnItemClickListener((parent, view, position, id) -> {
            AllEmployees selectedEmployee = employees.get(position);
            binding.edtSearchStudent.setText(selectedEmployee.getName());
            lastQuery = selectedEmployee.getName();
            dialog.dismiss();
            isSearchDialogShowing = false;

            // Apply both filters with the selected name
            applyFilters(selectedEmployee.getName());
        });

        dialog.show();
    }

    private void applyNameFilter(String nameQuery) {
        applyFilters(nameQuery);
    }

    private void applyRoleFilter() {
        applyFilters(binding.edtSearchStudent.getText().toString().trim());
    }

    private void applyFilters(String nameQuery) {
        employeesList.clear();

        for (AllEmployees employee : originalEmployeesList) {
            boolean matchesName = nameQuery.isEmpty() ||
                    (employee.getName() != null && employee.getName().toLowerCase().contains(nameQuery.toLowerCase()));

            boolean matchesRole = selectedRole.isEmpty() ||
                    (employee.getEmployeeRole() != null && employee.getEmployeeRole().equalsIgnoreCase(selectedRole));

            if (matchesName && matchesRole) {
                employeesList.add(employee);
            }
        }

        adapter.notifyDataSetChanged();
    }

    private void resetFilters() {
        selectedRole = "";
        binding.edtSelectRole.setText("");
        binding.edtSearchStudent.setText("");
        lastQuery = "";

        // Reset list to original
        employeesList.clear();
        employeesList.addAll(originalEmployeesList);
        adapter.notifyDataSetChanged();
    }

    private void loadEmployees() {
        binding.loader.rlLoader.setVisibility(View.VISIBLE);
        binding.rcIdCards.setVisibility(View.GONE);

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
                    // Clear lists
                    employeesList.clear();
                    originalEmployeesList.clear();

                    // Add all employees to our lists
                    employeesList.addAll(employees);
                    originalEmployeesList.addAll(employees);

                    // Notify the adapter that data has changed
                    adapter.notifyDataSetChanged();
                    binding.rcIdCards.setVisibility(View.VISIBLE);

                    Log.d(TAG, "Successfully loaded " + employeesList.size() + " employees");
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (employeeDialog != null && employeeDialog.isShowing()) {
            employeeDialog.dismiss();
        }
    }
}