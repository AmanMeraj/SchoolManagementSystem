package com.school.schoolmanagement.Admin.Employee.JobLetter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.school.schoolmanagement.Admin.Model.Employee2;
import com.school.schoolmanagement.Admin.Model.EmployeeList;
import com.school.schoolmanagement.GlobalViewModel.ViewModel;
import com.school.schoolmanagement.R;
import com.school.schoolmanagement.Utils.Utility;
import com.school.schoolmanagement.databinding.ActivityJobLetterBinding;

import java.util.ArrayList;
import java.util.List;

public class ActivityJobLetter extends Utility {

    ActivityJobLetterBinding binding;
    ViewModel viewModel;
    Employee2 employee2Response;

    private final List<EmployeeList.Data> allEmployeesList = new ArrayList<>();
    private final List<EmployeeList.Data> filteredEmployees = new ArrayList<>();
    private int selectedEmployeeId = -1;

    private boolean isDialogVisible = false;

    private static final String TAG = "JobLetterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityJobLetterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(ViewModel.class);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.toolbar.backBtn.setOnClickListener(v -> finish());

        binding.refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Hide UI components
                binding.cardAdmissionConfirmation.setVisibility(View.GONE); // Replace with your actual cardView ID
                binding.relSearch.setVisibility(View.GONE);
                binding.edtSearchStudent.setText(""); // Clear search text
                binding.createBtn.setVisibility(View.GONE);

                // Optionally reset selected employee
                selectedEmployeeId = -1;
                filteredEmployees.clear();

                // Reload employee list
                if (isInternetConnected(ActivityJobLetter.this)) {
                    String auth = "Bearer " + pref.getPrefString(ActivityJobLetter.this, pref.user_token);
                    getEmployeeList(auth);
                } else {
                    showToast("No internet connection");
                }
            }
        });


        binding.searchBtn.setOnClickListener(new View.OnClickListener() {
            private boolean isOrange = false;

            @Override
            public void onClick(View view) {
                if (isOrange) {
                    binding.searchBtn.setColorFilter(null);
                    binding.relSearch.setVisibility(View.GONE);
                } else {
                    binding.searchBtn.setColorFilter(getResources().getColor(R.color.orange));
                    binding.relSearch.setVisibility(View.VISIBLE);
                }
                isOrange = !isOrange;
            }
        });

        if (isInternetConnected(this)) {
            String auth = "Bearer " + pref.getPrefString(this, pref.user_token);
            Log.d(TAG, "Token: " + auth);
            getEmployeeList(auth);
        }

        binding.edtSearchStudent.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d(TAG, "Search query: " + s.toString());

                // If text is cleared, reset search button and hide search layout
                if (s.length() == 0) {
                    binding.searchBtn.setColorFilter(null);
                    binding.relSearch.setVisibility(View.GONE);
                    return;
                }

                // Show suggestions if 3+ characters entered
                if (s.length() >= 3 && !isDialogVisible) {
                    showEmployeeSuggestions(s.toString());
                }
            }
        });

    }

    private void getEmployeeList(String auth) {
        showLoading(true);
        viewModel.getEmployeeList(auth).observe(this, response -> {
            showLoading(false);
            if (response != null && response.isSuccess && response.data != null && response.data.data != null) {
                allEmployeesList.clear();
                allEmployeesList.addAll(response.data.data);
                Log.d(TAG, "Employee list loaded. Total employees: " + allEmployeesList.size());

                for (int i = 0; i < allEmployeesList.size(); i++) {
                    EmployeeList.Data emp = allEmployeesList.get(i);
                    Log.d(TAG, "Employee[" + i + "]: ID=" + emp.getId() + ", Name=" + emp.getName() + ", Role=" + emp.getRole());
                }

                setupSearchWatcher();
            } else {
                Log.e(TAG, "Failed to load employee list.");
                showToast("Failed to load employee list.");
            }
        });
    }

    private void setupSearchWatcher() {
        binding.edtSearchStudent.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d(TAG, "Search query: " + s.toString());
                if (s.length() >= 3 && !isDialogVisible) {
                    showEmployeeSuggestions(s.toString());
                }
            }
        });
    }

    private void showEmployeeSuggestions(String query) {
        filteredEmployees.clear();
        for (EmployeeList.Data emp : allEmployeesList) {
            if (emp.getName() != null && emp.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredEmployees.add(emp);
            }
        }

        if (filteredEmployees.isEmpty()) {
            Log.d(TAG, "No matching employee names found.");
            return;
        }

        isDialogVisible = true;

        ArrayAdapter<EmployeeList.Data> adapter = new ArrayAdapter<EmployeeList.Data>(this,
                R.layout.dialog_employee_item, R.id.tvName, filteredEmployees) {

            @Override
            public View getView(int position, View convertView, android.view.ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView tvName = view.findViewById(R.id.tvName);
                TextView tvRole = view.findViewById(R.id.tvRole);

                EmployeeList.Data emp = getItem(position);
                if (emp != null) {
                    tvName.setText(emp.getName());
                    tvRole.setText(emp.getRole());
                }
                return view;
            }
        };

        new MaterialAlertDialogBuilder(this)
                .setTitle("Select Employee")
                .setAdapter(adapter, (dialog, which) -> {
                    EmployeeList.Data selected = filteredEmployees.get(which);
                    binding.edtSearchStudent.setText(selected.getName());
                    selectedEmployeeId = selected.getId();
                    Log.d(TAG, "Selected employee: ID=" + selectedEmployeeId + ", Name=" + selected.getName());
                    showToast("Selected ID: " + selectedEmployeeId);
                    binding.edtSearchStudent.setText("");
                    getEmployeeDetails(selectedEmployeeId);
                    isDialogVisible = false;
                })
                .setOnDismissListener(dialog -> {
                    Log.d(TAG, "Dialog dismissed.");
                    isDialogVisible = false;
                })
                .show();
    }

    private void getEmployeeDetails(int selectedEmployeeId) {
        if (selectedEmployeeId != 0) {
            Log.d(TAG, "getEmployeeDetails: Starting retrieval for employee ID: " + selectedEmployeeId);
            // Show loading indicator
            showLoading(true);

            try {
                // Get auth token
                String authToken = "Bearer " + pref.getPrefString(this, pref.user_token);

                // Call the ViewModel method to get employee details
                viewModel.getEmployee(authToken, selectedEmployeeId).observe(this, response -> {
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
                        binding.cardAdmissionConfirmation.setVisibility(View.VISIBLE);
                        binding.createBtn.setVisibility(View.VISIBLE);
                    } else {
                        binding.cardAdmissionConfirmation.setVisibility(View.GONE);
                        binding.createBtn.setVisibility(View.GONE);
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
            Log.e(TAG, "fillEmployeeData: Employee data is null");
            return;
        }

        Log.d(TAG, "fillEmployeeData: Populating data for employee: " + employee.data.getName());

        // Make the card visible
        binding.cardAdmissionConfirmation.setVisibility(View.VISIBLE);

        // Set name
        binding.nameTv.setText(employee.data.getName());
        Log.d(TAG, "Name: " + employee.data.getName());

        // Set registration number
        binding.tvRegistrationNumber.setText(String.valueOf(employee.data.getEmployeeId()));
        Log.d(TAG, "Registration Number: " + employee.data.getEmployeeId());

        // Set employee role
        binding.tvEmployeeRole.setText(employee.data.getEmployeeRole());
        Log.d(TAG, "Role: " + employee.data.getEmployeeRole());

        // Set date of joining
        binding.tvDateOfJoining.setText(employee.data.getDateOfJoining());
        Log.d(TAG, "Date of Joining: " + employee.data.getDateOfJoining());

        // Set account status
        boolean isActive = Boolean.parseBoolean(employee.data.accountStatus);
        String status = isActive ? "Active" : "Inactive";
        binding.tvAccountStatus.setText(status);
        binding.tvAccountStatus.setTextColor(
                ContextCompat.getColor(this, isActive ? R.color.green : R.color.dark_red));
        Log.d(TAG, "Account Status: " + status);



        // Set username
        binding.tvUsername.setText(employee.data.getUsername());
        Log.d(TAG, "Username: " + employee.data.getUsername());

        // Set password
        binding.tvPassword.setText(employee.data.getPassword());
        Log.d(TAG, "Password: " + employee.data.getPassword());
        if (employee.data.getProfilePicture() != null && !employee.data.getProfilePicture().isEmpty()) {
            try {
                byte[] imageData = Base64.decode(employee.data.getProfilePicture(), Base64.DEFAULT);

                if (imageData != null && imageData.length > 0) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
                    if (bitmap != null) {
                        binding.profileImg.setImageBitmap(bitmap);
                    } else {
                        Log.e("EMP_DEBUG", "Bitmap decode failed");
                    }
                } else {
                    Log.e("EMP_DEBUG", "Image data is empty or null after Base64 decode");
                }
            } catch (Exception e) {
                Log.e("EMP_DEBUG", "Error decoding profile picture: " + e.getMessage(), e);
            }
        }
    }

    private void showLoading(boolean isLoading) {
        Log.d(TAG, "Loading: " + isLoading);
        binding.loader.rlLoader.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }

    private void showToast(String message) {
        Log.d(TAG, "Toast: " + message);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
