package com.school.schoolmanagement.Admin.Accounts.Statement;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.school.schoolmanagement.Admin.Adapter.AdapterAccountStatement;
import com.school.schoolmanagement.Admin.Model.AccountStatementModel;
import com.school.schoolmanagement.GlobalViewModel.ViewModel;
import com.school.schoolmanagement.Model.AccountStatement;
import com.school.schoolmanagement.Model.Income;
import com.school.schoolmanagement.Model.Expense;
import com.school.schoolmanagement.Model.TransactionWrapper;
import com.school.schoolmanagement.R;
import com.school.schoolmanagement.Utils.Utility;
import com.school.schoolmanagement.databinding.ActivityAccountsStatementBinding;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ActivityAccountsStatement extends Utility {
    private static final String TAG = "ActivityAccountsStatement";
    private ActivityAccountsStatementBinding binding;
    private AdapterAccountStatement adapter;
    private ViewModel viewModel; // Replace with your actual ViewModel class name

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityAccountsStatementBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeViewModel();
        setupRecyclerView();
        setupSearchFunctionality();
        fetchAccountStatementData();
    }

    private void initializeViewModel() {
        viewModel = new ViewModelProvider(this).get(ViewModel.class); // Replace with your ViewModel class
    }

    private void setupRecyclerView() {
        adapter = new AdapterAccountStatement();
        binding.rcStatement.setAdapter(adapter);
    }

    private void setupSearchFunctionality() {
        // Add TextWatcher to the search EditText
        binding.edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed for this implementation
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Filter the adapter data based on search text
                if (adapter != null) {
                    adapter.filter(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Not needed for this implementation
            }
        });

        // Optional: Add click listener to search icon for additional functionality
        binding.imageSearch.setOnClickListener(v -> {
            // You can add additional search functionality here if needed
            // For now, the TextWatcher handles real-time search
        });
    }

    private void fetchAccountStatementData() {
        // Show loading indicator
        binding.loader.rlLoader.setVisibility(View.VISIBLE); // Add progressBar to your layout if not present

        // Get auth token (replace with your method to get auth token)
        String authToken = getAuthToken(); // Implement getAuthToken() method

        viewModel.getAccountStatement(authToken).observe(this, response -> {
            binding.loader.rlLoader.setVisibility(View.GONE);

            if (response != null) {
                if (response.isSuccess && response.data != null) {
                    AccountStatement accountStatement = response.data;
                    if (accountStatement.getData() != null) {
                        processAccountStatementData(accountStatement.getData());
                    } else {
                        Log.e(TAG, "Account statement data is null");
                        // Handle empty data case
                        adapter.setData(new ArrayList<>());
                    }
                } else {
                    Log.e(TAG, "Failed to fetch account statement: " + response.message);
                    // Handle error case - you might want to show a toast or error message
                    adapter.setData(new ArrayList<>());
                }
            }
        });
    }

    private void processAccountStatementData(AccountStatement.Data data) {
        List<AccountStatementModel> combinedList = new ArrayList<>();
        List<TransactionWrapper> tempList = new ArrayList<>();

        // Collect incomes
        if (data.getIncomes() != null) {
            for (Income income : data.getIncomes()) {
                tempList.add(new TransactionWrapper(
                        income.getDate(),
                        "Income",
                        income.getAmount(),
                        income.getPaymentMethod()
                ));
            }
        }

        // Collect expenses
        if (data.getExpenses() != null) {
            for (Expense expense : data.getExpenses()) {
                tempList.add(new TransactionWrapper(
                        expense.getDate(),
                        "Expense",
                        expense.getAmount(),
                        expense.getPaymentMethod()
                ));
            }
        }

        // Sort transactions by date ASCENDING (oldest first)
        Collections.sort(tempList, Comparator.comparing(TransactionWrapper::getDate));

        double runningBalance = 0.0;

        // Process transactions and calculate net balance
        for (TransactionWrapper transaction : tempList) {
            String date = transaction.getDate();
            String type = transaction.getType();
            double amount = transaction.getAmount();
            String paymentMethod = transaction.getPaymentMethod();

            double debit = 0.0;
            double credit = 0.0;

            if ("Income".equals(type)) {
                credit = amount;
            } else if ("Expense".equals(type)) {
                debit = amount;
            }

            runningBalance = runningBalance + credit - debit;

            String amount1 = String.format(Locale.getDefault(), "$%.2f", debit);        // Debit
            String amount2 = String.format(Locale.getDefault(), "$%.2f", credit);       // Credit
            String amount3 = String.format(Locale.getDefault(), "$%.2f", runningBalance); // Net Balance

            AccountStatementModel model = new AccountStatementModel(
                    date,
                    type,
                    amount1,
                    amount2,
                    amount3,
                    paymentMethod
            );

            combinedList.add(model);
        }

        // ✅ Do NOT reverse — we want earliest first
        adapter.setData(combinedList);

        // Optional summary updates
        updateSummaryViews(data);
    }

    private void updateSummaryViews(AccountStatement.Data data) {
        // Update summary views if you have them in your layout
        // Example:
        // binding.textTotalIncome.setText(String.format(Locale.getDefault(), "$%.2f", data.getTotalIncome()));
        // binding.textTotalExpense.setText(String.format(Locale.getDefault(), "$%.2f", data.getTotalExpense()));
        // binding.textTotalBalance.setText(String.format(Locale.getDefault(), "$%.2f", data.getTotalBalance()));
    }

    private String getAuthToken() {
        return "Bearer "+pref.getPrefString(this,pref.user_token);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}