package com.school.schoolmanagement.Admin.Report;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.school.schoolmanagement.Adapters.StudentInfoAdapter;
import com.school.schoolmanagement.Admin.Model.ClassModel;
import com.school.schoolmanagement.Admin.Model.StudentInfoReport;
import com.school.schoolmanagement.GlobalViewModel.ViewModel;
import com.school.schoolmanagement.HelperClasses.ClassApiHelper;
import com.school.schoolmanagement.HelperClasses.DataExportHelper;
import com.school.schoolmanagement.R;
import com.school.schoolmanagement.Utils.Utility;
import com.school.schoolmanagement.databinding.ActivityStudentsInfoReportBinding;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityStudentsInfoReport extends Utility implements ClassApiHelper.ClassListCallback {

    private static final String TAG = "ActivityStudentsInfoReport";

    ActivityStudentsInfoReportBinding binding;
    private StudentInfoAdapter adapter;
    private ArrayList<StudentInfoReport.Datum> studentList;
    private ArrayList<StudentInfoReport.Datum> filteredList;

    // Class-related variables
    private ClassApiHelper classApiHelper;
    private ViewModel viewModel;
    private final ArrayList<ClassModel.Data> classList = new ArrayList<>();
    private final ArrayList<String> classNames = new ArrayList<>();
    private ArrayAdapter<String> classAdapter;
    private int selectedClassId = -1;
    private String selectedClassName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityStudentsInfoReportBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeViews();
        setupRecyclerView();
        setupClassDropdown();
        setupSearchFilter();
        setupClickListeners();
        setupExportButtons();

        // Load initial data
        loadInitialData();
        binding.toolbar.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void initializeViews() {
        studentList = new ArrayList<>();
        filteredList = new ArrayList<>();

        // Initialize ViewModel and ClassApiHelper
        viewModel = new ViewModelProvider(this).get(ViewModel.class);
        classApiHelper = new ClassApiHelper(this);
    }

    private void setupRecyclerView() {
        adapter = new StudentInfoAdapter(this, filteredList);
        binding.rcAccountReport.setLayoutManager(new LinearLayoutManager(this));
        binding.rcAccountReport.setAdapter(adapter);
    }

    private void setupClassDropdown() {
        // Initialize with empty list first
        classNames.clear();
        classNames.add("All Classes");

        classAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                classNames
        );
        classAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        binding.edtClass.setAdapter(classAdapter);

        // Set threshold to show dropdown on click
        binding.edtClass.setThreshold(1);
        binding.edtClass.setHint("Select Class");

        binding.edtClass.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                handleClassSelection(position);
            }
        });

        // Handle focus change to show dropdown
        binding.edtClass.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                binding.edtClass.showDropDown();
            }
        });

        // Handle click to show dropdown
        binding.edtClass.setOnClickListener(v -> {
            binding.edtClass.showDropDown();
        });
    }

    private void handleClassSelection(int position) {
        if (position == 0) {
            // "All Classes" selected
            selectedClassId = -1;
            selectedClassName = "All Classes";
            binding.edtClass.setText("All Classes", false);
            Toast.makeText(this, "All Classes selected", Toast.LENGTH_SHORT).show();
        } else if (position > 0 && position <= classList.size()) {
            // Adjust position since we have "All Classes" at index 0
            int actualPosition = position - 1;
            if (actualPosition < classList.size()) {
                ClassModel.Data selectedClass = classList.get(actualPosition);
                selectedClassId = selectedClass.getClassId();
                selectedClassName = selectedClass.getClassName();
                binding.edtClass.setText(selectedClass.getClassName(), false);

                Toast.makeText(this, "Class selected: " + selectedClass.getClassName() +
                        " (ID: " + selectedClassId + ")", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loadInitialData() {
        showLoading("Loading classes...");
        classApiHelper.fetchAllClasses(this);
    }

    // Update class ArrayAdapter when classes are loaded
    private void updateClassArrayAdapter() {
        runOnUiThread(() -> {
            classNames.clear();
            classNames.add("All Classes");

            for (ClassModel.Data classData : classList) {
                classNames.add(classData.getClassName());
            }

            classAdapter.notifyDataSetChanged();

            // Enable the dropdown
            binding.edtClass.setEnabled(true);
        });
    }

    // ClassApiHelper.ClassListCallback implementation
    @Override
    public void onSuccess(ArrayList<ClassModel.Data> classList) {
        this.classList.clear();
        this.classList.addAll(classList);
        updateClassArrayAdapter();
        hideLoading();

        // Load initial students data after classes are loaded
        loadStudentsData(false);
    }

    @Override
    public void onError(String errorMessage) {
        hideLoading();
        runOnUiThread(() -> {
            Toast.makeText(this, "Class Loading Error: " + errorMessage, Toast.LENGTH_LONG).show();
            // Still try to load students data even if classes fail
            loadStudentsData(false);
        });
    }

    @Override
    public void onLoading(boolean isLoading) {
        if (isLoading) {
            showLoading("Loading classes...");
        } else {
            hideLoading();
        }
    }

    private void showLoading(String message) {
        runOnUiThread(() -> {
            if (binding.loader != null && binding.loader.rlLoader != null) {
                binding.loader.rlLoader.setVisibility(View.VISIBLE);
            }
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        });
    }

    private void hideLoading() {
        runOnUiThread(() -> {
            if (binding.loader != null && binding.loader.rlLoader != null) {
                binding.loader.rlLoader.setVisibility(View.GONE);
            }
        });
    }

    private void setupSearchFilter() {
        binding.edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterStudents(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupClickListeners() {
        // Create/Generate Report Button
        binding.btnCreatePaper.setOnClickListener(v -> {
            handleCreateReport();
        });

        // Pagination buttons
        binding.prevButton.setOnClickListener(v -> loadPreviousPage());
        binding.nextButton.setOnClickListener(v -> loadNextPage());
    }

    // NEW: Setup export buttons with proper functionality
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

    // NEW: Handle export functionality
    private void handleExport(ArrayList<ArrayList<String>> tableData, String action) {
        if (tableData.size() <= 1) { // Only headers, no data
            Toast.makeText(this, "No data to export", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Create DataExportHelper instance with context
            DataExportHelper exportHelper = new DataExportHelper(this);

            // Generate dynamic filename based on selected criteria
            String fileName = generateDynamicFileName();

            // Use the exportData method with dynamic filename
            exportHelper.exportData(tableData, action, fileName);

        } catch (Exception e) {
            Log.e(TAG, "Export error: " + e.getMessage());
            Toast.makeText(this, "Export failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    // NEW: Generate dynamic filename
    private String generateDynamicFileName() {
        String fileName = "student_info_report";

        // Include class name if selected
        if (selectedClassId != -1 && !selectedClassName.isEmpty()) {
            fileName += "_" + selectedClassName.replace(" ", "_");
        }

        // Add timestamp for uniqueness
        fileName += "_" + System.currentTimeMillis();

        return fileName;
    }

    // NEW: Prepare table data for export
    private ArrayList<ArrayList<String>> prepareTableData() {
        ArrayList<ArrayList<String>> tableData = new ArrayList<>();

        // Add header row
        ArrayList<String> headers = new ArrayList<>();
        headers.add("Sr");
        headers.add("Student ID");
        headers.add("Student Name");
        headers.add("Father Name");
        headers.add("Class");
        headers.add("Roll Number");
        headers.add("Admission Date");
        headers.add("DOB");
        headers.add("Age");
        headers.add("Gender");
        headers.add("Student Birth ID");
        headers.add("Religion");
        headers.add("Cast");
        headers.add("Status");
        tableData.add(headers);

        int count = 1;
        // Add data rows from filteredList
        for (StudentInfoReport.Datum student : filteredList) {
            ArrayList<String> row = new ArrayList<>();
            row.add(String.valueOf(count));
            row.add(student.getStudentId() != -1 ? String.valueOf(student.getStudentId()) : "");
            row.add(student.getStudentName() != null ? student.getStudentName() : "");
            row.add(student.getParent() != null && student.getParent().getFatherName() != null ?
                    student.getParent().getFatherName() : "");
            row.add(student.getClassName() != null ? student.getClassName() : "");
            row.add(student.getRollNumber() != null ? student.getRollNumber() : "");
            row.add(student.getDateOfAdmission() != null ? student.getDateOfAdmission() : "");
            row.add(student.getDateOfBirth() != null ? student.getDateOfBirth() : "");
//            row.add(student.getAge() != null ? String.valueOf(student.getAge()) : "");
//            row.add(student.getGender() != null ? student.getGender() : "");
//            row.add(student.getStudentBirthId() != null ? student.getStudentBirthId() : "");
//            row.add(student.getReligion() != null ? student.getReligion() : "");
//            row.add(student.getCast!= null ? student.getCast() : "");
            row.add("Active");
            tableData.add(row);
            count++;
        }

        return tableData;
    }

    private void handleCreateReport() {
        if (selectedClassId == -1) {
            // Load all students
            Toast.makeText(this, "Generating report for all classes...", Toast.LENGTH_SHORT).show();
            loadStudentsData(false);
        } else {
            // Load students for selected class
            ClassModel.Data selectedClass = findClassById(selectedClassId);
            String className = selectedClass != null ? selectedClass.getClassName() : "Unknown";

            Toast.makeText(this, "Generating report for class: " + className +
                    " (ID: " + selectedClassId + ")", Toast.LENGTH_LONG).show();

            // Here you can make your API call with the selectedClassId
            loadStudentsData(true);
        }
    }

    // Helper method to find class by ID
    private ClassModel.Data findClassById(int classId) {
        for (ClassModel.Data classData : classList) {
            if (classData.getClassId() == classId) {
                return classData;
            }
        }
        return null;
    }

    private void loadStudentsData(boolean isFiltered) {
        showLoader(true);

        // Example API call structure - replace with your actual API endpoints
        if (isFiltered && selectedClassId != -1) {
            // API call for filtered data by class
            loadStudentsByClass(selectedClassId);
        } else {
            // API call for all students
            loadAllStudents();
        }
    }

    private void loadAllStudents() {
        String authToken = "Bearer " + pref.getPrefString(this, pref.user_token);
        viewModel.getStudentReport(authToken).observe(this, response -> {
            showLoader(false);
            if (response.isSuccess && response.data != null) {
                StudentInfoReport report = response.data;
                if (report.getData() != null) {
                    studentList.clear();
                    studentList.addAll(report.getData());
                    filteredList.clear();
                    filteredList.addAll(studentList);
                    adapter.notifyDataSetChanged();
                    updatePageInfo();
                    Toast.makeText(ActivityStudentsInfoReport.this,
                            "Loaded " + studentList.size() + " students", Toast.LENGTH_SHORT).show();
                }
            } else {
                showError("Failed to load student data");
            }
        });
    }

    private void loadStudentsByClass(int classId) {
        String authToken = "Bearer " + pref.getPrefString(this, pref.user_token);

        viewModel.getStudentReportClassWise(authToken, classId).observe(this, response -> {
            if (response.isSuccess && response.data != null) {
                showLoader(false);
                StudentInfoReport report = response.data;
                if (report.getData() != null) {
                    studentList.clear();
                    studentList.addAll(report.getData());
                    filteredList.clear();
                    filteredList.addAll(studentList);
                    adapter.notifyDataSetChanged();
                    updatePageInfo();

                    ClassModel.Data selectedClass = findClassById(classId);
                    String className = selectedClass != null ? selectedClass.getClassName() : "Class " + classId;
                    Toast.makeText(ActivityStudentsInfoReport.this,
                            "Loaded " + studentList.size() + " students for " + className, Toast.LENGTH_SHORT).show();
                }
            } else {
                showLoader(false);
                showError("Failed to load filtered student data");
            }
        });
    }

    private void filterStudents(String query) {
        filteredList.clear();

        if (query.isEmpty()) {
            filteredList.addAll(studentList);
        } else {
            String lowerCaseQuery = query.toLowerCase();
            for (StudentInfoReport.Datum student : studentList) {
                if (matchesQuery(student, lowerCaseQuery)) {
                    filteredList.add(student);
                }
            }
        }

        adapter.notifyDataSetChanged();
        updatePageInfo();
    }

    private boolean matchesQuery(StudentInfoReport.Datum student, String query) {
        return (student.getStudentName() != null && student.getStudentName().toLowerCase().contains(query)) ||
                (student.getRollNumber() != null && student.getRollNumber().toLowerCase().contains(query)) ||
                (student.getClassName() != null && student.getClassName().toLowerCase().contains(query)) ||
                (student.getParent() != null && student.getParent().getFatherName() != null &&
                        student.getParent().getFatherName().toLowerCase().contains(query));
    }

    private void showLoader(boolean show) {
        if (show) {
            if (binding.loader != null && binding.loader.rlLoader != null) {
                binding.loader.rlLoader.setVisibility(View.VISIBLE);
            }
        } else {
            if (binding.loader != null && binding.loader.rlLoader != null) {
                binding.loader.rlLoader.setVisibility(View.GONE);
            }
        }
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void updatePageInfo() {
        // Update pagination info
        int totalItems = filteredList.size();
        binding.pageTv.setText("Showing " + totalItems + " entries");
    }

    private void loadPreviousPage() {
        // Implement previous page functionality
        Toast.makeText(this, "Previous page", Toast.LENGTH_SHORT).show();
    }

    private void loadNextPage() {
        // Implement next page functionality
        Toast.makeText(this, "Next page", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}