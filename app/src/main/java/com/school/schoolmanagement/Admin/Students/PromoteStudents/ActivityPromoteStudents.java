package com.school.schoolmanagement.Admin.Students.PromoteStudents;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.school.schoolmanagement.Admin.Adapter.AdapterPromoteStudents;
import com.school.schoolmanagement.Admin.Model.PromoteStudents;
import com.school.schoolmanagement.R;
import com.school.schoolmanagement.databinding.ActivityPromoteStudentsBinding;

import java.util.ArrayList;
import java.util.List;

public class ActivityPromoteStudents extends AppCompatActivity {
ActivityPromoteStudentsBinding binding;
    AdapterPromoteStudents adapter;
    List<PromoteStudents> studentList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityPromoteStudentsBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        studentList = new ArrayList<>();
        studentList.add(new PromoteStudents("65261601", "John Doe", "Year 1"));
        studentList.add(new PromoteStudents("65261602", "Jane Smith", "Year 1"));
        studentList.add(new PromoteStudents("65261603", "Michael Johnson", "Year 1"));
        studentList.add(new PromoteStudents("65261604", "Emily Davis", "Year 1"));
        studentList.add(new PromoteStudents("65261605", "Chris Brown", "Year 1"));

        adapter = new AdapterPromoteStudents(studentList);
        binding.rcPromoteStudent.setAdapter(adapter);

        // Handle "Select All" Checkbox
        binding.selectAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
            adapter.selectAll(isChecked);
        });
    }

}