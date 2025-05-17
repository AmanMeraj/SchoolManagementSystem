package com.school.schoolmanagement.Teachers.Attendance;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.util.Pair;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import com.school.schoolmanagement.R;
import com.school.schoolmanagement.Teachers.Adapters.AttendanceAdapter;
import com.school.schoolmanagement.Teachers.Model.AttendanceReport;
import com.school.schoolmanagement.databinding.ActivityStudentsAttendancereportBinding;

public class ActivityStudentsAttendancereport extends AppCompatActivity {

    private List<AttendanceReport> attendanceList;
    private AttendanceAdapter adapter;


    ActivityStudentsAttendancereportBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding=ActivityStudentsAttendancereportBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // Set click listener on the card using binding
        binding.dateRangeCard.setOnClickListener(v -> showDateRangePicker());
        // Initialize RecyclerView
        setupRecyclerView();

        // Load data
        loadAttendanceData();
    }

    private void setupRecyclerView() {
        attendanceList = new ArrayList<>();
        adapter = new AttendanceAdapter(this, attendanceList);
        binding.rcRowAttendanceReport.setAdapter(adapter);
    }

    private void loadAttendanceData() {
        // Sample data - in a real app, you would get this from an API or database
        attendanceList.add(new AttendanceReport("01/03/2025", "Mon", "123444", "John Doe", "Year 1", "P"));
        attendanceList.add(new AttendanceReport("02/03/2025", "Tue", "123444", "John Doe", "Year 1", "P"));
        attendanceList.add(new AttendanceReport("03/03/2025", "Wed", "123444", "John Doe", "Year 1", "A"));
        attendanceList.add(new AttendanceReport("04/03/2025", "Thu", "123444", "John Doe", "Year 1", "P"));
        attendanceList.add(new AttendanceReport("05/03/2025", "Fri", "123444", "John Doe", "Year 1", "L"));

        // Add more students
        attendanceList.add(new AttendanceReport("01/03/2025", "Mon", "123445", "Jane Smith", "Year 1", "P"));
        attendanceList.add(new AttendanceReport("01/03/2025", "Mon", "123446", "Bob Johnson", "Year 2", "A"));

        // Notify adapter after data is loaded
        adapter.notifyDataSetChanged();
    }

    private void showDateRangePicker() {
        // Create the date range picker
        MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
        builder.setTitleText("Select Date Range");

        // Create the picker
        MaterialDatePicker<Pair<Long, Long>> picker = builder.build();

        // Add positive button click listener
        picker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
            @Override
            public void onPositiveButtonClick(Pair<Long, Long> selection) {
                // Format the dates for display
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

                Date startDate = new Date(selection.first);
                Date endDate = new Date(selection.second);

                String dateRange = simpleDateFormat.format(startDate) + " - " + simpleDateFormat.format(endDate);

                // Update the TextView with the selected date range using binding
                binding.dateRangeText.setText(dateRange);
            }
        });

        // Show the picker
        picker.show(getSupportFragmentManager(), picker.toString());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clear the binding reference to avoid memory leaks
        binding = null;
    }
}