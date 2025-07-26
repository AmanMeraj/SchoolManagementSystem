package com.school.schoolmanagement.Students.UpdateLoginDetails;

import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.school.schoolmanagement.GlobalViewModel.ViewModel;
import com.school.schoolmanagement.R;
import com.school.schoolmanagement.Utils.Utility;
import com.school.schoolmanagement.databinding.ActivityUpdateLoginCredentialsBinding;

public class ActivityUpdateLoginCredentials extends Utility {
    ActivityUpdateLoginCredentialsBinding binding;
    ViewModel viewModel;
    String auth,from;
    private String actualPassword = "";
    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityUpdateLoginCredentialsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        viewModel = new ViewModelProvider(this).get(ViewModel.class);
        auth = "Bearer " + pref.getPrefString(this, pref.user_token);
        from=getIntent().getStringExtra("from");


        initViews();
        setupClickListeners();

        if (isInternetConnected(this)) {
            loadUserData();
        } else {
            showNoInternetDialog();
        }
    }

    private void initViews() {
        // Make username field non-editable
        binding.edtFirstName.setEnabled(false);
        binding.edtFirstName.setFocusable(false);
        binding.edtFirstName.setClickable(false);

        // Set username from preferences or any stored value
        String username = pref.getPrefString(this, pref.user_name); // Assuming you have user_name key
        if (username != null && !username.isEmpty()) {
            binding.edtFirstName.setText(username);
        }

        // Clear password field initially
        binding.edtPassword.setText("");
        binding.passwordField.setText("********************");
    }

    private void setupClickListeners() {
        // Update button click listener
        binding.updateBtn.setOnClickListener(v -> {
            if (validateInput()) {
                if(from.matches("employee")){
                    updatePassword2();
                }else {
                    updatePassword();
                }
            }
        });

        // Password visibility toggle in the card view
        binding.toggleVisibility.setOnClickListener(v -> {
            togglePasswordVisibility();
        });

        // Back button listener (if using custom toolbar)
        // binding.toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void loadUserData() {
        // Load and display current username
        String currentUsername = pref.getPrefString(this, pref.user_name);
        if (currentUsername != null && !currentUsername.isEmpty()) {
            binding.edtFirstName.setText(currentUsername);
        }
    }

    private boolean validateInput() {
        String newPassword = binding.edtPassword.getText().toString().trim();

        if (newPassword.isEmpty()) {
            binding.textFieldPassword.setError("Please enter new password");
            binding.edtPassword.requestFocus();
            return false;
        }

        if (newPassword.length() < 6) {
            binding.textFieldPassword.setError("Password must be at least 6 characters");
            binding.edtPassword.requestFocus();
            return false;
        }

        // Clear any previous errors
        binding.textFieldPassword.setError(null);
        return true;
    }

    private void updatePassword() {
        if (!isInternetConnected(this)) {
            showNoInternetDialog();
            return;
        }

        String newPassword = binding.edtPassword.getText().toString().trim();

        // Show loading state
        binding.updateBtn.setEnabled(false);
        binding.refreshImg.setRotation(0f);
        binding.refreshImg.animate().rotationBy(360f).setDuration(1000).start();

        // Call API through ViewModel
        viewModel.updateStudentPassword(auth, newPassword).observe(this, response -> {
            // Hide loading state
            binding.updateBtn.setEnabled(true);
            binding.refreshImg.clearAnimation();

            if (response != null) {
                if (response.isSuccess) {
                    // Success
                    Toast.makeText(this, "Password updated successfully!", Toast.LENGTH_SHORT).show();

                    // Update the displayed password in the card view
                    updateDisplayedPassword(newPassword);

                    // Clear the input field
                    binding.edtPassword.setText("");

                    // Optionally finish activity or stay on the screen
                    // finish();

                } else {
                    // Error
                    String errorMessage = response.message;
                    if (errorMessage == null || errorMessage.isEmpty()) {
                        errorMessage = "Failed to update password. Please try again.";
                    }
                    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, "Something went wrong. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void updatePassword2() {
        if (!isInternetConnected(this)) {
            showNoInternetDialog();
            return;
        }

        String newPassword = binding.edtPassword.getText().toString().trim();

        // Show loading state
        binding.updateBtn.setEnabled(false);
        binding.refreshImg.setRotation(0f);
        binding.refreshImg.animate().rotationBy(360f).setDuration(1000).start();

        // Call API through ViewModel
        viewModel.updateEmployeePassword(auth, newPassword).observe(this, response -> {
            // Hide loading state
            binding.updateBtn.setEnabled(true);
            binding.refreshImg.clearAnimation();

            if (response != null) {
                if (response.isSuccess) {
                    // Success
                    Toast.makeText(this, "Password updated successfully!", Toast.LENGTH_SHORT).show();

                    // Update the displayed password in the card view
                    updateDisplayedPassword(newPassword);

                    // Clear the input field
                    binding.edtPassword.setText("");

                    // Optionally finish activity or stay on the screen
                    // finish();

                } else {
                    // Error
                    String errorMessage = response.message;
                    if (errorMessage == null || errorMessage.isEmpty()) {
                        errorMessage = "Failed to update password. Please try again.";
                    }
                    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, "Something went wrong. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateDisplayedPassword(String newPassword) {
        actualPassword = newPassword; // Save the actual password

        if (isPasswordVisible) {
            binding.passwordField.setText(actualPassword);
        } else {
            binding.passwordField.setText("*".repeat(actualPassword.length()));
        }

        binding.passwordField.setSelection(binding.passwordField.getText().length()); // move cursor to end
    }


    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            // Hide password
            binding.passwordField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            binding.toggleVisibility.setImageResource(R.drawable.open_eye);
            isPasswordVisible = false;

            // Show masked version
            binding.passwordField.setText("*".repeat(actualPassword.length()));
        } else {
            // Show password
            binding.passwordField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            binding.toggleVisibility.setImageResource(R.drawable.ic_eye_closed);
            isPasswordVisible = true;

            binding.passwordField.setText(actualPassword); // Show real password
        }

        // Move cursor to end
        binding.passwordField.setSelection(binding.passwordField.getText().length());
    }


    private void showNoInternetDialog() {
        Toast.makeText(this, "No internet connection. Please check your network.", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}