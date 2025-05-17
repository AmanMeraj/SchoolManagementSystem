package com.school.schoolmanagement.Login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        updateSelection("Admin"); // Set Admin as default
        loginViewModel = new ViewModelProvider(this).get(ViewModel.class);

        checkForSavedCredentials(); // check if "Remember Me" is checked

        binding.loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = binding.edtFirstName.getText().toString().trim();
                String password = binding.edtPassword.getText().toString().trim();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(ActivityLogin.this, "Please enter email and password", Toast.LENGTH_SHORT).show();
                    return;
                }

                Login login = new Login();
                login.setUsername(email);
                login.setPassword(password);

                loginViewModel.postLogin(login).observe(ActivityLogin.this, response -> {
                    if (response.isSuccess) {
                        String role = response.data.getData().getRole();

                        // Save token, name, role
                        saveToken(
                                response.data.getData().token,
                                response.data.getData().getUsername(),
                                role
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

                        navigateToDashboard(role);
                        finish();

                    } else {
                        Toast.makeText(ActivityLogin.this, "Login failed. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                });
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
        switch (role) {
            case "CHILD_ADMIN":
                startActivity(new Intent(ActivityLogin.this, ActivityAdminDashboard.class));
                break;
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
