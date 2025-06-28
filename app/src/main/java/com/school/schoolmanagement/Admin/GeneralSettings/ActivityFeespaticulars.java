package com.school.schoolmanagement.Admin.GeneralSettings;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.Gson;
import com.school.schoolmanagement.Adapters.FeesParticularsAdapter;
import com.school.schoolmanagement.Admin.Model.ClassModel;
import com.school.schoolmanagement.Admin.Model.FeesParticular;
import com.school.schoolmanagement.Admin.Model.FeesStructure;
import com.school.schoolmanagement.Admin.Model.FeesStructureRequest;
import com.school.schoolmanagement.GlobalViewModel.ViewModel;
import com.school.schoolmanagement.R;
import com.school.schoolmanagement.Utils.Utility;
import com.school.schoolmanagement.databinding.ActivityFeespaticularsBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityFeespaticulars extends Utility {

    ActivityFeespaticularsBinding binding;
    ViewModel viewModel;
    private FeesParticularsAdapter feesAdapter;
    private Gson gson;

    // Class selection variables
    private ArrayList<ClassModel.Data> classList;
    private int selectedClassId = -1;
    private Map<String, Integer> classNameToIdMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityFeespaticularsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        viewModel = new ViewModelProvider(this).get(ViewModel.class);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        gson = new Gson();

        // Initialize class list
        classList = new ArrayList<>();

        setupRecyclerView();
        setupClickListeners();
        setupClassSelectionDialog();

        // Fetch class data from API
        fetchClassData();
    }

    private void setupRecyclerView() {
        feesAdapter = new FeesParticularsAdapter(this);
        binding.rcFeesParticulars.setLayoutManager(new LinearLayoutManager(this));
        binding.rcFeesParticulars.setAdapter(feesAdapter);

        // Optional: Listen to data changes
        feesAdapter.setOnDataChangeListener(new FeesParticularsAdapter.OnDataChangeListener() {
            @Override
            public void onDataChanged(List<FeesParticular> updatedList) {
                // Handle data changes if needed
                Log.d("FeesData", "Data changed: " + updatedList.toString());
                // You can validate data, calculate totals, etc. here
            }
        });
    }

    private void setupClickListeners() {
        // Add More button click
        binding.addSubjectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                feesAdapter.addNewItem();
            }
        });

        // Remove button click
        binding.removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                feesAdapter.removeLastItem();
            }
        });

        // Create button click
        binding.createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveFeesParticulars();
            }
        });

        // Back button click (toolbar back button)
        binding.toolbar.getChildAt(0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void setupClassSelectionDialog() {
        binding.edtSelectClass.setFocusable(false);
        binding.edtSelectClass.setClickable(true);
        binding.edtSelectClass.setOnClickListener(v -> {
            if (classList == null || classList.isEmpty()) {
                showToast("No classes available. Please try again later.");
                // Try to fetch classes again
                fetchClassData();
                return;
            }

            showClassSelectionDialog();
        });
    }

    private void showClassSelectionDialog() {
        // Create a list of class names for the dialog
        ArrayList<String> classNames = new ArrayList<>();
        classNameToIdMap.clear();

        for (ClassModel.Data classData : classList) {
            classNames.add(classData.getClassName());
            classNameToIdMap.put(classData.getClassName(), classData.getClassId());
        }

        // Create dialog
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                com.google.android.material.R.layout.support_simple_spinner_dropdown_item,
                classNames
        );

        new MaterialAlertDialogBuilder(this)
                .setTitle("Select Class")
                .setAdapter(adapter, (dialog, position) -> {
                    String selectedClassName = classNames.get(position);
                    binding.edtSelectClass.setText(selectedClassName);

                    // Store the selected class ID
                    selectedClassId = classNameToIdMap.get(selectedClassName);
                    Log.d(TAG, "Selected class ID: " + selectedClassId);

                    // Clear any previous error
                    binding.edtSelectClass.setError(null);
                })
                .show();
    }

    private void fetchClassData() {
        showLoading(true);
        String auth = "Bearer " + pref.getPrefString(this, pref.user_token);
        viewModel.getAllClasses(auth).observe(this, response -> {
            showLoading(false);
            if (response != null && response.data != null && response.data.getData() != null) {
                classList.clear();
                classList.addAll(response.data.getData());
                Log.d(TAG, "Fetched " + classList.size() + " classes");
            } else {
                showToast("No classes found.");
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

    // Replace your saveFeesParticulars() method with this:
    private void saveFeesParticulars() {
        // Get all the fees data
        String selectedClass = binding.edtSelectClass.getText().toString().trim();

        // Validate data
        if (selectedClass.isEmpty()) {
            binding.edtSelectClass.setError("Please select a class");
            binding.edtSelectClass.requestFocus();
            return;
        }

        if (selectedClassId == -1) {
            binding.edtSelectClass.setError("Please select a valid class");
            return;
        }

        // Generate the fees structure request
        FeesStructureRequest feesRequest = feesAdapter.generateFeesStructureRequest();

        // Validate that at least default fees have amounts
        FeesStructure feesStructure = feesRequest.getFeesStructure();
        if (feesStructure.getTuitionFees().isEmpty() &&
                feesStructure.getAdmissionFees().isEmpty() &&
                feesStructure.getExaminationFees().isEmpty()) {
            Toast.makeText(this, "Please enter at least one fees amount", Toast.LENGTH_SHORT).show();
            return;
        }

        // Log the FeesStructure object for debugging
        Log.d("FeesData", "FeesStructure object: " + feesStructure.toString());

        // Send the FeesStructure object directly to server
        sendFeesDataToServer(selectedClass, feesStructure);
    }

    // Replace your sendFeesDataToServer() method with this:
    private void sendFeesDataToServer(String selectedClass, FeesStructure feesStructure) {
        Log.d("FeesData", "Class: " + selectedClass);
        Log.d("FeesData", "Class ID: " + selectedClassId);
        Log.d("FeesData", "FeesStructure: " + feesStructure.toString());

        // Show loading while sending data
        showLoading(true);

        // Send FeesStructure object directly to your ViewModel
        String auth = "Bearer " + pref.getPrefString(this, pref.user_token);
        viewModel.updateClassFees(auth, selectedClassId, feesStructure).observe(this, response -> {
            showLoading(false);
            if (response != null && response.isSuccess()) {
                Toast.makeText(this, "Fees updated successfully!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                String errorMessage = (response != null && response.getMessage() != null)
                        ? response.getMessage()
                        : "Failed to update fees. Please try again.";
                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    // Method to populate adapter from existing data (e.g., when editing)
    public void loadExistingFeesData(String jsonResponse) {
        try {
            FeesStructureRequest feesRequest = gson.fromJson(jsonResponse, FeesStructureRequest.class);
            if (feesRequest != null && feesRequest.getFeesStructure() != null) {
                feesAdapter.populateFromFeesStructure(feesRequest.getFeesStructure());
            }
        } catch (Exception e) {
            Log.e("FeesData", "Error parsing existing fees data: " + e.getMessage());
        }
    }

    // Method to get selected class ID (for external use)
    public int getSelectedClassId() {
        return selectedClassId;
    }

    // Method to get selected class name (for external use)
    public String getSelectedClassName() {
        return binding.edtSelectClass.getText().toString().trim();
    }

    // Method to set selected class programmatically (for external use)
    public void setSelectedClass(int classId, String className) {
        selectedClassId = classId;
        binding.edtSelectClass.setText(className);
    }

    private void processFeesData(String selectedClass, List<FeesParticular> feesData) {
        // This method is now replaced by saveFeesParticulars()
        // Keeping for backward compatibility if needed

        Log.d("FeesData", "Class: " + selectedClass);
        for (FeesParticular fees : feesData) {
            Log.d("FeesData", "Fees: " + fees.getFeesName() + " - Amount: " + fees.getAmount());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}