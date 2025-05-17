package com.school.schoolmanagement.Teachers.Attendance;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.school.schoolmanagement.R;
import com.school.schoolmanagement.ScannerActivity;
import com.school.schoolmanagement.databinding.ActivityAddUpdateAttendanceBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ActivityAddUpdateAttendance extends AppCompatActivity {

    private ActivityAddUpdateAttendanceBinding binding;
    private Calendar calendar;
    private SimpleDateFormat dateFormat;

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

        // Initialize calendar
        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        setupToolbar();
        setupTabSwitching();
        setupDatePicker();
        setupClassDropdown();
        setupButtons();
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

    private void setupClassDropdown() {
        // Sample class list - replace with your actual data source
        List<String> classList = new ArrayList<>();
        classList.add("Class 1");
        classList.add("Class 2");
        classList.add("Class 3");
        classList.add("Class 4");
        classList.add("Class 5");

        ArrayAdapter<String> classAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                classList
        );

        binding.edtSelectClass.setAdapter(classAdapter);
        binding.edtSelectClass.setOnClickListener(v -> {
            binding.edtSelectClass.showDropDown();
        });
    }

    private void setupButtons() {
        // Submit button for manual attendance
        binding.submitBtn.setOnClickListener(v -> {
            if (validateForm()) {
                // Process the attendance data
                submitAttendance();
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

        if (binding.edtSelectDate.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        if (binding.edtSelectClass.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please select a class", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        return isValid;
    }

    private void submitAttendance() {
        String date = binding.edtSelectDate.getText().toString();
        String className = binding.edtSelectClass.getText().toString();

        // Show a confirmation message
        Toast.makeText(this, "Redirecting to attendance list for " + className + " on " + date, Toast.LENGTH_SHORT).show();

        // Navigate to attendance list screen with selected parameters
//        Intent intent = new Intent(this, AttendanceListActivity.class);
//        intent.putExtra("selected_date", date);
//        intent.putExtra("selected_class", className);
//        startActivity(intent);
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
}