package com.school.schoolmanagement.Admin.Students.StudentsIdCard;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.school.schoolmanagement.Admin.Adapter.AdapterIdCard;
import com.school.schoolmanagement.Admin.Model.IdCard;
import com.school.schoolmanagement.R;
import com.school.schoolmanagement.databinding.ActivityStudentsIdCardsBinding;

import java.util.ArrayList;
import java.util.List;

public class ActivityStudentsIdCards extends AppCompatActivity {
ActivityStudentsIdCardsBinding binding;
    private AdapterIdCard adapter;
    private List<IdCard> studentList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding=ActivityStudentsIdCardsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        studentList = new ArrayList<>();
        studentList.add(new IdCard("Aman Meraj", "CSE204045", "4th Year"));
        studentList.add(new IdCard("Rahul Singh", "CSE204046", "3rd Year"));
        studentList.add(new IdCard("Priya Sharma", "CSE204047", "2nd Year"));
        studentList.add(new IdCard("Vikash Yadav", "CSE204048", "1st Year"));

        adapter = new AdapterIdCard(studentList);
        binding.rcIdCards.setAdapter(adapter);
    }
}