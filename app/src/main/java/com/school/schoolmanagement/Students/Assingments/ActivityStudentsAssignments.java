package com.school.schoolmanagement.Students.Assingments;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.school.schoolmanagement.GlobalViewModel.ViewModel;
import com.school.schoolmanagement.R;
import com.school.schoolmanagement.Students.Adapter.StudentsAssignmentAdapter;
import com.school.schoolmanagement.Students.Model.StudentHomeworkResponse;
import com.school.schoolmanagement.Utils.Utility;
import com.school.schoolmanagement.databinding.ActivityStudentsAssingmentsBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class ActivityStudentsAssignments extends Utility {
    private static final String TAG = "StudentsAssignments";

    ActivityStudentsAssingmentsBinding binding;
    String auth;
    ViewModel viewModel;
    StudentsAssignmentAdapter adapter;
    ArrayList<StudentHomeworkResponse.Datum> homeworkList;
    Calendar selectedDate;
    SimpleDateFormat dateFormat;
    SimpleDateFormat apiDateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityStudentsAssingmentsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeComponents();
        setupRecyclerView();
        setupClickListeners();
        setCurrentDate();
        binding.toolbar.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

     if ((isInternetConnected(this))){
         searchHomework();
     }else{
         Toast.makeText(this, "No Internet Connection !", Toast.LENGTH_SHORT).show();
     }

    }

    private void initializeComponents() {
        viewModel = new ViewModelProvider(this).get(ViewModel.class);
        auth = "Bearer " + pref.getPrefString(this, pref.user_token);
        homeworkList = new ArrayList<>();
        selectedDate = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        apiDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    }

    private void setupRecyclerView() {
        adapter = new StudentsAssignmentAdapter(homeworkList);
        binding.recyclerViewAssignments.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerViewAssignments.setAdapter(adapter);
    }

    private void setupClickListeners() {
        // Date picker click listener - Material Date Picker
        binding.edtHomeworkDate.setOnClickListener(v -> showMaterialDatePicker());

        // Search button click listener
        binding.updateBtn.setOnClickListener(v -> {
            String selectedDateStr = binding.edtHomeworkDate.getText().toString().trim();
            if (selectedDateStr.isEmpty() || selectedDateStr.equals(" DD/MM/YYYY")) {
                Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show();
                return;
            }
            searchHomework();
        });
    }

    private void setCurrentDate() {
        // Set current date as default
        String currentDate = dateFormat.format(selectedDate.getTime());
        binding.edtHomeworkDate.setText(currentDate);
    }

    private void showMaterialDatePicker() {
        // Create Material Date Picker
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Homework Date")
                .setSelection(selectedDate.getTimeInMillis())
                .build();

        datePicker.addOnPositiveButtonClickListener(selection -> {
            // Convert selection to Calendar
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            calendar.setTimeInMillis(selection);

            // Update selected date
            selectedDate.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
            selectedDate.set(Calendar.MONTH, calendar.get(Calendar.MONTH));
            selectedDate.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH));

            // Format and display selected date
            String formattedDate = dateFormat.format(selectedDate.getTime());
            binding.edtHomeworkDate.setText(formattedDate);
        });

        datePicker.show(getSupportFragmentManager(), "DATE_PICKER");
    }

    private void searchHomework() {
        // Show loading state
        binding.updateBtn.setEnabled(false);
        showLoadingState();

        // Convert selected date to API format (yyyy-MM-dd)
        String apiDate = apiDateFormat.format(selectedDate.getTime());
        Log.d(TAG, "Searching homework for date: " + apiDate);

        // Make API call
        viewModel.getStudentHomework(auth, apiDate).observe(this, response -> {
            binding.updateBtn.setEnabled(true);
            hideLoadingState();

            if (response != null) {
                if (response.isSuccess && response.data != null) {
                    StudentHomeworkResponse homeworkResponse = response.data;

                    if (homeworkResponse.data != null && !homeworkResponse.data.isEmpty()) {
                        // Show data state
                        showDataState();

                        homeworkList.clear();
                        homeworkList.addAll(homeworkResponse.getData());
                        adapter.notifyDataSetChanged();

                        Toast.makeText(this, "Homework loaded successfully", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Loaded " + homeworkList.size() + " homework items");
                    } else {
                        // Show empty state
                        showEmptyState();
                        Toast.makeText(this, "No homework found for selected date", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "No homework data available for date: " + apiDate);
                    }
                } else {
                    // Show empty state on error
                    showEmptyState();
                    String errorMessage = response.message != null ?
                            response.message : "Failed to fetch homework";
                    Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "API Error: " + errorMessage + ", Code: " + response.code);
                }
            } else {
                // Show empty state on network error
                showEmptyState();
                Toast.makeText(this, "Network error occurred", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Response is null");
            }
        });
    }

    private void showLoadingState() {
        // Hide both data and empty states while loading
        binding.recyclerviewRel.setVisibility(View.GONE);
        binding.relNoItem.setVisibility(View.GONE);
        // You can add a progress bar here if needed
    }

    private void hideLoadingState() {
        // This will be handled by showDataState() or showEmptyState()
    }

    private void showDataState() {
        binding.recyclerviewRel.setVisibility(View.VISIBLE);
        binding.relNoItem.setVisibility(View.GONE);
    }

    private void showEmptyState() {
        binding.recyclerviewRel.setVisibility(View.GONE);
        binding.relNoItem.setVisibility(View.VISIBLE);

        // Clear the list to ensure no stale data
        homeworkList.clear();
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Optionally refresh homework data when activity resumes
        // Uncomment the line below if you want to refresh on resume
         searchHomework();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up resources if needed
        if (binding != null) {
            binding = null;
        }
    }
}