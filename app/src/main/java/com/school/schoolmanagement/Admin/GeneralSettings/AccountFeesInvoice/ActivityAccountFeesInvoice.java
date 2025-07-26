package com.school.schoolmanagement.Admin.GeneralSettings.AccountFeesInvoice;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.school.schoolmanagement.Adapters.BankAccountAdapter;
import com.school.schoolmanagement.GlobalViewModel.ViewModel;
import com.school.schoolmanagement.Model.BankAccountFields;
import com.school.schoolmanagement.Model.EmployeeResponse;
import com.school.schoolmanagement.R;
import com.school.schoolmanagement.Utils.Utility;
import com.school.schoolmanagement.databinding.ActivityAccountFeesIinvoiceBinding;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ActivityAccountFeesInvoice extends Utility {
    private ActivityAccountFeesIinvoiceBinding binding;
    private static final int PERMISSION_REQUEST_CODE = 100;
    private Uri selectedImageUri;
    private Bitmap selectedImageBitmap;
    private List<BankAccount> bankAccountList;
    private BankAccountAdapter bankAccountAdapter;
    private boolean isEditMode = false;
    private int editPosition = -1;
    private boolean isLoading = false;

    // ViewModel
    private ViewModel viewModel;
    private String authToken;

    // Activity result launchers
    private ActivityResultLauncher<Intent> galleryLauncher;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<String> permissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityAccountFeesIinvoiceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(ViewModel.class);

        // Get auth token from SharedPreferences or Intent
        authToken = getAuthToken();

        initializeViews();
        setupActivityResultLaunchers();
        setupClickListeners();
        setupValidations();
        setupRecyclerView();
        loadBankAccounts(); // This will now actually load data
    }

    private String getAuthToken() {
        return "Bearer " + pref.getPrefString(this, pref.user_token);
    }

    private void initializeViews() {
        bankAccountList = new ArrayList<>();
    }

    private void setupActivityResultLaunchers() {
        // Gallery launcher
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        if (selectedImageUri != null) {
                            loadImageFromUri(selectedImageUri);
                        }
                    }
                }
        );

        // Camera launcher
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Bundle extras = result.getData().getExtras();
                        if (extras != null) {
                            Bitmap imageBitmap = (Bitmap) extras.get("data");
                            if (imageBitmap != null) {
                                selectedImageBitmap = imageBitmap;
                                binding.imgLogo.setImageBitmap(imageBitmap);
                            }
                        }
                    }
                }
        );

        // Permission launcher (for Android 13+)
        permissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Permission denied. Cannot access gallery.", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void setupClickListeners() {
        // Back button - find the ImageView in toolbar
        View toolbar = binding.getRoot().findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setOnClickListener(v -> onBackPressed());
        }

        // Change logo button
        binding.btnChangeLogo.setOnClickListener(v -> showImageSelectionDialog());

        // Add/Update bank button
        binding.updateBtn.setOnClickListener(v -> {
            if (!isLoading) {
                validateAndAddBank();
            }
        });

        // Search functionality
        View searchLayout = binding.getRoot().findViewById(R.id.rel_search);
        if (searchLayout != null) {
            searchLayout.setOnClickListener(v -> {
                View searchEditText = searchLayout.findViewById(R.id.search_img);
                if (searchEditText != null) {
                    searchEditText.requestFocus();
                }
            });
        }
    }

    private void setupValidations() {
        // Bank name validation
        binding.edtBankName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s.toString().trim();
                // Only show error if field has been touched and is empty
                if (text.isEmpty() && binding.edtBankName.hasFocus()) {
                    binding.edtBankName.setError("Bank name is required");
                } else if (!text.isEmpty()) {
                    binding.edtBankName.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Bank address validation
        binding.edtBankAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s.toString().trim();
                // Only show error if field has been touched and is empty
                if (text.isEmpty() && binding.edtBankAddress.hasFocus()) {
                    binding.edtBankAddress.setError("Bank address is required");
                } else if (!text.isEmpty()) {
                    binding.edtBankAddress.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Account number validation
        binding.edtPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String accountNumber = s.toString().trim();
                // Only validate if field has been touched
                if (binding.edtPhoneNumber.hasFocus()) {
                    if (accountNumber.isEmpty()) {
                        binding.edtPhoneNumber.setError("Account number is required");
                    } else if (accountNumber.length() < 8) {
                        binding.edtPhoneNumber.setError("Account number must be at least 8 digits");
                    } else if (!accountNumber.matches("\\d+")) {
                        binding.edtPhoneNumber.setError("Account number must contain only digits");
                    } else {
                        binding.edtPhoneNumber.setError(null);
                    }
                } else if (!accountNumber.isEmpty()) {
                    // Clear error if field is not empty
                    binding.edtPhoneNumber.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Add focus listeners to clear errors when fields lose focus after being cleared
        binding.edtBankName.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus && binding.edtBankName.getText().toString().trim().isEmpty()) {
                // Don't show error when focus is lost on empty field after clearing
                binding.edtBankName.setError(null);
            }
        });

        binding.edtBankAddress.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus && binding.edtBankAddress.getText().toString().trim().isEmpty()) {
                binding.edtBankAddress.setError(null);
            }
        });

        binding.edtPhoneNumber.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus && binding.edtPhoneNumber.getText().toString().trim().isEmpty()) {
                binding.edtPhoneNumber.setError(null);
            }
        });
    }

    private void showImageSelectionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Image Source");

        String[] options = {"Gallery", "Camera"};
        builder.setItems(options, (dialog, which) -> {
            switch (which) {
                case 0:
                    openGallery();
                    break;
                case 1:
                    openCamera();
                    break;
            }
        });

        builder.show();
    }

    private void openGallery() {
        if (checkGalleryPermission()) {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            galleryIntent.setType("image/*");
            galleryLauncher.launch(galleryIntent);
        } else {
            requestGalleryPermission();
        }
    }

    private void openCamera() {
        if (checkCameraPermission()) {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                cameraLauncher.launch(cameraIntent);
            } else {
                Toast.makeText(this, "Camera app not found", Toast.LENGTH_SHORT).show();
            }
        } else {
            requestCameraPermission();
        }
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

    private boolean checkCameraPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestGalleryPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_MEDIA_IMAGES)) {
                showPermissionRationaleDialog("Gallery", () ->
                        permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES));
            } else {
                permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
            }
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                showPermissionRationaleDialog("Gallery", () ->
                        ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                PERMISSION_REQUEST_CODE));
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_CODE);
            }
        }
    }

    private void requestCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            showPermissionRationaleDialog("Camera", () ->
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.CAMERA},
                            PERMISSION_REQUEST_CODE));
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    PERMISSION_REQUEST_CODE);
        }
    }

    private void showPermissionRationaleDialog(String permission, Runnable onPositive) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permission Required");
        builder.setMessage("This app needs " + permission + " permission to function properly. Please grant the permission.");
        builder.setPositiveButton("OK", (dialog, which) -> onPositive.run());
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void loadImageFromUri(Uri uri) {
        try {
            InputStream imageStream = getContentResolver().openInputStream(uri);
            Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
            if (selectedImage != null) {
                selectedImageBitmap = selectedImage;
                binding.imgLogo.setImageBitmap(selectedImage);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error loading image", Toast.LENGTH_SHORT).show();
        }
    }

    private void validateAndAddBank() {
        String bankName = binding.edtBankName.getText().toString().trim();
        String bankAddress = binding.edtBankAddress.getText().toString().trim();
        String accountNumber = binding.edtPhoneNumber.getText().toString().trim();
        String instructions = binding.edtInstructions.getText().toString().trim();

        // Validate required fields
        if (bankName.isEmpty()) {
            binding.edtBankName.setError("Bank name is required");
            binding.edtBankName.requestFocus();
            return;
        }

        if (bankAddress.isEmpty()) {
            binding.edtBankAddress.setError("Bank address is required");
            binding.edtBankAddress.requestFocus();
            return;
        }

        if (accountNumber.isEmpty()) {
            binding.edtPhoneNumber.setError("Account number is required");
            binding.edtPhoneNumber.requestFocus();
            return;
        }

        if (accountNumber.length() < 8) {
            binding.edtPhoneNumber.setError("Account number must be at least 8 digits");
            binding.edtPhoneNumber.requestFocus();
            return;
        }

        if (!accountNumber.matches("\\d+")) {
            binding.edtPhoneNumber.setError("Account number must contain only digits");
            binding.edtPhoneNumber.requestFocus();
            return;
        }

        // Check if account number already exists (skip if editing the same account)
        if (!isEditMode && isAccountNumberExists(accountNumber)) {
            binding.edtPhoneNumber.setError("Account number already exists");
            binding.edtPhoneNumber.requestFocus();
            return;
        }

        // Check auth token
        if (authToken == null || authToken.isEmpty() || authToken.equals("Bearer ")) {
            Toast.makeText(this, "Authentication token not found. Please login again.", Toast.LENGTH_LONG).show();
            return;
        }

        // Show loading state
        setLoadingState(true);

        // Prepare data for API call
        BankAccountFields accountFields = new BankAccountFields();
        accountFields.setName(bankName);
        accountFields.setAddress(bankAddress);
        accountFields.setAccNumber(accountNumber);
        accountFields.setInstruction(instructions.isEmpty() ? "No instructions" : instructions);

        // Convert image to base64 if available
        if (selectedImageBitmap != null) {
            try {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                selectedImageBitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
                byte[] imageBytes = byteArrayOutputStream.toByteArray();
                String base64Image = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                accountFields.setImageBase64(base64Image);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Error processing image", Toast.LENGTH_SHORT).show();
                setLoadingState(false);
                return;
            }
        }

        // Make API call and observe the response
        viewModel.addBankAccount(authToken, accountFields).observe(this, response -> {
            // Reset loading state first
            setLoadingState(false);

            if (response != null) {
                // Debug: Add logging to see the actual response
                android.util.Log.d("BankAccount", "Response: " + response.toString());

                // Check for success - adjust this based on your actual response structure
                boolean isSuccess = false;

                // Try different ways to check success based on your response model
                try {
                    if (response.isSuccess) {
                        isSuccess = true;
                    }
                } catch (Exception e) {
                    // If isSuccess field doesn't exist, try other methods
                }

                if (isSuccess) {
                    Toast.makeText(this, isEditMode ? "Bank account updated successfully" :
                            "Bank account added successfully", Toast.LENGTH_SHORT).show();

                    // Add to local list for immediate UI update
                    BankAccount newAccount = new BankAccount(
                            bankName,
                            bankAddress,
                            accountNumber,
                            instructions.isEmpty() ? "No instructions" : instructions,
                            selectedImageUri != null ? selectedImageUri.toString() : ""
                    );

                    if (isEditMode) {
                        bankAccountList.set(editPosition, newAccount);
                        bankAccountAdapter.notifyItemChanged(editPosition);
                        isEditMode = false;
                        editPosition = -1;
                        binding.btnTxt.setText("Add Bank Account");
                    } else {
                        bankAccountList.add(newAccount);
                        bankAccountAdapter.notifyItemInserted(bankAccountList.size() - 1);
                    }

                    clearForm();
                    updateRecyclerViewVisibility();
                } else {
                    String errorMessage = "Failed to " + (isEditMode ? "update" : "add") + " bank account";

                    // Try to get error message from response
                    try {
                        if (response.message != null && !response.message.isEmpty()) {
                            errorMessage = response.message;
                        }
                    } catch (Exception e) {
                        // If getMessage doesn't exist, use default message
                    }

                    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, "No response from server. Please try again.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setLoadingState(boolean loading) {
        isLoading = loading;
        binding.updateBtn.setEnabled(!loading);
        if (loading) {
            binding.btnTxt.setText("Please wait...");
        } else {
            binding.btnTxt.setText(isEditMode ? "Update Bank Account" : "Add Bank Account");
        }
    }

    private boolean isAccountNumberExists(String accountNumber) {
        for (BankAccount account : bankAccountList) {
            if (account.getAccountNumber().equals(accountNumber)) {
                return true;
            }
        }
        return false;
    }

    private void clearForm() {
        // Clear errors first to prevent validation issues
        binding.edtBankName.setError(null);
        binding.edtBankAddress.setError(null);
        binding.edtPhoneNumber.setError(null);
        binding.edtInstructions.setError(null);

        // Clear text fields
        binding.edtBankName.setText("");
        binding.edtBankAddress.setText("");
        binding.edtPhoneNumber.setText("");
        binding.edtInstructions.setText("");
        binding.imgLogo.setImageResource(R.drawable.placeholder);
        selectedImageUri = null;
        selectedImageBitmap = null;

        // Clear focus
        binding.edtBankName.clearFocus();
        binding.edtBankAddress.clearFocus();
        binding.edtPhoneNumber.clearFocus();
        binding.edtInstructions.clearFocus();
    }

    private void setupRecyclerView() {
        bankAccountAdapter = new BankAccountAdapter(bankAccountList, this);
        binding.rcBankAcc.setAdapter(bankAccountAdapter);

        // Set up adapter callbacks if the adapter supports them
        // Uncomment and modify these based on your adapter implementation
        /*
        bankAccountAdapter.setOnItemClickListener(new BankAccountAdapter.OnItemClickListener() {
            @Override
            public void onEditClick(BankAccount account, int position) {
                editBankAccount(account, position);
            }

            @Override
            public void onDeleteClick(BankAccount account, int position) {
                deleteBankAccount(account, position);
            }
        });
        */
    }

    private void editBankAccount(BankAccount account, int position) {
        // Fill form with existing data
        binding.edtBankName.setText(account.getBankName());
        binding.edtBankAddress.setText(account.getBankAddress());
        binding.edtPhoneNumber.setText(account.getAccountNumber());
        binding.edtInstructions.setText(account.getInstructions());

        // Load image if available
        if (!account.getLogoUri().isEmpty()) {
            selectedImageUri = Uri.parse(account.getLogoUri());
            loadImageFromUri(selectedImageUri);
        }

        // Set edit mode
        isEditMode = true;
        editPosition = position;
        binding.btnTxt.setText("Update Bank Account");

        // Scroll to top
        binding.getRoot().scrollTo(0, 0);
    }

    private void deleteBankAccount(BankAccount account, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Bank Account");
        builder.setMessage("Are you sure you want to delete this bank account?");
        builder.setPositiveButton("Delete", (dialog, which) -> {
            bankAccountList.remove(position);
            bankAccountAdapter.notifyItemRemoved(position);
            Toast.makeText(this, "Bank account deleted", Toast.LENGTH_SHORT).show();
            updateRecyclerViewVisibility();
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void loadBankAccounts() {
        // Show loading state
        setLoadingState(true);

        // Clear existing data
        bankAccountList.clear();

        // Make API call to fetch bank accounts
        if (authToken != null && !authToken.isEmpty() && !authToken.equals("Bearer ")) {
            // Uncomment and modify this based on your ViewModel method for getting bank accounts
            /*
            viewModel.getBankAccounts(authToken).observe(this, response -> {
                setLoadingState(false);

                if (response != null && response.isSuccess()) {
                    List<BankAccount> accounts = response.getData(); // Adjust based on your response structure
                    if (accounts != null && !accounts.isEmpty()) {
                        bankAccountList.addAll(accounts);
                        bankAccountAdapter.notifyDataSetChanged();
                    }
                }
                updateRecyclerViewVisibility();
            });
            */

            // For now, just update visibility since API call is commented
            setLoadingState(false);
            updateRecyclerViewVisibility();
        } else {
            setLoadingState(false);
            updateRecyclerViewVisibility();
        }
    }

    private void updateRecyclerViewVisibility() {
        View noDataText = binding.getRoot().findViewById(R.id.tv_no_data); // You might need to add this ID to your XML

        if (bankAccountList.isEmpty()) {
            binding.rcBankAcc.setVisibility(View.GONE);
            // Show "No Data Available" text if you have it in your layout
            if (noDataText != null) {
                noDataText.setVisibility(View.VISIBLE);
            }
        } else {
            binding.rcBankAcc.setVisibility(View.VISIBLE);
            // Hide "No Data Available" text
            if (noDataText != null) {
                noDataText.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (isLoading) {
            Toast.makeText(this, "Please wait for the operation to complete", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if form has unsaved changes
        if (hasUnsavedChanges()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Unsaved Changes");
            builder.setMessage("You have unsaved changes. Are you sure you want to go back?");
            builder.setPositiveButton("Yes", (dialog, which) -> super.onBackPressed());
            builder.setNegativeButton("No", null);
            builder.show();
        } else {
            super.onBackPressed();
        }
    }

    private boolean hasUnsavedChanges() {
        return !binding.edtBankName.getText().toString().trim().isEmpty() ||
                !binding.edtBankAddress.getText().toString().trim().isEmpty() ||
                !binding.edtPhoneNumber.getText().toString().trim().isEmpty() ||
                !binding.edtInstructions.getText().toString().trim().isEmpty() ||
                selectedImageUri != null ||
                selectedImageBitmap != null;
    }

    // BankAccount data model
    public static class BankAccount {
        private String bankName;
        private String bankAddress;
        private String accountNumber;
        private String instructions;
        private String logoUri;

        public BankAccount(String bankName, String bankAddress, String accountNumber, String instructions, String logoUri) {
            this.bankName = bankName;
            this.bankAddress = bankAddress;
            this.accountNumber = accountNumber;
            this.instructions = instructions;
            this.logoUri = logoUri;
        }

        // Getters and setters
        public String getBankName() { return bankName; }
        public void setBankName(String bankName) { this.bankName = bankName; }

        public String getBankAddress() { return bankAddress; }
        public void setBankAddress(String bankAddress) { this.bankAddress = bankAddress; }

        public String getAccountNumber() { return accountNumber; }
        public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }

        public String getInstructions() { return instructions; }
        public void setInstructions(String instructions) { this.instructions = instructions; }

        public String getLogoUri() { return logoUri; }
        public void setLogoUri(String logoUri) { this.logoUri = logoUri; }
    }
}