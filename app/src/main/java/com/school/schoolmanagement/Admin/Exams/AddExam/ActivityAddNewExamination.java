package com.school.schoolmanagement.Admin.Exams.AddExam;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.school.schoolmanagement.Admin.Model.CreateExam;
import com.school.schoolmanagement.GlobalViewModel.ViewModel;
import com.school.schoolmanagement.R;
import com.school.schoolmanagement.Utils.Utility;
import com.school.schoolmanagement.databinding.ActivityAddNewExaminationBinding;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ActivityAddNewExamination extends Utility {
    ActivityAddNewExaminationBinding binding;
    private Calendar startDateCalendar;
    ViewModel viewModel;
    private Calendar endDateCalendar;
    private SimpleDateFormat dateFormat;
    private  String from="";
    int examId=-1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityAddNewExaminationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        viewModel= new ViewModelProvider(this).get(ViewModel.class);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        from = getIntent().getStringExtra("from");
        if (from != null && !from.isEmpty() && from.matches("EDIT")) {
            // This is an edit operation - populate fields with existing data

            // Get the passed exam data
            examId = getIntent().getIntExtra("exam_id",-1);
            String examName = getIntent().getStringExtra("exam_name");
            String startDate = getIntent().getStringExtra("start_date");
            String endDate = getIntent().getStringExtra("end_date");

            // Populate your UI elements with the existing data
            if (examId != -1) {
                // Set exam ID (if you have a field for it)
                // etExamId.setText(examId);
            }

            if (examName != null) {
                // Set exam name in EditText
                binding.edtExaminationName.setText(examName);
            }

            if (startDate != null) {
                // Set start date in your date field
                binding.edtStartDate.setText(startDate);
                // Or if using DatePicker, parse and set the date
            }

            if (endDate != null) {
                // Set end date in your date field
                binding.edtEndDate.setText(endDate);
                // Or if using DatePicker, parse and set the date
            }

            // Change UI elements for edit mode
            // For example, change button text from "Add" to "Update"
            binding.submitTxt.setText("Update Exam");

            // Change activity title
            setTitle("Edit Examination");

        } else {
            // This is a new exam creation - keep fields empty
            setTitle("Add New Examination");
            binding.submitTxt.setText("Add Exam");
        }

        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        // Initialize calendars
        startDateCalendar = Calendar.getInstance();
        endDateCalendar = Calendar.getInstance();

        // Initialize date format (yyyy-MM-dd)
        dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        // Show loader initially hidden
        binding.loader.rlLoader.setVisibility(View.GONE);
    }

    private void setupClickListeners() {
        // Start Date Click Listener
        binding.edtStartDate.setOnClickListener(v -> showDatePickerDialog(true));

        // End Date Click Listener
        binding.edtEndDate.setOnClickListener(v -> showDatePickerDialog(false));

        binding.toolbar.backBtn.setOnClickListener(v-> finish());

        // Create Button Click Listener
        binding.btnSearch.setOnClickListener(v -> validateAndSubmit());
    }

    private void showDatePickerDialog(boolean isStartDate) {
        Calendar calendar = isStartDate ? startDateCalendar : endDateCalendar;

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    String selectedDate = dateFormat.format(calendar.getTime());

                    if (isStartDate) {
                        binding.edtStartDate.setText(selectedDate);
                        // Validate that start date is not after end date
                        validateDateRange();
                    } else {
                        binding.edtEndDate.setText(selectedDate);
                        // Validate that end date is not before start date
                        validateDateRange();
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        // Set minimum date to today for both start and end date
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());

        // If selecting end date and start date is already selected, set minimum date to start date
        if (!isStartDate && !TextUtils.isEmpty(binding.edtStartDate.getText().toString().trim())) {
            datePickerDialog.getDatePicker().setMinDate(startDateCalendar.getTimeInMillis());
        }

        datePickerDialog.show();
    }

    private void validateDateRange() {
        String startDateStr = binding.edtStartDate.getText().toString().trim();
        String endDateStr = binding.edtEndDate.getText().toString().trim();

        if (!TextUtils.isEmpty(startDateStr) && !TextUtils.isEmpty(endDateStr)) {
            if (endDateCalendar.getTimeInMillis() < startDateCalendar.getTimeInMillis()) {
                binding.edtEndDate.setText("");
                Toast.makeText(this, "End date cannot be before start date", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void validateAndSubmit() {
        // Get input values
        String examinationName = binding.edtExaminationName.getText().toString().trim();
        String startDate = binding.edtStartDate.getText().toString().trim();
        String endDate = binding.edtEndDate.getText().toString().trim();

        // Reset any previous errors
        binding.edtExaminationName.setError(null);

        boolean isValid = true;

        // Validate Examination Name
        if (TextUtils.isEmpty(examinationName)) {
            binding.edtExaminationName.setError("Examination name is required");
            binding.edtExaminationName.requestFocus();
            isValid = false;
        } else if (examinationName.length() < 3) {
            binding.edtExaminationName.setError("Examination name must be at least 3 characters");
            binding.edtExaminationName.requestFocus();
            isValid = false;
        }

        // Validate Start Date
        if (TextUtils.isEmpty(startDate)) {
            Toast.makeText(this, "Start date is required", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        // Validate End Date
        if (TextUtils.isEmpty(endDate)) {
            Toast.makeText(this, "End date is required", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        // If all validations pass, proceed with submission
        if (isValid) {
            // Check if this is an edit operation or create operation
            if (from != null && from.equals("EDIT")) {
                // Call update API
                updateExamination(examinationName, startDate, endDate);
            } else {
                // Call create API
                submitExamination(examinationName, startDate, endDate);
            }
        }
    }

    private void submitExamination(String examinationName, String startDate, String endDate) {

        String auth = "Bearer "+pref.getPrefString(this,pref.user_token);

        CreateExam createExam= new CreateExam();
        createExam.setExaminationName(examinationName);
        createExam.setStartDate(startDate);
        createExam.setEndDate(endDate);
        // Show loader
        binding.loader.rlLoader.setVisibility(View.VISIBLE);

        // Disable the submit button to prevent multiple clicks
        binding.btnSearch.setEnabled(false);

        viewModel.createExam(auth,createExam).observe(this,response->{
            if(response.isSuccess && response.code==200){
                binding.loader.rlLoader.setVisibility(View.GONE);
                binding.getRoot().postDelayed(() -> {
                    // Hide loader
                    binding.loader.rlLoader.setVisibility(View.GONE);

                    // Re-enable submit button
                    binding.btnSearch.setEnabled(true);

                    Toast.makeText(this, ""+response.message, Toast.LENGTH_SHORT).show();

                    // TODO: Navigate back or to next screen
                    // finish(); // Uncomment to close activity after successful submission

                    // Optional: Clear form for new entry
                    clearForm();

                }, 2000); // 2 second delay to simulate network request

            }else{
                binding.loader.rlLoader.setVisibility(View.GONE);
                Toast.makeText(this, ""+response.message, Toast.LENGTH_SHORT).show();
            }
        });

        // TODO: Implement your API call or database insertion logic here
        // For now, just showing success message after a delay

        // Simulate API call delay

    }
    private void updateExamination(String examinationName, String startDate, String endDate) {
        String auth = "Bearer " + pref.getPrefString(this, pref.user_token);

        CreateExam createExam = new CreateExam();
        createExam.setExaminationName(examinationName);
        createExam.setStartDate(startDate);
        createExam.setEndDate(endDate);

        // Show loader
        binding.loader.rlLoader.setVisibility(View.VISIBLE);

        // Disable the submit button to prevent multiple clicks
        binding.btnSearch.setEnabled(false);

        viewModel.updateExam(auth, examId, createExam).observe(this, response -> {
            if(response.isSuccess && response.code == 200) {
                binding.getRoot().postDelayed(() -> {
                    // Hide loader
                    binding.loader.rlLoader.setVisibility(View.GONE);

                    // Re-enable submit button
                    binding.btnSearch.setEnabled(true);

                    Toast.makeText(this, "" + response.message, Toast.LENGTH_SHORT).show();

                    // Navigate back after successful update
                    finish(); // Close activity after successful update

                }, 2000); // 2 second delay to simulate network request

            } else {
                binding.loader.rlLoader.setVisibility(View.GONE);
                binding.btnSearch.setEnabled(true);
                Toast.makeText(this, "" + response.message, Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void clearForm() {
        binding.edtExaminationName.setText("");
        binding.edtStartDate.setText("");
        binding.edtEndDate.setText("");
        binding.edtExaminationName.clearFocus();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}