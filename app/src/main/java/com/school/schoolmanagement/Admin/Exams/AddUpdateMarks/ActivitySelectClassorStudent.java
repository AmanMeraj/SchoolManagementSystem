package com.school.schoolmanagement.Admin.Exams.AddUpdateMarks;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.school.schoolmanagement.Admin.Model.Class2;
import com.school.schoolmanagement.Admin.Model.ClassModel;
import com.school.schoolmanagement.R;
import com.school.schoolmanagement.databinding.ActivitySelectClassorStudentBinding;

import java.util.ArrayList;

public class ActivitySelectClassorStudent extends AppCompatActivity {
    ActivitySelectClassorStudentBinding binding;

    // Data
    private Class2 selectedClass;
    private boolean isWholeClassMode = true;
    private ArrayList<Class2> classList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivitySelectClassorStudentBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        setupTabSelection();

        // Load data first
        loadClassData();

        // Then setup dropdown with the loaded data
        setupClassDropdown();

        setupSearchButton();
    }

    private void setupTabSelection() {
        // Default to Whole Class mode
        setModeWholeClass();

        binding.tabWholeClass.setOnClickListener(v -> setModeWholeClass());
        binding.tabSingleStudent.setOnClickListener(v -> setModeSingleStudent());
    }

    private void setModeWholeClass() {
        isWholeClassMode = true;
        binding.selectClassContainer.setVisibility(View.VISIBLE);
        binding.tabWholeClass.setBackground(getResources().getDrawable(R.drawable.ornage_blue_btn));
        binding.tabWholeClass.setTextColor(getResources().getColor(R.color.white));
        binding.tabSingleStudent.setBackground(null);
        binding.tabSingleStudent.setTextColor(getResources().getColor(R.color.grey));
        binding.mainTitle.setText("Add Data As A Whole Class");
        binding.registrationLayout.setVisibility(View.GONE);

        // Hide content area when switching tabs
        binding.cardContent.setVisibility(View.GONE);
    }

    private void setModeSingleStudent() {
        isWholeClassMode = false;
        binding.tabSingleStudent.setBackground(getResources().getDrawable(R.drawable.ornage_blue_btn));
        binding.tabSingleStudent.setTextColor(getResources().getColor(R.color.white));
        binding.tabWholeClass.setBackground(null);
        binding.tabWholeClass.setTextColor(getResources().getColor(R.color.grey));
        binding.mainTitle.setText("Add Data For Single Student");
        binding.registrationLayout.setVisibility(View.VISIBLE);
        binding.selectClassContainer.setVisibility(View.GONE);

        // Hide content area when switching tabs
        binding.cardContent.setVisibility(View.GONE);
    }

    private void setupClassDropdown() {
        // Create and set the adapter first
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                getClassNames(classList));

        binding.selectClassDropdown.setAdapter(adapter);

        // Set threshold to 1 (show dropdown after 1 character) or 0 (show immediately)
        binding.selectClassDropdown.setThreshold(0);

        // Make it non-editable but clickable
        binding.selectClassDropdown.setFocusable(true);
        binding.selectClassDropdown.setFocusableInTouchMode(true);

        // Show dropdown when clicked
        binding.selectClassDropdown.setOnClickListener(v -> {
            binding.selectClassDropdown.showDropDown();
        });

        // Handle item selection
        binding.selectClassDropdown.setOnItemClickListener((parent, view, position, id) -> {
            selectedClass = classList.get(position);
        });
    }

    private void setupSearchButton() {
        binding.btnSearch.setOnClickListener(v -> {
            if (selectedClass == null) {
                Toast.makeText(ActivitySelectClassorStudent.this, "Please select a class", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!isWholeClassMode) {
                // Single student mode validation
                String regNumber = binding.edtRegistrationNumber.getText().toString().trim();
                if (regNumber.isEmpty()) {
                    Toast.makeText(ActivitySelectClassorStudent.this, "Please enter registration number", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Load and display single student data
                loadSingleStudentData(regNumber);
            } else {
                // Load and display whole class data
                loadWholeClassData();
            }
        });
    }

    private void loadClassData() {
        // TODO: Replace with actual data loading from API or local database
        classList = getDummyClassData();

        // Setup adapter for class dropdown
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                getClassNames(classList));

        binding.selectClassDropdown.setAdapter(adapter);
    }

    private ArrayList<Class2> getDummyClassData() {
        // Sample data for demonstration
        ArrayList<Class2> classes = new ArrayList<>();
        classes.add(new Class2("Class 1","Section A"));
        classes.add(new Class2("Class 2","Section B"));
        classes.add(new Class2("Class 3","Section D"));
        // Add more as needed
        return classes;
    }

    private String[] getClassNames(ArrayList<Class2> classes) {
        String[] names = new String[classes.size()];
        for (int i = 0; i < classes.size(); i++) {
            names[i] = classes.get(i).getClassName() + " - " + classes.get(i).getSection();
        }
        return names;
    }

    private void loadWholeClassData() {
        // Show the content area
        binding.cardContent.setVisibility(View.VISIBLE);

        // Show the whole class container, hide single student container
        binding.wholeClassContainer.setVisibility(View.VISIBLE);
        binding.singleStudentContainer.setVisibility(View.GONE);

        // Set the title with the selected class name
        String className = selectedClass.getClassName() + " - " + selectedClass.getSection();
        binding.wholeClassTitle.setText("Marks for " + className);

        // TODO: Load actual student list data for the selected class
        // For example, load data and set up a RecyclerView adapter
        setupWholeClassMarksRecyclerView();
    }

    private void loadSingleStudentData(String registrationNumber) {
        // Show the content area
        binding.cardContent.setVisibility(View.VISIBLE);

        // Show the single student container, hide whole class container
        binding.singleStudentContainer.setVisibility(View.VISIBLE);
        binding.wholeClassContainer.setVisibility(View.GONE);

        // Set student information
        String className = selectedClass.getClassName() + " - " + selectedClass.getSection();
        binding.singleStudentTitle.setText("Marks for Student");
        binding.singleStudentClass.setText("Class: " + className);
        binding.singleStudentReg.setText("Registration Number: " + registrationNumber);

        // TODO: Load actual student marks data for the given registration number
        // For example, load data and set up a RecyclerView adapter
        setupSingleStudentMarksRecyclerView(registrationNumber);
    }

    private void setupWholeClassMarksRecyclerView() {
        // TODO: Implement the RecyclerView setup for whole class
        // This would include setting up an adapter with student data
        // For example:
        /*
        StudentListAdapter adapter = new StudentListAdapter(getStudentListForClass(selectedClass));
        binding.wholeClassRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.wholeClassRecyclerView.setAdapter(adapter);
        */
    }

    private void setupSingleStudentMarksRecyclerView(String registrationNumber) {
        // TODO: Implement the RecyclerView setup for single student
        // This would include setting up an adapter with the student's subjects
        // For example:
        /*
        SubjectMarksAdapter adapter = new SubjectMarksAdapter(getSubjectsForStudent(registrationNumber));
        binding.singleStudentRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.singleStudentRecyclerView.setAdapter(adapter);
        */
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null; // Avoid memory leaks
    }
}