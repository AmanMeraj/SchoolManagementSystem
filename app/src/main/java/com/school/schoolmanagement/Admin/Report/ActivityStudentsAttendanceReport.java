package com.school.schoolmanagement.Admin.Report;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.school.schoolmanagement.Adapters.AttendanceAdapter;
import com.school.schoolmanagement.HelperClasses.AttendanceApiHelper;
import com.school.schoolmanagement.HelperClasses.DataExportHelper;
import com.school.schoolmanagement.Model.AttendanceEntries;
import com.school.schoolmanagement.Model.StudentsAttendance;
import com.school.schoolmanagement.R;
import com.school.schoolmanagement.databinding.ActivityStudentsMonthlyAttendanceReportBinding;

import java.text.SimpleDateFormat;
import java.util.*;

public class ActivityStudentsAttendanceReport extends AppCompatActivity {

    private static final String TAG = "AttendanceReport";

    ActivityStudentsMonthlyAttendanceReportBinding binding;

    // Data received from previous activity
    private int classId;
    private String selectedMonth;
    private String selectedYear;
    private String monthName;
    private String className;
    private String reportType;
    private String reportTitle;

    // Calendar and date utilities
    private Calendar calendar;
    private int totalDaysInMonth;
    private Map<String, String> dayNamesMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityStudentsMonthlyAttendanceReportBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Receive data from previous activity
        receiveIntentData();

        // Initialize calendar and generate day headers
        initializeCalendar();

        // Setup UI components
        setupReportHeader();
        generateDayHeaders();

        // Load attendance data (replace dummy data with actual API call)
        loadAttendanceData();
        setupExportButtons();
    }

    private void receiveIntentData() {
        Intent intent = getIntent();
        if (intent != null) {
            classId = intent.getIntExtra("class_id", -1);
            selectedMonth = intent.getStringExtra("month");
            selectedYear = intent.getStringExtra("year");
            monthName = intent.getStringExtra("month_name");
            className = intent.getStringExtra("class_name");
            reportType = intent.getStringExtra("report_type");
            reportTitle = intent.getStringExtra("report_title");

            // Log received data for debugging
            Log.d(TAG, "=== Received Intent Data ===");
            Log.d(TAG, "Class ID: " + classId);
            Log.d(TAG, "Selected Month: " + selectedMonth);
            Log.d(TAG, "Selected Year: " + selectedYear);
            Log.d(TAG, "Month Name: " + monthName);
            Log.d(TAG, "Class Name: " + className);
            Log.d(TAG, "Report Type: " + reportType);
            Log.d(TAG, "Report Title: " + reportTitle);

            // Validate received data
            if (classId == -1 || selectedMonth == null || selectedYear == null) {
                Toast.makeText(this, "Invalid data received. Please try again.", Toast.LENGTH_LONG).show();
                finish();
                return;
            }
        } else {
            Toast.makeText(this, "No data received. Please try again.", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void setupReportHeader() {
        // Set report title and information
        if (reportTitle != null) {
            // Assuming you have a TextView for title in your layout
            // binding.txtReportTitle.setText(reportTitle);
        }

        // Set class and month information
        String headerInfo = String.format("Class: %s | Month: %s %s",
                className != null ? className : "N/A",
                monthName != null ? monthName : "N/A",
                selectedYear != null ? selectedYear : "N/A");

        // Log header info
        Log.d(TAG, "Header Info: " + headerInfo);

        // If you have a header TextView in your layout, set it here
        // binding.txtHeaderInfo.setText(headerInfo);
    }

    private void initializeCalendar() {
        try {
            calendar = Calendar.getInstance();
            int month = Integer.parseInt(selectedMonth) - 1; // Calendar months are 0-based
            int year = Integer.parseInt(selectedYear);

            calendar.set(year, month, 1);
            totalDaysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

            Log.d(TAG, "Calendar initialized - Month: " + month + ", Year: " + year +
                    ", Total Days: " + totalDaysInMonth);

            // Generate day names map based on actual calendar
            generateDayNamesMap();

        } catch (NumberFormatException e) {
            Log.e(TAG, "Error parsing month/year: " + e.getMessage());
            Toast.makeText(this, "Invalid date format", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void generateDayNamesMap() {
        dayNamesMap = new LinkedHashMap<>();
        Calendar tempCalendar = (Calendar) calendar.clone();

        // Day name format (SUN, MON, TUE, etc.)
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEE", Locale.getDefault());

        for (int day = 1; day <= totalDaysInMonth; day++) {
            tempCalendar.set(Calendar.DAY_OF_MONTH, day);
            String dayName = dayFormat.format(tempCalendar.getTime()).toUpperCase();
            dayNamesMap.put(String.valueOf(day), dayName);
        }

        Log.d(TAG, "Generated day names map with " + dayNamesMap.size() + " days");
    }

    private void setupRecyclerView(List<AttendanceEntries> students) {
        if (students != null && !students.isEmpty()) {
            AttendanceAdapter adapter = new AttendanceAdapter(this, students, totalDaysInMonth);
            binding.rvAttendance.setLayoutManager(new LinearLayoutManager(this));
            binding.rvAttendance.setAdapter(adapter);

            Log.d(TAG, "RecyclerView setup with " + students.size() + " students");
        } else {
            Log.w(TAG, "No student data available");
            Toast.makeText(this, "No attendance data found for the selected criteria",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void generateDayHeaders() {
        if (binding.headerRow != null && binding.headerRow.layoutDaysHeader != null) {
            binding.headerRow.layoutDaysHeader.removeAllViews();
            LayoutInflater inflater = LayoutInflater.from(this);

            // Sort days numerically
            List<Integer> days = new ArrayList<>();
            for (String key : dayNamesMap.keySet()) {
                days.add(Integer.parseInt(key));
            }
            Collections.sort(days);

            for (int day : days) {
                LinearLayout layout = createDayHeaderLayout(day, dayNamesMap.get(String.valueOf(day)));
                binding.headerRow.layoutDaysHeader.addView(layout);
            }

            Log.d(TAG, "Generated " + days.size() + " day headers");
        }
    }

    private LinearLayout createDayHeaderLayout(int day, String dayName) {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(android.view.Gravity.CENTER);

        // Set layout parameters
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                (int) (getResources().getDisplayMetrics().density * 40),
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(4, 0, 4, 0);
        layout.setLayoutParams(params);

        // Day number TextView
        android.widget.TextView tvDay = new android.widget.TextView(this);
        tvDay.setText(String.valueOf(day));
        tvDay.setTextAppearance(this, R.style.AttendanceHeader);
        tvDay.setGravity(android.view.Gravity.CENTER);

        // Day name TextView
        android.widget.TextView tvName = new android.widget.TextView(this);
        tvName.setText(dayName);
        tvName.setTextAppearance(this, R.style.AttendanceHeaderSmall);
        tvName.setGravity(android.view.Gravity.CENTER);

        layout.addView(tvDay);
        layout.addView(tvName);

        return layout;
    }

    private void loadAttendanceData() {
        // Show loading indicator
        showLoading(true);

        // Here you would make an API call to fetch actual attendance data
        // For now, using dummy data as placeholder

        // TODO: Replace with actual API call

        AttendanceApiHelper attendanceHelper = new AttendanceApiHelper(this);
        attendanceHelper.fetchMonthlyAttendance(
            classId,
            monthName,
            selectedYear,
            new AttendanceApiHelper.AttendanceCallback() {

                @Override
                public void onSuccess(List<AttendanceEntries> attendanceList) {
                    runOnUiThread(() -> {
                        showLoading(false);
                        setupRecyclerView(attendanceList);
                    });
                }

                @Override
                public void onError(String errorMessage) {
                    runOnUiThread(() -> {
                        showLoading(false);
                        Toast.makeText(ActivityStudentsAttendanceReport.this,
                                     "Error: " + errorMessage, Toast.LENGTH_LONG).show();
                    });
                }

                @Override
                public void onLoading(boolean isLoading) {
                    runOnUiThread(() -> showLoading(isLoading));
                }
            }
        );


        // Temporary: Use dummy data

    }


    private void showLoading(boolean isLoading) {
        if (binding != null) {
            // Assuming you have a progress bar in your layout
            // binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            // binding.rvAttendance.setVisibility(isLoading ? View.GONE : View.VISIBLE);

            Log.d(TAG, "Loading state: " + isLoading);
        }
    }

    private List<StudentsAttendance> generateDummyStudents() {
        List<StudentsAttendance> list = new ArrayList<>();

        // Generate dummy students with attendance data for the selected month
        for (int i = 1; i <= 5; i++) {
            StudentsAttendance student = new StudentsAttendance();
            // Set student properties based on your StudentsAttendance model
            // student.setStudentName("Student " + i);
            // student.setRollNumber("ROLL" + String.format("%03d", i));
            // student.setAttendanceData(generateDummyAttendanceForStudent());

            list.add(student);
        }

        Log.d(TAG, "Generated " + list.size() + " dummy students");
        return list;
    }

    // Method to get actual calendar data for API parameters
    public Map<String, Object> getApiParameters() {
        Map<String, Object> params = new HashMap<>();
        params.put("class_id", classId);
        params.put("month", selectedMonth);
        params.put("year", selectedYear);
        params.put("total_days", totalDaysInMonth);

        return params;
    }

    // Method to refresh data
    private void refreshData() {
        Log.d(TAG, "Refreshing attendance data...");
        loadAttendanceData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "Activity resumed");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
        Log.d(TAG, "Activity destroyed");
    }

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

    private void handleExport(ArrayList<ArrayList<String>> tableData, String action) {
        if (tableData.size() <= 1) { // Only headers, no data
            Toast.makeText(this, "No attendance data to export", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Create DataExportHelper instance with context
            DataExportHelper exportHelper = new DataExportHelper(this);

            // Generate dynamic filename based on selected criteria
            String fileName = generateDynamicFileName();

            // Use the exportData method with dynamic filename
            exportHelper.exportData(tableData, action, fileName);

        } catch (Exception e) {
            Log.e(TAG, "Export error: " + e.getMessage());
            Toast.makeText(this, "Export failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private String generateDynamicFileName() {
        String fileName = "attendance_report";

        // Include class name if available
        if (className != null && !className.isEmpty()) {
            fileName += "_" + className.replace(" ", "_");
        }

        // Include month and year if available
        if (monthName != null && !monthName.isEmpty()) {
            fileName += "_" + monthName.replace(" ", "_");
        }

        if (selectedYear != null && !selectedYear.isEmpty()) {
            fileName += "_" + selectedYear;
        }

        // Include report type if available
        if (reportType != null && !reportType.isEmpty()) {
            fileName += "_" + reportType.replace(" ", "_");
        }

        // Add timestamp for uniqueness
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        fileName += "_" + sdf.format(new Date());

        return fileName;
    }

    private ArrayList<ArrayList<String>> prepareTableData() {
        ArrayList<ArrayList<String>> tableData = new ArrayList<>();

        // Add header row
        ArrayList<String> headers = new ArrayList<>();
        headers.add("Sr");
        headers.add("Student Name");
        headers.add("Student ID");
        headers.add("Present");
        headers.add("Late");
        headers.add("Absent");
        headers.add("Holiday");
        headers.add("Half Day");
        headers.add("Percentage");

        // Add dynamic day headers
        if (dayNamesMap != null) {
            List<Integer> days = new ArrayList<>();
            for (String key : dayNamesMap.keySet()) {
                days.add(Integer.parseInt(key));
            }
            Collections.sort(days);

            for (int day : days) {
                String dayName = dayNamesMap.get(String.valueOf(day));
                headers.add(day + " (" + dayName + ")");
            }
        }

        tableData.add(headers);

        // Add data rows from your attendance data
        if (binding.rvAttendance.getAdapter() instanceof AttendanceAdapter) {
            AttendanceAdapter adapter = (AttendanceAdapter) binding.rvAttendance.getAdapter();
            List<AttendanceEntries> studentList = adapter.getStudentList();

            if (studentList != null && !studentList.isEmpty()) {
                int count = 1;
                for (AttendanceEntries student : studentList) {
                    ArrayList<String> row = new ArrayList<>();
                    row.add(String.valueOf(count));

                    // Add student basic info
                    row.add(student.getStudentName() != null ? student.getStudentName() : "");
                    row.add(String.valueOf(student.getStudentId()));

                    // Add attendance summary
                    if (student.getAttendanceSummary() != null) {
                        row.add(String.valueOf(student.getAttendanceSummary().getPresentDays()));
                        row.add(String.valueOf(student.getAttendanceSummary().getLateDays()));
                        row.add(String.valueOf(student.getAttendanceSummary().getAbsentDays()));
                        row.add(String.valueOf(student.getAttendanceSummary().getHolidayDays()));
                        row.add(String.valueOf(student.getAttendanceSummary().getHalfDays()));
                        row.add(String.format(Locale.getDefault(), "%.2f%%",
                                student.getAttendanceSummary().getAttendancePercentage()));
                    } else {
                        row.add("0");
                        row.add("0");
                        row.add("0");
                        row.add("0");
                        row.add("0");
                        row.add("0.00%");
                    }

                    // Add daily attendance data from compressed string
                    String compressedAttendance = student.getDailyAttendanceCompressed();
                    if (compressedAttendance != null && !compressedAttendance.isEmpty()) {
                        char[] dailyAttendance = compressedAttendance.toCharArray();

                        // Add attendance for each day
                        for (int i = 0; i < totalDaysInMonth && i < dailyAttendance.length; i++) {
                            row.add(String.valueOf(dailyAttendance[i]));
                        }

                        // Fill remaining days with '-' if compressed data is shorter
                        for (int i = dailyAttendance.length; i < totalDaysInMonth; i++) {
                            row.add("-");
                        }
                    } else {
                        // No attendance data available
                        for (int i = 0; i < totalDaysInMonth; i++) {
                            row.add("-");
                        }
                    }

                    tableData.add(row);
                    count++;
                }
            }
        }

        return tableData;
    }}