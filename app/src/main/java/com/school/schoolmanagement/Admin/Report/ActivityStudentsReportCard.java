package com.school.schoolmanagement.Admin.Report;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.school.schoolmanagement.Admin.Model.ClassModel;
import com.school.schoolmanagement.Admin.Model.StudentId;
import com.school.schoolmanagement.GlobalViewModel.ViewModel;
import com.school.schoolmanagement.HelperClasses.ClassApiHelper;
import com.school.schoolmanagement.HelperClasses.StudentHelper;
import com.school.schoolmanagement.Model.StudentDetails;
import com.school.schoolmanagement.R;
import com.school.schoolmanagement.Utils.Utility;
import com.school.schoolmanagement.databinding.ActivityStudentsReportCardBinding;

import java.util.ArrayList;
import java.util.List;

public class ActivityStudentsReportCard extends Utility implements
        ClassApiHelper.ClassListCallback,
        StudentHelper.StudentHelperCallback {

    private ActivityStudentsReportCardBinding binding;

    // Helper classes
    private ClassApiHelper classApiHelper;
    private StudentHelper studentHelper;
    private ViewModel viewModel;

    // Data lists
    private final ArrayList<ClassModel.Data> classList = new ArrayList<>();
    private final List<StudentDetails> studentList = new ArrayList<>();

    // String lists for adapters
    private final ArrayList<String> classNames = new ArrayList<>();
    private ArrayAdapter<String> classAdapter;
    private int selectedClassId = -1;
    private final ArrayList<String> studentNames = new ArrayList<>();

    // ArrayAdapter instances

    private ArrayAdapter<String> studentAdapter;

    // Selected IDs

    private int selectedStudentId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityStudentsReportCardBinding.inflate(getLayoutInflater());
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

        String authToken = "Bearer " + pref.getPrefString(this, pref.user_token);
        studentHelper = new StudentHelper(this, viewModel, authToken, this, this);
    }

    private void setupUI() {
        setupAutoCompleteTextViews();
        setupClickListeners();
    }

    private void setupAutoCompleteTextViews() {
        setupClassAutoComplete();
        setupStudentAutoComplete();
    }

    private void setupClassAutoComplete() {
        // Initialize with empty list first
        classNames.clear();
        classNames.add("Select Class");

        classAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, classNames);
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

    private void setupStudentAutoComplete() {
        studentNames.clear();
        studentNames.add("Select Student");

        studentAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, studentNames);
        studentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.edtStudent.setAdapter(studentAdapter);

        // Set threshold to show dropdown on click
        binding.edtStudent.setThreshold(1);
        binding.edtStudent.setHint("Select Student");

        // Initially disable student dropdown
        binding.edtStudent.setEnabled(false);

        binding.edtStudent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                handleStudentSelection(position);
            }
        });

        // Handle focus change to show dropdown
        binding.edtStudent.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus && binding.edtStudent.isEnabled()) {
                binding.edtStudent.showDropDown();
            }
        });

        // Handle click to show dropdown
        binding.edtStudent.setOnClickListener(v -> {
            if (binding.edtStudent.isEnabled()) {
                binding.edtStudent.showDropDown();
            }
        });
    }

    private void handleClassSelection(int position) {
        if (position > 0 && position <= classList.size()) {
            // Adjust position since we have "Select Class" at index 0
            int actualPosition = position - 1;
            if (actualPosition < classList.size()) {
                selectedClassId = classList.get(actualPosition).getClassId();

                // Load students for selected class
                loadStudentsForClass(selectedClassId);

                // Clear previous student selection
                clearStudentSelection();
            }
        } else {
            selectedClassId = -1;
            clearStudents();
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

    private void clearStudentSelection() {
        selectedStudentId = -1;
        binding.edtStudent.setText("", false);
        binding.edtStudent.setHint("Select Student");
    }

    private void clearStudents() {
        studentList.clear();
        studentNames.clear();
        studentNames.add("Select Student");
        studentAdapter.notifyDataSetChanged();
        binding.edtStudent.setEnabled(false);
        binding.edtStudent.setText("", false);
        binding.edtStudent.setHint("Select Student");
        selectedStudentId = -1;
    }

    private void loadInitialData() {
        showLoading("Loading classes...");
        classApiHelper.fetchAllClasses(this);
    }

    private void loadStudentsForClass(int classId) {
        showLoading("Loading students for class...");
        studentHelper.loadStudentsByClass(classId);
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

    // Updated method to refresh student ArrayAdapter
    private void updateStudentArrayAdapter() {
        runOnUiThread(() -> {
            studentNames.clear();
            studentNames.add("Select Student");

            for (StudentDetails student : studentList) {
                String displayName = student.getStudentName() + " (ID: " + student.getStudentId() + ")";
                studentNames.add(displayName);
            }

            studentAdapter.notifyDataSetChanged();

            // Enable student dropdown
            binding.edtStudent.setEnabled(true);
        });
    }

    private void setupClickListeners() {
        // Back button click listener
        binding.toolbar.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Generate button click listener
        binding.btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateFields()) {
                    generateReportCard();
                }
            }
        });
    }

    private boolean validateFields() {
        // Validate class selection
        if (selectedClassId <= 0) {
            Toast.makeText(this, "Please select a class", Toast.LENGTH_SHORT).show();
            binding.edtClass.requestFocus();
            return false;
        }

        // Validate student selection
        if (selectedStudentId <= 0) {
            Toast.makeText(this, "Please select a student", Toast.LENGTH_SHORT).show();
            binding.edtStudent.requestFocus();
            return false;
        }

        return true;
    }

    private void generateReportCard() {
        showLoading("Loading...");
        String auth = "Bearer "+pref.getPrefString(this,pref.user_token);
        StudentId studentId= new StudentId();
        studentId.setStudentId(selectedStudentId);
        viewModel.generatePdfResult(auth,studentId).observe(this,response -> {
            StudentDetails selectedStudent = findStudentById(selectedStudentId);
            ClassModel.Data selectedClass = findClassById(selectedClassId);

            if (selectedStudent != null && selectedClass != null) {
                hideLoading();
                Toast.makeText(this, response.message + selectedStudent.getStudentName(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, ActivityReportCardDisplay.class);
                intent.putExtra("STUDENT_ID", selectedStudentId);
                startActivity(intent);
            } else {
                hideLoading();
                Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show();
            }
        });
        // Find selected student details

    }

    private void showLoading(String message) {
        binding.loader.rlLoader.setVisibility(View.VISIBLE);
        runOnUiThread(() -> Toast.makeText(this, message, Toast.LENGTH_SHORT).show());
    }

    private void hideLoading() {
        // Implement loading indicator hiding if you have one
        // You can add a progress bar or loading overlay here
        binding.loader.rlLoader.setVisibility(View.GONE);
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

        Toast.makeText(this, "Loaded " + students.size() + " students", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClassesLoaded(ArrayList<ClassModel.Data> classes) {
        // Handle if needed, but we're using ClassApiHelper primarily
    }

    // Utility methods to find objects by ID
    private ClassModel.Data findClassById(int classId) {
        for (ClassModel.Data classData : classList) {
            if (classData.getClassId() == classId) {
                return classData;
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
}