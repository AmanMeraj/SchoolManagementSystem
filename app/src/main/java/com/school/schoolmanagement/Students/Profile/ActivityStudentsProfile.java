package com.school.schoolmanagement.Students.Profile;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.school.schoolmanagement.GlobalViewModel.ViewModel;
import com.school.schoolmanagement.R;
import com.school.schoolmanagement.Students.Model.StudentsProfileResponse;
import com.school.schoolmanagement.Utils.Utility;
import com.school.schoolmanagement.databinding.ActivityStudentsProfileBinding;

public class ActivityStudentsProfile extends Utility {
    private static final String TAG = "ActivityStudentsProfile";
    ActivityStudentsProfileBinding binding;
    ViewModel viewModel;
    String auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityStudentsProfileBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeViews();
        if(isInternetConnected(this)){
            setupObservers();
            loadStudentProfile();
        }else{
            showToast("No Internet Connection!");
        }

    }

    private void initializeViews() {
        viewModel = new ViewModelProvider(this).get(ViewModel.class);
        auth = "Bearer " + pref.getPrefString(this, pref.user_token);
    }

    private void setupObservers() {
        viewModel.getStudentProfile(auth).observe(this, response -> {
            if (response != null) {
                if (response.isSuccess && response.data != null) {
                    populateStudentData(response.data);
                } else {
                    Log.e(TAG, "Error: " + response.message);
                    showToast("Failed to load student profile: " + response.message);
                }
            }
        });
    }

    private void showToast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    private void loadStudentProfile() {
        // The observer will automatically trigger when data is available
        // You can add a loading indicator here if needed
    }

    private void populateStudentData(StudentsProfileResponse profileResponse) {
        try {
            StudentsProfileResponse.Data data = profileResponse.getData();

            if (data == null) {
                Log.e(TAG, "Student data is null");
                return;
            }

            // Basic Student Information
            binding.tvStudentsName.setText(data.getStudentName() != null ? data.getStudentName() : "N/A");
            binding.tvFathersName.setText("Father: " + (data.getFatherName() != null ? data.getFatherName() : "N/A"));

            // Student Info Card
            binding.tvRegistrationNo.setText(data.getRegistrationNumber() != null ? data.getRegistrationNumber() : "N/A");
            binding.tvDateOfAdmission.setText(data.getDateOfAdmission() != null ? data.getDateOfAdmission() : "N/A");
            binding.tvClass.setText(data.getClassName() != null ? data.getClassName() : "N/A");
            binding.tvFamily.setText(data.getFamily() != null ? data.getFamily() : "N/A");
            binding.tvDiscount.setText(String.valueOf(data.getDiscountInFees()) + "%");

            // Other Student Information (Additional Details)
            setTextViewSafely(binding.getRoot().findViewById(getDateOfBirthTextViewId()), data.getDateOfBirth());
            setTextViewSafely(binding.getRoot().findViewById(getGenderTextViewId()), data.getGender());
            setTextViewSafely(binding.getRoot().findViewById(getIdentificationMarkTextViewId()), data.getBloodGroup() != null ? "Yes" : "No");
            setTextViewSafely(binding.getRoot().findViewById(getDiseaseTextViewId()), data.getDiseaseIfAny() != null ? data.getDiseaseIfAny() : "None");
            setTextViewSafely(binding.getRoot().findViewById(getBirthFormIdTextViewId()), data.getBirthFormIdOrNic());
            setTextViewSafely(binding.getRoot().findViewById(getPreviousSchoolTextViewId()), data.getPreviousSchool());
            setTextViewSafely(binding.getRoot().findViewById(getPreviousIdTextViewId()), data.getPreviousIdOrBoardRollNumber());
            setTextViewSafely(binding.getRoot().findViewById(getAdditionalNoteTextViewId()), data.getNote() != null ? data.getNote() : "None");
            setTextViewSafely(binding.getRoot().findViewById(getOrphanStudentTextViewId()), "No"); // This field is not in the model
            setTextViewSafely(binding.getRoot().findViewById(getOscTextViewId()), data.getOsc() != null ? data.getOsc() : "None");
            setTextViewSafely(binding.getRoot().findViewById(getReligionTextViewId()), data.getCast() != null ? data.getCast() : "N/A");
            setTextViewSafely(binding.getRoot().findViewById(getTotalSiblingsTextViewId()), String.valueOf(data.getTotalSibling()));

            // Parent Information
            binding.tvFatherName.setText(data.getFatherName() != null ? data.getFatherName() : "N/A");
            binding.tvMotherName.setText(data.getMotherName() != null ? data.getMotherName() : "N/A");
            binding.tvAddress.setText(data.getAddress() != null ? data.getAddress() : "N/A");

            // Load profile image from base64 string
            loadProfileImage(data);

        } catch (Exception e) {
            Log.e(TAG, "Error populating student data: " + e.getMessage(), e);
            showToast("Error displaying student information");
        }
    }

    private void loadProfileImage(StudentsProfileResponse.Data data) {
        try {
            // Find the profile image view in the avatar layout
            ImageView profileImageView = binding.imgProfile;

            if (profileImageView == null) {
                Log.w(TAG, "Profile ImageView not found");
                return;
            }

            String base64Image = getProfileImageBase64(data);

            if (base64Image != null && !base64Image.trim().isEmpty()) {
                Bitmap bitmap = decodeBase64ToBitmap(base64Image);
                if (bitmap != null) {
                    profileImageView.setImageBitmap(bitmap);
                    Log.d(TAG, "Profile image loaded successfully");
                } else {
                    Log.w(TAG, "Failed to decode base64 image");
                    setDefaultProfileImage(profileImageView);
                }
            } else {
                Log.w(TAG, "No profile image data available");
                setDefaultProfileImage(profileImageView);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading profile image: " + e.getMessage(), e);
            ImageView profileImageView = binding.imgProfile;
            setDefaultProfileImage(profileImageView);
        }
    }

    private Bitmap decodeBase64ToBitmap(String base64String) {
        try {
            // Remove data URL prefix if present (e.g., "data:image/png;base64,")
            String cleanBase64 = base64String;
            if (base64String.contains(",")) {
                cleanBase64 = base64String.substring(base64String.indexOf(",") + 1);
            }

            // Remove any whitespace
            cleanBase64 = cleanBase64.replaceAll("\\s", "");

            // Decode base64 string to byte array
            byte[] decodedBytes = Base64.decode(cleanBase64, Base64.DEFAULT);

            // Convert byte array to bitmap
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);

            return bitmap;
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "Invalid base64 string: " + e.getMessage());
            return null;
        } catch (Exception e) {
            Log.e(TAG, "Error decoding base64 to bitmap: " + e.getMessage());
            return null;
        }
    }

    private String getProfileImageBase64(StudentsProfileResponse.Data data) {
        // Return the profile image base64 string from your model
        return data.getParentEmail();
    }

    private void setDefaultProfileImage(ImageView imageView) {
        if (imageView != null) {
            imageView.setImageResource(R.drawable.avatar2); // Default avatar
        }
    }

    private void setTextViewSafely(View view, String text) {
        if (view instanceof android.widget.TextView) {
            ((android.widget.TextView) view).setText(text != null ? text : "N/A");
        }
    }

    // Helper methods to get TextView IDs for the additional information section
    // Since these TextViews don't have IDs in the XML, you'll need to add them or use findViewById with tags
    private int getDateOfBirthTextViewId() {
        // You'll need to add android:id="@+id/tv_date_of_birth_value" to the corresponding TextView in XML
        return R.id.tv_date_of_birth_value; // Add this ID to your XML
    }

    private int getGenderTextViewId() {
        return R.id.tv_gender_value; // Add this ID to your XML
    }

    private int getIdentificationMarkTextViewId() {
        return R.id.tv_identification_mark_value; // Add this ID to your XML
    }

    private int getDiseaseTextViewId() {
        return R.id.tv_disease_value; // Add this ID to your XML
    }

    private int getBirthFormIdTextViewId() {
        return R.id.tv_birth_form_id_value; // Add this ID to your XML
    }

    private int getPreviousSchoolTextViewId() {
        return R.id.tv_previous_school_value; // Add this ID to your XML
    }

    private int getPreviousIdTextViewId() {
        return R.id.tv_previous_id_value; // Add this ID to your XML
    }

    private int getAdditionalNoteTextViewId() {
        return R.id.tv_additional_note_value; // Add this ID to your XML
    }

    private int getOrphanStudentTextViewId() {
        return R.id.tv_orphan_student_value; // Add this ID to your XML
    }

    private int getOscTextViewId() {
        return R.id.tv_osc_value; // Add this ID to your XML
    }

    private int getReligionTextViewId() {
        return R.id.tv_religion_value; // Add this ID to your XML
    }

    private int getTotalSiblingsTextViewId() {
        return R.id.tv_total_siblings_value; // Add this ID to your XML
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}