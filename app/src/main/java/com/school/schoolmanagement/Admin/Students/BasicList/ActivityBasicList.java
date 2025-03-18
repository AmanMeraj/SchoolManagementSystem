package com.school.schoolmanagement.Admin.Students.BasicList;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.school.schoolmanagement.Admin.Adapter.AdapterBasicList;
import com.school.schoolmanagement.Admin.Model.BasicList;
import com.school.schoolmanagement.R;
import com.school.schoolmanagement.databinding.ActivityBasicListBinding;

import java.util.ArrayList;
import java.util.List;

public class ActivityBasicList extends AppCompatActivity {
ActivityBasicListBinding binding;
    private List<BasicList> studentList;
    private AdapterBasicList adapterStudent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding=ActivityBasicListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        studentList = new ArrayList<>();
        loadStudents();  // Load dummy data

        adapterStudent = new AdapterBasicList(this, studentList);
        binding.rcPromoteStudent.setAdapter(adapterStudent);
    }
    private void loadStudents() {
        studentList.add(new BasicList("1", "John Doe", "10A", "10", "9876543210"));
        studentList.add(new BasicList("2", "Jane Smith", "9B", "15", "9876543221"));
        studentList.add(new BasicList("3", "Alice Brown", "8C", "20", "9876543232"));
        studentList.add(new BasicList("4", "Michael Lee", "7D", "30", "9876543243"));
    }
}