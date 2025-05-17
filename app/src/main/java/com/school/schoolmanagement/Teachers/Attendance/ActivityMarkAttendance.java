package com.school.schoolmanagement.Teachers.Attendance;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.school.schoolmanagement.Adapters.AdapterMarkAttendance;
import com.school.schoolmanagement.Model.MarkAttendance;
import com.school.schoolmanagement.R;
import com.school.schoolmanagement.Utils.Utility;
import com.school.schoolmanagement.databinding.ActivityMarkAttendanceBinding;

import java.util.ArrayList;
import java.util.List;

public class ActivityMarkAttendance extends Utility implements AdapterMarkAttendance.OnAttendanceChangeListener {
ActivityMarkAttendanceBinding binding;
    private AdapterMarkAttendance adapter;
    private List<MarkAttendance> studentList;
    private String selectedDate;
    private String selectedClass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding=ActivityMarkAttendanceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // Get data from intent
        selectedDate = getIntent().getStringExtra("selected_date");
        selectedClass = getIntent().getStringExtra("selected_class");

        setupToolbar();
        initStudentList();
        setupRecyclerView();
    }

    private void setupToolbar() {

        View backButton = binding.toolbar.backBtn.findViewById(R.id.back_btn);
        if (backButton != null) {
            backButton.setOnClickListener(v -> onBackPressed());
        }
    }

    private void initStudentList() {
        studentList = new ArrayList<>();

        // Dummy data for testing - replace with your actual data source
        studentList.add(new MarkAttendance("1234567", "John Doe"));
        studentList.add(new MarkAttendance("2345678", "Jane Smith"));
        studentList.add(new MarkAttendance("3456789", "Steve Johnson"));
        studentList.add(new MarkAttendance("4567890", "Mary Williams"));
        studentList.add(new MarkAttendance("5678901", "David Brown"));
        studentList.add(new MarkAttendance("6789012", "Sarah Davis"));
        studentList.add(new MarkAttendance("7890123", "Michael Miller"));
        studentList.add(new MarkAttendance("8901234", "Emily Wilson"));
        studentList.add(new MarkAttendance("9012345", "James Taylor"));
        studentList.add(new MarkAttendance("0123456", "Emma Anderson"));

        // In a real app, you would fetch this data from your database
        // loadStudentsFromDatabase(selectedClass);
    }

    private void setupRecyclerView() {
        adapter = new AdapterMarkAttendance(this, studentList, this);
        binding.rcMarkAttendance.setAdapter(adapter);

        // Update header information
        binding.yearDate.setText(selectedClass+" "+selectedDate);
    }

    private void setupSubmitButton() {
        binding.submitBtn.setOnClickListener(v -> {
            if (validateAttendance()) {
                saveAttendanceRecords();
            } else {
                Toast.makeText(this, "Please mark attendance for all students", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validateAttendance() {
        // Check if all students have attendance marked
        for (MarkAttendance student : studentList) {
            if (student.hasNoStatus()) {
                return false;
            }
        }
        return true;
    }

    private void saveAttendanceRecords() {
        // Count attendance statistics
        int presentCount = 0;
        int absentCount = 0;
        int leaveCount = 0;

        for (MarkAttendance student : studentList) {
            if (student.isPresent()) {
                presentCount++;
            } else if (student.isAbsent()) {
                absentCount++;
            } else if (student.isOnLeave()) {
                leaveCount++;
            }
        }

        // In a real app, you would save data to your database here
        // saveAttendanceToDatabase(studentList, selectedClass, selectedDate);

        // Show success message with statistics
        Toast.makeText(this,
                "Attendance saved successfully!\nPresent: " + presentCount +
                        ", Absent: " + absentCount +
                        ", Leave: " + leaveCount,
                Toast.LENGTH_LONG).show();

        // Return to previous screen
        finish();
    }

    @Override
    public void onAttendanceChanged(int position, int status) {
        // Update attendance statistics if needed
        updateAttendanceStats();
    }

    private void updateAttendanceStats() {
        int presentCount = 0;
        int absentCount = 0;
        int leaveCount = 0;
        int unmarkedCount = 0;

        for (MarkAttendance student : studentList) {
            if (student.isPresent()) {
                presentCount++;
            } else if (student.isAbsent()) {
                absentCount++;
            } else if (student.isOnLeave()) {
                leaveCount++;
            } else {
                unmarkedCount++;
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}