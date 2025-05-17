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
        loadAccountStatementData();
    }

    private void setupRecyclerView() {
        adapter = new AdapterAccountStatement();
        binding.rcStatement.setAdapter(adapter);
    }

    private void loadAccountStatementData() {
        // This is dummy data. In a real app, you would fetch data from API or database
        List<AccountStatementModel> statements = new ArrayList<>();
        statements.add(new AccountStatementModel("19/03/2025", "Completed", "$243", "$243", "$243"));
        statements.add(new AccountStatementModel("18/03/2025", "Pending", "$125", "$125", "$125"));
        statements.add(new AccountStatementModel("17/03/2025", "Failed", "$300", "$300", "$300"));
        // Add more dummy data as needed

        adapter.setData(statements);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}