package com.school.schoolmanagement.Admin.Classes.AllClasses;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.school.schoolmanagement.Admin.Adapter.AdapterAllClasses;
import com.school.schoolmanagement.Admin.Classes.NewClass.ActivityNewClass;
import com.school.schoolmanagement.Admin.Model.ClassModel;
import com.school.schoolmanagement.GlobalViewModel.ViewModel;

import com.school.schoolmanagement.Utils.Utility;
import com.school.schoolmanagement.databinding.ActivityAllClassBinding;

import java.util.ArrayList;

public class ActivityAllClass extends Utility {

    private ActivityAllClassBinding binding;
    private AdapterAllClasses adapter;
    private ViewModel viewModel;
    private ArrayList<ClassModel.Data> classList;
    private static final String TAG = "ActivityAllClass";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityAllClassBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.toolbar.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        classList = new ArrayList<>();
        viewModel = new ViewModelProvider(this).get(ViewModel.class);

        binding.cardAddClass.setOnClickListener(view -> {
            Intent intent = new Intent(ActivityAllClass.this, ActivityNewClass.class);
            startActivity(intent);
        });

if(isInternetConnected(this)){
    fetchClassData();
}else{
    showToast("No Internet Connection ! ");
}

    }

    private void fetchClassData() {
        showLoading(true);
        String auth= "Bearer "+pref.getPrefString(this,pref.user_token);
        viewModel.getAllClasses(auth).observe(this, response -> {
            showLoading(false);
            if (response != null ) {
                classList.clear();
                if(response.data!=null){
                    setAdapter(response.data.getData());
                    classList.addAll(response.data.getData());
                    adapter.notifyDataSetChanged();
                }else{
                    Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show();
                }

            } else {
                showToast("No classes found.");
            }
        });
    }

    private void setAdapter(ArrayList<ClassModel.Data> data) {

        adapter = new AdapterAllClasses(this,data, new AdapterAllClasses.OnItemClickListener() {
            @Override
            public void onEditClick(int position) {
                ClassModel.Data item = classList.get(position);

                new MaterialAlertDialogBuilder(ActivityAllClass.this)
                        .setTitle("Edit Class")
                        .setMessage("Are you sure you want to edit this class?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            Intent intent = new Intent(ActivityAllClass.this, ActivityNewClass.class);
                            intent.putExtra("isEditMode", true);
                            intent.putExtra("classData", classList.get(position)); // Serializable
                            // Make sure ClassModel.Data is Parcelable or Serializable
                            startActivity(intent);
                        })
                        .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                        .show();
            }


            @Override
            public void onDeleteClick(int position) {
                ClassModel.Data item = classList.get(position);
                new MaterialAlertDialogBuilder(ActivityAllClass.this)
                        .setTitle("Delete Class")
                        .setMessage("Are you sure you want to delete this class?")
                        .setPositiveButton("OK", (dialog, which) -> {
                            // Call the delete API
                            deleteClass(item.getClassId());
                        })
                        .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                        .show();
            }
        });

        binding.rcClasses.setAdapter(adapter);
    }

    private void deleteClass(int classId) {
        String auth = "Bearer " + pref.getPrefString(this, pref.user_token);

        viewModel.deleteClass(auth, classId).observe(this, response -> {
            if (response != null) {
                if (response.isSuccess) {
                    showToast("Class deleted successfully");
                    int position = findClassPositionById(classId);
                    if (position != -1) {
                        classList.remove(position);
                        adapter.notifyItemRemoved(position);
                        adapter.notifyItemRangeChanged(position, classList.size());
                    }
                } else {
                    showToast(response.message);
                }
            }
        });
    }


    private int findClassPositionById(int classId) {
        for (int i = 0; i < classList.size(); i++) {
            if (classList.get(i).getClassId() == classId) {
                return i;
            }
        }
        return -1;
    }

    private void showLoading(boolean isLoading) {
        Log.d(TAG, "Loading: " + isLoading);
        binding.loader.rlLoader.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }

    private void showToast(String message) {
        Log.d(TAG, "Toast: " + message);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
