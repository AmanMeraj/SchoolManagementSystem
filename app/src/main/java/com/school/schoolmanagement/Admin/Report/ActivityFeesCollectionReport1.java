package com.school.schoolmanagement.Admin.Report;

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
import com.school.schoolmanagement.databinding.ActivityFeesCollectionReport1Binding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ActivityFeesCollectionReport1 extends Utility implements StudentHelper.StudentHelperCallback {
    private static final String TAG = "FeesCollectionReport";
    private static final int INVALID_ID = -1;

    private ActivityFeesCollectionReport1Binding binding;
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
    private int selectedClassId = INVALID_ID;
    private int selectedStudentId = INVALID_ID;
    private String selectedClassName = "";
    private String selectedStudentName = "";

    // Date range variables
    private String startDate = "";
    private String endDate = "";
    private Calendar startCalendar = Calendar.getInstance();
    private Calendar endCalendar = Calendar.getInstance();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    // Loading states
    private boolean isClassesLoaded = false;
    private boolean isStudentsLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityFeesCollectionReport1Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupWindowInsets();
        initializeComponents();
        setupUI();
        loadClasses();
        binding.toolbar.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initializeComponents() {
        // Setup back button
        binding.toolbar.backBtn.setOnClickListener(v -> finish());

        // Initialize API helpers
        classApiHelper = new ClassApiHelper(this);
        classList = new ArrayList<>();
        studentsList = new ArrayList<>();

        // Initialize ViewModel and StudentHelper
        viewModel = new ViewModelProvider(this).get(ViewModel.class);
        String authToken = getAuthToken();

        Log.d(TAG, "Auth token: " + (authToken != null ? "***" + authToken.substring(Math.max(0, authToken.length() - 4)) : "NULL"));

        if (authToken == null || authToken.isEmpty()) {
            Log.e(TAG, "Invalid auth token detected!");
            showToast("❌ Authentication error. Please login again.");
            return;
        }

        studentHelper = new StudentHelper(this, viewModel, authToken, this, this);
    }

    private void setupUI() {
        setupTabSelection();
        setupClassDropdown();
        setupStudentDropdown();
        setupDateRangePicker();
        setupGenerateButton();
    }

    private void setupTabSelection() {
        setModeWholeClass(); // Set initial mode
        binding.tabWholeClass.setOnClickListener(v -> setModeWholeClass());
        binding.tabSingleStudent.setOnClickListener(v -> setModeSingleStudent());
    }

    private void setupClassDropdown() {
        // Initialize with loading state
        ArrayList<String> initialList = new ArrayList<>();
        initialList.add("Loading classes...");

        classAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, initialList);
        classAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.edtClass.setAdapter(classAdapter);
        binding.edtClass.setThreshold(0);
        binding.edtClass.setKeyListener(null);
        binding.edtClass.setFocusable(false);
        binding.edtClass.setEnabled(false);

        binding.edtClass.setOnClickListener(v -> {
            if (!isClassesLoaded) {
                showToast("⏳ Please wait, classes are still loading...");
                return;
            }
            if (classList != null && !classList.isEmpty()) {
                binding.edtClass.showDropDown();
            } else {
                showToast("❌ No classes available. Please refresh.");
            }
        });

        binding.edtClass.setOnItemClickListener((parent, view, position, id) -> handleClassSelection(position));

        binding.edtClass.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus && isClassesLoaded && classList != null && !classList.isEmpty()) {
                binding.edtClass.showDropDown();
            }
        });
    }

    private void setupStudentDropdown() {
        // Initialize student dropdown
        ArrayList<String> initialStudentList = new ArrayList<>();
        initialStudentList.add("Select class first...");

        studentsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, initialStudentList);
        studentsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.edtStudent.setAdapter(studentsAdapter);
        binding.edtStudent.setThreshold(0);
        binding.edtStudent.setKeyListener(null);
        binding.edtStudent.setFocusable(false);
        binding.edtStudent.setEnabled(false);

        binding.edtStudent.setOnClickListener(v -> {
            if (selectedClassId == INVALID_ID) {
                showToast("📚 Please select a class first");
                return;
            }
            if (!isStudentsLoaded) {
                showToast("⏳ Please wait, students are still loading...");
                return;
            }
            if (studentsList != null && !studentsList.isEmpty()) {
                binding.edtStudent.showDropDown();
            } else {
                showToast("❌ No students available for selected class.");
            }
        });

        binding.edtStudent.setOnItemClickListener((parent, view, position, id) -> handleStudentSelection(position));

        binding.edtStudent.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus && isStudentsLoaded && studentsList != null && !studentsList.isEmpty()) {
                binding.edtStudent.showDropDown();
            }
        });
    }

    private void setupDateRangePicker() {
        binding.edtDateRange.setFocusable(false);
        binding.edtDateRange.setClickable(true);
        binding.edtDateRange.setOnClickListener(v -> showDateRangePicker());
    }

    private void setupGenerateButton() {
        binding.btnCreatePaper.setOnClickListener(v -> generateReport());
    }

    private void showDateRangePicker() {
        // Show start date picker first
        DatePickerDialog startDatePicker = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        startCalendar.set(Calendar.YEAR, year);
                        startCalendar.set(Calendar.MONTH, month);
                        startCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        startDate = dateFormat.format(startCalendar.getTime());

                        // After start date is selected, show end date picker
                        showEndDatePicker();
                    }
                },
                startCalendar.get(Calendar.YEAR),
                startCalendar.get(Calendar.MONTH),
                startCalendar.get(Calendar.DAY_OF_MONTH)
        );

        startDatePicker.setTitle("Select Start Date");
        startDatePicker.show();
    }

    private void showEndDatePicker() {
        // Set minimum date for end date picker to be start date
        DatePickerDialog endDatePicker = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        endCalendar.set(Calendar.YEAR, year);
                        endCalendar.set(Calendar.MONTH, month);
                        endCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        endDate = dateFormat.format(endCalendar.getTime());

                        // Validate date range
                        if (endCalendar.before(startCalendar)) {
                            showToast("❌ End date cannot be before start date");
                            endDate = "";
                            return;
                        }

                        // Update the EditText with the selected date range
                        String dateRange = startDate + " - " + endDate;
                        binding.edtDateRange.setText(dateRange);
                        showToast("✅ Date range selected: " + dateRange);
                    }
                },
                endCalendar.get(Calendar.YEAR),
                endCalendar.get(Calendar.MONTH),
                endCalendar.get(Calendar.DAY_OF_MONTH)
        );

        endDatePicker.setTitle("Select End Date");
        // Set minimum date to start date
        endDatePicker.getDatePicker().setMinDate(startCalendar.getTimeInMillis());
        endDatePicker.show();
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

        binding.edtClass.dismissDropDown();
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

        binding.edtStudent.dismissDropDown();
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
        binding.edtStudent.setText("⏳ Loading students...", false);
        binding.edtStudent.setEnabled(false);

        // Load students using StudentHelper
        studentHelper.loadStudentsByClass(selectedClassId);
    }

    private void generateReport() {
        if (isWholeClassMode) {
            generateWholeClassReport();
        } else {
            generateSingleStudentReport();
        }
    }

    private void generateWholeClassReport() {
        // Validate required fields
        if (!validateClassSelection()) return;
        if (!validateDateRange()) return;

        showToast("📊 Generating fees collection report for " + selectedClassName + "...");

        // TODO: Implement your report generation logic here
        Log.d(TAG, "Generating whole class report - ClassID: " + selectedClassId +
                ", ClassName: " + selectedClassName +
                ", DateRange: " + startDate + " - " + endDate);

        // Example: Call your API to generate report
        Intent intent= new Intent(ActivityFeesCollectionReport1.this, ActivityFeesCollectionReport.class);
        intent.putExtra("classId",selectedClassId);
        intent.putExtra("startDate",startDate);
        intent.putExtra("endDate",endDate);
        intent.putExtra("from","wholeClass");
        startActivity(intent);

    }

    private void generateSingleStudentReport() {
        // Validate required fields
        if (!validateClassSelection()) return;
        if (!validateStudentSelection()) return;
        if (!validateDateRange()) return;

        showToast("📊 Generating fees collection report for " + selectedStudentName + "...");

        // TODO: Implement your report generation logic here
        Log.d(TAG, "Generating single student report - ClassID: " + selectedClassId +
                ", StudentID: " + selectedStudentId +
                ", StudentName: " + selectedStudentName +
                ", DateRange: " + startDate + " - " + endDate);

        // Example: Call your API to generate report
        Intent intent= new Intent(ActivityFeesCollectionReport1.this, ActivityFeesCollectionReport.class);
        intent.putExtra("studentId",selectedStudentId);
        intent.putExtra("startDate",startDate);
        intent.putExtra("endDate",endDate);
        intent.putExtra("from","singleStudent");
        startActivity(intent);
    }

    private boolean validateClassSelection() {
        if (selectedClassId == INVALID_ID) {
            showToast("📚 Please select a class first");
            binding.edtClass.requestFocus();
            if (isClassesLoaded) {
                binding.edtClass.showDropDown();
            }
            return false;
        }
        return true;
    }

    private boolean validateStudentSelection() {
        if (selectedStudentId == INVALID_ID) {
            showToast("👤 Please select a student first");
            binding.edtStudent.requestFocus();
            if (isStudentsLoaded) {
                binding.edtStudent.showDropDown();
            }
            return false;
        }
        return true;
    }

    private boolean validateDateRange() {
        if (TextUtils.isEmpty(startDate) || TextUtils.isEmpty(endDate)) {
            showToast("📅 Please select a date range first");
            binding.edtDateRange.requestFocus();
            return false;
        }
        return true;
    }

    private void setModeWholeClass() {
        isWholeClassMode = true;

        // Update UI visibility - Only class and date range visible
        binding.edtClass.setVisibility(View.VISIBLE);
        binding.relStudent.setVisibility(View.GONE);
        binding.edtDateRange.setVisibility(View.VISIBLE);

        // Update tab styling
        binding.tabWholeClass.setBackground(getResources().getDrawable(R.drawable.ornage_blue_btn));
        binding.tabWholeClass.setTextColor(getResources().getColor(R.color.white));
        binding.tabSingleStudent.setBackground(null);
        binding.tabSingleStudent.setTextColor(getResources().getColor(R.color.grey));

        // Update title
        binding.mainTitle.setText("Fees Collection Report");

        resetFormData();
    }

    private void setModeSingleStudent() {
        isWholeClassMode = false;

        // Update UI visibility - All fields visible
        binding.edtClass.setVisibility(View.VISIBLE);
        binding.relStudent.setVisibility(View.VISIBLE);
        binding.edtDateRange.setVisibility(View.VISIBLE);

        // Update tab styling
        binding.tabSingleStudent.setBackground(getResources().getDrawable(R.drawable.ornage_blue_btn));
        binding.tabSingleStudent.setTextColor(getResources().getColor(R.color.white));
        binding.tabWholeClass.setBackground(null);
        binding.tabWholeClass.setTextColor(getResources().getColor(R.color.grey));

        // Update title
        binding.mainTitle.setText("Fees Collection Report");

        resetFormData();
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

            binding.edtStudent.setEnabled(true);
            binding.edtStudent.setText("❌ Failed to load students", false);
            binding.edtStudent.setOnClickListener(v -> {
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

        binding.edtStudent.setEnabled(true);
        binding.edtStudent.setHint("📋 Select Student");
        binding.edtStudent.setText("", false);
    }

    private void loadClasses() {
        isClassesLoaded = false;

        classApiHelper.fetchAllClasses(new ClassApiHelper.ClassListCallback() {
            @Override
            public void onSuccess(ArrayList<ClassModel.Data> classList) {
                ActivityFeesCollectionReport1.this.classList = classList;

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

                binding.edtClass.setEnabled(true);
                binding.edtClass.setText("❌ Failed to load classes", false);
                binding.edtClass.setOnClickListener(v -> {
                    showToast("🔄 Retrying to load classes...");
                    loadClasses();
                });
            }

            @Override
            public void onLoading(boolean isLoading) {
                if (isLoading) {
                    binding.edtClass.setEnabled(false);
                    classAdapter.clear();
                    classAdapter.add("⏳ Loading classes...");
                    classAdapter.notifyDataSetChanged();
                    binding.edtClass.setText("⏳ Loading classes...", false);
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

        binding.edtClass.setEnabled(false);
        binding.edtClass.setText("⚠️ No classes available", false);
    }

    private void updateClassDropdown(ArrayList<ClassModel.Data> classList) {
        ArrayList<String> classNames = classApiHelper.getClassNames(classList);

        classAdapter.clear();
        classAdapter.addAll(classNames);
        classAdapter.notifyDataSetChanged();

        binding.edtClass.setEnabled(true);
        binding.edtClass.setHint("📚 Select Class");
        binding.edtClass.setText("", false);
    }

    private void resetFormData() {
        resetClassSelection();
        resetStudentSelection();
        resetDateRange();
    }

    private void resetClassSelection() {
        selectedClassId = INVALID_ID;
        selectedClassName = "";

        if (classAdapter != null && isClassesLoaded) {
            binding.edtClass.setText("", false);
            binding.edtClass.setHint("📚 Select Class");
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
            binding.edtStudent.setText("", false);
            binding.edtStudent.setHint("📋 Select Student");
            binding.edtStudent.setEnabled(false);
        }
    }

    private void resetDateRange() {
        startDate = "";
        endDate = "";
        binding.edtDateRange.setText("");
        binding.edtDateRange.setHint("yyyy-MM-dd - yyyy-MM-dd");
    }

    private void showToast(String message) {
        if (!isFinishing() && !isDestroyed()) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }

    private String getAuthToken() {
        return "Bearer " + pref.getPrefString(this, pref.user_token);
    }

    // Getter methods
    public int getSelectedClassId() {
        return selectedClassId;
    }

    public String getSelectedClassName() {
        return selectedClassName;
    }

    public int getSelectedStudentId() {
        return selectedStudentId;
    }

    public String getSelectedStudentName() {
        return selectedStudentName;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public boolean isWholeClassMode() {
        return isWholeClassMode;
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
        showToast("📊 Report generation cancelled");
    }
}