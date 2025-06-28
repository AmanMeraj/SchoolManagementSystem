package com.school.schoolmanagement.Admin.Students.AdmissionLetter;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.school.schoolmanagement.GlobalViewModel.ViewModel;
import com.school.schoolmanagement.Model.StudentDetails;
import com.school.schoolmanagement.R;
import com.school.schoolmanagement.Utils.Utility;
import com.school.schoolmanagement.databinding.ActivityAdmissionLetterBinding;

import java.util.ArrayList;
import java.util.List;

public class ActivityAdmissionLetter extends Utility {
    ActivityAdmissionLetterBinding binding;
    String authToken;
    private List<StudentDetails> studentList;
    private List<StudentDetails> filteredStudentList;
    private ArrayAdapter<String> searchAdapter;
    private List<String> studentDisplayList;
    ArrayList<StudentDetails>responseDetails;
    ViewModel viewModel;
    private String selectedStudentId = "";
    private boolean isDialogShowing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityAdmissionLetterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        viewModel = new ViewModelProvider(this).get(ViewModel.class);

        // Initialize lists
        studentList = new ArrayList<>();
        filteredStudentList = new ArrayList<>();
        studentDisplayList = new ArrayList<>();

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


      setupRefreshButton();
      setupSearchButton();

        authToken = "Bearer " + pref.getPrefString(this, pref.user_token);

        // Load students when activity starts
        if(isInternetConnected(this)){
            loadStudentsFromAPI();
        }else{
            Toast.makeText(this, "No Internet Connection!", Toast.LENGTH_SHORT).show();
        }

        setupSearchFunctionality();
    }


    private void setupRefreshButton() {
        binding.refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Reset UI elements to initial state
                resetUIToInitialState();

                // Reload students data from API
                if(isInternetConnected(ActivityAdmissionLetter.this)){
                    loadStudentsFromAPI();
                } else {
                    Toast.makeText(ActivityAdmissionLetter.this, "No Internet Connection!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setupSearchButton() {
        try {
            if (binding != null ) {
                binding.searchBtn.setOnClickListener(new View.OnClickListener() {
                    private boolean isOrange = false;

                    @Override
                    public void onClick(View view) {
                        try {
                            if (binding != null ) {
                                if (isOrange) {
                                    binding.searchBtn.setColorFilter(null);
                                    binding.relSearch.setVisibility(View.GONE);
                                    binding.edtSearchStudent.setText("");
                                    selectedStudentId = "";
                                } else {
                                    binding.searchBtn.setColorFilter(getResources().getColor(R.color.orange));
                                    binding.relSearch.setVisibility(View.VISIBLE);
                                    binding.edtSearchStudent.requestFocus();
                                }
                                isOrange = !isOrange;
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error in search button click: " + e.getMessage(), e);
                        }
                    }
                });
            }
        } catch (Exception e) {
            Log.e(TAG, "Error setting up search button: " + e.getMessage(), e);
        }
    }
    private void resetUIToInitialState() {
        try {
            // Hide admission confirmation card and create button
            binding.cardAdmissionConfirmation.setVisibility(View.GONE);
            binding.createBtn.setVisibility(View.GONE);

            // Reset search functionality
            binding.relSearch.setVisibility(View.GONE);
            binding.edtSearchStudent.setText("");
            binding.searchBtn.setColorFilter(null);

            // Clear selected student
            selectedStudentId = "";

            // Reset dialog flag
            isDialogShowing = false;

            // Clear all student lists
            if (studentList != null) {
                studentList.clear();
            }
            if (filteredStudentList != null) {
                filteredStudentList.clear();
            }
            if (studentDisplayList != null) {
                studentDisplayList.clear();
            }
            if (responseDetails != null) {
                responseDetails.clear();
            }

            // Clear UI text fields (in case they have data)
            binding.nameTv.setText("");
            binding.tvRegistrationNumber.setText("");
            binding.tvClass.setText("");
            binding.tvDob.setText("");
            binding.tvAccountStatus.setText("");
            binding.tvUsername.setText("");
            binding.tvPassword.setText("");

            // Reset profile image to default
            Glide.with(this)
                    .load(R.drawable.avatar2)
                    .into(binding.profileImg);

            Log.d(TAG, "UI reset to initial state");

        } catch (Exception e) {
            Log.e(TAG, "Error resetting UI to initial state: " + e.getMessage(), e);
        }
    }

    private void setupSearchFunctionality() {
        try {
            if (binding != null) {
                binding.edtSearchStudent.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        // Not needed
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        String query = s != null ? s.toString().trim() : "";
                        if (!query.isEmpty()) {
                            filterStudents(query);
                            // Only show dialog if it's not already showing and we have results
                            if (!filteredStudentList.isEmpty() && !isDialogShowing) {
                                showStudentSelectionDialog();
                            }
                        } else {
                            // Clear filtered list when query is empty
                            filteredStudentList.clear();
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        // Not needed
                    }
                });
            }
        } catch (Exception e) {
            Log.e(TAG, "Error setting up search functionality: " + e.getMessage(), e);
        }
    }

    private void showStudentSelectionDialog() {
        try {
            if (filteredStudentList == null || filteredStudentList.isEmpty() || isDialogShowing) {
                return;
            }

            isDialogShowing = true; // Set flag to prevent multiple dialogs

            // Prepare display list for dialog
            studentDisplayList.clear();
            for (StudentDetails student : filteredStudentList) {
                if (student != null && student.getStudentName() != null) {
                    String displayText = student.getStudentName();
                    if (student.getStudentId() > 0) {
                        displayText += " (ID: " + student.getStudentId() + ")";
                    }
                    studentDisplayList.add(displayText);
                }
            }

            if (studentDisplayList.isEmpty()) {
                isDialogShowing = false; // Reset flag if no data
                return;
            }

            // Create ArrayAdapter for the dialog
            searchAdapter = new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_list_item_1,
                    studentDisplayList
            );

            // Create and show Material 3 dialog
            new MaterialAlertDialogBuilder(this)
                    .setTitle("Select Student")
                    .setAdapter(searchAdapter, (dialog, which) -> {
                        try {
                            if (which >= 0 && which < filteredStudentList.size()) {
                                StudentDetails selectedStudent = filteredStudentList.get(which);
                                if (selectedStudent != null) {
                                    // Set selected student ID
                                    selectedStudentId = String.valueOf(selectedStudent.getStudentId());

                                    // Clear and close search
                                    binding.edtSearchStudent.setText("");
                                    binding.relSearch.setVisibility(View.GONE);
                                    binding.searchBtn.setColorFilter(null);

                                    // Show confirmation toast
                                    showToast("Selected: " + selectedStudent.getStudentName() +
                                            " (ID: " + selectedStudentId + ")");

                                    // Handle the selected student
                                    onStudentSelected(selectedStudent);
                                }
                            }
                            isDialogShowing = false; // Reset flag when dialog is dismissed
                            dialog.dismiss();
                        } catch (Exception e) {
                            Log.e(TAG, "Error selecting student: " + e.getMessage(), e);
                            isDialogShowing = false; // Reset flag on error
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> {
                        isDialogShowing = false; // Reset flag when canceled
                        dialog.dismiss();
                    })
                    .setOnDismissListener(dialog -> {
                        isDialogShowing = false; // Reset flag when dialog is dismissed
                    })
                    .show();

        } catch (Exception e) {
            Log.e(TAG, "Error showing student selection dialog: " + e.getMessage(), e);
            isDialogShowing = false; // Reset flag on error
        }
    }

    private void onStudentSelected(StudentDetails student) {
        try {
            // Handle the selected student here
            Log.d(TAG, "Student selected: " + student.getStudentName() +
                    ", ID: " + student.getStudentId());
            if(isInternetConnected(this)){
                getDetails(student.studentId);
            }else{
                Toast.makeText(this, "No Internet Connection!", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            Log.e(TAG, "Error handling selected student: " + e.getMessage(), e);
        }
    }

    private void getDetails(int studentId) {
        String auth = "Bearer "+ pref.getPrefString(this,pref.user_token);
        showLoading(true);
        viewModel.getStudentsDetails(auth,studentId).observe(this,response->{
            if(response.isSuccess){
                responseDetails=response.data.getData();
                showLoading(false);
                setData(responseDetails);
            }else {
                showLoading(false);
                showToast(response.message);
            }
        });
    }

    private void setData(ArrayList<StudentDetails> response) {
        binding.nameTv.setText(response.get(0).getStudentName());
        binding.tvRegistrationNumber.setText(String.valueOf(response.get(0).getRollNumber()));
        binding.tvClass.setText(response.get(0).getClassName());
        binding.tvDob.setText(response.get(0).getDateOfBirth());
        binding.tvAccountStatus.setText(response.get(0).getAccountStatus());
        binding.tvUsername.setText(response.get(0).getUsername());
        binding.tvPassword.setText(response.get(0).getPassword());

        String base64Image = response.get(0).getProfilePicture(); // Assuming this method returns Base64 string
        setBase64ImageWithGlide(base64Image, binding.profileImg);
        binding.cardAdmissionConfirmation.setVisibility(View.VISIBLE);
        binding.createBtn.setVisibility(View.VISIBLE);
    }
    private void setBase64ImageWithGlide(String base64String, ImageView imageView) {
        if (base64String != null && !base64String.isEmpty()) {
            try {
                String cleanBase64 = base64String;
                if (base64String.contains(",")) {
                    cleanBase64 = base64String.substring(base64String.indexOf(",") + 1);
                }

                byte[] decodedBytes = Base64.decode(cleanBase64, Base64.DEFAULT);

                Glide.with(this)
                        .load(decodedBytes)
                        .placeholder(R.drawable.avatar2)
                        .error(R.drawable.avatar2)
                        .circleCrop() // Optional: make it circular
                        .into(imageView);
            } catch (Exception e) {
                e.printStackTrace();
                Glide.with(this)
                        .load(R.drawable.avatar2)
                        .into(imageView);
            }
        }
    }

    private void loadStudentsFromAPI() {
        try {
            showLoading(true);

            if (viewModel == null) {
                Log.e(TAG, "ViewModel is null");
                showLoading(false);
                showToast("Error: ViewModel not initialized");
                return;
            }

            if (authToken == null || authToken.isEmpty()) {
                Log.e(TAG, "Auth token is null or empty");
                showLoading(false);
                showToast("Authentication error. Please login again.");
                return;
            }

            viewModel.getAllStudents(authToken).observe(this, response -> {
                try {
                    showLoading(false);

                    if (response == null) {
                        Log.e(TAG, "Response is null");
                        showToast("No response from server");
                        return;
                    }

                    if (response.isSuccess && response.data.getData() != null) {
                        handleSuccessResponse(response.data.getData());
                    } else {
                        String errorMessage = response.message != null ?
                                response.message : "Failed to fetch students";
                        Log.e(TAG, "API Error: " + errorMessage + ", Code: " + response.code);
                        showToast(errorMessage);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error handling API response: " + e.getMessage(), e);
                    showLoading(false);
                    showToast("Error processing server response");
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error loading students from API: " + e.getMessage(), e);
            showLoading(false);
            showToast("Error connecting to server");
        }
    }

    private void handleSuccessResponse(Object data) {
        try {
            // Clear existing data
            studentList.clear();
            filteredStudentList.clear();

            if (data instanceof List) {
                List<?> responseList = (List<?>) data;
                for (Object item : responseList) {
                    if (item instanceof StudentDetails) {
                        studentList.add((StudentDetails) item);
                    }
                }
            }

            // Update filtered list
            filteredStudentList.addAll(studentList);

            Log.d(TAG, "Successfully loaded " + studentList.size() + " students");
            showToast("Successfully loaded " + studentList.size() + " students");

        } catch (Exception e) {
            Log.e(TAG, "Error handling success response: " + e.getMessage(), e);
            showToast("Error processing student data");
        }
    }

    private void filterStudents(String query) {
        try {
            if (studentList == null) {
                return;
            }

            filteredStudentList.clear();

            if (query == null || query.isEmpty()) {
                filteredStudentList.addAll(studentList);
            } else {
                String lowerCaseQuery = query.toLowerCase();
                for (StudentDetails student : studentList) {
                    if (student != null && student.getStudentName() != null) {
                        String studentName = student.getStudentName().toLowerCase();
                        String studentId = String.valueOf(student.getStudentId());

                        if (studentName.contains(lowerCaseQuery) ||
                                studentId.contains(query)) {
                            filteredStudentList.add(student);
                        }
                    }
                }
            }

        } catch (Exception e) {
            Log.e(TAG, "Error filtering students: " + e.getMessage(), e);
        }
    }

    private void showLoading(boolean isLoading) {
        Log.d(TAG, "Loading: " + isLoading);
        try {
            if (binding != null) {
                binding.loader.rlLoader.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error showing/hiding loader: " + e.getMessage(), e);
        }
    }

    private void showToast(String message) {
        Log.d(TAG, "Toast: " + message);
        try {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error showing toast: " + e.getMessage(), e);
        }
    }

    // Getter method to access selected student ID from other parts of your code
    public String getSelectedStudentId() {
        return selectedStudentId;
    }
}