package com.school.schoolmanagement.Admin.Exams.AddUpdateMarks;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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

import com.school.schoolmanagement.Admin.Exams.UpdateInfo.ActivityInsertMarks;
import com.school.schoolmanagement.Admin.Model.ClassModel;
import com.school.schoolmanagement.HelperClasses.ClassApiHelper;
import com.school.schoolmanagement.HelperClasses.StudentHelper;
import com.school.schoolmanagement.GlobalViewModel.ViewModel;
import com.school.schoolmanagement.Model.StudentDetails;
import com.school.schoolmanagement.R;
import com.school.schoolmanagement.Utils.Utility;
import com.school.schoolmanagement.databinding.ActivitySelectClassorStudentBinding;

import java.util.ArrayList;
import java.util.List;

public class ActivitySelectClassorStudent extends Utility implements StudentHelper.StudentHelperCallback {
    private static final String TAG = "ActivitySelectClass";
    private static final int INVALID_ID = -1;

    private ActivitySelectClassorStudentBinding binding;
    private boolean isWholeClassMode = true;
    private ClassApiHelper classApiHelper;
    private StudentHelper studentHelper;
    private ViewModel viewModel;

    // Class related
    private ArrayList<ClassModel.Data> classList;
    private ArrayAdapter<String> classAdapter;

    // Student related
    private List<StudentDetails> studentsList;
    private ArrayAdapter<String> studentsAdapter;

    // Selection variables
    private int selectedExamId = INVALID_ID;
    private int selectedClassId = INVALID_ID;
    private int selectedStudentId = INVALID_ID;
    private String selectedClassName = "";
    private String selectedStudentName = "";
    private int selectedRegistrationNumber = INVALID_ID;

    // Loading states
    private boolean isClassesLoaded = false;
    private boolean isStudentsLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivitySelectClassorStudentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupWindowInsets();

        if (!getDataFromIntent()) {
            return; // Exit if invalid exam ID
        }

        binding.toolbar.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        initializeComponents();
        setupUI();
        loadClasses();
    }

    private void setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private boolean getDataFromIntent() {
        selectedExamId = getIntent().getIntExtra("exam_id", INVALID_ID);
        Log.d(TAG, "Intent Exam ID: " + selectedExamId);

        if (selectedExamId == INVALID_ID) {
            showToast("❌ Invalid exam data. Please try again.");
            finish();
            return false;
        }
        return true;
    }

    private void initializeComponents() {
        classApiHelper = new ClassApiHelper(this);
        classList = new ArrayList<>();
        studentsList = new ArrayList<>();

        // Initialize ViewModel and StudentHelper
        viewModel = new ViewModelProvider(this).get(ViewModel.class);
        String authToken = getAuthToken();

        // Add debugging
        Log.d(TAG, "Auth token: " + (authToken != null ? "***" + authToken.substring(Math.max(0, authToken.length() - 4)) : "NULL"));
        Log.d(TAG, "Auth token length: " + (authToken != null ? authToken.length() : 0));

        if (authToken == null || authToken.isEmpty() || authToken.equals("your_auth_token_here")) {
            Log.e(TAG, "Invalid auth token detected!");
            showToast("❌ Authentication error. Please login again.");
            // Optionally redirect to login
            return;
        }

        studentHelper = new StudentHelper(this, viewModel, authToken, this, this);
    }

    private void setupUI() {
        setupTabSelection();
        setupClassDropdown();
        setupStudentDropdown();
        setupSearchButton();
    }

    private void setupTabSelection() {
        setModeWholeClass();
        binding.tabWholeClass.setOnClickListener(v -> setModeWholeClass());
        binding.tabSingleStudent.setOnClickListener(v -> setModeSingleStudent());
    }

    private void setupClassDropdown() {
        // Initialize with loading state
        ArrayList<String> initialList = new ArrayList<>();
        initialList.add("Loading classes...");

        classAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, initialList);
        classAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.selectClassDropdown.setAdapter(classAdapter);
        binding.selectClassDropdown.setThreshold(0);
        binding.selectClassDropdown.setKeyListener(null);
        binding.selectClassDropdown.setFocusable(false);
        binding.selectClassDropdown.setEnabled(false);

        binding.selectClassDropdown.setOnClickListener(v -> {
            if (!isClassesLoaded) {
                showToast("⏳ Please wait, classes are still loading...");
                return;
            }
            if (classList != null && !classList.isEmpty()) {
                binding.selectClassDropdown.showDropDown();
            } else {
                showToast("❌ No classes available. Please refresh.");
            }
        });

        binding.selectClassDropdown.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                handleClassSelection(position);
            }
        });

        binding.selectClassDropdown.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus && isClassesLoaded && classList != null && !classList.isEmpty()) {
                binding.selectClassDropdown.showDropDown();
            }
        });
    }

    private void setupStudentDropdown() {
        // Initialize student dropdown
        ArrayList<String> initialStudentList = new ArrayList<>();
        initialStudentList.add("Select class first...");

        studentsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, initialStudentList);
        studentsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.edtRegistrationNumber.setAdapter(studentsAdapter);
        binding.edtRegistrationNumber.setThreshold(0);
        binding.edtRegistrationNumber.setKeyListener(null);
        binding.edtRegistrationNumber.setFocusable(false);
        binding.edtRegistrationNumber.setEnabled(false);

        binding.edtRegistrationNumber.setOnClickListener(v -> {
            if (selectedClassId == INVALID_ID) {
                showToast("📚 Please select a class first");
                return;
            }
            if (!isStudentsLoaded) {
                showToast("⏳ Please wait, students are still loading...");
                return;
            }
            if (studentsList != null && !studentsList.isEmpty()) {
                binding.edtRegistrationNumber.showDropDown();
            } else {
                showToast("❌ No students available for selected class.");
            }
        });

        binding.edtRegistrationNumber.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                handleStudentSelection(position);
            }
        });

        binding.edtRegistrationNumber.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus && isStudentsLoaded && studentsList != null && !studentsList.isEmpty()) {
                binding.edtRegistrationNumber.showDropDown();
            }
        });
    }

    private void handleClassSelection(int position) {
        if (classList == null || classList.isEmpty()) {
            showToast("❌ No classes available");
            return;
        }

        if (position == 0) {
            // "Select Class" option selected
            resetClassSelection();
        } else {
            // Actual class selected
            try {
                selectedClassId = classApiHelper.getClassIdByPosition(classList, position);
                selectedClassName = classList.get(position - 1).getClassName();

                showToast("✅ Selected: " + selectedClassName);
                Log.d(TAG, "Class selected - ID: " + selectedClassId + ", Name: " + selectedClassName);

                // Load students for selected class in single student mode
                if (!isWholeClassMode) {
                    loadStudentsForSelectedClass();
                }
            } catch (Exception e) {
                Log.e(TAG, "Error selecting class: " + e.getMessage());
                showToast("❌ Error selecting class. Please try again.");
                resetClassSelection();
            }
        }

        binding.selectClassDropdown.dismissDropDown();
    }

    private void handleStudentSelection(int position) {
        if (studentsList == null || studentsList.isEmpty()) {
            showToast("❌ No students available");
            return;
        }

        if (position == 0) {
            // "Select Student" option selected
            resetStudentSelection();
        } else {
            // Actual student selected
            try {
                StudentDetails selectedStudent = studentsList.get(position - 1);
                selectedStudentId = selectedStudent.getStudentId();
                selectedStudentName = selectedStudent.getStudentName();
                selectedRegistrationNumber = selectedStudent.getStudentId();

                showToast("✅ Selected: " + selectedStudentName + " (Reg: " + selectedRegistrationNumber + ")");
                Log.d(TAG, "Student selected - ID: " + selectedStudentId +
                        ", Name: " + selectedStudentName + ", RegNo: " + selectedRegistrationNumber);

            } catch (Exception e) {
                Log.e(TAG, "Error selecting student: " + e.getMessage());
                showToast("❌ Error selecting student. Please try again.");
                resetStudentSelection();
            }
        }

        binding.edtRegistrationNumber.dismissDropDown();
    }

    private void loadStudentsForSelectedClass() {
        if (selectedClassId == INVALID_ID) {
            return;
        }

        isStudentsLoaded = false;
        resetStudentSelection();

        // Update dropdown to show loading state
        studentsAdapter.clear();
        studentsAdapter.add("⏳ Loading students...");
        studentsAdapter.notifyDataSetChanged();
        binding.edtRegistrationNumber.setText("⏳ Loading students...", false);
        binding.edtRegistrationNumber.setEnabled(false);

        // Load students using StudentHelper
        studentHelper.loadStudentsByClass(selectedClassId);
    }

    private void setupSearchButton() {
        binding.btnSearch.setOnClickListener(v -> {
            if (isWholeClassMode) {
                handleWholeClassSearch();
            } else {
                handleSingleStudentSearch();
            }
        });
    }

    private boolean validateExamId() {
        if (selectedExamId == INVALID_ID) {
            showToast("❌ Invalid exam selected. Please restart the process.");
            return false;
        }
        return true;
    }

    private boolean validateClassSelection() {
        if (selectedClassId == INVALID_ID) {
            showToast("📚 Please select a class first");
            // Focus on class dropdown
            binding.selectClassDropdown.requestFocus();
            if (isClassesLoaded) {
                binding.selectClassDropdown.showDropDown();
            }
            return false;
        }
        return true;
    }

    private boolean validateStudentSelection() {
        if (selectedStudentId == INVALID_ID || selectedRegistrationNumber == INVALID_ID) {
            showToast("👤 Please select a student first");
            // Focus on student dropdown
            binding.edtRegistrationNumber.requestFocus();
            if (isStudentsLoaded) {
                binding.edtRegistrationNumber.showDropDown();
            }
            return false;
        }
        return true;
    }

    private void handleWholeClassSearch() {
        // Validate all required fields
        if (!validateExamId()) return;
        if (!validateClassSelection()) return;

        showToast("🔍 Loading students for " + selectedClassName + "...");
        loadStudentsForClass(selectedClassId);
    }

    private void handleSingleStudentSearch() {
        // Validate all required fields
        if (!validateExamId()) return;
        if (!validateClassSelection()) return;
        if (!validateStudentSelection()) return;

        showToast("🔍 Opening marks entry for " + selectedStudentName + "...");
        searchStudentByRegistration(selectedRegistrationNumber, selectedClassId);
    }

    // StudentHelper.StudentHelperCallback implementations
    @Override
    public void onLoadingStateChanged(boolean isLoading) {
        // Handle loading state changes if needed
    }

    @Override
    public void onStudentsLoaded(List<StudentDetails> students, int totalCount) {
        runOnUiThread(() -> {
            try {
                studentsList = new ArrayList<>(students);
                updateStudentDropdown(students);
                isStudentsLoaded = true;

                showToast("✅ " + students.size() + " students loaded for " + selectedClassName);
                Log.d(TAG, "Students loaded successfully: " + students.size());

            } catch (Exception e) {
                Log.e(TAG, "Error updating student dropdown: " + e.getMessage());
                showToast("❌ Error loading students. Please try again.");
            }
        });
    }

    @Override
    public void onError(String errorMessage) {
        runOnUiThread(() -> {
            isStudentsLoaded = false;
            Log.e(TAG, "Error loading students: " + errorMessage);

            showToast("❌ Failed to load students: " + errorMessage);

            studentsAdapter.clear();
            studentsAdapter.add("❌ Failed to load students - Tap to retry");
            studentsAdapter.notifyDataSetChanged();

            binding.edtRegistrationNumber.setEnabled(true);
            binding.edtRegistrationNumber.setText("❌ Failed to load students", false);
            binding.edtRegistrationNumber.setOnClickListener(v -> {
                showToast("🔄 Retrying to load students...");
                loadStudentsForSelectedClass();
            });
        });
    }

    @Override
    public void onClassesLoaded(ArrayList<ClassModel.Data> classes) {
        // This callback is not used in this context since we're using ClassApiHelper for classes
    }

    private void updateStudentDropdown(List<StudentDetails> students) {
        ArrayList<String> studentDisplayList = new ArrayList<>();
        studentDisplayList.add("📋 Select Student");

        for (StudentDetails student : students) {
            String displayText = student.getStudentName() + " (Reg: " + student.getStudentId() + ")";
            studentDisplayList.add(displayText);
        }

        studentsAdapter.clear();
        studentsAdapter.addAll(studentDisplayList);
        studentsAdapter.notifyDataSetChanged();

        binding.edtRegistrationNumber.setEnabled(true);
        binding.edtRegistrationNumber.setHint("📋 Select Student");
        binding.edtRegistrationNumber.setText("", false);
    }

    private void loadClasses() {
        isClassesLoaded = false;

        classApiHelper.fetchAllClasses(new ClassApiHelper.ClassListCallback() {
            @Override
            public void onSuccess(ArrayList<ClassModel.Data> classList) {
                ActivitySelectClassorStudent.this.classList = classList;

                if (classList == null || classList.isEmpty()) {
                    handleEmptyClassList();
                    return;
                }

                updateClassDropdown(classList);
                isClassesLoaded = true;
                showToast("✅ " + classList.size() + " classes loaded successfully");
                Log.d(TAG, "Classes loaded successfully: " + classList.size());
            }

            @Override
            public void onError(String errorMessage) {
                isClassesLoaded = false;
                Log.e(TAG, "Error loading classes: " + errorMessage);

                showToast("❌ Failed to load classes. Please check your connection and try again.");

                classAdapter.clear();
                classAdapter.add("❌ Failed to load classes - Tap to retry");
                classAdapter.notifyDataSetChanged();

                binding.selectClassDropdown.setEnabled(true);
                binding.selectClassDropdown.setText("❌ Failed to load classes", false);
                binding.selectClassDropdown.setOnClickListener(v -> {
                    showToast("🔄 Retrying to load classes...");
                    loadClasses();
                });
            }

            @Override
            public void onLoading(boolean isLoading) {
                if (isLoading) {
                    binding.selectClassDropdown.setEnabled(false);
                    classAdapter.clear();
                    classAdapter.add("⏳ Loading classes...");
                    classAdapter.notifyDataSetChanged();
                    binding.selectClassDropdown.setText("⏳ Loading classes...", false);
                }
            }
        });
    }

    private void handleEmptyClassList() {
        isClassesLoaded = false;
        showToast("⚠️ No classes found. Please contact administrator.");

        classAdapter.clear();
        classAdapter.add("⚠️ No classes available");
        classAdapter.notifyDataSetChanged();

        binding.selectClassDropdown.setEnabled(false);
        binding.selectClassDropdown.setText("⚠️ No classes available", false);
    }

    private void updateClassDropdown(ArrayList<ClassModel.Data> classList) {
        ArrayList<String> classNames = classApiHelper.getClassNames(classList);

        classAdapter.clear();
        classAdapter.addAll(classNames);
        classAdapter.notifyDataSetChanged();

        binding.selectClassDropdown.setEnabled(true);
        binding.selectClassDropdown.setHint("📚 Select Class");
        binding.selectClassDropdown.setText("", false);
    }

    private void loadStudentsForClass(int classId) {
        try {
            Intent classIntent = new Intent(this, ActivityInsertMarks.class);
            classIntent.putExtra("examId", selectedExamId);
            classIntent.putExtra("classId", classId);

            Log.d(TAG, "Navigating to ActivityInsertMarks - ExamId: " + selectedExamId + ", ClassId: " + classId);
            startActivity(classIntent);

        } catch (Exception e) {
            Log.e(TAG, "Error navigating to ActivityInsertMarks: " + e.getMessage());
            showToast("❌ Error opening marks entry. Please try again.");
        }
    }

    private void searchStudentByRegistration(int registrationNumber, int classId) {
        try {
            Intent studentIntent = new Intent(this, ActivityInsertMarks.class);
            studentIntent.putExtra("examId", selectedExamId);
            studentIntent.putExtra("classId", classId);
            studentIntent.putExtra("registrationNumber", registrationNumber);
            studentIntent.putExtra("studentId", selectedStudentId);

            Log.d(TAG, "Navigating to ActivityInsertMarks - ExamId: " + selectedExamId +
                    ", ClassId: " + classId + ", RegNo: " + registrationNumber + ", StudentId: " + selectedStudentId);
            startActivity(studentIntent);

        } catch (Exception e) {
            Log.e(TAG, "Error navigating to ActivityInsertMarks: " + e.getMessage());
            showToast("❌ Error opening student marks entry. Please try again.");
        }
    }

    private void setModeWholeClass() {
        isWholeClassMode = true;

        // Update UI visibility
        binding.selectClassContainer.setVisibility(View.VISIBLE);
        binding.registrationLayout.setVisibility(View.GONE);

        // Update tab styling
        binding.tabWholeClass.setBackground(getResources().getDrawable(R.drawable.ornage_blue_btn));
        binding.tabWholeClass.setTextColor(getResources().getColor(R.color.white));
        binding.tabSingleStudent.setBackground(null);
        binding.tabSingleStudent.setTextColor(getResources().getColor(R.color.grey));

        // Update title
        binding.mainTitle.setText("📚 Add Marks for Entire Class");

        resetFormData();
        showToast("📚 Switched to Whole Class mode");
    }

    private void setModeSingleStudent() {
        isWholeClassMode = false;

        // Update UI visibility
        binding.registrationLayout.setVisibility(View.VISIBLE);

        // Update tab styling
        binding.tabSingleStudent.setBackground(getResources().getDrawable(R.drawable.ornage_blue_btn));
        binding.tabSingleStudent.setTextColor(getResources().getColor(R.color.white));
        binding.tabWholeClass.setBackground(null);
        binding.tabWholeClass.setTextColor(getResources().getColor(R.color.grey));

        // Update title
        binding.mainTitle.setText("👤 Add Marks for Single Student");

        resetFormData();
        showToast("👤 Switched to Single Student mode");
    }

    private void resetFormData() {
        resetClassSelection();
        resetStudentSelection();
    }

    private void resetClassSelection() {
        selectedClassId = INVALID_ID;
        selectedClassName = "";

        if (classAdapter != null && isClassesLoaded) {
            binding.selectClassDropdown.setText("", false);
            binding.selectClassDropdown.setHint("📚 Select Class");
        }

        // Also reset student selection when class changes
        resetStudentSelection();
    }

    private void resetStudentSelection() {
        selectedStudentId = INVALID_ID;
        selectedStudentName = "";
        selectedRegistrationNumber = INVALID_ID;
        isStudentsLoaded = false;

        if (studentsAdapter != null) {
            studentsAdapter.clear();
            studentsAdapter.add("Select class first...");
            studentsAdapter.notifyDataSetChanged();
            binding.edtRegistrationNumber.setText("", false);
            binding.edtRegistrationNumber.setHint("📋 Select Student");
            binding.edtRegistrationNumber.setEnabled(false);
        }
    }

    private void showToast(String message) {
        if (!isFinishing() && !isDestroyed()) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }

    // TODO: Implement this method to get your authentication token
    private String getAuthToken() {
        return "Bearer "+pref.getPrefString(this,pref.user_token);
    }

    // Getter methods
    public int getSelectedClassId() {
        return selectedClassId;
    }

    public String getSelectedClassName() {
        return selectedClassName;
    }

    public boolean isWholeClassMode() {
        return isWholeClassMode;
    }

    public int getSelectedExamId() {
        return selectedExamId;
    }

    public int getRegistrationNumber() {
        return selectedRegistrationNumber;
    }

    public int getSelectedStudentId() {
        return selectedStudentId;
    }

    public String getSelectedStudentName() {
        return selectedStudentName;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (binding != null) {
            binding = null;
        }

        // Clean up references
        classList = null;
        classAdapter = null;
        classApiHelper = null;
        studentsList = null;
        studentsAdapter = null;
        studentHelper = null;
        viewModel = null;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        showToast("📝 Marks entry cancelled");
    }
}