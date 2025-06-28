package com.school.schoolmanagement.Admin.Students.PromoteStudents;

import static android.content.ContentValues.TAG;
import static android.widget.Toast.LENGTH_SHORT;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.school.schoolmanagement.Admin.Adapter.AdapterPromoteStudents;
import com.school.schoolmanagement.Admin.Model.ClassModel;
import com.school.schoolmanagement.GlobalViewModel.ViewModel;
import com.school.schoolmanagement.Model.PromotionBody;
import com.school.schoolmanagement.Model.StudentDetails;
import com.school.schoolmanagement.R;
import com.school.schoolmanagement.Utils.Utility;
import com.school.schoolmanagement.databinding.ActivityPromoteStudentsBinding;

import java.util.ArrayList;
import java.util.List;

public class ActivityPromoteStudents extends Utility {
    ActivityPromoteStudentsBinding binding;
    private List<StudentDetails> studentList; // Original full list
    private List<StudentDetails> filteredStudentList; // Filtered list for display
    private AdapterPromoteStudents adapter;
    ViewModel viewModel;
    private ArrayList<ClassModel.Data> classList;
    private int selectedClassId = -1; // Variable to store selected class ID
    private int promotedClassId = -1; // Variable to store selected class ID for promotion
    String auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityPromoteStudentsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        viewModel = new ViewModelProvider(this).get(ViewModel.class);
        auth = "Bearer " + pref.getPrefString(this, pref.user_token);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize classList
        classList = new ArrayList<>();
        if(isInternetConnected(this)){
            fetchClassData();
        } else {
            showToast("No Internet Connection.");
        }

        // Class selection click listener for "Select From Class"
        binding.edtSelectClass.setOnClickListener(v -> {
            if (classList.isEmpty()) {
                if(isInternetConnected(this)){
                    fetchClassData();
                } else {
                    showToast("No Internet Connection.");
                }
            } else {
                showClassSelectionDialog(false); // false for selecting from class
            }
        });

        // Class selection click listener for "Promote To Class"
        binding.edtPromoteToClass.setOnClickListener(v -> {
            if (classList.isEmpty()) {
                if(isInternetConnected(this)){
                    fetchClassData();
                } else {
                    showToast("No Internet Connection.");
                }
            } else {
                showPromoteToClassSelectionDialog(); // For selecting promotion target class
            }
        });

        // Initialize both lists
        studentList = new ArrayList<>();
        filteredStudentList = new ArrayList<>();

        // Initialize adapter with filtered list
        adapter = new AdapterPromoteStudents(filteredStudentList);

        // Set selection change listener to track selected IDs
        adapter.setOnSelectionChangeListener(selectedIds -> {
            Log.d(TAG, "Selection changed. Selected IDs: " + selectedIds.toString());
            // You can update UI here based on selection count
            // For example, update a counter or enable/disable buttons
        });

        binding.rcPromoteStudent.setAdapter(adapter);

        // Handle "Select All" Checkbox
        binding.selectAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
            adapter.selectAll(isChecked);
        });

        // Initialize with class data and set default to "All Classes"
        initializeClassData();
        setupSearchFilter();

        // Back button listener (if you have toolbar)
        if (binding.toolbar != null && binding.toolbar.backBtn != null) {
            binding.toolbar.backBtn.setOnClickListener(view -> finish());
        }

        // Refresh button listener (if you have one)
        if (binding.reloadBtn != null) {
            binding.reloadBtn.setOnClickListener(view -> refreshData());
        }

        // Add promote button click listener (if you have a promote button)
        setupPromoteButton();
    }

    private void setupPromoteButton() {
        // Assuming you have a promote button in your layout
        if (binding.updateBtn != null) {
            binding.updateBtn.setOnClickListener(v -> {
                if (promotedClassId == -1) {
                    showToast("Please select a class to promote students to");
                    return;
                }
                promoteSelectedStudents();
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Only reload data if we already have a selection, don't duplicate the initial load
        if (selectedClassId != -1) {
            if (selectedClassId == 0) {
                // All classes selected
                getAllStudents();
            } else {
                // Specific class selected
                getBasicList(auth, selectedClassId);
            }
        }
    }

    private void setupSearchFilter() {
        // Check if search EditText exists in your layout
        binding.edtSearchStudent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Not needed for this implementation
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Filter the list as user types
                filterStudentList(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Not needed for this implementation
            }
        });
    }

    private void filterStudentList(String query) {
        // Null check to prevent crashes
        if (studentList == null || filteredStudentList == null) {
            Log.w(TAG, "Lists not initialized yet, skipping filter");
            return;
        }

        filteredStudentList.clear();

        if (query.isEmpty()) {
            // If search query is empty, show all students
            filteredStudentList.addAll(studentList);
        } else {
            // Filter students based on name (case-insensitive)
            String lowerCaseQuery = query.toLowerCase().trim();

            for (StudentDetails student : studentList) {
                if (student != null && student.getStudentName() != null) {
                    // Check if student name contains the search query
                    if (student.getStudentName().toLowerCase().contains(lowerCaseQuery)) {
                        filteredStudentList.add(student);
                    }
                    // Also search by student ID if available
                    else if (String.valueOf(student.getStudentId()).contains(query.trim())) {
                        filteredStudentList.add(student);
                    }
                    // Also search by class name if available
                    else if (student.getClassName() != null &&
                            student.getClassName().toLowerCase().contains(lowerCaseQuery)) {
                        filteredStudentList.add(student);
                    }
                }
            }
        }

        // Notify adapter about data changes
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }

        // Optional: Show a message if no results found
        if (filteredStudentList.isEmpty() && !query.isEmpty() && !studentList.isEmpty()) {
            showToast("No students found matching: " + query);
        }

        Log.d(TAG, "Filtered " + studentList.size() + " students to " + filteredStudentList.size() + " results");
    }

    private void initializeClassData() {
        showLoading(true);
        viewModel.getAllClasses(auth).observe(this, response -> {
            showLoading(false);
            if (response != null) {
                classList.clear();

                // Add "All Classes" option at position 0
                ClassModel.Data allClassesOption = createAllClassesOption();
                if (allClassesOption != null) {
                    classList.add(allClassesOption);
                }

                if (response.data != null) {
                    classList.addAll(response.data.getData());
                }

                // Set default selection to "All Classes" and load all students
                binding.edtSelectClass.setText("All Classes");
                selectedClassId = 0;
                getAllStudents();

            } else {
                showToast("No classes found.");
            }
        });
    }

    private ClassModel.Data createAllClassesOption() {
        try {
            ClassModel.Data allClasses = new ClassModel.Data();
            allClasses.setClassId(0);
            allClasses.setClassName("All Classes");
            return allClasses;
        } catch (Exception e) {
            Log.e(TAG, "Error creating All Classes option: " + e.getMessage());
            return null;
        }
    }

    private void fetchClassData() {
        // This method is no longer needed as we initialize in initializeClassData()
        // But keeping it for backward compatibility if called elsewhere
        if (!classList.isEmpty()) {
            showClassSelectionDialog(false);
        } else {
            initializeClassData();
        }
    }

    private void showClassSelectionDialog(boolean isPromoteToClass) {
        // Create class names array for display
        String[] classNames = new String[classList.size()];
        for (int i = 0; i < classList.size(); i++) {
            classNames[i] = classList.get(i).getClassName();
        }

        String title = isPromoteToClass ? "Select Class to Promote To" : "Select Class";

        // Create and show AlertDialog with ArrayAdapter
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(title)
                .setItems(classNames, (dialogInterface, position) -> {
                    // Handle class selection
                    ClassModel.Data selectedClass = classList.get(position);
                    if (isPromoteToClass) {
                        onPromoteToClassSelected(selectedClass);
                    } else {
                        onClassSelected(selectedClass);
                    }
                    dialogInterface.dismiss();
                })
                .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss())
                .create();

        dialog.show();
    }

    private void showPromoteToClassSelectionDialog() {
        // Filter out "All Classes" option for promotion target
        ArrayList<ClassModel.Data> promoteClassList = new ArrayList<>();
        for (ClassModel.Data classData : classList) {
            if (classData.getClassId() != 0) { // Exclude "All Classes"
                promoteClassList.add(classData);
            }
        }

        if (promoteClassList.isEmpty()) {
            showToast("No classes available for promotion");
            return;
        }

        // Create class names array for display
        String[] classNames = new String[promoteClassList.size()];
        for (int i = 0; i < promoteClassList.size(); i++) {
            classNames[i] = promoteClassList.get(i).getClassName();
        }

        // Create and show AlertDialog with ArrayAdapter
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Select Class to Promote To")
                .setItems(classNames, (dialogInterface, position) -> {
                    // Handle class selection
                    ClassModel.Data selectedClass = promoteClassList.get(position);
                    onPromoteToClassSelected(selectedClass);
                    dialogInterface.dismiss();
                })
                .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss())
                .create();

        dialog.show();
    }

    private void onClassSelected(ClassModel.Data selectedClass) {
        selectedClassId = selectedClass.getClassId(); // Save the selected class ID
        binding.edtSelectClass.setText(selectedClass.getClassName()); // Update the EditText

        if (selectedClassId == 0) {
            // "All Classes" selected - call method with only auth
            getAllStudents();
        } else {
            // Specific class selected - call method with auth and classId
            getBasicList(auth, selectedClassId);
        }

        Log.d(TAG, "Selected Class ID: " + selectedClassId);
        showToast("Selected: " + selectedClass.getClassName());
    }

    private void onPromoteToClassSelected(ClassModel.Data selectedClass) {
        promotedClassId = selectedClass.getClassId(); // Save the promoted class ID
        binding.edtPromoteToClass.setText(selectedClass.getClassName()); // Update the EditText

        Log.d(TAG, "Selected Promote To Class ID: " + promotedClassId);
        showToast("Promote to: " + selectedClass.getClassName());
    }

    private void getAllStudents() {
        showLoading(true);
        viewModel.getAllStudents(auth).observe(this, response -> {
            showLoading(false);
            if (response != null) {
                studentList.clear(); // Clear existing data
                filteredStudentList.clear(); // Also clear filtered list

                if (response.data != null) {
                    studentList.addAll(response.data.getData());
                    filteredStudentList.addAll(studentList); // Initialize filtered list with all data
                    adapter.notifyDataSetChanged();

                    Log.d(TAG, "Loaded " + studentList.size() + " students");
                } else {
                    Toast.makeText(this, response.message, LENGTH_SHORT).show();
                }
            } else {
                showToast("No Students found.");
            }
        });
    }

    private void getBasicList(String auth, int classId) {
        showLoading(true);
        viewModel.getBasicList(auth, classId).observe(this, response -> {
            showLoading(false);
            if (response.isSuccess) {
                // Clear existing lists and add new data for selected class
                studentList.clear();
                filteredStudentList.clear();

                studentList.addAll(response.data.getData());
                filteredStudentList.addAll(studentList); // Initialize filtered list with all data
                adapter.notifyDataSetChanged();

                Log.d(TAG, "Loaded " + studentList.size() + " students for class " + classId);
                showToast(response.message);
            } else {
                showToast(response.message);
            }
        });
    }

    private void refreshData() {
        // Clear search text if search field exists
        binding.edtSearchStudent.setText("");

        // Show loading and refresh based on current selection
        if (selectedClassId == -1) {
            // No class selected yet, initialize data
            initializeClassData();
        } else if (selectedClassId == 0) {
            // All classes selected, refresh all students
            getAllStudents();
        } else {
            // Specific class selected, refresh that class data
            getBasicList(auth, selectedClassId);
        }

        showToast("Data refreshed");
        Log.d(TAG, "Data refreshed for class ID: " + selectedClassId);
    }

    // Method to get selected student IDs in array format [18,22]
    public List<Integer> getSelectedStudentIds() {
        List<Integer> selectedIds = new ArrayList<>();
        for (StudentDetails student : filteredStudentList) {
            if (student.isSelected()) {
                selectedIds.add(student.getStudentId());
            }
        }
        return selectedIds;
    }

    // Method to get selected students for promotion
    public List<StudentDetails> getSelectedStudents() {
        List<StudentDetails> selectedStudents = new ArrayList<>();
        for (StudentDetails student : filteredStudentList) {
            if (student.isSelected()) {
                selectedStudents.add(student);
            }
        }
        return selectedStudents;
    }

    // Method to get selected IDs as JSON string format
    public List<Long> getSelectedStudentIdsAsLong() {
        List<Long> selectedIds = new ArrayList<>();
        for (StudentDetails student : filteredStudentList) {
            if (student.isSelected()) {
                selectedIds.add((long) student.getStudentId()); // Convert int to long
            }
        }
        return selectedIds;
    }

    // Updated promotion method
    private void performStudentPromotion(List<Long> studentIds) {
        if (studentIds.isEmpty()) {
            showToast("No students selected for promotion");
            return;
        }

        if (promotedClassId == -1) {
            showToast("No target class selected for promotion");
            return;
        }

        // Send the List<Long> directly, not wrapped in an object
        // No need for PromotionBody wrapper

        // Show loading
        showLoading(true);

        // Call the API through ViewModel - pass the List<Long> directly
        viewModel.promoteStudents(auth, promotedClassId, studentIds).observe(this, response -> {
            showLoading(false);

            if (response != null && response.isSuccess) {
                showToast("Students promoted successfully to " + binding.edtPromoteToClass.getText().toString());

                // Clear selections after successful promotion
                clearAllSelections();

                // Reset the promote to class selection
                binding.edtPromoteToClass.setText("");
                promotedClassId = -1;

                // Refresh the data to show updated student list
                refreshData();

                Log.d(TAG, "Students promoted successfully");
            } else {
                String errorMessage = response != null ? response.message : "Failed to promote students";
                showToast(errorMessage);
                Log.e(TAG, "Promotion failed: " + errorMessage);
            }
        });

        Log.d(TAG, "Promoting students with IDs: " + studentIds.toString() + " to class ID: " + promotedClassId);
    }

    // Update the main promotion method to use Long IDs
    public void promoteSelectedStudents() {
        List<Long> selectedIds = getSelectedStudentIdsAsLong(); // Use Long instead of Integer
        if (selectedIds.isEmpty()) {
            showToast("Please select students to promote");
            return;
        }

        if (promotedClassId == -1) {
            showToast("Please select a class to promote students to");
            return;
        }

        // Log the selected IDs for debugging
        Log.d(TAG, "Selected Student IDs: " + selectedIds.toString());
        Log.d(TAG, "Promote to Class ID: " + promotedClassId);

        // Show confirmation dialog
        String promoteToClassName = binding.edtPromoteToClass.getText().toString();
        new AlertDialog.Builder(this)
                .setTitle("Promote Students")
                .setMessage("Are you sure you want to promote " + selectedIds.size() +
                        " selected students to " + promoteToClassName + "?\n\nStudent IDs: " + selectedIds.toString())
                .setPositiveButton("Promote", (dialog, which) -> {
                    // Implement your promotion logic here
                    performStudentPromotion(selectedIds);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // Method to clear all selections
    public void clearAllSelections() {
        for (StudentDetails student : studentList) {
            student.setSelected(false);
        }
        for (StudentDetails student : filteredStudentList) {
            student.setSelected(false);
        }

        // Update select all checkbox
        if (binding.selectAll != null) {
            binding.selectAll.setChecked(false);
        }

        // Notify adapter
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    // Method to get count of selected students
    public int getSelectedCount() {
        return getSelectedStudentIds().size();
    }

    private void showLoading(boolean isLoading) {
        Log.d(TAG, "Loading: " + isLoading);
        try {
            if (binding != null && binding.loader != null) {
                binding.loader.rlLoader.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error showing/hiding loader: " + e.getMessage(), e);
        }
    }

    private void showToast(String message) {
        Log.d(TAG, "Toast: " + message);
        try {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(this, message, LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error showing toast: " + e.getMessage(), e);
        }
    }
}