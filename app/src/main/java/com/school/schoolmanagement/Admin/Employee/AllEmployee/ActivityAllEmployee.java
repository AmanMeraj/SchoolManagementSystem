package com.school.schoolmanagement.Admin.Employee.AllEmployee;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;

import com.school.schoolmanagement.Admin.Adapter.AdapterAllStudents;
import com.school.schoolmanagement.Admin.Model.StudentModel;
import com.school.schoolmanagement.R;
import com.school.schoolmanagement.databinding.ActivityAllEmployeeBinding;

import java.util.ArrayList;
import java.util.List;

public class ActivityAllEmployee extends AppCompatActivity implements AdapterAllStudents.OnStudentClickListener{
ActivityAllEmployeeBinding binding;
    private List<StudentModel> studentList;
    private AdapterAllStudents studentAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding=ActivityAllEmployeeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.searchBtn.setOnClickListener(new View.OnClickListener() {
            private boolean isOrange = false; // Flag to track the current color state

            @Override
            public void onClick(View view) {
                if (isOrange) {
                    binding.searchBtn.setColorFilter(null); // Reset to default color
                    binding.relSearch.setVisibility(View.GONE);
                } else {
                    binding.searchBtn.setColorFilter(getResources().getColor(R.color.orange)); // Set to orange
                    binding.relSearch.setVisibility(View.VISIBLE);
                }
                isOrange = !isOrange; // Toggle state
            }
        });

        // Initialize student list with dummy data
        studentList = new ArrayList<>();
        loadDummyStudents();

        // Set up RecyclerView
        studentAdapter = new AdapterAllStudents(this, studentList, this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        binding.rcAllStudents.setLayoutManager(gridLayoutManager);
        binding.rcAllStudents.setHasFixedSize(true);
        binding.rcAllStudents.setItemAnimator(new DefaultItemAnimator());

        binding.rcAllStudents.setAdapter(studentAdapter);
    }

    private void loadDummyStudents() {
        studentList.add(new StudentModel("John Doe", "Teacher"));
        studentList.add(new StudentModel("Alice Smith", "Principle"));
        studentList.add(new StudentModel("Bob Johnson", "Teacher"));
        studentList.add(new StudentModel("Emma Brown", "Cleaner"));
        studentList.add(new StudentModel("Liam Wilson", "Gardener"));
    }

    @Override
    public void onViewClick(StudentModel student) {
        // Handle view click (if needed)
    }

    @Override
    public void onEditClick(StudentModel student) {
        showEditDialog(student);
    }

    @Override
    public void onDeleteClick(StudentModel student) {
        showDeleteDialog(student);
    }

    private void showDeleteDialog(StudentModel student) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Student")
                .setMessage("Are you sure you want to delete this student?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    studentList.remove(student);
                    studentAdapter.notifyDataSetChanged();
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void showEditDialog(StudentModel student) {
        new AlertDialog.Builder(this)
                .setTitle("Edit Student")
                .setMessage("Are you sure you want to edit this student?")
                .setPositiveButton("Edit", (dialog, which) -> {
                    // Handle edit functionality (navigate to edit screen or show input dialog)
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }
}