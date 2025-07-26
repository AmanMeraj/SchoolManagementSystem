package com.school.schoolmanagement.Admin.Report;

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
import com.school.schoolmanagement.R;
import com.school.schoolmanagement.databinding.ActivityEmployeeAttendanceReportBinding;

import java.text.SimpleDateFormat;
import java.util.*;

public class ActivityEmployeeAttendanceReport extends AppCompatActivity {

    private static final String TAG = "EmployeeAttendanceReport";

    ActivityEmployeeAttendanceReportBinding binding;

    // Data received from previous activity
    private String selectedMonth;
    private String selectedYear;
    private String selectedMonthName;
    private String selectedRole;
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
        binding = ActivityEmployeeAttendanceReportBinding.inflate(getLayoutInflater());
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

        // Load attendance data
        loadEmployeeAttendanceData();
        setupExportButtons();
        binding.toolbar.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void receiveIntentData() {
        Intent intent = getIntent();
        if (intent != null) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                selectedMonth = extras.getString("month", "");
                selectedYear = extras.getString("year", "");
                selectedMonthName = extras.getString("month_name", "");
                selectedRole = extras.getString("employee_role", "");
                reportType = extras.getString("report_type", "Employee Attendance");
                reportTitle = extras.getString("report_title", "Employee Monthly Attendance Report");

                // Log received data for debugging
                Log.d(TAG, "=== Received Intent Data ===");
                Log.d(TAG, "Selected Month: " + selectedMonth);
                Log.d(TAG, "Selected Year: " + selectedYear);
                Log.d(TAG, "Selected Month Name: " + selectedMonthName);
                Log.d(TAG, "Selected Role: " + selectedRole);
                Log.d(TAG, "Report Type: " + reportType);
                Log.d(TAG, "Report Title: " + reportTitle);

                // Validate received data
                if (selectedMonth.isEmpty() || selectedYear.isEmpty()) {
                    Toast.makeText(this, "Invalid data received. Please try again.", Toast.LENGTH_LONG).show();
                    finish();
                    return;
                }
            } else {
                Toast.makeText(this, "No data received. Please try again.", Toast.LENGTH_LONG).show();
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
        if (reportTitle != null && !reportTitle.isEmpty()) {
            // Assuming you have a TextView for title in your layout
            // binding.txtReportTitle.setText(reportTitle);
        }

        // Set role and month information
        String headerInfo = String.format("Role: %s | Month: %s %s",
                selectedRole != null && !selectedRole.isEmpty() ? selectedRole : "All Employees",
                selectedMonthName != null && !selectedMonthName.isEmpty() ? selectedMonthName : "N/A",
                selectedYear != null && !selectedYear.isEmpty() ? selectedYear : "N/A");

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

    private void setupRecyclerView(List<AttendanceEntries> employees) {
        if (employees != null && !employees.isEmpty()) {
            AttendanceAdapter adapter = new AttendanceAdapter(this, employees, totalDaysInMonth);
            binding.rvAttendance.setLayoutManager(new LinearLayoutManager(this));
            binding.rvAttendance.setAdapter(adapter);

            Log.d(TAG, "RecyclerView setup with " + employees.size() + " employees");
        } else {
            Log.w(TAG, "No employee data available");
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

    private void loadEmployeeAttendanceData() {
        // Show loading indicator
        showLoading(true);

        // Make API call to fetch employee attendance data
        AttendanceApiHelper attendanceHelper = new AttendanceApiHelper(this);
        attendanceHelper.fetchEmployeeMonthlyAttendance(
                selectedMonthName,
                selectedYear,
                selectedRole,
                new AttendanceApiHelper.EmployeeAttendanceCallback() {

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
                            Toast.makeText(ActivityEmployeeAttendanceReport.this,
                                    "Error: " + errorMessage, Toast.LENGTH_LONG).show();
                            Log.e(TAG, "API Error: " + errorMessage);
                        });
                    }

                    @Override
                    public void onLoading(boolean isLoading) {
                        runOnUiThread(() -> showLoading(isLoading));
                    }
                }
        );
    }

    private void showLoading(boolean isLoading) {
        if (binding != null) {
            // Assuming you have a progress bar in your layout
            // binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            // binding.rvAttendance.setVisibility(isLoading ? View.GONE : View.VISIBLE);

            Log.d(TAG, "Loading state: " + isLoading);
        }
    }

    // Method to get actual calendar data for API parameters
    public Map<String, Object> getApiParameters() {
        Map<String, Object> params = new HashMap<>();
        params.put("employee_role", selectedRole);
        params.put("month", selectedMonth);
        params.put("year", selectedYear);
        params.put("month_name", selectedMonthName);
        params.put("total_days", totalDaysInMonth);

        return params;
    }

    // Method to refresh data
    private void refreshData() {
        Log.d(TAG, "Refreshing employee attendance data...");
        loadEmployeeAttendanceData();
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

    // Helper method to format date for display
    private String getFormattedDate() {
        if (selectedMonth != null && !selectedMonth.isEmpty() &&
                selectedYear != null && !selectedYear.isEmpty() &&
                selectedMonthName != null && !selectedMonthName.isEmpty()) {
            return selectedMonthName + " " + selectedYear;
        }
        return "Unknown Date";
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
            Toast.makeText(this, "No employee attendance data to export", Toast.LENGTH_SHORT).show();
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
        String fileName = "employee_attendance_report";

        // Include role if available
        if (selectedRole != null && !selectedRole.isEmpty()) {
            fileName += "_" + selectedRole.replace(" ", "_");
        }

        // Include month and year if available
        if (selectedMonthName != null && !selectedMonthName.isEmpty()) {
            fileName += "_" + selectedMonthName.replace(" ", "_");
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
        headers.add("Employee Name");
        headers.add("Employee ID");
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
            List<AttendanceEntries> employeeList = adapter.getStudentList(); // Note: might need to rename this method to getEmployeeList()

            if (employeeList != null && !employeeList.isEmpty()) {
                int count = 1;
                for (AttendanceEntries employee : employeeList) {
                    ArrayList<String> row = new ArrayList<>();
                    row.add(String.valueOf(count));

                    // Add employee basic info
                    row.add(employee.getStudentName() != null ? employee.getStudentName() : ""); // Note: might need to rename to getEmployeeName()
                    row.add(String.valueOf(employee.getStudentId())); // Note: might need to rename to getEmployeeId()

                    // Add attendance summary
                    if (employee.getAttendanceSummary() != null) {
                        row.add(String.valueOf(employee.getAttendanceSummary().getPresentDays()));
                        row.add(String.valueOf(employee.getAttendanceSummary().getLateDays()));
                        row.add(String.valueOf(employee.getAttendanceSummary().getAbsentDays()));
                        row.add(String.valueOf(employee.getAttendanceSummary().getHolidayDays()));
                        row.add(String.valueOf(employee.getAttendanceSummary().getHalfDays()));
                        row.add(String.format(Locale.getDefault(), "%.2f%%",
                                employee.getAttendanceSummary().getAttendancePercentage()));
                    } else {
                        row.add("0");
                        row.add("0");
                        row.add("0");
                        row.add("0");
                        row.add("0");
                        row.add("0.00%");
                    }

                    // Add daily attendance data from compressed string
                    String compressedAttendance = employee.getDailyAttendanceCompressed();
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
    }

    // Method to handle back button press
    @Override
    public void onBackPressed() {
        Log.d(TAG, "Back button pressed");
        super.onBackPressed();
    }
}