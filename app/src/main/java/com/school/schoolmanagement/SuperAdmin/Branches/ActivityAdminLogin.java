package com.school.schoolmanagement.SuperAdmin.Branches;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.school.schoolmanagement.R;
import com.school.schoolmanagement.SuperAdmin.Adapter.AdapterSearchSubAdmin;
import com.school.schoolmanagement.SuperAdmin.Model.SearchSubAdminModel;
import com.school.schoolmanagement.databinding.ActivityAdminLoginBinding;


import java.util.ArrayList;
import java.util.List;

public class ActivityAdminLogin extends AppCompatActivity implements AdapterSearchSubAdmin.SubAdminListener {

    private ActivityAdminLoginBinding binding;
    private AdapterSearchSubAdmin adapter;
    private List<SearchSubAdminModel> subAdminList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminLoginBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        subAdminList = new ArrayList<>();

        // Load sample data (replace with your data fetching logic)
        loadSampleData();

        // Initialize adapter
        adapter = new AdapterSearchSubAdmin(this, subAdminList, this);
        binding.rcRowInsertMarks.setAdapter(adapter);

    }

    // Sample data method - replace with your actual data loading method
    private void loadSampleData() {
        subAdminList.add(new SearchSubAdminModel("B001", "Main Branch", "John Doe", "admin1", "pass123"));
        subAdminList.add(new SearchSubAdminModel("B002", "Downtown", "Jane Smith", "admin2", "secure456"));
        subAdminList.add(new SearchSubAdminModel("B003", "Westside", "Robert Johnson", "admin3", "pass789"));
    }

    // Implement SubAdminListener methods
    @Override
    public void onEditClicked(SearchSubAdminModel subAdmin, int position) {
        // Handle edit action
        // For example, open a dialog to edit all fields
        Toast.makeText(this, "Edit clicked for " + subAdmin.getBranchName(), Toast.LENGTH_SHORT).show();

        // Here you could open a dialog with all fields for editing
        showEditDialog(subAdmin, position);
    }

    @Override
    public void onDeleteClicked(SearchSubAdminModel subAdmin, int position) {
        // Show confirmation dialog
        new AlertDialog.Builder(this)
                .setTitle("Delete User")
                .setMessage("Are you sure you want to delete " + subAdmin.getUserName() + "?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    // Delete from database (add your database code here)

                    // Remove from adapter
                    adapter.removeItem(position);

                    Toast.makeText(this, "User deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onUserNameChanged(SearchSubAdminModel subAdmin, String userName, int position) {
        // Handle username change
        // You might want to update your database here
        subAdmin.setUserName(userName);
    }

    @Override
    public void onPasswordChanged(SearchSubAdminModel subAdmin, String password, int position) {
        // Handle password change
        // You might want to update your database here
        subAdmin.setPassword(password);
    }

    // Method to show edit dialog
    private void showEditDialog(SearchSubAdminModel subAdmin, int position) {
        // Create your custom dialog here
        // This is just a skeleton example
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Sub Admin");

        // You would typically inflate a custom layout here using binding
        // View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_sub_admin, null);
        // builder.setView(dialogView);

        builder.setPositiveButton("Save", (dialog, which) -> {
            // Save changes to database

            // Update adapter
            adapter.notifyItemChanged(position);

            Toast.makeText(this, "Changes saved", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Cancel", null);

        builder.show();
    }

    // Method to add new sub admin
//    private void addNewSubAdmin() {
//        // Logic to add a new sub admin
//
//        SearchSubAdminModel newUser = new SearchSubAdminModel(
//                "B" + String.format("%03d", subAdminList.size() + 1),
//                "New Branch",
//                "New Admin",
//                "",
//                ""
//        );
//
//        adapter.addItem(newUser);
//
//        // Scroll to the new item
//        binding.rc.smoothScrollToPosition(subAdminList.size() - 1);
//    }
}
