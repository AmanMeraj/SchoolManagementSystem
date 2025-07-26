package com.school.schoolmanagement.Students.LastSubmittedFees;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.school.schoolmanagement.GlobalViewModel.ViewModel;
import com.school.schoolmanagement.HelperClasses.FeesSlipPdfGenerator;
import com.school.schoolmanagement.R;
import com.school.schoolmanagement.Students.Model.LastSubmittedFeesResponse;
import com.school.schoolmanagement.Utils.Utility;
import com.school.schoolmanagement.databinding.ActivityStudentsLastSubmittedFeesBinding;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ActivityStudentsLastSubmittedFees extends Utility {
    ActivityStudentsLastSubmittedFeesBinding binding;
    String auth;
    ViewModel viewModel;
    DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");

    private LastSubmittedFeesResponse.Data lastFeesData; // Store for PDF generation

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityStudentsLastSubmittedFeesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.toolbar.backBtn.setOnClickListener(v -> finish());

        initializeViews();
        setupViewModel();
        fetchLastSubmittedFees();

        // PDF Button Click
        binding.createBtn.setOnClickListener(v -> {
            if (lastFeesData != null) {
                FeesSlipPdfGenerator pdfGenerator = new FeesSlipPdfGenerator(this);
                pdfGenerator.generateFeesSlip(lastFeesData, formatDate(lastFeesData.getDate()));
            } else {
                Toast.makeText(this, "No fees data available to generate PDF", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initializeViews() {
        // Set up labels
        binding.rowId.label.setText("Registration/ID");
        binding.rowClass.label.setText("Class");
        binding.rowName.label.setText("Name");
        binding.rowMonth.label.setText("Fee Month");
        binding.rowTotal.label.setText("Total Amount");
        binding.rowDeposit.label.setText("Deposit");
        binding.rowRemaining.label.setText("Remainings");

        showLoading(true);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(ViewModel.class);
        auth = "Bearer " + pref.getPrefString(this, pref.user_token);
    }

    private void fetchLastSubmittedFees() {
        viewModel.getLastSubmittedFees(auth).observe(this, apiResponse -> {
            showLoading(false);
            if (apiResponse != null && apiResponse.isSuccess && apiResponse.data != null) {
                handleSuccessResponse(apiResponse.data);
            } else {
                handleErrorResponse(apiResponse != null ? apiResponse.message : "Failed to load data");
            }
        });
    }

    private void handleSuccessResponse(LastSubmittedFeesResponse response) {
        if (response.getData() != null) {
            lastFeesData = response.getData(); // Save for PDF generation
            LastSubmittedFeesResponse.Data data = lastFeesData;

            binding.heading.setText("Fees Slip of " + formatDate(data.getDate()));
            binding.rowId.value.setText(String.valueOf(data.getId()));
            binding.rowClass.value.setText(data.getStudentClass() != null ? data.getStudentClass() : "N/A");
            binding.rowName.value.setText(data.getName() != null ? data.getName() : "N/A");
            binding.rowMonth.value.setText(data.getDate() != null ? formatDate(data.getDate()) : "N/A");
            binding.rowTotal.value.setText("₹ " + decimalFormat.format(data.getTotalAmount()));
            binding.rowDeposit.value.setText("₹ " + decimalFormat.format(data.getDepositAmount()));
            binding.rowRemaining.value.setText("₹ " + decimalFormat.format(data.getRemainingAmount()));
        } else {
            handleErrorResponse("No data available");
        }
    }

    private void handleErrorResponse(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
        setDefaultValues();
    }

    private void setDefaultValues() {
        binding.rowId.value.setText("N/A");
        binding.rowClass.value.setText("N/A");
        binding.rowName.value.setText("N/A");
        binding.rowMonth.value.setText("N/A");
        binding.rowTotal.value.setText("₹ 0.00");
        binding.rowDeposit.value.setText("₹ 0.00");
        binding.rowRemaining.value.setText("₹ 0.00");
    }

    private void showLoading(boolean show) {
        if (show) {
            binding.rowId.value.setText("Loading...");
            binding.rowClass.value.setText("Loading...");
            binding.rowName.value.setText("Loading...");
            binding.rowMonth.value.setText("Loading...");
            binding.rowTotal.value.setText("Loading...");
            binding.rowDeposit.value.setText("Loading...");
            binding.rowRemaining.value.setText("Loading...");
        }
        // You may control progress bar visibility here if used
    }

    private String formatDate(String dateString) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("MMMM, yyyy", Locale.getDefault());
            Date date = inputFormat.parse(dateString);
            return date != null ? outputFormat.format(date) : dateString;
        } catch (Exception e) {
            return dateString;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
