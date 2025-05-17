package com.school.schoolmanagement.Admin.Employee.Details;

import static android.content.ContentValues.TAG;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import com.bumptech.glide.Glide;
import com.school.schoolmanagement.Admin.Model.Employee2;
import com.school.schoolmanagement.GlobalViewModel.ViewModel;
import com.school.schoolmanagement.R;
import com.school.schoolmanagement.Utils.Utility;
import com.school.schoolmanagement.databinding.ActivityEmployeesDetailsBinding;

public class ActivityEmployeesDetails extends Utility {
ActivityEmployeesDetailsBinding binding;
int employeeId;
Employee2 employee2Response;
ViewModel viewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding=ActivityEmployeesDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        viewModel= new ViewModelProvider(this).get(ViewModel.class);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        binding.toolbar.backBtn.setOnClickListener(view -> {
            finish();
        });

        employeeId=getIntent().getIntExtra("ID",0);

        if(isInternetConnected(this)){
            getEmployeeDetails();
        }else{
            Toast.makeText(this, "NO Internet Connection!", Toast.LENGTH_SHORT).show();
        }
    }

    private void getEmployeeDetails() {
        if (employeeId != 0) {
            Log.d(TAG, "getEmployeeDetails: Starting retrieval for employee ID: " + employeeId);
            // Show loading indicator
            showLoading(true);

            try {
                // Get auth token
                String authToken = "Bearer " + pref.getPrefString(this, pref.user_token);

                // Call the ViewModel method to get employee details
                viewModel.getEmployee(authToken, employeeId).observe(this, response -> {
                    // Hide loading indicator
                    showLoading(false);

                    if (response == null) {
                        Log.e(TAG, "API returned null response");
                        Toast.makeText(this, "Network error - null response", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (response.isSuccess && response.data != null) {
                        Log.d(TAG, "getEmployeeDetails: Successful API response with data");

                        // The key change - response.data is the Employee2 object
                        employee2Response = response.data;

                        // Fill the form with employee data
                        fillEmployeeData(employee2Response);
                    } else {
                        String errorMessage = response.message != null ? response.message : "Failed to fetch employee details";
                        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                showLoading(false);
                Log.e(TAG, "Exception in getEmployeeDetails: " + e.getMessage(), e);
                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
    private String getValueOrDefault(String value) {
        return (value != null && !value.trim().isEmpty()) ? value : "Not Provided";
    }


    private void fillEmployeeData(Employee2 employee2Response) {
        if (employee2Response != null && employee2Response.getData() != null) {

            // Basic information
            binding.tvTeachersName.setText(getValueOrDefault(employee2Response.getData().getName()));

            // Employee information section
            binding.tvRegistrationNo.setText(getValueOrDefault(String.valueOf(employee2Response.getData().getRegistrationNumber())));
            binding.tvEmployeeRole.setText(getValueOrDefault(employee2Response.getData().getEmployeeRole()));
            binding.tvMonthlySalary.setText(getValueOrDefault(String.valueOf(employee2Response.getData().getMonthlySalary())));
            binding.tvUsername.setText(getValueOrDefault(employee2Response.getData().getUsername()));
            binding.tvPassword.setText(getValueOrDefault(employee2Response.getData().getPassword()));

            // Profile information section
            binding.tvFatherName.setText(getValueOrDefault(employee2Response.getData().getFatherOrHusbandName()));
            binding.tvMobileNo.setText(getValueOrDefault(employee2Response.getData().getMobileNumber()));
            binding.tvEmailAddress.setText(getValueOrDefault(employee2Response.getData().getEmailAddress()));
            binding.tvHomeAddress.setText(getValueOrDefault(employee2Response.getData().getHomeAddress()));
            binding.tvNationalId.setText(getValueOrDefault(employee2Response.getData().getNationalId()));
            binding.tvEducation.setText(getValueOrDefault(employee2Response.getData().getEducation()));
            binding.tvGender.setText(getValueOrDefault(employee2Response.getData().getGender()));
            binding.tvReligion.setText(getValueOrDefault(employee2Response.getData().getReligion()));
            binding.tvBloodGroup.setText(getValueOrDefault(employee2Response.getData().getBloodGroup()));
            binding.tvDateOfBirth.setText(getValueOrDefault(employee2Response.getData().getDob()));
            binding.tvDateOfJoining.setText(getValueOrDefault(employee2Response.getData().getDateOfJoining()));
            binding.tvExperience.setText(getValueOrDefault(employee2Response.getData().getExperience()));

            // Load base64 image into profileImg using Glide
            String base64Image = employee2Response.getData().getProfilePicture();
            if (base64Image != null && !base64Image.trim().isEmpty()) {
                try {
                    if (base64Image.contains(",")) {
                        base64Image = base64Image.split(",")[1];
                    }
                    byte[] imageBytes = Base64.decode(base64Image, Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

                    Glide.with(this)
                            .load(bitmap)
                            .placeholder(R.drawable.avatar2)
                            .error(R.drawable.avatar2)
                            .into(binding.profileImg);
                } catch (Exception e) {
                    e.printStackTrace();
                    binding.profileImg.setImageResource(R.drawable.avatar2); // fallback image
                }
            } else {
                binding.profileImg.setImageResource(R.drawable.avatar2); // default image if null
            }
        }
    }


    private void showLoading(boolean isLoading) {
        if (isLoading) {
            binding.loader.rlLoader.setVisibility(View.VISIBLE);
        } else {
            binding.loader.rlLoader.setVisibility(View.GONE);
        }
    }
}