package com.school.schoolmanagement.Admin.Exams.UpdateInfo;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.school.schoolmanagement.Admin.Adapter.StudentRowAdapter;
import com.school.schoolmanagement.Admin.Adapter.SubjectHeaderAdapter;
import com.school.schoolmanagement.Admin.Model.StudentInsertMarks;
import com.school.schoolmanagement.Admin.Model.Subject;
import com.school.schoolmanagement.R;
import com.school.schoolmanagement.databinding.ActivityInsertMarksBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityInsertMarks extends AppCompatActivity implements StudentRowAdapter.MarksChangedListener{
ActivityInsertMarksBinding binding;
    private RecyclerView rcSubjects;
    private RecyclerView rcStudents;
    private List<Subject> subjects;
    private List<StudentInsertMarks> students;

    private Map<String, Map<String, String>> marksData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding=ActivityInsertMarksBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize the marks data structure
        marksData = new HashMap<>();

        // Initialize sample data
        initSampleData();

        // Set up subject headers adapter
        SubjectHeaderAdapter subjectAdapter = new SubjectHeaderAdapter(subjects);
        binding.rcSubjects.setAdapter(subjectAdapter);

        // Set up student rows adapter
        StudentRowAdapter studentAdapter = new StudentRowAdapter(students, subjects, this);
        binding.rcRowInsertMarks.setAdapter(studentAdapter);


//        // Set up save button click listener
//        binding.tvAddUpdate.setOnClickListener(v -> onSaveButtonClick(v));
    }

    private void initSampleData() {
        // Sample subjects
        subjects = new ArrayList<>();
        subjects.add(new Subject("SUB1", "Math"));
        subjects.add(new Subject("SUB2", "Science"));
        subjects.add(new Subject("SUB3", "English"));
        subjects.add(new Subject("SUB4", "History"));
        subjects.add(new Subject("SUB5", "Geography"));

        // Sample students
        students = new ArrayList<>();
        students.add(new StudentInsertMarks("10001", "John Smith", "Mary Smith"));
        students.add(new StudentInsertMarks("10002", "Emma Johnson", "Robert Johnson"));
        students.add(new StudentInsertMarks("10003", "Michael Brown", "Patricia Brown"));

    }

    @Override
    public void onMarkChanged(StudentInsertMarks student, String subjectId, String mark) {
        // This gets called whenever a mark is changed
        // You can implement logic to save to database or update totals
        Log.d("Marks", "Student " + student.getName() +
                " got " + mark + " in subject " + subjectId);
    }

    // Method to save all marks to your database
    private void saveAllMarks() {
        for (StudentInsertMarks student : students) {
            String studentId = student.getId();
            HashMap<String, String> allMarks = student.getAllSubjects();

            for (Map.Entry<String, String> entry : allMarks.entrySet()) {
                String subjectId = entry.getKey();
                String mark = entry.getValue();

                // Save to database
                // saveMarkToDatabase(studentId, subjectId, mark);
            }
        }
    }
}