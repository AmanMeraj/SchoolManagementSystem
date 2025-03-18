package com.school.schoolmanagement.Admin.Subjects.ClassesWithSubjects;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.school.schoolmanagement.Admin.Adapter.ClassesWithSubjectsParentAdapter;
import com.school.schoolmanagement.Admin.Model.ClassesWithSubjectParentModel;
import com.school.schoolmanagement.R;
import com.school.schoolmanagement.databinding.ActivityClassesWithSubjectsBinding;

import java.util.ArrayList;
import java.util.List;

public class ActivityClassesWithSubjects extends AppCompatActivity {
ActivityClassesWithSubjectsBinding binding;
    private List<ClassesWithSubjectParentModel> classList;
ClassesWithSubjectsParentAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding=ActivityClassesWithSubjectsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        loadDummyData();

        adapter = new ClassesWithSubjectsParentAdapter(this, classList);
        binding.rcClasses.setAdapter(adapter);
    }
    private void loadDummyData() {
        classList = new ArrayList<>();
        classList.add(new ClassesWithSubjectParentModel("Class 10", 5, 400, 500));
        classList.add(new ClassesWithSubjectParentModel("Class 9", 6, 350, 500));
    }
}