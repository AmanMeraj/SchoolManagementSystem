package com.school.schoolmanagement.Admin.Accounts.Statement;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.school.schoolmanagement.Admin.Adapter.AdapterAccountStatement;
import com.school.schoolmanagement.Admin.Model.AccountStatementModel;
import com.school.schoolmanagement.GlobalViewModel.ViewModel;
import com.school.schoolmanagement.HelperClasses.DataExportHelper;
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
        binding.toolbar.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        initializeViewModel();
        setupExportButtons();
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
    private void setupExportButtons() {
        // Copy button
        binding.tvCopy.setOnClickListener(view -> {
            ArrayList<ArrayList<String>> tableData = prepareTableData();
            handleExport(tableData, "copy");
        });

        // CSV button
        binding.tvCsv.setOnClickListener(view -> {
            ArrayList<ArrayList<String>> tableData = prepareTableData();
            handleExport(tableData, "csv");
        });

        // Excel button
        binding.tvExcel.setOnClickListener(view -> {
            ArrayList<ArrayList<String>> tableData = prepareTableData();
            handleExport(tableData, "excel");
        });

        // PDF button
        binding.tvPdf.setOnClickListener(view -> {
            ArrayList<ArrayList<String>> tableData = prepareTableData();
            handleExport(tableData, "pdf");
        });

        // Print button
        binding.tvPrint.setOnClickListener(view -> {
            ArrayList<ArrayList<String>> tableData = prepareTableData();
            handleExport(tableData, "print");
        });
    }

    private void handleExport(ArrayList<ArrayList<String>> tableData, String action) {
        if (tableData.size() <= 1) { // Only headers, no data
            Toast.makeText(this, "No data to export", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Create DataExportHelper instance with context
            DataExportHelper exportHelper = new DataExportHelper(this);

            // Generate dynamic filename based on date range
            String fileName = generateDynamicFileName();

            // Use the exportData method with dynamic filename
            exportHelper.exportData(tableData, action, fileName);

        } catch (Exception e) {
            Log.e(TAG, "Export error: " + e.getMessage());
            Toast.makeText(this, "Export failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private String generateDynamicFileName() {
        String fileName = "account_statement";

        // Get the date range from the TextView
        String dateRange = binding.dateTv.getText().toString();

        if (dateRange != null && !dateRange.isEmpty()) {
            // Replace spaces and special characters with underscores
            String sanitizedDateRange = dateRange.replace(" ", "_")
                    .replace(",", "")
                    .replace("-", "to");
            fileName = "account_statement_" + sanitizedDateRange;
        }

        return fileName;
    }

    private ArrayList<ArrayList<String>> prepareTableData() {
        ArrayList<ArrayList<String>> tableData = new ArrayList<>();

        // Add header row
        ArrayList<String> headers = new ArrayList<>();
        headers.add("Sr");
        headers.add("Date");
        headers.add("Description");
        headers.add("Debit");
        headers.add("Credit");
        headers.add("Net Balance");
        headers.add("Payment Method");
        tableData.add(headers);

        // Add data rows from your adapter
        if (adapter != null && adapter.getCurrentFilteredData() != null) {
            int count=1;
            for (AccountStatementModel statement : adapter.getCurrentFilteredData()) {
                ArrayList<String> row = new ArrayList<>();
                row.add(String.valueOf(count));
                row.add(statement.getDate() != null ? statement.getDate() : "");
                row.add(statement.getStatus()!= null ? statement.getStatus() : "");
                row.add(statement.getAmount1() != null ? statement.getAmount1() : "$0.00");
                row.add(statement.getAmount2() != null ? statement.getAmount2() : "$0.00");
                row.add(statement.getAmount3() != null ? statement.getAmount3() : "$0.00");
                row.add(statement.getPaymentMethod() != null ? statement.getPaymentMethod() : "");
                tableData.add(row);
                count++;
            }
        }

        return tableData;
    }
}