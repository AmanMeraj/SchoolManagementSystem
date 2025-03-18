package com.school.schoolmanagement.Admin.Employee.StaffIdCard;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.school.schoolmanagement.Admin.Adapter.AdapterIdCard;
import com.school.schoolmanagement.Admin.Model.IdCard;
import com.school.schoolmanagement.R;
import com.school.schoolmanagement.databinding.ActivityStaffIdCardBinding;

import java.util.ArrayList;
import java.util.List;

public class ActivityStaffIdCard extends AppCompatActivity {

    ActivityStaffIdCardBinding binding;
    private AdapterIdCard adapter;
    private List<IdCard> studentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding=ActivityStaffIdCardBinding.inflate(getLayoutInflater()) ;
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        studentList = new ArrayList<>();
        studentList.add(new IdCard("Teacher", "Farhan", "Burnpur Riverside School"));
        studentList.add(new IdCard("Teacher", "Arbaz", "Asansol Ag Church"));
        studentList.add(new IdCard("Cleaner", "sophia", "Burnpur Riverside School"));
        studentList.add(new IdCard("Head Master", "Aman", "Burnpur Riverside School"));

        adapter = new AdapterIdCard(studentList);
        binding.rcIdCards.setAdapter(adapter);
    }
}