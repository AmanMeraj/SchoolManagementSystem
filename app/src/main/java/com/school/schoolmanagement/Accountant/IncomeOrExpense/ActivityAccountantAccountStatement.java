package com.school.schoolmanagement.Accountant.IncomeOrExpense;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.school.schoolmanagement.Admin.Adapter.AdapterAccountStatement;
import com.school.schoolmanagement.Admin.Model.AccountStatementModel;
import com.school.schoolmanagement.R;
import com.school.schoolmanagement.databinding.ActivityAccountantAccountStatementBinding;

import java.util.ArrayList;
import java.util.List;

public class ActivityAccountantAccountStatement extends AppCompatActivity {
    ActivityAccountantAccountStatementBinding binding;
    private AdapterAccountStatement adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding=ActivityAccountantAccountStatementBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        setupRecyclerView();

    }

    private void setupRecyclerView() {
        adapter = new AdapterAccountStatement();
        binding.rcStatement.setAdapter(adapter);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}