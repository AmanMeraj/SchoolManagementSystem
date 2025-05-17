package com.school.schoolmanagement.Students.ExamResult;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.school.schoolmanagement.R;
import com.school.schoolmanagement.databinding.ActivityStudentsExamResultBinding;
import com.school.schoolmanagement.databinding.RowFeesInfoBinding;

public class ActivityStudentsExamResult extends AppCompatActivity {
ActivityStudentsExamResultBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding=ActivityStudentsExamResultBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.rowId.label.setText("Registration/ID");
        binding.rowId.value.setText("615384362");

        binding.rowClass.label.setText("Class");
        binding.rowClass.value.setText("Year 1");

        binding.rowName.label.setText("Name");
        binding.rowName.value.setText("John Doe");

        binding.rowExamCount.label.setText("Exam Counts");
        binding.rowExamCount.value.setText("6");

        binding.rowTotalMarks.label.setText("Total Marks");
        binding.rowTotalMarks.value.setText("10035");

        binding.rowObtainedMarks.label.setText("Obtained Marks");
        binding.rowObtainedMarks.value.setText("845");

        binding.rowPercentage.label.setText("Percentage");
        binding.rowPercentage.value.setText("86%");
    }


}