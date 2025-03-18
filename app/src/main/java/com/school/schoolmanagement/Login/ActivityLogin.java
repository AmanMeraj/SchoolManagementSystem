package com.school.schoolmanagement.Login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.school.schoolmanagement.Admin.ActivityAdminDashboard;
import com.school.schoolmanagement.ForgotPassword.ActivityForgotPassword;
import com.school.schoolmanagement.R;
import com.school.schoolmanagement.Signup.ActivitySignup;
import com.school.schoolmanagement.databinding.ActivityLoginBinding;

public class ActivityLogin extends AppCompatActivity {
ActivityLoginBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActivityLogin.this, ActivityAdminDashboard.class);
                startActivity(intent);
            }
        });
        binding.newAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ActivityLogin.this, ActivitySignup.class);
                startActivity(i);

            }
        });
        binding.relForgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ActivityLogin.this, ActivityForgotPassword.class);
                startActivity(i);

            }
        });
    }
}