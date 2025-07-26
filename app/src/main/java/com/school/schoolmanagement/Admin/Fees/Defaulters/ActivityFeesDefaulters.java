package com.school.schoolmanagement.Admin.Fees.Defaulters;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.school.schoolmanagement.Admin.Adapter.AdapterFeesDefaulter;
import com.school.schoolmanagement.GlobalViewModel.ViewModel;
import com.school.schoolmanagement.Model.FeesDefaulterModel;
import com.school.schoolmanagement.R;
import com.school.schoolmanagement.Utils.Utility;
import com.school.schoolmanagement.databinding.ActivityFeesDefaultersBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ActivityFeesDefaulters extends Utility {
    ActivityFeesDefaultersBinding binding;
    private AdapterFeesDefaulter adapter;
    private List<FeesDefaulterModel.Datum> defaulterList;
    String auth;
    ViewModel viewModel;
    private Calendar calendar;
    private SimpleDateFormat dateFormat;
    private SimpleDateFormat monthNameFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityFeesDefaultersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeViews();
        setupClickListeners();

        if (isInternetConnected(this)) {
            // Set default month (current month)
            setCurrentMonth();
        } else {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
        }
    }

    private void initializeViews() {
        viewModel = new ViewModelProvider(this).get(ViewModel.class);
        auth = "Bearer " + pref.getPrefString(this, pref.user_token);

        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("MMMM", Locale.getDefault()); // For API calls
        monthNameFormat = new SimpleDateFormat("MMMM", Locale.getDefault()); // For display (e.g., "July 2024")

        // Initialize defaulter list
        defaulterList = new ArrayList<>();

        // Setup RecyclerView
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        binding.rcDefaulters.setLayoutManager(layoutManager);

        adapter = new AdapterFeesDefaulter(this, defaulterList);
        binding.rcDefaulters.setAdapter(adapter);
    }

    private void setupClickListeners() {
        // Date picker for fees month - show month/year picker
        binding.edtFeeMonth.setOnClickListener(v -> showMonthYearPicker());

        // Carry Forward Balance button
        binding.balanceBtn.setOnClickListener(v -> {
            Toast.makeText(this, "Carry Forward Balance clicked", Toast.LENGTH_SHORT).show();
            // Add your carry forward balance logic here
        });

        // Input/Select button
        binding.imgSelect.setOnClickListener(v -> {
            if (!binding.edtFeeMonth.getText().toString().trim().isEmpty()) {
                fetchFeesDefaulters();
            } else {
                Toast.makeText(this, "Please select fees month first", Toast.LENGTH_SHORT).show();
            }
        });

        // Notification button
        binding.imgNotification.setOnClickListener(v -> {
            Toast.makeText(this, "Notifications", Toast.LENGTH_SHORT).show();
            // Add your notification logic here
        });
    }

    private void showMonthYearPicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    // Set the selected year and month
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, 1); // Set to first day of month

                    // Display month and year
                    String selectedMonthYear = monthNameFormat.format(calendar.getTime());
                    binding.edtFeeMonth.setText(selectedMonthYear);

                    // Automatically call API when date is selected
                    fetchFeesDefaulters();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                1 // Set day to 1
        );

        // Hide the day picker to show only month and year
        try {
            datePickerDialog.getDatePicker().findViewById(
                    getResources().getIdentifier("day", "id", "android")
            ).setVisibility(View.GONE);
        } catch (Exception e) {
            // If hiding day fails, continue with normal date picker
        }

        datePickerDialog.show();
    }

    private void setCurrentMonth() {
        String currentMonth = monthNameFormat.format(calendar.getTime());
        binding.edtFeeMonth.setText(currentMonth);
        fetchFeesDefaulters();
    }

    private void fetchFeesDefaulters() {
        String feesMonth = binding.edtFeeMonth.getText().toString().trim();

        if (feesMonth.isEmpty()) {
            Toast.makeText(this, "Please select fees month", Toast.LENGTH_SHORT).show();
            return;
        }

        // Convert display format to API format (MM/yyyy)
        String apiMonth = convertToApiFormat(feesMonth);

        // Show loading
        showProgressDialog("Loading defaulters...");

        viewModel.getFeesDefaulter(auth, apiMonth).observe(this, response -> {
            hideProgressDialog();

            if (response != null) {
                if (response.isSuccess && response.data != null) {
                    FeesDefaulterModel feesDefaulterModel = response.data;

                    if (feesDefaulterModel.getData() != null && !feesDefaulterModel.getData().isEmpty()) {
                        defaulterList.clear();
                        defaulterList.addAll(feesDefaulterModel.getData());
                        adapter.notifyDataSetChanged();

                        // Show success message
                        Toast.makeText(this, "Found " + defaulterList.size() + " defaulters", Toast.LENGTH_SHORT).show();
                    } else {
                        defaulterList.clear();
                        adapter.notifyDataSetChanged();
                        Toast.makeText(this, "No defaulters found for selected month", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String convertToApiFormat(String displayMonth) {
        try {
            // Parse the display format (e.g., "July 2024")
            Calendar cal = Calendar.getInstance();
            cal.setTime(monthNameFormat.parse(displayMonth));
            // Convert to API format (MM/yyyy)
            return dateFormat.format(cal.getTime());
        } catch (Exception e) {
            // If parsing fails, try to extract month/year from current calendar
            return dateFormat.format(calendar.getTime());
        }
    }

    private void showProgressDialog(String message) {
        // Implement your progress dialog here
        // You can use a ProgressBar or custom dialog
    }

    private void hideProgressDialog() {
        // Hide your progress dialog here
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data when returning to activity
        if (isInternetConnected(this) && !binding.edtFeeMonth.getText().toString().trim().isEmpty()) {
            fetchFeesDefaulters();
        }
    }
}