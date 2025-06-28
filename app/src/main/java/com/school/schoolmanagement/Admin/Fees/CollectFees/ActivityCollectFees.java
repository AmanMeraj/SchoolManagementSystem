package com.school.schoolmanagement.Admin.Fees.CollectFees;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.school.schoolmanagement.Admin.Adapter.AdapterAllStudents;
import com.school.schoolmanagement.Admin.Model.ClassModel;
import com.school.schoolmanagement.GlobalViewModel.ViewModel;
import com.school.schoolmanagement.Model.StudentDetails;
import com.school.schoolmanagement.R;
import com.school.schoolmanagement.ScannerActivity;
import com.school.schoolmanagement.Utils.Utility;
import com.school.schoolmanagement.databinding.ActivityCollectFeesBinding;

import java.util.ArrayList;
import java.util.List;

public class ActivityCollectFees extends Utility implements View.OnClickListener, AdapterAllStudents.OnStudentClickListener {

    private static final String TAG = "ActivityCollectFees";

    // View Binding
    private ActivityCollectFeesBinding binding;

    // Activity result launcher for scanner
    private ActivityResultLauncher<Intent> scannerLauncher;

    // Class and Student Selection Variables
    private ViewModel viewModel;
    private String authToken;
    private ArrayList<ClassModel.Data> classList;
    private List<StudentDetails> studentList;
    private List<StudentDetails> filteredStudentList;
    private AdapterAllStudents studentAdapter;
    private int selectedClassId = -1;
    private int selectedStudentId = -1; // This will store the selected student ID
    private String selectedStudentName = "";
    private boolean isSearchVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityCollectFeesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Setup Edge-to-Edge display
        setupEdgeToEdge();

        // Initialize ViewModel and other components
        initializeComponents();

        // Set click listeners
        setClickListeners();

        // Initialize scanner result launcher
        initScannerLauncher();

        // Set default tab (Student Wise is already selected in XML)
        updateTabUI(binding.tvStudentWise);

        // Initialize class data if internet is connected
        if (isInternetConnected(this)) {
            initializeClassData();
        } else {
            showToast("No Internet Connection.");
        }

        binding.toolbar.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void initializeComponents() {
        try {
            // Initialize ViewModel
            viewModel = new ViewModelProvider(this).get(ViewModel.class);

            // Initialize SharedPreferences for auth token
            initializeSharedPreferences();

            // Initialize lists
            classList = new ArrayList<>();
            studentList = new ArrayList<>();
            filteredStudentList = new ArrayList<>();

            // Setup student selection components
            setupClassSelection();
            setupStudentSelection();

        } catch (Exception e) {
            Log.e(TAG, "Error initializing components: " + e.getMessage(), e);
            showToast("Error initializing the activity");
        }
    }

    private void initializeSharedPreferences() {
        try {
            authToken = "Bearer " + pref.getPrefString(this, pref.user_token);

            if (authToken == null || authToken.isEmpty()) {
                Log.w(TAG, "Auth token not found in SharedPreferences");
                authToken = "Bearer default_token"; // fallback
            } else if (!authToken.startsWith("Bearer ")) {
                authToken = "Bearer " + authToken;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error initializing SharedPreferences: " + e.getMessage(), e);
            authToken = "Bearer default_token"; // fallback
        }
    }

    private void setupClassSelection() {
        try {
            // Assuming you have edtSelectClass in your layout
            if (binding.edtSelectClass != null) {
                binding.edtSelectClass.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (classList.isEmpty()) {
                            initializeClassData();
                        } else {
                            showClassSelectionDialog();
                        }
                    }
                });

                binding.edtSelectClass.setFocusable(false);
                binding.edtSelectClass.setClickable(true);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error setting up class selection: " + e.getMessage(), e);
        }
    }

    private void setupStudentSelection() {
        try {
            // Assuming you have edtSelectStudent in your layout
            if (binding.edtSelectStudent != null) {
                binding.edtSelectStudent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (selectedClassId == -1) {
                            showToast("Please select a class first");
                            return;
                        }
                        showStudentSelectionDialog();
                    }
                });

                binding.edtSelectStudent.setFocusable(false);
                binding.edtSelectStudent.setClickable(true);
            }

            // Setup search button for students
            toggleStudentSearch();
        } catch (Exception e) {
            Log.e(TAG, "Error setting up student selection: " + e.getMessage(), e);
        }
    }


    private void initializeClassData() {
        showLoading(true);
        viewModel.getAllClasses(authToken).observe(this, response -> {
            showLoading(false);
            if (response != null) {
                classList.clear();

                if (response.data != null) {
                    classList.addAll(response.data.getData());
                }

                Log.d(TAG, "Classes loaded: " + classList.size());
            } else {
                showToast("No classes found.");
            }
        });
    }

    private void showClassSelectionDialog() {
        try {
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
        }
    }

    private void onClassSelected(ClassModel.Data selectedClass) {
        try {
            selectedClassId = selectedClass.getClassId();
            if (binding.edtSelectClass != null) {
                binding.edtSelectClass.setText(selectedClass.getClassName());
            }

            // Reset student selection
            selectedStudentId = -1;
            selectedStudentName = "";
            if (binding.edtSelectStudent != null) {
                binding.edtSelectStudent.setText("");
                binding.edtSelectStudent.setHint("Select Student");
            }

            // Load students for selected class
            loadStudentsByClass(selectedClassId);

            Log.d(TAG, "Selected Class ID: " + selectedClassId);
            showToast("Selected: " + selectedClass.getClassName());
        } catch (Exception e) {
            Log.e(TAG, "Error handling class selection: " + e.getMessage(), e);
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
            filteredStudentList.clear();

            if (data instanceof List) {
                List<?> responseList = (List<?>) data;
                for (Object item : responseList) {
                    if (item instanceof StudentDetails) {
                        studentList.add((StudentDetails) item);
                    }
                }
            }

            filteredStudentList.addAll(studentList);
            Log.d(TAG, "Successfully loaded " + studentList.size() + " students");

        } catch (Exception e) {
            Log.e(TAG, "Error handling students response: " + e.getMessage(), e);
        }
    }

    private void showStudentSelectionDialog() {
        try {
            if (filteredStudentList.isEmpty()) {
                showToast("No students found for selected class");
                return;
            }

            String[] studentNames = new String[filteredStudentList.size()];
            for (int i = 0; i < filteredStudentList.size(); i++) {
                studentNames[i] = filteredStudentList.get(i).getStudentName();
            }

            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("Select Student")
                    .setItems(studentNames, (dialogInterface, position) -> {
                        StudentDetails selectedStudent = filteredStudentList.get(position);
                        onStudentSelected(selectedStudent);
                        dialogInterface.dismiss();
                    })
                    .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss())
                    .create();

            dialog.show();
        } catch (Exception e) {
            Log.e(TAG, "Error showing student selection dialog: " + e.getMessage(), e);
        }
    }

    private void onStudentSelected(StudentDetails student) {
        try {
            selectedStudentId = student.getStudentId(); // Store the selected student ID
            selectedStudentName = student.getStudentName();

            if (binding.edtSelectStudent != null) {
                binding.edtSelectStudent.setText(selectedStudentName);
            }

            Log.d(TAG, "Selected Student ID: " + selectedStudentId + ", Name: " + selectedStudentName);
            showToast("Selected: " + selectedStudentName);

            // Navigate to fee collection activity with student details
            navigateToFeeCollectionActivity(student);

        } catch (Exception e) {
            Log.e(TAG, "Error handling student selection: " + e.getMessage(), e);
        }
    }

    /**
     * Navigate to fee collection activity with student details
     * @param student Selected student details
     */
    private void navigateToFeeCollectionActivity(StudentDetails student) {
        try {
            // Replace 'FeeCollectionActivity.class' with your actual target activity
            Intent intent = new Intent(this, ActivityCollectFees2.class);

            // Pass student details as extras
            intent.putExtra("STUDENT_ID", student.getStudentId());
            intent.putExtra("CLASS_ID", selectedClassId);
            Log.d(TAG, "Navigating to FeeCollectionActivity with Student ID: " + student.getStudentId());

            startActivity(intent);

        } catch (Exception e) {
            Log.e(TAG, "Error navigating to fee collection activity: " + e.getMessage(), e);
            showToast("Error opening fee collection screen");
        }
    }

    private void toggleStudentSearch() {
        try {
            // Show class selection dialog when search button is clicked
            if (classList.isEmpty()) {
                // If classes are not loaded yet, load them first
                showToast("Loading classes...");
                initializeClassData();
            } else {
                // Show class selection dialog
                showClassSelectionDialog();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in toggleStudentSearch: " + e.getMessage(), e);
            showToast("Error loading classes");
        }
    }

    private void filterStudents(String query) {
        try {
            if (studentList == null || filteredStudentList == null) {
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

            if (filteredStudentList.isEmpty() && !query.isEmpty() && !studentList.isEmpty()) {
                showToast("No students found matching: " + query);
            }

        } catch (Exception e) {
            Log.e(TAG, "Error filtering students: " + e.getMessage(), e);
        }
    }

    private void setupEdgeToEdge() {
        // Enable edge-to-edge display
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        // For Android 11+ devices
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            getWindow().setDecorFitsSystemWindows(false);

            // Optional: control system bar appearance if needed
            getWindow().getInsetsController().setSystemBarsBehavior(
                    WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
        } else {
            // For older devices
            getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        // Handle insets to ensure content doesn't overlap with system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void setClickListeners() {
        binding.tvStudentWise.setOnClickListener(this);
        binding.tvFamilyWise.setOnClickListener(this);
        binding.tvScanInvoice.setOnClickListener(this);
        binding.btnSubmit.setOnClickListener(this);
    }

    private void initScannerLauncher() {
        scannerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        String scannedResult = result.getData().getStringExtra("scanned_result");
                        handleScannedResult(scannedResult);
                    }
                }
        );
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.tv_student_wise) {
            // Student wise tab clicked
            updateTabUI(binding.tvStudentWise);
            showStudentWiseContent();
        } else if (id == R.id.tv_family_wise) {
            // Family wise tab clicked
            updateTabUI(binding.tvFamilyWise);
            showFamilyWiseContent();
        } else if (id == R.id.tv_scan_invoice) {
            // Scan invoice tab clicked
            updateTabUI(binding.tvScanInvoice);
            startScanner();
        } else if (id == R.id.btn_submit) {
            // Submit button clicked
            submitFees();
        }
    }

    private void updateTabUI(View selectedTab) {
        // Reset all tabs to default state
        binding.tvStudentWise.setBackgroundResource(0);
        binding.tvFamilyWise.setBackgroundResource(0);
        binding.tvScanInvoice.setBackgroundResource(0);

        binding.tvStudentWise.setTextColor(getResources().getColor(R.color.grey));
        binding.tvFamilyWise.setTextColor(getResources().getColor(R.color.grey));
        binding.tvScanInvoice.setTextColor(getResources().getColor(R.color.grey));

        // Set selected tab to active state
        selectedTab.setBackgroundResource(R.drawable.tab_bg);
        if (selectedTab instanceof android.widget.TextView) {
            ((android.widget.TextView) selectedTab).setTextColor(getResources().getColor(R.color.white));
        }
    }

    private void showStudentWiseContent() {
        // Update heading
        binding.tvHeading.setText("Collect Fees for A Student");

        // Show Student Wise layout, hide others
        binding.linearStudentWise.setVisibility(View.VISIBLE);
        binding.linearFamilyWise.setVisibility(View.GONE);
        binding.linearScanInvoice.setVisibility(View.GONE);
    }

    private void showFamilyWiseContent() {
        // Update heading
        binding.tvHeading.setText("Collect Fees for A Family");

        // Show Family Wise layout, hide others
        binding.linearStudentWise.setVisibility(View.GONE);
        binding.linearFamilyWise.setVisibility(View.VISIBLE);
        binding.linearScanInvoice.setVisibility(View.GONE);
    }

    private void startScanner() {
        // Update heading
        binding.tvHeading.setText("Scan Invoice QR Code");

        // Launch scanner activity
        Intent scannerIntent = new Intent(this, ScannerActivity.class);
        scannerLauncher.launch(scannerIntent);
    }

    private void handleScannedResult(String scannedResult) {
        // Process the scanned result
        if (scannedResult != null && !scannedResult.isEmpty()) {
            // Show scan invoice layout
            binding.linearStudentWise.setVisibility(View.GONE);
            binding.linearFamilyWise.setVisibility(View.GONE);
            binding.linearScanInvoice.setVisibility(View.VISIBLE);
        } else {
            // Handle invalid scan
            updateTabUI(binding.tvStudentWise);
            showStudentWiseContent();
        }
    }

    private void submitFees() {
        try {
            // Validate selection based on current tab
            if (binding.linearStudentWise.getVisibility() == View.VISIBLE) {
                // Student wise fee collection
                if (selectedStudentId == -1) {
                    showToast("Please select a student first");
                    return;
                }

                // Use selectedStudentId for fee collection
                Log.d(TAG, "Submitting fees for Student ID: " + selectedStudentId);
                showToast("Collecting fees for: " + selectedStudentName + " (ID: " + selectedStudentId + ")");

                // Add your fee collection logic here
                // Example: collectFeesForStudent(selectedStudentId);

            } else if (binding.linearFamilyWise.getVisibility() == View.VISIBLE) {
                // Family wise fee collection logic
                showToast("Family wise fee collection - implement as needed");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error submitting fees: " + e.getMessage(), e);
            showToast("Error processing fee submission");
        }
    }

    // Implement required methods from AdapterAllStudents.OnStudentClickListener
    @Override
    public void onViewClick(StudentDetails student) {
        // Handle view click - can also navigate to fee collection activity
        navigateToFeeCollectionActivity(student);
    }

    @Override
    public void onEditClick(StudentDetails student) {
        // Handle edit click if needed
    }

    @Override
    public void onDeleteClick(StudentDetails student) {
        // Handle delete click if needed
    }

    private void showLoading(boolean isLoading) {
        try {
            if (binding != null && binding.loader != null) {
                binding.loader.rlLoader.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            }
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

    // Getter methods to access selected values from other classes if needed
    public int getSelectedStudentId() {
        return selectedStudentId;
    }

    public String getSelectedStudentName() {
        return selectedStudentName;
    }

    public int getSelectedClassId() {
        return selectedClassId;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up binding
        binding = null;
    }
}