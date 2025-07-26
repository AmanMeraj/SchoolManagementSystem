package com.school.schoolmanagement.Admin.Fees.FeespaidReceipt;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.school.schoolmanagement.Admin.Model.FeesRecordResponse;
import com.school.schoolmanagement.Admin.Model.ReceiptData;
import com.school.schoolmanagement.GlobalViewModel.ViewModel;
import com.school.schoolmanagement.HelperClasses.ReceiptPdfHelper;
import com.school.schoolmanagement.R;
import com.school.schoolmanagement.Utils.Utility;
import com.school.schoolmanagement.databinding.ActivityFeesPaidReceiptBinding;

public class ActivityFeesPaidReceipt extends Utility {

    private static final String TAG = "ActivityFeesPaidReceipt";

    // View Binding
    private ActivityFeesPaidReceiptBinding binding;

    // Intent Data Variables
    private String selectedMonth, auth;
    private int classId;
    private String className;
    private int studentId;
    private String studentName;
    private ViewModel viewModel;

    // Receipt data
    private FeesRecordResponse feesData;
    private ReceiptPdfHelper pdfHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        // Initialize binding
        binding = ActivityFeesPaidReceiptBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        viewModel = new ViewModelProvider(this).get(ViewModel.class);
        auth = "Bearer " + pref.getPrefString(this, pref.user_token);

        // Initialize PDF helper
        pdfHelper = new ReceiptPdfHelper(this);

        // Setup edge-to-edge display
        setupEdgeToEdge();

        // Receive Intent data
        receiveIntentData();

        // Setup UI with received data
        setupUI();

        // Setup back button
        setupBackButton();

        // Setup print buttons
        setupPrintButtons();
    }

    private void setupEdgeToEdge() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void receiveIntentData() {
        try {
            Intent intent = getIntent();

            if (intent != null) {
                selectedMonth = intent.getStringExtra("SELECTED_MONTH");
                classId = intent.getIntExtra("CLASS_ID", -1);
                className = intent.getStringExtra("CLASS_NAME");
                studentId = intent.getIntExtra("STUDENT_ID", -1);
                studentName = intent.getStringExtra("STUDENT_NAME");

                Log.d(TAG, "Received Intent Data:");
                Log.d(TAG, "Selected Month: " + selectedMonth);
                Log.d(TAG, "Class ID: " + classId + ", Name: " + className);
                Log.d(TAG, "Student ID: " + studentId + ", Name: " + studentName);

                if (isValidData()) {
                    Log.d(TAG, "All intent data received successfully");
                } else {
                    Log.w(TAG, "Some intent data is missing or invalid");
                    showToast("Some data is missing. Please try again.");
                }
            } else {
                Log.e(TAG, "Intent is null");
                showToast("No data received. Please try again.");
            }

        } catch (Exception e) {
            Log.e(TAG, "Error receiving intent data: " + e.getMessage(), e);
            showToast("Error loading data");
        }
    }

    private boolean isValidData() {
        return selectedMonth != null && !selectedMonth.isEmpty() &&
                classId != -1 &&
                className != null && !className.isEmpty() &&
                studentId != -1 &&
                studentName != null && !studentName.isEmpty();
    }

    private void setupUI() {
        try {
            // Set initial UI with basic data
            updateBasicUI();

            // Load fee receipt data from API
            loadFeeReceiptData();

        } catch (Exception e) {
            Log.e(TAG, "Error setting up UI: " + e.getMessage(), e);
            showToast("Error displaying data");
        }
    }

    private void updateBasicUI() {
        // Set basic information that we already have
        if (className != null) {
            binding.tvClass.setText(className);
        }

        if (studentName != null) {
            binding.tvName.setText(studentName);
        }

        if (selectedMonth != null) {
            binding.tvFeesMonth.setText(selectedMonth);
        }
    }

    private void loadFeeReceiptData() {
        try {
            Log.d(TAG, "Loading fee receipt data for:");
            Log.d(TAG, "Student ID: " + studentId);
            Log.d(TAG, "Class ID: " + classId);
            Log.d(TAG, "Month: " + selectedMonth);

            if (isInternetConnected(this)) {
                showLoading(true);

                viewModel.getFeesRecord(auth, studentId, selectedMonth)
                        .observe(this, response -> {
                            showLoading(false);

                            if (response != null && response.isSuccess) {
                                feesData = response.data;
                                displayFeeReceiptData(response.data);
                            } else {
                                String errorMessage = response != null ? response.message : "Failed to load fee receipt";
                                showToast(errorMessage);
                            }
                        });
            } else {
                showToast("No Internet Connection");
            }

        } catch (Exception e) {
            Log.e(TAG, "Error loading fee receipt data: " + e.getMessage(), e);
            showToast("Error loading receipt data");
        }
    }


    private void displayFeeReceiptData(FeesRecordResponse data) {
        try {
            if (data != null && data.data != null) {
                Log.d(TAG, "Displaying fee receipt data: " + data.toString());

                // Registration/ID - Always use studentId as it's more reliable
                binding.tvRegistration.setText(String.valueOf(studentId));
                Log.d(TAG, "Registration ID set: " + studentId);

                // Class (already set in updateBasicUI, but update if API provides different data)
                if (data.data.className != null && !data.data.className.trim().isEmpty()) {
                    binding.tvClass.setText(data.data.className);
                    Log.d(TAG, "Class name updated: " + data.data.className);
                }

                // Student Name (already set, but update if API provides different data)
                if (data.data.studentName != null && !data.data.studentName.trim().isEmpty()) {
                    binding.tvName.setText(data.data.studentName);
                    Log.d(TAG, "Student name updated: " + data.data.studentName);
                }

                // Fees Month (already set, but format if needed)
                if (data.data.feesMonth != null && !data.data.feesMonth.trim().isEmpty()) {
                    binding.tvFeesMonth.setText(data.data.feesMonth);
                    Log.d(TAG, "Fees month updated: " + data.data.feesMonth);
                }

                // Handle Total Amount (double value)
                double totalAmount = parseDoubleValue(data.data.totalAmount, "totalAmount");
                String formattedTotalAmount = formatCurrency(totalAmount);
                binding.tvTotalAmount.setText(formattedTotalAmount);
                Log.d(TAG, "Total amount set: " + totalAmount + " -> " + formattedTotalAmount);

                // Handle Deposited Amount (double value)
                double depositedAmount = parseDoubleValue(data.data.depositedAmount, "depositedAmount");
                String formattedDepositedAmount = formatCurrency(depositedAmount);
                binding.tvDeposit.setText(formattedDepositedAmount);
                Log.d(TAG, "Deposited amount set: " + depositedAmount + " -> " + formattedDepositedAmount);

                // Handle Due Balance/Remaining Amount (double value)
                double dueBalance = parseDoubleValue(data.data.dueBalance, "dueBalance");
                double calculatedRemaining = 0;

                if (dueBalance > 0) {
                    // Use the provided due balance if it's valid
                    calculatedRemaining = dueBalance;
                    Log.d(TAG, "Using provided due balance: " + dueBalance);
                } else {
                    // Calculate remaining if due balance is not provided or invalid
                    calculatedRemaining = totalAmount - depositedAmount;
                    Log.d(TAG, "Calculated remaining: " + totalAmount + " - " + depositedAmount + " = " + calculatedRemaining);
                }

                // Ensure remaining amount is not negative (in case of overpayment)
                if (calculatedRemaining < 0) {
                    calculatedRemaining = 0;
                    Log.d(TAG, "Remaining amount was negative, set to 0 (possible overpayment)");
                }

                String formattedRemainingAmount = formatCurrency(calculatedRemaining);
                binding.tvRemaining.setText(formattedRemainingAmount);
                Log.d(TAG, "Remaining amount set: " + calculatedRemaining + " -> " + formattedRemainingAmount);

                Log.d(TAG, "Fee receipt data displayed successfully");
                Log.d(TAG, "Summary - Total: " + formattedTotalAmount + ", Paid: " + formattedDepositedAmount + ", Remaining: " + formattedRemainingAmount);

            } else {
                Log.w(TAG, "Received null fee data or data.data is null");
                showToast("No fee data available");
                setDefaultValues();
            }

        } catch (Exception e) {
            Log.e(TAG, "Error displaying fee receipt data: " + e.getMessage(), e);
            showToast("Error displaying receipt data");
            setDefaultValues();
        }
    }

    /**
     * Safely parse double values from API response
     * Handles various data types that might come from API (String, Integer, Double, etc.)
     */
    private double parseDoubleValue(Object value, String fieldName) {
        try {
            if (value == null) {
                Log.d(TAG, fieldName + " is null, returning 0.0");
                return 0.0;
            }

            double parsedValue = 0.0;

            if (value instanceof Double) {
                parsedValue = (Double) value;
                Log.d(TAG, fieldName + " parsed as Double: " + parsedValue);
            } else if (value instanceof Integer) {
                parsedValue = ((Integer) value).doubleValue();
                Log.d(TAG, fieldName + " parsed as Integer to Double: " + parsedValue);
            } else if (value instanceof Float) {
                parsedValue = ((Float) value).doubleValue();
                Log.d(TAG, fieldName + " parsed as Float to Double: " + parsedValue);
            } else if (value instanceof String) {
                String stringValue = ((String) value).trim();
                if (!stringValue.isEmpty()) {
                    parsedValue = Double.parseDouble(stringValue);
                    Log.d(TAG, fieldName + " parsed from String: " + stringValue + " -> " + parsedValue);
                } else {
                    Log.d(TAG, fieldName + " is empty string, returning 0.0");
                }
            } else {
                // Try to convert using toString() as last resort
                String stringValue = value.toString().trim();
                if (!stringValue.isEmpty()) {
                    parsedValue = Double.parseDouble(stringValue);
                    Log.d(TAG, fieldName + " parsed from toString(): " + stringValue + " -> " + parsedValue);
                } else {
                    Log.d(TAG, fieldName + " toString() is empty, returning 0.0");
                }
            }

            // Ensure the value is not negative (except for calculations)
            if (parsedValue < 0 && !fieldName.equals("calculatedRemaining")) {
                Log.w(TAG, fieldName + " was negative (" + parsedValue + "), setting to 0.0");
                parsedValue = 0.0;
            }

            return parsedValue;

        } catch (NumberFormatException e) {
            Log.e(TAG, "Error parsing " + fieldName + ": " + e.getMessage() + ", value was: " + value);
            return 0.0;
        } catch (Exception e) {
            Log.e(TAG, "Unexpected error parsing " + fieldName + ": " + e.getMessage() + ", value was: " + value);
            return 0.0;
        }
    }

    /**
     * Format double value to currency string with proper formatting
     */
    private String formatCurrency(double amount) {
        try {
            // Format with 2 decimal places, but remove unnecessary trailing zeros
            String formatted = String.format("%.2f", amount);

            // Remove trailing zeros after decimal point
            if (formatted.contains(".")) {
                formatted = formatted.replaceAll("0*$", "").replaceAll("\\.$", "");
            }

            // If no decimal part remains, ensure we don't have a bare decimal point
            if (formatted.endsWith(".")) {
                formatted = formatted.substring(0, formatted.length() - 1);
            }

            // Add currency symbol
            return "₹ " + formatted;

        } catch (Exception e) {
            Log.e(TAG, "Error formatting currency: " + e.getMessage());
            return "₹ 0";
        }
    }

    /**
     * Set default values when data is not available
     */
    private void setDefaultValues() {
        try {
            binding.tvRegistration.setText(String.valueOf(studentId));
            binding.tvTotalAmount.setText("₹ 0");
            binding.tvDeposit.setText("₹ 0");
            binding.tvRemaining.setText("₹ 0");
            Log.d(TAG, "Default values set due to error or null data");
        } catch (Exception e) {
            Log.e(TAG, "Error setting default values: " + e.getMessage(), e);
        }
    }
    private void setupBackButton() {
        try {
            binding.toolbar.backBtn.setOnClickListener(view -> finish());
        } catch (Exception e) {
            Log.e(TAG, "Error setting up back button: " + e.getMessage(), e);
        }
    }

    private void setupPrintButtons() {
        try {
            // Detailed Receipt Print Button
            binding.detailsReceiptBtn.setOnClickListener(view -> {
                if (feesData != null) {
                    pdfHelper.generateAndPrintDetailedReceipt(createReceiptData(), false);
                } else {
                    showToast("Receipt data not loaded yet. Please wait.");
                }
            });

            // Mini Receipt Print Button
            binding.miniReceiptBtn.setOnClickListener(view -> {
                if (feesData != null) {
                    pdfHelper.generateAndPrintDetailedReceipt(createReceiptData(), true);
                } else {
                    showToast("Receipt data not loaded yet. Please wait.");
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "Error setting up print buttons: " + e.getMessage(), e);
        }
    }

    private ReceiptData createReceiptData() {
        ReceiptData receiptData = new ReceiptData();

        // Basic Info
        receiptData.studentName = studentName;
        receiptData.className = className;
        receiptData.feesMonth = selectedMonth;
        receiptData.registrationId = binding.tvRegistration.getText().toString();

        // Financial Info
        receiptData.totalAmount = binding.tvTotalAmount.getText().toString();
        receiptData.paidAmount = binding.tvDeposit.getText().toString();
        receiptData.remainingAmount = binding.tvRemaining.getText().toString();

        return receiptData;
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

    // Getter methods
    public String getSelectedMonth() {
        return selectedMonth;
    }

    public int getClassId() {
        return classId;
    }

    public String getClassName() {
        return className;
    }

    public int getStudentId() {
        return studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void refreshReceiptData() {
        loadFeeReceiptData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.d(TAG, "Activity finished via back press");
    }
    private void showLoading(boolean isLoading) {
        try {
            // Assuming you have a loader in your layout or can add one
            // If you don't have a loader, you can add a ProgressBar to your layout
            // For now, we'll just log the loading state
            Log.d(TAG, "Loading: " + isLoading);

            // If you have a loader view, uncomment and modify this:
            // if (binding.loader != null && binding.loader.rlLoader != null) {
            //     binding.loader.rlLoader.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            // }
        } catch (Exception e) {
            Log.e(TAG, "Error showing/hiding loader: " + e.getMessage(), e);
        }
    }
}