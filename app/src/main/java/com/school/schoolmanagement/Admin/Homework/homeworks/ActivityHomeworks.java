package com.school.schoolmanagement.Admin.Homework.homeworks;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.school.schoolmanagement.Adapters.AdapterHomeworkDetails;
import com.school.schoolmanagement.Admin.Model.AllEmployees;
import com.school.schoolmanagement.Admin.Model.ClassModel;
import com.school.schoolmanagement.Admin.Model.CreateHomework;
import com.school.schoolmanagement.Admin.Model.SubjectModel;
import com.school.schoolmanagement.GlobalViewModel.ViewModel;
import com.school.schoolmanagement.HelperClasses.ClassApiHelper;
import com.school.schoolmanagement.HelperClasses.EmployeeManager;
import com.school.schoolmanagement.HelperClasses.SubjectApiHelper;
import com.school.schoolmanagement.Model.HomeworkDetails;
import com.school.schoolmanagement.R;
import com.school.schoolmanagement.Utils.Utility;
import com.school.schoolmanagement.databinding.ActivityHomeworksBinding;
import com.school.schoolmanagement.databinding.AddHomeworkDialogBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ActivityHomeworks extends Utility {

    private ActivityHomeworksBinding binding;
    private AdapterHomeworkDetails adapter;
    private ArrayList<HomeworkDetails.Datum> homeworkList;
    private Calendar calendar;
    private SimpleDateFormat dateFormat;

    // Helper classes

    private SubjectApiHelper subjectApiHelper;
    private ArrayList<SubjectModel> subjectList = new ArrayList<>();
    private ArrayList<String> subjectNames = new ArrayList<>();
    private int dialogSelectedSubjectId = -1;
    private String dialogSelectedSubjectName = "";

    private EmployeeManager employeeManager;
    private ClassApiHelper classApiHelper;
    ViewModel viewModel;

    // Class related variables
    private ArrayList<ClassModel.Data> classList = new ArrayList<>();
    private ArrayList<String> classNames = new ArrayList<>();
    private int selectedClassId = -1;
    private String selectedClassName = "";

    // Employee related variables
    private ArrayList<String> teacherNames = new ArrayList<>();
    private List<AllEmployees> teacherEmployees = new ArrayList<>();
    private String selectedTeacherId = "";
    private String selectedTeacherName = "";

    // For dialog - separate variables
    private int dialogSelectedClassId = -1;
    private String dialogSelectedClassName = "";
    private String dialogSelectedTeacherId = "";
    private String dialogSelectedTeacherName = "";

    // Loading states
    private boolean isClassesLoaded = false;
    private boolean isTeachersLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityHomeworksBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        viewModel = new ViewModelProvider(this).get(ViewModel.class);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeComponents();
        setupRecyclerView();
        setupClickListeners();
        setupSearchFilters();

        // Load data when activity is created
        loadInitialData();
    }

    private void initializeComponents() {
        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        homeworkList = new ArrayList<>();

        // Initialize helper classes
        employeeManager = EmployeeManager.getInstance();
        employeeManager.initialize(this, this);
        classApiHelper = new ClassApiHelper(this);
        subjectApiHelper = new SubjectApiHelper(this);
    }

    private void loadInitialData() {
        // Show loading state
        showLoadingState();

        // Load classes and teachers simultaneously
        loadClassesData();
        loadTeachersData();
        loadAllHomeWorks();
    }

    private void loadAllHomeWorks() {
        showLoading(true);
        String auth ="Bearer "+ pref.getPrefString(this, pref.user_token);

        viewModel.getAllHomework(auth).observe(this, response -> {
            showLoading(false);

            if (response != null) {
                if (response.isSuccess && response.data != null) {
                    HomeworkDetails homeworkDetails = response.data;
                    if (homeworkDetails.data != null && !homeworkDetails.data.isEmpty()) {
                        homeworkList.clear();
                        homeworkList.addAll(homeworkDetails.data);
                        adapter.notifyDataSetChanged();

                        // Show/hide empty state
                        showEmptyState(false);

                    } else {
                        handleEmptyHomework("No homework found");
                    }
                } else {
                    handleApiError(response.message, response.code);
                }
            } else {
                handleApiError("Failed to load homework", -1);
            }
        });
    }

    private void performSearch() {
        String searchText = binding.edtSearch.getText().toString().trim();
        String selectedDate = binding.edtHomeworkDate.getText().toString().trim();

        // Prepare filter parameters
        Integer classIdFilter = (selectedClassId != -1) ? selectedClassId : null;
        String teacherIdFilter = (!selectedTeacherId.isEmpty()) ? selectedTeacherId : null;
        String dateFilter = (!selectedDate.isEmpty()) ? selectedDate : null;
        String searchFilter = (!searchText.isEmpty()) ? searchText : null;

        // Log filters for debugging
        android.util.Log.d("HomeworkFilter", "Applying filters - Class ID: " + classIdFilter +
                ", Teacher ID: " + teacherIdFilter + ", Date: " + dateFilter + ", Search: " + searchFilter);

        // Show loading
        showLoading(true);

        String auth ="Bearer "+ pref.getPrefString(this, pref.user_token);

        viewModel.getFilteredHomework(auth, classIdFilter, teacherIdFilter, dateFilter)
                .observe(this, response -> {
                    showLoading(false);

                    if (response != null) {
                        if (response.isSuccess && response.data != null) {
                            HomeworkDetails homeworkDetails = response.data;
                            if (homeworkDetails.data != null && !homeworkDetails.data.isEmpty()) {
                                homeworkList.clear();
                                homeworkList.addAll(homeworkDetails.data);
                                adapter.notifyDataSetChanged();

                                // Show/hide empty state
                                showEmptyState(false);

                            } else {
                                handleEmptyHomework("No homework found with current filters");
                            }
                        } else {
                            handleApiError(response.message, response.code);
                        }
                    } else {
                        handleApiError("Search failed. Please try again.", -1);
                    }
                });
    }

    private void handleEmptyHomework(String message) {
        homeworkList.clear();
        adapter.notifyDataSetChanged();
        showEmptyState(true);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void handleApiError(String errorMessage, int statusCode) {
        showLoading(false);

        switch (statusCode) {
            case 401:
                Toast.makeText(this, "Session expired. Please login again.", Toast.LENGTH_LONG).show();
                // You might want to redirect to login activity here
                break;
            case 403:
                Toast.makeText(this, "You don't have permission to view homework.", Toast.LENGTH_LONG).show();
                break;
            case 404:
                Toast.makeText(this, "No homework found.", Toast.LENGTH_SHORT).show();
                break;
            case -1:
                Toast.makeText(this, "No internet connection. Please check your network.", Toast.LENGTH_LONG).show();
                break;
            default:
                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
        }
    }

    private void showLoading(boolean show) {
        if (show) {
            binding.loader.rlLoader.setVisibility(View.VISIBLE);
            binding.btnSearch.setEnabled(false);
            binding.addHomeworkBtn.setEnabled(false);
        } else {
            binding.loader.rlLoader.setVisibility(View.GONE);
            binding.btnSearch.setEnabled(true);
            binding.addHomeworkBtn.setEnabled(true);
        }
    }

    private void showEmptyState(boolean show) {
        // Assuming you have empty state views in your layout
        // Adjust these based on your actual layout
        if (show) {
            binding.rcPromoteStudent.setVisibility(View.GONE);
            // binding.emptyStateLayout.setVisibility(View.VISIBLE); // Uncomment if you have empty state layout
        } else {
            binding.rcPromoteStudent.setVisibility(View.VISIBLE);
            // binding.emptyStateLayout.setVisibility(View.GONE); // Uncomment if you have empty state layout
        }
    }

    private void showLoadingState() {
        binding.edtSelectClass.setHint("Loading classes...");
        binding.edtSelectTeacher.setHint("Loading teachers...");
    }


    private void loadClassesData() {
        classApiHelper.fetchAllClasses(new ClassApiHelper.ClassListCallback() {
            @Override
            public void onSuccess(ArrayList<ClassModel.Data> classListData) {
                classList.clear();
                classList.addAll(classListData);

                // Get class names for dropdown
                classNames = classApiHelper.getClassNames(classList);

                runOnUiThread(() -> {
                    binding.edtSelectClass.setHint("Select Class");
                    setupClassDropdown();
                    isClassesLoaded = true;
                    checkDataLoadingComplete();
                });
            }

            @Override
            public void onError(String errorMessage) {
                runOnUiThread(() -> {
                    binding.edtSelectClass.setHint("Error loading classes");

                    isClassesLoaded = true; // Mark as loaded even on error
                    checkDataLoadingComplete();
                });
            }

            @Override
            public void onLoading(boolean isLoading) {
                // Handle loading state if needed
            }
        });
    }



    private void loadTeachersData() {
        employeeManager.loadAllEmployees(new EmployeeManager.EmployeeLoadCallback() {
            @Override
            public void onSuccess(List<AllEmployees> employees) {
                // Filter teachers from all employees
                teacherEmployees = employeeManager.getEmployeesByRole("Teacher");

                // Clear and populate teacher names
                teacherNames.clear();
                for (AllEmployees employee : teacherEmployees) {
                    if (employee.getName() != null && !employee.getName().trim().isEmpty()) {
                        teacherNames.add(employee.getName());
                    }
                }

                runOnUiThread(() -> {
                    binding.edtSelectTeacher.setHint("Select Teacher");
                    setupTeacherDropdown();
                    isTeachersLoaded = true;
                    checkDataLoadingComplete();


                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    binding.edtSelectTeacher.setHint("Error loading teachers");

                    // Fallback to sample data if API fails
                    setupFallbackTeacherData();

                    isTeachersLoaded = true; // Mark as loaded even on error
                    checkDataLoadingComplete();
                });
            }
        });
    }

    private void checkDataLoadingComplete() {
        if (isClassesLoaded && isTeachersLoaded) {
            // Both data sets loaded, enable UI interactions
        }
    }

    private void setupClassDropdown() {
        if (!classNames.isEmpty()) {
            ArrayAdapter<String> classAdapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_dropdown_item_1line, classNames);
            binding.edtSelectClass.setAdapter(classAdapter);
        }
    }

    private void setupTeacherDropdown() {
        if (!teacherNames.isEmpty()) {
            ArrayAdapter<String> teacherAdapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_dropdown_item_1line, teacherNames);
            binding.edtSelectTeacher.setAdapter(teacherAdapter);
        }
    }

    private void setupFallbackTeacherData() {
        // Fallback to sample data if API fails
        String[] fallbackTeachers = {"No Teachers Found!"};

        teacherNames.clear();
        for (String teacher : fallbackTeachers) {
            teacherNames.add(teacher);
        }
        setupTeacherDropdown();
    }

    private void setupRecyclerView() {
        adapter = new AdapterHomeworkDetails(this, homeworkList);
        binding.rcPromoteStudent.setLayoutManager(new LinearLayoutManager(this));
        binding.rcPromoteStudent.setAdapter(adapter);
    }

    private void setupClickListeners() {
        // Add homework button click
        binding.addHomeworkBtn.setOnClickListener(view -> showAddHomeworkDialog());

        // Search button click - updated to handle both search and show all
        binding.btnSearch.setOnClickListener(view -> {
            if (hasActiveFilters()) {
                performSearch();
            } else {
                loadAllHomeWorks(); // Load all homework if no filters applied
            }
        });

        // Filter date picker
        binding.edtHomeworkDate.setOnClickListener(view -> showDatePicker(binding.edtHomeworkDate));

        // Export buttons
        binding.tvCopy.setOnClickListener(view -> Toast.makeText(this, "Copy functionality", Toast.LENGTH_SHORT).show());
        binding.tvCsv.setOnClickListener(view -> Toast.makeText(this, "CSV export functionality", Toast.LENGTH_SHORT).show());
        binding.tvExcel.setOnClickListener(view -> Toast.makeText(this, "Excel export functionality", Toast.LENGTH_SHORT).show());
        binding.tvPdf.setOnClickListener(view -> Toast.makeText(this, "PDF export functionality", Toast.LENGTH_SHORT).show());
        binding.tvPrint.setOnClickListener(view -> Toast.makeText(this, "Print functionality", Toast.LENGTH_SHORT).show());
        binding.tvColumnVisibility.setOnClickListener(view -> Toast.makeText(this, "Column visibility", Toast.LENGTH_SHORT).show());

        // Pagination buttons
        binding.prevButton.setOnClickListener(view -> Toast.makeText(this, "Previous page", Toast.LENGTH_SHORT).show());
        binding.nextButton.setOnClickListener(view -> Toast.makeText(this, "Next page", Toast.LENGTH_SHORT).show());
    }

    private boolean hasActiveFilters() {
        String searchText = binding.edtSearch.getText().toString().trim();
        String selectedDate = binding.edtHomeworkDate.getText().toString().trim();

        return !searchText.isEmpty() ||
                !selectedDate.isEmpty() ||
                selectedClassId != -1 ||
                !selectedTeacherId.isEmpty();
    }

    private void clearFilters() {
        // Clear UI elements
        binding.edtSearch.setText("");
        binding.edtHomeworkDate.setText("");
        binding.edtSelectClass.setText("");
        binding.edtSelectTeacher.setText("");

        // Reset selected values
        selectedClassId = -1;
        selectedClassName = "";
        selectedTeacherId = "";
        selectedTeacherName = "";

        // Reload all homework
        loadAllHomeWorks();
    }

    private void refreshHomework() {
        if (hasActiveFilters()) {
            performSearch(); // Apply current filters
        } else {
            loadAllHomeWorks(); // Load all homework
        }
    }

    private void setupSearchFilters() {
        // Setup class dropdown with click listener
        binding.edtSelectClass.setOnClickListener(view -> {
            if (!isClassesLoaded) {
                Toast.makeText(this, "Classes are still loading...", Toast.LENGTH_SHORT).show();
                return;
            }
            if (classNames.isEmpty()) {
                Toast.makeText(this, "No classes available", Toast.LENGTH_SHORT).show();
                return;
            }
            binding.edtSelectClass.showDropDown();
        });

        // Setup class selection listener
        binding.edtSelectClass.setOnItemClickListener((parent, view, position, id) -> {
            if (position < classNames.size()) {
                String selectedName = classNames.get(position);

                // Skip if "Select Class" is selected
                if (position == 0) {
                    selectedClassId = -1;
                    selectedClassName = "";
                    binding.edtSelectClass.setText("");
                    return;
                }

                selectedClassId = classApiHelper.getClassIdByPosition(classList, position);
                selectedClassName = selectedName;

                // Set the name in the EditText
                binding.edtSelectClass.setText(selectedClassName);

                // Log for debugging
                android.util.Log.d("ClassSelection", "Selected Class: " + selectedClassName + " (ID: " + selectedClassId + ")");
            }
        });

        // Setup teacher dropdown with click listener
        binding.edtSelectTeacher.setOnClickListener(view -> {
            if (!isTeachersLoaded) {
                Toast.makeText(this, "Teachers are still loading...", Toast.LENGTH_SHORT).show();
                return;
            }
            if (teacherNames.isEmpty()) {
                Toast.makeText(this, "No teachers available", Toast.LENGTH_SHORT).show();
                return;
            }
            binding.edtSelectTeacher.showDropDown();
        });

        // Setup teacher selection listener
        binding.edtSelectTeacher.setOnItemClickListener((parent, view, position, id) -> {
            if (position < teacherEmployees.size()) {
                AllEmployees selectedTeacher = teacherEmployees.get(position);
                selectedTeacherId = selectedTeacher.getEmployeeId() >= 0 ? String.valueOf(selectedTeacher.getEmployeeId()) : "";
                selectedTeacherName = selectedTeacher.getName();

                // Set the name in the EditText
                binding.edtSelectTeacher.setText(selectedTeacherName);

                // Log for debugging
                android.util.Log.d("TeacherSelection", "Selected Teacher: " + selectedTeacherName + " (ID: " + selectedTeacherId + ")");
            }
        });

        // Setup search functionality
        binding.edtSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (hasActiveFilters()) {
                performSearch();
            } else {
                loadAllHomeWorks();
            }
            return true;
        });
    }

    private void showDatePicker(View targetView) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    String selectedDate = dateFormat.format(calendar.getTime());
                    if (targetView.getId() == R.id.edt_homework_date) {
                        binding.edtHomeworkDate.setText(selectedDate);
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        datePickerDialog.show();
    }

    private void showAddHomeworkDialog() {
        Dialog dialog = new Dialog(this);
        AddHomeworkDialogBinding dialogBinding = AddHomeworkDialogBinding.inflate(getLayoutInflater());
        dialog.setContentView(dialogBinding.getRoot());
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // Setup dropdowns in dialog
        setupDialogDropdowns(dialogBinding);

        // Set up date picker for homework date in dialog
        dialogBinding.edtHomeworkDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    (view, year, month, dayOfMonth) -> {
                        Calendar cal = Calendar.getInstance();
                        cal.set(Calendar.YEAR, year);
                        cal.set(Calendar.MONTH, month);
                        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        String selectedDate = dateFormat.format(cal.getTime());
                        dialogBinding.edtHomeworkDate.setText(selectedDate);
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        });

        // Set on click listener for add button
        dialogBinding.btnAddHomework.setOnClickListener(v -> {
            if (validateHomeworkForm(dialogBinding)) {
                addNewHomework(dialogBinding);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void setupDialogDropdowns(AddHomeworkDialogBinding dialogBinding) {
        // Setup class dropdown in dialog
        if (dialogBinding.edtSelectClass != null) {
            setupClassDropdown(dialogBinding);
        }

        // Setup teacher dropdown in dialog
        if (dialogBinding.edtSelectTeacher != null) {
            setupTeacherDropdown(dialogBinding);
        }

        // Setup subject dropdown (initially empty until class is selected)
        setupSubjectDropdown(dialogBinding);
    }

    private void setupClassDropdown(AddHomeworkDialogBinding dialogBinding) {
        // Prepare class names list
        ArrayList<String> displayClassNames = new ArrayList<>();
        displayClassNames.add("Select Class");

        if (!classNames.isEmpty() && classNames.size() > 1) {
            // Add actual class names (skip the first "Select Class" if it exists)
            for (int i = 1; i < classNames.size(); i++) {
                displayClassNames.add(classNames.get(i));
            }
        }

        ArrayAdapter<String> classAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, displayClassNames);
        dialogBinding.edtSelectClass.setAdapter(classAdapter);
        dialogBinding.edtSelectClass.setThreshold(1000); // Prevent filtering
        dialogBinding.edtSelectClass.setKeyListener(null); // Make it non-editable
        dialogBinding.edtSelectClass.setText("Select Class");

        dialogBinding.edtSelectClass.setOnClickListener(view -> {
            if (displayClassNames.size() <= 1) {
                Toast.makeText(this, "No classes available", Toast.LENGTH_SHORT).show();
            } else {
                dialogBinding.edtSelectClass.showDropDown();
            }
        });

        // Setup class selection listener for dialog
        dialogBinding.edtSelectClass.setOnItemClickListener((parent, view, position, id) -> {
            if (position == 0) {
                // "Select Class" is selected
                dialogSelectedClassId = -1;
                dialogSelectedClassName = "";
                dialogBinding.edtSelectClass.setText("Select Class");
                clearSubjectDropdown(dialogBinding);
                return;
            }

            if (position < displayClassNames.size()) {
                String selectedName = displayClassNames.get(position);

                // Get the actual position in the original classList (accounting for the removed "Select Class")
                int actualPosition = position; // Since we reconstructed the list without duplicate "Select Class"

                dialogSelectedClassId = classApiHelper.getClassIdByPosition(classList, actualPosition);
                dialogSelectedClassName = selectedName;

                // Set the name in the EditText
                dialogBinding.edtSelectClass.setText(dialogSelectedClassName);

                // Load subjects for the selected class
                loadSubjectsForSelectedClass(dialogBinding, dialogSelectedClassId);

                // Log for debugging
                android.util.Log.d("DialogClassSelection", "Selected Class: " + dialogSelectedClassName + " (ID: " + dialogSelectedClassId + ")");
            }
        });
    }

    private void setupTeacherDropdown(AddHomeworkDialogBinding dialogBinding) {
        // Prepare teacher names list
        ArrayList<String> displayTeacherNames = new ArrayList<>();
        displayTeacherNames.add("Select Teacher");

        if (!teacherNames.isEmpty()) {
            // Add actual teacher names (skip the first "Select Teacher" if it exists)
            for (int i = 0; i < teacherNames.size(); i++) {
                displayTeacherNames.add(teacherNames.get(i));
            }
        }

        ArrayAdapter<String> teacherAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, displayTeacherNames);
        dialogBinding.edtSelectTeacher.setAdapter(teacherAdapter);
        dialogBinding.edtSelectTeacher.setThreshold(1000); // Prevent filtering
        dialogBinding.edtSelectTeacher.setKeyListener(null); // Make it non-editable
        dialogBinding.edtSelectTeacher.setText("Select Teacher");

        dialogBinding.edtSelectTeacher.setOnClickListener(view -> {
            if (displayTeacherNames.size() == 0) {
                Toast.makeText(this, "No teachers available", Toast.LENGTH_SHORT).show();
            } else {
                dialogBinding.edtSelectTeacher.showDropDown();
            }
        });

        // Setup teacher selection listener for dialog
        dialogBinding.edtSelectTeacher.setOnItemClickListener((parent, view, position, id) -> {
            if (position == 0) {
                // "Select Teacher" is selected
                dialogSelectedTeacherId = "";
                dialogSelectedTeacherName = "";
                dialogBinding.edtSelectTeacher.setText("Select Teacher");
                return;
            }

            if (position < displayTeacherNames.size() && position <= teacherEmployees.size()) {
                // Account for the "Select Teacher" placeholder when accessing teacherEmployees
                AllEmployees selectedTeacher = teacherEmployees.get(position-1);
                dialogSelectedTeacherId = selectedTeacher.getEmployeeId() >= 0 ? String.valueOf(selectedTeacher.getEmployeeId()) : "";
                dialogSelectedTeacherName = selectedTeacher.getName();

                // Set the name in the EditText
                dialogBinding.edtSelectTeacher.setText(dialogSelectedTeacherName);

                // Log for debugging
                android.util.Log.d("DialogTeacherSelection", "Selected Teacher: " + dialogSelectedTeacherName + " (ID: " + dialogSelectedTeacherId + ")");
            }
        });
    }

    private void setupSubjectDropdown(AddHomeworkDialogBinding dialogBinding) {
        if (dialogBinding.edtSelectSubject != null) {
            // Initialize with only "Select Subject"
            ArrayList<String> initialSubjectNames = new ArrayList<>();

            ArrayAdapter<String> subjectAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, initialSubjectNames);
            dialogBinding.edtSelectSubject.setAdapter(subjectAdapter);
            dialogBinding.edtSelectSubject.setThreshold(1000); // Prevent filtering
            dialogBinding.edtSelectSubject.setKeyListener(null); // Make it non-editable
            dialogBinding.edtSelectSubject.setText("Select Subject");

            dialogBinding.edtSelectSubject.setOnClickListener(view -> {
                if (dialogSelectedClassId == -1) {
                    Toast.makeText(this, "Please select a class first", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check if adapter has subjects (more than just "Select Subject")
                ArrayAdapter<String> adapter = (ArrayAdapter<String>) dialogBinding.edtSelectSubject.getAdapter();
                if (adapter == null || adapter.getCount() <= 1) {
                    Toast.makeText(this, "No subjects available for selected class", Toast.LENGTH_SHORT).show();
                } else {
                    dialogBinding.edtSelectSubject.showDropDown();
                }
            });

            // Setup subject selection listener
            dialogBinding.edtSelectSubject.setOnItemClickListener((parent, view, position, id) -> {
                if (position == 0) {
                    // "Select Subject" is selected
                    dialogSelectedSubjectId = -1;
                    dialogSelectedSubjectName = "";
                    dialogBinding.edtSelectSubject.setText("Select Subject");
                    return;
                }

                if (position - 1 < subjectList.size()) {
                    SubjectModel selectedSubject = subjectList.get(position - 1); // -1 because first item is "Select Subject"
                    dialogSelectedSubjectId = selectedSubject.getId();
                    dialogSelectedSubjectName = selectedSubject.getName();

                    // Set the name in the EditText
                    dialogBinding.edtSelectSubject.setText(dialogSelectedSubjectName);

                    // Log for debugging
                    android.util.Log.d("DialogSubjectSelection", "Selected Subject: " + dialogSelectedSubjectName + " (ID: " + dialogSelectedSubjectId + ")");
                }
            });
        }
    }

    private void loadSubjectsForSelectedClass(AddHomeworkDialogBinding dialogBinding, int classId) {
        // Show loading state
        if (dialogBinding.edtSelectSubject != null) {
            dialogBinding.edtSelectSubject.setText("Loading subjects...");
            dialogBinding.edtSelectSubject.setEnabled(false);
        }

        subjectApiHelper.fetchSubjectsForClass(classId, new SubjectApiHelper.SubjectListCallback() {
            @Override
            public void onSubjectsSuccess(ArrayList<SubjectModel> subjects) {
                // Update the subject list
                subjectList.clear();
                subjectList.addAll(subjects);

                // Prepare display names
                ArrayList<String> displaySubjectNames = new ArrayList<>();

                if (!subjects.isEmpty()) {
                    displaySubjectNames.addAll(subjectApiHelper.getSubjectNames(subjects));
                }

                // Update the adapter
                if (dialogBinding.edtSelectSubject != null) {
                    ArrayAdapter<String> subjectAdapter = new ArrayAdapter<>(
                            getApplicationContext(),
                            android.R.layout.simple_dropdown_item_1line,
                            displaySubjectNames
                    );
                    dialogBinding.edtSelectSubject.setAdapter(subjectAdapter);
                    dialogBinding.edtSelectSubject.setThreshold(1000); // Prevent filtering
                    dialogBinding.edtSelectSubject.setText("Select Subject");
                    dialogBinding.edtSelectSubject.setEnabled(true);
                }

                // Reset selected subject
                dialogSelectedSubjectId = -1;
                dialogSelectedSubjectName = "";

                android.util.Log.d("SubjectLoader", "Loaded " + subjects.size() + " subjects for class ID: " + classId);
            }

            @Override
            public void onSubjectsError(String errorMessage) {
                Toast.makeText(getApplicationContext(), "Error loading subjects: " + errorMessage, Toast.LENGTH_SHORT).show();

                // Clear subjects and reset UI
                clearSubjectDropdown(dialogBinding);

                android.util.Log.e("SubjectLoader", "Error loading subjects: " + errorMessage);
            }

            @Override
            public void onSubjectsLoading(boolean isLoading) {
                // You can show/hide a progress indicator here if needed
                android.util.Log.d("SubjectLoader", "Loading subjects: " + isLoading);
            }
        });
    }

    private void clearSubjectDropdown(AddHomeworkDialogBinding dialogBinding) {
        subjectList.clear();

        // Create clean list with only "Select Subject"
        ArrayList<String> cleanSubjectNames = new ArrayList<>();
        cleanSubjectNames.add("Select Subject");

        if (dialogBinding.edtSelectSubject != null) {
            ArrayAdapter<String> emptyAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, cleanSubjectNames);
            dialogBinding.edtSelectSubject.setAdapter(emptyAdapter);
            dialogBinding.edtSelectSubject.setThreshold(1000); // Prevent filtering
            dialogBinding.edtSelectSubject.setText("Select Subject");
            dialogBinding.edtSelectSubject.setEnabled(true);
        }

        dialogSelectedSubjectId = -1;
        dialogSelectedSubjectName = "";
    }

    private boolean validateHomeworkForm(AddHomeworkDialogBinding dialogBinding) {
        if (dialogBinding.edtHomeworkDate.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please select homework date", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (dialogSelectedClassId == -1) {
            Toast.makeText(this, "Please select class", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (dialogSelectedTeacherId.isEmpty()) {
            Toast.makeText(this, "Please select teacher", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (dialogSelectedSubjectId == -1) {
            Toast.makeText(this, "Please select subject", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (dialogBinding.edtHomeworkDetails != null && dialogBinding.edtHomeworkDetails.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter homework details", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void addNewHomework(AddHomeworkDialogBinding dialogBinding) {
        CreateHomework homeworkDetails = new CreateHomework();
        homeworkDetails.setHomeworkDate(dialogBinding.edtHomeworkDate.getText().toString());
        homeworkDetails.setClassId(dialogSelectedClassId);
        homeworkDetails.setSubjectId(dialogSelectedSubjectId);
        homeworkDetails.setTeacherId(Integer.parseInt(dialogSelectedTeacherId));
        homeworkDetails.setDetails(dialogBinding.edtHomeworkDetails.getText().toString());

String auth= "Bearer "+pref.getPrefString(this,pref.user_token);
        // Here you would typically make an API call with class ID, teacher ID, and subject ID
     makeHomeworkApiCall(dialogBinding,auth,homeworkDetails);
    }

    // Getter methods for selected IDs
    public int getSelectedClassId() {
        return selectedClassId;
    }

    public String getSelectedTeacherId() {
        return selectedTeacherId;
    }

    public int getDialogSelectedClassId() {
        return dialogSelectedClassId;
    }

    public String getDialogSelectedTeacherId() {
        return dialogSelectedTeacherId;
    }

    // Updated API call method to include both class and teacher IDs
    private void makeHomeworkApiCall(AddHomeworkDialogBinding dialogBinding,String auth, CreateHomework createHomework) {
        binding.loader.rlLoader.setVisibility(View.VISIBLE);
        viewModel.createHomework(auth, createHomework).observe(this, response -> {
            binding.loader.rlLoader.setVisibility(View.GONE);

            if (response != null && response.isSuccess) {
                // Show success message
                Toast.makeText(this, response.message != null ? response.message : "Homework created successfully", Toast.LENGTH_SHORT).show();

                // Clear dialog fields
                clearDialogFields(dialogBinding);
                refreshHomework();

            } else {
                // Show error message
                String errorMessage = response != null && response.message != null
                        ? response.message
                        : "Failed to create homework. Please try again.";
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void clearDialogFields(AddHomeworkDialogBinding dialogBinding) {
        if ( dialogBinding!= null) {
            // Clear all input fields
            if (dialogBinding.edtSelectClass != null) {
                dialogBinding.edtSelectClass.setText("Select Class");
            }
            if (dialogBinding.edtSelectTeacher != null) {
                dialogBinding.edtSelectTeacher.setText("Select Teacher");
            }
            if (dialogBinding.edtSelectSubject != null) {
                dialogBinding.edtSelectSubject.setText("Select Subject");
            }
            if (dialogBinding.edtHomeworkDate != null) {
                dialogBinding.edtHomeworkDate.setText("");
            }
            if (dialogBinding.edtHomeworkDetails != null) {
                dialogBinding.edtHomeworkDetails.setText("");
            }

            // Reset selected values
            dialogSelectedClassId = -1;
            dialogSelectedClassName = "";
            dialogSelectedTeacherId = "";
            dialogSelectedTeacherName = "";
            dialogSelectedSubjectId = -1;
            dialogSelectedSubjectName = "";

            // Clear subject dropdown since no class is selected
            clearSubjectDropdown(dialogBinding);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}