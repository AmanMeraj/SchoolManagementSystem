package com.school.schoolmanagement.Students.TestResult;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.school.schoolmanagement.R;
import com.school.schoolmanagement.Students.Adapter.AdapterStudentTest;
import com.school.schoolmanagement.Students.Model.StudentTestModel;
import com.school.schoolmanagement.databinding.ActivityStudentsExamResultBinding;
import com.school.schoolmanagement.databinding.ActivityStudentsTestResultBinding;

import java.util.ArrayList;
import java.util.List;

public class ActivityStudentsTestResult extends AppCompatActivity {
    ActivityStudentsTestResultBinding binding;

    private List<StudentTestModel> testList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding= ActivityStudentsTestResultBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        testList = new ArrayList<>();
        testList.add(new StudentTestModel("12/04/2025", "Year 1", "Maths", "100", "89"));
        testList.add(new StudentTestModel("15/04/2025", "Year 1", "Science", "100", "85"));
        // Add more as needed

        AdapterStudentTest adapter = new AdapterStudentTest(this, testList);
        binding.rcTestResult.setAdapter(adapter);
    }
}