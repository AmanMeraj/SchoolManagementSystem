package com.school.schoolmanagement.Admin.QuestionPaper;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.school.schoolmanagement.Adapters.QuestionBankAdapter;
import com.school.schoolmanagement.Admin.Model.AddQuestionBank;
import com.school.schoolmanagement.Admin.Model.ClassModel;
import com.school.schoolmanagement.Admin.Model.ExamModel;
import com.school.schoolmanagement.Admin.Model.QuestionBankModel;
import com.school.schoolmanagement.Admin.Model.SubjectModel;
import com.school.schoolmanagement.Admin.Model.Entry;
import com.school.schoolmanagement.HelperClasses.DataExportHelper;
import com.school.schoolmanagement.HelperClasses.ExamApiHelper;
import com.school.schoolmanagement.Model.AddChapter;
import com.school.schoolmanagement.Model.ChapterResponse;

import com.school.schoolmanagement.HelperClasses.ChapterApiHelper;
import com.school.schoolmanagement.HelperClasses.ClassApiHelper;
import com.school.schoolmanagement.HelperClasses.SubjectApiHelper;
import com.school.schoolmanagement.HelperClasses.QuestionBankApiHelper;
import com.school.schoolmanagement.Model.EmployeeResponse;
import com.school.schoolmanagement.R;
import com.school.schoolmanagement.databinding.ActivityQuestionBankBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ActivityQuestionBank extends AppCompatActivity {

    private static final String TAG = "ActivityQuestionBank";
    private ActivityQuestionBankBinding binding;

    // Helper classes
    private ClassApiHelper classApiHelper;
    private SubjectApiHelper subjectApiHelper;
    private ExamApiHelper examApiHelper;
    private QuestionBankApiHelper questionBankApiHelper;

    // Data lists
    private ArrayList<ExamModel.Datum> examListStorage = new ArrayList<>();
    private ArrayList<ClassModel.Data> classList = new ArrayList<>();
    private ArrayList<SubjectModel> subjectList = new ArrayList<>();
    private ArrayList<Entry> questionBankList = new ArrayList<>();

    // Name lists for adapters
    private ArrayList<String> examNameList = new ArrayList<>();
    private ArrayList<String> classNames = new ArrayList<>();
    private ArrayList<String> subjectNames = new ArrayList<>();

    // Adapters
    private ArrayAdapter<String> examAdapter;
    private ArrayAdapter<String> classAdapter;
    private ArrayAdapter<String> subjectAdapter;
    private QuestionBankAdapter questionBankAdapter;

    // Selected values
    private int selectedClassId = -1;
    private int selectedExamId = -1;
    private int selectedSubjectId = -1;

    // Edit mode
    private boolean isEditMode = false;
    private int editingQuestionId = -1;
    private int editingPosition = -1;

    // Search functionality
    private ArrayList<Entry> filteredQuestionList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: Starting ActivityQuestionBank");

        EdgeToEdge.enable(this);
        binding = ActivityQuestionBankBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeHelpers();
        setupUI();
        loadInitialData();
        setupClickListeners();

        Log.d(TAG, "onCreate: ActivityQuestionBank setup completed");
    }

    private void initializeHelpers() {
        Log.d(TAG, "initializeHelpers: Initializing API helpers");
        classApiHelper = new ClassApiHelper(this);
        subjectApiHelper = new SubjectApiHelper(this);
        questionBankApiHelper = new QuestionBankApiHelper(this);
        examApiHelper = new ExamApiHelper(this);
        Log.d(TAG, "initializeHelpers: All helpers initialized");
    }

    private void setupUI() {
        Log.d(TAG, "setupUI: Setting up UI components");
        setupClassSpinner();
        setupSubjectSpinner();
        setupExamSpinner();
        setupRecyclerView();
        resetFormToCreateMode();
        Log.d(TAG, "setupUI: UI setup completed");
    }

    private void setupClassSpinner() {
        Log.d(TAG, "setupClassSpinner: Setting up class spinner");
        // Initialize with empty list first
        classNames.clear();
        classNames.add("Select Class");

        classAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, classNames);
        classAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.edtClass.setAdapter(classAdapter);

        // Set threshold to show dropdown on click
        binding.edtClass.setThreshold(1);

        binding.edtClass.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "Class selected at position: " + position);
                if (position > 0) { // Skip "Select Class" option
                    // Adjust position since we have "Select Class" at index 0
                    int actualPosition = position - 1;
                    if (actualPosition < classList.size()) {
                        selectedClassId = classList.get(actualPosition).getClassId();
                        Log.d(TAG, "Selected class ID: " + selectedClassId);
                        loadSubjects();

                        // Reset subject selection
                        selectedSubjectId = -1;
                        binding.edtSubject.setText("", false);
                    }
                } else {
                    selectedClassId = -1;
                    Log.d(TAG, "Class selection cleared");
                    clearSubjectAndChapter();
                }
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

        Log.d(TAG, "setupClassSpinner: Class spinner setup completed");
    }

    private void setupSubjectSpinner() {
        Log.d(TAG, "setupSubjectSpinner: Setting up subject spinner");
        subjectNames.clear();
        subjectNames.add("Select Subject");

        subjectAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, subjectNames);
        subjectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.edtSubject.setAdapter(subjectAdapter);

        // Set threshold to show dropdown on click
        binding.edtSubject.setThreshold(1);

        // Initially disable subject dropdown
        binding.edtSubject.setEnabled(false);

        binding.edtSubject.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "Subject selected at position: " + position);
                if (position > 0) { // Skip "Select Subject" option
                    // Adjust position since we have "Select Subject" at index 0
                    int actualPosition = position - 1;
                    if (actualPosition < subjectList.size()) {
                        selectedSubjectId = subjectList.get(actualPosition).getId();
                        Log.d(TAG, "Selected subject ID: " + selectedSubjectId);
                    }
                } else {
                    selectedSubjectId = -1;
                    Log.d(TAG, "Subject selection cleared");
                }
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

        Log.d(TAG, "setupSubjectSpinner: Subject spinner setup completed");
    }

    private void setupExamSpinner() {
        Log.d(TAG, "setupExamSpinner: Setting up exam spinner");
        examNameList.clear();
        examNameList.add("Select Exam");

        examAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, examNameList);
        examAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.edtExam.setAdapter(examAdapter);

        // Set threshold to show dropdown on click
        binding.edtExam.setThreshold(1);

        binding.edtExam.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "Exam selected at position: " + position);
                if (position > 0) { // Skip "Select Exam" option
                    // Adjust position since we have "Select Exam" at index 0
                    int actualPosition = position - 1;
                    if (actualPosition < examListStorage.size()) {
                        selectedExamId = examListStorage.get(actualPosition).getExamId();
                        Log.d(TAG, "Selected exam ID: " + selectedExamId);
                    }
                } else {
                    selectedExamId = -1;
                    Log.d(TAG, "Exam selection cleared");
                }
            }
        });

        // Handle focus change to show dropdown
        binding.edtExam.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                binding.edtExam.showDropDown();
            }
        });

        // Handle click to show dropdown
        binding.edtExam.setOnClickListener(v -> {
            binding.edtExam.showDropDown();
        });

        Log.d(TAG, "setupExamSpinner: Exam spinner setup completed");
    }

    private void setupRecyclerView() {
        Log.d(TAG, "setupRecyclerView: Setting up RecyclerView");

        // Initialize RecyclerView
        binding.rcQuestions.setLayoutManager(new LinearLayoutManager(this));

        // Create adapter with proper callback implementation
        questionBankAdapter = new QuestionBankAdapter(this, filteredQuestionList, new QuestionBankAdapter.OnQuestionBankClickListener(){
            @Override
            public void onQuestionEditClick(Entry entry, int position) {
                Log.d(TAG, "Edit clicked for question at position: " + position + ", ID: " + entry.getId());
                editQuestion(entry, position);
            }

            @Override
            public void onQuestionDeleteClick(Entry entry, int position) {
                Log.d(TAG, "Delete clicked for question at position: " + position + ", ID: " + entry.getId());
                deleteQuestion(entry, position);
            }

        });

        binding.rcQuestions.setAdapter(questionBankAdapter);

        Log.d(TAG, "setupRecyclerView: RecyclerView setup completed");
        Log.d(TAG, "setupRecyclerView: Adapter set with " + filteredQuestionList.size() + " items");
    }

    private void loadInitialData() {
        Log.d(TAG, "loadInitialData: Loading initial data");
        loadClasses();
    }

    private void setupClickListeners() {
        Log.d(TAG, "setupClickListeners: Setting up click listeners");

        // Create button listener
        binding.btnCreate.setOnClickListener(v -> {
            Log.d(TAG, "Create button clicked");
            if (validateForm()) {
                createQuestion();
            }
        });
        setupExportButtons();
        binding.toolbar.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Edit button listener
        binding.btnEdit.setOnClickListener(v -> {
            Log.d(TAG, "Edit button clicked, editing question ID: " + editingQuestionId);
            if (validateForm()) {
                updateQuestion();
            }
        });

        // Cancel button listener
        binding.btnCancel.setOnClickListener(v -> {
            Log.d(TAG, "Cancel button clicked");
            resetFormToCreateMode();
            clearForm();
        });

        // Search functionality
        binding.edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d(TAG, "Search query changed: " + s.toString());
                filterQuestions(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        Log.d(TAG, "setupClickListeners: All click listeners set up");
    }

    private void loadClasses() {
        Log.d(TAG, "loadClasses: Starting to load classes");
        classApiHelper.fetchAllClasses(new ClassApiHelper.ClassListCallback() {
            @Override
            public void onSuccess(ArrayList<ClassModel.Data> classes) {
                Log.d(TAG, "loadClasses: Successfully loaded " + (classes != null ? classes.size() : 0) + " classes");
                if (classes != null) {
                    classList.clear();
                    classList.addAll(classes);

                    classNames.clear();
                    classNames.add("Select Class");

                    // Add class names to the list
                    for (ClassModel.Data classData : classList) {
                        classNames.add(classData.getClassName());
                        Log.d(TAG, "Added class: " + classData.getClassName() + " with ID: " + classData.getClassId());
                    }

                    classAdapter.notifyDataSetChanged();
                    loadExams(); // Load exams after classes are loaded
                }
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "loadClasses: Error loading classes: " + errorMessage);
                showToast("Error loading classes: " + errorMessage);
            }

            @Override
            public void onLoading(boolean isLoading) {
                Log.d(TAG, "loadClasses: Loading state: " + isLoading);
                showLoading(isLoading);
            }
        });
    }

    private void loadExams() {
        Log.d(TAG, "loadExams: Starting to load exams");
        examApiHelper.fetchAllExams(new ExamApiHelper.ExamListCallback() {
            @Override
            public void onExamSuccess(ArrayList<ExamModel.Datum> examList) {
                Log.d(TAG, "loadExams: Successfully loaded " + (examList != null ? examList.size() : 0) + " exams");
                if (examList != null) {
                    examListStorage.clear();
                    examListStorage.addAll(examList);

                    examNameList.clear();
                    examNameList.add("Select Exam");

                    // Add exam names to the list
                    for (ExamModel.Datum exam : examList) {
                        examNameList.add(exam.getDisplayName());
                        Log.d(TAG, "Added exam: " + exam.getDisplayName() + " with ID: " + exam.getExamId());
                    }

                    examAdapter.notifyDataSetChanged();
                    loadQuestions(); // Load questions after exams are loaded
                }
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "loadExams: Error loading exams: " + errorMessage);
                showToast("Error loading exams: " + errorMessage);
            }

            @Override
            public void onLoading(boolean isLoading) {
                Log.d(TAG, "loadExams: Loading state: " + isLoading);
                showLoading(isLoading);
            }
        });
    }

    private void loadSubjects() {
        Log.d(TAG, "loadSubjects: Starting to load subjects for class ID: " + selectedClassId);
        subjectApiHelper.fetchSubjectsForClass(selectedClassId, new SubjectApiHelper.SubjectListCallback() {
            @Override
            public void onSubjectsSuccess(ArrayList<SubjectModel> subjects) {
                Log.d(TAG, "loadSubjects: Successfully loaded " + (subjects != null ? subjects.size() : 0) + " subjects");
                if (subjects != null) {
                    subjectList.clear();
                    subjectList.addAll(subjects);

                    subjectNames.clear();
                    subjectNames.add("Select Subject");

                    // Add subject names to the list
                    for (SubjectModel subject : subjects) {
                        subjectNames.add(subject.getName());
                        Log.d(TAG, "Added subject: " + subject.getName() + " with ID: " + subject.getId());
                    }

                    subjectAdapter.notifyDataSetChanged();

                    // Enable subject dropdown
                    binding.edtSubject.setEnabled(true);
                }
            }

            @Override
            public void onSubjectsError(String errorMessage) {
                Log.e(TAG, "loadSubjects: Error loading subjects: " + errorMessage);
                showToast("Error loading subjects: " + errorMessage);
                clearSubjects();
            }

            @Override
            public void onSubjectsLoading(boolean isLoading) {
                Log.d(TAG, "loadSubjects: Loading state: " + isLoading);
                showLoading(isLoading);
            }
        });
    }

    private void clearSubjects() {
        Log.d(TAG, "clearSubjects: Clearing subjects list");
        subjectList.clear();
        subjectNames.clear();
        subjectNames.add("Select Subject");
        subjectAdapter.notifyDataSetChanged();
        binding.edtSubject.setEnabled(false);
        binding.edtSubject.setText("", false);
        selectedSubjectId = -1;
    }

    private void loadQuestions() {
        Log.d(TAG, "loadQuestions: Starting to load questions");
        questionBankApiHelper.fetchQuestionBanks(new QuestionBankApiHelper.QuestionBankApiListCallback() {
            @Override
            public void onQuestionBankApiSuccess(ArrayList<Entry> response) {
                Log.d(TAG, "loadQuestions: Successfully loaded " + (response != null ? response.size() : 0) + " questions");
                if (response != null) {
                    questionBankList.clear();
                    questionBankList.addAll(response);

                    filteredQuestionList.clear();
                    filteredQuestionList.addAll(response);

                    // Log each question for debugging
                    for (int i = 0; i < response.size(); i++) {
                        Entry question = response.get(i);
                        Log.d(TAG, "Question " + i + ": ID=" + question.getId() + ", Text=" +
                                (question.getQuestion() != null ? question.getQuestion().substring(0, Math.min(50, question.getQuestion().length())) + "..." : "null"));
                    }

                    runOnUiThread(() -> {
                        if (questionBankAdapter != null) {
                            Log.d(TAG, "loadQuestions: Notifying adapter of data change");
                            questionBankAdapter.notifyDataSetChanged();
                        } else {
                            Log.e(TAG, "loadQuestions: Adapter is null!");
                        }
                    });
                }
            }

            @Override
            public void onQuestionBankApiError(String error) {
                Log.e(TAG, "loadQuestions: Error loading questions: " + error);
                showToast("Error loading questions: " + error);
            }

            @Override
            public void onQuestionBankApiLoading(boolean isLoading) {
                Log.d(TAG, "loadQuestions: Loading state: " + isLoading);
                showLoading(isLoading);
            }
        });
    }

    private boolean validateForm() {
        Log.d(TAG, "validateForm: Validating form inputs");

        // Validate Class selection
        if (selectedClassId == -1) {
            Log.w(TAG, "validateForm: No class selected");
            showToast("Please select a class");
            binding.edtClass.requestFocus();
            return false;
        }

        // Validate Subject selection
        if (selectedSubjectId == -1) {
            Log.w(TAG, "validateForm: No subject selected");
            showToast("Please select a subject");
            binding.edtSubject.requestFocus();
            return false;
        }

        // Validate Question Type
        String questionType = binding.edtQuestionType.getText().toString().trim();
        if (TextUtils.isEmpty(questionType)) {
            Log.w(TAG, "validateForm: Question type is empty");
            binding.edtQuestionType.setError("Question type is required");
            binding.edtQuestionType.requestFocus();
            return false;
        }

        // Validate Marks
        String marksStr = binding.edtMarks.getText().toString().trim();
        if (TextUtils.isEmpty(marksStr)) {
            Log.w(TAG, "validateForm: Marks is empty");
            binding.edtMarks.setError("Marks is required");
            binding.edtMarks.requestFocus();
            return false;
        }

        try {
            int marks = Integer.parseInt(marksStr);
            if (marks <= 0) {
                Log.w(TAG, "validateForm: Marks is not positive: " + marks);
                binding.edtMarks.setError("Marks must be greater than 0");
                binding.edtMarks.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            Log.w(TAG, "validateForm: Invalid marks format: " + marksStr);
            binding.edtMarks.setError("Please enter valid marks");
            binding.edtMarks.requestFocus();
            return false;
        }

        // Validate Question
        String question = binding.edtQuestion.getText().toString().trim();
        if (TextUtils.isEmpty(question)) {
            Log.w(TAG, "validateForm: Question is empty");
            binding.edtQuestion.setError("Question is required");
            binding.edtQuestion.requestFocus();
            return false;
        }

        if (question.length() < 10) {
            Log.w(TAG, "validateForm: Question too short: " + question.length() + " characters");
            binding.edtQuestion.setError("Question must be at least 10 characters long");
            binding.edtQuestion.requestFocus();
            return false;
        }

        Log.d(TAG, "validateForm: Form validation passed");
        return true;
    }

    private void createQuestion() {
        Log.d(TAG, "createQuestion: Creating new question");
        AddQuestionBank questionBank = new AddQuestionBank();
        questionBank.setClassId(selectedClassId);
        questionBank.setSubjectId(selectedSubjectId);
        questionBank.setExamId(selectedExamId);
        questionBank.setQuestionType(binding.edtQuestionType.getText().toString().trim());
        questionBank.setMark(String.valueOf(Integer.parseInt(binding.edtMarks.getText().toString().trim())));
        questionBank.setQuestion(binding.edtQuestion.getText().toString().trim());

        Log.d(TAG, "createQuestion: Question data - ClassID: " + selectedClassId +
                ", SubjectID: " + selectedSubjectId + ", ExamID: " + selectedExamId);

        questionBankApiHelper.addNewQuestionBank(questionBank, new QuestionBankApiHelper.QuestionBankApiAddCallback() {
            @Override
            public void onQuestionBankApiSuccess(String message) {
                Log.d(TAG, "createQuestion: Question created successfully: " + message);
                showToast("Question created successfully");
                clearForm();
                loadQuestions(); // Refresh the list
            }

            @Override
            public void onQuestionBankApiError(String error) {
                Log.e(TAG, "createQuestion: Error creating question: " + error);
                showToast("Error creating question: " + error);
            }

            @Override
            public void onQuestionBankApiLoading(boolean isLoading) {
                Log.d(TAG, "createQuestion: Loading state: " + isLoading);
                showLoading(isLoading);
            }
        });
    }

    private void updateQuestion() {
        Log.d(TAG, "updateQuestion: Updating question with ID: " + editingQuestionId);
        AddQuestionBank questionBank = new AddQuestionBank();
        questionBank.setClassId(selectedClassId);
        questionBank.setSubjectId(selectedSubjectId);
        questionBank.setExamId(selectedExamId);
        questionBank.setQuestionType(binding.edtQuestionType.getText().toString().trim());
        questionBank.setMark(binding.edtMarks.getText().toString().trim());
        questionBank.setQuestion(binding.edtQuestion.getText().toString().trim());

        questionBankApiHelper.updateQuestionBank(editingQuestionId, questionBank, new QuestionBankApiHelper.QuestionBankApiUpdateCallback() {
            @Override
            public void onQuestionBankApiSuccess(String message) {
                Log.d(TAG, "updateQuestion: Question updated successfully: " + message);
                showToast("Question updated successfully");
                resetFormToCreateMode();
                clearForm();
                loadQuestions(); // Refresh the list
            }

            @Override
            public void onQuestionBankApiError(String error) {
                Log.e(TAG, "updateQuestion: Error updating question: " + error);
                showToast("Error updating question: " + error);
            }

            @Override
            public void onQuestionBankApiLoading(boolean isLoading) {
                Log.d(TAG, "updateQuestion: Loading state: " + isLoading);
                showLoading(isLoading);
            }
        });
    }

    private void editQuestion(Entry question, int position) {
        Log.d(TAG, "editQuestion: Starting edit for question ID: " + question.getId() + " at position: " + position);

        isEditMode = true;
        editingQuestionId = question.getId();
        editingPosition = position;

        Log.d(TAG, "editQuestion: Edit mode activated - QuestionID: " + editingQuestionId + ", Position: " + editingPosition);

        // Populate form with question data
        populateFormWithQuestion(question);

        // Show edit buttons
        binding.btnCreate.setVisibility(View.GONE);
        binding.editBtnLinear.setVisibility(View.VISIBLE);

        Log.d(TAG, "editQuestion: UI switched to edit mode");

        // Scroll to top
        binding.scrollView.smoothScrollTo(0, 0);
    }

    private void populateFormWithQuestion(Entry question) {
        Log.d(TAG, "populateFormWithQuestion: Populating form with question data");
        Log.d(TAG, "Question details - ID: " + question.getId() +
                ", Class: " + question.getClassName() +
                ", Subject: " + question.getSubject() +
                ", Exam: " + question.getExamTitle());

        // Set class
        for (int i = 0; i < classList.size(); i++) {
            if (Objects.equals(classList.get(i).getClassName(), question.getClassName())) {
                selectedClassId = classList.get(i).getClassId();
                binding.edtClass.setText(classNames.get(i + 1)); // +1 because "Select Class" is at index 0
                Log.d(TAG, "populateFormWithQuestion: Class set - " + question.getClassName() + " (ID: " + selectedClassId + ")");
                break;
            }
        }

        // Fix exam selection - match by exam name instead of ID
        for (int i = 0; i < examListStorage.size(); i++) {
            if (Objects.equals(examListStorage.get(i).getDisplayName(), question.getExamTitle())) {
                selectedExamId = examListStorage.get(i).getExamId();
                binding.edtExam.setText(examNameList.get(i + 1)); // +1 because "Select Exam" is at index 0
                Log.d(TAG, "populateFormWithQuestion: Exam set - " + question.getExamTitle() + " (ID: " + selectedExamId + ")");
                break;
            }
        }

        // Load subjects first, then set the selected subject after subjects are loaded
        loadSubjectsForEdit(question.getSubject(), question.getId());

        binding.edtQuestionType.setText(question.getQuestionType());
        binding.edtMarks.setText(question.getMark());
        binding.edtQuestion.setText(question.getQuestion());

        Log.d(TAG, "populateFormWithQuestion: Form populated successfully");
    }

    private void loadSubjectsForEdit(String subjectName, int questionId) {
        subjectApiHelper.fetchSubjectsForClass(selectedClassId, new SubjectApiHelper.SubjectListCallback() {
            @Override
            public void onSubjectsSuccess(ArrayList<SubjectModel> subjects) {
                if (subjects != null) {
                    subjectList.clear();
                    subjectList.addAll(subjects);

                    subjectNames.clear();
                    subjectNames.add("Select Subject");

                    // Add subject names to the list
                    for (SubjectModel subject : subjects) {
                        subjectNames.add(subject.getName());
                    }

                    subjectAdapter.notifyDataSetChanged();

                    // Enable subject dropdown
                    binding.edtSubject.setEnabled(true);

                    // Set selected subject by matching the name
                    for (int i = 0; i < subjectList.size(); i++) {
                        if (Objects.equals(subjectList.get(i).getName(), subjectName)) {
                            selectedSubjectId = subjectList.get(i).getId();
                            binding.edtSubject.setText(subjectNames.get(i + 1)); // +1 because "Select Subject" is at index 0
                            break;
                        }
                    }
                }
            }

            @Override
            public void onSubjectsError(String errorMessage) {
                showToast("Error loading subjects: " + errorMessage);
            }

            @Override
            public void onSubjectsLoading(boolean isLoading) {
                showLoading(isLoading);
            }
        });
    }

    private void deleteQuestion(Entry question, int position) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Question")
                .setMessage("Are you sure you want to delete this question?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    questionBankApiHelper.deleteQuestionBank(question.getId(), new QuestionBankApiHelper.QuestionBankApiDeleteCallback() {
                        @Override
                        public void onQuestionBankApiSuccess(String message) {
                            showToast("Question deleted successfully");
                            loadQuestions(); // Refresh the list
                        }

                        @Override
                        public void onQuestionBankApiError(String error) {
                            showToast("Error deleting question: " + error);
                        }

                        @Override
                        public void onQuestionBankApiLoading(boolean isLoading) {
                            showLoading(isLoading);
                        }
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void resetFormToCreateMode() {
        isEditMode = false;
        editingQuestionId = -1;
        editingPosition = -1;

        binding.btnCreate.setVisibility(View.VISIBLE);
        binding.editBtnLinear.setVisibility(View.GONE);
    }

    private void clearForm() {
        binding.edtClass.setText("", false);
        binding.edtSubject.setText("", false);
        binding.edtExam.setText("", false);
        binding.edtQuestionType.setText("");
        binding.edtMarks.setText("");
        binding.edtQuestion.setText("");

        selectedClassId = -1;
        selectedSubjectId = -1;
        selectedExamId = -1;
        clearSubjectAndChapter();
    }

    private void clearSubjectAndChapter() {
        binding.edtSubject.setText("", false);
        selectedSubjectId = -1;
        subjectList.clear();
        subjectNames.clear();
        subjectNames.add("Select Subject");
        subjectAdapter.notifyDataSetChanged();
        binding.edtSubject.setEnabled(false);
    }

    private void filterQuestions(String query) {
        filteredQuestionList.clear();

        if (TextUtils.isEmpty(query)) {
            filteredQuestionList.addAll(questionBankList);
        } else {
            String lowerCaseQuery = query.toLowerCase().trim();
            for (Entry question : questionBankList) {
                if (question.getQuestion().toLowerCase().contains(lowerCaseQuery) ||
                        question.getQuestionType().toLowerCase().contains(lowerCaseQuery) ||
                        question.getClassName().toLowerCase().contains(lowerCaseQuery) ||
                        question.getSubject().toLowerCase().contains(lowerCaseQuery)) {
                    filteredQuestionList.add(question);
                }
            }
        }

        questionBankAdapter.notifyDataSetChanged();
    }

    // Export functions
    private void copyToClipboard() {
        StringBuilder sb = new StringBuilder();
        for (Entry question : filteredQuestionList) {
            sb.append("Class: ").append(question.getClassName()).append("\n");
            sb.append("Subject: ").append(question.getSubject()).append("\n");
            sb.append("Type: ").append(question.getQuestionType()).append("\n");
            sb.append("Marks: ").append(question.getMark()).append("\n");
            sb.append("Question: ").append(question.getQuestion()).append("\n\n");
        }

        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Questions", sb.toString());
        clipboard.setPrimaryClip(clip);
        showToast("Questions copied to clipboard");
    }

    private void exportToCSV() {
        // Implement CSV export functionality
        showToast("CSV export functionality to be implemented");
    }

    private void exportToExcel() {
        // Implement Excel export functionality
        showToast("Excel export functionality to be implemented");
    }

    private void exportToPDF() {
        // Implement PDF export functionality
        showToast("PDF export functionality to be implemented");
    }

    private void printQuestions() {
        // Implement print functionality
        showToast("Print functionality to be implemented");
    }

    // Centralized loading method like in ActivityAddSubjectChapter
    private void showLoading(boolean isLoading) {
        if (isLoading) {
            showLoader();
            // Disable buttons during loading
            binding.btnCreate.setEnabled(false);
            binding.btnEdit.setEnabled(false);
            binding.btnCancel.setEnabled(false);
        } else {
            hideLoader();
            // Re-enable buttons after loading
            binding.btnCreate.setEnabled(true);
            binding.btnEdit.setEnabled(true);
            binding.btnCancel.setEnabled(true);
        }
    }

    private void showLoader() {
        if (binding != null && binding.loader != null && binding.loader.rlLoader != null) {
            binding.loader.rlLoader.setVisibility(View.VISIBLE);
        }
    }

    private void hideLoader() {
        if (binding != null && binding.loader != null && binding.loader.rlLoader != null) {
            binding.loader.rlLoader.setVisibility(View.GONE);
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (binding != null) {
            binding = null;
        }
    }
    // Setup export buttons functionality
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

            // Generate dynamic filename based on question bank criteria
            String fileName = generateDynamicFileName();

            // Use the exportData method with dynamic filename
            exportHelper.exportData(tableData, action, fileName);

        } catch (Exception e) {
            Log.e(TAG, "Export error: " + e.getMessage());
            showToast("Export failed: " + e.getMessage());
        }
    }

    // Generate dynamic filename based on selected criteria
    private String generateDynamicFileName() {
        StringBuilder fileName = new StringBuilder("question_bank");

        // Add selected class name if available
        if (selectedClassId != -1) {
            for (ClassModel.Data classData : classList) {
                if (classData.getClassId() == selectedClassId) {
                    fileName.append("_").append(classData.getClassName().replaceAll("\\s+", "_"));
                    break;
                }
            }
        }

        // Add selected subject name if available
        if (selectedSubjectId != -1) {
            for (SubjectModel subject : subjectList) {
                if (subject.getId() == selectedSubjectId) {
                    fileName.append("_").append(subject.getName().replaceAll("\\s+", "_"));
                    break;
                }
            }
        }

        // Add selected exam name if available
        if (selectedExamId != -1) {
            for (ExamModel.Datum exam : examListStorage) {
                if (exam.getExamId() == selectedExamId) {
                    fileName.append("_").append(exam.getDisplayName().replaceAll("\\s+", "_"));
                    break;
                }
            }
        }

        // Add current search query if available
        String searchQuery = binding.edtSearch.getText().toString().trim();
        if (!TextUtils.isEmpty(searchQuery)) {
            fileName.append("_search_").append(searchQuery.replaceAll("\\s+", "_"));
        }

        // Add timestamp for uniqueness
        fileName.append("_").append(System.currentTimeMillis());

        return fileName.toString();
    }

    // Prepare table data for export based on your XML layout structure
    private ArrayList<ArrayList<String>> prepareTableData() {
        ArrayList<ArrayList<String>> tableData = new ArrayList<>();

        // Add header row based on your XML layout
        ArrayList<String> headers = new ArrayList<>();
        headers.add("Sr");
        headers.add("Title");
        headers.add("Class");
        headers.add("Subject");
        headers.add("Q Type");
        headers.add("Marks");
        headers.add("Question");
        tableData.add(headers);

        int count = 1;
        // Add data rows from filteredQuestionList (current displayed questions)
        for (Entry question : filteredQuestionList) {
            ArrayList<String> row = new ArrayList<>();
            row.add(String.valueOf(count));

            // Add exam title (this appears to be the "Title" column)
            row.add(question.getExamTitle() != null ? question.getExamTitle() : "");

            // Add class name
            row.add(question.getClassName() != null ? question.getClassName() : "");

            // Add subject name
            row.add(question.getSubject() != null ? question.getSubject() : "");

            // Add question type
            row.add(question.getQuestionType() != null ? question.getQuestionType() : "");

            // Add marks
            row.add(question.getMark() != null ? question.getMark() : "0");

            // Add question text (truncate if too long for better readability)
            String questionText = question.getQuestion() != null ? question.getQuestion() : "";
            if (questionText.length() > 100) {
                questionText = questionText.substring(0, 100) + "...";
            }
            row.add(questionText);

            tableData.add(row);
            count++;
        }

        return tableData;
    }
}