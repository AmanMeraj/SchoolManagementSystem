package com.school.schoolmanagement.Admin.Employee.ManageLogin;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.school.schoolmanagement.Admin.Adapter.AdapterStudentPassword;
import com.school.schoolmanagement.Admin.Model.StudentPassword;
import com.school.schoolmanagement.R;
import com.school.schoolmanagement.databinding.ActivityStaffLoginBinding;

import java.util.ArrayList;
import java.util.List;

public class ActivityStaffLogin extends AppCompatActivity {
ActivityStaffLoginBinding binding;
    private List<StudentPassword> userList;
    private AdapterStudentPassword userAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding=ActivityStaffLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        userList = new ArrayList<>();
        userList.add(new StudentPassword("1", "John Doe", "10", "john123", "pass123"));
        userList.add(new StudentPassword("2", "Jane Smith", "9", "jane456", "pass456"));
        userList.add(new StudentPassword("3", "Mike Johnson", "11", "mike789", "pass789"));
        userList.add(new StudentPassword("1", "John Doe", "10", "john123", "pass123"));
        userList.add(new StudentPassword("2", "Jane Smith", "9", "jane456", "pass456"));
        userList.add(new StudentPassword("3", "Mike Johnson", "11", "mike789", "pass789"));

        // Setup RecyclerView
        userAdapter = new AdapterStudentPassword(this, userList);
        binding.rcPromoteStudent.setAdapter(userAdapter);
    }
}