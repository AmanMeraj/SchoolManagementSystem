package com.school.schoolmanagement.Admin.Classes.NewClass;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.school.schoolmanagement.Admin.Model.ClassModel;
import com.school.schoolmanagement.Admin.Model.CreateClass;
import com.school.schoolmanagement.Admin.Model.EmployeeList;
import com.school.schoolmanagement.GlobalViewModel.ViewModel;
import com.school.schoolmanagement.R;
import com.school.schoolmanagement.Utils.Utility;
import com.school.schoolmanagement.databinding.ActivityNewClassBinding;

import java.util.ArrayList;
import java.util.List;

public class ActivityNewClass extends Utility {

    ActivityNewClassBinding binding;
    private final List<EmployeeList.Data> allEmployeesList = new ArrayList<>();
    private final List<EmployeeList.Data> filteredEmployees = new ArrayList<>();
    private int selectedEmployeeId = -1;

    private int classId = -1; // for edit mode
    private boolean isEditMode = false;
    ViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityNewClassBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(ViewModel.class);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });



        isEditMode=getIntent().getBooleanExtra("isEditMode",false);
        // Load employee list
        if (isInternetConnected(this)) {
            String auth = "Bearer " + pref.getPrefString(this, pref.user_token);
            getEmployeeList(auth);
        } else {
            showToast("No internet connection");
        }

        // Disable manual input in AutoCompleteTextView
        binding.edtClassTeacher.setFocusable(false);
        binding.edtClassTeacher.setClickable(true);

        // On click of Class Teacher EditText
        binding.edtClassTeacher.setOnClickListener(v -> {
            if (allEmployeesList.isEmpty()) {
                showToast("No employees loaded");
                return;
            }

            // Filter teachers
            filteredEmployees.clear();
            for (EmployeeList.Data emp : allEmployeesList) {
                if ("Teacher".equalsIgnoreCase(emp.getRole())) {
                    filteredEmployees.add(emp);
                }
            }

            if (filteredEmployees.isEmpty()) {
                showToast("No teachers found");
                return;
            }

            // Build list of teacher names
            List<String> teacherNames = new ArrayList<>();
            for (EmployeeList.Data emp : filteredEmployees) {
                teacherNames.add(emp.getName());
            }

            // Show AlertDialog
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, teacherNames);

            new MaterialAlertDialogBuilder(this)
                    .setTitle("Select Class Teacher")
                    .setAdapter(adapter, (dialog, which) -> {
                        EmployeeList.Data selected = filteredEmployees.get(which);
                        binding.edtClassTeacher.setText(selected.getName());
                        selectedEmployeeId = selected.getId();
                        Log.d(TAG, "Selected Teacher: ID=" + selectedEmployeeId + ", Name=" + selected.getName());
                    })
                    .show();
        });

        if (getIntent().getBooleanExtra("isEditMode", false)) {
            ClassModel.Data classData = (ClassModel.Data) getIntent().getSerializableExtra("classData");
            if (classData != null) {
                binding.edtClassName.setText(classData.getClassName());
//                binding.edtMonthlyTutionFees.setText(classData.get);
                classId = classData.getClassId(); // Save for PATCH call
                Log.d(TAG, "onCreate: class id"+classId);
            }
        }

        binding.createBtn.setOnClickListener(v -> {
            if (isInternetConnected(this)) {
                String auth = "Bearer " + pref.getPrefString(this, pref.user_token);
                if (isEditMode) {
                    updateClass(auth, classId); // PATCH
                } else {
                    postClass(auth); // POST
                }
            } else {
                showToast("No internet connection");
            }
        });
    }

    private void updateClass(String auth, int classId) {
        String className = binding.edtClassName.getText().toString().trim();
        String monthlyFeesStr = binding.edtMonthlyTutionFees.getText().toString().trim();

        if (className.isEmpty()) {
            binding.edtClassName.setError("Class name required");
            return;
        }

        if (monthlyFeesStr.isEmpty()) {
            binding.edtMonthlyTutionFees.setError("Monthly fees required");
            return;
        }

        if (selectedEmployeeId == -1) {
            showToast("Please select a class teacher");
            return;
        }

        double monthlyFees;
        try {
            monthlyFees = Double.parseDouble(monthlyFeesStr);
        } catch (NumberFormatException e) {
            binding.edtMonthlyTutionFees.setError("Invalid amount");
            return;
        }

        CreateClass createClass = new CreateClass();
        createClass.setClassName(className);
        createClass.setMonthlyTuitionFees((int) monthlyFees);
        createClass.setClassTeacherId(selectedEmployeeId);

        // 🔍 Log data being sent
        Log.d("UpdateClass", "Auth Token: " + auth);
        Log.d("UpdateClass", "Class ID: " + classId);
        Log.d("UpdateClass", "Class Name: " + className);
        Log.d("UpdateClass", "Monthly Tuition Fees: " + (int) monthlyFees);
        Log.d("UpdateClass", "Selected Teacher ID: " + selectedEmployeeId);

        showLoading(true);

        viewModel.updateClass(auth, classId, createClass).observe(this, response -> {
            showLoading(false);
            if (response != null && response.isSuccess()) {
                showToast("Class updated successfully");
                finish(); // Or navigate back / refresh UI
            } else {
                String error = (response != null && response.getMessage() != null) ? response.getMessage() : "Failed to update class";
                showToast(error);
                Log.e("UpdateClass", "API Error: " + error);
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
            } else {
                Log.e(TAG, "Failed to load employee list.");
                showToast("Failed to load employee list.");
            }
        });
    }

    private void showLoading(boolean isLoading) {
        Log.d(TAG, "Loading: " + isLoading);
        binding.loader.rlLoader.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }

    private void showToast(String message) {
        Log.d(TAG, "Toast: " + message);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void postClass(String auth) {
        String className = binding.edtClassName.getText().toString().trim();
        String monthlyFeesStr = binding.edtMonthlyTutionFees.getText().toString().trim();

        if (className.isEmpty()) {
            binding.edtClassName.setError("Class name required");
            return;
        }

        if (monthlyFeesStr.isEmpty()) {
            binding.edtMonthlyTutionFees.setError("Monthly fees required");
            return;
        }

        if (selectedEmployeeId == -1) {
            showToast("Please select a class teacher");
            return;
        }

        double monthlyFees;
        try {
            monthlyFees = Double.parseDouble(monthlyFeesStr);
        } catch (NumberFormatException e) {
            binding.edtMonthlyTutionFees.setError("Invalid amount");
            return;
        }

        CreateClass createClass = new CreateClass();
        createClass.setClassName(className);
        createClass.setMonthlyTuitionFees((int) monthlyFees);
        createClass.setClassTeacherId(selectedEmployeeId);

        showLoading(true);

        viewModel.postClass(auth, createClass).observe(this, response -> {
            showLoading(false);
            if (response != null && response.isSuccess && response.data != null) {
                showToast("Class created successfully");
                finish(); // Or navigate back / refresh UI
            } else {
                String error = (response != null && response.message != null) ? response.message : "Failed to create class";
                showToast(error);
            }
        });
    }

}
