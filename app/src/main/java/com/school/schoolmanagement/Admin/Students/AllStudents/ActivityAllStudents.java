package com.school.schoolmanagement.Admin.Students.AllStudents;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;

import com.school.schoolmanagement.Admin.Adapter.AdapterAllStudents;
import com.school.schoolmanagement.Admin.Model.ClassModel;
import com.school.schoolmanagement.Admin.Model.StudentModel;
import com.school.schoolmanagement.Admin.Students.StudentDetails.ActivityStudentsDetail;
import com.school.schoolmanagement.GlobalViewModel.ViewModel;
import com.school.schoolmanagement.Model.StudentDetails;
import com.school.schoolmanagement.R;
import com.school.schoolmanagement.Utils.Utility;
import com.school.schoolmanagement.databinding.ActivityAllStudentsBinding;

import java.util.ArrayList;
import java.util.List;

public class ActivityAllStudents extends Utility implements AdapterAllStudents.OnStudentClickListener {
    private static final String TAG = "ActivityAllStudents";

    private ActivityAllStudentsBinding binding;
    private List<StudentDetails> studentList;
    private List<StudentDetails> filteredStudentList;
    private ArrayList<ClassModel.Data> classList;
    private AdapterAllStudents studentAdapter;
    private ViewModel viewModel;
    private String authToken;
    private int selectedClassId = -1;

    // Track search state
    private boolean isSearchVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        try {
            binding = ActivityAllStudentsBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());

            // Initialize ViewModel
            viewModel = new ViewModelProvider(this).get(ViewModel.class);

            // Initialize SharedPreferences for auth token
            initializeSharedPreferences();

            // Setup window insets
            setupWindowInsets();

            // Setup UI components
            setupSearchButton();
            setupSearchFunctionality();
            setupRefreshButton();
            setupClassSelection();

            // Initialize lists
            studentList = new ArrayList<>();
            filteredStudentList = new ArrayList<>();
            classList = new ArrayList<>();

            // Setup RecyclerView
            setupRecyclerView();

            setUpToolBar();

            // Initialize data if internet is connected
            if (isInternetConnected(this)) {
                initializeClassData();
            } else {
                showToast("No Internet Connection.");
            }

        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: " + e.getMessage(), e);
            showToast("Error initializing the activity");
        }
    }

    private void setUpToolBar() {
        binding.toolbar.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void initializeSharedPreferences() {
        try {
            // Replace "auth_token_key" with your actual auth token key
            authToken = "Bearer " + pref.getPrefString(this, pref.user_token);

            if (authToken == null || authToken.isEmpty()) {
                Log.w(TAG, "Auth token not found in SharedPreferences");
                // You might want to redirect to login or handle this case
                authToken = "Bearer default_token"; // fallback or handle appropriately
            } else if (!authToken.startsWith("Bearer ")) {
                authToken = "Bearer " + authToken;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error initializing SharedPreferences: " + e.getMessage(), e);
            authToken = "Bearer default_token"; // fallback
        }
    }

    private void setupWindowInsets() {
        try {
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
                if (v != null && insets != null) {
                    Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                    v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                }
                return insets;
            });
        } catch (Exception e) {
            Log.e(TAG, "Error setting up window insets: " + e.getMessage(), e);
        }
    }

    private void setupClassSelection() {
        try {
            // Add click listener for class selection EditText (if it exists in your layout)
            if (binding.edtSelectClass != null) {
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
            }
        } catch (Exception e) {
            Log.e(TAG, "Error setting up class selection: " + e.getMessage(), e);
        }
    }

    private ClassModel.Data createAllClassesOption() {
        try {
            ClassModel.Data allClasses = new ClassModel.Data();
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
        viewModel.getAllClasses(authToken).observe(this, response -> {
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
                if (binding.edtSelectClass != null) {
                    binding.edtSelectClass.setText("All Classes");
                }
                selectedClassId = 0;

                // Load all students initially
                loadStudentsFromAPI();

            } else {
                showToast("No classes found.");
            }
        });
    }

    private void showClassSelectionDialog() {
        try {
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
        } catch (Exception e) {
            Log.e(TAG, "Error showing class selection dialog: " + e.getMessage(), e);
        }
    }

    private void onClassSelected(ClassModel.Data selectedClass) {
        try {
            selectedClassId = selectedClass.getClassId(); // Save the selected class ID
            if (binding.edtSelectClass != null) {
                binding.edtSelectClass.setText(selectedClass.getClassName()); // Update the EditText
            }

            // Reset search when class is selected
            resetSearchView();

            if (selectedClassId == 0) {
                // "All Classes" selected - load all students
                loadStudentsFromAPI();
            } else {
                // Specific class selected - load students by class
                loadStudentsByClass(selectedClassId);
            }

            Log.d(TAG, "Selected Class ID: " + selectedClassId);
            showToast("Selected: " + selectedClass.getClassName());
        } catch (Exception e) {
            Log.e(TAG, "Error handling class selection: " + e.getMessage(), e);
        }
    }

    private void resetSearchView() {
        try {
            // Clear search input
            if (binding.edtSearchStudent != null) {
                binding.edtSearchStudent.setText("");
            }

            // Hide search view
            if (binding.relSearch != null) {
                binding.relSearch.setVisibility(View.GONE);
            }

            // Reset search button color
            if (binding.searchBtn != null) {
                binding.searchBtn.setColorFilter(null);
            }

            // Update search state
            isSearchVisible = false;

            Log.d(TAG, "Search view reset");
        } catch (Exception e) {
            Log.e(TAG, "Error resetting search view: " + e.getMessage(), e);
        }
    }

    private void setupRefreshButton() {
        try {
            // Add refresh button setup if it exists in your layout
            if (binding.refreshBtn != null) {
                binding.refreshBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        resetEverything();
                    }
                });
            }
        } catch (Exception e) {
            Log.e(TAG, "Error setting up refresh button: " + e.getMessage(), e);
        }
    }

    private void resetEverything() {
        try {
            // Reset search view
            resetSearchView();

            // Reset class selection to "All Classes"
            if (binding.edtSelectClass != null) {
                binding.edtSelectClass.setText("All Classes");
            }
            selectedClassId = 0;

            // Clear all lists
            studentList.clear();
            filteredStudentList.clear();

            // Notify adapter
            if (studentAdapter != null) {
                studentAdapter.notifyDataSetChanged();
            }

            // Reload all students
            if (isInternetConnected(this)) {
                loadStudentsFromAPI();
                showToast("Data refreshed");
            } else {
                showToast("No Internet Connection.");
            }

            Log.d(TAG, "Everything reset");
        } catch (Exception e) {
            Log.e(TAG, "Error resetting everything: " + e.getMessage(), e);
        }
    }

    private void setupSearchButton() {
        try {
            if (binding != null && binding.searchBtn != null) {
                binding.searchBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            if (binding != null && binding.relSearch != null) {
                                if (isSearchVisible) {
                                    // Hide search
                                    resetSearchView();
                                } else {
                                    // Show search
                                    binding.searchBtn.setColorFilter(getResources().getColor(R.color.orange));
                                    binding.relSearch.setVisibility(View.VISIBLE);
                                    if (binding.edtSearchStudent != null) {
                                        binding.edtSearchStudent.requestFocus();
                                    }
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

    private void setupSearchFunctionality() {
        try {
            if (binding != null && binding.edtSearchStudent != null) {
                binding.edtSearchStudent.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        // Not needed
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        filterStudents(s != null ? s.toString().trim() : "");
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        // Not needed
                    }
                });
            }
        } catch (Exception e) {
            Log.e(TAG, "Error setting up search functionality: " + e.getMessage(), e);
        }
    }

    private void setupRecyclerView() {
        try {
            if (binding != null) {
                studentAdapter = new AdapterAllStudents(this, filteredStudentList, this);
                GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
                binding.rcAllStudents.setLayoutManager(gridLayoutManager);
                binding.rcAllStudents.setHasFixedSize(true);
                binding.rcAllStudents.setItemAnimator(new DefaultItemAnimator());
                binding.rcAllStudents.setAdapter(studentAdapter);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error setting up RecyclerView: " + e.getMessage(), e);
        }
    }

    private void loadStudentsFromAPI() {
        try {
            showLoading(true);

            if (viewModel == null) {
                Log.e(TAG, "ViewModel is null");
                showLoading(false);
                showToast("Error: ViewModel not initialized");
                return;
            }

            if (authToken == null || authToken.isEmpty()) {
                Log.e(TAG, "Auth token is null or empty");
                showLoading(false);
                showToast("Authentication error. Please login again.");
                return;
            }

            viewModel.getAllStudents(authToken).observe(this, response -> {
                try {
                    showLoading(false);

                    if (response == null) {
                        Log.e(TAG, "Response is null");
                        showToast("No response from server");
                        return;
                    }

                    if (response.isSuccess && response.data.getData() != null) {
                        handleSuccessResponse(response.data.getData());
                    } else {
                        String errorMessage = response.message != null ?
                                response.message : "Failed to fetch students";
                        Log.e(TAG, "API Error: " + errorMessage + ", Code: " + response.code);
                        showToast(errorMessage);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error handling API response: " + e.getMessage(), e);
                    showLoading(false);
                    showToast("Error processing server response");
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error loading students from API: " + e.getMessage(), e);
            showLoading(false);
            showToast("Error connecting to server");
        }
    }

    private void loadStudentsByClass(int classId) {
        try {
            showLoading(true);

            if (viewModel == null) {
                Log.e(TAG, "ViewModel is null");
                showLoading(false);
                showToast("Error: ViewModel not initialized");
                return;
            }

            if (authToken == null || authToken.isEmpty()) {
                Log.e(TAG, "Auth token is null or empty");
                showLoading(false);
                showToast("Authentication error. Please login again.");
                return;
            }

            viewModel.getBasicList(authToken, classId).observe(this, response -> {
                try {
                    showLoading(false);

                    if (response == null) {
                        Log.e(TAG, "Response is null");
                        showToast("No response from server");
                        return;
                    }

                    if (response.isSuccess && response.data.getData() != null) {
                        handleSuccessResponse(response.data.getData());
                    } else {
                        String errorMessage = response.message != null ?
                                response.message : "Failed to fetch students for selected class";
                        Log.e(TAG, "API Error: " + errorMessage + ", Code: " + response.code);
                        showToast(errorMessage);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error handling class students API response: " + e.getMessage(), e);
                    showLoading(false);
                    showToast("Error processing server response");
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error loading students by class from API: " + e.getMessage(), e);
            showLoading(false);
            showToast("Error connecting to server");
        }
    }

    private void handleSuccessResponse(Object data) {
        try {
            // Clear existing data
            studentList.clear();
            filteredStudentList.clear();

            if (data instanceof List) {
                List<?> responseList = (List<?>) data;
                for (Object item : responseList) {
                    if (item instanceof StudentDetails) {
                        studentList.add((StudentDetails) item);
                    }
                }
            }

            // Update filtered list
            filteredStudentList.addAll(studentList);

            // Notify adapter
            if (studentAdapter != null) {
                studentAdapter.notifyDataSetChanged();
            }

            Log.d(TAG, "Successfully loaded " + studentList.size() + " students");
            showToast("Successfully loaded " + studentList.size() + " students");

        } catch (Exception e) {
            Log.e(TAG, "Error handling success response: " + e.getMessage(), e);
            showToast("Error processing student data");
        }
    }

    private void filterStudents(String query) {
        try {
            if (studentList == null || filteredStudentList == null) {
                Log.w(TAG, "Lists not initialized yet, skipping filter");
                return;
            }

            filteredStudentList.clear();

            if (query == null || query.isEmpty()) {
                filteredStudentList.addAll(studentList);
            } else {
                String lowerCaseQuery = query.toLowerCase();
                for (StudentDetails student : studentList) {
                    if (student != null &&
                            student.getStudentName() != null &&
                            student.getStudentName().toLowerCase().contains(lowerCaseQuery)) {
                        filteredStudentList.add(student);
                    }
                }
            }

            if (studentAdapter != null) {
                studentAdapter.notifyDataSetChanged();
            }

            // Optional: Show a message if no results found
            if (filteredStudentList.isEmpty() && !query.isEmpty() && !studentList.isEmpty()) {
                showToast("No students found matching: " + query);
            }

            Log.d(TAG, "Filtered " + studentList.size() + " students to " + filteredStudentList.size() + " results");

        } catch (Exception e) {
            Log.e(TAG, "Error filtering students: " + e.getMessage(), e);
        }
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
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error showing toast: " + e.getMessage(), e);
        }
    }

    @Override
    public void onViewClick(StudentDetails student) {
        try {
            if (student != null) {
                Intent intent = new Intent(this, ActivityStudentsDetail.class);
                intent.putExtra("student_id", student.getStudentId());
                startActivity(intent);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in onViewClick: " + e.getMessage(), e);
        }
    }

    @Override
    public void onEditClick(StudentDetails student) {
        try {
            if (student != null) {
                showEditDialog(student);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in onEditClick: " + e.getMessage(), e);
        }
    }

    @Override
    public void onDeleteClick(StudentDetails student) {
        try {
            if (student != null) {
                showDeleteDialog(student);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in onDeleteClick: " + e.getMessage(), e);
        }
    }

    private void showDeleteDialog(StudentDetails student) {
        try {
            if (student == null) {
                return;
            }

            new AlertDialog.Builder(this)
                    .setTitle("Delete Student")
                    .setMessage("Are you sure you want to delete " +
                            (student.getStudentName() != null ? student.getStudentName() : "this student") + "?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        try {
                            deleteStudent(student.studentId);
                            studentList.remove(student);
                            filteredStudentList.remove(student);
                            if (studentAdapter != null) {
                                studentAdapter.notifyDataSetChanged();
                            }
                            showToast("Student deleted");
                        } catch (Exception e) {
                            Log.e(TAG, "Error deleting student: " + e.getMessage(), e);
                            showToast("Error deleting student");
                        }
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> {
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        } catch (Exception e) {
            Log.e(TAG, "Error showing delete dialog: " + e.getMessage(), e);
        }
    }

    private void deleteStudent(int studentId){
        String auth="Bearer "+pref.getPrefString(this,pref.user_token);
        showLoading(true);
        viewModel.deleteStudent(auth,studentId).observe(this,response->{
            if(response.isSuccess){
                showLoading(false);
            }
            else{
                showLoading(false);
                Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showEditDialog(StudentDetails student) {
        try {
            if (student == null) {
                return;
            }

            new AlertDialog.Builder(this)
                    .setTitle("Edit Student")
                    .setMessage("Are you sure you want to edit " +
                            (student.getStudentName() != null ? student.getStudentName() : "this student") + "?")
                    .setPositiveButton("Edit", (dialog, which) -> {
                        try {
                            // Add your edit functionality here
                            // For example, navigate to edit activity
                            showToast("Edit functionality to be implemented");
                        } catch (Exception e) {
                            Log.e(TAG, "Error starting edit: " + e.getMessage(), e);
                            showToast("Error opening edit screen");
                        }
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> {
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        } catch (Exception e) {
            Log.e(TAG, "Error showing edit dialog: " + e.getMessage(), e);
        }
    }
}