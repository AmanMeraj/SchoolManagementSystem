package com.school.schoolmanagement.Admin.Certificate;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.school.schoolmanagement.Admin.Model.ClassModel;
import com.school.schoolmanagement.HelperClasses.ClassApiHelper;
import com.school.schoolmanagement.HelperClasses.StudentHelper;
import com.school.schoolmanagement.GlobalViewModel.ViewModel;
import com.school.schoolmanagement.Model.StudentDetails;
import com.school.schoolmanagement.R;
import com.school.schoolmanagement.Utils.Utility;
import com.school.schoolmanagement.databinding.ActivityCertificateDataSelectionBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ActivityCertificateDataSelection extends Utility implements StudentHelper.StudentHelperCallback {
    private static final String TAG = "CertificateDataSelection";
    private static final int INVALID_ID = -1;

    private ActivityCertificateDataSelectionBinding binding;
    private ClassApiHelper classApiHelper;
    private StudentHelper studentHelper;
    private ViewModel viewModel;
    private Calendar calendar;
    private SimpleDateFormat dateFormat;

    // Data from previous activity
    private String fromActivity = "";

    // Class related
    private ArrayList<ClassModel.Data> classList;
    private ArrayAdapter<String> classAdapter;

    // Student related
    private List<StudentDetails> studentsList;
    private ArrayAdapter<String> studentsAdapter;

    // Selection variables
    private int selectedClassId = INVALID_ID;
    private int selectedStudentId = INVALID_ID;
    private String selectedClassName = "";
    private String selectedStudentName = "";
    private String selectedDate = "";

    // Loading states
    private boolean isClassesLoaded = false;
    private boolean isStudentsLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityCertificateDataSelectionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupWindowInsets();

        if (!getDataFromIntent()) {
            return; // Exit if invalid data
        }

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
        fromActivity = getIntent().getStringExtra("from");
        Log.d(TAG, "Intent From: " + fromActivity);

        if (TextUtils.isEmpty(fromActivity)) {
            showToast("❌ Invalid request source. Please try again.");
            finish();
            return false;
        }
        return true;
    }

    private void initializeComponents() {
        classApiHelper = new ClassApiHelper(this);
        classList = new ArrayList<>();
        studentsList = new ArrayList<>();
        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        // Initialize ViewModel and StudentHelper
        viewModel = new ViewModelProvider(this).get(ViewModel.class);
        String authToken = getAuthToken();

        // Add debugging
        Log.d(TAG, "Auth token: " + (authToken != null ? "***" + authToken.substring(Math.max(0, authToken.length() - 4)) : "NULL"));
        Log.d(TAG, "Auth token length: " + (authToken != null ? authToken.length() : 0));

        if (authToken == null || authToken.isEmpty() || authToken.equals("your_auth_token_here")) {
            Log.e(TAG, "Invalid auth token detected!");
            showToast("❌ Authentication error. Please login again.");
            return;
        }

        studentHelper = new StudentHelper(this, viewModel, authToken, this, this);
    }

    private void setupUI() {
        setupToolbar();
        setupDatePicker();
        setupClassDropdown();
        setupStudentDropdown();
        setupCreateButton();
    }

    private void setupToolbar() {
        binding.toolbar.backBtn.setOnClickListener(v -> finish());
    }

    private void setupDatePicker() {
        // Set click listener for date field
        binding.edtStartDate.setOnClickListener(v -> showDatePickerDialog());
        binding.edtStartDate.setFocusable(false);
        binding.edtStartDate.setClickable(true);
        binding.edtStartDate.setKeyListener(null);

        // Set current date as default
        String currentDate = dateFormat.format(calendar.getTime());
        binding.edtStartDate.setText(currentDate);
        selectedDate = currentDate;
    }

    private void showDatePickerDialog() {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        selectedDate = dateFormat.format(calendar.getTime());
                        binding.edtStartDate.setText(selectedDate);

                        Log.d(TAG, "Date selected: " + selectedDate);
                        showToast("📅 Date selected: " + selectedDate);
                    }
                },
                year, month, day);

        // Optional: Set max date to current date if needed
        // datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

        datePickerDialog.show();
    }

    private void setupClassDropdown() {
        // Initialize with loading state
        ArrayList<String> initialList = new ArrayList<>();
        initialList.add("Loading classes...");

        classAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, initialList);
        classAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.edtSelectClass.setAdapter(classAdapter);
        binding.edtSelectClass.setThreshold(0);
        binding.edtSelectClass.setKeyListener(null);
        binding.edtSelectClass.setFocusable(false);
        binding.edtSelectClass.setEnabled(false);

        binding.edtSelectClass.setOnClickListener(v -> {
            if (!isClassesLoaded) {
                showToast("⏳ Please wait, classes are still loading...");
                return;
            }
            if (classList != null && !classList.isEmpty()) {
                binding.edtSelectClass.showDropDown();
            } else {
                showToast("❌ No classes available. Please refresh.");
            }
        });

        binding.edtSelectClass.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                handleClassSelection(position);
            }
        });

        binding.edtSelectClass.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus && isClassesLoaded && classList != null && !classList.isEmpty()) {
                binding.edtSelectClass.showDropDown();
            }
        });
    }

    private void setupStudentDropdown() {
        // Initialize student dropdown
        ArrayList<String> initialStudentList = new ArrayList<>();
        initialStudentList.add("Select class first...");

        studentsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, initialStudentList);
        studentsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.edtSelectStudent.setAdapter(studentsAdapter);
        binding.edtSelectStudent.setThreshold(0);
        binding.edtSelectStudent.setKeyListener(null);
        binding.edtSelectStudent.setFocusable(false);
        binding.edtSelectStudent.setEnabled(false);

        binding.edtSelectStudent.setOnClickListener(v -> {
            if (selectedClassId == INVALID_ID) {
                showToast("📚 Please select a class first");
                return;
            }
            if (!isStudentsLoaded) {
                showToast("⏳ Please wait, students are still loading...");
                return;
            }
            if (studentsList != null && !studentsList.isEmpty()) {
                binding.edtSelectStudent.showDropDown();
            } else {
                showToast("❌ No students available for selected class.");
            }
        });

        binding.edtSelectStudent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                handleStudentSelection(position);
            }
        });

        binding.edtSelectStudent.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus && isStudentsLoaded && studentsList != null && !studentsList.isEmpty()) {
                binding.edtSelectStudent.showDropDown();
            }
        });
    }

    private void setupCreateButton() {
        binding.btnSearch.setOnClickListener(v -> handleCreateButtonClick());
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

                // Load students for selected class
                loadStudentsForSelectedClass();
            } catch (Exception e) {
                Log.e(TAG, "Error selecting class: " + e.getMessage());
                showToast("❌ Error selecting class. Please try again.");
                resetClassSelection();
            }
        }

        binding.edtSelectClass.dismissDropDown();
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

                showToast("✅ Selected: " + selectedStudentName);
                Log.d(TAG, "Student selected - ID: " + selectedStudentId + ", Name: " + selectedStudentName);

            } catch (Exception e) {
                Log.e(TAG, "Error selecting student: " + e.getMessage());
                showToast("❌ Error selecting student. Please try again.");
                resetStudentSelection();
            }
        }

        binding.edtSelectStudent.dismissDropDown();
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
        binding.edtSelectStudent.setText("⏳ Loading students...", false);
        binding.edtSelectStudent.setEnabled(false);

        // Load students using StudentHelper
        studentHelper.loadStudentsByClass(selectedClassId);
    }

    private void handleCreateButtonClick() {
        // Validate all fields
        if (!validateDate()) return;
        if (!validateClassSelection()) return;
        if (!validateStudentSelection()) return;

        // All validations passed, navigate to next activity
        navigateToNextActivity();
    }

    private boolean validateDate() {
        if (TextUtils.isEmpty(selectedDate)) {
            showToast("📅 Please select a date");
            binding.edtStartDate.requestFocus();
            return false;
        }

        // Additional date validation if needed
        try {
            dateFormat.parse(selectedDate);
        } catch (Exception e) {
            showToast("📅 Invalid date format. Please select a valid date.");
            binding.edtStartDate.requestFocus();
            return false;
        }

        return true;
    }

    private boolean validateClassSelection() {
        if (selectedClassId == INVALID_ID) {
            showToast("📚 Please select a class");
            binding.edtSelectClass.requestFocus();
            if (isClassesLoaded) {
                binding.edtSelectClass.showDropDown();
            }
            return false;
        }
        return true;
    }

    private boolean validateStudentSelection() {
        if (selectedStudentId == INVALID_ID) {
            showToast("👤 Please select a student");
            binding.edtSelectStudent.requestFocus();
            if (isStudentsLoaded) {
                binding.edtSelectStudent.showDropDown();
            }
            return false;
        }
        return true;
    }

    private void navigateToNextActivity() {
        try {
            // Replace with your actual next activity class
            Intent intent = new Intent(this, ActivityCharacterOrLeaveCertificate.class);
            intent.putExtra("from", fromActivity);
            intent.putExtra("classId", selectedClassId);
            intent.putExtra("studentId", selectedStudentId);
            intent.putExtra("date", selectedDate);
            intent.putExtra("className", selectedClassName);
            intent.putExtra("studentName", selectedStudentName);

            Log.d(TAG, "Navigating to next activity - From: " + fromActivity +
                    ", ClassId: " + selectedClassId + ", StudentId: " + selectedStudentId +
                    ", Date: " + selectedDate);

            startActivity(intent);
            showToast("🎓 Generating certificate for " + selectedStudentName + "...");

        } catch (Exception e) {
            Log.e(TAG, "Error navigating to next activity: " + e.getMessage());
            showToast("❌ Error opening certificate generation. Please try again.");
        }
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

            binding.edtSelectStudent.setEnabled(true);
            binding.edtSelectStudent.setText("❌ Failed to load students", false);
            binding.edtSelectStudent.setOnClickListener(v -> {
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
            String displayText = student.getStudentName() + " (ID: " + student.getStudentId() + ")";
            studentDisplayList.add(displayText);
        }

        studentsAdapter.clear();
        studentsAdapter.addAll(studentDisplayList);
        studentsAdapter.notifyDataSetChanged();

        binding.edtSelectStudent.setEnabled(true);
        binding.edtSelectStudent.setHint("📋 Select Student");
        binding.edtSelectStudent.setText("", false);
    }

    private void loadClasses() {
        isClassesLoaded = false;

        classApiHelper.fetchAllClasses(new ClassApiHelper.ClassListCallback() {
            @Override
            public void onSuccess(ArrayList<ClassModel.Data> classList) {
                ActivityCertificateDataSelection.this.classList = classList;

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

                binding.edtSelectClass.setEnabled(true);
                binding.edtSelectClass.setText("❌ Failed to load classes", false);
                binding.edtSelectClass.setOnClickListener(v -> {
                    showToast("🔄 Retrying to load classes...");
                    loadClasses();
                });
            }

            @Override
            public void onLoading(boolean isLoading) {
                if (isLoading) {
                    binding.edtSelectClass.setEnabled(false);
                    classAdapter.clear();
                    classAdapter.add("⏳ Loading classes...");
                    classAdapter.notifyDataSetChanged();
                    binding.edtSelectClass.setText("⏳ Loading classes...", false);
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

        binding.edtSelectClass.setEnabled(false);
        binding.edtSelectClass.setText("⚠️ No classes available", false);
    }

    private void updateClassDropdown(ArrayList<ClassModel.Data> classList) {
        ArrayList<String> classNames = classApiHelper.getClassNames(classList);

        classAdapter.clear();
        classAdapter.addAll(classNames);
        classAdapter.notifyDataSetChanged();

        binding.edtSelectClass.setEnabled(true);
        binding.edtSelectClass.setHint("📚 Select Class");
        binding.edtSelectClass.setText("", false);
    }

    private void resetClassSelection() {
        selectedClassId = INVALID_ID;
        selectedClassName = "";

        if (classAdapter != null && isClassesLoaded) {
            binding.edtSelectClass.setText("", false);
            binding.edtSelectClass.setHint("📚 Select Class");
        }

        // Also reset student selection when class changes
        resetStudentSelection();
    }

    private void resetStudentSelection() {
        selectedStudentId = INVALID_ID;
        selectedStudentName = "";
        isStudentsLoaded = false;

        if (studentsAdapter != null) {
            studentsAdapter.clear();
            studentsAdapter.add("Select class first...");
            studentsAdapter.notifyDataSetChanged();
            binding.edtSelectStudent.setText("", false);
            binding.edtSelectStudent.setHint("📋 Select Student");
            binding.edtSelectStudent.setEnabled(false);
        }
    }

    private void showToast(String message) {
        if (!isFinishing() && !isDestroyed()) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }

    private String getAuthToken() {
        // Implement your auth token retrieval logic here
         return "Bearer " + pref.getPrefString(this, pref.user_token);
    }

    // Getter methods for testing or external access
    public int getSelectedClassId() {
        return selectedClassId;
    }

    public int getSelectedStudentId() {
        return selectedStudentId;
    }

    public String getSelectedDate() {
        return selectedDate;
    }

    public String getFromActivity() {
        return fromActivity;
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
        showToast("📄 Certificate generation cancelled");
    }
}