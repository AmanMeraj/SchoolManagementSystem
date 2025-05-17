package com.school.schoolmanagement.Admin.Homework.homeworks;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.school.schoolmanagement.R;
import com.school.schoolmanagement.databinding.ActivityHomeworksBinding;

public class ActivityHomeworks extends AppCompatActivity {
ActivityHomeworksBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding=ActivityHomeworksBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.addHomeworkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddHomeworkDialog();
            }
        });



    }

    private void showAddHomeworkDialog() {
        Dialog dialog = new Dialog(ActivityHomeworks.this);
        dialog.setContentView(R.layout.add_homework_dialog);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // Initialize views from dialog
        RelativeLayout btnAddHomework = dialog.findViewById(R.id.btn_add_homework);
        AppCompatAutoCompleteTextView edtHomeworkDate = dialog.findViewById(R.id.edt_homework_date);

        // Set up date picker for homework date
        edtHomeworkDate.setOnClickListener(v -> {
            // Show date picker
        });

        // Set on click listener for add button
        btnAddHomework.setOnClickListener(v -> {
            // Add homework logic
            dialog.dismiss();
        });

        dialog.show();
    }
}