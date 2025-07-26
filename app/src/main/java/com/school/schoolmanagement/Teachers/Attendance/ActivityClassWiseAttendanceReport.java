package com.school.schoolmanagement.Teachers.Attendance;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.school.schoolmanagement.Admin.Model.ClassWiseAttendanceReport;
import com.school.schoolmanagement.GlobalViewModel.ViewModel;
import com.school.schoolmanagement.R;
import com.school.schoolmanagement.Teachers.Adapters.AdapterAllClassesAttendancereport;
import com.school.schoolmanagement.Utils.Utility;
import com.school.schoolmanagement.databinding.ActivityClassWiseAttendanceReportBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class ActivityClassWiseAttendanceReport extends Utility {

    private static final String TAG = "ClassWiseAttendance";
    private ActivityClassWiseAttendanceReportBinding binding;
    private AdapterAllClassesAttendancereport adapter;
    private ViewModel viewModel;
    private Calendar calendar;
    private SimpleDateFormat dateFormat;
    private String selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityClassWiseAttendanceReportBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeComponents();
        setupRecyclerView();
        setupDatePicker();
        setCurrentDate();
        setupObservers();
        loadAttendanceData();
    }

    private void initializeComponents() {
        viewModel = new ViewModelProvider(this).get(ViewModel.class);
        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    }

    private void setupRecyclerView() {
        adapter = new AdapterAllClassesAttendancereport(new ArrayList<>());
        binding.rcAllClassAttendanceReport.setLayoutManager(new LinearLayoutManager(this));
        binding.rcAllClassAttendanceReport.setAdapter(adapter);
    }

    private void setupDatePicker() {
        binding.edtSelectDate.setOnClickListener(v -> showDatePickerDialog());

        // Make sure the EditText is not editable directly
        binding.edtSelectDate.setFocusable(false);
        binding.edtSelectDate.setClickable(true);
    }

    private void setCurrentDate() {
        // Set current date as default
        selectedDate = dateFormat.format(calendar.getTime());
        binding.edtSelectDate.setText(selectedDate);
    }

    private void showDatePickerDialog() {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        selectedDate = dateFormat.format(calendar.getTime());
                        binding.edtSelectDate.setText(selectedDate);

                        // Load attendance data for the selected date
                        loadAttendanceData();
                    }
                },
                year, month, day
        );

        // Optional: Set max date to today
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

        datePickerDialog.show();
    }

    private void setupObservers() {
        // Set up observer but don't call the API here
        // The observer will be triggered when loadAttendanceData() is called
    }

    private void loadAttendanceData() {
        if (selectedDate == null || selectedDate.isEmpty()) {
            Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show();
            return;
        }

        showLoading(true);

        // Add detailed logging
        Log.d(TAG, "Loading attendance data for date: " + selectedDate);
        Log.d(TAG, "Auth token: " + getAuthToken());

        // Call the API and observe the response
        viewModel.getStudentReportClassWise(getAuthToken(), selectedDate).observe(this, response -> {
            Log.d(TAG, "Response received: " + (response != null ? "Not null" : "Null"));

            showLoading(false);

            if (response != null) {
                Log.d(TAG, "Response success: " + response.isSuccess);
                Log.d(TAG, "Response message: " + response.message);
                Log.d(TAG, "Response data: " + (response.data != null ? "Not null" : "Null"));

                if (response.isSuccess && response.data != null) {
                    ClassWiseAttendanceReport attendanceReport = response.data;

                    Log.d(TAG, "Attendance report status: " + attendanceReport.getStatus());
                    Log.d(TAG, "Attendance report message: " + attendanceReport.getMessage());
                    Log.d(TAG, "Attendance report data size: " +
                            (attendanceReport.getData() != null ? attendanceReport.getData().size() : "null"));

                    if (attendanceReport.status == 200) {
                        if (attendanceReport.getData() != null && !attendanceReport.getData().isEmpty()) {
                            adapter.updateData(attendanceReport.getData());
                            showEmptyState(false);
                            Log.d(TAG, "Attendance data loaded successfully with " +
                                    attendanceReport.getData().size() + " records");
                            Toast.makeText(this, "Loaded " + attendanceReport.getData().size() +
                                    " attendance records", Toast.LENGTH_SHORT).show();
                        } else {
                            showEmptyState(true);
                            Toast.makeText(this, "No attendance data found for selected date",
                                    Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "No attendance data found");
                        }
                    } else {
                        String errorMsg = "Error: " + attendanceReport.getMessage();
                        showError(errorMsg);
                        Log.e(TAG, errorMsg);
                    }
                } else {
                    String errorMsg = response.message != null ? response.message : "Failed to load attendance data";
                    showError(errorMsg);
                    Log.e(TAG, "API call failed: " + errorMsg);
                }
            } else {
                String errorMsg = "No response received from server";
                showError(errorMsg);
                Log.e(TAG, errorMsg);
            }
        });
    }

    private String getAuthToken() {
        String token = "Bearer " + pref.getPrefString(this, pref.user_token);
        Log.d(TAG, "Generated auth token: " + token);
        return token;
    }

    private void showLoading(boolean show) {
        Log.d(TAG, "Show loading: " + show);
        if (binding.loader != null) {
            binding.loader.rlLoader.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        binding.rcAllClassAttendanceReport.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void showEmptyState(boolean show) {
        Log.d(TAG, "Show empty state: " + show);
        // You can add an empty state view in your layout if needed
        // For now, we'll just show/hide the RecyclerView
        binding.rcAllClassAttendanceReport.setVisibility(show ? View.GONE : View.VISIBLE);

        if (show) {
            // You can add an empty state TextView or layout here
            Toast.makeText(this, "No attendance records found", Toast.LENGTH_SHORT).show();
        }
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        Log.e(TAG, "Error: " + message);

        // Hide loading and show empty state
        showLoading(false);
        showEmptyState(true);
    }

    // Method to refresh data - call this if you have a refresh button
    public void refreshData() {
        if (selectedDate != null && !selectedDate.isEmpty()) {
            loadAttendanceData();
        } else {
            setCurrentDate();
            loadAttendanceData();
        }
    }

    // Method to get selected date (useful for testing or other purposes)
    public String getSelectedDate() {
        return selectedDate;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}