package com.school.schoolmanagement.Students.TestResult;

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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.school.schoolmanagement.GlobalRepository.GlobalRepository;
import com.school.schoolmanagement.GlobalViewModel.ViewModel;
import com.school.schoolmanagement.R;
import com.school.schoolmanagement.Students.Adapter.AdapterStudentTest;
import com.school.schoolmanagement.Students.Model.StudentsTestResultResponse;
import com.school.schoolmanagement.Utils.Utility;
import com.school.schoolmanagement.databinding.ActivityStudentsTestResultBinding;

import java.util.ArrayList;
import java.util.List;

public class ActivityStudentsTestResult extends Utility {
    private static final String TAG = "ActivityStudentsTestResult";

    ActivityStudentsTestResultBinding binding;
    ViewModel viewModel;
    String auth;

    private List<StudentsTestResultResponse.Datum> testList;
    private AdapterStudentTest adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityStudentsTestResultBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        binding.toolbar.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        initializeViews();
        if(isInternetConnected(this)){
            setupRecyclerView();
            fetchTestResults();
        }else{
            Toast.makeText(this, "No Internet Connection!", Toast.LENGTH_SHORT).show();
        }

    }

    private void initializeViews() {
        viewModel = new ViewModelProvider(this).get(ViewModel.class);
        auth = "Bearer " + pref.getPrefString(this, pref.user_token);
        testList = new ArrayList<>();

        // Set student name if available from preferences
        String studentName = pref.getPrefString(this, "student_name");
        if (studentName != null && !studentName.isEmpty()) {
            binding.tvStudentsName.setText(studentName);
        }

        // Load profile image if available
        loadProfileImage();
    }

    private void setupRecyclerView() {
        binding.rcTestResult.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdapterStudentTest(this, testList);
        binding.rcTestResult.setAdapter(adapter);
    }

    private void fetchTestResults() {
        showProgressDialog("Loading test results...");

        viewModel.getStudentTestResult(auth).observe(this, response -> {
            hideProgressDialog();

            if (response != null) {
                if (response.isSuccess && response.data != null) {
                    handleSuccessResponse(response.data);
                } else {
                    handleErrorResponse(response.message);
                }
            } else {
                handleErrorResponse("Failed to fetch test results");
            }
        });
    }

    private void handleSuccessResponse(StudentsTestResultResponse response) {
        try {
            // Check if the response has data object
            if (response.getData() != null) {
                StudentsTestResultResponse.Data dataObject = response.getData();

                // Check if the data array exists and is not empty
                if (dataObject.getData() != null && !dataObject.getData().isEmpty()) {
                    testList.clear();
                    testList.addAll(dataObject.getData());
                    adapter.notifyDataSetChanged();

                    // Update student name if available
                    if (dataObject.getStudentName() != null && !dataObject.getStudentName().isEmpty()) {
                        binding.tvStudentsName.setText(dataObject.getStudentName());
                    }

                    // Update profile image if available
                    if (dataObject.getProfileImage() != null && !dataObject.getProfileImage().isEmpty()) {
                        loadBase64Image(dataObject.getProfileImage());
                    }

                    // Show success message
                    Toast.makeText(this, "Test results loaded successfully", Toast.LENGTH_SHORT).show();
                } else {
                    showEmptyState();
                }
            } else {
                showEmptyState();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error handling success response: " + e.getMessage());
            handleErrorResponse("Error processing test results");
        }
    }

    private void handleErrorResponse(String errorMessage) {
        Log.e(TAG, "Error fetching test results: " + errorMessage);

        if (errorMessage != null && errorMessage.contains("401")) {
            Toast.makeText(this, "Session expired. Please login again.", Toast.LENGTH_LONG).show();
            // Handle logout or redirect to login
        } else {
            Toast.makeText(this, "Failed to load test results. Please try again.", Toast.LENGTH_LONG).show();
        }

        showEmptyState();
    }

    private void showEmptyState() {
        testList.clear();
        adapter.notifyDataSetChanged();
        // You can show an empty state view here if needed
    }

    private void loadProfileImage() {
        // Load profile image from preferences if available
        String base64Image = pref.getPrefString(this, "student_profile_image");
        if (base64Image != null && !base64Image.isEmpty()) {
            loadBase64Image(base64Image);
        }
    }

    private void loadBase64Image(String base64String) {
        try {
            // Remove data:image prefix if present
            if (base64String.contains(",")) {
                base64String = base64String.split(",")[1];
            }

            // Decode base64 string to bitmap
            byte[] decodedString = Base64.decode(base64String, Base64.DEFAULT);
            Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

            if (decodedBitmap != null) {
                // Find the ImageView in the avatar layout
                // Since it's nested in RelativeLayouts, we need to find it properly
                View avatarRel = binding.avatarRel;
                if (avatarRel instanceof android.widget.RelativeLayout) {
                    android.widget.RelativeLayout parentRel = (android.widget.RelativeLayout) avatarRel;
                    View innerRel = parentRel.getChildAt(0);
                    if (innerRel instanceof android.widget.RelativeLayout) {
                        android.widget.RelativeLayout innerRelLayout = (android.widget.RelativeLayout) innerRel;
                        View imageView = innerRelLayout.getChildAt(0);
                        if (imageView instanceof android.widget.ImageView) {
                            ((android.widget.ImageView) imageView).setImageBitmap(decodedBitmap);
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading profile image: " + e.getMessage());
            // Keep default avatar if base64 decoding fails
        }
    }

    private void showProgressDialog(String message) {
        // Implement your progress dialog logic here
        // For example, show a progress bar or loading dialog
        if (binding.rcTestResult != null) {
            binding.rcTestResult.setVisibility(View.GONE);
        }
    }

    private void hideProgressDialog() {
        // Hide progress dialog
        if (binding.rcTestResult != null) {
            binding.rcTestResult.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}