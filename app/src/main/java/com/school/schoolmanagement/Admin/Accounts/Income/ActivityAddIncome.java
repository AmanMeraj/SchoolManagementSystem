package com.school.schoolmanagement.Admin.Accounts.Income;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.school.schoolmanagement.Admin.Model.AccountGet;
import com.school.schoolmanagement.Admin.Model.AddIncome;
import com.school.schoolmanagement.GlobalViewModel.ViewModel;
import com.school.schoolmanagement.HelperClasses.ChartApiHelper;
import com.school.schoolmanagement.R;
import com.school.schoolmanagement.Utils.Utility;
import com.school.schoolmanagement.databinding.ActivityAddExpenseBinding;
import com.school.schoolmanagement.databinding.ActivityAddIncomeBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ActivityAddIncome extends Utility implements ChartApiHelper.ChartFetchCallback {
    ActivityAddIncomeBinding binding;
    private ChartApiHelper chartApiHelper;
    private ViewModel viewModel;
    private Calendar calendar;
    private SimpleDateFormat dateFormat;

    // Lists for expense charts
    private List<AccountGet.Datum> expenseCharts;
    private List<String> expenseChartNames;
    private ArrayAdapter<String> expenseAdapter;
    private int selectedExpenseChartId = -1;

    // Auth token - replace with your actual token retrieval method
    private String authToken = ""; // Get this from SharedPreferences or wherever you store it


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding=ActivityAddIncomeBinding.inflate(getLayoutInflater());
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

        initializeComponents();
        setupDatePicker();
        setupDescriptionDropdown();
        setupValidation();
        loadExpenseCharts();


    }
    private void initializeComponents() {
        // Initialize ViewModel and Helper
        viewModel = new ViewModelProvider(this).get(ViewModel.class);
        chartApiHelper = new ChartApiHelper(viewModel, this);
        chartApiHelper.setFetchCallback(this);

        // Initialize Calendar and Date Format
        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        // Initialize Lists
        expenseCharts = new ArrayList<>();
        expenseChartNames = new ArrayList<>();

        // Disable date input - only allow picker
        binding.edtDate.setInputType(InputType.TYPE_NULL);
        binding.edtDate.setFocusable(false);
        binding.edtDate.setClickable(true);
    }

    private void setupDatePicker() {
        binding.edtDate.setOnClickListener(v -> showDatePicker());
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    String selectedDate = dateFormat.format(calendar.getTime());
                    binding.edtDate.setText(selectedDate);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        // Optional: Set max date to today to prevent future dates
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

        datePickerDialog.show();
    }

    private void setupDescriptionDropdown() {
        // Initialize adapter with empty list first
        expenseAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, expenseChartNames);
        binding.edtDescription.setAdapter(expenseAdapter);

        // Set threshold to 0 so dropdown shows immediately when clicked
        binding.edtDescription.setThreshold(0);

        // Optional: Disable keyboard input if you want dropdown-only behavior
        // binding.edtDescription.setInputType(InputType.TYPE_NULL);
        // binding.edtDescription.setKeyListener(null);

        // Show dropdown when clicked
        binding.edtDescription.setOnClickListener(v -> {
            binding.edtDescription.showDropDown();
        });

        // Show dropdown when focused
        binding.edtDescription.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                binding.edtDescription.showDropDown();
            } else {
                // Handle manual text changes when focus is lost
                String enteredText = binding.edtDescription.getText().toString().trim();
                boolean found = false;

                for (int i = 0; i < expenseChartNames.size(); i++) {
                    if (expenseChartNames.get(i).equalsIgnoreCase(enteredText)) {
                        selectedExpenseChartId = expenseCharts.get(i).getId();
                        found = true;
                        break;
                    }
                }

                if (!found && !enteredText.isEmpty()) {
                    selectedExpenseChartId = -1;
                }
            }
        });

        binding.edtDescription.setOnItemClickListener((parent, view, position, id) -> {
            if (position >= 0 && position < expenseCharts.size()) {
                selectedExpenseChartId = expenseCharts.get(position).getId();
                binding.edtDescription.setText(expenseChartNames.get(position));
                binding.edtDescription.dismissDropDown();
                // Clear focus to hide keyboard
                binding.edtDescription.clearFocus();
            }
        });
    }

    // Update the onSuccess method to properly refresh the adapter


    private void setupValidation() {
        binding.addIncome.setOnClickListener(v -> {
            if (validateForm()) {
                addIncome();
            }
        });
    }

    private boolean validateForm() {
        boolean isValid = true;

        // Validate Date
        if (TextUtils.isEmpty(binding.edtDate.getText().toString().trim())) {
            showError("Please select a date");
            binding.edtDate.requestFocus();
            isValid = false;
        }

        // Validate Description
        else if (TextUtils.isEmpty(binding.edtDescription.getText().toString().trim())) {
            showError("Please select an expense description");
            binding.edtDescription.requestFocus();
            isValid = false;
        }
        else if (selectedExpenseChartId == -1) {
            showError("Please select a valid expense description from the list");
            binding.edtDescription.requestFocus();
            isValid = false;
        }

        // Validate Amount
        else if (TextUtils.isEmpty(binding.edtAmount.getText().toString().trim())) {
            showError("Please enter an amount");
            binding.edtAmount.requestFocus();
            isValid = false;
        }
        else {
            try {
                double amount = Double.parseDouble(binding.edtAmount.getText().toString().trim());
                if (amount <= 0) {
                    showError("Amount must be greater than 0");
                    binding.edtAmount.requestFocus();
                    isValid = false;
                }
                else if (amount > 99999999.99) {
                    showError("Amount is too large");
                    binding.edtAmount.requestFocus();
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                showError("Please enter a valid amount");
                binding.edtAmount.requestFocus();
                isValid = false;
            }
        }

        // Validate Payment Method
        if (TextUtils.isEmpty(binding.edtPaymentMethod.getText().toString().trim())) {
            showError("Please enter a payment method");
            binding.edtPaymentMethod.requestFocus();
            isValid = false;
        }
        else if (binding.edtPaymentMethod.getText().toString().trim().length() < 2) {
            showError("Payment method must be at least 2 characters");
            binding.edtPaymentMethod.requestFocus();
            isValid = false;
        }

        return isValid;
    }

    private void addIncome() {
        AddIncome addIncome = new AddIncome();
        String date = binding.edtDate.getText().toString().trim();
        String amount = binding.edtAmount.getText().toString().trim();
        String paymentMethod = binding.edtPaymentMethod.getText().toString().trim();

        // Show progress and disable button
        binding.addIncome.setEnabled(false);

        // Set the expense data
        addIncome.setDate(date);
        addIncome.setChartId(selectedExpenseChartId);
        addIncome.setAmount(Double.parseDouble(amount));
        addIncome.setPaymentMethod(paymentMethod);

        // Log the request data
        Log.d("addIncome", "Sending expense request:");
        Log.d("addIncome", "Date: " + date);
        Log.d("addIncome", "Chart ID: " + selectedExpenseChartId);
        Log.d("addIncome", "Amount: " + amount);
        Log.d("addIncome", "Payment Method: " + paymentMethod);
        Log.d("addIncome", "Auth Token: " + (authToken != null ? "Present" : "Null"));

        viewModel.addIncome(authToken, addIncome).observe(this, response -> {
            // Re-enable button first
            binding.addIncome.setEnabled(true);

            // Log the complete response
            Log.d("addIncome", "Response received:");
            Log.d("addIncome", "Success: " + response.isSuccess);
            Log.d("addIncome", "Message: " + response.message);

            if (response.data != null) {
                Log.d("addIncome", "Response Data: " + response.data.toString());
            } else {
                Log.d("addIncome", "Response Data: null");
            }

            if (response.isSuccess) {
                Log.d("addIncome", "Expense added successfully");
                showSuccess("Income added successfully!");
                resetForm();
                finish();
            } else {
                Log.e("Income", "Failed to add expense: " + response.message);
                Toast.makeText(this, "Failed to add expense: " + response.message, Toast.LENGTH_LONG).show();
            }
        });
    }
    private void loadExpenseCharts() {
        // TODO: Replace with your actual auth token retrieval
        authToken = getAuthToken(); // Implement this method to get your auth token

        if (TextUtils.isEmpty(authToken)) {
            showError("Authentication required. Please login again.");
            return;
        }

        chartApiHelper.getAccountChart(authToken);
    }

    private String getAuthToken() {
        return "Bearer "+pref.getPrefString(this,pref.user_token);
    }

    private void resetForm() {
        binding.edtDate.setText("");
        binding.edtDescription.setText("");
        binding.edtAmount.setText("");
        binding.edtPaymentMethod.setText("");
        selectedExpenseChartId = -1;
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void showSuccess(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    // ChartApiHelper.ChartFetchCallback implementation
    @Override
    public void onSuccess(AccountGet response) {
        if (response != null) {
            expenseCharts.clear();
            expenseChartNames.clear();

            // Filter only EXPENSE type charts
            for (AccountGet.Datum chart : response.data) {
                if ("INCOME".equalsIgnoreCase(chart.getChartType())) {
                    expenseCharts.add(chart);
                    expenseChartNames.add(chart.getChartName());
                }
            }

            // Update adapter on UI thread
            runOnUiThread(() -> {
                // Recreate adapter with updated data
                expenseAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, expenseChartNames);
                binding.edtDescription.setAdapter(expenseAdapter);
                binding.edtDescription.setThreshold(0);

                if (expenseCharts.isEmpty()) {
                    showError("No expense categories found. Please create expense categories first.");
                }
            });
        }
    }

    @Override
    public void onError(String errorMessage, int errorCode) {
        runOnUiThread(() -> {
            showError("Failed to load expense categories: " + errorMessage);

            // Handle authentication errors
            if (ChartApiHelper.isAuthError(errorCode)) {
                // TODO: Redirect to login or refresh token
                showError("Please login again");
            }
        });
    }

    @Override
    public void onNetworkFailure() {
        runOnUiThread(() -> {
            showError("Network error. Please check your internet connection and try again.");
        });
    }

    @Override
    public void showLoading() {
        runOnUiThread(() -> {

        });
    }

    @Override
    public void hideLoading() {
        runOnUiThread(() -> {

        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
