package com.school.schoolmanagement.Teachers.Attendance;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
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
import com.school.schoolmanagement.R;
import com.school.schoolmanagement.Utils.Utility;
import com.school.schoolmanagement.databinding.ActivitySearchStudentsAttendanceReportBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ActivitySearchStudentsAttendanceReport extends Utility implements ClassApiHelper.ClassListCallback {

    private ActivitySearchStudentsAttendanceReportBinding binding;

    // Helper classes
    private ClassApiHelper classApiHelper;
    private ViewModel viewModel;

    // Data lists
    private final ArrayList<ClassModel.Data> classList = new ArrayList<>();
    private final ArrayList<String> classNames = new ArrayList<>();

    // ArrayAdapter for class dropdown
    private ArrayAdapter<String> classAdapter;

    // Selected values
    private int selectedClassId = -1;
    private String selectedStartDate = "";
    private String selectedEndDate = "";

    // Date formatter
    private SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    // Calendar instances for date validation
    private Calendar startCalendar = Calendar.getInstance();
    private Calendar endCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivitySearchStudentsAttendanceReportBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupWindowInsets();
        initializeComponents();
        setupUI();
        loadInitialData();
    }

    private void setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initializeComponents() {
        viewModel = new ViewModelProvider(this).get(ViewModel.class);
        classApiHelper = new ClassApiHelper(this);
    }

    private void setupUI() {
        setupClassAutoComplete();
        setupDatePickers();
        setupClickListeners();
    }

    private void setupClassAutoComplete() {
        // Initialize with empty list first
        classNames.clear();
        classNames.add("Select Class");

        classAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, classNames);
        classAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.edtClassesPaper.setAdapter(classAdapter);

        // Set threshold to show dropdown on click
        binding.edtClassesPaper.setThreshold(1);
        binding.edtClassesPaper.setHint("Select Class");

        binding.edtClassesPaper.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                handleClassSelection(position);
            }
        });

        // Handle focus change to show dropdown
        binding.edtClassesPaper.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                binding.edtClassesPaper.showDropDown();
            }
        });

        // Handle click to show dropdown
        binding.edtClassesPaper.setOnClickListener(v -> {
            binding.edtClassesPaper.showDropDown();
        });
    }

    private void setupDatePickers() {
        // Start date picker
        binding.edtMonth.setOnClickListener(v -> showStartDatePicker());
        binding.edtMonth.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                showStartDatePicker();
            }
        });

        // End date picker
        binding.edtDate.setOnClickListener(v -> showEndDatePicker());
        binding.edtDate.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                showEndDatePicker();
            }
        });
    }

    private void showStartDatePicker() {
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        startCalendar.set(year, month, dayOfMonth);
                        selectedStartDate = dateFormatter.format(startCalendar.getTime());
                        binding.edtMonth.setText(selectedStartDate);

                        // If end date is already selected and is before start date, clear it
                        if (!selectedEndDate.isEmpty() && isDateBefore(selectedEndDate, selectedStartDate)) {
                            selectedEndDate = "";
                            binding.edtDate.setText("");
                            binding.edtDate.setHint("YYYY-MM-DD");
                            Toast.makeText(ActivitySearchStudentsAttendanceReport.this,
                                    "End date cleared as it was before start date", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        datePickerDialog.show();
    }

    private void showEndDatePicker() {
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        endCalendar.set(year, month, dayOfMonth);
                        String tempEndDate = dateFormatter.format(endCalendar.getTime());

                        // Validate that end date is not before start date
                        if (!selectedStartDate.isEmpty() && isDateBefore(tempEndDate, selectedStartDate)) {
                            Toast.makeText(ActivitySearchStudentsAttendanceReport.this,
                                    "End date cannot be before start date", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        selectedEndDate = tempEndDate;
                        binding.edtDate.setText(selectedEndDate);
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        // Set minimum date to start date if it's selected
        if (!selectedStartDate.isEmpty()) {
            datePickerDialog.getDatePicker().setMinDate(startCalendar.getTimeInMillis());
        }

        datePickerDialog.show();
    }

    private boolean isDateBefore(String date1, String date2) {
        try {
            Date d1 = dateFormatter.parse(date1);
            Date d2 = dateFormatter.parse(date2);
            return d1.before(d2);
        } catch (Exception e) {
            return false;
        }
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

    private void loadInitialData() {
        showLoading("Loading classes...");
        classApiHelper.fetchAllClasses(this);
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

    private void setupClickListeners() {
        // Back button click listener
        binding.toolbar.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Search button click listener
        binding.btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateFields()) {
                    proceedToAttendanceReport();
                }
            }
        });
    }

    private boolean validateFields() {
        // Validate class selection
        if (selectedClassId <= 0) {
            Toast.makeText(this, "Please select a class", Toast.LENGTH_SHORT).show();
            binding.edtClassesPaper.requestFocus();
            return false;
        }

        // Validate start date
        if (selectedStartDate.isEmpty()) {
            Toast.makeText(this, "Please select start date", Toast.LENGTH_SHORT).show();
            binding.edtMonth.requestFocus();
            return false;
        }

        // Validate end date
        if (selectedEndDate.isEmpty()) {
            Toast.makeText(this, "Please select end date", Toast.LENGTH_SHORT).show();
            binding.edtDate.requestFocus();
            return false;
        }

        // Additional validation: ensure end date is not before start date
        if (isDateBefore(selectedEndDate, selectedStartDate)) {
            Toast.makeText(this, "End date cannot be before start date", Toast.LENGTH_SHORT).show();
            binding.edtDate.requestFocus();
            return false;
        }

        return true;
    }

    private void proceedToAttendanceReport() {
        Intent intent = new Intent(ActivitySearchStudentsAttendanceReport.this, ActivityStudentsAttendancereport.class);
        intent.putExtra("CLASS_ID", selectedClassId);
        intent.putExtra("START_DATE", selectedStartDate);
        intent.putExtra("END_DATE", selectedEndDate);

        // Find selected class name for display
        String selectedClassName = "";
        for (ClassModel.Data classData : classList) {
            if (classData.getClassId() == selectedClassId) {
                selectedClassName = classData.getClassName();
                break;
            }
        }
        intent.putExtra("CLASS_NAME", selectedClassName);

        startActivity(intent);
    }

    private void showLoading(String message) {
        // Add loading indicator if you have one in your layout
        runOnUiThread(() -> Toast.makeText(this, message, Toast.LENGTH_SHORT).show());
    }

    private void hideLoading() {
        // Hide loading indicator
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
            showLoading("Loading...");
        } else {
            hideLoading();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}