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
import com.school.schoolmanagement.Admin.Model.ClassModel2;
import com.school.schoolmanagement.Admin.Model.FeeItem;
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
    String from = "";
    int intentedClassId = -1;
    private int selectedClassId = -1;
    private Map<String, Integer> classNameToIdMap = new HashMap<>();

    // Fee items list for existing data
    private List<FeeItem> feeItemList = new ArrayList<>();

    // Add flag to prevent multiple submissions
    private boolean isSubmitting = false;

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

        from = getIntent().getStringExtra("from");
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
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Remove button click
        binding.removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                feesAdapter.removeLastItem();
            }
        });

        // Create button click - Fixed to prevent multiple submissions
        binding.createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Prevent multiple rapid clicks
                if (isSubmitting) {
                    return;
                }
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

    private void autoSelectClassIfIntended() {
        if (intentedClassId != -1) {
            // Find the class with the matching ID
            for (ClassModel.Data classData : classList) {
                if (classData.getClassId() == intentedClassId) {
                    // Set the selected class
                    selectedClassId = intentedClassId;
                    binding.edtSelectClass.setText(classData.getClassName());

                    // Add to map for consistency
                    classNameToIdMap.put(classData.getClassName(), classData.getClassId());

                    Log.d(TAG, "Auto-selected class: " + classData.getClassName() + " (ID: " + intentedClassId + ")");

                    // Load existing fees data for this class
                    fetchExistingFeesData();

                    // Optionally disable the field if coming from another activity
                    if (from != null && !from.isEmpty()) {
                        binding.edtSelectClass.setEnabled(false);
                        binding.edtSelectClass.setFocusable(false);
                        binding.edtSelectClass.setClickable(false);
                    }

                    break;
                }
            }

            // If class not found, log error
            if (selectedClassId == -1) {
                Log.e(TAG, "Intended class ID " + intentedClassId + " not found in class list");
                showToast("Selected class not found. Please select manually.");
            }
        }
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

                    // Get the new class ID
                    Integer newClassId = classNameToIdMap.get(selectedClassName);

                    // Only proceed if it's a different class
                    if (newClassId != null && newClassId != selectedClassId) {
                        Log.d(TAG, "Class changed from " + selectedClassId + " to " + newClassId);

                        // Update UI
                        binding.edtSelectClass.setText(selectedClassName);

                        // Store the selected class ID
                        selectedClassId = newClassId;
                        Log.d(TAG, "Selected class ID: " + selectedClassId);

                        // Clear any previous error
                        binding.edtSelectClass.setError(null);

                        // Load existing fees data for the newly selected class
                        fetchExistingFeesData();
                    } else if (newClassId != null && newClassId == selectedClassId) {
                        Log.d(TAG, "Same class selected, no changes needed");
                    }
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
                if (from != null && !from.isEmpty()) {
                    intentedClassId = getIntent().getIntExtra("ClassId", -1);
                    autoSelectClassIfIntended();
                }
            } else {
                showToast("No classes found.");
            }
        });
    }

    private void fetchExistingFeesData() {
        if (selectedClassId == -1) {
            Log.e(TAG, "No class selected, cannot fetch fees data");
            return;
        }

        // Clear existing data first before fetching new data
        Log.d(TAG, "Clearing existing data before fetching for class: " + selectedClassId);
        feesAdapter.clearAll();

        showLoading(true);
        String auth = "Bearer " + pref.getPrefString(this, pref.user_token);

        viewModel.getAllClassesWithId(auth, selectedClassId).observe(this, response -> {
            showLoading(false);

            if (response != null && response.isSuccess && response.data != null) {
                ClassModel2 classModel = response.data;
                populateFeesFromClassData(classModel.getData());
            } else {
                // Check if this is the specific null map error or no data
                String errorMessage = response != null ? response.message : "Failed to load class data";
                Log.e(TAG, "Error fetching class data: " + errorMessage);

                // If it's a "no data" scenario, clear adapter and show one empty item
                if (errorMessage != null && (errorMessage.contains("null") || errorMessage.contains("No data"))) {
                    Log.d(TAG, "No existing fees data found for class " + selectedClassId + ". Starting with empty adapter.");
                    // Ensure adapter has at least one empty item for input
                    feesAdapter.addNewItem();
                } else {
                    // Show toast for other errors and add empty item
                    showToast(errorMessage);
                    feesAdapter.addNewItem();
                }
            }
        });
    }

    /**
     * Convert camelCase to proper case with spaces - FIXED VERSION
     * e.g., "transportFees" -> "Transport Fees"
     *       "monthlyTuition" -> "Monthly Tuition"
     */
    private String convertCamelCaseToProperCase(String camelCase) {
        if (camelCase == null || camelCase.isEmpty()) {
            return "";
        }

        StringBuilder result = new StringBuilder();

        for (int i = 0; i < camelCase.length(); i++) {
            char c = camelCase.charAt(i);

            // Add space before uppercase letters (except first character)
            if (i > 0 && Character.isUpperCase(c)) {
                result.append(" ");
            }

            // Capitalize first letter of each word
            if (i == 0 || (i > 0 && Character.isUpperCase(c))) {
                result.append(Character.toUpperCase(c));
            } else {
                result.append(Character.toLowerCase(c));
            }
        }

        return result.toString();
    }

    /**
     * Convert fee type from server to camelCase key - FIXED VERSION
     * e.g., "Transport Fees" -> "transport fees" (normalized)
     *       "Library Fee" -> "library fees"
     */
    private String convertFeeTypeToKey(String feeType) {
        if (feeType == null || feeType.trim().isEmpty()) {
            return "";
        }

        // Convert to lowercase and normalize
        String lowerType = feeType.toLowerCase().trim();

        // Normalize common variations to standard format
        switch (lowerType) {
            case "tuition fee":
            case "monthly tuition fee":
            case "tuition fees":
            case "monthly tuition fees":
                return "tuition fees";
            case "admission fee":
            case "admission fees":
                return "admission fees";
            case "examination fee":
            case "examination fees":
            case "exam fee":
            case "exam fees":
                return "examination fees";
            case "transport fee":
            case "transport fees":
            case "transportation fee":
            case "transportation fees":
                return "transport fees";
            case "library fee":
            case "library fees":
                return "library fees";
            case "sports fee":
            case "sports fees":
            case "sport fee":
            case "sport fees":
                return "sports fees";
            case "lab fee":
            case "lab fees":
            case "laboratory fee":
            case "laboratory fees":
                return "lab fees";
            case "development fee":
            case "development fees":
                return "development fees";
            case "activity fee":
            case "activity fees":
            case "activities fee":
            case "activities fees":
                return "activity fees";
            case "computer fee":
            case "computer fees":
                return "computer fees";
            case "maintenance fee":
            case "maintenance fees":
                return "maintenance fees";
            case "security fee":
            case "security fees":
                return "security fees";
            case "uniform fee":
            case "uniform fees":
                return "uniform fees";
            case "book fee":
            case "book fees":
            case "books fee":
            case "books fees":
                return "book fees";
            case "stationery fee":
            case "stationery fees":
            case "stationary fee":
            case "stationary fees":
                return "stationery fees";
            default:
                // For custom fees, normalize spacing and return lowercase
                return normalizeSpacing(lowerType);
        }
    }

    /**
     * Convert key to proper display name with title case - FIXED VERSION
     * Ensures proper spacing between words
     */
    private String convertKeyToDisplayName(String key) {
        if (key == null || key.trim().isEmpty()) {
            return "";
        }

        // Convert to title case with proper spacing
        return toTitleCase(key.trim());
    }

    /**
     * Helper method to normalize spacing in fee names - ENHANCED
     */
    private String normalizeSpacing(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "";
        }

        // Replace multiple spaces/tabs/newlines with single space, trim, and convert to lowercase
        return input.replaceAll("\\s+", " ").trim().toLowerCase();
    }

    /**
     * Helper method to convert string to title case - FIXED VERSION
     * Ensures proper spacing between words
     */
    private String toTitleCase(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "";
        }

        String[] words = input.trim().toLowerCase().split("\\s+");
        StringBuilder titleCase = new StringBuilder();

        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            if (word.length() > 0) {
                if (i > 0) {
                    titleCase.append(" "); // Ensure space between words
                }
                // Capitalize first letter, rest lowercase
                titleCase.append(Character.toUpperCase(word.charAt(0)));
                if (word.length() > 1) {
                    titleCase.append(word.substring(1));
                }
            }
        }

        return titleCase.toString();
    }

    /**
     * Updated populateFeesFromClassData method - FIXED to prevent duplicate population
     */
    private void populateFeesFromClassData(ClassModel2.Data classData) {
        Log.d(TAG, "Populating fees from class data for class ID: " + selectedClassId);

        // Create a FeesStructure object to populate the adapter
        FeesStructure feesStructure = new FeesStructure();
        Map<String, String> feesMap = new HashMap<>();

        // Create a map to store original display names
        Map<String, String> displayNamesMap = new HashMap<>();

        // Add monthly tuition fees if available
        if (classData.getMonthlyTuitionFees() > 0) {
            String key = "tuition fees";
            String amount = String.valueOf((int) classData.getMonthlyTuitionFees());
            String displayName = "Tuition Fees";

            feesMap.put(key, amount);
            displayNamesMap.put(key, displayName);
            Log.d(TAG, "Added tuition fees: " + classData.getMonthlyTuitionFees());
        }

        // Add other fees from fee structure
        if (classData.getFeesStructure() != null && !classData.getFeesStructure().isEmpty()) {
            for (ClassModel2.FeesStructures feeStructure : classData.getFeesStructure()) {
                String originalFeeType = feeStructure.getFeeType();
                String amount = String.valueOf((int) feeStructure.getAmount());

                // Convert fee type to normalized key for internal use
                String feeKey = convertFeeTypeToKey(originalFeeType);

                // Only add if not already present (avoid duplicates)
                if (!feesMap.containsKey(feeKey)) {
                    feesMap.put(feeKey, amount);
                    // Store proper display name (title case with proper spacing)
                    displayNamesMap.put(feeKey, convertKeyToDisplayName(feeKey));

                    Log.d(TAG, "Added fee: " + originalFeeType + " -> " + feeKey + " = " + amount + " (Display: " + displayNamesMap.get(feeKey) + ")");
                } else {
                    Log.w(TAG, "Duplicate fee key found, skipping: " + feeKey);
                }
            }
        }

        // Set the fees map in the structure
        feesStructure.setFeesStructure(feesMap);

        // Populate the adapter with the fees structure and display names
        if (!feesMap.isEmpty()) {
            Log.d(TAG, "About to populate adapter with " + feesMap.size() + " unique fees");
            feesAdapter.populateFromFeesStructure(feesStructure, displayNamesMap);
            Log.d(TAG, "Successfully populated adapter with fees data");
        } else {
            Log.d(TAG, "No fees found for class " + selectedClassId + ". Adding empty item for input.");
            feesAdapter.addNewItem();
        }
    }

    private void showLoading(boolean isLoading) {
        Log.d(TAG, "Loading: " + isLoading);
        binding.loader.rlLoader.setVisibility(isLoading ? View.VISIBLE : View.GONE);

        // Disable/enable submit button during loading
        binding.createBtn.setEnabled(!isLoading);
    }

    private void showToast(String message) {
        Log.d(TAG, "Toast: " + message);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void saveFeesParticulars() {
        // Prevent multiple submissions
        if (isSubmitting) {
            Log.d(TAG, "Already submitting, ignoring request");
            return;
        }

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

        // Validate that at least one fee has been entered
        FeesStructure feesStructure = feesRequest.getFeesStructure();
        Map<String, String> allFees = feesStructure.getFeesStructure();

        if (allFees == null || allFees.isEmpty()) {
            Toast.makeText(this, "Please enter at least one fees amount", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if at least one fee has a non-empty amount
        boolean hasValidFee = false;
        for (String amount : allFees.values()) {
            if (amount != null && !amount.trim().isEmpty()) {
                hasValidFee = true;
                break;
            }
        }

        if (!hasValidFee) {
            Toast.makeText(this, "Please enter at least one fees amount", Toast.LENGTH_SHORT).show();
            return;
        }

        // Log the FeesStructure object for debugging
        Log.d("FeesData", "FeesStructure object: " + feesStructure.toString());

        // Set submitting flag and send data to server
        isSubmitting = true;
        sendFeesDataToServer(selectedClass, feesStructure);
    }

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
            isSubmitting = false; // Reset the flag

            if (response.isSuccess()) {
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
                // Clear existing data first
                feesAdapter.clearAll();

                // Create display names map with proper spacing
                Map<String, String> displayNamesMap = new HashMap<>();
                Map<String, String> feesMap = feesRequest.getFeesStructure().getFeesStructure();

                if (feesMap != null) {
                    for (String key : feesMap.keySet()) {
                        displayNamesMap.put(key, convertKeyToDisplayName(key));
                    }
                }

                feesAdapter.populateFromFeesStructure(feesRequest.getFeesStructure(), displayNamesMap);
            }
        } catch (Exception e) {
            Log.e("FeesData", "Error parsing existing fees data: " + e.getMessage());
        }
    }

    /**
     * Method to handle class change and ensure proper data clearing
     */
    private void handleClassChange(int newClassId) {
        Log.d(TAG, "Handling class change to ID: " + newClassId);

        // Clear any existing observer to prevent memory leaks
        if (viewModel != null) {
            viewModel.getAllClassesWithId("", selectedClassId).removeObservers(this);
        }

        // Update selected class ID
        selectedClassId = newClassId;

        // Fetch new data
        fetchExistingFeesData();
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
        // Load existing fees data for the set class
        fetchExistingFeesData();
    }

    // Utility method to get display name for any fee key (for external use)
    public String getDisplayNameForKey(String key) {
        return convertKeyToDisplayName(key);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}