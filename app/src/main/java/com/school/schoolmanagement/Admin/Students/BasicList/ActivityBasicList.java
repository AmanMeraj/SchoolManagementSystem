package com.school.schoolmanagement.Admin.Students.BasicList;

import static android.content.ContentValues.TAG;
import static android.widget.Toast.LENGTH_SHORT;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.school.schoolmanagement.Admin.Adapter.AdapterBasicList;
import com.school.schoolmanagement.Admin.Model.BasicList;
import com.school.schoolmanagement.Admin.Model.ClassModel;
import com.school.schoolmanagement.GlobalViewModel.ViewModel;
import com.school.schoolmanagement.HelperClasses.DataExportHelper;
import com.school.schoolmanagement.Model.StudentDetails;
import com.school.schoolmanagement.R;
import com.school.schoolmanagement.Utils.Utility;
import com.school.schoolmanagement.databinding.ActivityBasicListBinding;

import java.util.ArrayList;
import java.util.List;

public class ActivityBasicList extends Utility {
    ActivityBasicListBinding binding;
    private List<StudentDetails> studentList; // Original full list
    private List<StudentDetails> filteredStudentList; // Filtered list for display
    private AdapterBasicList adapterStudent;
    ViewModel viewModel;
    private ArrayList<ClassModel.Data> classList;
    private int selectedClassId = -1; // Variable to store selected class ID
    String auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityBasicListBinding.inflate(getLayoutInflater());
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
        }else{
            showToast("No Internet Connection.");
        }

        binding.edtSelectClass.setOnClickListener(v -> {
            if (classList.isEmpty()) {
                if(isInternetConnected(this)){
                    fetchClassData();
                }else{
                    showToast("No Internet Connection.");
                }
            } else {
                showClassSelectionDialog();
            }
        });

        // Initialize both lists
        studentList = new ArrayList<>();
        filteredStudentList = new ArrayList<>();

        // Pass filteredStudentList to adapter since that's what will be displayed
        adapterStudent = new AdapterBasicList(this, filteredStudentList);
        binding.rcPromoteStudent.setAdapter(adapterStudent);

        // Initialize with class data and set default to "All Classes"
        initializeClassData();
        setupSearchFilter();
        setupExportButtons(); // Add this line to setup export buttons

        binding.toolbar.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
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
        binding.edtSearch.addTextChangedListener(new TextWatcher() {
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

                    // Optional: Also search by other fields like student ID, email, etc.
                    // Uncomment and adjust based on your StudentDetails model
                    /*
                    else if (student.getStudentId() != null &&
                             student.getStudentId().toLowerCase().contains(lowerCaseQuery)) {
                        filteredStudentList.add(student);
                    }
                    else if (student.getEmail() != null &&
                             student.getEmail().toLowerCase().contains(lowerCaseQuery)) {
                        filteredStudentList.add(student);
                    }
                    */
                }
            }
        }

        // Notify adapter about data changes
        if (adapterStudent != null) {
            adapterStudent.notifyDataSetChanged();
        }

        // Optional: Show a message if no results found
        if (filteredStudentList.isEmpty() && !query.isEmpty() && !studentList.isEmpty()) {
            showToast("No students found matching: " + query);
        }

        Log.d(TAG, "Filtered " + studentList.size() + " students to " + filteredStudentList.size() + " results");
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
        String selectedClassName = binding.edtSelectClass.getText().toString();

        if (selectedClassName != null && !selectedClassName.isEmpty() && !selectedClassName.equals("All Classes")) {
            // If a specific class is selected, include it in the filename
            fileName = "students_basic_list_" + selectedClassName.replace(" ", "_");
        } else if (selectedClassName != null && selectedClassName.equals("All Classes")) {
            // If "All Classes" is selected, use a general filename
            fileName = "students_basic_list_all_classes";
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
        headers.add("Class");
        headers.add("Remaining Fees");
        headers.add("Phone");
        tableData.add(headers);

        // Add data rows from your filteredStudentList (to export only what's currently displayed)
        for (int i = 0; i < filteredStudentList.size(); i++) {
            StudentDetails student = filteredStudentList.get(i);
            ArrayList<String> row = new ArrayList<>();

            // Sr (Serial Number)
            row.add(String.valueOf(i + 1));

            // ID - adjust this based on your StudentDetails model
            row.add(student.getStudentId() != -1 ? String.valueOf(student.getStudentId()) : "");

            // S Name
            row.add(student.getStudentName() != null ? student.getStudentName() : "");

            // Class - adjust this based on your StudentDetails model
            row.add(student.getClassName() != null ? student.getClassName() : "");

            // Remaining Fees - adjust this based on your StudentDetails model
            row.add(student.getRemainingFees() != -1 ? String.valueOf(student.getRemainingFees()) : "0");

            // Phone - adjust this based on your StudentDetails model
            row.add(student.getMobileNumber()!= null ? student.getMobileNumber() : "");

            tableData.add(row);
        }

        return tableData;
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
            ClassModel.Data allClasses;
            allClasses = new ClassModel.Data();
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
            showClassSelectionDialog();
        } else {
            initializeClassData();
        }
    }

    private void showClassSelectionDialog() {
        // Create class names array for display
        String[] classNames = new String[classList.size()];
        for (int i = 0; i < classList.size(); i++) {
            classNames[i] = classList.get(i).getClassName();
        }

        // Create and show AlertDialog with ArrayAdapter
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Select Class")
                .setItems(classNames, (dialogInterface, position) -> {
                    // Handle class selection
                    ClassModel.Data selectedClass = classList.get(position);
                    onClassSelected(selectedClass);
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
                    adapterStudent.notifyDataSetChanged();

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
                adapterStudent.notifyDataSetChanged();

                Log.d(TAG, "Loaded " + studentList.size() + " students for class " + classId);
                showToast(response.message);
            } else {
                showToast(response.message);
            }
        });
    }

    private void showLoading(boolean isLoading) {
        Log.d(TAG, "Loading: " + isLoading);
        try {
            if (binding != null) {
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