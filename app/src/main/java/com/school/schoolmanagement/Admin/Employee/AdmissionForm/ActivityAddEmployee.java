package com.school.schoolmanagement.Admin.Employee.AdmissionForm;


import static android.util.Base64.DEFAULT;

import android.media.session.MediaSessionManager;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.Gson;
import com.school.schoolmanagement.Admin.Model.Employee2;
import com.school.schoolmanagement.GlobalViewModel.ViewModel;
import com.school.schoolmanagement.Model.Employee;
import com.school.schoolmanagement.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import com.school.schoolmanagement.R;
import com.school.schoolmanagement.Utils.Utility;
import com.school.schoolmanagement.databinding.ActivityAddEmployeeBinding;
import com.school.schoolmanagement.databinding.ActivityAllEmployeeBinding;

public class ActivityAddEmployee extends Utility {
    ActivityAddEmployeeBinding binding;
    Employee2 employee2Response;
    Employee employeeData;

    private static final String TAG = "ActivityAddEmployee";

    // ViewModel
    private ViewModel employeeViewModel;

    // Image handling variables

    int employeeId;
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
        binding = ActivityAddEmployeeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize ViewModel
        employeeViewModel = new ViewModelProvider(this).get(ViewModel.class);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize default objects
        employeeData = new Employee();

        // Get and log employee ID
        employeeId = getIntent().getIntExtra("EMPLOYEE_ID", 0);
        FLAG= getIntent().getStringExtra("FLAG");
        Log.d(TAG, "onCreate: Employee ID received: " + employeeId);

        // Update toolbar title based on mode
        if (employeeId != 0) {
            Log.d(TAG, "Edit mode activated for employee ID: " + employeeId);
            getEmployeeDetails();
        } else {
            Log.d(TAG, "Add new employee mode");
        }

        // Initialize Activity Result Launchers
        setupActivityResultLaunchers();

        // Setup toolbar
        setupToolbar();

        // Setup date pickers
        setupDatePickers();

        // Setup Spinners
        setupSpinners();

        // Setup Employee Role Dropdown
        setupEmployeeRoleDropdown();

        // Setup Buttons
        setupButtons();
    }

    // Change 1: Fix the getEmployeeDetails() method to handle response correctly
    private void getEmployeeDetails() {
        if (employeeId != 0) {
            Log.d(TAG, "getEmployeeDetails: Starting retrieval for employee ID: " + employeeId);
            // Show loading indicator
            showLoading(true);

            try {
                // Get auth token
                String authToken = "Bearer " + pref.getPrefString(this, pref.user_token);

                // Call the ViewModel method to get employee details
                employeeViewModel.getEmployee(authToken, employeeId).observe(this, response -> {
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

    // Change 2: Fix the fillEmployeeData() method to handle Employee2 structure correctly
    private void fillEmployeeData(Employee2 employee) {
        if (employee == null) {
            Log.e("EMP_DEBUG", "Received null employee object");
            Toast.makeText(this, "Failed to load employee data", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            Log.d("EMP_DEBUG", "Processing employee data");

            Employee2.Data employeeData = employee.getData();

            if (employeeData == null) {
                Log.e("EMP_DEBUG", "Null data object in employee");
                Toast.makeText(this, "Missing employee data", Toast.LENGTH_SHORT).show();
                return;
            }

            // Log full employee data as JSON
            Log.d("EMP_DEBUG", "Full employee data: " + new Gson().toJson(employeeData));

            if (employeeData.getName() != null) {
                binding.edtEmployeeName.setText(employeeData.getName());
            }

            if (employeeData.getMobileNumber() != null) {
                binding.edtMobileNumber.setText(employeeData.getMobileNumber());
            }

            if (employeeData.getDateOfJoining() != null) {
                binding.edtDateOfJoining.setText(employeeData.getDateOfJoining());
            }

            if (employeeData.getEmployeeRole() != null) {
                binding.edtEmployeeRole.setText(employeeData.getEmployeeRole());
            }

            binding.edtMonthlySalary.setText(String.valueOf(employeeData.getMonthlySalary()));

            if (employeeData.getFatherOrHusbandName() != null) {
                binding.edtFatherHusbandName.setText(employeeData.getFatherOrHusbandName());
            }

            if (employeeData.getNationalId() != null) {
                binding.edtNid.setText(employeeData.getNationalId());
            }

            if (employeeData.getEducation() != null) {
                binding.edtEducation.setText(employeeData.getEducation());
            }

            if (employeeData.getExperience() != null && !employeeData.getExperience().isEmpty()) {
                binding.edtExperience.setText(employeeData.getExperience());
            }

            if (employeeData.getEmailAddress() != null) {
                binding.edtEmail.setText(employeeData.getEmailAddress());
            }

            if (employeeData.getDob() != null) {
                binding.edtDob.setText(employeeData.getDob());
            }

            if (employeeData.getHomeAddress() != null) {
                binding.edtHomeAddress.setText(employeeData.getHomeAddress());
            }

            // Spinner values
            if (employeeData.getGender() != null && !employeeData.getGender().isEmpty()) {
                setSpinnerSelection(binding.spinnerGender, employeeData.getGender().trim());
            }

            if (employeeData.getReligion() != null && !employeeData.getReligion().isEmpty()) {
                setSpinnerSelection(binding.spinnerReligion, employeeData.getReligion().trim());
            }

            if (employeeData.getBloodGroup() != null && !employeeData.getBloodGroup().isEmpty()) {
                setSpinnerSelection(binding.spinnerBloodGroup, employeeData.getBloodGroup().trim());
            }

            // Handle Base64 profile image
            if (employeeData.getProfilePicture() != null && !employeeData.getProfilePicture().isEmpty()) {
                try {
                    byte[] imageData = Base64.decode(employeeData.getProfilePicture(), Base64.DEFAULT);

                    if (imageData != null && imageData.length > 0) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
                        if (bitmap != null) {
                            binding.imgGallery.setImageBitmap(bitmap);
                            profileImageData = imageData;
                        } else {
                            Log.e("EMP_DEBUG", "Bitmap decode failed");
                        }
                    } else {
                        Log.e("EMP_DEBUG", "Image data is empty or null after Base64 decode");
                    }
                } catch (Exception e) {
                    Log.e("EMP_DEBUG", "Error decoding profile picture: " + e.getMessage(), e);
                }
            }

            // Update submit button
            binding.submitTxt.setText("Update");

            // Convert and store for update use
            Employee convertedEmployee = convertEmployee2ToEmployee(employee);
            // Save this somewhere if needed for later update

        } catch (Exception e) {
            Log.e("EMP_DEBUG", "Exception in fillEmployeeData: " + e.getMessage(), e);
            Toast.makeText(this, "Error processing employee data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    // Change 3: Fix the convertEmployee2ToEmployee() method
    private Employee convertEmployee2ToEmployee(Employee2 employee2) {
        Employee employee = new Employee();

        try {
            // Get the data object from Employee2
            Employee2.Data data = employee2.getData();

            if (data == null) {
                Log.e("EMP_DEBUG", "No data object in Employee2");
                return employee;
            }

            // Copy all relevant fields from Employee2.Data to Employee
            employee.setId(data.getEmployeeId());

            // Handle experience properly
            if (data.getExperience() != null && !data.getExperience().isEmpty()) {
                try {
                    employee.setExperience(Integer.parseInt(data.getExperience()));
                } catch (NumberFormatException e) {
                    Log.e("EMP_DEBUG", "Invalid experience format: " + data.getExperience(), e);
                    employee.setExperience(0);
                }
            }

            if (data.getName() != null)
                employee.setName(data.getName());

            if (data.getMobileNumber() != null)
                employee.setMobileNumber(data.getMobileNumber());

            if (data.getDateOfJoining() != null)
                employee.setDateOfJoining(data.getDateOfJoining());

            if (data.getEmployeeRole() != null)
                employee.setEmployeeRole(data.getEmployeeRole());

            employee.setMonthlySalary(data.getMonthlySalary());

            if (data.getFatherOrHusbandName() != null)
                employee.setFatherName(data.getFatherOrHusbandName());

            if (data.getNationalId() != null)
                employee.setNationalId(data.getNationalId());

            if (data.getEducation() != null)
                employee.setEducation(data.getEducation());

            if (data.getGender() != null)
                employee.setGender(data.getGender());

            if (data.getReligion() != null)
                employee.setReligion(data.getReligion());

            if (data.getBloodGroup() != null)
                employee.setBloodGroup(data.getBloodGroup());

            if (data.getEmailAddress() != null)
                employee.setEmail(data.getEmailAddress());

            if (data.getDob() != null)
                employee.setDob(data.getDob());

            if (data.getHomeAddress() != null)
                employee.setHomeAddress(data.getHomeAddress());

            // Handle profile image data if it exists
            if (profileImageData != null) {
                employee.setProfileImageData(profileImageData);
            } else if (data.getProfilePicture() != null) {
                try {
                    byte[] imageData = Base64.decode(data.getProfilePicture(), DEFAULT);
                    employee.setProfileImageData(imageData);
                } catch (Exception e) {
                    Log.e("EMP_DEBUG", "Error decoding profile picture: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            Log.e("EMP_DEBUG", "Error converting Employee2 to Employee: " + e.getMessage(), e);
        }

        return employee;
    }


    private void setSpinnerSelection(Spinner spinner, String value) {
        if (value == null || value.isEmpty()) {
            Log.d("EMP_DEBUG", "Empty value for spinner, not setting selection");
            return;
        }

        try {
            for (int i = 0; i < spinner.getCount(); i++) {
                if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(value)) {
                    spinner.setSelection(i);
                    Log.d("EMP_DEBUG", "Spinner selection set for value: " + value);
                    return;
                }
            }
            Log.d("EMP_DEBUG", "Value not found in spinner: " + value);
        } catch (Exception e) {
            Log.e("EMP_DEBUG", "Error setting spinner selection: " + e.getMessage());
        }
    }

    private void showLoading(boolean isLoading) {
        if (isLoading) {
            binding.loader.rlLoader.setVisibility(View.VISIBLE);
        } else {
            binding.loader.rlLoader.setVisibility(View.GONE);
        }
    }



    private void setupToolbar() {
        binding.toolbar.backBtn.setOnClickListener(v -> onBackPressed());
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

    private void setupDatePickers() {
        // Disable keyboard input for date fields
        binding.edtDateOfJoining.setInputType(InputType.TYPE_NULL);
        binding.edtDob.setInputType(InputType.TYPE_NULL);

        Calendar calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener dateOfJoiningListener = (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            binding.edtDateOfJoining.setText(dateFormat.format(calendar.getTime()));
        };

        DatePickerDialog.OnDateSetListener dobListener = (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            binding.edtDob.setText(dateFormat.format(calendar.getTime()));
        };

        binding.edtDateOfJoining.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    dateOfJoiningListener,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        });

        binding.edtDob.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    dobListener,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        });
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

        // Blood Group Spinner
        String[] bloodGroupOptions = {"Select Blood Group", "A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
        ArrayAdapter<String> bloodGroupAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                bloodGroupOptions
        );
        bloodGroupAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerBloodGroup.setAdapter(bloodGroupAdapter);
    }

    private void setupEmployeeRoleDropdown() {
        // Set up the AutoCompleteTextView for employee role
        String[] roleOptions = {"Teacher", "Administrator", "Accountant", "Librarian", "Clerk", "Peon", "Security Guard", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                roleOptions
        );
        binding.edtEmployeeRole.setAdapter(adapter);
        binding.edtEmployeeRole.setThreshold(1);  // Start showing dropdown after first character
        binding.edtEmployeeRole.setInputType(InputType.TYPE_NULL);  // Optional: Disable keyboard

        // Make the entire RelativeLayout clickable to show dropdown
        binding.employeeRoleRel.setOnClickListener(v -> {
            binding.edtEmployeeRole.showDropDown();
        });

        // Also make the EditText itself show dropdown when clicked
        binding.edtEmployeeRole.setOnClickListener(v -> {
            binding.edtEmployeeRole.showDropDown();
        });
    }

    private void setupButtons() {
        // Change Logo/Photo Button
        binding.btnChangeLogo.setOnClickListener(v -> showImageSourceDialog());

        // Reset Button
        binding.resetBtn.setOnClickListener(v -> resetForm());

        // Submit Button
        binding.submitBtn.setOnClickListener(v -> {
            if (validateInputs()) {
                submitEmployeeData();
            }
        });
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
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestGalleryPermission() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                GALLERY_PERMISSION_CODE
        );
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
                Toast.makeText(this, "Gallery permission denied", Toast.LENGTH_SHORT).show();
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

    private boolean validateInputs() {
        boolean isValid = true;

        // Required fields
        String employeeName = binding.edtEmployeeName.getText().toString().trim();
        String dateOfJoining = binding.edtDateOfJoining.getText().toString().trim();
        String employeeRole = binding.edtEmployeeRole.getText().toString().trim();
        String monthlySalary = binding.edtMonthlySalary.getText().toString().trim();

        // Validate required fields
        if (TextUtils.isEmpty(employeeName)) {
            binding.edtEmployeeName.setError("Employee name is required");
            isValid = false;
        }

        if (TextUtils.isEmpty(dateOfJoining)) {
            binding.edtDateOfJoining.setError("Date of joining is required");
            isValid = false;
        }

        if (TextUtils.isEmpty(employeeRole)) {
            binding.edtEmployeeRole.setError("Employee role is required");
            isValid = false;
        }

        if (TextUtils.isEmpty(monthlySalary)) {
            binding.edtMonthlySalary.setError("Monthly salary is required");
            isValid = false;
        }

        // Validate mobile number format
        String mobileNumber = binding.edtMobileNumber.getText().toString().trim();
        if (!TextUtils.isEmpty(mobileNumber) && !mobileNumber.matches("\\d{10,15}")) {
            binding.edtMobileNumber.setError("Please enter a valid mobile number");
            isValid = false;
        }

        // Validate email format if provided
        String email = binding.edtEmail.getText().toString().trim();
        if (!TextUtils.isEmpty(email) && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.edtEmail.setError("Please enter a valid email address");
            isValid = false;
        }

        // Validate NID format if provided
        String nid = binding.edtNid.getText().toString().trim();
        if (!TextUtils.isEmpty(nid) && !nid.matches("\\d{5,20}")) {
            binding.edtNid.setError("Please enter a valid National ID");
            isValid = false;
        }

        return isValid;
    }

    private void resetForm() {
        // Reset all input fields
        binding.edtEmployeeName.setText("");
        binding.edtMobileNumber.setText("");
        binding.edtDateOfJoining.setText("");
        binding.edtEmployeeRole.setText("");
        binding.edtMonthlySalary.setText("");
        binding.edtFatherHusbandName.setText("");
        binding.edtNid.setText("");
        binding.edtEducation.setText("");
        binding.edtExperience.setText("");
        binding.edtEmail.setText("");
        binding.edtDob.setText("");
        binding.edtHomeAddress.setText("");

        // Reset spinners
        binding.spinnerGender.setSelection(0);
        binding.spinnerReligion.setSelection(0);
        binding.spinnerBloodGroup.setSelection(0);

        // Reset profile image
        binding.imgGallery.setImageResource(R.drawable.gallery);
        profileImageData = null;

        Toast.makeText(this, "Form has been reset", Toast.LENGTH_SHORT).show();
    }

    private void submitEmployeeData() {
        // Create Employee object with form data
        Employee employee = new Employee();
        employee.setName(binding.edtEmployeeName.getText().toString().trim());
        employee.setMobileNumber(binding.edtMobileNumber.getText().toString().trim());
        employee.setDateOfJoining(binding.edtDateOfJoining.getText().toString().trim());
        employee.setEmployeeRole(binding.edtEmployeeRole.getText().toString().trim());
        employee.setMonthlySalary(Double.parseDouble(binding.edtMonthlySalary.getText().toString().trim()));

        // Optional fields
        String fatherHusbandName = binding.edtFatherHusbandName.getText().toString().trim();
        if (!TextUtils.isEmpty(fatherHusbandName)) {
            employee.setFatherName(fatherHusbandName);
        }

        String nationalId = binding.edtNid.getText().toString().trim();
        if (!TextUtils.isEmpty(nationalId)) {
            employee.setNationalId(nationalId);
        }

        String education = binding.edtEducation.getText().toString().trim();
        if (!TextUtils.isEmpty(education)) {
            employee.setEducation(education);
        }

        // Get spinner values
        if (binding.spinnerGender.getSelectedItemPosition() > 0) {
            employee.setGender(binding.spinnerGender.getSelectedItem().toString());
        }

        if (binding.spinnerReligion.getSelectedItemPosition() > 0) {
            employee.setReligion(binding.spinnerReligion.getSelectedItem().toString());
        }

        if (binding.spinnerBloodGroup.getSelectedItemPosition() > 0) {
            employee.setBloodGroup(binding.spinnerBloodGroup.getSelectedItem().toString());
        }

        String experience = binding.edtExperience.getText().toString().trim();
        if (!TextUtils.isEmpty(experience)) {
            employee.setExperience(Integer.valueOf(experience));
        }

        String email = binding.edtEmail.getText().toString().trim();
        if (!TextUtils.isEmpty(email)) {
            employee.setEmail(email);
        }

        String dob = binding.edtDob.getText().toString().trim();
        if (!TextUtils.isEmpty(dob)) {
            employee.setDob(dob);
        }

        String homeAddress = binding.edtHomeAddress.getText().toString().trim();
        if (!TextUtils.isEmpty(homeAddress)) {
            employee.setHomeAddress(homeAddress);
        }

        // Profile image data
        if (profileImageData != null) {
            employee.setProfileImageData(profileImageData);
        }

        if("Edit".equals(FLAG)){
            String authToken = "Bearer " + pref.getPrefString(this,pref.user_token);
//            employeeViewModel.updateEmployee(authToken, employeeId,employee).observe(this, response -> {
//                showLoading(false);
//
//                if (response == null) {
//                    Toast.makeText(this, "Network error - null response", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                if (response.isSuccess) {
//                    Toast.makeText(this, "Employee updated successfully", Toast.LENGTH_SHORT).show();
//                    finish(); // Or navigate as needed
//                } else {
//                    String errorMessage = response.message != null ? response.message : "Failed to update employee";
//                    Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
//                    Log.d(TAG, "submitEmployeeData: "+errorMessage);
//                }
//            });
        }else{
            // Call API through ViewModel
            String authToken = "Bearer " + pref.getPrefString(this,pref.user_token);
            employeeViewModel.createEmployee(authToken, employee).observe(this, response -> {
                if (response.isSuccess && response.data != null) {
                    Toast.makeText(this, "Employee created successfully!", Toast.LENGTH_SHORT).show();
                    // Navigate back or to employee details
                    finish();
                } else {
                    String errorMessage = response.message != null ? response.message : "Failed to create employee";
                    Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "submitEmployeeData: "+errorMessage);
                }
            });
        }


    }
}