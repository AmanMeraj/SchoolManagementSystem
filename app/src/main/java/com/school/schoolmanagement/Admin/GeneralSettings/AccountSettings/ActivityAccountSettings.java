package com.school.schoolmanagement.Admin.GeneralSettings.AccountSettings;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.school.schoolmanagement.Admin.Model.AccountSettings;
import com.school.schoolmanagement.Admin.Model.AccountSettingsResponse;
import com.school.schoolmanagement.GlobalViewModel.ViewModel;
import com.school.schoolmanagement.R;
import com.school.schoolmanagement.Utils.Utility;
import com.school.schoolmanagement.databinding.ActivityAccountSettingsBinding;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityAccountSettings extends Utility {
    ActivityAccountSettingsBinding binding;
    ViewModel viewModel;
    String auth;

    // Static data for dropdowns
    private String[] timeZones = {
            "UTC-12:00", "UTC-11:00", "UTC-10:00", "UTC-09:00", "UTC-08:00", "UTC-07:00",
            "UTC-06:00", "UTC-05:00", "UTC-04:00", "UTC-03:00", "UTC-02:00", "UTC-01:00",
            "UTC+00:00", "UTC+01:00", "UTC+02:00", "UTC+03:00", "UTC+04:00", "UTC+05:00",
            "UTC+05:30", "UTC+06:00", "UTC+07:00", "UTC+08:00", "UTC+09:00", "UTC+10:00",
            "UTC+11:00", "UTC+12:00"
    };

    private String[] currencies = {
            "USD - US Dollar", "EUR - Euro", "GBP - British Pound", "JPY - Japanese Yen",
            "CAD - Canadian Dollar", "AUD - Australian Dollar", "CHF - Swiss Franc",
            "CNY - Chinese Yuan", "INR - Indian Rupee", "KRW - South Korean Won",
            "SGD - Singapore Dollar", "NZD - New Zealand Dollar", "MXN - Mexican Peso",
            "ZAR - South African Rand", "BRL - Brazilian Real", "RUB - Russian Ruble",
            "HKD - Hong Kong Dollar", "SEK - Swedish Krona", "NOK - Norwegian Krone",
            "DKK - Danish Krone", "PLN - Polish Zloty", "CZK - Czech Koruna",
            "HUF - Hungarian Forint", "ILS - Israeli Shekel", "TRY - Turkish Lira"
    };

    private String[] symbols = {
            "$", "€", "£", "¥", "C$", "A$", "CHF", "¥", "₹", "₩",
            "S$", "NZ$", "$", "R", "R$", "₽", "HK$", "kr", "kr",
            "kr", "zł", "Kč", "Ft", "₪", "₺"
    };

    // Map to match currency with symbol
    private Map<String, String> currencySymbolMap;

    private ArrayAdapter<String> timeZoneAdapter;
    private ArrayAdapter<String> currencyAdapter;
    private ArrayAdapter<String> symbolAdapter;

    private AccountSettings currentAccountSettings;
    private boolean isUpdateMode = false;
    private int accountId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityAccountSettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        viewModel = new ViewModelProvider(this).get(ViewModel.class);
        auth = generateAuth();

        initializeCurrencySymbolMap();
        setupDropdowns();
        setupClickListeners();
        getAccountSettings();
    }

    private void initializeCurrencySymbolMap() {
        currencySymbolMap = new HashMap<>();
        for (int i = 0; i < currencies.length && i < symbols.length; i++) {
            currencySymbolMap.put(currencies[i], symbols[i]);
        }
    }

    private void setupDropdowns() {
        // Time Zone Dropdown
        timeZoneAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, timeZones);
        binding.edtTimeZone.setAdapter(timeZoneAdapter);
        binding.edtTimeZone.setThreshold(1);

        // Show dropdown on focus and click
        binding.edtTimeZone.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                // Reset adapter to show all items
                timeZoneAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, timeZones);
                binding.edtTimeZone.setAdapter(timeZoneAdapter);
                binding.edtTimeZone.showDropDown();
            }
        });

        binding.edtTimeZone.setOnClickListener(v -> {
            // Reset adapter to show all items
            timeZoneAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, timeZones);
            binding.edtTimeZone.setAdapter(timeZoneAdapter);
            binding.edtTimeZone.showDropDown();
        });

        binding.edtTimeZone.setOnItemClickListener((parent, view, position, id) -> {
            binding.edtTimeZone.setText(timeZones[position]);
            binding.edtTimeZone.clearFocus(); // Hide keyboard
            binding.edtTimeZone.dismissDropDown(); // Hide dropdown
        });

        // Currency Dropdown
        currencyAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, currencies);
        binding.edtCurrency.setAdapter(currencyAdapter);
        binding.edtCurrency.setThreshold(1);

        binding.edtCurrency.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                // Reset adapter to show all items
                currencyAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, currencies);
                binding.edtCurrency.setAdapter(currencyAdapter);
                binding.edtCurrency.showDropDown();
            }
        });

        binding.edtCurrency.setOnClickListener(v -> {
            // Reset adapter to show all items
            currencyAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, currencies);
            binding.edtCurrency.setAdapter(currencyAdapter);
            binding.edtCurrency.showDropDown();
        });

        binding.edtCurrency.setOnItemClickListener((parent, view, position, id) -> {
            String selectedCurrency = currencies[position];
            binding.edtCurrency.setText(selectedCurrency);
            binding.edtCurrency.clearFocus();
            binding.edtCurrency.dismissDropDown();

            // Auto-select corresponding symbol
            String correspondingSymbol = currencySymbolMap.get(selectedCurrency);
            if (correspondingSymbol != null) {
                binding.edtSymbol.setText(correspondingSymbol);
            }
        });

        // Symbol Dropdown
        symbolAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, symbols);
        binding.edtSymbol.setAdapter(symbolAdapter);
        binding.edtSymbol.setThreshold(1);

        binding.edtSymbol.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                // Reset adapter to show all items
                symbolAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, symbols);
                binding.edtSymbol.setAdapter(symbolAdapter);
                binding.edtSymbol.showDropDown();
            }
        });

        binding.edtSymbol.setOnClickListener(v -> {
            // Reset adapter to show all items
            symbolAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, symbols);
            binding.edtSymbol.setAdapter(symbolAdapter);
            binding.edtSymbol.showDropDown();
        });

        binding.edtSymbol.setOnItemClickListener((parent, view, position, id) -> {
            binding.edtSymbol.setText(symbols[position]);
            binding.edtSymbol.clearFocus();
            binding.edtSymbol.dismissDropDown();
        });
    }


    private void setupClickListeners() {
        // Update Button Click
        binding.updateBtn.setOnClickListener(v -> {
            if (validateInputs()) {
                if (isUpdateMode) {
                    updateAccountSettings();
                } else {
                    createAccountSettings();
                }
            }
        });

        // Delete Button Click
        binding.deleteBtn.setOnClickListener(v -> {
            if (accountId != -1) {
                deleteAccountSettings();
            } else {
                showToast("No account to delete");
            }
        });

        // Back Button Click
        binding.toolbar.getChildAt(0).setOnClickListener(v -> finish());
    }

    private boolean validateInputs() {
        String username = binding.edtUserName.getText().toString().trim();
        String password = binding.edtPassword.getText().toString().trim();
        String timeZone = binding.edtTimeZone.getText().toString().trim();
        String currency = binding.edtCurrency.getText().toString().trim();
        String symbol = binding.edtSymbol.getText().toString().trim();

        // Username validation
        if (TextUtils.isEmpty(username)) {
            binding.edtUserName.setError("Username is required");
            binding.edtUserName.requestFocus();
            return false;
        }

        if (username.length() < 3) {
            binding.edtUserName.setError("Username must be at least 3 characters");
            binding.edtUserName.requestFocus();
            return false;
        }

        // Password validation (only for create mode)
        if (!isUpdateMode && TextUtils.isEmpty(password)) {
            binding.edtPassword.setError("Password is required");
            binding.edtPassword.requestFocus();
            return false;
        }

        if (!isUpdateMode && password.length() < 6) {
            binding.edtPassword.setError("Password must be at least 6 characters");
            binding.edtPassword.requestFocus();
            return false;
        }

        // Time Zone validation
        if (TextUtils.isEmpty(timeZone)) {
            showToast("Please select a time zone");
            return false;
        }

        // Currency validation
        if (TextUtils.isEmpty(currency)) {
            showToast("Please select a currency");
            return false;
        }

        // Symbol validation
        if (TextUtils.isEmpty(symbol)) {
            showToast("Please select a symbol");
            return false;
        }

        // Check if currency and symbol match
        String expectedSymbol = currencySymbolMap.get(currency);
        if (expectedSymbol != null && !expectedSymbol.equals(symbol)) {
            showToast("Currency and symbol don't match. Expected: " + expectedSymbol);
            return false;
        }

        return true;
    }

    private void getAccountSettings() {
        showProgressDialog("Loading account settings...");

        viewModel.getAccountSettings(auth).observe(this, response -> {
            hideProgressDialog();

            if (response != null && response.isSuccess) {
                AccountSettingsResponse accountSettingsResponse = response.data;
                if (accountSettingsResponse != null && accountSettingsResponse.data != null) {
                    populateFields(response.data);
                    isUpdateMode = true;
                    accountId = response.data.data.id;

                    // Convert Data to AccountSettings for updateAccountDetails
                    currentAccountSettings = convertDataToAccountSettings(response.data.data);
                    updateAccountDetails();
                } else {
                    // No existing settings, create mode
                    isUpdateMode = false;
                    showToast("No existing account settings found. You can create new settings.");
                }
            } else {
                showToast("Failed to load account settings: " + (response != null ? response.message : "Unknown error"));
            }
        });
    }

    private void populateFields(AccountSettingsResponse settings) {
        if (settings != null && settings.getData() != null) {
            binding.edtUserName.setText(settings.getData().getUserName() != null ? settings.getData().getUserName() : "");
            binding.edtPassword.setText(settings.getData().getPassword() != null ? settings.getData().getPassword() : "");
            binding.edtTimeZone.setText(settings.getData().getTimezone() != null ? settings.getData().getTimezone(): "");
            binding.edtCurrency.setText(settings.getData().getCurrency() != null ? settings.getData().getCurrency() : "");
            binding.edtSymbol.setText(settings.getData().getSymbolOfCurrency() != null ? settings.getData().getSymbolOfCurrency() : "");
        }
    }

    // Helper method to convert Data to AccountSettings
    private AccountSettings convertDataToAccountSettings(AccountSettingsResponse.Data data) {
        AccountSettings accountSettings = new AccountSettings();
        if (data != null) {
            accountSettings.setUserName(data.getUserName());
            accountSettings.setPassword(data.getPassword());
            accountSettings.setTimezone(data.getTimezone());
            accountSettings.setCurrency(data.getCurrency());
            accountSettings.setSymbolOfCurrency(data.getSymbolOfCurrency());
            // Add other fields if they exist in both classes
            // accountSettings.setSubscription(data.getSubscription());
            // accountSettings.setExpiry(data.getExpiry());
        }
        return accountSettings;
    }
    private void updateAccountDetails() {
        if (currentAccountSettings != null) {
            binding.tvUserName.setText(currentAccountSettings.getUserName() != null ? currentAccountSettings.getUserName() : "N/A");
            binding.tvSubscription.setText(currentAccountSettings.getSubscription() != null ? currentAccountSettings.getSubscription() : "Free");
            binding.tvExpiry.setText(currentAccountSettings.getExpiry() != null ? currentAccountSettings.getExpiry() : "Never");
        }
    }

    private void createAccountSettings() {
        showProgressDialog("Creating account settings...");

        AccountSettings accountSettings = new AccountSettings();
        accountSettings.setUserName(binding.edtUserName.getText().toString().trim());
        accountSettings.setPassword(binding.edtPassword.getText().toString().trim());
        accountSettings.setTimezone(binding.edtTimeZone.getText().toString().trim());
        accountSettings.setCurrency(binding.edtCurrency.getText().toString().trim());
        accountSettings.setSymbolOfCurrency(binding.edtSymbol.getText().toString().trim());

        viewModel.postAccountSettings(auth, accountSettings).observe(this, response -> {
            hideProgressDialog();

            if (response != null && response.isSuccess) {
                showToast("Account settings created successfully");
                // Refresh the data
                getAccountSettings();
            } else {
                showToast("Failed to create account settings: " + (response != null ? response.message : "Unknown error"));
            }
        });
    }

    private void updateAccountSettings() {
        if (accountId == -1) {
            showToast("No account ID available for update");
            return;
        }

        showProgressDialog("Updating account settings...");

        AccountSettings accountSettings = new AccountSettings();
        accountSettings.setUserName(binding.edtUserName.getText().toString().trim());

        // Only include password if it's not empty
        String password = binding.edtPassword.getText().toString().trim();
        if (!TextUtils.isEmpty(password)) {
            accountSettings.setPassword(password);
        }

        accountSettings.setTimezone(binding.edtTimeZone.getText().toString().trim());
        accountSettings.setCurrency(binding.edtCurrency.getText().toString().trim());
        accountSettings.setSymbolOfCurrency(binding.edtSymbol.getText().toString().trim());

        viewModel.updateAccountSettings(auth, accountId, accountSettings).observe(this, response -> {
            hideProgressDialog();

            if (response != null && response.isSuccess) {
                showToast("Account settings updated successfully");
                // Refresh the data
                getAccountSettings();
            } else {
                showToast("Failed to update account settings: " + (response != null ? response.message : "Unknown error"));
            }
        });
    }

    private void deleteAccountSettings() {
        showProgressDialog("Deleting account settings...");

        viewModel.deleteAccountSettings(auth, accountId).observe(this, response -> {
            hideProgressDialog();

            if (response != null && response.isSuccess) {
                showToast("Account settings deleted successfully");
                // Reset to create mode
                isUpdateMode = false;
                accountId = -1;
                clearFields();
                clearAccountDetails();
            } else {
                showToast("Failed to delete account settings: " + (response != null ? response.message : "Unknown error"));
            }
        });
    }

    private void clearFields() {
        binding.edtUserName.setText("");
        binding.edtPassword.setText("");
        binding.edtTimeZone.setText("");
        binding.edtCurrency.setText("");
        binding.edtSymbol.setText("");
    }

    private void clearAccountDetails() {
        binding.tvUserName.setText("N/A");
        binding.tvSubscription.setText("Free");
        binding.tvExpiry.setText("Never");
    }

    public String generateAuth() {
        return "Bearer " + pref.getPrefString(this, pref.user_token);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void showProgressDialog(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        binding.loader.rlLoader.setVisibility(View.VISIBLE);
    }

    private void hideProgressDialog() {
        // Implement hiding progress dialog here
        binding.loader.rlLoader.setVisibility(View.GONE);
    }
}