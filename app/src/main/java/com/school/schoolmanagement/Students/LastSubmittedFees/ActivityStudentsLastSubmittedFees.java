package com.school.schoolmanagement.Students.LastSubmittedFees;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.school.schoolmanagement.R;
import com.school.schoolmanagement.databinding.ActivityStudentsLastSubmittedFeesBinding;

public class ActivityStudentsLastSubmittedFees extends AppCompatActivity {
    ActivityStudentsLastSubmittedFeesBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding=ActivityStudentsLastSubmittedFeesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.rowId.label.setText("Registration/ID");
        binding.rowId.value.setText("615384362");

        binding.rowClass.label.setText("Class");
        binding.rowClass.value.setText("Year 1");

        binding.rowName.label.setText("Name");
        binding.rowName.value.setText("John Doe");

        binding.rowMonth.label.setText("Fee Month");
        binding.rowMonth.value.setText("February, 2025");

        binding.rowTotal.label.setText("Total Amount");
        binding.rowTotal.value.setText("$ 211196");

        binding.rowDeposit.label.setText("Deposit");
        binding.rowDeposit.value.setText("$ 211196");

        binding.rowRemaining.label.setText("Remainings");
        binding.rowRemaining.value.setText("$ 0");
    }
}