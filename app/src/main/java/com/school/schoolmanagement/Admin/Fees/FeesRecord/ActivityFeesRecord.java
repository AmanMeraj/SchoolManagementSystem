package com.school.schoolmanagement.Admin.Fees.FeesRecord;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.school.schoolmanagement.Admin.Fees.FeespaidReceipt.ActivityFeesPaidReceipt;
import com.school.schoolmanagement.Admin.Model.ClassModel;
import com.school.schoolmanagement.GlobalViewModel.ViewModel;
import com.school.schoolmanagement.Model.StudentDetails;
import com.school.schoolmanagement.R;
import com.school.schoolmanagement.Utils.Utility;
import com.school.schoolmanagement.databinding.ActivityFeesRecordBinding;

import java.util.ArrayList;
import java.util.List;

public class ActivityFeesRecord extends Utility implements View.OnClickListener {

    private static final String TAG = "ActivityFeesRecord";

    // View Binding
    private ActivityFeesRecordBinding binding;

    // ViewModel and Authentication
    private ViewModel viewModel;
    private String authToken;

    // Class and Student Selection Variables
    private ArrayList<ClassModel.Data> classList;
    private List<StudentDetails> studentList;
    private int selectedClassId = -1;
    private int selectedStudentId = -1;
    private String selectedStudentName = "";
    private String selectedClassName = "";
    private String selectedMonth = "";

    // Month array for dropdown
    private final String[] months = {
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        // Initialize binding
        binding = ActivityFeesRecordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Setup edge-to-edge display
        setupEdgeToEdge();

        // Initialize components
        initializeComponents();

        // Set click listeners
        setClickListeners();

        // Load initial data
        if (isInternetConnected(this)) {
            initializeClassData();
        } else {
            showToast("No Internet Connection.");
        }

        // Setup back button
        binding.toolbar.backBtn.setOnClickListener(view -> finish());
    }

    private void setupEdgeToEdge() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initializeComponents() {
        try {
            // Initialize ViewModel
            viewModel = new ViewModelProvider(this).get(ViewModel.class);

            // Initialize auth token
            authToken = "Bearer " + pref.getPrefString(this, pref.user_token);

            // Initialize lists
            classList = new ArrayList<>();
            studentList = new ArrayList<>();

            Log.d(TAG, "Components initialized successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error initializing components: " + e.getMessage(), e);
            showToast("Error initializing the activity");
        }
    }

    private void setClickListeners() {
        // Set EditText fields as non-editable and clickable
        binding.edtFeeMonth.setFocusable(false);
        binding.edtFeeMonth.setClickable(true);
        binding.edtFeeMonth.setOnClickListener(this);

        binding.edtSelectClass.setFocusable(false);
        binding.edtSelectClass.setClickable(true);
        binding.edtSelectClass.setOnClickListener(this);

        binding.edtSelectStudent.setFocusable(false);
        binding.edtSelectStudent.setClickable(true);
        binding.edtSelectStudent.setOnClickListener(this);

        // Submit button click listener
        binding.btnSubmit.setOnClickListener(this);
    }

    private void initializeClassData() {
        try {
            showLoading(true);

            viewModel.getAllClasses(authToken).observe(this, response -> {
                showLoading(false);

                if (response != null && response.data != null) {
                    classList.clear();
                    classList.addAll(response.data.getData());
                    Log.d(TAG, "Classes loaded: " + classList.size());
                } else {
                    showToast("No classes found.");
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error loading classes: " + e.getMessage(), e);
            showLoading(false);
            showToast("Error loading classes");
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.edt_fee_month) {
            showMonthSelectionDialog();
        } else if (id == R.id.edt_select_class) {
            showClassSelectionDialog();
        } else if (id == R.id.edt_select_student) {
            showStudentSelectionDialog();
        } else if (id == R.id.btn_submit) {
            onSubmitClicked();
        }
    }

    private void showMonthSelectionDialog() {
        try {
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("Select Month")
                    .setItems(months, (dialogInterface, position) -> {
                        selectedMonth = months[position];
                        binding.edtFeeMonth.setText(selectedMonth);
                        dialogInterface.dismiss();
                        Log.d(TAG, "Selected Month: " + selectedMonth);
                    })
                    .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss())
                    .create();

            dialog.show();
        } catch (Exception e) {
            Log.e(TAG, "Error showing month selection dialog: " + e.getMessage(), e);
            showToast("Error showing month selection");
        }
    }

    private void showClassSelectionDialog() {
        try {
            if (classList.isEmpty()) {
                showToast("Loading classes...");
                initializeClassData();
                return;
            }

            String[] classNames = new String[classList.size()];
            for (int i = 0; i < classList.size(); i++) {
                classNames[i] = classList.get(i).getClassName();
            }

            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("Select Class")
                    .setItems(classNames, (dialogInterface, position) -> {
                        ClassModel.Data selectedClass = classList.get(position);
                        onClassSelected(selectedClass);
                        dialogInterface.dismiss();
                    })
                    .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss())
                    .create();

            dialog.show();
        } catch (Exception e) {
            Log.e(TAG, "Error showing class selection dialog: " + e.getMessage(), e);
            showToast("Error showing class selection");
        }
    }

    private void onClassSelected(ClassModel.Data selectedClass) {
        try {
            selectedClassId = selectedClass.getClassId();
            selectedClassName = selectedClass.getClassName();
            binding.edtSelectClass.setText(selectedClassName);

            // Reset student selection
            selectedStudentId = -1;
            selectedStudentName = "";
            binding.edtSelectStudent.setText("");
            binding.edtSelectStudent.setHint("Select Student");

            // Load students for selected class
            loadStudentsByClass(selectedClassId);

            Log.d(TAG, "Selected Class ID: " + selectedClassId + ", Name: " + selectedClassName);
            showToast("Selected: " + selectedClassName);
        } catch (Exception e) {
            Log.e(TAG, "Error handling class selection: " + e.getMessage(), e);
            showToast("Error selecting class");
        }
    }

    private void loadStudentsByClass(int classId) {
        try {
            showLoading(true);

            viewModel.getBasicList(authToken, classId).observe(this, response -> {
                try {
                    showLoading(false);

                    if (response == null) {
                        Log.e(TAG, "Response is null");
                        showToast("No response from server");
                        return;
                    }

                    if (response.isSuccess && response.data.getData() != null) {
                        handleStudentsResponse(response.data.getData());
                    } else {
                        String errorMessage = response.message != null ?
                                response.message : "Failed to fetch students for selected class";
                        Log.e(TAG, "API Error: " + errorMessage);
                        showToast(errorMessage);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error handling students response: " + e.getMessage(), e);
                    showLoading(false);
                    showToast("Error processing server response");
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error loading students by class: " + e.getMessage(), e);
            showLoading(false);
            showToast("Error connecting to server");
        }
    }

    private void handleStudentsResponse(Object data) {
        try {
            studentList.clear();

            if (data instanceof List) {
                List<?> responseList = (List<?>) data;
                for (Object item : responseList) {
                    if (item instanceof StudentDetails) {
                        studentList.add((StudentDetails) item);
                    }
                }
            }

            Log.d(TAG, "Successfully loaded " + studentList.size() + " students");
        } catch (Exception e) {
            Log.e(TAG, "Error handling students response: " + e.getMessage(), e);
        }
    }

    private void showStudentSelectionDialog() {
        try {
            if (selectedClassId == -1) {
                showToast("Please select a class first");
                return;
            }

            if (studentList.isEmpty()) {
                showToast("No students found for selected class");
                return;
            }

            String[] studentNames = new String[studentList.size()];
            for (int i = 0; i < studentList.size(); i++) {
                studentNames[i] = studentList.get(i).getStudentName();
            }

            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("Select Student")
                    .setItems(studentNames, (dialogInterface, position) -> {
                        StudentDetails selectedStudent = studentList.get(position);
                        onStudentSelected(selectedStudent);
                        dialogInterface.dismiss();
                    })
                    .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss())
                    .create();

            dialog.show();
        } catch (Exception e) {
            Log.e(TAG, "Error showing student selection dialog: " + e.getMessage(), e);
            showToast("Error showing student selection");
        }
    }

    private void onStudentSelected(StudentDetails student) {
        try {
            selectedStudentId = student.getStudentId();
            selectedStudentName = student.getStudentName();
            binding.edtSelectStudent.setText(selectedStudentName);

            Log.d(TAG, "Selected Student ID: " + selectedStudentId + ", Name: " + selectedStudentName);
            showToast("Selected: " + selectedStudentName);
        } catch (Exception e) {
            Log.e(TAG, "Error handling student selection: " + e.getMessage(), e);
            showToast("Error selecting student");
        }
    }

    private void onSubmitClicked() {
        try {
            // Validate all fields
            if (selectedMonth.isEmpty()) {
                showToast("Please select a month");
                return;
            }

            if (selectedClassId == -1) {
                showToast("Please select a class");
                return;
            }

            if (selectedStudentId == -1) {
                showToast("Please select a student");
                return;
            }

            // Navigate to fees record details activity
            navigateToFeesRecordDetails();

        } catch (Exception e) {
            Log.e(TAG, "Error in submit: " + e.getMessage(), e);
            showToast("Error processing submission");
        }
    }

    private void navigateToFeesRecordDetails() {
        try {
            // Replace 'FeesRecordDetailsActivity.class' with your actual target activity
            Intent intent = new Intent(this, ActivityFeesPaidReceipt.class);

            // Pass all selected data as extras
            intent.putExtra("SELECTED_MONTH", selectedMonth);
            intent.putExtra("CLASS_ID", selectedClassId);
            intent.putExtra("CLASS_NAME", selectedClassName);
            intent.putExtra("STUDENT_ID", selectedStudentId);
            intent.putExtra("STUDENT_NAME", selectedStudentName);

            Log.d(TAG, "Navigating to FeesRecordDetailsActivity with data:");
            Log.d(TAG, "Month: " + selectedMonth);
            Log.d(TAG, "Class ID: " + selectedClassId + ", Name: " + selectedClassName);
            Log.d(TAG, "Student ID: " + selectedStudentId + ", Name: " + selectedStudentName);

            startActivity(intent);

        } catch (Exception e) {
            Log.e(TAG, "Error navigating to fees record details: " + e.getMessage(), e);
            showToast("Error opening fees record details");
        }
    }

    private void showLoading(boolean isLoading) {
        try {
            // Assuming you have a loader in your layout or can add one
            // If you don't have a loader, you can add a ProgressBar to your layout
            // For now, we'll just log the loading state
            Log.d(TAG, "Loading: " + isLoading);

            // If you have a loader view, uncomment and modify this:
            // if (binding.loader != null && binding.loader.rlLoader != null) {
            //     binding.loader.rlLoader.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            // }
        } catch (Exception e) {
            Log.e(TAG, "Error showing/hiding loader: " + e.getMessage(), e);
        }
    }

    private void showToast(String message) {
        try {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error showing toast: " + e.getMessage(), e);
        }
    }

    // Getter methods for accessing selected values
    public String getSelectedMonth() {
        return selectedMonth;
    }

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up binding
        binding = null;
    }
}