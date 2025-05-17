package com.school.schoolmanagement.Admin.Salary.Report;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.school.schoolmanagement.Admin.Adapter.AdapterFeesReport;
import com.school.schoolmanagement.Admin.Adapter.AdapterSalaryReport;
import com.school.schoolmanagement.Admin.Model.FeesReportModel;
import com.school.schoolmanagement.Admin.Model.SalaryReportModel;
import com.school.schoolmanagement.R;
import com.school.schoolmanagement.databinding.ActivitySalaryReportBinding;

import java.util.ArrayList;
import java.util.List;

public class ActivitySalaryReport extends AppCompatActivity {
ActivitySalaryReportBinding binding;
    private AdapterSalaryReport adapter;
    private List<SalaryReportModel> salaryReportList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding=ActivitySalaryReportBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        salaryReportList = new ArrayList<>();
        loadData();
        adapter = new AdapterSalaryReport(this, salaryReportList);
        binding.rcFeesReport.setAdapter(adapter);
    }

    private void loadData() {
        salaryReportList.add(new SalaryReportModel("1", "Aman Meraj", "2025-04-10", "$50", "Paid"));
        salaryReportList.add(new SalaryReportModel("2", "John Doe", "2025-04-12", "$100", "Unpaid"));
        salaryReportList.add(new SalaryReportModel("3", "Jane Smith", "2025-04-15", "$75", "Paid"));
    }
}