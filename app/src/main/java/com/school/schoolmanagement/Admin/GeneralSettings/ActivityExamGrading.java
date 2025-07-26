package com.school.schoolmanagement.Admin.GeneralSettings;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.school.schoolmanagement.Adapters.GradingAdapter;
import com.school.schoolmanagement.GlobalViewModel.ViewModel;
import com.school.schoolmanagement.Model.GradingItem;
import com.school.schoolmanagement.R;
import com.school.schoolmanagement.databinding.ActivityFeespaticularsBinding;

import java.util.ArrayList;
import java.util.List;

public class ActivityExamGrading extends AppCompatActivity {

    private RecyclerView recyclerGrading;
    private GradingAdapter gradingAdapter;
    private List<GradingItem> gradingItems;
    private RelativeLayout btnAddMore, btnRemove, btnSaveChanges;
    private ImageView btnBack;
    private TextView tabMarkGrading, tabFailCriteria;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_grading);
        EdgeToEdge.enable(this);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setupRecyclerView();
        setupClickListeners();
        setupTabs();
    }

    private void initViews() {
        recyclerGrading = findViewById(R.id.recycler_grading);
        btnAddMore = findViewById(R.id.btn_add_more);
        btnRemove = findViewById(R.id.btn_remove);
        btnSaveChanges = findViewById(R.id.btn_save_changes);
        btnBack = findViewById(R.id.btn_back);
        tabMarkGrading = findViewById(R.id.tab_mark_grading);
        tabFailCriteria = findViewById(R.id.tab_fail_criteria);
    }

    private void setupRecyclerView() {
        // Initialize with default grading items
        gradingItems = new ArrayList<>();
        gradingItems.add(new GradingItem("A+", "80", "100", "PASS"));
        gradingItems.add(new GradingItem("A", "70", "79", "PASS"));
        gradingItems.add(new GradingItem("B+", "60", "69", "PASS"));
        gradingItems.add(new GradingItem("B", "50", "59", "PASS"));
        gradingItems.add(new GradingItem("C+", "40", "49", "PASS"));
        gradingItems.add(new GradingItem("C", "35", "39", "PASS"));
        gradingItems.add(new GradingItem("F", "0", "34", "FAIL"));

        gradingAdapter = new GradingAdapter(gradingItems);
        recyclerGrading.setLayoutManager(new LinearLayoutManager(this));
        recyclerGrading.setAdapter(gradingAdapter);
    }

    private void setupClickListeners() {
        btnAddMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gradingAdapter.addItem();
                recyclerGrading.scrollToPosition(gradingAdapter.getItemCount() - 1);
            }
        });

        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gradingItems.size() > 1) {
                    gradingAdapter.removeLastItem();
                } else {
                    Toast.makeText(ActivityExamGrading.this, "At least one grading item is required", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnSaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveGradingData();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void setupTabs() {
        tabMarkGrading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectTab(true);
            }
        });

        tabFailCriteria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectTab(false);
                // Navigate to fail criteria screen
                Toast.makeText(ActivityExamGrading.this, "Fail Criteria tab selected", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void selectTab(boolean isMarkGrading) {
        if (isMarkGrading) {
            tabMarkGrading.setBackgroundResource(R.drawable.button_bg_sm);
            tabMarkGrading.setTextColor(getResources().getColor(R.color.white));

            tabFailCriteria.setBackgroundResource(R.drawable.bg_day_unselected);
            tabFailCriteria.setTextColor(getResources().getColor(R.color.grey));
        } else {
            tabFailCriteria.setBackgroundResource(R.drawable.button_bg_sm);
            tabFailCriteria.setTextColor(getResources().getColor(R.color.white));

            tabMarkGrading.setBackgroundResource(R.drawable.bg_day_unselected);
            tabMarkGrading.setTextColor(getResources().getColor(R.color.grey));
        }
    }

    private void saveGradingData() {
        // Validate grading data before saving
        if (validateGradingData()) {
            // Save to database or preferences
            // This is where you would implement your data persistence logic
            Toast.makeText(this, "Grading data saved successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Please check your grading data for errors", Toast.LENGTH_LONG).show();
        }
    }

    private boolean validateGradingData() {
        if (gradingItems.isEmpty()) {
            Toast.makeText(this, "At least one grading item is required", Toast.LENGTH_SHORT).show();
            return false;
        }

        for (int i = 0; i < gradingItems.size(); i++) {
            GradingItem item = gradingItems.get(i);

            // Check if grade is empty
            if (item.getGrade().trim().isEmpty()) {
                Toast.makeText(this, "Grade cannot be empty at position " + (i + 1), Toast.LENGTH_SHORT).show();
                return false;
            }

            // Check if min and max marks are valid numbers
            try {
                int minMarks = Integer.parseInt(item.getPercentUpto());
                int maxMarks = Integer.parseInt(item.getPercentFrom());

                if (minMarks < 0 || maxMarks < 0) {
                    Toast.makeText(this, "Marks cannot be negative at position " + (i + 1), Toast.LENGTH_SHORT).show();
                    return false;
                }

                if (minMarks > maxMarks) {
                    Toast.makeText(this, "Minimum marks cannot be greater than maximum marks at position " + (i + 1), Toast.LENGTH_SHORT).show();
                    return false;
                }

                if (maxMarks > 100) {
                    Toast.makeText(this, "Maximum marks cannot exceed 100 at position " + (i + 1), Toast.LENGTH_SHORT).show();
                    return false;
                }

            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid marks format at position " + (i + 1), Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        // Check for overlapping ranges
        for (int i = 0; i < gradingItems.size(); i++) {
            GradingItem item1 = gradingItems.get(i);
            int min1 = Integer.parseInt(item1.getPercentFrom());
            int max1 = Integer.parseInt(item1.getPercentUpto());

            for (int j = i + 1; j < gradingItems.size(); j++) {
                GradingItem item2 = gradingItems.get(j);
                int min2 = Integer.parseInt(item2.getPercentFrom());
                int max2 = Integer.parseInt(item2.getPercentUpto());

                // Check if ranges overlap
                if ((min1 <= max2 && max1 >= min2)) {
                    Toast.makeText(this, "Overlapping ranges found between positions " + (i + 1) + " and " + (j + 1), Toast.LENGTH_LONG).show();
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up resources if needed
    }
}