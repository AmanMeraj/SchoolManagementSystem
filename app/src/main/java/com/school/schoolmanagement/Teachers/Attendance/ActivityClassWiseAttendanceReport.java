package com.school.schoolmanagement.Teachers.Attendance;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.school.schoolmanagement.R;
import com.school.schoolmanagement.Teachers.Adapters.AdapterAllClassesAttendancereport;
import com.school.schoolmanagement.Teachers.Model.AllClassesAttendancereport;
import com.school.schoolmanagement.databinding.ActivityClassWiseAttendanceReportBinding;

import java.util.ArrayList;
import java.util.List;

public class ActivityClassWiseAttendanceReport extends AppCompatActivity {
ActivityClassWiseAttendanceReportBinding binding;
    private AdapterAllClassesAttendancereport adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding=ActivityClassWiseAttendanceReportBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        setupRecyclerView();
        loadAttendanceData();
    }


    private void setupRecyclerView() {
        adapter = new AdapterAllClassesAttendancereport(new ArrayList<>());
        binding.rcAllClassAttendanceReport.setAdapter(adapter);
    }

    private void loadAttendanceData() {
        // Example data - replace with your actual data loading logic
        List<AllClassesAttendancereport> reportList = getSampleData();
        adapter.updateData(reportList);
    }

    private List<AllClassesAttendancereport> getSampleData() {
        List<AllClassesAttendancereport> reports = new ArrayList<>();

        // Add sample data
        reports.add(new AllClassesAttendancereport(
                "22 Mar, 2025",
                "Year 1",
                20,
                45,
                15,
                35,
                8,
                20
        ));
        reports.add(new AllClassesAttendancereport(
                "22 Mar, 2025",
                "Year 2",
                20,
                45,
                15,
                35,
                8,
                20
        ));
        reports.add(new AllClassesAttendancereport(
                "22 Mar, 2025",
                "Year 3",
                20,
                45,
                15,
                35,
                8,
                20
        ));

        // Add more sample data as needed

        return reports;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}