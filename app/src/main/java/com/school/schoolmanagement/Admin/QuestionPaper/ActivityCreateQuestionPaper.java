package com.school.schoolmanagement.Admin.QuestionPaper;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
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

import com.school.schoolmanagement.Adapters.QuestionPaperAdapter;
import com.school.schoolmanagement.Admin.Model.ClassModel;
import com.school.schoolmanagement.Admin.Model.CreateQuestionPaper;
import com.school.schoolmanagement.Admin.Model.ExamModel;
import com.school.schoolmanagement.Admin.Model.SubjectModel;
import com.school.schoolmanagement.Admin.Model.QuestionPaperResponse;
import com.school.schoolmanagement.GlobalViewModel.ViewModel;
import com.school.schoolmanagement.HelperClasses.ClassApiHelper;
import com.school.schoolmanagement.HelperClasses.DataExportHelper;
import com.school.schoolmanagement.HelperClasses.ExamApiHelper;
import com.school.schoolmanagement.HelperClasses.QuestionPaperApiHelper;
import com.school.schoolmanagement.HelperClasses.SubjectApiHelper;
import com.school.schoolmanagement.Model.EmployeeResponse;
import com.school.schoolmanagement.R;
import com.school.schoolmanagement.Utils.Utility;
import com.school.schoolmanagement.databinding.ActivityCreateQuestionPaperBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class ActivityCreateQuestionPaper extends Utility implements
        QuestionPaperApiHelper.QuestionPaperCreationCallback,
        QuestionPaperApiHelper.QuestionPaperFetchCallback , QuestionPaperApiHelper.QuestionPaperUpdateCallback {

    private ActivityCreateQuestionPaperBinding binding;

    // Helper classes
    private ClassApiHelper classApiHelper;
    private SubjectApiHelper subjectApiHelper;
    private int currentEditingQuestionPaperId = -1;
    private boolean isEditMode = false;
    private ExamApiHelper examApiHelper;
    private QuestionPaperAdapter adapter;
    private QuestionPaperApiHelper questionPaperApiHelper;

    // ViewModel
    private ViewModel viewModel;

    // Data lists
    private ArrayList<ClassModel.Data> classList = new ArrayList<>();
    private ArrayList<SubjectModel> subjectList = new ArrayList<>();
    private ArrayList<ExamModel.Datum> examList = new ArrayList<>();
    private ArrayList<QuestionPaperResponse.Datum> questionPaperList = new ArrayList<>();

    // Name lists for adapters
    private ArrayList<String> classNames = new ArrayList<>();
    private ArrayList<Integer> classIds = new ArrayList<>();
    private ArrayList<String> subjectNames = new ArrayList<>();
    private ArrayList<String> examNames = new ArrayList<>();

    // Adapters
    private ArrayAdapter<String> classAdapter;
    private ArrayAdapter<String> subjectAdapter;
    private ArrayAdapter<String> examAdapter;

    // Selected values
    private int selectedClassId = -1;
    private int selectedSubjectId = -1;
    private int selectedExamId = -1;

    // Date and time
    private Calendar selectedDate = Calendar.getInstance();
    private Calendar startTime = Calendar.getInstance();
    private Calendar endTime = Calendar.getInstance();

    // Progress dialog
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityCreateQuestionPaperBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeComponents();
        setupUI();
        loadInitialData();
        setupQuestionPaperAdapter();
        loadQuestionPapers();
    }

    private void initializeComponents() {
        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(ViewModel.class);

        // Initialize API helpers
        classApiHelper = new ClassApiHelper(this);
        subjectApiHelper = new SubjectApiHelper(this);
        examApiHelper = new ExamApiHelper(this);
        questionPaperApiHelper = new QuestionPaperApiHelper(viewModel, this);

        // Set callbacks for question paper operations
        questionPaperApiHelper.setCreationCallback(this);
        questionPaperApiHelper.setFetchCallback(this);
        questionPaperApiHelper.setUpdateCallback(this);

        // Initialize progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
    }

    private void setupUI() {
        setupToolbar();
        setupDropdowns();
        setupDatePicker();
        setupTimePickers();
        setupCreateButton();
        setupAutoMarkSwitch();
        setupExportButtons();
    }

    private void setupQuestionPaperAdapter() {
        // Initialize the adapter with empty list
        adapter = new QuestionPaperAdapter(this, questionPaperList);

        // If you have a RecyclerView for displaying question papers
        if (binding.rcRowAttendanceReport != null) {
            binding.rcRowAttendanceReport.setAdapter(adapter);
        }

        // Set up adapter click listeners for edit/delete operations
        adapter.setOnItemClickListener(new QuestionPaperAdapter.OnItemClickListener() {
            @Override
            public void onEditClick(QuestionPaperResponse.Datum questionPaper, int position) {
                editQuestionPaper(questionPaper);
            }

            @Override
            public void onDeleteClick(QuestionPaperResponse.Datum questionPaper, int position) {
                deleteQuestionPaper(questionPaper);
            }
        });
    }

    private void setupToolbar() {
       binding.toolbar.backBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               finish();
           }
       });
    }

    private void setupAutoMarkSwitch() {
        // Set default value for auto mark switch
        binding.switchAutoMark.setChecked(true);
    }

    private void setupDropdowns() {
        setupExamDropdown();
        setupClassDropdown();
        setupSubjectDropdown();
    }

    private void setupExamDropdown() {
        // Initialize with empty list first
        examNames.clear();
        examNames.add("Select Exam");

        examAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, examNames);
        examAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.edtExamTypePaper.setAdapter(examAdapter);

        binding.edtExamTypePaper.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) { // Skip "Select Exam" option
                    // Adjust position since we have "Select Exam" at index 0
                    int actualPosition = position - 1; // because "Select Exam" is at index 0
                    if (actualPosition < examList.size()) {
                        selectedExamId = examList.get(actualPosition).getExamId();
                    }
                } else {
                    selectedExamId = -1;
                }
            }
        });

        // Handle focus change to show dropdown
        binding.edtExamTypePaper.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                binding.edtExamTypePaper.showDropDown();
            }
        });

        // Handle click to show dropdown
        binding.edtExamTypePaper.setOnClickListener(v -> {
            binding.edtExamTypePaper.showDropDown();
        });
    }

    private void setupClassDropdown() {
        // Initialize with empty list first
        classNames.clear();
        classNames.add("Select Class");

        classAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, classNames);
        classAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.edtClassesPaper.setAdapter(classAdapter);

        // Set threshold to show dropdown on click
        binding.edtClassesPaper.setThreshold(1);

        binding.edtClassesPaper.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) { // Skip "Select Class" option
                    // Adjust position since we have "Select Class" at index 0
                    int actualPosition = position - 1;
                    if (actualPosition < classList.size()) {
                        selectedClassId = classList.get(actualPosition).getClassId();
                        loadSubjectsForClass(selectedClassId);

                        // Reset subject selection
                        selectedSubjectId = -1;
                        binding.edtSubjectPaper.setText("", false);
                    }
                } else {
                    selectedClassId = -1;
                    clearSubjects();
                }
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

    private void setupSubjectDropdown() {
        subjectNames.clear();
        subjectNames.add("Select Subject");

        subjectAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, subjectNames);
        subjectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.edtSubjectPaper.setAdapter(subjectAdapter);

        // Set threshold to show dropdown on click
        binding.edtSubjectPaper.setThreshold(1);

        // Initially disable subject dropdown
        binding.edtSubjectPaper.setEnabled(false);

        binding.edtSubjectPaper.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) { // Skip "Select Subject" option
                    // Adjust position since we have "Select Subject" at index 0
                    int actualPosition = position - 1;
                    if (actualPosition < subjectList.size()) {
                        selectedSubjectId = subjectList.get(actualPosition).getId();
                    }
                } else {
                    selectedSubjectId = -1;
                }
            }
        });

        // Handle focus change to show dropdown
        binding.edtSubjectPaper.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus && binding.edtSubjectPaper.isEnabled()) {
                binding.edtSubjectPaper.showDropDown();
            }
        });

        // Handle click to show dropdown
        binding.edtSubjectPaper.setOnClickListener(v -> {
            if (binding.edtSubjectPaper.isEnabled()) {
                binding.edtSubjectPaper.showDropDown();
            }
        });
    }

    private void setupDatePicker() {
        binding.edtDate.setOnClickListener(v -> {
            // Clear focus to prevent keyboard issues
            binding.edtDate.clearFocus();
            showDatePicker();
        });

        // Prevent keyboard from showing
        binding.edtDate.setFocusable(false);
        binding.edtDate.setClickable(true);
    }

    private void setupTimePickers() {
        binding.edtStartTime.setOnClickListener(v -> {
            // Clear focus to prevent keyboard issues
            binding.edtStartTime.clearFocus();
            showStartTimePicker();
        });

        binding.edtEndTime.setOnClickListener(v -> {
            // Clear focus to prevent keyboard issues
            binding.edtEndTime.clearFocus();
            showEndTimePicker();
        });

        // Prevent keyboard from showing
        binding.edtStartTime.setFocusable(false);
        binding.edtStartTime.setClickable(true);
        binding.edtEndTime.setFocusable(false);
        binding.edtEndTime.setClickable(true);
    }

    private void setupCreateButton() {
        binding.btnCreatePaper.setOnClickListener(v -> {
            if (validateForm()) {
                if(isEditMode){
editPaper();
                }else{
                    createQuestionPaper();
                }

            }
        });
    }

    private void loadInitialData() {
        loadClasses();
        loadExams();
    }

    private void loadQuestionPapers() {
        String authToken = "Bearer " + pref.getPrefString(this, pref.user_token);

        if (authToken == null || authToken.equals("Bearer ")) {
            Toast.makeText(this, "Authentication token not found. Please login again.", Toast.LENGTH_SHORT).show();
            return;
        }

        questionPaperApiHelper.getQuestionPaper(authToken);
    }

    private void loadClasses() {
        classApiHelper.fetchAllClasses(new ClassApiHelper.ClassListCallback() {
            @Override
            public void onSuccess(ArrayList<ClassModel.Data> classes) {
                classList.clear();
                classList.addAll(classes);

                classNames.clear();
                classNames.add("Select Class");

                // Add class names to the list
                for (ClassModel.Data classData : classList) {
                    classNames.add(classData.getClassName()); // Adjust method name as needed
                }

                classAdapter.notifyDataSetChanged();

                // Reset subject dropdown when classes are loaded
                clearSubjects();
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(ActivityCreateQuestionPaper.this, "Error loading classes: " + errorMessage, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLoading(boolean isLoading) {
                // Show/hide loading indicator if needed
            }
        });
    }

    private void loadExams() {
        examApiHelper.fetchValidExams(new ExamApiHelper.ExamListCallback() {

            @Override
            public void onExamSuccess(ArrayList<ExamModel.Datum> exams) {
                // Clear and store the exam list - THIS WAS MISSING
                examList.clear();
                examList.addAll(exams);

                examNames.clear();
                examNames.add("Select Exam");

                for (ExamModel.Datum examData : exams) {
                    examNames.add(examData.getExaminationName()); // Use correct getter
                }

                examAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(ActivityCreateQuestionPaper.this, "Error loading exams: " + errorMessage, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLoading(boolean isLoading) {
                // Optionally show a loading dialog
            }
        });
    }

    private void loadSubjectsForClass(int classId) {
        subjectApiHelper.fetchSubjectsForClass(classId, new SubjectApiHelper.SubjectListCallback() {
            @Override
            public void onSubjectsSuccess(ArrayList<SubjectModel> subjects) {
                subjectList.clear();
                subjectList.addAll(subjects);

                subjectNames.clear();
                subjectNames.add("Select Subject");

                // Add subject names to the list
                for (SubjectModel subject : subjectList) {
                    subjectNames.add(subject.getName()); // Adjust method name as needed
                }

                subjectAdapter.notifyDataSetChanged();

                // Enable subject dropdown
                binding.edtSubjectPaper.setEnabled(true);
            }

            @Override
            public void onSubjectsError(String errorMessage) {
                Toast.makeText(ActivityCreateQuestionPaper.this, "Error loading subjects: " + errorMessage, Toast.LENGTH_SHORT).show();
                clearSubjects();
            }

            @Override
            public void onSubjectsLoading(boolean isLoading) {
                // Show/hide loading indicator if needed
            }
        });
    }

    private void clearSubjects() {
        subjectList.clear();
        subjectNames.clear();
        subjectNames.add("Select Subject");
        subjectAdapter.notifyDataSetChanged();
        binding.edtSubjectPaper.setEnabled(false);
        binding.edtSubjectPaper.setText("", false);
        selectedSubjectId = -1;
    }

    private void showDatePicker() {
        Calendar currentDate = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    selectedDate.set(Calendar.YEAR, year);
                    selectedDate.set(Calendar.MONTH, month);
                    selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    binding.edtDate.setText(dateFormat.format(selectedDate.getTime()));
                },
                currentDate.get(Calendar.YEAR),
                currentDate.get(Calendar.MONTH),
                currentDate.get(Calendar.DAY_OF_MONTH)
        );

        // Set minimum date to today (disable past dates)
        datePickerDialog.getDatePicker().setMinDate(currentDate.getTimeInMillis());

        datePickerDialog.show();
    }

    private void showStartTimePicker() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> {
                    startTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    startTime.set(Calendar.MINUTE, minute);

                    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                    binding.edtStartTime.setText(timeFormat.format(startTime.getTime()));

                    // Clear end time if start time is changed
                    if (!binding.edtEndTime.getText().toString().isEmpty()) {
                        binding.edtEndTime.setText("");
                    }
                },
                startTime.get(Calendar.HOUR_OF_DAY),
                startTime.get(Calendar.MINUTE),
                true // Use 24-hour format for consistency
        );

        timePickerDialog.show();
    }

    private void showEndTimePicker() {
        if (binding.edtStartTime.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please select start time first", Toast.LENGTH_SHORT).show();
            return;
        }

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> {
                    endTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    endTime.set(Calendar.MINUTE, minute);

                    // Validate that end time is after start time
                    if (endTime.getTimeInMillis() <= startTime.getTimeInMillis()) {
                        Toast.makeText(this, "End time must be after start time", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                    binding.edtEndTime.setText(timeFormat.format(endTime.getTime()));
                },
                endTime.get(Calendar.HOUR_OF_DAY),
                endTime.get(Calendar.MINUTE),
                true // Use 24-hour format for consistency
        );

        timePickerDialog.show();
    }

    private boolean validateForm() {
        // Validate Exam Type
        if (selectedExamId == -1 || binding.edtExamTypePaper.getText().toString().equals("Select Exam")) {
            Toast.makeText(this, "Please select an exam ", Toast.LENGTH_SHORT).show();
            binding.edtExamTypePaper.requestFocus();
            return false;
        }

        // Validate Class
        if (selectedClassId == -1 || binding.edtClassesPaper.getText().toString().equals("Select Class")) {
            Toast.makeText(this, "Please select class", Toast.LENGTH_SHORT).show();
            binding.edtClassesPaper.requestFocus();
            return false;
        }

        // Validate Subject
        if (selectedSubjectId == -1 || binding.edtSubjectPaper.getText().toString().equals("Select Subject")) {
            Toast.makeText(this, "Please select subject", Toast.LENGTH_SHORT).show();
            binding.edtSubjectPaper.requestFocus();
            return false;
        }

        // Validate Date
        if (TextUtils.isEmpty(binding.edtDate.getText().toString())) {
            Toast.makeText(this, "Please select exam date", Toast.LENGTH_SHORT).show();
            binding.edtDate.requestFocus();
            return false;
        }

        // Validate Start Time
        if (TextUtils.isEmpty(binding.edtStartTime.getText().toString())) {
            Toast.makeText(this, "Please select start time", Toast.LENGTH_SHORT).show();
            binding.edtStartTime.requestFocus();
            return false;
        }

        // Validate End Time
        if (TextUtils.isEmpty(binding.edtEndTime.getText().toString())) {
            Toast.makeText(this, "Please select end time", Toast.LENGTH_SHORT).show();
            binding.edtEndTime.requestFocus();
            return false;
        }

        // Validate Minimum Percentage
        String minPercentageStr = binding.edtMinimumPercentage.getText().toString().trim();
        if (TextUtils.isEmpty(minPercentageStr)) {
            Toast.makeText(this, "Please enter minimum percentage", Toast.LENGTH_SHORT).show();
            binding.edtMinimumPercentage.requestFocus();
            return false;
        }

        try {
            double minPercentage = Double.parseDouble(minPercentageStr);
            if (minPercentage < 0 || minPercentage > 100) {
                Toast.makeText(this, "Minimum percentage must be between 0 and 100", Toast.LENGTH_SHORT).show();
                binding.edtMinimumPercentage.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter a valid percentage", Toast.LENGTH_SHORT).show();
            binding.edtMinimumPercentage.requestFocus();
            return false;
        }

        // Validate Instruction
        if (TextUtils.isEmpty(binding.edtInstruction.getText().toString().trim())) {
            Toast.makeText(this, "Please enter instructions", Toast.LENGTH_SHORT).show();
            binding.edtInstruction.requestFocus();
            return false;
        }

        return true;
    }

    private void createQuestionPaper() {
        try {
            // Create the question paper object
            CreateQuestionPaper createQuestionPaper = new CreateQuestionPaper();

            // Set all the required fields (adjust these based on your CreateQuestionPaper model)
            createQuestionPaper.setExamId(selectedExamId);
            classIds.clear();
            classIds.add(selectedClassId);
            createQuestionPaper.setClassIds(classIds);
            createQuestionPaper.setSubjectId(selectedSubjectId);
            createQuestionPaper.setDate(binding.edtDate.getText().toString());
            createQuestionPaper.setStartTime(binding.edtStartTime.getText().toString());
            createQuestionPaper.setEndTime(binding.edtEndTime.getText().toString());
            createQuestionPaper.setMinimumPercentage((int) Double.parseDouble(binding.edtMinimumPercentage.getText().toString()));
            createQuestionPaper.setInstructions(binding.edtInstruction.getText().toString().trim());

            // Get auth token from SharedPreferences
            String authToken = "Bearer " + pref.getPrefString(this, pref.user_token); // Adjust method name as needed

            if (authToken == null || authToken.equals("Bearer ")) {
                Toast.makeText(this, "Authentication token not found. Please login again.", Toast.LENGTH_SHORT).show();
                // Navigate to login activity or handle accordingly
                return;
            }

            // Make API call using the helper
            questionPaperApiHelper.createQuestionPaper(authToken, createQuestionPaper);

        } catch (Exception e) {
            Toast.makeText(this, "Error creating question paper: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    private void editPaper() {
        try {
            // Create the question paper object
            CreateQuestionPaper createQuestionPaper = new CreateQuestionPaper();

            // Set all the required fields (adjust these based on your CreateQuestionPaper model)
            createQuestionPaper.setExamId(selectedExamId);
            classIds.clear();
            classIds.add(selectedClassId);
            createQuestionPaper.setClassIds(classIds);
            createQuestionPaper.setSubjectId(selectedSubjectId);
            createQuestionPaper.setDate(binding.edtDate.getText().toString());
            createQuestionPaper.setStartTime(binding.edtStartTime.getText().toString());
            createQuestionPaper.setEndTime(binding.edtEndTime.getText().toString());
            createQuestionPaper.setMinimumPercentage((int) Double.parseDouble(binding.edtMinimumPercentage.getText().toString()));
            createQuestionPaper.setInstructions(binding.edtInstruction.getText().toString().trim());

            // Get auth token from SharedPreferences
            String authToken = "Bearer " + pref.getPrefString(this, pref.user_token); // Adjust method name as needed

            if (authToken == null || authToken.equals("Bearer ")) {
                Toast.makeText(this, "Authentication token not found. Please login again.", Toast.LENGTH_SHORT).show();
                // Navigate to login activity or handle accordingly
                return;
            }

            // Make API call using the helper
            questionPaperApiHelper.updateQuestionPaper(authToken,currentEditingQuestionPaperId, createQuestionPaper);

        } catch (Exception e) {
            Toast.makeText(this, "Error creating question paper: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // Question Paper CRUD operations
    private void editQuestionPaper(QuestionPaperResponse.Datum questionPaper) {
        Log.d("EditQuestionPaper", "editQuestionPaper: Starting to edit question paper with ID: " + questionPaper.getId());

        // Show loading
        if (progressDialog != null && !progressDialog.isShowing()) {
            progressDialog.setMessage("Loading question paper details...");
            progressDialog.show();
        }

        // Populate the form with the question paper data
        populateFormWithQuestionPaper(questionPaper);

        // Change button text to indicate edit mode
        binding.btnCreate.setText("Update Question Paper");

        // Store the question paper ID for update operation
        currentEditingQuestionPaperId = questionPaper.getId();
        isEditMode = true;

        // Hide loading
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

        Toast.makeText(this, "Question paper loaded for editing", Toast.LENGTH_SHORT).show();
    }

    private void populateFormWithQuestionPaper(QuestionPaperResponse.Datum questionPaper) {
        Log.d("EditQuestionPaper", "populateFormWithQuestionPaper: Populating form with question paper data");
        Log.d("EditQuestionPaper", "Question Paper details - ID: " + questionPaper.getId() +
                ", Class: " + questionPaper.getClassNames() +
                ", Subject: " + questionPaper.getSubject() +
                ", Exam: " + questionPaper.getTitle());

        // Set exam
        for (int i = 0; i < examList.size(); i++) {
            if (Objects.equals(examList.get(i).getExaminationName(), questionPaper.getTitle())) {
                selectedExamId = examList.get(i).getExamId();
                binding.edtExamTypePaper.setText(examNames.get(i + 1)); // +1 because "Select Exam" is at index 0
                Log.d("EditQuestionPaper", "populateFormWithQuestionPaper: Exam set - " + questionPaper.getTitle() + " (ID: " + selectedExamId + ")");
                break;
            }
        }

        // Set class
        for (int i = 0; i < classList.size(); i++) {
            if (Objects.equals(classList.get(i).getClassName(), questionPaper.getClassNames())){
                selectedClassId = classList.get(i).getClassId();
                binding.edtClassesPaper.setText(classNames.get(i + 1)); // +1 because "Select Class" is at index 0
                Log.d("EditQuestionPaper", "populateFormWithQuestionPaper: Class set - " + questionPaper.getClassNames() + " (ID: " + selectedClassId + ")");
                break;
            }
        }

        // Load subjects for the selected class, then set the selected subject
        loadSubjectsForEdit(questionPaper.getSubject(), questionPaper.getId());

        // Set date
        binding.edtDate.setText(questionPaper.getExamDate());

        String duration = questionPaper.getDuration(); // e.g., "09:30 - 11:00"

        String startDate = "";
        String endDate = "";

        if (duration != null && duration.contains(" - ")) {
            String[] parts = duration.split(" - ");
            if (parts.length == 2) {
                startDate = parts[0].trim();
                endDate = parts[1].trim();
            }
        }

// Set start time
        binding.edtStartTime.setText(startDate);

// Set end time
        binding.edtEndTime.setText(endDate);

        // Set minimum percentage
        binding.edtMinimumPercentage.setText(String.valueOf(questionPaper.getMinimumPercentage()));

        // Set instructions
        binding.edtInstruction.setText(questionPaper.getInstructions());

        Log.d("EditQuestionPaper", "populateFormWithQuestionPaper: Form populated successfully");
    }

    private void loadSubjectsForEdit(String subjectName, int subjectId) {
        if (selectedClassId == -1) {
            Log.e("EditQuestionPaper", "loadSubjectsForEdit: No class selected");
            return;
        }

        subjectApiHelper.fetchSubjectsForClass(selectedClassId, new SubjectApiHelper.SubjectListCallback() {
            @Override
            public void onSubjectsSuccess(ArrayList<SubjectModel> subjects) {
                subjectList.clear();
                subjectList.addAll(subjects);

                subjectNames.clear();
                subjectNames.add("Select Subject");

                // Add subject names to the list
                for (SubjectModel subject : subjectList) {
                    subjectNames.add(subject.getName());
                }

                subjectAdapter.notifyDataSetChanged();

                // Enable subject dropdown
                binding.edtSubjectPaper.setEnabled(true);

                // Set the selected subject after subjects are loaded
                for (int i = 0; i < subjectList.size(); i++) {
                    if (Objects.equals(subjectList.get(i).getName(), subjectName) ||
                            subjectList.get(i).getId() == subjectId) {
                        selectedSubjectId = subjectList.get(i).getId();
                        binding.edtSubjectPaper.setText(subjectNames.get(i + 1)); // +1 because "Select Subject" is at index 0
                        Log.d("EditQuestionPaper", "loadSubjectsForEdit: Subject set - " + subjectName + " (ID: " + selectedSubjectId + ")");
                        break;
                    }
                }
            }

            @Override
            public void onSubjectsError(String errorMessage) {
                Toast.makeText(ActivityCreateQuestionPaper.this, "Error loading subjects: " + errorMessage, Toast.LENGTH_SHORT).show();
                Log.e("EditQuestionPaper", "loadSubjectsForEdit: Error - " + errorMessage);
            }

            @Override
            public void onSubjectsLoading(boolean isLoading) {
                // Handle loading state if needed
            }
        });
    }

    private void deleteQuestionPaper(QuestionPaperResponse.Datum questionPaper) {
        // Show confirmation dialog before deletion
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Delete Question Paper");
        builder.setMessage("Are you sure you want to delete this question paper?");
        builder.setPositiveButton("Delete", (dialog, which) -> {
            String authToken = "Bearer " + pref.getPrefString(this, pref.user_token);

            // Set the delete callback
            questionPaperApiHelper.setDeleteCallback(new QuestionPaperApiHelper.QuestionPaperDeleteCallback() {
                @Override
                public void onQuestionPaperDeleteSuccess(EmployeeResponse response) {
                    Toast.makeText(ActivityCreateQuestionPaper.this, "Question paper deleted successfully!", Toast.LENGTH_SHORT).show();
                    // Refresh the list
                    loadQuestionPapers();
                }

                @Override
                public void onQuestionPaperDeleteError(String errorMessage, int errorCode) {
                    Toast.makeText(ActivityCreateQuestionPaper.this, errorMessage, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onQuestionPaperDeleteNetworkFailure() {
                    Toast.makeText(ActivityCreateQuestionPaper.this, "Network error. Please try again.", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void showQuestionPaperDeleteLoading() {
                    if (progressDialog != null && !progressDialog.isShowing()) {
                        progressDialog.setMessage("Deleting question paper...");
                        progressDialog.show();
                    }
                }

                @Override
                public void hideQuestionPaperDeleteLoading() {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                }
            });

            // Assuming questionPaper has an ID field
            questionPaperApiHelper.deleteQuestionPaper(authToken, questionPaper.getId());
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    // Implementation of QuestionPaperCreationCallback methods
    @Override
    public void onQuestionPaperCreationSuccess(EmployeeResponse response) {
        Toast.makeText(this, "Question paper created successfully!", Toast.LENGTH_LONG).show();

        // Optionally clear the form
        clearForm();

        // Refresh the question papers list
        loadQuestionPapers();

        // Navigate back to previous screen or to question paper list
        setResult(RESULT_OK);
    }

    @Override
    public void onQuestionPaperCreationError(String errorMessage, int errorCode) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();

        // Handle specific error codes if needed
        if (QuestionPaperApiHelper.isAuthError(errorCode)) {
            // Handle authentication error - maybe redirect to login
            Toast.makeText(this, "Please login again", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onQuestionPaperCreationNetworkFailure() {
        Toast.makeText(this, "Network error. Please check your internet connection and try again.", Toast.LENGTH_LONG).show();
    }

    @Override
    public void showQuestionPaperCreationLoading() {
        if (progressDialog != null && !progressDialog.isShowing()) {
            progressDialog.setMessage("Creating question paper...");
            progressDialog.show();
        }

        // Disable the create button to prevent multiple submissions
        binding.btnCreatePaper.setEnabled(false);
    }

    @Override
    public void hideQuestionPaperCreationLoading() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

        // Re-enable the create button
        binding.btnCreatePaper.setEnabled(true);
    }

    // Implementation of QuestionPaperFetchCallback methods
    @Override
    public void onQuestionPaperFetchSuccess(QuestionPaperResponse response) {
        if (response != null && response.getData() != null) {
            questionPaperList.clear();
            questionPaperList.addAll(response.getData());

            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }

            Toast.makeText(this, "Question papers loaded successfully!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No question papers found!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onQuestionPaperFetchError(String errorMessage, int errorCode) {
        Toast.makeText(this, "Error loading question papers: " + errorMessage, Toast.LENGTH_SHORT).show();

        if (QuestionPaperApiHelper.isAuthError(errorCode)) {
            Toast.makeText(this, "Please login again", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onQuestionPaperFetchNetworkFailure() {
        Toast.makeText(this, "Network error. Please check your internet connection.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showQuestionPaperFetchLoading() {
        if (progressDialog != null && !progressDialog.isShowing()) {
            progressDialog.setMessage("Loading question papers...");
            progressDialog.show();
        }
    }

    @Override
    public void hideQuestionPaperFetchLoading() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private void clearForm() {
        // Reset all form fields to their default state
        binding.edtExamTypePaper.setText("", false);
        binding.edtClassesPaper.setText("", false);
        binding.edtSubjectPaper.setText("", false);
        binding.edtDate.setText("");
        binding.edtStartTime.setText("");
        binding.edtEndTime.setText("");
        binding.edtMinimumPercentage.setText("");
        binding.edtInstruction.setText("");

        // Reset selected values
        selectedExamId = -1;
        selectedClassId = -1;
        selectedSubjectId = -1;

        // Reset date and time calendars to current
        selectedDate = Calendar.getInstance();
        startTime = Calendar.getInstance();
        endTime = Calendar.getInstance();

        // Reset auto mark switch to default
        binding.switchAutoMark.setChecked(true);

        // Clear and reset subject dropdown
        clearSubjects();
    }

    @Override
    public boolean onSupportNavigateUp() {
        // Handle back button in toolbar
        onBackPressed();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Dismiss progress dialog if showing to prevent window leak
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

        // Clear binding reference
        binding = null;
    }

    @Override
    public void onBackPressed() {
        // Check if any form fields are filled and show confirmation dialog
        if (isFormDirty()) {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
            builder.setTitle("Discard Changes");
            builder.setMessage("You have unsaved changes. Are you sure you want to go back?");
            builder.setPositiveButton("Discard", (dialog, which) -> {
                super.onBackPressed();
            });
            builder.setNegativeButton("Cancel", null);
            builder.show();
        } else {
            super.onBackPressed();
        }
    }

    private boolean isFormDirty() {
        // Check if any form field has been modified
        return !TextUtils.isEmpty(binding.edtExamTypePaper.getText().toString()) ||
                !TextUtils.isEmpty(binding.edtClassesPaper.getText().toString()) ||
                !TextUtils.isEmpty(binding.edtSubjectPaper.getText().toString()) ||
                !TextUtils.isEmpty(binding.edtDate.getText().toString()) ||
                !TextUtils.isEmpty(binding.edtStartTime.getText().toString()) ||
                !TextUtils.isEmpty(binding.edtEndTime.getText().toString()) ||
                !TextUtils.isEmpty(binding.edtMinimumPercentage.getText().toString()) ||
                !TextUtils.isEmpty(binding.edtInstruction.getText().toString());
    }

    // Optional: Method to handle orientation changes or configuration changes
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save current form state
        outState.putInt("selectedExamId", selectedExamId);
        outState.putInt("selectedClassId", selectedClassId);
        outState.putInt("selectedSubjectId", selectedSubjectId);
        outState.putString("selectedDate", binding.edtDate.getText().toString());
        outState.putString("startTime", binding.edtStartTime.getText().toString());
        outState.putString("endTime", binding.edtEndTime.getText().toString());
        outState.putString("minPercentage", binding.edtMinimumPercentage.getText().toString());
        outState.putString("instructions", binding.edtInstruction.getText().toString());
        outState.putBoolean("autoMark", binding.switchAutoMark.isChecked());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // Restore form state after configuration change
        if (savedInstanceState != null) {
            selectedExamId = savedInstanceState.getInt("selectedExamId", -1);
            selectedClassId = savedInstanceState.getInt("selectedClassId", -1);
            selectedSubjectId = savedInstanceState.getInt("selectedSubjectId", -1);

            binding.edtDate.setText(savedInstanceState.getString("selectedDate", ""));
            binding.edtStartTime.setText(savedInstanceState.getString("startTime", ""));
            binding.edtEndTime.setText(savedInstanceState.getString("endTime", ""));
            binding.edtMinimumPercentage.setText(savedInstanceState.getString("minPercentage", ""));
            binding.edtInstruction.setText(savedInstanceState.getString("instructions", ""));
            binding.switchAutoMark.setChecked(savedInstanceState.getBoolean("autoMark", true));
        }
    }

    // Optional: Method to handle menu options if you have any
    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        // Inflate menu if you have any menu items
        // getMenuInflater().inflate(R.menu.create_question_paper_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onQuestionPaperUpdateSuccess(EmployeeResponse response) {
  showToastGrey(this,response.getMessage());

        clearForm();

        // Refresh the question papers list
        loadQuestionPapers();

        // Navigate back to previous screen or to question paper list
        setResult(RESULT_OK);
    }

    @Override
    public void onQuestionPaperUpdateError(String errorMessage, int errorCode) {
        showToastGrey(this,errorMessage);
    }

    @Override
    public void onQuestionPaperUpdateNetworkFailure() {

    }

    @Override
    public void showQuestionPaperUpdateLoading() {

    }

    @Override
    public void hideQuestionPaperUpdateLoading() {

    }
    // Setup export button click listeners
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

    // Handle export functionality
    private void handleExport(ArrayList<ArrayList<String>> tableData, String action) {
        if (tableData.size() <= 1) { // Only headers, no data
            showToast("No data to export");
            return;
        }

        try {
            // Create DataExportHelper instance with context
            DataExportHelper exportHelper = new DataExportHelper(this);

            // Generate dynamic filename based on report type and selected criteria
            String fileName = generateDynamicFileName();

            // Use the exportData method with dynamic filename
            exportHelper.exportData(tableData, action, fileName);

        } catch (Exception e) {
            Log.e("QuestionPaperActivity", "Export error: " + e.getMessage());
        }
    }

    private void showToast(String noDataToExport) {
        Toast.makeText(this, noDataToExport, Toast.LENGTH_SHORT).show();
    }

    // Generate dynamic filename based on report type
    private String generateDynamicFileName() {
        String fileName = "question_paper_report";

        // Include selected class if available
        if (selectedClassId != -1) {
            // Find class name from classList
            for (ClassModel.Data classData : classList) {
                if (classData.getClassId() == selectedClassId) {
                    fileName += "_" + classData.getClassName().replace(" ", "_");
                    break;
                }
            }
        }

        // Include selected subject if available
        if (selectedSubjectId != -1) {
            // Find subject name from subjectList
            for (SubjectModel subject : subjectList) {
                if (subject.getId() == selectedSubjectId) {
                    fileName += "_" + subject.getName().replace(" ", "_");
                    break;
                }
            }
        }

        // Include selected exam if available
        if (selectedExamId != -1) {
            // Find exam name from examList
            for (ExamModel.Datum exam : examList) {
                if (exam.getExamId() == selectedExamId) {
                    fileName += "_" + exam.getExaminationName().replace(" ", "_");
                    break;
                }
            }
        }

        // Add timestamp for uniqueness
        fileName += "_" + System.currentTimeMillis();

        return fileName;
    }

    // Prepare table data for export based on XML layout structure
    private ArrayList<ArrayList<String>> prepareTableData() {
        ArrayList<ArrayList<String>> tableData = new ArrayList<>();

        // Add header row based on your XML layout
        ArrayList<String> headers = new ArrayList<>();
        headers.add("Sr");
        headers.add("Title");
        headers.add("Class");
        headers.add("Subject");
        headers.add("Exam Date");
        headers.add("Duration");
        tableData.add(headers);

        int count = 1;
        // Add data rows from questionPaperList
        for (QuestionPaperResponse.Datum questionPaper : questionPaperList) {
            ArrayList<String> row = new ArrayList<>();
            row.add(String.valueOf(count));

            // Adjust these field names based on your QuestionPaperResponse.Datum model structure
            row.add(questionPaper.getTitle() != null ? questionPaper.getTitle() : "");

            // Get class name from class ID
            String className = classNames.get(0);
            row.add(className);

            // Get subject name from subject ID
            String subjectName = "";
            if (questionPaper.getId() != -1) {
                for (SubjectModel subject : subjectList) {
                        subjectName = subject.getName();
                        break;

                }
            }
            row.add(subjectName);

            row.add(questionPaper.getExamDate() != null ? questionPaper.getExamDate() : "");
            row.add(questionPaper.getDuration() != null ? questionPaper.getDuration() : "");

            tableData.add(row);
            count++;
        }

        return tableData;
    }
}