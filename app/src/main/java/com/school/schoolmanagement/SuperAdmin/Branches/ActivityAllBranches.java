package com.school.schoolmanagement.SuperAdmin.Branches;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.school.schoolmanagement.R;
import com.school.schoolmanagement.SuperAdmin.Adapter.AdapterAllBranches;
import com.school.schoolmanagement.SuperAdmin.Model.ModelAllBranches;
import com.school.schoolmanagement.databinding.ActivityAllBranchesBinding;

import java.util.ArrayList;
import java.util.List;

public class ActivityAllBranches extends AppCompatActivity {
    ActivityAllBranchesBinding binding;
    private List<ModelAllBranches> branchesList;
    private AdapterAllBranches adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityAllBranchesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize UI components
        initUI();

        // Load data
        loadBranchesData();
    }

    private void initUI() {

        // Initialize RecyclerView
        branchesList = new ArrayList<>();
        adapter = new AdapterAllBranches(this, branchesList);
        binding.rcBranches.setAdapter(adapter);

        // Set click listener for adapter
        adapter.setOnBranchClickListener(new AdapterAllBranches.BranchClickListener() {
            @Override
            public void onBranchClick(ModelAllBranches branch, int position) {
                // Handle branch click event
                Toast.makeText(ActivityAllBranches.this,
                        "Clicked on " + branch.getSchoolName(),
                        Toast.LENGTH_SHORT).show();
                // You can navigate to branch details activity here
            }
        });

        // Add Branch Button
        binding.cardAddBranch.setOnClickListener(v -> {
            // Navigate to add branch activity

            // Intent intent = new Intent(ActivityAllBranches.this, ActivityAddBranch.class);
            // startActivity(intent);
            Toast.makeText(ActivityAllBranches.this, "Add Branch clicked", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadBranchesData() {
        // Show loading indicator
        binding.progressBar.setVisibility(View.VISIBLE);

        // For testing purposes, add some dummy data
        // In a real app, you would fetch data from your database (Firebase, etc.)
        addDummyData();

        // Remove loading indicator
        binding.progressBar.setVisibility(View.GONE);

        // Check if data is empty
        if (branchesList.isEmpty()) {
            binding.tvNoData.setVisibility(View.VISIBLE);
            binding.rcBranches.setVisibility(View.GONE);
        } else {
            binding.tvNoData.setVisibility(View.GONE);
            binding.rcBranches.setVisibility(View.VISIBLE);
        }
    }

    private void addDummyData() {
        // Add some test data
        branchesList.add(new ModelAllBranches("1", "Springfield High School", 345, "John Smith"));
        branchesList.add(new ModelAllBranches("2", "Riverside Academy", 567, "Mary Johnson"));
        branchesList.add(new ModelAllBranches("3", "Central School", 289, "Robert Williams"));
        branchesList.add(new ModelAllBranches("4", "Oak Park Institution", 420, "Sarah Brown"));
        branchesList.add(new ModelAllBranches("5", "Westview High", 510, "James Wilson"));

        // Notify adapter of data change
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data when coming back to this activity
        // loadBranchesData();
    }
}