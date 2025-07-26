package com.school.schoolmanagement.Accountant.Profile;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.school.schoolmanagement.GlobalRepository.GlobalRepository;
import com.school.schoolmanagement.GlobalViewModel.ViewModel;
import com.school.schoolmanagement.Model.EmployeeProfile;
import com.school.schoolmanagement.R;
import com.school.schoolmanagement.Utils.Utility;
import com.school.schoolmanagement.databinding.ActivityAcountantProfileBinding;

public class ActivityAccountantProfile extends Utility {
    private static final String TAG = "AccountantProfile";
    ViewModel viewModel;
    String auth;
    int employeeId;
    ActivityAcountantProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityAcountantProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeViews();
        setupObservers();
        loadEmployeeProfile();
    }

    private void initializeViews() {
        viewModel = new ViewModelProvider(this).get(ViewModel.class);
        auth = "Bearer " + pref.getPrefString(this, pref.user_token);

        // Get employee ID from intent or preferences
        employeeId = getIntent().getIntExtra("employeeId", 0);

        // Setup toolbar back button
        binding.toolbar.backBtn.setOnClickListener(v -> onBackPressed());
    }

    private void setupObservers() {
        viewModel.getEmployeeProfile(auth, employeeId).observe(this, response -> {
            if (response != null) {
                if (response.isSuccess && response.data != null) {
                    populateProfileData(response.data);
                } else {
                    handleError(response.message);
                }
            }
        });
    }

    private void loadEmployeeProfile() {
        if (isInternetConnected(this)) {
            if (employeeId > 0) {
                // The observer will handle the response
                Log.d(TAG, "Loading profile for employee ID: " + employeeId);
            } else {
                Toast.makeText(this, "Invalid employee ID", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
        }
    }

    private void populateProfileData(EmployeeProfile employeeProfile) {
        try {
            EmployeeProfile.Data data = employeeProfile.getData();
            if (data != null) {
                // Basic Info
                binding.tvTeachersName.setText(data.getName() != null ? data.getName() : "N/A");

                // Load profile image
                if (data.getProfilePicture() != null && !data.getProfilePicture().isEmpty()) {
                    Glide.with(this)
                            .load(data.getProfilePicture())
                            .placeholder(R.drawable.avatar2)
                            .error(R.drawable.avatar2)
                            .into(binding.imgProfile);
                }

                // Employee Information
                binding.tvRegistrationNo.setText(String.valueOf(data.getId()));
                binding.tvEmployeeRole.setText(data.getEmployeeRole() != null ? data.getEmployeeRole() : "N/A");
                binding.tvMonthlySalary.setText(data.getMonthlySalary() > 0 ? "₹" + String.format("%.2f", data.getMonthlySalary()) : "N/A");

                // Personal Information
                binding.tvFatherName.setText(data.getFatherName() != null ? data.getFatherName() : "N/A");
                binding.tvMobileNo.setText(data.getMobileNumber() != null ? data.getMobileNumber() : "N/A");
                binding.tvEmailAddress.setText(data.getEmail() != null ? data.getEmail() : "N/A");
                binding.tvHomeAddress.setText(data.getHomeAddress() != null ? data.getHomeAddress() : "N/A");

                // Additional Details
                binding.tvNationalId.setText(data.getNationalId() != null ? data.getNationalId() : "N/A");
                binding.tvEducation.setText(data.getEducation() != null ? data.getEducation() : "N/A");
                binding.tvGender.setText(data.getGender() != null ? data.getGender() : "N/A");
                binding.tvReligion.setText(data.getReligion() != null ? data.getReligion() : "N/A");
                binding.tvBloodGroup.setText(data.getBloodGroup() != null ? data.getBloodGroup() : "N/A");
                binding.tvDateOfBirth.setText(data.getDob() != null ? data.getDob() : "N/A");
                binding.tvDateOfJoining.setText(data.getDateOfJoining() != null ? data.getDateOfJoining() : "N/A");
                binding.tvExperience.setText(data.getExperience() > 0 ? data.getExperience() + " years" : "N/A");

                Log.d(TAG, "Profile data populated successfully");
            } else {
                Toast.makeText(this, "No profile data available", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error populating profile data: " + e.getMessage());
            Toast.makeText(this, "Error displaying profile data", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleError(String message) {
        Log.e(TAG, "Error loading profile: " + message);
        Toast.makeText(this, message != null ? message : "Failed to load profile", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}