package com.school.schoolmanagement.Teachers.QuestionBank;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.school.schoolmanagement.R;
import com.school.schoolmanagement.Teachers.Adapters.AdapterQuestionPaper;
import com.school.schoolmanagement.Teachers.Model.ModelQuestionPaper;
import com.school.schoolmanagement.databinding.ActivityCreateQuestionPaperBinding;

import java.util.ArrayList;
import java.util.List;

public class ActivityCreateQuestionPaper extends AppCompatActivity implements AdapterQuestionPaper.OnItemClickListener{
ActivityCreateQuestionPaperBinding binding;

    private AdapterQuestionPaper adapter;
    private List<ModelQuestionPaper> questionPaperList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding=ActivityCreateQuestionPaperBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        questionPaperList = new ArrayList<>();

        // Add sample data (replace with your actual data source)
        loadSampleData();

        // Set up adapter
        adapter = new AdapterQuestionPaper(this, questionPaperList);
        adapter.setOnItemClickListener(this);
        binding.rcRowAttendanceReport.setAdapter(adapter);
    }

    private void loadSampleData() {
        // Add sample data for testing
        questionPaperList.add(new ModelQuestionPaper(1, "Mid Term", "Class 10", "Physics", "15/04/2025", "2 Hours"));
        questionPaperList.add(new ModelQuestionPaper(2, "Final Exam", "Class 12", "Chemistry", "20/04/2025", "3 Hours"));
        questionPaperList.add(new ModelQuestionPaper(3, "Quiz 3", "Class 9", "Mathematics", "25/04/2025", "1 Hour"));
        questionPaperList.add(new ModelQuestionPaper(4, "Unit Test", "Class 11", "Biology", "30/04/2025", "1.5 Hours"));

        // In a real app, you would fetch this data from a database or API
    }

    private void openCreateQuestionPaperDialog() {
        // Open your create question paper dialog or navigate to create question paper activity
        // For example:
        // Intent intent = new Intent(this, CreateQuestionPaperActivity.class);
        // startActivity(intent);
        Toast.makeText(this, "Add new question paper clicked", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClick(int position) {
        // Handle item click
        ModelQuestionPaper selectedPaper = questionPaperList.get(position);
        Toast.makeText(this, "Clicked: " + selectedPaper.getTitle(), Toast.LENGTH_SHORT).show();

        // You can navigate to question paper details screen here
    }

    @Override
    public void onEditClick(int position) {
        // Handle edit click
        ModelQuestionPaper selectedPaper = questionPaperList.get(position);
        Toast.makeText(this, "Edit: " + selectedPaper.getTitle(), Toast.LENGTH_SHORT).show();

        // You can navigate to edit question paper screen here
    }

    @Override
    public void onDeleteClick(int position) {
        // Handle delete click with confirmation dialog
        ModelQuestionPaper selectedPaper = questionPaperList.get(position);

        new AlertDialog.Builder(this)
                .setTitle("Delete Question Paper")
                .setMessage("Are you sure you want to delete " + selectedPaper.getTitle() + "?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    // Remove from list and notify adapter
                    questionPaperList.remove(position);
                    adapter.notifyItemRemoved(position);
                    Toast.makeText(this, "Question paper deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}