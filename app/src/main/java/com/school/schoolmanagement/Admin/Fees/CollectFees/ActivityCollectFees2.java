package com.school.schoolmanagement.Admin.Fees.CollectFees;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.school.schoolmanagement.Admin.Adapter.AdapterCollectFees2;
import com.school.schoolmanagement.Admin.GeneralSettings.ActivityFeespaticulars;
import com.school.schoolmanagement.Admin.Model.ClassModel2;
import com.school.schoolmanagement.Admin.Model.FeeItem;
import com.school.schoolmanagement.Admin.Model.StudentModel;
import com.school.schoolmanagement.Admin.Model.SubmitFees;
import com.school.schoolmanagement.GlobalViewModel.ViewModel;
import com.school.schoolmanagement.Model.AllStudentResponse;
import com.school.schoolmanagement.R;
import com.school.schoolmanagement.Utils.Utility;
import com.school.schoolmanagement.databinding.ActivityCollectFees2Binding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ActivityCollectFees2 extends Utility implements AdapterCollectFees2.OnFeeAmountChangedListener {
    private static final String TAG = "ActivityCollectFees2";

    ActivityCollectFees2Binding binding;
    private AdapterCollectFees2 adapter;
    private List<FeeItem> feeItemList = new ArrayList<>();

    int studentId, classId;
    ViewModel viewModel;
    private String authToken; // Replace with actual token

    private double totalAmount = 0.0;
    private double depositAmount = 0.0;
    private double dueBalance = 0.0;

    // Date picker variables
    private Calendar calendar;
    private SimpleDateFormat dateFormat;

    // Months array for month picker
    private String[] months = {
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityCollectFees2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(ViewModel.class);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize calendar and date format
        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        // Get intent data
        studentId = getIntent().getIntExtra("STUDENT_ID", -1);
        classId = getIntent().getIntExtra("CLASS_ID", -1);
        authToken = "Bearer " + pref.getPrefString(this, pref.user_token);

        // Initialize UI
        setupRecyclerView();
        setupClickListeners();
        setupDepositTextWatcher();
        setDefaultDates();

        // Fetch class data if classId is valid
        if (classId != -1) {
            fetchClassData();
            fetchStudentData(authToken, studentId);
        } else {
            Log.e(TAG, "Invalid classId received");
            Toast.makeText(this, "Invalid class information", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupRecyclerView() {
        adapter = new AdapterCollectFees2(feeItemList);
        adapter.setOnFeeAmountChangedListener(this);
        binding.rcFeesCollection.setAdapter(adapter);
    }

    private void setupClickListeners() {
        // Submit button click
        binding.btnSearch.setOnClickListener(v -> handleSubmitClick());

        // Date picker clicks
        binding.edtFeesDate.setOnClickListener(v -> showDatePicker());
        binding.edtFeesMonth.setOnClickListener(v -> showMonthPicker());
    }

    private void setupDepositTextWatcher() {
        binding.editDeposit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    depositAmount = s.toString().isEmpty() ? 0.0 : Double.parseDouble(s.toString());
                    calculateDueBalance();
                } catch (NumberFormatException e) {
                    depositAmount = 0.0;
                    calculateDueBalance();
                }
            }
        });
    }

    private void setDefaultDates() {
        // Set current date as default
        binding.edtFeesDate.setText(dateFormat.format(calendar.getTime()));

        // Set current month as default
        int currentMonth = calendar.get(Calendar.MONTH);
        binding.edtFeesMonth.setText(months[currentMonth]);
    }

    private void showDatePicker() {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                        calendar.set(selectedYear, selectedMonth, selectedDay);
                        binding.edtFeesDate.setText(dateFormat.format(calendar.getTime()));
                    }
                },
                year, month, day
        );

        datePickerDialog.show();
    }

    private void showMonthPicker() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Month");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, months);

        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                binding.edtFeesMonth.setText(months[which]);
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void fetchClassData() {
        // Show loading state
        showLoadingState(true);

        viewModel.getAllClassesWithId(authToken, classId).observe(this, response -> {
            showLoadingState(false);

            if (response != null && response.isSuccess && response.data != null) {
                ClassModel2 classModel = response.data;
                populateFeeItems(classModel.getData());
            } else {
                // Check if this is the specific null map error
                String errorMessage = response != null ? response.message : "Failed to load class data";
                Log.e(TAG, "Error fetching class data: " + errorMessage);

                // Check for the specific error message that indicates missing fee structure
                if (errorMessage.contains("Cannot invoke")) {
                    // Show dialog instead of toast for this specific error
                    showFeeStructureRequiredDialog();
                } else {
                    // Show toast for other errors
                    Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showFeeStructureRequiredDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Fee Structure Required");
        builder.setMessage("Add fees structure first");
        builder.setCancelable(false); // Make it non-cancellable

        // Only positive button to proceed to fee structure setup
        builder.setPositiveButton("Add Fee Structure", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                navigateToFeeStructureSetup();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void navigateToFeeStructureSetup() {
        // Navigate to the fee structure setup activity
        Intent intent = new Intent(ActivityCollectFees2.this, ActivityFeespaticulars.class);

        // Optional: Pass any necessary data
        intent.putExtra("ClassId", classId);
        intent.putExtra("from", "from");

        try {
            startActivity(intent);
        } catch (Exception e) {
            // If FeeStructureSetupActivity doesn't exist, you can navigate to a different activity
            // or show a message to the user
            Toast.makeText(this, "Please setup fee structure from admin panel", Toast.LENGTH_LONG).show();
            finish(); // Close current activity
        }
    }

    private void populateFeeItems(ClassModel2.Data classData) {
        if (classData == null || classData.getFeesStructure() == null) {
            showFeeStructureRequiredDialog();
            return;
        }

        feeItemList.clear();

        // Add monthly tuition fees
        if (classData.getMonthlyTuitionFees() > 0) {
            feeItemList.add(new FeeItem(
                    feeItemList.size() + 1,
                    "Monthly Tuition Fees",
                    String.valueOf((int) classData.getMonthlyTuitionFees())
            ));
        }

        // Add other fees from fee structure
        for (ClassModel2.FeesStructures feeStructure : classData.getFeesStructure()) {
            feeItemList.add(new FeeItem(
                    feeItemList.size() + 1,
                    feeStructure.getFeeType(),
                    String.valueOf((int) feeStructure.getAmount())
            ));
        }

        // Check if no fee items were added
        if (feeItemList.isEmpty()) {
            showFeeStructureRequiredDialog();
            return;
        }

        // Notify adapter and calculate totals
        adapter.notifyDataSetChanged();
        updateTotalFees();
    }

    private void handleSubmitClick() {
        // Validate required fields
        if (!validateFields()) {
            return;
        }

        // Show confirmation dialog
        showSubmissionConfirmationDialog();
    }

    private void showSubmissionConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Fee Submission");
        builder.setMessage("Are you sure you want to submit the fee collection?\n\n" +
                "Total Amount: ₹" + String.format("%.2f", totalAmount) + "\n" +
                "Deposit Amount: ₹" + String.format("%.2f", depositAmount) + "\n" +
                "Due Balance: ₹" + String.format("%.2f", dueBalance));

        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                submitFeesCollection();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private boolean validateFields() {
        if (binding.edtFeesDate.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please select fees date", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (binding.edtFeesMonth.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please select fees month", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (depositAmount <= 0) {
            Toast.makeText(this, "Please enter deposit amount", Toast.LENGTH_SHORT).show();
            binding.editDeposit.requestFocus();
            return false;
        }

        if (depositAmount > totalAmount) {
            Toast.makeText(this, "Deposit amount cannot exceed total amount", Toast.LENGTH_SHORT).show();
            binding.editDeposit.requestFocus();
            return false;
        }

        if (feeItemList.isEmpty()) {
            Toast.makeText(this, "No fee items available", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void submitFeesCollection() {
        // Show loading state
        showLoadingState(true);

        // Create submission data
        SubmitFees submitFees = createSubmissionData();

        // Call API through ViewModel
        viewModel.submitFees(authToken, submitFees).observe(this, response -> {
            // Hide loading state
            showLoadingState(false);

            if (response != null && response.isSuccess) {
                // Success
                Toast.makeText(this, "Fees collected successfully!", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Fees submission successful");

                // Show success dialog with option to collect more fees or go back
                showSuccessDialog();

            } else {
                // Error
                String errorMessage = response != null ? response.message : "Failed to submit fees";
                Log.e(TAG, "Error submitting fees: " + errorMessage);

                Toast.makeText(this, "Failed to submit fees: " + errorMessage, Toast.LENGTH_LONG).show();

                // Re-enable the submit button for retry
                binding.btnSearch.setEnabled(true);
            }
        });
    }

    private SubmitFees createSubmissionData() {
        SubmitFees submitFees = new SubmitFees();

        // Set basic information
        submitFees.setStudentId(studentId);
        submitFees.setFeesMonth(binding.edtFeesMonth.getText().toString().trim());
        submitFees.setDate(binding.edtFeesDate.getText().toString().trim());
        submitFees.setTotalAmount(totalAmount);
        submitFees.setDepositedAmount(depositAmount);

        // Create fee structure list
        ArrayList<SubmitFees.FeesStructure2> feesStructureList = new ArrayList<>();

        for (FeeItem feeItem : feeItemList) {
            SubmitFees.FeesStructure2 feeStructure = submitFees.new FeesStructure2();
            feeStructure.setFeeType(feeItem.getFeeName());

            try {
                int amount = Integer.parseInt(feeItem.getFeeAmount().trim());
                feeStructure.setAmount(amount);
                feesStructureList.add(feeStructure);
            } catch (NumberFormatException e) {
                Log.w(TAG, "Invalid fee amount for " + feeItem.getFeeName() + ": " + feeItem.getFeeAmount());
                // Skip this fee item if amount is invalid
            }
        }

        submitFees.setFeesStructure(feesStructureList);

        // Log the submission data for debugging
        Log.d(TAG, "Submitting fees data: " +
                "\nStudent ID: " + studentId +
                "\nFees Month: " + submitFees.getFeesMonth() +
                "\nDate: " + submitFees.getDate() +
                "\nTotal Amount: " + submitFees.getTotalAmount() +
                "\nDeposited Amount: " + submitFees.getDepositedAmount() +
                "\nNumber of fee items: " + feesStructureList.size());

        return submitFees;
    }

    private void showSuccessDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Success!");
        builder.setMessage("Fees have been collected successfully.");
        builder.setCancelable(false);

        builder.setPositiveButton("Collect More Fees", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                // Reset form for collecting more fees
                resetFormForNewCollection();
            }
        });

        builder.setNegativeButton("Go Back", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish(); // Return to previous activity
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void resetFormForNewCollection() {
        // Reset deposit amount
        binding.editDeposit.setText("");
        depositAmount = 0.0;

        // Reset dates to current
        setDefaultDates();

        // Recalculate due balance
        calculateDueBalance();

        // Re-enable all controls
        showLoadingState(false);

        Toast.makeText(this, "Form reset for new collection", Toast.LENGTH_SHORT).show();
    }

    private void calculateDueBalance() {
        dueBalance = totalAmount - depositAmount;
        binding.textDueBalance.setText(String.valueOf((int) dueBalance));

        // Change color based on due balance
        if (dueBalance > 0) {
            binding.textDueBalance.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        } else {
            binding.textDueBalance.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        }
    }

    @Override
    public void onFeeAmountChanged(int position, String newAmount) {
        // Handle fee amount changes
        Log.d(TAG, "Fee amount changed at position " + position + " to " + newAmount);
        updateTotalFees();
    }

    private void updateTotalFees() {
        totalAmount = 0.0;

        for (FeeItem item : feeItemList) {
            try {
                String amount = item.getFeeAmount();
                if (amount != null && !amount.trim().isEmpty()) {
                    totalAmount += Double.parseDouble(amount.trim());
                }
            } catch (NumberFormatException e) {
                Log.w(TAG, "Invalid fee amount: " + item.getFeeAmount());
            }
        }

        // Update total display
        binding.textTotal.setText(String.valueOf((int) totalAmount));

        // Recalculate due balance
        calculateDueBalance();

        // Update deposit field if it's empty
        if (binding.editDeposit.getText().toString().trim().isEmpty()) {
            binding.editDeposit.setText(String.valueOf((int) totalAmount));
        }
    }

    private void fetchStudentData(String auth, int studentId) {
        // Show loading state
        showLoadingState(true);

        viewModel.getStudent(auth, studentId).observe(this, response -> {
            // Hide loading state
            showLoadingState(false);

            if (response != null && response.isSuccess && response.data != null) {
                // Successfully got student data
                populateStudentData(response.data);
            } else {
                // Handle error cases
                String errorMessage = response != null ? response.message : "Failed to load student data";
                Log.e(TAG, "Error fetching student data: " + errorMessage);

                // Show error message to user
                Toast.makeText(this, "Unable to load student details: " + errorMessage, Toast.LENGTH_LONG).show();

                // Optionally, you might want to finish the activity or show default values
                showDefaultStudentData();
            }
        });
    }

    private void populateStudentData(Object studentData) {
        try {
            AllStudentResponse student = (AllStudentResponse) studentData;

            // Set Registration Number
            String registrationNo = String.valueOf(student.getData().get(0).getStudentId());
            binding.textViewRegistrationNo.setText(registrationNo);

            // Set Student Name
            String studentName = student.getData().get(0).getStudentName();
            if (studentName != null && !studentName.isEmpty()) {
                binding.textViewStudentName.setText(studentName);
            } else {
                binding.textViewStudentName.setText("N/A");
            }

            // Set Guardian Name
            String guardianName = student.getData().get(0).getParentName();
            if (guardianName != null && !guardianName.isEmpty()) {
                binding.textViewGuardianName.setText(guardianName);
            } else {
                binding.textViewGuardianName.setText("N/A");
            }

            // Set Class Name
            String className = student.getData().get(0).getClassName();
            if (className != null && !className.isEmpty()) {
                binding.textViewClass.setText(className);
            } else {
                binding.textViewClass.setText("N/A");
            }

            // Generic approach if you're using reflection or if the data comes as a map/json
            // This is a fallback approach - replace with proper model casting above

            if (studentData instanceof java.util.Map) {
                java.util.Map<String, Object> studentMap = (java.util.Map<String, Object>) studentData;

                // Set Registration Number
                Object regNo = studentMap.get("registrationNumber");
                binding.textViewRegistrationNo.setText(regNo != null ? regNo.toString() : "N/A");

                // Set Student Name
                Object name = studentMap.get("studentName");
                binding.textViewStudentName.setText(name != null ? name.toString() : "N/A");

                // Set Guardian Name
                Object guardian = studentMap.get("guardianName");
                binding.textViewGuardianName.setText(guardian != null ? guardian.toString() : "N/A");

                // Set Class Name
                Object className2 = studentMap.get("className");
                binding.textViewClass.setText(className2 != null ? className2.toString() : "N/A");
            }

            Log.d(TAG, "Student data populated successfully");

        } catch (Exception e) {
            Log.e(TAG, "Error populating student data: " + e.getMessage());
            showDefaultStudentData();
            Toast.makeText(this, "Error displaying student details", Toast.LENGTH_SHORT).show();
        }
    }

    private void showDefaultStudentData() {
        // Show default/placeholder values when student data can't be loaded
        binding.textViewRegistrationNo.setText("N/A");
        binding.textViewStudentName.setText("N/A");
        binding.textViewGuardianName.setText("N/A");
        binding.textViewClass.setText("N/A");
    }

    // Enhanced loading state method
    private void showLoadingState(boolean isLoading) {
        if (isLoading) {
            binding.btnSearch.setEnabled(false);
            binding.submitTxt.setText("Processing...");

            // Disable other interactive elements during loading
            binding.edtFeesDate.setEnabled(false);
            binding.edtFeesMonth.setEnabled(false);
            binding.editDeposit.setEnabled(false);

            // Show loading text on student fields if they're currently empty
            if (binding.textViewRegistrationNo.getText().toString().trim().isEmpty() ||
                    binding.textViewRegistrationNo.getText().toString().equals("N/A")) {
                binding.textViewRegistrationNo.setText("Loading...");
                binding.textViewStudentName.setText("Loading...");
                binding.textViewGuardianName.setText("Loading...");
                binding.textViewClass.setText("Loading...");
            }

            // You can show a progress bar here if you have one
            // binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.btnSearch.setEnabled(true);
            binding.submitTxt.setText("Submit Fees"); // Reset button text

            // Re-enable interactive elements
            binding.edtFeesDate.setEnabled(true);
            binding.edtFeesMonth.setEnabled(true);
            binding.editDeposit.setEnabled(true);

            // Hide progress bar
            // binding.progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}