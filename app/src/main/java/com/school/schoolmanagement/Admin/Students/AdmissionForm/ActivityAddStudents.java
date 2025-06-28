package com.school.schoolmanagement.Admin.Students.AdmissionForm;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.Gson;
import com.school.schoolmanagement.Admin.Model.ClassModel;
import com.school.schoolmanagement.GlobalViewModel.ViewModel;
import com.school.schoolmanagement.Model.StudentFields;
import com.school.schoolmanagement.R;
import com.school.schoolmanagement.Utils.Utility;
import com.school.schoolmanagement.databinding.ActivityAddStudentsBinding;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

public class ActivityAddStudents extends Utility {
    ActivityAddStudentsBinding binding;
    ViewModel viewModel;
    private ArrayList<ClassModel.Data> classList;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private int selectedClassId = -1;
    private Map<String, Integer> classNameToIdMap = new HashMap<>();

    String FLAG="New";
    private Uri photoURI;
    private String currentPhotoPath;
    private byte[] profileImageData;

    private static final int CAMERA_PERMISSION_CODE = 101;
    private static final int GALLERY_PERMISSION_CODE = 102;
    private static final int MAX_IMAGE_SIZE_KB = 100;

    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<Intent> galleryLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityAddStudentsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        viewModel = new ViewModelProvider(this).get(ViewModel.class);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize class list
        classList = new ArrayList<>();

        //set up activity result launcher
        setupActivityResultLaunchers();

        // Set up date pickers
        setupDatePickers();

        // Set up class selection dialog
        setupClassSelectionDialog();

        // Fetch class data from API
        fetchClassData();

        // Set up toolbar navigation
        setupToolbar();

       //set up buttons
        setUpButtons();

        //set up spinners
        setupSpinners();


    }

    private void setUpButtons(){
        binding.btnChangeLogo.setOnClickListener(v -> showImageSourceDialog());

        // Add validation on form submission
        binding.submitBtn.setOnClickListener(view -> {
            if (validateForm()) {
                submitForm();
            }
        });
    }


    private void setupActivityResultLaunchers() {
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        try {
                            Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath);
                            if (bitmap == null) {
                                Log.e(TAG, "Failed to decode bitmap from camera path");
                                Toast.makeText(this, "Failed to load captured image", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            bitmap = compressImage(bitmap);
                            binding.imgGallery.setImageBitmap(bitmap);

                            // Convert bitmap to byte array for upload
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
                            profileImageData = stream.toByteArray();

                            // Check file size
                            if (profileImageData.length > MAX_IMAGE_SIZE_KB * 1024) {
                                Toast.makeText(this, "Image is too large, please select another one", Toast.LENGTH_SHORT).show();
                                profileImageData = null;
                            } else {
                                Log.d(TAG, "Camera image processed successfully, size: " + (profileImageData.length / 1024) + "KB");
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error handling camera result: " + e.getMessage(), e);
                            Toast.makeText(this, "Error processing camera image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        try {
                            Uri selectedImageUri = result.getData().getData();
                            if (selectedImageUri == null) {
                                Log.e(TAG, "Selected image URI is null");
                                Toast.makeText(this, "Failed to load selected image", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                            if (bitmap == null) {
                                Log.e(TAG, "Failed to load bitmap from gallery URI");
                                Toast.makeText(this, "Failed to process selected image", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            bitmap = compressImage(bitmap);
                            binding.imgGallery.setImageBitmap(bitmap);

                            // Convert bitmap to byte array for upload
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
                            profileImageData = stream.toByteArray();

                            // Check file size
                            if (profileImageData.length > MAX_IMAGE_SIZE_KB * 1024) {
                                Toast.makeText(this, "Image is too large, please select another one", Toast.LENGTH_SHORT).show();
                                profileImageData = null;
                            } else {
                                Log.d(TAG, "Gallery image processed successfully, size: " + (profileImageData.length / 1024) + "KB");
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error handling gallery result: " + e.getMessage(), e);
                            Toast.makeText(this, "Error processing gallery image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }

    private void showImageSourceDialog() {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};

        new MaterialAlertDialogBuilder(this)
                .setTitle("Choose Profile Picture")
                .setItems(options, (dialog, which) -> {
                    if (options[which].equals("Take Photo")) {
                        if (checkCameraPermission()) {
                            openCamera();
                        } else {
                            requestCameraPermission();
                        }
                    } else if (options[which].equals("Choose from Gallery")) {
                        if (checkGalleryPermission()) {
                            openGallery();
                        } else {
                            requestGalleryPermission();
                        }
                    } else {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private boolean checkCameraPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.CAMERA},
                CAMERA_PERMISSION_CODE
        );
    }

    private boolean checkGalleryPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                    == PackageManager.PERMISSION_GRANTED;
        } else {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED;
        }
    }

    private void requestGalleryPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                    GALLERY_PERMISSION_CODE
            );
        } else {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    GALLERY_PERMISSION_CODE
            );
        }
    }
    private void setupSpinners() {
        // Gender Spinner
        String[] genderOptions = {"Select Gender", "Male", "Female", "Other"};
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                genderOptions
        );
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerGender.setAdapter(genderAdapter);

        // Religion Spinner

        String[] religionOptions = {"Select Religion", "Christianity", "Islam", "Hinduism", "Buddhism", "Judaism", "Sikhism", "Other"};
        ArrayAdapter<String> religionAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                religionOptions
        );
        religionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerReligion.setAdapter(religionAdapter);

        String[] yesNo = {"No","Yes"};
        ArrayAdapter<String> yesNoAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                yesNo
        );
        yesNoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerOrphan.setAdapter(yesNoAdapter);

        // Blood Group Spinner
        String[] bloodGroupOptions = {"Select Blood Group", "A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
        ArrayAdapter<String> bloodGroupAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                bloodGroupOptions
        );
        bloodGroupAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.edtBloodGroup.setAdapter(bloodGroupAdapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == GALLERY_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                // Permission denied - check if we should show rationale
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    new MaterialAlertDialogBuilder(this)
                            .setTitle("Permission Required")
                            .setMessage("Gallery access is required to select a photo. Please grant permission.")
                            .setPositiveButton("Allow", (dialog, which) -> requestGalleryPermission())
                            .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                            .show();
                } else {
                    // User checked "Don't ask again"
                    Toast.makeText(this, "Gallery permission denied permanently. Enable it in settings.", Toast.LENGTH_LONG).show();
                }
            }
        }
    }


    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.e(TAG, "Error creating image file: " + ex.getMessage());
                Toast.makeText(this, "Error creating image file", Toast.LENGTH_SHORT).show();
            }

            if (photoFile != null) {
                try {
                    photoURI = FileProvider.getUriForFile(
                            this,
                            "com.school.schoolmanagement.fileprovider",
                            photoFile
                    );
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    cameraLauncher.launch(cameraIntent);
                } catch (Exception e) {
                    Log.e(TAG, "Error with FileProvider: " + e.getMessage(), e);
                    Toast.makeText(this, "Error launching camera: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Toast.makeText(this, "No camera app available", Toast.LENGTH_SHORT).show();
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(galleryIntent);
    }

    private Bitmap compressImage(Bitmap original) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        original.compress(Bitmap.CompressFormat.JPEG, 80, out);

        int compressQuality = 80;
        while (out.toByteArray().length > MAX_IMAGE_SIZE_KB * 1024 && compressQuality > 10) {
            out.reset();
            compressQuality -= 10;
            original.compress(Bitmap.CompressFormat.JPEG, compressQuality, out);
        }

        byte[] compressedData = out.toByteArray();
        return BitmapFactory.decodeByteArray(compressedData, 0, compressedData.length);
    }

    private void setupToolbar() {
        binding.toolbar.backBtn.setOnClickListener(v -> onBackPressed());
    }

    private void setupDatePickers() {
        // Set up date picker for admission date
        binding.edtDateOfAdmission.setFocusable(false);
        binding.edtDateOfAdmission.setClickable(true);
        binding.edtDateOfAdmission.setOnClickListener(v -> showDatePickerDialog(binding.edtDateOfAdmission));

        // Set up date picker for date of birth
        binding.edtDateOfBirth.setFocusable(false);
        binding.edtDateOfBirth.setClickable(true);
        binding.edtDateOfBirth.setOnClickListener(v -> showDatePickerDialog(binding.edtDateOfBirth));
    }

    private void showDatePickerDialog(android.widget.EditText dateField) {
        Calendar calendar = Calendar.getInstance();

        // Set initial date to today or from an existing date value
        if (!TextUtils.isEmpty(dateField.getText())) {
            try {
                Date date = dateFormat.parse(dateField.getText().toString());
                if (date != null) {
                    calendar.setTime(date);
                }
            } catch (ParseException e) {
                // Use current date if parsing fails
                Log.e(TAG, "Error parsing date", e);
            }
        }

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                com.google.android.material.R.style.ThemeOverlay_Material3_MaterialCalendar,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.set(selectedYear, selectedMonth, selectedDay);
                    String formattedDate = dateFormat.format(selectedDate.getTime());
                    dateField.setText(formattedDate);
                },
                year,
                month,
                day
        );

        // If this is the date of birth field, set a reasonable range
        if (dateField == binding.edtDateOfBirth) {
            // Limit date selection to past dates (for birthdate)
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        }

        datePickerDialog.show();
    }

    private void setupClassSelectionDialog() {
        binding.edtSelectClass.setFocusable(false);
        binding.edtSelectClass.setClickable(true);
        binding.edtSelectClass.setOnClickListener(v -> {
            if (classList == null || classList.isEmpty()) {
                showToast("No classes available. Please try again later.");
                // Try to fetch classes again
                fetchClassData();
                return;
            }

            showClassSelectionDialog();
        });
    }

    private void showClassSelectionDialog() {
        // Create a list of class names for the dialog
        ArrayList<String> classNames = new ArrayList<>();
        classNameToIdMap.clear();

        for (ClassModel.Data classData : classList) {
            classNames.add(classData.getClassName());
            classNameToIdMap.put(classData.getClassName(), classData.getClassId());
        }

        // Create dialog
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                com.google.android.material.R.layout.support_simple_spinner_dropdown_item,
                classNames
        );

        new MaterialAlertDialogBuilder(this)
                .setTitle("Select Class")
                .setAdapter(adapter, (dialog, position) -> {
                    String selectedClassName = classNames.get(position);
                    binding.edtSelectClass.setText(selectedClassName);

                    // Store the selected class ID
                    selectedClassId = classNameToIdMap.get(selectedClassName);
                    Log.d(TAG, "Selected class ID: " + selectedClassId);
                })
                .show();
    }

    private void fetchClassData() {
        showLoading(true);
        String auth = "Bearer " + pref.getPrefString(this, pref.user_token);
        viewModel.getAllClasses(auth).observe(this, response -> {
            showLoading(false);
            if (response != null && response.data != null && response.data.getData() != null) {
                classList.clear();
                classList.addAll(response.data.getData());
                Log.d(TAG, "Fetched " + classList.size() + " classes");
            } else {
                showToast("No classes found.");
            }
        });
    }

    private void showLoading(boolean isLoading) {
        Log.d(TAG, "Loading: " + isLoading);
        binding.loader.rlLoader.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }

    private void showToast(String message) {
        Log.d(TAG, "Toast: " + message);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private boolean validateForm() {
        boolean isValid = true;

        // Student Information Section - Required Fields
        if (!validateRequiredField(binding.edtStudentName, "Student name is required")) {
            isValid = false;
        }

        if (!validateEmail()) {
            isValid = false;
        }

        if (!validateRequiredField(binding.edtRegistrationNumber, "Registration number is required")) {
            isValid = false;
        }

        if (!validateDateField(binding.edtDateOfAdmission, "Valid admission date is required")) {
            isValid = false;
        }

        if (!validateRequiredField(binding.edtSelectClass, "Class selection is required")) {
            isValid = false;
        }

        if (selectedClassId == -1) {
            binding.edtSelectClass.setError("Please select a valid class");
            isValid = false;
        }

        if (!validateDiscountFees()) {
            isValid = false;
        }

        // Optional Fields - Student
        if (!TextUtils.isEmpty(binding.edtMobileNumber.getText())) {
            if (!validateMobileNumber(binding.edtMobileNumber)) {
                isValid = false;
            }
        }

        if (!TextUtils.isEmpty(binding.edtDateOfBirth.getText())) {
            if (!validateDateField(binding.edtDateOfBirth, "Please enter a valid date of birth")) {
                isValid = false;
            }
        }

        if (!TextUtils.isEmpty(binding.edtTotalSibbling.getText())) {
            if (!validateNumericField(binding.edtTotalSibbling, "Please enter a valid number for siblings")) {
                isValid = false;
            }
        }

        // Father/Guardian Information - All Optional
        if (!TextUtils.isEmpty(binding.edtFatherMobile.getText())) {
            if (!validateMobileNumber(binding.edtFatherMobile)) {
                isValid = false;
            }
        }

        if (!TextUtils.isEmpty(binding.edtFatherNid.getText())) {
            if (!validateNationalID(binding.edtFatherNid, "Please enter a valid National ID")) {
                isValid = false;
            }
        }

        if (!TextUtils.isEmpty(binding.edtFatherIncome.getText())) {
            if (!validateIncomeField(binding.edtFatherIncome, "Please enter a valid income amount")) {
                isValid = false;
            }
        }

        // Mother Information - All Optional
        if (!TextUtils.isEmpty(binding.edtMotherMobile.getText())) {
            if (!validateMobileNumber(binding.edtMotherMobile)) {
                isValid = false;
            }
        }

        if (!TextUtils.isEmpty(binding.edtMotherNid.getText())) {
            if (!validateNationalID(binding.edtMotherNid, "Please enter a valid National ID")) {
                isValid = false;
            }
        }

        if (!TextUtils.isEmpty(binding.edtMotherIncome.getText())) {
            if (!validateIncomeField(binding.edtMotherIncome, "Please enter a valid income amount")) {
                isValid = false;
            }
        }

        // Check that hint values are properly set
        validateProperHintText();

        return isValid;
    }

    private boolean validateRequiredField(android.widget.EditText field, String errorMessage) {
        if (TextUtils.isEmpty(field.getText())) {
            field.setError(errorMessage);
            field.requestFocus();
            return false;
        }
        return true;
    }

    private boolean validateEmail() {
        String email = binding.edtEmail.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            binding.edtEmail.setError("Email is required");
            binding.edtEmail.requestFocus();
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.edtEmail.setError("Please enter a valid email address");
            binding.edtEmail.requestFocus();
            return false;
        }
        return true;
    }

    private boolean validateDateField(android.widget.EditText dateField, String errorMessage) {
        String dateStr = dateField.getText().toString().trim();
        if (TextUtils.isEmpty(dateStr)) {
            dateField.setError(errorMessage);
            dateField.requestFocus();
            return false;
        }

        try {
            // Attempt to parse the date
            Date date = dateFormat.parse(dateStr);

            // Check if the date is in the future if it's admission date
            if (dateField == binding.edtDateOfAdmission) {
                Date currentDate = new Date();
                if (date != null && date.after(currentDate)) {
                    dateField.setError("Admission date cannot be in the future");
                    return false;
                }
            }

            // Check if the birth date is reasonable
            if (dateField == binding.edtDateOfBirth) {
                Date currentDate = new Date();
                if (date != null && date.after(currentDate)) {
                    dateField.setError("Birth date cannot be in the future");
                    return false;
                }
            }

            return true;
        } catch (ParseException e) {
            dateField.setError("Please use format YYYY-MM-DD");
            dateField.requestFocus();
            return false;
        }
    }

    private boolean validateDiscountFees() {
        String discountStr = binding.edtDiscountFees.getText().toString().trim();
        if (TextUtils.isEmpty(discountStr)) {
            binding.edtDiscountFees.setError("Discount value is required");
            binding.edtDiscountFees.requestFocus();
            return false;
        }

        try {
            int discount = Integer.parseInt(discountStr);
            if (discount < 0 || discount > 100) {
                binding.edtDiscountFees.setError("Discount must be between 0-100%");
                binding.edtDiscountFees.requestFocus();
                return false;
            }
            return true;
        } catch (NumberFormatException e) {
            binding.edtDiscountFees.setError("Please enter a valid number");
            binding.edtDiscountFees.requestFocus();
            return false;
        }
    }

    private boolean validateMobileNumber(android.widget.EditText field) {
        String mobile = field.getText().toString().trim();
        // Customize this pattern based on your country's phone number format
        String phonePattern = "^[0-9]{10,12}$";

        if (!Pattern.matches(phonePattern, mobile)) {
            field.setError("Please enter a valid mobile number");
            field.requestFocus();
            return false;
        }
        return true;
    }

    private boolean validateNumericField(android.widget.EditText field, String errorMessage) {
        String value = field.getText().toString().trim();
        try {
            int numValue = Integer.parseInt(value);
            if (numValue < 0) {
                field.setError("Value cannot be negative");
                field.requestFocus();
                return false;
            }
            return true;
        } catch (NumberFormatException e) {
            field.setError(errorMessage);
            field.requestFocus();
            return false;
        }
    }

    private boolean validateNationalID(android.widget.EditText field, String errorMessage) {
        String idNumber = field.getText().toString().trim();
        // Adjust the pattern to match your country's National ID format
        // This example assumes a numeric ID between 8 and 15 digits
        String idPattern = "^[0-9]{8,15}$";

        if (!Pattern.matches(idPattern, idNumber)) {
            field.setError(errorMessage);
            field.requestFocus();
            return false;
        }
        return true;
    }

    private boolean validateIncomeField(android.widget.EditText field, String errorMessage) {
        String income = field.getText().toString().trim();
        try {
            // Parse as double to allow decimal values
            double incomeValue = Double.parseDouble(income);
            if (incomeValue < 0) {
                field.setError("Income cannot be negative");
                field.requestFocus();
                return false;
            }
            return true;
        } catch (NumberFormatException e) {
            field.setError(errorMessage);
            field.requestFocus();
            return false;
        }
    }

    /**
     * Check that hint text is properly set in the mother section
     * (XML shows some incorrect values)
     */
    private void validateProperHintText() {
        // Check and fix mother name hint
        if (binding.edtMotherName.getHint().toString().equals("Father Name")) {
            binding.edtMotherName.setHint("Mother Name");
        }

        // Check and fix mother National ID label
        if (binding.tvMotherNid.getText().toString().contains("Father")) {
            binding.tvMotherNid.setText("Mother National ID (Optional)*");
        }

        // Check and fix mother income hint
        if (binding.edtMotherIncome.getHint().toString().equals("Profession")) {
            binding.edtMotherIncome.setHint("Income");
        }

        // Check and fix father income hint
        if (binding.edtFatherIncome.getHint().toString().equals("Profession")) {
            binding.edtFatherIncome.setHint("Income");
        }
    }

    private void submitForm() {
        String auth = "Bearer " + pref.getPrefString(this, pref.user_token);
        StudentFields studentFields = new StudentFields();

        // Initialize Parent object
        StudentFields.Parent parent = new StudentFields.Parent();

        // Student Basic Information
        studentFields.setStudentName(binding.edtStudentName.getText().toString().trim());
        studentFields.setMail(binding.edtEmail.getText().toString().trim());
        studentFields.setLastRegistrationNumber(binding.edtRegistrationNumber.getText().toString().trim());
        studentFields.setDateOfAdmission(binding.edtDateOfAdmission.getText().toString().trim());
        studentFields.setClassId(selectedClassId);

        // Extract discount fee first as this is required
        try {
            double discount = Double.parseDouble(binding.edtDiscountFees.getText().toString().trim());
            studentFields.setDiscountInFees(discount);
        } catch (NumberFormatException e) {
            Log.e(TAG, "Error parsing discount fees", e);
            studentFields.setDiscountInFees(0); // Default to 0 if parsing fails
        }

        // Extra Student Information
        studentFields.setMobileNumber(binding.edtMobileNumber.getText().toString().trim());
        studentFields.setDateOfBirth(binding.edtDateOfBirth.getText().toString().trim());
        studentFields.setAddress(binding.edtAddress.getText().toString().trim());
        studentFields.setCast(binding.edtCast.getText().toString().trim());

        // Optional fields from the JSON format that weren't in your form
        studentFields.setDiseaseIfAny(binding.edtDisease.getText().toString().trim()); // Default value or get from form if you add this field
        studentFields.setBirthFormIdOrNic(binding.edtBirthId.getText().toString().trim()); // Default value or get from form if you add this field
        studentFields.setPreviousSchool(binding.edtPreviousSchool.getText().toString()); // Default value or get from form if you add this field
        studentFields.setPreviousIdOrBoardRollNumber(binding.edtPreviousIdOrBoardRollNo.getText().toString()); // Default value or get from form if you add this field
        studentFields.setFamily(binding.spinnerSelectFamily.getText().toString().trim()); // Default value or get from form if you add this field

        // Gender
        int genderPosition = binding.spinnerGender.getSelectedItemPosition();
        if (genderPosition > 0) { // Skip "Select Gender" option
            studentFields.setGender(binding.spinnerGender.getSelectedItem().toString());
        }

        // Blood Group
        int bloodGroupPosition = binding.edtBloodGroup.getSelectedItemPosition();
        if (bloodGroupPosition > 0) { // Skip "Select Blood Group" option
            studentFields.setBloodGroup(binding.edtBloodGroup.getSelectedItem().toString());
        }

        // Religion - from spinner
        int religionPosition = binding.spinnerReligion.getSelectedItemPosition();
        if (religionPosition > 0) {
            // Set religion if needed in your model
            // studentFields.setReligion(binding.spinnerReligion.getSelectedItem().toString());
        }

        // Orphan status (stored as 'osc' - Orphan Status Code)
        int orphanPosition = binding.spinnerOrphan.getSelectedItemPosition();
        // Assuming "No" is at index 0 and "Yes" is at index 1
        studentFields.setOsc(orphanPosition == 1 ? "Yes" : "No");

        // Total siblings
        if (!TextUtils.isEmpty(binding.edtTotalSibbling.getText())) {
            try {
                int siblings = Integer.parseInt(binding.edtTotalSibbling.getText().toString().trim());
                studentFields.setTotalSibling(siblings);
            } catch (NumberFormatException e) {
                Log.e(TAG, "Error parsing total siblings", e);
            }
        }

        // Father Information
        parent.setFatherName(binding.edtFatherName.getText().toString().trim());
        parent.setFatherMobileNumber(binding.edtFatherMobile.getText().toString().trim());
        parent.setFatherNationalId(binding.edtFatherNid.getText().toString().trim());
        parent.setFatherProfession(binding.edtFatherProfession.getText().toString().trim());

        // Education field is missing in your form but present in JSON
        parent.setFatherEducation(""); // Set default or add to your form

        // Parent Email - use the student's email as parent email if needed
        String parentEmail = binding.edtEmailParents.getText().toString().trim();
        if (TextUtils.isEmpty(parentEmail)) {
            // Use student email as fallback if parent email is empty
            parentEmail = binding.edtEmail.getText().toString().trim();
        }
        parent.setParentEmail(parentEmail);

        // Father Income
        if (!TextUtils.isEmpty(binding.edtFatherIncome.getText())) {
            try {
                int fatherIncome = Integer.parseInt(binding.edtFatherIncome.getText().toString().trim());
                parent.setFatherIncome(fatherIncome);
            } catch (NumberFormatException e) {
                Log.e(TAG, "Error parsing father income", e);
            }
        }

        // Mother Information
        parent.setMotherName(binding.edtMotherName.getText().toString().trim());
        parent.setMotherMobileNumber(binding.edtMotherMobile.getText().toString().trim());
        parent.setMotherNationalId(binding.edtMotherNid.getText().toString().trim());
        parent.setMotherProfession(binding.edtMotherProfession.getText().toString().trim());

        // Education field is missing in your form but present in JSON
        parent.setMotherEducation(""); // Set default or add to your form

        // The JSON uses motherOccupation but your code uses motherProfession
        parent.setMotherOccupation(binding.edtMotherProfession.getText().toString().trim());

        // Mother Income
        if (!TextUtils.isEmpty(binding.edtMotherIncome.getText())) {
            try {
                int motherIncome = Integer.parseInt(binding.edtMotherIncome.getText().toString().trim());
                parent.setMotherIncome(motherIncome);
            } catch (NumberFormatException e) {
                Log.e(TAG, "Error parsing mother income", e);
            }
        }

        // Assign parent object to studentFields
        studentFields.setParent(parent);

        // Additional notes
        studentFields.setNote(binding.edtAnyAdditionalNote.getText().toString().trim());

        // Set profile image if available
        if (profileImageData != null) {
            studentFields.setProfileImageData(profileImageData);
        }

        // Show loading indicator
        showLoading(true);

        // Log the student data for debugging
        Gson gson= new Gson();
        Log.d(TAG, "Submitting student data: " + gson.toJson(studentFields));

        // Submit the data to the API
        viewModel.createStudents(auth, studentFields).observe(this, response -> {
            showLoading(false);

            if (response != null && response.isSuccess) {
                Toast.makeText(this, "Student added successfully", Toast.LENGTH_SHORT).show();
                // Clear the form or navigate back
                finish();
            } else {
                String errorMessage = (response != null && response.message != null)
                        ? response.message
                        : "Failed to add student. Please try again.";

                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
                Log.d(TAG, "Error in submitForm: " + errorMessage);
            }
        });
    }    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null; // Avoid memory leaks
    }
}