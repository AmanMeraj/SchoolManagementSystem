package com.school.schoolmanagement.Students.AdmissionConfirmation;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.school.schoolmanagement.GlobalViewModel.ViewModel;
import com.school.schoolmanagement.HelperClasses.AdmissionLetterPdfGenerator;
import com.school.schoolmanagement.HelperClasses.ReceiptPdfHelper;
import com.school.schoolmanagement.Model.StudentsAdmissionConfirmationResponse;
import com.school.schoolmanagement.R;
import com.school.schoolmanagement.Utils.Utility;
import com.school.schoolmanagement.databinding.ActivityStudentsAdmissionConfirmationBinding;

public class ActivityStudentsAdmissionConfirmation extends Utility {
    private static final String TAG = "AdmissionConfirmation";

    ActivityStudentsAdmissionConfirmationBinding binding;
    String auth;
    ViewModel viewModel;
    StudentsAdmissionConfirmationResponse.Data studentData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityStudentsAdmissionConfirmationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeComponents();
        setupClickListeners();
        fetchAdmissionConfirmationData();
    }

    private void initializeComponents() {
        viewModel = new ViewModelProvider(this).get(ViewModel.class);
        auth = "Bearer " + pref.getPrefString(this, pref.user_token);
    }

    private void setupClickListeners() {
        // Back button click listener
        binding.toolbar.backBtn.setOnClickListener(v -> finish());

        // Print admission letter button click listener
        binding.createBtn.setOnClickListener(v -> {
            if (studentData != null) {
                printAdmissionLetter();
            } else {
                Toast.makeText(this, "Student data not available", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchAdmissionConfirmationData() {
        showProgressDialog("Loading admission details...");

        viewModel.getStudentAdmissionConfirmation(auth).observe(this, response -> {
            hideProgressDialog();

            if (response != null) {
                if (response.isSuccess && response.data != null) {
                    handleSuccessResponse(response.data);
                } else {
                    handleErrorResponse(response.message);
                }
            } else {
                handleErrorResponse("Failed to load admission details");
            }
        });
    }

    private void hideProgressDialog() {
        binding.loader.rlLoader.setVisibility(View.GONE);
    }

    private void showProgressDialog(String s) {
        Toast.makeText(this,s, Toast.LENGTH_SHORT).show();
        binding.loader.rlLoader.setVisibility(View.VISIBLE);
    }

    private void handleSuccessResponse(StudentsAdmissionConfirmationResponse data) {
        try {
            if (data.getData() != null) {
                studentData = data.getData();
                populateStudentData();
            } else {
                Toast.makeText(this, "No student data found", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error handling success response: " + e.getMessage());
            Toast.makeText(this, "Error processing data", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleErrorResponse(String errorMessage) {
        Log.e(TAG, "API Error: " + errorMessage);
        Toast.makeText(this, errorMessage != null ? errorMessage : "Something went wrong", Toast.LENGTH_LONG).show();
    }

    private void populateStudentData() {
        if (studentData == null) return;

        try {
            // Set student name
            if (studentData.getName() != null && !studentData.getName().isEmpty()) {
                binding.tvStudentsName.setText(studentData.getName());
            } else {
                binding.tvStudentsName.setText("Student Name");
            }

            // Load profile image
            loadProfileImage();

            // Set student information in the info card
            setStudentInfo();

        } catch (Exception e) {
            Log.e(TAG, "Error populating student data: " + e.getMessage());
            Toast.makeText(this, "Error displaying student information", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadProfileImage() {
        // Find the ImageView inside the avatar layout
        View avatarImageView = binding.avatarRel;

        if (avatarImageView != null && studentData.getProfileImage() != null && !studentData.getProfileImage().isEmpty()) {
            try {
                Glide.with(this)
                        .load(studentData.getProfileImage())
                        .apply(new RequestOptions()
                                .placeholder(R.drawable.avatar2)
                                .error(R.drawable.avatar2)
                                .circleCrop())
                        .into((android.widget.ImageView) avatarImageView);
            } catch (Exception e) {
                Log.e(TAG, "Error loading profile image: " + e.getMessage());
            }
        }
    }

    private void setStudentInfo() {

        // For now, we'll log the data that should be displayed
        Log.d(TAG, "Student ID: " + studentData.getStudentId());
        Log.d(TAG, "Admission Date: " + studentData.getAdmissionDate());
        Log.d(TAG, "Account Status: " + studentData.getAccountStatus());
        Log.d(TAG, "Username: " + studentData.getUsername());
        Log.d(TAG, "Class: " + studentData.getClassName());
        binding.regNo.setText(String.valueOf(studentData.getStudentId()));
        binding.tvAdmissionDate.setText(studentData.getAdmissionDate());
        binding.status.setText(studentData.getAccountStatus());
        binding.username.setText(studentData.getUsername());
        binding.password.setText(studentData.getPassword());
    }
    // Inner class for admission data
    public static class AdmissionData {
        public String studentName;
        public String registrationId;
        public String className;
        public String admissionDate;
        public String accountStatus;
        public String username;
        public String password;
        public String profileImageBase64; // Add this field for base64 image

        public boolean isDataValid() {
            return studentName != null && !studentName.trim().isEmpty() &&
                    registrationId != null && !registrationId.trim().isEmpty();
        }

        public String getFormattedAdmissionTitle() {
            return "Admission_Confirmation_" + (studentName != null ? studentName.replaceAll("\\s+", "_") : "Student");
        }
    }

    // Update your createAdmissionData method
    private AdmissionData createAdmissionData() {
        AdmissionData admissionData = new AdmissionData();

        // Set basic student information
        admissionData.studentName = studentData.getName() != null ? studentData.getName() : "N/A";
        admissionData.registrationId = String.valueOf(studentData.getStudentId());
        admissionData.className = studentData.getClassName() != null ? studentData.getClassName() : "N/A";
        admissionData.admissionDate = studentData.getAdmissionDate() != null ? studentData.getAdmissionDate() : "N/A";
        admissionData.accountStatus = studentData.getAccountStatus() != null ? studentData.getAccountStatus() : "Active";
        admissionData.username = studentData.getUsername() != null ? studentData.getUsername() : "N/A";
        admissionData.password = studentData.getPassword() != null ? studentData.getPassword() : "N/A";

        // Add profile image base64 string
        admissionData.profileImageBase64 = studentData.getProfileImage();

        return admissionData;
    }

    private void printAdmissionLetter() {
        try {
            if (studentData == null) {
                Toast.makeText(this, "Student data not available", Toast.LENGTH_SHORT).show();
                return;
            }

            // Show progress dialog
            showProgressDialog("Generating admission letter...");

            // Create admission data for printing
            AdmissionData admissionData = createAdmissionData();

            // Create the PDF generator and generate the admission letter
            AdmissionLetterPdfGenerator pdfGenerator = new AdmissionLetterPdfGenerator(this);

            // Generate the PDF in a background thread to avoid blocking UI
            new Thread(() -> {
                try {
                    pdfGenerator.generateAdmissionLetter(admissionData);

                    // Hide progress dialog on main thread
                    runOnUiThread(() -> {
                        hideProgressDialog();
                    });

                } catch (Exception e) {
                    Log.e(TAG, "Error generating PDF: " + e.getMessage());
                    runOnUiThread(() -> {
                        hideProgressDialog();
                        Toast.makeText(this, "Error generating admission letter", Toast.LENGTH_SHORT).show();
                    });
                }
            }).start();

        } catch (Exception e) {
            hideProgressDialog();
            Log.e(TAG, "Error in print function: " + e.getMessage());
            Toast.makeText(this, "Error preparing admission letter", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (binding != null) {
            binding = null;
        }
    }
}