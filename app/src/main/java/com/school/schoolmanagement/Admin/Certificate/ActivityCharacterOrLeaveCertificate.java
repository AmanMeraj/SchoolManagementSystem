package com.school.schoolmanagement.Admin.Certificate;

import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.rajat.pdfviewer.PdfRendererView;
import com.school.schoolmanagement.GlobalViewModel.ViewModel;
import com.school.schoolmanagement.Utils.Utility;
import com.school.schoolmanagement.databinding.ActivityCharacterOrLeaveCertificateBinding;
import com.school.schoolmanagement.databinding.ActivityUpdateExaminationInfoBinding;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ActivityCharacterOrLeaveCertificate extends Utility {

    private static final String TAG = "CertificateActivity";

    ActivityCharacterOrLeaveCertificateBinding binding;
    ViewModel viewModel;

    // Intent data
    private String fromActivity;
    private int classId;
    private int studentId;
    private String selectedDate;
    private String className;
    private String studentName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityCharacterOrLeaveCertificateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        viewModel = new ViewModelProvider(this).get(ViewModel.class);

        // Apply edge insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(binding.main.getId()), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get data from intent
        getIntentData();

        // Set up UI
        setupUI();

        // Set up back button
        binding.toolbar.backBtn.setOnClickListener(view -> finish());

        // Check internet and load PDF
        if (isInternetConnected(this)) {
            loadCertificatePdf();
        } else {
            Toast.makeText(this, "No Internet Connection!", Toast.LENGTH_SHORT).show();
        }
    }

    private void getIntentData() {
        fromActivity = getIntent().getStringExtra("from");
        classId = getIntent().getIntExtra("classId", -1);
        studentId = getIntent().getIntExtra("studentId", -1);
        selectedDate = getIntent().getStringExtra("date");
        className = getIntent().getStringExtra("className");
        studentName = getIntent().getStringExtra("studentName");

        Log.d(TAG, "Intent Data - From: " + fromActivity +
                ", ClassId: " + classId +
                ", StudentId: " + studentId +
                ", Date: " + selectedDate +
                ", ClassName: " + className +
                ", StudentName: " + studentName);
    }

    private void setupUI() {
        // Update the certificate title based on type
        String certificateType = "";
        if ("leave".equalsIgnoreCase(fromActivity)) {
            certificateType = "Leave Certificate";
        } else if ("character".equalsIgnoreCase(fromActivity)) {
            certificateType = "Character Certificate";
        } else {
            certificateType = "Student Certificate";
        }
        binding.tvTitle2.setText(certificateType );

        Log.d(TAG, "UI Setup - Certificate Type: " + certificateType);
    }

    private void loadCertificatePdf() {
        showLoading("Generating " + (fromActivity != null ? fromActivity : "student") + " certificate...");

        String auth = "Bearer " + pref.getPrefString(this, pref.user_token);

        // Choose API based on 'from' parameter
        if ("leave".equalsIgnoreCase(fromActivity)) {
            getLeaveCertificatePdf(auth);
        } else if ("character".equalsIgnoreCase(fromActivity)) {
            getCharacterCertificatePdf(auth);
        } else {
            // Default fallback
            Log.w(TAG, "Unknown certificate type: " + fromActivity + ". Using default.");
        }
    }

    private void getLeaveCertificatePdf(String auth) {
        Log.d(TAG, "Fetching Leave Certificate PDF");

        // Call your leave certificate API
        viewModel.getLeaveCertificate(auth,selectedDate, classId,studentId)
                .observe(this, response -> {
                    if (response.isSuccess) {
                        hideLoading();
                        displayPdf(response.data.getData(), studentName+"leave");
                        Log.d(TAG, "Leave Certificate PDF loaded successfully");
                    } else {
                        hideLoading();
                        String errorMsg = "Failed to load leave certificate: " + response.message;
                        Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show();
                        Log.e(TAG, errorMsg);
                    }
                });
    }

    private void getCharacterCertificatePdf(String auth) {
        Log.d(TAG, "Fetching Character Certificate PDF");

        // Call your character certificate API
        viewModel.getCharacterCertificate(auth,selectedDate,classId,studentId )
                .observe(this, response -> {
                    if (response.isSuccess) {
                        hideLoading();
                        displayPdf(response.data.getData(), "");
                        Log.d(TAG, "Character Certificate PDF loaded successfully");
                    } else {
                        hideLoading();
                        String errorMsg = "Failed to load character certificate: " + response.message;
                        Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show();
                        Log.e(TAG, errorMsg);
                    }
                });
    }

    private void displayPdf(String base64PdfData, String fileName) {
        if (base64PdfData == null || base64PdfData.isEmpty()) {
            Toast.makeText(this, "No PDF data received", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Decode base64 PDF data
            byte[] pdfBytes = Base64.decode(base64PdfData, Base64.DEFAULT);

            // Create a proper filename - ensure it's not null or empty
            String pdfFileName;
            if (fileName != null && !fileName.trim().isEmpty()) {
                pdfFileName = fileName;
            } else {
                // Generate a default filename based on certificate type and timestamp
                String certificateType = fromActivity != null ? fromActivity : "certificate";
                pdfFileName = certificateType + "_" + System.currentTimeMillis() + ".pdf";
            }

            // Create file in cache directory
            File file = new File(getCacheDir(), pdfFileName);

            // Ensure the cache directory exists
            if (!getCacheDir().exists()) {
                getCacheDir().mkdirs();
            }

            // Write PDF data to file
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(pdfBytes);
            fos.close();

            // Load PDF in viewer
            binding.pdfView.initWithFile(file);

            Log.d(TAG, "PDF displayed successfully: " + file.getAbsolutePath());

        } catch (IOException e) {
            Log.e(TAG, "Error saving/displaying PDF", e);
            Toast.makeText(this, "Error displaying PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "Invalid base64 PDF data", e);
            Toast.makeText(this, "Invalid PDF data received", Toast.LENGTH_SHORT).show();
        }
    }

    private void showLoading(String message) {
        binding.loader2.rlLoader.setVisibility(View.VISIBLE);
        runOnUiThread(() -> Toast.makeText(this, message, Toast.LENGTH_SHORT).show());
    }

    private void hideLoading() {
        binding.loader2.rlLoader.setVisibility(View.GONE);
    }
}