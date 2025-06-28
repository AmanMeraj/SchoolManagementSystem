package com.school.schoolmanagement.Admin.Salary.PaidReceipt;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.gson.Gson;
import com.school.schoolmanagement.Model.SalaryPaidResponse;
import com.school.schoolmanagement.R;
import com.school.schoolmanagement.Utils.ImageUtils;
import com.school.schoolmanagement.Utils.Utility;
import com.school.schoolmanagement.databinding.ActivityPaidReceiptDetailsBinding;

public class ActivityPaidReceiptDetails extends Utility {
ActivityPaidReceiptDetailsBinding binding;

    private SalaryPaidResponse.Datum salaryData;
    private String employeeName;
    private int employeeId;
    private int position;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding=ActivityPaidReceiptDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        receiveIntentData();

        // Use the received data
        setupUI();
        setupClickListeners();
    }

    private void setProfileImage(String base64String) {
        // Replace 'binding.profileImageView' with your actual ImageView binding
        if (binding.profilePicture != null) {
            // Option 1: Using the basic method
            ImageUtils.setImageFromBase64(base64String, binding.profilePicture);

            // Option 2: Using Glide (recommended for better performance)
            // setProfileImageWithGlide(base64String, binding.profileImageView);
        } else {
            Log.e(TAG, "Profile ImageView not found in binding");
        }
    }

    private void receiveIntentData() {
        Intent intent = getIntent();

        if (intent != null) {
            // Method 1: If using Serializable
            salaryData = (SalaryPaidResponse.Datum) intent.getSerializableExtra("SALARY_DATA");

            // Method 2: If using JSON serialization (alternative)
            String salaryDataJson = intent.getStringExtra("SALARY_DATA_JSON");
            if (salaryDataJson != null) {
                try {
                    Gson gson = new Gson();
                    salaryData = gson.fromJson(salaryDataJson, SalaryPaidResponse.Datum.class);
                } catch (Exception e) {
                    Log.e("ActivityPaidReceiptDetails", "Error deserializing salary data: " + e.getMessage());
                }
            }

            // Get other data
            employeeName = intent.getStringExtra("EMPLOYEE_NAME");
            employeeId = intent.getIntExtra("EMPLOYEE_ID", 0);
            position = intent.getIntExtra("POSITION", -1);

            // Log received data for debugging
            Log.d("ActivityPaidReceiptDetails", "Received data - Employee: " + employeeName +
                    ", ID: " + employeeId + ", Position: " + position);
        }
    }

    private void setupUI() {
        if (salaryData != null) {
            populateReceiptData();
        } else {
            Log.e(TAG, "Salary data is null");
            Toast.makeText(this, "Error: No salary data received", Toast.LENGTH_SHORT).show();
            // Set default/fallback values
            setDefaultValues();
        }
    }

    private void populateReceiptData() {
        try {

                // Set profile image first (if available)
                if (salaryData.getProfilePicture() != null && !salaryData.getProfilePicture().isEmpty()) {
                    setProfileImage(salaryData.getProfilePicture());
                }

                // Set employee name
            if (employeeName != null && !employeeName.isEmpty()) {
                binding.numberTv.setText(employeeName);
            } else {
                binding.numberTv.setText("Unknown Employee");
            }

            // Set Registration/ID
            binding.tvRegistration.setText(String.valueOf(employeeId));

            // Set employee type/role (you might need to get this from employee data)
            // For now, using a placeholder - replace with actual field from your data
            binding.tvClass.setText("Employee"); // Replace with actual role field

            // Populate salary data fields
            // Note: Replace these field names with actual field names from your SalaryPaidResponse.Datum class

            // Salary Month - replace with actual field name
            if (salaryData.getSalaryMonth() != null) {
                binding.tvSalaryMonth.setText(salaryData.getSalaryMonth());
            } else {
                binding.tvSalaryMonth.setText("N/A");
            }

            // Date of Receiving - replace with actual field name
            if (salaryData.getDateOfReceiving() != null) {
                binding.tvReceivingDate.setText(salaryData.dateOfReceiving);
            } else {
                binding.tvReceivingDate.setText("N/A");
            }

            // Bonus - replace with actual field name
            if (salaryData.getBonus() != 0.0) {
                binding.tvBonus.setText("$ " + salaryData.getBonus());
            } else {
                binding.tvBonus.setText("$ 0");
            }

            // Deduction - replace with actual field name
            if (salaryData.getDeduction() != 0.0) {
                binding.tvDeduction.setText("$ " + salaryData.getDeduction());
            } else {
                binding.tvDeduction.setText("$ 0");
            }

            // Net Paid - replace with actual field name
            if (salaryData.getNetPaid() != 0.0) {
                binding.tvNetPaid.setText("$ " + salaryData.getNetPaid());
            } else {
                binding.tvNetPaid.setText("$ 0");
            }

            Log.d(TAG, "UI populated successfully with salary data");

        } catch (Exception e) {
            Log.e(TAG, "Error populating UI: " + e.getMessage());
            Toast.makeText(this, "Error displaying salary data", Toast.LENGTH_SHORT).show();
            setDefaultValues();
        }
    }

    private void setDefaultValues() {
        // Set default values when data is not available
        binding.numberTv.setText(employeeName != null ? employeeName : "Unknown Employee");
        binding.tvRegistration.setText(employeeId > 0 ? String.valueOf(employeeId) : "N/A");
        binding.tvClass.setText("Employee");
        binding.tvSalaryMonth.setText("N/A");
        binding.tvReceivingDate.setText("N/A");
        binding.tvBonus.setText("$ 0");
        binding.tvDeduction.setText("$ 0");
        binding.tvNetPaid.setText("$ 0");
    }

    private void setupClickListeners() {
        // Back button click listener
        binding.toolbar.backBtn.setOnClickListener(view -> finish());

        // Print Detail Receipt button
        binding.detailsReceiptBtn.setOnClickListener(v -> printDetailedReceipt());

        // Mini Receipt button
        binding.miniReceiptBtn.setOnClickListener(v -> printMiniReceipt());
    }

    private void printDetailedReceipt() {
        if (salaryData == null) {
            Toast.makeText(this, "No salary data available for printing", Toast.LENGTH_SHORT).show();
            return;
        }

        // Implement detailed receipt printing logic here
        Toast.makeText(this, "Printing detailed receipt for " + employeeName, Toast.LENGTH_SHORT).show();

        // You can add actual printing implementation here
        // For example, generate PDF or send to printer
        Log.d(TAG, "Print detailed receipt requested for employee: " + employeeName);
    }

    private void printMiniReceipt() {
        if (salaryData == null) {
            Toast.makeText(this, "No salary data available for printing", Toast.LENGTH_SHORT).show();
            return;
        }

        // Implement mini receipt printing logic here
        Toast.makeText(this, "Printing mini receipt for " + employeeName, Toast.LENGTH_SHORT).show();

        // You can add actual printing implementation here
        // For example, generate PDF or send to printer
        Log.d(TAG, "Print mini receipt requested for employee: " + employeeName);
    }

    // Helper method to safely get string values
    private String getSafeString(String value) {
        return value != null ? value : "N/A";
    }

    // Helper method to safely format currency
    private String formatCurrency(Double amount) {
        return amount != null ? "$ " + String.format("%.2f", amount) : "$ 0.00";
    }

}

