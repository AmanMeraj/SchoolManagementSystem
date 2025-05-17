package com.school.schoolmanagement.Admin.Employee.AllEmployee;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;

import com.school.schoolmanagement.Admin.Adapter.AdapterAllEmployees;
import com.school.schoolmanagement.Admin.Employee.AdmissionForm.ActivityAddEmployee;
import com.school.schoolmanagement.Admin.Employee.Details.ActivityEmployeesDetails;
import com.school.schoolmanagement.Admin.Model.AllEmployees;
import com.school.schoolmanagement.GlobalViewModel.ViewModel;
import com.school.schoolmanagement.R;
import com.school.schoolmanagement.Utils.Utility;
import com.school.schoolmanagement.databinding.ActivityAllEmployeeBinding;

import java.util.ArrayList;
import java.util.List;

public class ActivityAllEmployee extends Utility implements AdapterAllEmployees.OnEmployeeClickListener {
    private ActivityAllEmployeeBinding binding;
    private List<AllEmployees> employeeList;
    private AdapterAllEmployees employeeAdapter;
    private ViewModel viewModel;
    private static final String TAG = "ActivityAllEmployee";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityAllEmployeeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(ViewModel.class);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.searchBtn.setOnClickListener(new View.OnClickListener() {
            private boolean isOrange = false; // Flag to track the current color state

            @Override
            public void onClick(View view) {
                if (isOrange) {
                    binding.searchBtn.setColorFilter(null); // Reset to default color
                    binding.relSearch.setVisibility(View.GONE);
                } else {
                    binding.searchBtn.setColorFilter(getResources().getColor(R.color.orange)); // Set to orange
                    binding.relSearch.setVisibility(View.VISIBLE);
                }
                isOrange = !isOrange; // Toggle state
            }
        });

        // Initialize employee list and set up RecyclerView ONCE
        employeeList = new ArrayList<>();
        setUpRecyclerView();

        // Set up search functionality
        binding.edtSearchEmployee.setOnEditorActionListener((v, actionId, event) -> {
            String query = binding.edtSearchEmployee.getText().toString().trim();
            if (!query.isEmpty()) {
                // searchEmployees(query);
                return true;
            }
            return false;
        });

        // Load employees on start
        if(isInternetConnected(this)){
            loadEmployees();
        } else {
            Toast.makeText(this, "No internet Connection!", Toast.LENGTH_SHORT).show();
            // Show empty state or cached data if available
            binding.loader.rlLoader.setVisibility(View.GONE);
        }
    }

    private void loadEmployees() {
        binding.loader.rlLoader.setVisibility(View.VISIBLE);
        binding.rcAllStudents.setVisibility(View.GONE);

        // Get auth token - ensure it's not null or empty
        String authToken = "Bearer "+pref.getPrefString(this, pref.user_token);

        if (authToken == null || authToken.isEmpty()) {
            binding.loader.rlLoader.setVisibility(View.GONE);
            Toast.makeText(this, "Authentication error. Please login again.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Debug log for token
        Log.d(TAG, "Using auth token: " + authToken.substring(0, Math.min(10, authToken.length())) + "...");

        viewModel.getAllEmployees(authToken).observe(this, response -> {
            binding.loader.rlLoader.setVisibility(View.GONE);

            if (response == null) {
                Toast.makeText(this, "Server error. Please try again later.", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Response object is null");
                return;
            }

            if (response.isSuccess) {
                List<AllEmployees> employees = response.data;
                Log.d(TAG, "Employees loaded: " + (employees != null ? employees.size() : 0));

                if (employees != null && !employees.isEmpty()) {
                    employeeList.clear();
                    employeeList.addAll(employees);
                    employeeAdapter.notifyDataSetChanged();
                    binding.rcAllStudents.setVisibility(View.VISIBLE);
                    Log.d(TAG, "Data added to adapter, notifying changes");
                } else {
                    Log.d(TAG, "Empty employee list returned");
                    Toast.makeText(this, "No employees found", Toast.LENGTH_SHORT).show();
                    // Show empty state
                }
            } else {
                String errorMsg = response.message != null ? response.message : "Unknown error occurred";
                Log.e(TAG, "Error loading employees: " + errorMsg);
                Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onViewClick(AllEmployees employee) {
        // Navigate to employee details
        Intent intent = new Intent(this, ActivityEmployeesDetails.class);
        intent.putExtra("ID", employee.employeeId);
        startActivity(intent);
    }

    @Override
    public void onEditClick(AllEmployees employee) {
        showEditDialog(employee);
    }

    @Override
    public void onDeleteClick(AllEmployees employee) {
        showDeleteDialog(employee);
    }

    @Override
    public void onDViewClick(AllEmployees employee) {
         Intent intent = new Intent(this, ActivityEmployeesDetails.class);
         intent.putExtra("ID", String.valueOf(employee.employeeId));
         startActivity(intent);
    }

    private void showDeleteDialog(AllEmployees employee) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Employee")
                .setMessage("Are you sure you want to delete this employee?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    // Call delete API here
                    deleteEmployee(employee);
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void deleteEmployee(AllEmployees employee) {
        if (employee == null || employee.getEmployeeId() == 0) return;

        String token = "Bearer " + pref.getPrefString(this,pref.user_token); // Adjust as per your token storage
        int employeeId = employee.getEmployeeId();

        viewModel.deleteEmployee(token, employeeId).observe(this, response -> {
            if (response.isSuccess) {
                // Only remove from list if API deletion is successful
                employeeList.remove(employee);
                employeeAdapter.notifyDataSetChanged();
                Toast.makeText(this, "Employee deleted successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to delete employee: " + response.message, Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void showEditDialog(AllEmployees employee) {
        new AlertDialog.Builder(this)
                .setTitle("Edit Employee")
                .setMessage("Do you want to edit " + employee.name + "?")
                .setPositiveButton("Edit", (dialog, which) -> {
                    // Navigate to edit employee screen
                     Intent intent = new Intent(this, ActivityAddEmployee.class);
                     intent.putExtra("EMPLOYEE_ID", employee.employeeId);
                     intent.putExtra("FLAG", "Edit");
                     startActivity(intent);
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    public void setUpRecyclerView() {
        // Set up RecyclerView
        employeeAdapter = new AdapterAllEmployees(this, employeeList, this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        binding.rcAllStudents.setLayoutManager(gridLayoutManager);
        binding.rcAllStudents.setHasFixedSize(true);
        binding.rcAllStudents.setItemAnimator(new DefaultItemAnimator());
        binding.rcAllStudents.setAdapter(employeeAdapter);
        Log.d(TAG, "RecyclerView setup complete");
    }
}