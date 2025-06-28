package com.school.schoolmanagement.Admin.Students.StudentsIdCard;

import static android.content.ContentValues.TAG;
import static android.widget.Toast.LENGTH_SHORT;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.school.schoolmanagement.Admin.Adapter.AdapterIdCard;
import com.school.schoolmanagement.Admin.Adapter.AdapterStudentsIdCard;
import com.school.schoolmanagement.Admin.Model.ClassModel;
import com.school.schoolmanagement.Admin.Model.IdCard;
import com.school.schoolmanagement.GlobalViewModel.ViewModel;
import com.school.schoolmanagement.Model.StudentDetails;
import com.school.schoolmanagement.R;
import com.school.schoolmanagement.Utils.Utility;
import com.school.schoolmanagement.databinding.ActivityStudentsIdCardsBinding;

import java.util.ArrayList;
import java.util.List;

public class ActivityStudentsIdCards extends Utility {
    ActivityStudentsIdCardsBinding binding;
    private AdapterStudentsIdCard adapter;
    private List<StudentDetails> studentList;
    private ArrayList<ClassModel.Data> classList;
    private List<StudentDetails> filteredStudentList; // This will be used by the adapter
    ViewModel viewModel;
    String auth;
    int selectedClassId=-1;

    // Track search state
    private boolean isSearchVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityStudentsIdCardsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        viewModel = new ViewModelProvider(this).get(ViewModel.class);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize lists
        studentList = new ArrayList<>();
        filteredStudentList = new ArrayList<>();
        classList = new ArrayList<>();

        auth = "Bearer " + pref.getPrefString(this, pref.user_token);
        setupSearchButton();
        setupRefreshButton(); // Add refresh button setup

        binding.toolbar.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Add click listener for class selection EditText
        binding.edtSelectClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (classList.isEmpty()) {
                    // If classList is empty, initialize it first
                    initializeClassData();
                } else {
                    // If classList already has data, show the dialog
                    showClassSelectionDialog();
                }
            }
        });

        // Make EditText non-editable and focusable for click events
        binding.edtSelectClass.setFocusable(false);
        binding.edtSelectClass.setClickable(true);

        // Initialize adapter with filtered list (empty initially)
        adapter = new AdapterStudentsIdCard(filteredStudentList);
        binding.rcIdCards.setAdapter(adapter);

        setupSearchFilter();

        if (isInternetConnected(this)) {
            // Initialize class data on startup
            initializeClassData();
        } else {
            showToast("No Internet Connection.");
        }
    }

    private ClassModel.Data createAllClassesOption() {
        try {
            ClassModel.Data allClasses;
            allClasses = new ClassModel.Data();
            allClasses.setClassId(0);
            allClasses.setClassName("All Classes");
            return allClasses;
        } catch (Exception e) {
            Log.e(TAG, "Error creating All Classes option: " + e.getMessage());
            return null;
        }
    }

    private void initializeClassData() {
        showLoading(true);
        viewModel.getAllClasses(auth).observe(this, response -> {
            showLoading(false);
            if (response != null) {
                classList.clear();

                // Add "All Classes" option at position 0
                ClassModel.Data allClassesOption = createAllClassesOption();
                if (allClassesOption != null) {
                    classList.add(allClassesOption);
                }

                if (response.data != null) {
                    classList.addAll(response.data.getData());
                }

                // Set default selection to "All Classes" and load all students
                binding.edtSelectClass.setText("All Classes");
                selectedClassId = 0;

                // Load all students initially
                getAllStudents();

            } else {
                showToast("No classes found.");
            }
        });
    }

    private void showClassSelectionDialog() {
        // Create class names array for display
        String[] classNames = new String[classList.size()];
        for (int i = 0; i < classList.size(); i++) {
            classNames[i] = classList.get(i).getClassName();
        }

        // Create and show AlertDialog with ArrayAdapter
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Select Class")
                .setItems(classNames, (dialogInterface, position) -> {
                    // Handle class selection
                    ClassModel.Data selectedClass = classList.get(position);
                    onClassSelected(selectedClass);
                    dialogInterface.dismiss();
                })
                .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss())
                .create();

        dialog.show();
    }

    private void onClassSelected(ClassModel.Data selectedClass) {
        selectedClassId = selectedClass.getClassId(); // Save the selected class ID
        binding.edtSelectClass.setText(selectedClass.getClassName()); // Update the EditText

        // Reset search when class is selected
        resetSearchView();

        if (selectedClassId == 0) {
            // "All Classes" selected - call method with only auth
            getAllStudents();
        } else {
            // Specific class selected - call method with auth and classId
            getBasicList(auth, selectedClassId);
        }

        Log.d(TAG, "Selected Class ID: " + selectedClassId);
        showToast("Selected: " + selectedClass.getClassName());
    }

    private void resetSearchView() {
        // Clear search input
        binding.edtSearchStudent.setText("");

        // Hide search view
        binding.relSearch.setVisibility(View.GONE);

        // Reset search button color
        binding.searchBtn.setColorFilter(null);

        // Update search state
        isSearchVisible = false;

        Log.d(TAG, "Search view reset");
    }

    private void setupRefreshButton() {
        // Assuming you have a refresh button in your layout
        // If you don't have one, you might need to add it to your XML layout first
        try {
            if (binding != null && binding.refreshBtn != null) { // Replace 'refreshBtn' with your actual refresh button ID
                binding.refreshBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        resetEverything();
                    }
                });
            }
        } catch (Exception e) {
            Log.e(TAG, "Error setting up refresh button: " + e.getMessage(), e);
            // If refresh button doesn't exist in layout, you can add it programmatically or handle it differently
        }
    }

    private void resetEverything() {
        try {
            // Reset search view
            resetSearchView();

            // Reset class selection to "All Classes"
            binding.edtSelectClass.setText("All Classes");
            selectedClassId = 0;

            // Clear all lists
            studentList.clear();
            filteredStudentList.clear();

            // Notify adapter
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }

            // Reload all students
            if (isInternetConnected(this)) {
                getAllStudents();
                showToast("Data refreshed");
            } else {
                showToast("No Internet Connection.");
            }

            Log.d(TAG, "Everything reset");
        } catch (Exception e) {
            Log.e(TAG, "Error resetting everything: " + e.getMessage(), e);
        }
    }

    private void getAllStudents() {
        showLoading(true);
        viewModel.getAllStudents(auth).observe(this, response -> {
            showLoading(false);
            if (response != null) {
                studentList.clear(); // Clear existing data
                filteredStudentList.clear(); // Also clear filtered list

                if (response.data != null) {
                    studentList.addAll(response.data.getData());
                    filteredStudentList.addAll(studentList); // Initialize filtered list with all data
                    adapter.notifyDataSetChanged();

                    Log.d(TAG, "Loaded " + studentList.size() + " students");
                } else {
                    Toast.makeText(this, response.message, LENGTH_SHORT).show();
                }
            } else {
                showToast("No Students found.");
            }
        });
    }

    private void getBasicList(String auth, int classId) {
        showLoading(true);
        viewModel.getBasicList(auth, classId).observe(this, response -> {
            showLoading(false);
            if (response.isSuccess) {
                // Clear existing lists and add new data for selected class
                studentList.clear();
                filteredStudentList.clear();

                studentList.addAll(response.data.getData());
                filteredStudentList.addAll(studentList); // Initialize filtered list with all data
                adapter.notifyDataSetChanged();

                Log.d(TAG, "Loaded " + studentList.size() + " students for class " + classId);
                showToast(response.message);
            } else {
                showToast(response.message);
            }
        });
    }

    private void setupSearchButton() {
        try {
            if (binding != null) {
                binding.searchBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            if (binding != null) {
                                if (isSearchVisible) {
                                    // Hide search
                                    resetSearchView();
                                } else {
                                    // Show search
                                    binding.searchBtn.setColorFilter(getResources().getColor(R.color.orange));
                                    binding.relSearch.setVisibility(View.VISIBLE);
                                    binding.edtSearchStudent.requestFocus();
                                    isSearchVisible = true;
                                }
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error in search button click: " + e.getMessage(), e);
                        }
                    }
                });
            }
        } catch (Exception e) {
            Log.e(TAG, "Error setting up search button: " + e.getMessage(), e);
        }
    }

    private void setupSearchFilter() {
        binding.edtSearchStudent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Not needed for this implementation
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Filter the list as user types
                filterStudentList(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Not needed for this implementation
            }
        });
    }

    private void filterStudentList(String query) {
        // Null check to prevent crashes
        if (studentList == null || filteredStudentList == null) {
            Log.w(TAG, "Lists not initialized yet, skipping filter");
            return;
        }

        filteredStudentList.clear();

        if (query.isEmpty()) {
            // If search query is empty, show all students
            filteredStudentList.addAll(studentList);
        } else {
            // Filter students based on name (case-insensitive)
            String lowerCaseQuery = query.toLowerCase().trim();

            for (StudentDetails student : studentList) {
                if (student != null && student.getStudentName() != null) {
                    // Check if student name contains the search query
                    if (student.getStudentName().toLowerCase().contains(lowerCaseQuery)) {
                        filteredStudentList.add(student);
                    }

                    // Optional: Also search by other fields like student ID, email, etc.
                    // Uncomment and adjust based on your StudentDetails model
                    /*
                    else if (student.getStudentId() != null &&
                             student.getStudentId().toLowerCase().contains(lowerCaseQuery)) {
                        filteredStudentList.add(student);
                    }
                    else if (student.getEmail() != null &&
                             student.getEmail().toLowerCase().contains(lowerCaseQuery)) {
                        filteredStudentList.add(student);
                    }
                    */
                }
            }
        }

        // Notify adapter about data changes
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }

        // Optional: Show a message if no results found
        if (filteredStudentList.isEmpty() && !query.isEmpty() && !studentList.isEmpty()) {
            showToast("No students found matching: " + query);
        }

        Log.d(TAG, "Filtered " + studentList.size() + " students to " + filteredStudentList.size() + " results");
    }

    private void showLoading(boolean isLoading) {
        Log.d(TAG, "Loading: " + isLoading);
        try {
            if (binding != null) {
                binding.loader.rlLoader.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error showing/hiding loader: " + e.getMessage(), e);
        }
    }

    private void showToast(String message) {
        Log.d(TAG, "Toast: " + message);
        try {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(this, message, LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error showing toast: " + e.getMessage(), e);
        }
    }
}