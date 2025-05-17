package com.school.schoolmanagement.Teachers.Test;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.school.schoolmanagement.R;
import com.school.schoolmanagement.Teachers.Adapters.AdapterTestMark;
import com.school.schoolmanagement.Teachers.Model.TestMarkModel;
import com.school.schoolmanagement.databinding.ActivityStudentsTestResultBinding;
import com.school.schoolmanagement.databinding.ActivityTestResultsBinding;

import java.util.ArrayList;
import java.util.List;

public class ActivityTestResults extends AppCompatActivity {
ActivityTestResultsBinding binding;
    private AdapterTestMark adapter;
    private List<TestMarkModel> testMarkList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding=ActivityTestResultsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initRecyclerView();
        loadSampleData();
    }

    private void initRecyclerView() {
        testMarkList = new ArrayList<>();
        adapter = new AdapterTestMark(testMarkList);
        binding.rcRowAttendanceReport.setAdapter(adapter);
    }

    private void loadSampleData() {
        // Add sample data for demonstration
        List<TestMarkModel> sampleData = new ArrayList<>();

        sampleData.add(new TestMarkModel("2025-04-01", "S001", "John Doe",
                "10-A", "Mathematics", 100, 85));
        sampleData.add(new TestMarkModel("2025-04-01", "S002", "Jane Smith",
                "10-A", "Mathematics", 100, 92));
        sampleData.add(new TestMarkModel("2025-04-02", "S003", "Robert Brown",
                "10-B", "Science", 100, 78));
        sampleData.add(new TestMarkModel("2025-04-02", "S004", "Emily Wilson",
                "10-B", "Science", 100, 88));

        // Update adapter with sample data
        adapter.updateList(sampleData);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}