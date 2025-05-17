package com.school.schoolmanagement.Accountant.IncomeOrExpense;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.school.schoolmanagement.R;
import com.school.schoolmanagement.databinding.ActivityAddIncomeOrExpenseBinding;

public class ActivityAddIncomeOrExpense extends AppCompatActivity {
    ActivityAddIncomeOrExpenseBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding=ActivityAddIncomeOrExpenseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        String mode = getIntent().getStringExtra("MODE");

        if ("INCOME".equals(mode)) {
            binding.heading.setText("Expense");
            binding.subHeading.setText("Add Income");
            binding.cardAddIncome.setVisibility(View.VISIBLE);
            binding.cardAddExpense.setVisibility(View.GONE);

        } else {
            binding.heading.setText("Expense");
            binding.subHeading.setText("Add Expense");
            binding.cardAddIncome.setVisibility(View.GONE);
            binding.cardAddExpense.setVisibility(View.VISIBLE);
        }

    }
}