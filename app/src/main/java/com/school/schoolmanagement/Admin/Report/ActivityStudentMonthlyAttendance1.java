package com.school.schoolmanagement.Admin.Report;

import static android.content.ContentValues.TAG;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.school.schoolmanagement.Admin.Model.ClassModel;
import com.school.schoolmanagement.HelperClasses.ClassApiHelper;
import com.school.schoolmanagement.HelperClasses.DataExportHelper;
import com.school.schoolmanagement.Model.AttendanceEntries;
import com.school.schoolmanagement.R;
import com.school.schoolmanagement.Teachers.Adapters.AttendanceAdapter;
import com.school.schoolmanagement.databinding.ActivityStudentMonthlyAttendance1Binding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ActivityStudentMonthlyAttendance1 extends AppCompatActivity {

    private ActivityStudentMonthlyAttendance1Binding binding;
    private ClassApiHelper classApiHelper;
    private ArrayList<ClassModel.Data> classList;
    private ArrayAdapter<String> classAdapter;
    private int selectedClassId = -1;
    private String selectedMonth = "";
    private String selectedMonthName = "";
    private String selectedYear = "";

    // Month names array
    private final String[] monthNames = {
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityStudentMonthlyAttendance1Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeViews();
        setupClassDropdown();
        setupMonthDropdown();
        setupYearPicker();
        setupGenerateButton();
        binding.toolbar.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void initializeViews() {
        classApiHelper = new ClassApiHelper(this);
        classList = new ArrayList<>();
    }

    private void setupClassDropdown() {
        // Initialize adapter with empty list first
        ArrayList<String> classNames = new ArrayList<>();
        classAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, classNames);
        binding.edtClass.setAdapter(classAdapter);

        // Set threshold to 1 to show dropdown when user types
        binding.edtClass.setThreshold(1);

        // Set up item click listener for class selection
        binding.edtClass.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position < classList.size() && classList.size() > 0) {
                    ClassModel.Data selectedClass = classList.get(position);
                    selectedClassId = selectedClass.getClassId();
                    binding.edtClass.setText(selectedClass.getClassName());
                    binding.edtClass.dismissDropDown();
                } else {
                    selectedClassId = -1;
                }
            }
        });

        // Handle focus to show dropdown
        binding.edtClass.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus && classList.size() > 0) {
                binding.edtClass.showDropDown();
            }
        });

        // Handle click to show dropdown
        binding.edtClass.setOnClickListener(v -> {
            if (classList.size() > 0) {
                binding.edtClass.showDropDown();
            }
        });

        // Fetch classes from API
        fetchClasses();
    }

    private void fetchClasses() {
        classApiHelper.fetchAllClasses(new ClassApiHelper.ClassListCallback() {
            @Override
            public void onSuccess(ArrayList<ClassModel.Data> classDataList) {
                classList.clear();
                classList.addAll(classDataList);

                // Update adapter with fetched classes
                ArrayList<String> classNames = new ArrayList<>();
                for (ClassModel.Data classData : classList) {
                    classNames.add(classData.getClassName());
                }

                classAdapter.clear();
                classAdapter.addAll(classNames);
                classAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(ActivityStudentMonthlyAttendance1.this,
                        "Error loading classes: " + errorMessage, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLoading(boolean isLoading) {
                // You can show/hide loading indicator here if needed
                binding.edtClass.setEnabled(!isLoading);
            }
        });
    }

    private void setupMonthDropdown() {
        binding.edtMonth.setOnClickListener(v -> showMonthSelectionDialog());
        binding.edtMonth.setFocusable(false);
        binding.edtMonth.setClickable(true);
    }

    private void showMonthSelectionDialog() {
        // Create ArrayList from month names array
        ArrayList<String> monthList = new ArrayList<>();
        for (String month : monthNames) {
            monthList.add(month);
        }

        // Create ArrayAdapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, monthList);

        // Create AlertDialog with ListView
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Select Month");

        // Create ListView and set adapter
        android.widget.ListView listView = new android.widget.ListView(this);
        listView.setAdapter(adapter);
        builder.setView(listView);

        androidx.appcompat.app.AlertDialog dialog = builder.create();

        // Set item click listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedMonthName = monthNames[position];
                selectedMonth = String.format(Locale.getDefault(), "%02d", position + 1);
                binding.edtMonth.setText(selectedMonthName);
                binding.edtMonth.setError(null); // Clear any previous error
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void setupYearPicker() {
        binding.edtDate.setOnClickListener(v -> showYearPicker());
        binding.edtDate.setFocusable(false);
        binding.edtDate.setClickable(true);
    }

    private void showYearPicker() {
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);

        DatePickerDialog yearPickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    selectedYear = String.valueOf(year);
                    binding.edtDate.setText(selectedYear);
                    binding.edtDate.setError(null); // Clear any previous error
                },
                currentYear,
                0,
                1
        );

        // Safer way to hide month and day pickers
        try {
            int monthId = getResources().getIdentifier("month", "id", "android");
            int dayId = getResources().getIdentifier("day", "id", "android");

            if (monthId != 0) {
                View monthView = yearPickerDialog.getDatePicker().findViewById(monthId);
                if (monthView != null) {
                    monthView.setVisibility(View.GONE);
                }
            }

            if (dayId != 0) {
                View dayView = yearPickerDialog.getDatePicker().findViewById(dayId);
                if (dayView != null) {
                    dayView.setVisibility(View.GONE);
                }
            }
        } catch (Exception e) {
            // If hiding the pickers fails, the dialog will still work
            e.printStackTrace();
        }

        yearPickerDialog.setTitle("Select Year");
        yearPickerDialog.show();
    }

    private void setupGenerateButton() {
        binding.btnCreatePaper.setOnClickListener(v -> {
            if (validateInputs()) {
                generateReport();
            }
        });
    }

    private boolean validateInputs() {
        boolean isValid = true;

        // Validate class selection
        if (selectedClassId == -1) {
            binding.edtClass.setError("Please select a class");
            isValid = false;
        }

        // Validate month selection
        if (TextUtils.isEmpty(selectedMonth) || TextUtils.isEmpty(selectedMonthName)) {
            binding.edtMonth.setError("Please select a month");
            isValid = false;
        }

        // Validate year selection
        if (TextUtils.isEmpty(selectedYear)) {
            binding.edtDate.setError("Please select a year");
            isValid = false;
        }

        // Additional validation: Check if selected date is not in future
        if (!TextUtils.isEmpty(selectedMonth) && !TextUtils.isEmpty(selectedYear)) {
            try {
                Calendar selectedCalendar = Calendar.getInstance();
                selectedCalendar.set(Integer.parseInt(selectedYear), Integer.parseInt(selectedMonth) - 1, 1);

                Calendar currentCalendar = Calendar.getInstance();

                if (selectedCalendar.after(currentCalendar)) {
                    Toast.makeText(this, "Cannot generate report for future dates", Toast.LENGTH_SHORT).show();
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid date format", Toast.LENGTH_SHORT).show();
                isValid = false;
            }
        }

        return isValid;
    }

    private void generateReport() {
        // Create intent to pass data to next activity
        Intent intent = new Intent(this, ActivityStudentsAttendanceReport.class);

        // Pass selected data
        intent.putExtra("class_id", selectedClassId);
        intent.putExtra("month", selectedMonth);
        intent.putExtra("year", selectedYear);
        intent.putExtra("month_name", selectedMonthName);
        intent.putExtra("class_name", binding.edtClass.getText().toString());

        // Start the report activity
        startActivity(intent);

        // Show success message
        Toast.makeText(this, "Generating report...", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh classes when activity resumes (optional)
        if (classList.isEmpty()) {
            fetchClasses();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }


}