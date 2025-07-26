package com.school.schoolmanagement.Admin.ClassTest;

import static android.content.ContentValues.TAG;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.school.schoolmanagement.Adapters.ClassTestResultAdapter;
import com.school.schoolmanagement.Admin.Model.ClassModel;
import com.school.schoolmanagement.Admin.Model.SubjectModel;
import com.school.schoolmanagement.GlobalViewModel.ViewModel;
import com.school.schoolmanagement.HelperClasses.ClassApiHelper;
import com.school.schoolmanagement.HelperClasses.ClassTestResultApiHelper;
import com.school.schoolmanagement.HelperClasses.DataExportHelper;
import com.school.schoolmanagement.HelperClasses.StudentHelper;
import com.school.schoolmanagement.HelperClasses.SubjectApiHelper;
import com.school.schoolmanagement.Model.ClassTestResult;
import com.school.schoolmanagement.Model.StudentDetails;
import com.school.schoolmanagement.Model.StudentsAttendanceReport;
import com.school.schoolmanagement.R;
import com.school.schoolmanagement.Utils.Utility;
import com.school.schoolmanagement.databinding.ActivityClassTestResultBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ActivityClassTestResult extends Utility implements
        ClassApiHelper.ClassListCallback,
        SubjectApiHelper.SubjectListCallback,
        StudentHelper.StudentHelperCallback {

    private ActivityClassTestResultBinding binding;
    private ClassTestResultApiHelper classTestResultApiHelper;
    private String currentStartDate = "";
    private String currentEndDate = "";
    private final ArrayList<ClassTestResult.Datum> attendanceList = new ArrayList<>();



    // Add these member variables to your ActivityClassTestResult class
    private RecyclerView recyclerViewResults;
    private ClassTestResultAdapter adapter;
    private ArrayList<ClassTestResult.Datum> testResultsList = new ArrayList<>();

    private String selectedMethod = "class_subject";
    private String startDate = "";
    private String endDate = "";

    // Helper classes
    private ClassApiHelper classApiHelper;
    private SubjectApiHelper subjectApiHelper;
    private StudentHelper studentHelper;
    private ViewModel viewModel;

    // Data lists
    private final ArrayList<ClassModel.Data> classList = new ArrayList<>();
    private final ArrayList<SubjectModel> subjectList = new ArrayList<>();
    private final List<StudentDetails> studentList = new ArrayList<>();

    // String lists for adapters
    private final ArrayList<String> classNames = new ArrayList<>();
    private final ArrayList<String> subjectNames = new ArrayList<>();
    private final ArrayList<String> studentNames = new ArrayList<>();

    // ArrayAdapter instances
    private ArrayAdapter<String> classAdapter;
    private ArrayAdapter<String> subjectAdapter;
    private ArrayAdapter<String> studentAdapter;

    // Selected IDs
    private int selectedClassId = -1;
    private int selectedSubjectId = -1;
    private int selectedStudentId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityClassTestResultBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        setupWindowInsets();
        initializeComponents();
        setupUI();
        loadInitialData();

    }

    private void setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initializeComponents() {
        viewModel = new ViewModelProvider(this).get(ViewModel.class);
        classApiHelper = new ClassApiHelper(this);
        subjectApiHelper = new SubjectApiHelper(this);
        classTestResultApiHelper = new ClassTestResultApiHelper(viewModel, this);


        String authToken = "Bearer " + pref.getPrefString(this, pref.user_token);
        studentHelper = new StudentHelper(this, viewModel, authToken, this, this);
        setupCallbacks();
        setupRecyclerView();
    }


    private void setupRecyclerView() {
        // Assuming you have a RecyclerView with id 'recyclerViewResults' in your layout
        if (binding.rcTestResult != null) {
            adapter = new ClassTestResultAdapter(testResultsList);
            binding.rcTestResult.setAdapter(adapter);
        }
    }

    // Updated display methods


    // Helper methods
    private void showResultViews(boolean show) {
        if (binding.rcTestResult != null) {
            binding.rcTestResult.setVisibility(show ? View.VISIBLE : View.GONE);
        }

        // Show/hide other result-related views
        // You might have result card, summary views, etc.
    }

    private void showEmptyState(String message) {
        showResultViews(false);
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void updateResultSummary(String resultType, String message) {
        // Update summary TextViews if you have them
        // Example:
        // TextView tvResultType = findViewById(R.id.tvResultType);
        // TextView tvResultMessage = findViewById(R.id.tvResultMessage);
        // TextView tvResultCount = findViewById(R.id.tvResultCount);

        // if (tvResultType != null) tvResultType.setText(resultType);
        // if (tvResultMessage != null) tvResultMessage.setText(message);
        // if (tvResultCount != null) tvResultCount.setText("Total Results: " + testResultsList.size());
    }

    private void setupCallbacks() {
        // Set up class subject callback
        classTestResultApiHelper.setClassSubjectCallback(new ClassTestResultApiHelper.ClassSubjectCallback() {
            @Override
            public void onClassSubjectSuccess(ClassTestResult response) {
                runOnUiThread(() -> {
                    hideLoading();
                    // Handle successful response - display results
                    displayClassSubjectResults(response);
                });
            }

            @Override
            public void onClassSubjectError(String errorMessage, int errorCode) {
                runOnUiThread(() -> {
                    hideLoading();
                    Toast.makeText(ActivityClassTestResult.this,
                            "Error: " + errorMessage, Toast.LENGTH_LONG).show();
                });
            }

            @Override
            public void onClassSubjectNetworkFailure() {
                runOnUiThread(() -> {
                    hideLoading();
                    Toast.makeText(ActivityClassTestResult.this,
                            "Network error. Please check your connection.", Toast.LENGTH_LONG).show();
                });
            }

            @Override
            public void showClassSubjectLoading() {
                showLoading("Loading class test results...");
            }

            @Override
            public void hideClassSubjectLoading() {
                hideLoading();
            }
        });

        // Set up student subject callback
        classTestResultApiHelper.setStudentSubjectCallback(new ClassTestResultApiHelper.StudentSubjectCallback() {
            @Override
            public void onStudentSubjectSuccess(ClassTestResult response) {
                runOnUiThread(() -> {
                    hideLoading();
                    displayStudentSubjectResults(response);
                });
            }

            @Override
            public void onStudentSubjectError(String errorMessage, int errorCode) {
                runOnUiThread(() -> {
                    hideLoading();
                    Toast.makeText(ActivityClassTestResult.this,
                            "Error: " + errorMessage, Toast.LENGTH_LONG).show();
                });
            }

            @Override
            public void onStudentSubjectNetworkFailure() {
                runOnUiThread(() -> {
                    hideLoading();
                    Toast.makeText(ActivityClassTestResult.this,
                            "Network error. Please check your connection.", Toast.LENGTH_LONG).show();
                });
            }

            @Override
            public void showStudentSubjectLoading() {
                showLoading("Loading student test results...");
            }

            @Override
            public void hideStudentSubjectLoading() {
                hideLoading();
            }
        });

        // Set up date range callback
        classTestResultApiHelper.setDateRangeCallback(new ClassTestResultApiHelper.DateRangeCallback() {
            @Override
            public void onDateRangeSuccess(ClassTestResult response) {
                runOnUiThread(() -> {
                    hideLoading();
                    displayDateRangeResults(response);
                });
            }

            @Override
            public void onDateRangeError(String errorMessage, int errorCode) {
                runOnUiThread(() -> {
                    hideLoading();
                    Toast.makeText(ActivityClassTestResult.this,
                            "Error: " + errorMessage, Toast.LENGTH_LONG).show();
                });
            }

            @Override
            public void onDateRangeNetworkFailure() {
                runOnUiThread(() -> {
                    hideLoading();
                    Toast.makeText(ActivityClassTestResult.this,
                            "Network error. Please check your connection.", Toast.LENGTH_LONG).show();
                });
            }

            @Override
            public void showDateRangeLoading() {
                showLoading("Loading test results for date range...");
            }

            @Override
            public void hideDateRangeLoading() {
                hideLoading();
            }
        });
    }
    private void displayClassSubjectResults(ClassTestResult response) {
        if (response != null && response.getData() != null) {
            testResultsList.clear();

            // Assuming ClassTestResult has a getData() method that returns a list of test results
            if (response.getData() instanceof List) {
                List<?> dataList = (List<?>) response.getData();
                for (Object item : dataList) {
                    if (item instanceof ClassTestResult.Datum) {
                        testResultsList.add((ClassTestResult.Datum) item);
                    }
                }
            }

            // Update the adapter
            if (adapter != null) {
                adapter.updateResults(testResultsList, "Class-Subject Results");
                adapter.notifyDataSetChanged();
            }

            // Show/hide result views
            showResultViews(true);

            // Show success message
            Toast.makeText(this, "Class-Subject results loaded: " + testResultsList.size() + " records", Toast.LENGTH_SHORT).show();

            // Update result summary if you have summary views
            updateResultSummary("Class-Subject", response.getMessage());
        } else {
            showEmptyState("No class-subject results found");
        }
    }

    private void displayStudentSubjectResults(ClassTestResult response) {
        if (response != null && response.getData() != null) {
            testResultsList.clear();

            // Handle student-subject specific data
            if (response.getData() instanceof List) {
                List<?> dataList = (List<?>) response.getData();
                for (Object item : dataList) {
                    if (item instanceof ClassTestResult.Datum) {
                        testResultsList.add((ClassTestResult.Datum) item);
                    }
                }
            }

            // Update the adapter
            if (adapter != null) {
                adapter.updateResults(testResultsList, "Student-Subject Results");
                adapter.notifyDataSetChanged();
            }

            // Show/hide result views
            showResultViews(true);

            // Show success message
            Toast.makeText(this, "Student-Subject results loaded: " + testResultsList.size() + " records", Toast.LENGTH_SHORT).show();

            // Update result summary
            updateResultSummary("Student-Subject", response.getMessage());
        } else {
            showEmptyState("No student-subject results found");
        }
    }

    private void displayDateRangeResults(ClassTestResult response) {
        if (response != null && response.getData() != null) {
            testResultsList.clear();

            // Handle date range specific data
            if (response.getData() instanceof List) {
                List<?> dataList = (List<?>) response.getData();
                for (Object item : dataList) {
                    if (item instanceof ClassTestResult.Datum) {
                        testResultsList.add((ClassTestResult.Datum) item);
                    }
                }
            }

            // Update the adapter
            if (adapter != null) {
                adapter.updateResults(testResultsList, "Date Range Results");
                adapter.notifyDataSetChanged();
            }

            // Show/hide result views
            showResultViews(true);

            // Show success message with date range info
            String dateRangeInfo = "Date Range (" + startDate + " to " + endDate + ")";
            Toast.makeText(this, dateRangeInfo + " results loaded: " + testResultsList.size() + " records", Toast.LENGTH_SHORT).show();

            // Update result summary
            updateResultSummary(dateRangeInfo, response.getMessage());
        } else {
            showEmptyState("No results found for the selected date range");
        }
    }
    private void setupUI() {
        hideResultCard();
        selectMethod("class_subject");
        showFieldsForMethod(selectedMethod);
        setupExportButtons();
        setupAutoCompleteTextViews();
        setupClickListeners();
    }

    private void setupAutoCompleteTextViews() {
        setupClassAutoComplete();
        setupSubjectAutoComplete();
        setupStudentAutoComplete();
    }

    private void setupClassAutoComplete() {
        // Initialize with empty list first
        classNames.clear();
        classNames.add("Select Class");

        classAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, classNames);
        classAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.edtClassesPaper.setAdapter(classAdapter);

        // Set threshold to show dropdown on click
        binding.edtClassesPaper.setThreshold(1);
        binding.edtClassesPaper.setHint("Select Class");

        binding.edtClassesPaper.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                handleClassSelection(position);
            }
        });

        // Handle focus change to show dropdown
        binding.edtClassesPaper.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                binding.edtClassesPaper.showDropDown();
            }
        });

        // Handle click to show dropdown
        binding.edtClassesPaper.setOnClickListener(v -> {
            binding.edtClassesPaper.showDropDown();
        });
    }

    private void setupSubjectAutoComplete() {
        subjectNames.clear();
        subjectNames.add("Select Subject");

        subjectAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, subjectNames);
        subjectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.edtSubject.setAdapter(subjectAdapter);

        // Set threshold to show dropdown on click
        binding.edtSubject.setThreshold(1);
        binding.edtSubject.setHint("Select Subject");

        // Initially disable subject dropdown
        binding.edtSubject.setEnabled(false);

        binding.edtSubject.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                handleSubjectSelection(position);
            }
        });

        // Handle focus change to show dropdown
        binding.edtSubject.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus && binding.edtSubject.isEnabled()) {
                binding.edtSubject.showDropDown();
            }
        });

        // Handle click to show dropdown
        binding.edtSubject.setOnClickListener(v -> {
            if (binding.edtSubject.isEnabled()) {
                binding.edtSubject.showDropDown();
            }
        });
    }

    private void setupStudentAutoComplete() {
        studentNames.clear();
        studentNames.add("Select Student");

        studentAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, studentNames);
        studentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.edtStudents.setAdapter(studentAdapter);

        // Set threshold to show dropdown on click
        binding.edtStudents.setThreshold(1);
        binding.edtStudents.setHint("Select Student");

        // Initially disable student dropdown
        binding.edtStudents.setEnabled(false);

        binding.edtStudents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                handleStudentSelection(position);
            }
        });

        // Handle focus change to show dropdown
        binding.edtStudents.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus && binding.edtStudents.isEnabled()) {
                binding.edtStudents.showDropDown();
            }
        });

        // Handle click to show dropdown
        binding.edtStudents.setOnClickListener(v -> {
            if (binding.edtStudents.isEnabled()) {
                binding.edtStudents.showDropDown();
            }
        });
    }

    private void handleClassSelection(int position) {
        if (position > 0 && position <= classList.size()) {
            // Adjust position since we have "Select Class" at index 0
            int actualPosition = position - 1;
            if (actualPosition < classList.size()) {
                selectedClassId = classList.get(actualPosition).getClassId();

                // Handle different behaviors based on selected method
                if ("class_subject".equals(selectedMethod)) {
                    // Load subjects for class_subject method
                    loadSubjectsForClass(selectedClassId);
                } else if ("student_subject".equals(selectedMethod)) {
                    // Load students filtered by class for student_subject method
                    loadStudentsForClass(selectedClassId);
                    // Also load subjects for the selected class
                    loadSubjectsForClass(selectedClassId);
                }
            }
        } else {
            selectedClassId = -1;
            if ("class_subject".equals(selectedMethod)) {
                clearSubjects();
            } else if ("student_subject".equals(selectedMethod)) {
                clearStudents();
                clearSubjects();
            }
        }
    }

    private void handleSubjectSelection(int position) {
        if (position > 0 && position <= subjectList.size()) {
            // Adjust position since we have "Select Subject" at index 0
            int actualPosition = position - 1;
            if (actualPosition < subjectList.size()) {
                selectedSubjectId = subjectList.get(actualPosition).getId();
            }
        } else {
            selectedSubjectId = -1;
        }
    }

    private void handleStudentSelection(int position) {
        if (position > 0 && position <= studentList.size()) {
            // Adjust position since we have "Select Student" at index 0
            int actualPosition = position - 1;
            if (actualPosition < studentList.size()) {
                selectedStudentId = studentList.get(actualPosition).getStudentId();
            }
        } else {
            selectedStudentId = -1;
        }
    }

    private void loadInitialData() {
        showLoading("Loading classes...");
        classApiHelper.fetchAllClasses(this);
    }

    private void loadSubjectsForClass(int classId) {
        showLoading("Loading subjects...");
        subjectApiHelper.fetchSubjectsForClass(classId, this);
    }

    private void loadAllStudents() {
        showLoading("Loading students...");
        studentHelper.loadAllStudents();
    }

    // New method to load students filtered by class
    private void loadStudentsForClass(int classId) {
        showLoading("Loading students for class...");
        studentHelper.loadStudentsByClass(classId);
    }

    private void clearSubjects() {
        subjectList.clear();
        subjectNames.clear();
        subjectNames.add("Select Subject");
        subjectAdapter.notifyDataSetChanged();
        binding.edtSubject.setEnabled(false);
        binding.edtSubject.setText("", false);
        binding.edtSubject.setHint("Select Subject");
        selectedSubjectId = -1;
    }

    private void clearStudents() {
        studentList.clear();
        studentNames.clear();
        studentNames.add("Select Student");
        studentAdapter.notifyDataSetChanged();
        binding.edtStudents.setEnabled(false);
        binding.edtStudents.setText("", false);
        binding.edtStudents.setHint("Select Student");
        selectedStudentId = -1;
    }

    // Updated method to refresh class ArrayAdapter
    private void updateClassArrayAdapter() {
        runOnUiThread(() -> {
            classNames.clear();
            classNames.add("Select Class");

            for (ClassModel.Data classData : classList) {
                classNames.add(classData.getClassName());
            }

            classAdapter.notifyDataSetChanged();
        });
    }

    // Updated method to refresh subject ArrayAdapter
    private void updateSubjectArrayAdapter() {
        runOnUiThread(() -> {
            subjectNames.clear();
            subjectNames.add("Select Subject");

            for (SubjectModel subject : subjectList) {
                subjectNames.add(subject.getName());
            }

            subjectAdapter.notifyDataSetChanged();

            // Enable subject dropdown
            binding.edtSubject.setEnabled(true);
        });
    }

    // Updated method to refresh student ArrayAdapter
    private void updateStudentArrayAdapter() {
        runOnUiThread(() -> {
            studentNames.clear();
            studentNames.add("Select Student");

            for (StudentDetails student : studentList) {
                String displayName = student.getStudentName() + " (" + student.getStudentId() + ")";
                studentNames.add(displayName);
            }

            studentAdapter.notifyDataSetChanged();

            // Enable student dropdown
            binding.edtStudents.setEnabled(true);
        });
    }

    private void showLoading(String message) {
        runOnUiThread(() -> Toast.makeText(this, message, Toast.LENGTH_SHORT).show());
    }

    // ClassApiHelper.ClassListCallback implementation
    @Override
    public void onSuccess(ArrayList<ClassModel.Data> classList) {
        this.classList.clear();
        this.classList.addAll(classList);
        updateClassArrayAdapter();
        hideLoading();
    }

    @Override
    public void onError(String errorMessage) {
        hideLoading();
        runOnUiThread(() -> {
            Toast.makeText(this, "Class Loading Error: " + errorMessage, Toast.LENGTH_LONG).show();
        });
    }

    @Override
    public void onLoading(boolean isLoading) {
        if (isLoading) {
            showLoading("Loading...");
        } else {
            hideLoading();
        }
    }

    // SubjectApiHelper.SubjectListCallback implementation
    @Override
    public void onSubjectsSuccess(ArrayList<SubjectModel> subjectList) {
        this.subjectList.clear();
        this.subjectList.addAll(subjectList);
        updateSubjectArrayAdapter();
        hideLoading();
    }

    @Override
    public void onSubjectsError(String errorMessage) {
        hideLoading();
        runOnUiThread(() -> {
            Toast.makeText(this, "Subject Loading Error: " + errorMessage, Toast.LENGTH_LONG).show();
        });
    }

    @Override
    public void onSubjectsLoading(boolean isLoading) {
        if (isLoading) {
            showLoading("Loading subjects...");
        } else {
            hideLoading();
        }
    }

    // StudentHelper.StudentHelperCallback implementation
    @Override
    public void onLoadingStateChanged(boolean isLoading) {
        if (isLoading) {
            showLoading("Loading students...");
        } else {
            hideLoading();
        }
    }

    @Override
    public void onStudentsLoaded(List<StudentDetails> students, int totalCount) {
        this.studentList.clear();
        this.studentList.addAll(students);
        updateStudentArrayAdapter();
        hideLoading();
    }

    @Override
    public void onClassesLoaded(ArrayList<ClassModel.Data> classes) {
        // Handle if needed, but we're using ClassApiHelper primarily
    }

    private void hideLoading() {
        // Implement loading indicator hiding if you have one
    }

    private void hideResultCard() {
        // Your existing implementation for hiding result card
        View resultCard = findViewById(R.id.main);
        if (resultCard != null) {
            // Add your logic to hide the result card
        }
    }

    private void setupClickListeners() {
        binding.tvClassSubject.setOnClickListener(v -> handleMethodSelection("class_subject"));
        binding.tvStudentSubject.setOnClickListener(v -> handleMethodSelection("student_subject"));
        binding.tvDateRange.setOnClickListener(v -> handleMethodSelection("date_range"));
        binding.btnCreatePaper.setOnClickListener(v -> handleCreatePaper());
        binding.edtDate.setOnClickListener(v -> showDateRangePicker());
        binding.toolbar.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void handleMethodSelection(String method) {
        selectMethod(method);
        showFieldsForMethod(method);

        // Reset all selections and clear all fields when switching methods
        clearAllSelections();

        // Reset specific fields based on method
        switch (method) {
            case "class_subject":
                // For class_subject, enable class selection
                binding.edtClassesPaper.setEnabled(true);
                break;
            case "student_subject":
                // For student_subject, enable class selection first
                binding.edtClassesPaper.setEnabled(true);
                break;
            case "date_range":
                // For date_range, clear date fields as well
                binding.edtDate.setText("");
                binding.edtDate.setError(null);
                startDate = "";
                endDate = "";
                break;
        }
    }

    // New method to clear all selections and reset all fields
    private void clearAllSelections() {
        // Reset all selected IDs
        selectedClassId = -1;
        selectedSubjectId = -1;
        selectedStudentId = -1;

        // Reset all AutoCompleteTextViews
        resetAutoCompleteTextView(binding.edtClassesPaper);
        resetAutoCompleteTextView(binding.edtSubject);
        resetAutoCompleteTextView(binding.edtStudents);

        // Clear all data lists and reset adapters
        clearSubjects();
        clearStudents();

        // Reset class field to show "Select Class" and enable it
        binding.edtClassesPaper.setText("", false);
        binding.edtClassesPaper.setHint("Select Class");
        binding.edtClassesPaper.setEnabled(classNames.size() > 1); // Enable if classes are loaded
    }

    // Helper method to reset AutoCompleteTextView
    private void resetAutoCompleteTextView(View view) {
        if (view == null) return;

        try {
            if (view instanceof android.widget.AutoCompleteTextView) {
                android.widget.AutoCompleteTextView autoComplete = (android.widget.AutoCompleteTextView) view;
                autoComplete.setText("", false);
                autoComplete.clearFocus();
                autoComplete.dismissDropDown();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleCreatePaper() {
        if (validateFields()) {
            showResultCard();
            performSearch();
        }
    }

    private void selectMethod(String method) {
        selectedMethod = method;
        resetButtonStyles();

        switch (method) {
            case "class_subject":
                setSelectedButtonStyle(binding.tvClassSubject);
                break;
            case "student_subject":
                setSelectedButtonStyle(binding.tvStudentSubject);
                break;
            case "date_range":
                setSelectedButtonStyle(binding.tvDateRange);
                break;
        }
    }

    private void resetButtonStyles() {
        setDefaultButtonStyle(binding.tvClassSubject);
        setDefaultButtonStyle(binding.tvStudentSubject);
        setDefaultButtonStyle(binding.tvDateRange);
    }

    private void setSelectedButtonStyle(View button) {
        button.setBackground(ContextCompat.getDrawable(this, R.drawable.blue_bg));
        if (button instanceof android.widget.TextView) {
            ((android.widget.TextView) button).setTextColor(ContextCompat.getColor(this, R.color.white));
        }
    }

    private void setDefaultButtonStyle(View button) {
        button.setBackground(ContextCompat.getDrawable(this, R.drawable.text_view_bg));
        if (button instanceof android.widget.TextView) {
            ((android.widget.TextView) button).setTextColor(ContextCompat.getColor(this, R.color.black));
        }
    }

    private void showFieldsForMethod(String method) {
        // Hide all fields first
        binding.classRel.setVisibility(View.GONE);
        binding.subjectRel.setVisibility(View.GONE);
        binding.studentRel.setVisibility(View.GONE);
        binding.dateRel.setVisibility(View.GONE);

        // Show fields based on selected method
        switch (method) {
            case "class_subject":
                binding.classRel.setVisibility(View.VISIBLE);
                binding.subjectRel.setVisibility(View.VISIBLE);
                break;
            case "student_subject":
                // Show class, student, and subject fields for student_subject method
                binding.classRel.setVisibility(View.VISIBLE);
                binding.studentRel.setVisibility(View.VISIBLE);
                binding.subjectRel.setVisibility(View.VISIBLE);
                break;
            case "date_range":
                binding.dateRel.setVisibility(View.VISIBLE);
                break;
        }
    }

    private boolean validateFields() {
        switch (selectedMethod) {
            case "class_subject":
                return validateClassSubjectFields();
            case "student_subject":
                return validateStudentSubjectFields();
            case "date_range":
                return validateDateRangeFields();
            default:
                return false;
        }
    }

    private boolean validateClassSubjectFields() {
        if (selectedClassId <= 0) {
            Toast.makeText(this, "Please select a class", Toast.LENGTH_SHORT).show();
            binding.edtClassesPaper.requestFocus();
            return false;
        }

        if (selectedSubjectId <= 0) {
            Toast.makeText(this, "Please select a subject", Toast.LENGTH_SHORT).show();
            binding.edtSubject.requestFocus();
            return false;
        }

        return true;
    }

    private boolean validateStudentSubjectFields() {
        // For student_subject method, validate class, student, and subject
        if (selectedClassId <= 0) {
            Toast.makeText(this, "Please select a class", Toast.LENGTH_SHORT).show();
            binding.edtClassesPaper.requestFocus();
            return false;
        }

        if (selectedStudentId <= 0) {
            Toast.makeText(this, "Please select a student", Toast.LENGTH_SHORT).show();
            binding.edtStudents.requestFocus();
            return false;
        }

        if (selectedSubjectId <= 0) {
            Toast.makeText(this, "Please select a subject", Toast.LENGTH_SHORT).show();
            binding.edtSubject.requestFocus();
            return false;
        }

        return true;
    }

    private boolean validateDateRangeFields() {
        if (TextUtils.isEmpty(startDate) || TextUtils.isEmpty(endDate)) {
            binding.edtDate.setError("Please select date range");
            Toast.makeText(this, "Please select a date range", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void showDateRangePicker() {
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog startDatePicker = new DatePickerDialog(
                this,
                (view, year, monthOfYear, dayOfMonth) -> {
                    startDate = String.format("%02d/%02d/%d", dayOfMonth, monthOfYear + 1, year);
                    showEndDatePicker();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        startDatePicker.setTitle("Select Start Date");
        startDatePicker.show();
    }

    private void showEndDatePicker() {
        Calendar calendar = Calendar.getInstance();

        try {
            String[] startDateParts = startDate.split("/");
            if (startDateParts.length == 3) {
                Calendar minDate = Calendar.getInstance();
                minDate.set(Integer.parseInt(startDateParts[2]),
                        Integer.parseInt(startDateParts[1]) - 1,
                        Integer.parseInt(startDateParts[0]));

                DatePickerDialog endDatePicker = new DatePickerDialog(
                        this,
                        (view, year, monthOfYear, dayOfMonth) -> {
                            endDate = String.format("%02d/%02d/%d", dayOfMonth, monthOfYear + 1, year);
                            String dateRangeText = startDate + " - " + endDate;
                            binding.edtDate.setText(dateRangeText);
                            binding.edtDate.setError(null);
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                );

                endDatePicker.setTitle("Select End Date");
                endDatePicker.getDatePicker().setMinDate(minDate.getTimeInMillis());
                endDatePicker.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            showSimpleEndDatePicker();
        }
    }

    private void showSimpleEndDatePicker() {
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog endDatePicker = new DatePickerDialog(
                this,
                (view, year, monthOfYear, dayOfMonth) -> {
                    endDate = String.format("%02d/%02d/%d", dayOfMonth, monthOfYear + 1, year);
                    String dateRangeText = startDate + " - " + endDate;
                    binding.edtDate.setText(dateRangeText);
                    binding.edtDate.setError(null);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        endDatePicker.setTitle("Select End Date");
        endDatePicker.show();
    }

    private void showResultCard() {
        // Your existing implementation for showing result card
        View resultCard = findViewById(R.id.main);
        if (resultCard != null) {
            // Add your logic to show the result card
        }
    }

    private void performSearch() {
        Toast.makeText(this, "Searching for results...", Toast.LENGTH_SHORT).show();

        switch (selectedMethod) {
            case "class_subject":
                searchByClassAndSubject();
                break;
            case "student_subject":
                searchByStudentAndSubject();
                break;
            case "date_range":
                searchByDateRange();
                break;
        }
    }

    private void searchByClassAndSubject() {
        ClassModel.Data selectedClass = findClassById(selectedClassId);
        SubjectModel selectedSubject = findSubjectById(selectedSubjectId);

        String className = selectedClass != null ? selectedClass.getClassName() : "Unknown";
        String subjectName = selectedSubject != null ? selectedSubject.getName() : "Unknown";

        Toast.makeText(this, "Searching for Class: " + className + ", Subject: " + subjectName, Toast.LENGTH_LONG).show();

        // Get auth token
        String authToken = pref.getPrefString(this, pref.user_token);

        // Make API call using helper
        classTestResultApiHelper.getClassSubjectWise(authToken, selectedClassId, selectedSubjectId);
    }

    private void searchByStudentAndSubject() {
        ClassModel.Data selectedClass = findClassById(selectedClassId);
        StudentDetails selectedStudent = findStudentById(selectedStudentId);
        SubjectModel selectedSubject = findSubjectById(selectedSubjectId);

        String className = selectedClass != null ? selectedClass.getClassName() : "Unknown";
        String studentName = selectedStudent != null ? selectedStudent.getStudentName() : "Unknown";
        String subjectName = selectedSubject != null ? selectedSubject.getName() : "Unknown";

        Toast.makeText(this, "Searching for Class: " + className + ", Student: " + studentName + " (" + selectedStudentId + "), Subject: " + subjectName, Toast.LENGTH_LONG).show();

        // Get auth token
        String authToken = pref.getPrefString(this, pref.user_token);

        // Make API call using helper
        classTestResultApiHelper.getStudentSubjectWise(authToken, selectedStudentId, selectedSubjectId);
    }

    private void searchByDateRange() {
        Toast.makeText(this, "Searching from " + startDate + " to " + endDate, Toast.LENGTH_LONG).show();

        // Convert date format from DD/MM/YYYY to YYYY-MM-DD for API
        String formattedStartDate = convertDateFormat(startDate);
        String formattedEndDate = convertDateFormat(endDate);

        if (formattedStartDate == null || formattedEndDate == null) {
            Toast.makeText(this, "Invalid date format", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get auth token
        String authToken = pref.getPrefString(this, pref.user_token);

        // Make API call using helper
        classTestResultApiHelper.getDateRangeWise(authToken, formattedStartDate, formattedEndDate);
    }

    // 5. Add utility method for date conversion
    private String convertDateFormat(String dateStr) {
        try {
            // Convert from DD/MM/YYYY to YYYY-MM-DD
            String[] parts = dateStr.split("/");
            if (parts.length == 3) {
                String day = parts[0];
                String month = parts[1];
                String year = parts[2];

                // Ensure two digits for day and month
                if (day.length() == 1) day = "0" + day;
                if (month.length() == 1) month = "0" + month;

                return year + "-" + month + "-" + day;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private ClassModel.Data findClassById(int classId) {
        for (ClassModel.Data classData : classList) {
            if (classData.getClassId() == classId) {
                return classData;
            }
        }
        return null;
    }

    private SubjectModel findSubjectById(int subjectId) {
        for (SubjectModel subject : subjectList) {
            if (subject.getId() == subjectId) {
                return subject;
            }
        }
        return null;
    }

    private StudentDetails findStudentById(int studentId) {
        for (StudentDetails student : studentList) {
            if (student.getStudentId() == studentId) {
                return student;
            }
        }
        return null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
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

    // Method 2: Handle export functionality
    private void handleExport(ArrayList<ArrayList<String>> tableData, String action) {
        if (tableData.size() <= 1) { // Only headers, no data
            Toast.makeText(this, "No data to export", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Create DataExportHelper instance with context
            DataExportHelper exportHelper = new DataExportHelper(this);

            // Generate dynamic filename based on selected method and parameters
            String fileName = generateDynamicFileName();

            // Use the exportData method with dynamic filename
            exportHelper.exportData(tableData, action, fileName);

        } catch (Exception e) {
            Log.e(TAG, "Export error: " + e.getMessage());
            Toast.makeText(this, "Export failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    // Method 3: Generate dynamic filename based on search method
    private String generateDynamicFileName() {
        String fileName = "class_test_results";

        switch (selectedMethod) {
            case "class_subject":
                ClassModel.Data selectedClass = findClassById(selectedClassId);
                SubjectModel selectedSubject = findSubjectById(selectedSubjectId);

                String className = selectedClass != null ? selectedClass.getClassName().replaceAll("\\s+", "_") : "unknown";
                String subjectName = selectedSubject != null ? selectedSubject.getName().replaceAll("\\s+", "_") : "unknown";

                fileName = "class_test_results_" + className + "_" + subjectName;
                break;

            case "student_subject":
                StudentDetails selectedStudent = findStudentById(selectedStudentId);
                SubjectModel selectedSubjectForStudent = findSubjectById(selectedSubjectId);

                String studentName = selectedStudent != null ?
                        selectedStudent.getStudentName().replaceAll("\\s+", "_") : "unknown";
                String subjectNameForStudent = selectedSubjectForStudent != null ?
                        selectedSubjectForStudent.getName().replaceAll("\\s+", "_") : "unknown";

                fileName = "class_test_results_" + studentName + "_" + subjectNameForStudent;
                break;

            case "date_range":
                if (!TextUtils.isEmpty(startDate) && !TextUtils.isEmpty(endDate)) {
                    try {
                        // Convert dates to a more readable format for filename
                        SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                        SimpleDateFormat fileFormat = new SimpleDateFormat("yyyy_MM_dd", Locale.getDefault());

                        Date start = inputFormat.parse(startDate);
                        Date end = inputFormat.parse(endDate);

                        if (start != null && end != null) {
                            fileName = "class_test_results_" + fileFormat.format(start) + "_to_" + fileFormat.format(end);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error formatting filename dates: " + e.getMessage());
                        fileName = "class_test_results_date_range";
                    }
                }
                break;
        }

        return fileName;
    }

    // Method 4: Prepare table data for export
    private ArrayList<ArrayList<String>> prepareTableData() {
        ArrayList<ArrayList<String>> tableData = new ArrayList<>();

        // Add header row for class test results
        ArrayList<String> headers = new ArrayList<>();
        headers.add("Sr");
        headers.add("Test Date");
        headers.add("Student ID");
        headers.add("Student Name");
        headers.add("Class");
        headers.add("Subject");
        headers.add("Total Marks");
        headers.add("Obtained Marks");
        headers.add("Percentage");
        tableData.add(headers);
        ClassModel.Data className=findClassById(selectedClassId);

        // Add data rows from your testResultsList
        for (int i = 0; i < testResultsList.size(); i++) {
            ClassTestResult.Datum testResult = testResultsList.get(i);
            ArrayList<String> row = new ArrayList<>();

            // Sr No
            row.add(String.valueOf(i + 1));

            // Test Date
            row.add(testResult.getDate() != null ? testResult.getDate() : "");

            // Student ID
            row.add(testResult.getStudentId() != -1 ? String.valueOf(testResult.getStudentId()) : "");

            // Student Name
            row.add(testResult.getStudentName() != null ? testResult.getStudentName() : "");
            assert className != null;
            row.add(className.className);

            // Subject
            row.add(testResult.getSubject() != null ? testResult.getSubject() : "");

            // Total Marks
            row.add(testResult.getTotalMarks() != -1 ? String.valueOf(testResult.getTotalMarks()) : "");

            // Obtained Marks
            row.add(testResult.getObtainedMarks() != -1 ? String.valueOf(testResult.getObtainedMarks()) : "");

            // Percentage
            row.add(testResult.getPercentage() != -1 ? String.valueOf(testResult.getPercentage()) + "%" : "");


            tableData.add(row);
        }

        return tableData;
    }

    // Additional helper method to update current date range (call this in your display methods)
    private void updateCurrentDateRange() {
        if ("date_range".equals(selectedMethod)) {
            currentStartDate = convertDateFormat(startDate);
            currentEndDate = convertDateFormat(endDate);
        }
    }
}