package com.school.schoolmanagement.Admin.GeneralSettings.RulesAndregulations;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;
import android.text.Editable;
import android.text.TextWatcher;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.school.schoolmanagement.Admin.Model.StudentId;
import com.school.schoolmanagement.GlobalViewModel.ViewModel;
import com.school.schoolmanagement.R;
import com.school.schoolmanagement.Utils.Utility;
import com.school.schoolmanagement.databinding.ActivityRulesAndRegulationsBinding;

public class ActivityRulesAndRegulations extends Utility {
    ActivityRulesAndRegulationsBinding binding;
    ViewModel viewModel;

    // State management variables
    private boolean isLoading = false;
    private boolean hasUnsavedChanges = false;
    private String originalRulesText = "";

    // Validation constants
    private static final int MIN_RULES_LENGTH = 10;
    private static final int MAX_RULES_LENGTH = 50000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRulesAndRegulationsBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeComponents();
        setupValidations();
    }

    private void initializeComponents() {
        viewModel = new ViewModelProvider(this).get(ViewModel.class);

        // Back button click listener
        binding.imgBack.setOnClickListener(view -> handleBackPress());

        // Save button click listener
        binding.updateBtn.setOnClickListener(view -> {
            if (validateInput()) {
                saveRulesAndRegulations();
            }
        });

        // Initially disable save button
        updateSaveButtonState(false);
    }

    private void setupValidations() {
        // Add text watcher for real-time validation
        binding.edtAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateInputRealTime(s.toString());
                checkForUnsavedChanges(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void validateInputRealTime(String text) {
        String trimmedText = text.trim();

        if (trimmedText.isEmpty()) {
            showInputError("Rules and regulations cannot be empty");
            updateSaveButtonState(false);
        } else if (trimmedText.length() < MIN_RULES_LENGTH) {
            showInputError("Rules must be at least " + MIN_RULES_LENGTH + " characters long");
            updateSaveButtonState(false);
        } else if (trimmedText.length() > MAX_RULES_LENGTH) {
            showInputError("Rules cannot exceed " + MAX_RULES_LENGTH + " characters");
            updateSaveButtonState(false);
        } else {
            clearInputError();
            updateSaveButtonState(true);
        }
    }

    private boolean validateInput() {
        String rulesText = binding.edtAddress.getText().toString().trim();

        // Check if empty
        if (TextUtils.isEmpty(rulesText)) {
            showToast("Please enter rules and regulations");
            binding.edtAddress.requestFocus();
            return false;
        }

        // Check minimum length
        if (rulesText.length() < MIN_RULES_LENGTH) {
            showToast("Rules must be at least " + MIN_RULES_LENGTH + " characters long");
            binding.edtAddress.requestFocus();
            return false;
        }

        // Check maximum length
        if (rulesText.length() > MAX_RULES_LENGTH) {
            showToast("Rules cannot exceed " + MAX_RULES_LENGTH + " characters");
            binding.edtAddress.requestFocus();
            return false;
        }

        // Check for internet connection
        if (!isInternetConnected(this)) {
            showToast("No Internet Connection!");
            return false;
        }

        // Check if already loading
        if (isLoading) {
            showToast("Please wait, saving in progress...");
            return false;
        }

        return true;
    }

    private void checkForUnsavedChanges(String currentText) {
        hasUnsavedChanges = !currentText.equals(originalRulesText);
        // You can add visual indicators for unsaved changes here
        // For example, change the save button text or add an asterisk to title
    }

    private void saveRulesAndRegulations() {
        String rulesText = binding.edtAddress.getText().toString().trim();

        StudentId rulesAndRegulations = new StudentId();
        rulesAndRegulations.setRulesAndRegulations(rulesText);

        String auth = "Bearer " + pref.getPrefString(this, pref.user_token);

        setLoadingState(true);

        viewModel.postRulesAndRegulations(auth, rulesAndRegulations).observe(this, response -> {
            setLoadingState(false);

            if (response.isSuccess) {
                showToast("Rules and regulations saved successfully!");
                originalRulesText = rulesText;
                hasUnsavedChanges = false;
                // Optionally finish the activity or reset form
            } else {
                showToast("Failed to save: " + response.message);
            }
        });
    }

    private void setLoadingState(boolean loading) {
        isLoading = loading;

        if (loading) {
            binding.loader.rlLoader.setVisibility(View.VISIBLE);
            binding.updateBtn.setEnabled(false);
            binding.edtAddress.setEnabled(false);
        } else {
            binding.loader.rlLoader.setVisibility(View.GONE);
            binding.updateBtn.setEnabled(true);
            binding.edtAddress.setEnabled(true);
        }
    }

    private void updateSaveButtonState(boolean enabled) {
        binding.updateBtn.setEnabled(enabled && !isLoading);
        binding.updateBtn.setAlpha(enabled ? 1.0f : 0.5f);
    }

    private void showInputError(String message) {
        // You can implement this to show error below the EditText
        // For now, we'll just store it or show as toast for critical errors
    }

    private void clearInputError() {
        // Clear any error messages
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void handleBackPress() {
        if (hasUnsavedChanges) {
            // Show dialog to confirm exit without saving
            showUnsavedChangesDialog();
        } else {
            finish();
        }
    }

    private void showUnsavedChangesDialog() {
        // Create AlertDialog to ask user if they want to save before leaving
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Unsaved Changes")
                .setMessage("You have unsaved changes. Do you want to save before leaving?")
                .setPositiveButton("Save", (dialog, which) -> {
                    if (validateInput()) {
                        saveRulesAndRegulations();
                    }
                })
                .setNegativeButton("Discard", (dialog, which) -> finish())
                .setNeutralButton("Cancel", null)
                .show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        handleBackPress();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up any resources
        if (viewModel != null) {
            // Remove observers if needed
        }
    }
}