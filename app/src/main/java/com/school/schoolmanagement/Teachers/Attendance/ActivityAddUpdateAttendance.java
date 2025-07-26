package com.school.schoolmanagement.Teachers.Attendance;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.school.schoolmanagement.Admin.Model.ClassModel;
import com.school.schoolmanagement.GlobalViewModel.ViewModel;
import com.school.schoolmanagement.HelperClasses.ClassApiHelper;
import com.school.schoolmanagement.HelperClasses.StudentHelper;
import com.school.schoolmanagement.R;
import com.school.schoolmanagement.ScannerActivity;
import com.school.schoolmanagement.Teachers.Exams.ActivityTeachersInsertMarks;
import com.school.schoolmanagement.Utils.Utility;
import com.school.schoolmanagement.databinding.ActivityAddUpdateAttendanceBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ActivityAddUpdateAttendance extends Utility implements ClassApiHelper.ClassListCallback {

    private ActivityAddUpdateAttendanceBinding binding;
    private Calendar calendar;
    private SimpleDateFormat dateFormat;
    ClassApiHelper classApiHelper;
    String from="";
    private final ArrayList<ClassModel.Data> classList = new ArrayList<>();
    private final ArrayList<String> classNames = new ArrayList<>();
    private ArrayAdapter<String> classAdapter;
    private int selectedClassId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddUpdateAttendanceBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        from= getIntent().getStringExtra("from");
        if(from!=null&& from.matches("EMPLOYEE")){
            binding.relDate.setVisibility(View.VISIBLE);
            binding.relClass.setVisibility(View.GONE);
        }else {
            binding.relDate.setVisibility(View.VISIBLE);
            binding.relClass.setVisibility(View.VISIBLE);
        }
        initializeComponents();
        setupToolbar();
        setupTabSwitching();
        setupDatePicker();
        setupClassAutoComplete(); // Only call this method
        setupButtons();
        if(from.matches("STUDENT")){
            loadClassList();
        }

    }

    private void initializeComponents() {
        // Initialize calendar
        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        classApiHelper = new ClassApiHelper(this);
    }

    private void setupAutoCompleteTextViews() {
        setupClassAutoComplete();
    }

    private void handleClassSelection(int position) {
        if (position > 0 && position <= classList.size()) {
            // Adjust position since we have "Select Class" at index 0
            int actualPosition = position - 1;
            if (actualPosition < classList.size()) {
                selectedClassId = classList.get(actualPosition).getClassId();
            }
        } else {
            selectedClassId = -1;
        }
    }

    private void setupClassAutoComplete() {
        // Initialize with empty list first
        classNames.clear();
        classNames.add("Select Class");

        classAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, classNames);
        classAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.edtSelectClass.setAdapter(classAdapter);

        // Set threshold to show dropdown on click
        binding.edtSelectClass.setThreshold(1);
        binding.edtSelectClass.setHint("Select Class");

        binding.edtSelectClass.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                handleClassSelection(position);
            }
        });

        // Handle focus change to show dropdown
        binding.edtSelectClass.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                binding.edtSelectClass.showDropDown();
            }
        });

        // Handle click to show dropdown
        binding.edtSelectClass.setOnClickListener(v -> {
            binding.edtSelectClass.showDropDown();
        });
    }

    private void setupToolbar() {
        View backButton = binding.toolbar.backBtn.findViewById(R.id.back_btn);
        if (backButton != null) {
            backButton.setOnClickListener(v -> onBackPressed());
        }
    }

    private void setupTabSwitching() {
        // Manual attendance tab
        binding.tvStudentWise.setOnClickListener(v -> {
            switchToManualMode();
        });

        // Card scanning tab
        binding.tvScanInvoice.setOnClickListener(v -> {
            switchToScanningMode();
        });
    }

    private void switchToManualMode() {
        binding.tvStudentWise.setBackgroundResource(R.drawable.tab_bg);
        binding.tvStudentWise.setTextColor(getResources().getColor(R.color.white));
        binding.tvScanInvoice.setBackgroundResource(0);
        binding.tvScanInvoice.setTextColor(getResources().getColor(R.color.grey));

        binding.linearStudentWise.setVisibility(View.VISIBLE);
        binding.linearScanInvoice.setVisibility(View.GONE);
        binding.tvHeading.setText("Add/Update Attendance");
    }

    private void switchToScanningMode() {
        binding.tvScanInvoice.setBackgroundResource(R.drawable.tab_bg);
        binding.tvScanInvoice.setTextColor(getResources().getColor(R.color.white));
        binding.tvStudentWise.setBackgroundResource(0);
        binding.tvStudentWise.setTextColor(getResources().getColor(R.color.grey));

        binding.linearStudentWise.setVisibility(View.GONE);
        binding.linearScanInvoice.setVisibility(View.VISIBLE);
        binding.tvHeading.setText("Scan Student Card");
    }

    private void setupDatePicker() {
        binding.edtSelectDate.setOnClickListener(v -> {
            showDatePickerDialog();
        });
    }

    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    binding.edtSelectDate.setText(dateFormat.format(calendar.getTime()));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    // REMOVED: setupClassDropdown() method - this was overriding the API data

    private void loadClassList() {
        // This method calls the API to fetch class list
        if (classApiHelper != null) {
            classApiHelper.fetchAllClasses(this); // Make sure this method exists in ClassApiHelper
        }
    }

    private void setupButtons() {
        setupAutoCompleteTextViews();
        // Submit button for manual attendance
        binding.submitBtn.setOnClickListener(v -> {

            if(from.matches("EMPLOYEE")){
                if(validateForm2()){
                    submitEmployeeAttendance();
                }
            }else{
                if (validateForm()&&validateForm2()) {
                submitAttendance();
                 }
            }
        });

        // Scan button for card scanning
        binding.btnScanAgain.setOnClickListener(v -> {
            // Launch the global scanner activity
            launchScannerActivity();
        });
    }

    private boolean validateForm() {
        boolean isValid = true;

        if (binding.edtSelectClass.getText().toString().isEmpty() ||
                binding.edtSelectClass.getText().toString().equals("Select Class")) {
            Toast.makeText(this, "Please select a class", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        return isValid;
    }
    private boolean validateForm2() {
        boolean isValid = true;

        if (binding.edtSelectDate.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        return isValid;
    }

    private void submitAttendance() {
        String date = binding.edtSelectDate.getText().toString();
        String className = binding.edtSelectClass.getText().toString();

        // Navigate to attendance list screen with selected parameters
        Intent intent = new Intent(this, ActivityMarkAttendance.class);
        intent.putExtra("selected_date", date);
        intent.putExtra("selected_class", className);
        intent.putExtra("selected_class_id", selectedClassId);
        intent.putExtra("from", from);
        startActivity(intent);
    }
    private void submitEmployeeAttendance() {
        String date = binding.edtSelectDate.getText().toString();

        // Navigate to attendance list screen with selected parameters
        Intent intent = new Intent(this, ActivityMarkEmployeesAttendance.class);
        intent.putExtra("selected_date", date);
        intent.putExtra("from", from);
        startActivity(intent);
    }

    private void launchScannerActivity() {
        // Launch the global scanner activity
        Intent scannerIntent = new Intent(ActivityAddUpdateAttendance.this, ScannerActivity.class);
        startActivityForResult(scannerIntent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            // Process scanner result
            String scannedData = data.getStringExtra("SCAN_RESULT");
            if (scannedData != null && !scannedData.isEmpty()) {
                // Process the scanned card data
                processScannedData(scannedData);
            } else {
                Toast.makeText(this, "Failed to scan the card. Please try again.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void processScannedData(String scannedData) {
        // Process the scanned card data
        // This could be parsing a student ID or other information
        Toast.makeText(this, "Card scanned successfully: " + scannedData, Toast.LENGTH_SHORT).show();

        // You might want to perform attendance marking based on the scanned card
        // or navigate to another screen with the scanned data
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    private void showLoading(String message) {
        binding.loader.rlLoader.setVisibility(View.VISIBLE);
        runOnUiThread(() -> Toast.makeText(this, message, Toast.LENGTH_SHORT).show());
    }

    private void hideLoading() {
        // Implement loading indicator hiding if you have one
        // You can add a progress bar or loading overlay here
        binding.loader.rlLoader.setVisibility(View.GONE);
    }

    // ClassApiHelper.ClassListCallback implementation
    @Override
    public void onSuccess(ArrayList<ClassModel.Data> classList) {
        this.classList.clear();
        this.classList.addAll(classList);
        updateClassArrayAdapter();
        hideLoading();
    }

    @Override
    public void onError(String errorMessage) {
        hideLoading();
        runOnUiThread(() -> {
            Toast.makeText(this, "Class Loading Error: " + errorMessage, Toast.LENGTH_LONG).show();
        });
    }

    @Override
    public void onLoading(boolean isLoading) {
        if (isLoading) {
            showLoading("Loading classes...");
        } else {
            hideLoading();
        }
    }

    private void updateClassArrayAdapter() {
        runOnUiThread(() -> {
            classNames.clear();
            classNames.add("Select Class");

            for (ClassModel.Data classData : classList) {
                classNames.add(classData.getClassName());
            }

            classAdapter.notifyDataSetChanged();
        });
    }
}