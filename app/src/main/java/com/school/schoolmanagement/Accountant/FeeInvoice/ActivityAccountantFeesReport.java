package com.school.schoolmanagement.Accountant.FeeInvoice;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.school.schoolmanagement.Admin.Adapter.AdapterFeesReport;
import com.school.schoolmanagement.Admin.Model.FeesReportModel;
import com.school.schoolmanagement.R;
import com.school.schoolmanagement.databinding.ActivityAccountantFeesReportBinding;

import java.util.ArrayList;
import java.util.List;

public class ActivityAccountantFeesReport extends AppCompatActivity {
    ActivityAccountantFeesReportBinding binding;
    private AdapterFeesReport adapter;
    private List<FeesReportModel> feesReportList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding=ActivityAccountantFeesReportBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        feesReportList = new ArrayList<>();
        adapter = new AdapterFeesReport(this, feesReportList);
        binding.rcFeesReport.setAdapter(adapter);
    }
}