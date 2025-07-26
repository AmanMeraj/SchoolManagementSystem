package com.school.schoolmanagement.Login;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.school.schoolmanagement.Accountant.Dashboard.ActivityAccountantDashboard;
import com.school.schoolmanagement.Admin.ActivityAdminDashboard;
import com.school.schoolmanagement.ForgotPassword.ActivityForgotPassword;
import com.school.schoolmanagement.GlobalViewModel.ViewModel;
import com.school.schoolmanagement.Model.Login;
import com.school.schoolmanagement.R;
import com.school.schoolmanagement.Students.Dashboard.ActivityStudentsDashboard;
import com.school.schoolmanagement.Teachers.Dashboard.ActivityTeachersDashboard;
import com.school.schoolmanagement.Utils.Utility;
import com.school.schoolmanagement.databinding.ActivityLoginBinding;

public class ActivityLogin extends Utility {

    ActivityLoginBinding binding;
    String selectedCategory = "Admin";
    ViewModel loginViewModel;
    private ViewTreeObserver.OnGlobalLayoutListener keyboardLayoutListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Multiple approaches to handle keyboard
        getWindow().setSoftInputMode(
                android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                        android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN
        );

        // Alternative: Force fullscreen mode adjustment
        getWindow().setFlags(
                android.view.WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                android.view.WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Add keyboard detection and manual adjustment
        setupKeyboardListener();

        updateSelection("Admin"); // Set Admin as default
        loginViewModel = new ViewModelProvider(this).get(ViewModel.class);

        checkForSavedCredentials(); // check if "Remember Me" is checked

        binding.loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isInternetConnected(ActivityLogin.this)){
                    getLogin();
                }else {
                    Toast.makeText(ActivityLogin.this, "No internet connection!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.relForgotPass.setOnClickListener(view -> {
            startActivity(new Intent(ActivityLogin.this, ActivityForgotPassword.class));
        });

        binding.relAdmin.setOnClickListener(view -> {
            selectedCategory = "Admin";
            updateSelection("Admin");
        });

        binding.relStudents.setOnClickListener(view -> {
            selectedCategory = "Students";
            updateSelection("Students");
        });

        binding.relTeacher.setOnClickListener(view -> {
            selectedCategory = "Employee";
            updateSelection("Employee");
        });
    }

    private void setupKeyboardListener() {
        final View rootView = binding.getRoot();
        keyboardLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
            private int initialHeight = 0;

            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                rootView.getWindowVisibleDisplayFrame(r);
                int currentHeight = r.height();

                if (initialHeight == 0) {
                    initialHeight = currentHeight;
                    return;
                }

                int heightDiff = initialHeight - currentHeight;

                if (heightDiff > 200) { // Keyboard is opened (threshold of 200px)
                    // Force the layout to move up
                    rootView.post(new Runnable() {
                        @Override
                        public void run() {
                            // Get the focused view (input field)
                            View focusedView = getCurrentFocus();
                            if (focusedView != null) {
                                // Calculate how much to scroll up
                                int[] location = new int[2];
                                focusedView.getLocationOnScreen(location);
                                int focusedViewY = location[1];
                                int availableHeight = r.bottom;

                                // If focused view is below visible area, scroll up
                                if (focusedViewY + focusedView.getHeight() > availableHeight - 100) {
                                    int scrollAmount = (focusedViewY + focusedView.getHeight()) - (availableHeight - 200);
                                    rootView.scrollBy(0, scrollAmount);
                                }
                            }
                        }
                    });
                } else if (heightDiff < 100) { // Keyboard is closed
                    rootView.scrollTo(0, 0);
                }
            }
        };
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(keyboardLayoutListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remove the keyboard listener to prevent memory leaks
        if (keyboardLayoutListener != null && binding != null) {
            binding.getRoot().getViewTreeObserver().removeOnGlobalLayoutListener(keyboardLayoutListener);
        }
    }

    private void updateSelection(String category) {
        binding.relAdmin.setBackgroundResource(R.drawable.background_elipse);
        binding.relTeacher.setBackgroundResource(R.drawable.background_elipse);
        binding.relStudents.setBackgroundResource(R.drawable.background_elipse);

        selectedCategory = category;

        switch (category) {
            case "Admin":
                binding.relAdmin.setBackgroundResource(R.drawable.background_elipse_selected);
                break;
            case "Employee":
                binding.relTeacher.setBackgroundResource(R.drawable.background_elipse_selected);
                break;
            case "Students":
                binding.relStudents.setBackgroundResource(R.drawable.background_elipse_selected);
                break;
        }
    }

    private void getLogin(){
        String email = binding.edtFirstName.getText().toString().trim();
        String password = binding.edtPassword.getText().toString().trim();
        binding.loader.rlLoader.setVisibility(View.VISIBLE);

        if (email.isEmpty() || password.isEmpty()) {
            binding.loader.rlLoader.setVisibility(View.GONE);
            Toast.makeText(ActivityLogin.this, "Please enter email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        Login login = new Login();
        login.setUsername(email);
        login.setPassword(password);

        loginViewModel.postLogin(login).observe(ActivityLogin.this, response -> {
            binding.loader.rlLoader.setVisibility(View.GONE);

            if (response.isSuccess) {
                String apiRole = response.data.getData().getRole();

                // Check if selected role matches API response role
                if (!isRoleMatching(selectedCategory, apiRole)) {
                    String userFriendlyRole = getUserFriendlyRoleName(apiRole);
                    Toast.makeText(ActivityLogin.this,
                            "Try signing in as " + userFriendlyRole,
                            Toast.LENGTH_LONG).show();
                    return;
                }

                // Save token, name, role
                saveToken(
                        response.data.getData().token,
                        response.data.getData().getUsername(),
                        apiRole
                );
                Log.d("TAG", "onClick: "+response.data.getData().token);

                // Save credentials if "Remember Me" is checked
                if (binding.shippingCheckBox.isChecked()) {
                    pref.setPrefBoolean(ActivityLogin.this, pref.remember_me, true);
                    pref.setPrefString(ActivityLogin.this, pref.user_name, email);
                    pref.setPrefString(ActivityLogin.this, pref.user_password, password);
                } else {
                    pref.setPrefBoolean(ActivityLogin.this, pref.remember_me, false);
                }

                navigateToDashboard(apiRole);
                finish();

            } else {
                Toast.makeText(ActivityLogin.this, "Login failed. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Helper method to check if selected role matches API role
    private boolean isRoleMatching(String selectedCategory, String apiRole) {
        switch (selectedCategory) {
            case "Admin":
                // Allow any admin role to access admin dashboard
                return apiRole.contains("ADMIN") || "ADMIN".equals(apiRole);
            case "Employee":
                return "TEACHER".equals(apiRole) || "ACCOUNTANT".equals(apiRole);
            case "Students":
                return "STUDENT".equals(apiRole);
            default:
                return false;
        }
    }

    // Helper method to convert API role to user-friendly name
    private String getUserFriendlyRoleName(String apiRole) {
        // Check if role contains "ADMIN" or is exactly "ADMIN"
        if (apiRole.contains("ADMIN") || "ADMIN".equals(apiRole)) {
            return "Admin";
        }

        switch (apiRole) {
            case "TEACHER":
                return "Teacher";
            case "ACCOUNTANT":
                return "Accountant";
            case "STUDENT":
                return "Student";
            default:
                return apiRole; // fallback to original role name
        }
    }

    private void saveToken(String token, String displayName, String role) {
        Log.d("Token Saving", "saveToken: "+token);
        pref.setPrefString(this, pref.user_token, token);
        pref.setPrefString(this, pref.user_name, displayName);
        pref.setPrefString(this, pref.user_role, role);
        pref.setPrefBoolean(this, pref.login_status, true);
    }

    private void checkForSavedCredentials() {
        boolean rememberMe = pref.getPrefBoolean(this, pref.remember_me);

        if (rememberMe) {
            String savedUsername = pref.getPrefString(this, pref.user_name);
            String savedPassword = pref.getPrefString(this, pref.user_password);

            binding.edtFirstName.setText(savedUsername);
            binding.edtPassword.setText(savedPassword);
            binding.shippingCheckBox.setChecked(true);
        }

        boolean isLoggedIn = pref.getPrefBoolean(this, pref.login_status);
        if (isLoggedIn) {
            String savedRole = pref.getPrefString(this, pref.user_role);
            navigateToDashboard(savedRole);
            finish();
        }
    }

    private void navigateToDashboard(String role) {
        // Check if role contains "ADMIN" or is exactly "ADMIN"
        if (role.contains("ADMIN") || "ADMIN".equals(role)) {
            startActivity(new Intent(ActivityLogin.this, ActivityAdminDashboard.class));
        } else {
            switch (role) {
                case "TEACHER":
                    startActivity(new Intent(ActivityLogin.this, ActivityTeachersDashboard.class));
                    break;
                case "ACCOUNTANT":
                    startActivity(new Intent(ActivityLogin.this, ActivityAccountantDashboard.class));
                    break;
                case "STUDENT":
                    startActivity(new Intent(ActivityLogin.this, ActivityStudentsDashboard.class));
                    break;
                default:
                    Toast.makeText(ActivityLogin.this, "Unknown role: " + role, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
}