package com.school.schoolmanagement.Admin.ClassTest;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.school.schoolmanagement.Admin.Model.ClassModel;
import com.school.schoolmanagement.Admin.Model.SubjectModel;
import com.school.schoolmanagement.HelperClasses.ClassApiHelper;
import com.school.schoolmanagement.HelperClasses.SubjectApiHelper;
import com.school.schoolmanagement.R;
import com.school.schoolmanagement.Utils.Utility;
import com.school.schoolmanagement.databinding.ActivityCreateNewClassTestBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ActivityCreateNewClassTest extends Utility {

    private ActivityCreateNewClassTestBinding binding;
    private ClassApiHelper classApiHelper;
    private SubjectApiHelper subjectApiHelper;

    // Data lists
    private ArrayList<ClassModel.Data> classList = new ArrayList<>();
    private ArrayList<SubjectModel> subjectList = new ArrayList<>();
    private ArrayList<String> classNames = new ArrayList<>();
    private ArrayList<String> subjectNames = new ArrayList<>();

    // Selected values
    private int selectedClassId = -1;
    private int selectedSubjectId = -1;
    private String selectedDate = "";

    // Adapters
    private ArrayAdapter<String> classAdapter;
    private ArrayAdapter<String> subjectAdapter;

    // Date formatter
    private SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private Calendar calendar = Calendar.getInstance();

    // Constants for Intent extras
    public static final String EXTRA_CLASS_ID = "EXTRA_CLASS_ID";
    public static final String EXTRA_SUBJECT_ID = "EXTRA_SUBJECT_ID";
    public static final String EXTRA_DATE = "EXTRA_DATE";
    public static final String EXTRA_CLASS_NAME = "EXTRA_CLASS_NAME";
    public static final String EXTRA_SUBJECT_NAME = "EXTRA_SUBJECT_NAME";
    public static final String EXTRA_TOTAL_MARKS = "EXTRA_TOTAL_MARKS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityCreateNewClassTestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeHelpers();
        setupUI();
        loadClasses();
    }

    private void initializeHelpers() {
        classApiHelper = new ClassApiHelper(this);
        subjectApiHelper = new SubjectApiHelper(this);
    }

    private void setupUI() {
        setupClassSpinner();
        setupSubjectSpinner();
        setupDatePicker();
        setupCreateButton();
    }

    private void setupClassSpinner() {
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
                        binding.edtSubject.setText("", false);
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

    private void setupSubjectSpinner() {
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

    private void setupDatePicker() {
        binding.edtDate.setOnClickListener(v -> showDatePicker());
    }

    private void showDatePicker() {
        Calendar currentDate = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    selectedDate = dateFormatter.format(calendar.getTime());
                    binding.edtDate.setText(selectedDate);
                },
                currentDate.get(Calendar.YEAR),
                currentDate.get(Calendar.MONTH),
                currentDate.get(Calendar.DAY_OF_MONTH)
        );

        // Set maximum date to today (no future dates allowed)
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

        datePickerDialog.show();
    }

    private void setupCreateButton() {
        binding.btnCreatePaper.setOnClickListener(v -> {
            if (validateInputs()) {
                createTest();
            }
        });
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
            }

            @Override
            public void onError(String errorMessage) {
                showToast("Error loading classes: " + errorMessage);
            }

            @Override
            public void onLoading(boolean isLoading) {
                // Show/hide loading indicator if needed
                if (isLoading) {
                    // Show progress
                } else {
                    // Hide progress
                }
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
                binding.edtSubject.setEnabled(true);
            }

            @Override
            public void onSubjectsError(String errorMessage) {
                showToast("Error loading subjects: " + errorMessage);
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
        binding.edtSubject.setEnabled(false);
        binding.edtSubject.setText("", false);
        selectedSubjectId = -1;
    }

    private boolean validateInputs() {
        // Validate class selection
        if (selectedClassId == -1) {
            showToast("Please select a class");
            binding.edtClassesPaper.requestFocus();
            return false;
        }

        // Validate date selection
        if (TextUtils.isEmpty(selectedDate)) {
            showToast("Please select a date");
            binding.edtDate.requestFocus();
            return false;
        }

        // Validate subject selection
        if (selectedSubjectId == -1) {
            showToast("Please select a subject");
            binding.edtSubject.requestFocus();
            return false;
        }


        return true;
    }

    private void createTest() {
        // Find selected class and subject
        ClassModel.Data selectedClass = null;
        for (ClassModel.Data classData : classList) {
            if (classData.getClassId() == selectedClassId) {
                selectedClass = classData;
                break;
            }
        }

        SubjectModel selectedSubject = null;
        for (SubjectModel subject : subjectList) {
            if (subject.getId() == selectedSubjectId) {
                selectedSubject = subject;
                break;
            }
        }

        String className = selectedClass != null ? selectedClass.getClassName() : "";
        String subjectName = selectedSubject != null ? selectedSubject.getName() : "";

        // Pass data to next activity
        Intent intent = new Intent(this, ActivityCreateNewClassTest2.class); // Replace NextActivity with your actual next activity class
        intent.putExtra(EXTRA_CLASS_ID, selectedClassId);
        intent.putExtra(EXTRA_SUBJECT_ID, selectedSubjectId);
        intent.putExtra(EXTRA_DATE, selectedDate);
        intent.putExtra(EXTRA_CLASS_NAME, className);
        intent.putExtra(EXTRA_SUBJECT_NAME, subjectName);

        startActivity(intent);

        clearForm();
    }

    private void clearForm() {
        selectedClassId = -1;
        selectedSubjectId = -1;
        selectedDate = "";

        binding.edtClassesPaper.setText("", false);
        binding.edtSubject.setText("", false);
        binding.edtDate.setText("");

        clearSubjects();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}