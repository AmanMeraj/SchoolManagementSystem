package com.school.schoolmanagement.Accountant.FeeInvoice;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.school.schoolmanagement.R;
import com.school.schoolmanagement.ScannerActivity;
import com.school.schoolmanagement.databinding.ActivityAccountantCollectFeesBinding;

public class ActivityAccountantCollectFees extends AppCompatActivity implements View.OnClickListener  {
    ActivityAccountantCollectFeesBinding binding;
    private ActivityResultLauncher<Intent> scannerLauncher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding=ActivityAccountantCollectFeesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setClickListeners();

        // Initialize scanner result launcher
        initScannerLauncher();

        // Set default tab (Student Wise is already selected in XML)
        updateTabUI(binding.tvStudentWise);
        binding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(ActivityAccountantCollectFees.this,ActivityAccountantCollectFees2.class);
                startActivity(intent);
            }
        });
    }

    private void setClickListeners() {
        binding.tvStudentWise.setOnClickListener(this);
        binding.tvFamilyWise.setOnClickListener(this);
        binding.tvScanInvoice.setOnClickListener(this);
        binding.btnSubmit.setOnClickListener(this);
    }

    private void initScannerLauncher() {
        scannerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        String scannedResult = result.getData().getStringExtra("scanned_result");
                        handleScannedResult(scannedResult);
                    }
                }
        );
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.tv_student_wise) {
            // Student wise tab clicked
            updateTabUI(binding.tvStudentWise);
            showStudentWiseContent();
        } else if (id == R.id.tv_family_wise) {
            // Family wise tab clicked
            updateTabUI(binding.tvFamilyWise);
            showFamilyWiseContent();
        } else if (id == R.id.tv_scan_invoice) {
            // Scan invoice tab clicked
            updateTabUI(binding.tvScanInvoice);
            startScanner();
        } else if (id == R.id.btn_submit) {
            // Submit button clicked
            submitFees();
        }
    }

    private void updateTabUI(View selectedTab) {
        // Reset all tabs to default state
        binding.tvStudentWise.setBackgroundResource(0);
        binding.tvFamilyWise.setBackgroundResource(0);
        binding.tvScanInvoice.setBackgroundResource(0);

        binding.tvStudentWise.setTextColor(getResources().getColor(R.color.grey));
        binding.tvFamilyWise.setTextColor(getResources().getColor(R.color.grey));
        binding.tvScanInvoice.setTextColor(getResources().getColor(R.color.grey));

        // Set selected tab to active state
        selectedTab.setBackgroundResource(R.drawable.tab_bg);
        if (selectedTab instanceof android.widget.TextView) {
            ((android.widget.TextView) selectedTab).setTextColor(getResources().getColor(R.color.white));
        }
    }

    private void showStudentWiseContent() {
        // Update heading
        binding.tvHeading.setText("Collect Fees for A Student");

        // Show Student Wise layout, hide others
        binding.linearStudentWise.setVisibility(View.VISIBLE);
        binding.linearFamilyWise.setVisibility(View.GONE);
        binding.linearScanInvoice.setVisibility(View.GONE);
    }

    private void showFamilyWiseContent() {
        // Update heading
        binding.tvHeading.setText("Collect Fees for A Family");

        // Show Family Wise layout, hide others
        binding.linearStudentWise.setVisibility(View.GONE);
        binding.linearFamilyWise.setVisibility(View.VISIBLE);
        binding.linearScanInvoice.setVisibility(View.GONE);
    }

    private void startScanner() {
        // Update heading
        binding.tvHeading.setText("Scan Invoice QR Code");

        // Launch scanner activity
        Intent scannerIntent = new Intent(this, ScannerActivity.class);
        scannerLauncher.launch(scannerIntent);
    }

    private void handleScannedResult(String scannedResult) {
        // Process the scanned result
        if (scannedResult != null && !scannedResult.isEmpty()) {
            // You might want to show the result on screen or process it
            // For example: binding.linearScanInvoice.tvScanResult.setText(scannedResult);

            // Show scan invoice layout
            binding.linearStudentWise.setVisibility(View.GONE);
            binding.linearFamilyWise.setVisibility(View.GONE);
            binding.linearScanInvoice.setVisibility(View.VISIBLE);
        } else {
            // Handle invalid scan
            // After handling the scan, reset to Student Wise tab
            updateTabUI(binding.tvStudentWise);
            showStudentWiseContent();
        }
    }

    private void submitFees() {
        // Handle fee submission logic
        // This would typically include data validation and API calls
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up binding
        binding = null;
    }
}