package com.school.schoolmanagement.Admin.Classes.AllClasses;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.school.schoolmanagement.Admin.Adapter.AdapterAllClasses;
import com.school.schoolmanagement.Admin.Classes.NewClass.ActivityNewClass;
import com.school.schoolmanagement.Admin.Model.ClassModel;
import com.school.schoolmanagement.R;
import com.school.schoolmanagement.databinding.ActivityAllClassBinding;
import java.util.ArrayList;
import java.util.List;

public class ActivityAllClass extends AppCompatActivity {
    ActivityAllClassBinding binding;
    AdapterAllClasses adapter;
    List<ClassModel> classList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityAllClassBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.cardAddClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(ActivityAllClass.this, ActivityNewClass.class);
                startActivity(intent);
            }
        });

        // Create Sample Data
        classList = new ArrayList<>();
        classList.add(new ClassModel("Class 10", 50, 25, 20, 5));
        classList.add(new ClassModel("Class 12", 60, 0, 0, 60));

        // Set Adapter
        adapter = new AdapterAllClasses(this, classList, new AdapterAllClasses.OnItemClickListener() {
            @Override
            public void onEditClick(int position) {
                // Handle Edit Click
            }

            @Override
            public void onDeleteClick(int position) {
                new MaterialAlertDialogBuilder(ActivityAllClass.this)
                        .setTitle("Delete Class")
                        .setMessage("Are you sure you want to delete this class?")
                        .setPositiveButton("OK", (dialog, which) -> {
                            // Remove item from list and notify adapter
                            classList.remove(position);
                            adapter.notifyItemRemoved(position);
                            adapter.notifyItemRangeChanged(position, classList.size()); // Update indices
                        })
                        .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                        .show();
            }

        });

        binding.rcClasses.setAdapter(adapter);
    }
}
