package com.school.schoolmanagement.Admin.QuestionPaper;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.school.schoolmanagement.Adapters.ChapterAdapter;
import com.school.schoolmanagement.Admin.Model.ClassModel;
import com.school.schoolmanagement.Admin.Model.SubjectModel;
import com.school.schoolmanagement.HelperClasses.DataExportHelper;
import com.school.schoolmanagement.Model.AddChapter;
import com.school.schoolmanagement.Model.ChapterResponse;
import com.school.schoolmanagement.HelperClasses.ChapterApiHelper;
import com.school.schoolmanagement.HelperClasses.ClassApiHelper;
import com.school.schoolmanagement.HelperClasses.SubjectApiHelper;
import com.school.schoolmanagement.R;
import com.school.schoolmanagement.databinding.ActivityAddSubjectChapterBinding;

import java.util.ArrayList;

public class ActivityAddSubjectChapter extends AppCompatActivity {

    private ActivityAddSubjectChapterBinding binding;

    // Helper classes
    private ClassApiHelper classApiHelper;
    private SubjectApiHelper subjectApiHelper;
    private ChapterApiHelper chapterApiHelper;

    // Data lists
    private ArrayList<ClassModel.Data> classList = new ArrayList<>();
    private ArrayList<SubjectModel> subjectList = new ArrayList<>();
    private ArrayList<ChapterResponse.Datum> chapterList = new ArrayList<>();
    private ArrayList<ChapterResponse.Datum> filteredChapterList = new ArrayList<>();

    // Name lists for adapters
    private ArrayList<String> classNames = new ArrayList<>();
    private ArrayList<String> subjectNames = new ArrayList<>();

    // Adapters
    private ArrayAdapter<String> classAdapter;
    private ArrayAdapter<String> subjectAdapter;
    private ChapterAdapter chapterAdapter;

    // Selected values
    private int selectedClassId = -1;
    private int selectedSubjectId = -1;

    // Edit mode
    private boolean isEditMode = false;
    private int editingChapterId = -1;
    private int editingPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityAddSubjectChapterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeHelpers();
        setupUI();
        loadInitialData();
    }

    private void initializeHelpers() {
        classApiHelper = new ClassApiHelper(this);
        subjectApiHelper = new SubjectApiHelper(this);
        chapterApiHelper = new ChapterApiHelper(this);
    }

    private void setupUI() {
        setupDropdowns();
        setupRecyclerView();
        setupButtons();
        setupSearch();
        setupExportButtons();
    }

    private void setupDropdowns() {
        setupClassDropdown();
        setupSubjectDropdown();
    }

    private void setupClassDropdown() {
        // Initialize with empty list first
        classNames.clear();
        classNames.add("Select Class");

        classAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, classNames);
        classAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.edtClass.setAdapter(classAdapter);

        // Set threshold to show dropdown on click
        binding.edtClass.setThreshold(1);

        binding.edtClass.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) { // Skip "Select Class" option
                    // Adjust position since we have "Select Class" at index 0
                    int actualPosition = position - 1;
                    if (actualPosition < classList.size()) {
                        selectedClassId = classList.get(actualPosition).getClassId();
                        loadSubjectsForClass(selectedClassId);

                        // Reset subject selection
                        selectedSubjectId = -1;
                        binding.edtHeadType.setText("", false);

                        // Clear chapters
                        clearChapters();
                    }
                } else {
                    selectedClassId = -1;
                    clearSubjects();
                    clearChapters();
                }
            }
        });

        // Handle focus change to show dropdown
        binding.edtClass.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                binding.edtClass.showDropDown();
            }
        });

        // Handle click to show dropdown
        binding.edtClass.setOnClickListener(v -> {
            binding.edtClass.showDropDown();
        });
    }

    private void setupSubjectDropdown() {
        subjectNames.clear();
        subjectNames.add("Select Subject");

        subjectAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, subjectNames);
        subjectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.edtHeadType.setAdapter(subjectAdapter);

        // Set threshold to show dropdown on click
        binding.edtHeadType.setThreshold(1);

        // Initially disable subject dropdown
        binding.edtHeadType.setEnabled(false);

        binding.edtHeadType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) { // Skip "Select Subject" option
                    // Adjust position since we have "Select Subject" at index 0
                    int actualPosition = position - 1;
                    if (actualPosition < subjectList.size()) {
                        selectedSubjectId = subjectList.get(actualPosition).getId();
                        loadChaptersForSubject(selectedSubjectId);
                    }
                } else {
                    selectedSubjectId = -1;
                    clearChapters();
                }
            }
        });

        // Handle focus change to show dropdown
        binding.edtHeadType.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus && binding.edtHeadType.isEnabled()) {
                binding.edtHeadType.showDropDown();
            }
        });

        // Handle click to show dropdown
        binding.edtHeadType.setOnClickListener(v -> {
            if (binding.edtHeadType.isEnabled()) {
                binding.edtHeadType.showDropDown();
            }
        });
    }

    private void setupRecyclerView() {
        chapterAdapter = new ChapterAdapter(filteredChapterList, new ChapterAdapter.OnChapterClickListener() {
            @Override
            public void onChapterEditClick(ChapterResponse.Datum chapter, int position) {
                enterEditMode(chapter, position);
            }

            @Override
            public void onChapterDeleteClick(ChapterResponse.Datum chapter, int position) {
                showDeleteConfirmationDialog(chapter, position);
            }
        });

        binding.rcStatement.setLayoutManager(new LinearLayoutManager(this));
        binding.rcStatement.setAdapter(chapterAdapter);
    }

    private void setupButtons() {
        // Add/Save button
        binding.btnSaveHead.setOnClickListener(v -> {
            if (isEditMode) {
                updateChapter();
            } else {
                addChapter();
            }
        });

        // Edit button
        binding.btnEdit.setOnClickListener(v -> {
            updateChapter();
        });

        // Cancel button
        binding.btnCancel.setOnClickListener(v -> {
            cancelEdit();
        });
    }

    private void setupSearch() {
        binding.edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterChapters(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void loadInitialData() {
        loadClasses();
    }

    private void loadClasses() {
        classApiHelper.fetchAllClasses(new ClassApiHelper.ClassListCallback() {
            @Override
            public void onSuccess(ArrayList<ClassModel.Data> classes) {
                classList.clear();
                classList.addAll(classes);

                classNames.clear();
                classNames.add("Select Class");

                // Add class names to the list
                for (ClassModel.Data classData : classList) {
                    classNames.add(classData.getClassName());
                }

                classAdapter.notifyDataSetChanged();

                // Reset subject dropdown when classes are loaded
                clearSubjects();
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(ActivityAddSubjectChapter.this, "Error loading classes: " + errorMessage, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLoading(boolean isLoading) {
                // Show/hide loading indicator if needed
                showLoading(isLoading);
            }
        });
    }

    private void loadSubjectsForClass(int classId) {
        subjectApiHelper.fetchSubjectsForClass(classId, new SubjectApiHelper.SubjectListCallback() {
            @Override
            public void onSubjectsSuccess(ArrayList<SubjectModel> subjects) {
                subjectList.clear();
                subjectList.addAll(subjects);

                subjectNames.clear();
                subjectNames.add("Select Subject");

                // Add subject names to the list
                for (SubjectModel subject : subjectList) {
                    subjectNames.add(subject.getName());
                }

                subjectAdapter.notifyDataSetChanged();

                // Enable subject dropdown
                binding.edtHeadType.setEnabled(true);
            }

            @Override
            public void onSubjectsError(String errorMessage) {
                Toast.makeText(ActivityAddSubjectChapter.this, "Error loading subjects: " + errorMessage, Toast.LENGTH_SHORT).show();
                clearSubjects();
            }

            @Override
            public void onSubjectsLoading(boolean isLoading) {
                // Show/hide loading indicator if needed
                showLoading(isLoading);
            }
        });
    }

    private void loadChaptersForSubject(int subjectId) {
        chapterApiHelper.fetchChaptersBySubject(subjectId, new ChapterApiHelper.ChapterListCallback() {
            @Override
            public void onSuccess(ArrayList<ChapterResponse.Datum> chapters) {
                chapterList.clear();
                chapterList.addAll(chapters);

                filteredChapterList.clear();
                filteredChapterList.addAll(chapterList);

                chapterAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(ActivityAddSubjectChapter.this, "Error loading chapters: " + errorMessage, Toast.LENGTH_SHORT).show();
                clearChapters();
            }

            @Override
            public void onLoading(boolean isLoading) {
                showLoading(isLoading);
            }
        });
    }

    private void clearSubjects() {
        subjectList.clear();
        subjectNames.clear();
        subjectNames.add("Select Subject");
        subjectAdapter.notifyDataSetChanged();
        binding.edtHeadType.setEnabled(false);
        binding.edtHeadType.setText("", false);
        selectedSubjectId = -1;
    }

    private void clearChapters() {
        chapterList.clear();
        filteredChapterList.clear();
        chapterAdapter.notifyDataSetChanged();
    }

    private void filterChapters(String query) {
        filteredChapterList.clear();

        if (TextUtils.isEmpty(query)) {
            filteredChapterList.addAll(chapterList);
        } else {
            for (ChapterResponse.Datum chapter : chapterList) {
                if (chapter.getChapterName().toLowerCase().contains(query.toLowerCase()) ||
                        chapter.getSubjectName().toLowerCase().contains(query.toLowerCase())) {
                    filteredChapterList.add(chapter);
                }
            }
        }

        chapterAdapter.notifyDataSetChanged();
    }

    private boolean validateForm() {
        // Validate Class
        if (selectedClassId == -1 || binding.edtClass.getText().toString().equals("Select Class")) {
            Toast.makeText(this, "Please select class", Toast.LENGTH_SHORT).show();
            binding.edtClass.requestFocus();
            return false;
        }

        // Validate Subject
        if (selectedSubjectId == -1 || binding.edtHeadType.getText().toString().equals("Select Subject")) {
            Toast.makeText(this, "Please select subject", Toast.LENGTH_SHORT).show();
            binding.edtHeadType.requestFocus();
            return false;
        }

        // Validate Chapter Title
        String title = binding.edtHeadName.getText().toString().trim();
        if (TextUtils.isEmpty(title)) {
            Toast.makeText(this, "Please enter chapter title", Toast.LENGTH_SHORT).show();
            binding.edtHeadName.requestFocus();
            return false;
        }

        // Check for duplicate titles (if not in edit mode or editing different chapter)
        for (ChapterResponse.Datum chapter : chapterList) {
            if (chapter.getChapterName().equalsIgnoreCase(title) &&
                    (!isEditMode || chapter.getId() != editingChapterId)) {
                Toast.makeText(this, "Chapter with this title already exists", Toast.LENGTH_SHORT).show();
                binding.edtHeadName.requestFocus();
                return false;
            }
        }

        return true;
    }

    private void addChapter() {
        if (!validateForm()) {
            return;
        }

        String title = binding.edtHeadName.getText().toString().trim();

        // Create AddChapter object
        AddChapter addChapter = new AddChapter();
        addChapter.setSubjectId(selectedSubjectId);
        addChapter.setChapterName(title);

        chapterApiHelper.addNewChapter(addChapter, new ChapterApiHelper.AddChapterCallback() {
            @Override
            public void onSuccess(String message) {
                // Clear form
                binding.edtHeadName.setText("");

                // Reload chapters to get the updated list
                loadChaptersForSubject(selectedSubjectId);

                Toast.makeText(ActivityAddSubjectChapter.this, message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(ActivityAddSubjectChapter.this, "Error adding chapter: " + errorMessage, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLoading(boolean isLoading) {
                showLoading(isLoading);
            }
        });
    }

    private void enterEditMode(ChapterResponse.Datum chapter, int position) {
        isEditMode = true;
        editingChapterId = chapter.getId();
        editingPosition = position;

        // Fill form with existing data
        binding.edtHeadName.setText(chapter.getChapterName());

        // Set the dropdowns to the correct values
        for (int i = 0; i < classNames.size(); i++) {
            if (classNames.get(i).equals(getClassNameById(selectedClassId))) {
                binding.edtClass.setText(classNames.get(i), false);
                break;
            }
        }

        for (int i = 0; i < subjectNames.size(); i++) {
            if (subjectNames.get(i).equals(chapter.getSubjectName())) {
                binding.edtHeadType.setText(subjectNames.get(i), false);
                break;
            }
        }

        // Show edit buttons, hide add button
        binding.btnSaveHead.setVisibility(View.GONE);
        binding.editBtnLinear.setVisibility(View.VISIBLE);

        // Update button text
        binding.submitTxt.setText("Update Chapter");
    }

    private void updateChapter() {
        if (!validateForm()) {
            return;
        }

        String title = binding.edtHeadName.getText().toString().trim();

        // Create update request object
        AddChapter updateChapter = new AddChapter();
        updateChapter.setSubjectId(selectedSubjectId);
        updateChapter.setChapterName(title);

        // Use the helper class method for updating chapter
        chapterApiHelper.updateChapter(editingChapterId, updateChapter, new ChapterApiHelper.UpdateChapterCallback() {
            @Override
            public void onSuccess(String message) {
                // Update the local lists
                for (int i = 0; i < chapterList.size(); i++) {
                    if (chapterList.get(i).getId() == editingChapterId) {
                        chapterList.get(i).setChapterName(title);
                        break;
                    }
                }

                // Update filtered list
                for (int i = 0; i < filteredChapterList.size(); i++) {
                    if (filteredChapterList.get(i).getId() == editingChapterId) {
                        filteredChapterList.get(i).setChapterName(title);
                        chapterAdapter.notifyItemChanged(i);
                        break;
                    }
                }

                cancelEdit();
                Toast.makeText(ActivityAddSubjectChapter.this, message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(ActivityAddSubjectChapter.this, "Error updating chapter: " + errorMessage, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLoading(boolean isLoading) {
                showLoading(isLoading);
            }
        });
    }

    private void cancelEdit() {
        isEditMode = false;
        editingChapterId = -1;
        editingPosition = -1;

        // Clear form
        binding.edtHeadName.setText("");

        // Show add button, hide edit buttons
        binding.btnSaveHead.setVisibility(View.VISIBLE);
        binding.editBtnLinear.setVisibility(View.GONE);

        // Reset button text
        binding.submitTxt.setText("Add Chapter");
    }

    private void showDeleteConfirmationDialog(ChapterResponse.Datum chapter, int position) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Delete Chapter")
                .setMessage("Are you sure you want to delete '" + chapter.getChapterName() + "'? This action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    deleteChapter(chapter, position);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteChapter(ChapterResponse.Datum chapter, int position) {
        // Use the helper class method for deleting chapter
        chapterApiHelper.deleteChapter(chapter.getId(), new ChapterApiHelper.DeleteChapterCallback() {
            @Override
            public void onSuccess(String message) {
                // Remove from both lists
                chapterList.remove(chapter);

                // Find and remove from filtered list
                int filteredPosition = -1;
                for (int i = 0; i < filteredChapterList.size(); i++) {
                    if (filteredChapterList.get(i).getId() == chapter.getId()) {
                        filteredPosition = i;
                        break;
                    }
                }

                if (filteredPosition != -1) {
                    filteredChapterList.remove(filteredPosition);
                    chapterAdapter.notifyItemRemoved(filteredPosition);
                }

                // If we were editing this chapter, cancel edit mode
                if (isEditMode && editingChapterId == chapter.getId()) {
                    cancelEdit();
                }

                Toast.makeText(ActivityAddSubjectChapter.this, message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(ActivityAddSubjectChapter.this, "Error deleting chapter: " + errorMessage, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLoading(boolean isLoading) {
                showLoading(isLoading);
            }
        });
    }

    private String getClassNameById(int classId) {
        for (ClassModel.Data classData : classList) {
            if (classData.getClassId() == classId) {
                return classData.getClassName();
            }
        }
        return "";
    }

    private void showLoading(boolean isLoading) {
        // Implement loading indicator logic here
        // You can show/hide progress bar or loading dialog
        if (isLoading) {
            // Show loading - you can add a progress bar or disable buttons
            binding.btnSaveHead.setEnabled(!isLoading);
            binding.btnEdit.setEnabled(!isLoading);
        } else {
            // Hide loading - re-enable buttons
            binding.btnSaveHead.setEnabled(true);
            binding.btnEdit.setEnabled(true);
        }
    }
    // Add these methods to your ActivityAddSubjectChapter class

    private static final String TAG = "ActivityAddSubjectChapter";

    private void setupExportButtons() {
        // Copy button
        binding.tvCopy.setOnClickListener(view -> {
            ArrayList<ArrayList<String>> tableData = prepareTableData();
            handleExport(tableData, "copy");
        });

        // CSV button
        binding.tvCsv.setOnClickListener(view -> {
            ArrayList<ArrayList<String>> tableData = prepareTableData();
            handleExport(tableData, "csv");
        });

        // Excel button
        binding.tvExcel.setOnClickListener(view -> {
            ArrayList<ArrayList<String>> tableData = prepareTableData();
            handleExport(tableData, "excel");
        });

        // PDF button
        binding.tvPdf.setOnClickListener(view -> {
            ArrayList<ArrayList<String>> tableData = prepareTableData();
            handleExport(tableData, "pdf");
        });

        // Print button
        binding.tvPrint.setOnClickListener(view -> {
            ArrayList<ArrayList<String>> tableData = prepareTableData();
            handleExport(tableData, "print");
        });
    }

    // Handle export functionality
    private void handleExport(ArrayList<ArrayList<String>> tableData, String action) {
        if (tableData.size() <= 1) { // Only headers, no data
            showToast("No data to export");
            return;
        }

        try {
            // Create DataExportHelper instance with context
            DataExportHelper exportHelper = new DataExportHelper(this);

            // Generate dynamic filename based on chapter data
            String fileName = generateDynamicFileName();

            // Use the exportData method with dynamic filename
            exportHelper.exportData(tableData, action, fileName);

        } catch (Exception e) {
            Log.e(TAG, "Export error: " + e.getMessage());
            showToast("Export failed: " + e.getMessage());
        }
    }

    // Generate dynamic filename based on selected class and subject
    private String generateDynamicFileName() {
        StringBuilder fileName = new StringBuilder("chapters_report");

        // Include class name in filename if selected
        if (selectedClassId != -1) {
            String className = getClassNameById(selectedClassId);
            if (!className.isEmpty()) {
                fileName.append("_").append(className.replaceAll("[^a-zA-Z0-9]", "_"));
            }
        }

        // Include subject name in filename if selected
        if (selectedSubjectId != -1) {
            String subjectName = getSubjectNameById(selectedSubjectId);
            if (!subjectName.isEmpty()) {
                fileName.append("_").append(subjectName.replaceAll("[^a-zA-Z0-9]", "_"));
            }
        }

        // Add timestamp for uniqueness
        fileName.append("_").append(System.currentTimeMillis());

        return fileName.toString();
    }

    // Get subject name by ID
    private String getSubjectNameById(int subjectId) {
        for (SubjectModel subject : subjectList) {
            if (subject.getId() == subjectId) {
                return subject.getName();
            }
        }
        return "";
    }

    // Prepare table data for export
    private ArrayList<ArrayList<String>> prepareTableData() {
        ArrayList<ArrayList<String>> tableData = new ArrayList<>();

        // Add header row based on your XML layout
        ArrayList<String> headers = new ArrayList<>();
        headers.add("Sr");
        headers.add("Subject");
        headers.add("Chapter");
        headers.add("Chapter ID"); // Adding ID for reference
        tableData.add(headers);

        int count = 1;
        // Add data rows from filteredChapterList (what user currently sees)
        for (ChapterResponse.Datum chapter : filteredChapterList) {
            ArrayList<String> row = new ArrayList<>();
            row.add(String.valueOf(count));
            row.add(chapter.getSubjectName() != null ? chapter.getSubjectName() : "");
            row.add(chapter.getChapterName() != null ? chapter.getChapterName() : "");
            row.add(String.valueOf(chapter.getId()));

            tableData.add(row);
            count++;
        }

        return tableData;
    }

    // Helper method to show toast messages
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    // Alternative method to prepare table data with more details (if needed)
    private ArrayList<ArrayList<String>> prepareDetailedTableData() {
        ArrayList<ArrayList<String>> tableData = new ArrayList<>();

        // Add header row with more details
        ArrayList<String> headers = new ArrayList<>();
        headers.add("Sr");
        headers.add("Chapter ID");
        headers.add("Chapter Name");
        headers.add("Subject Name");
        headers.add("Subject ID");
        headers.add("Class Name");
        tableData.add(headers);

        int count = 1;
        // Add data rows from filteredChapterList
        for (ChapterResponse.Datum chapter : filteredChapterList) {
            ArrayList<String> row = new ArrayList<>();
            row.add(String.valueOf(count));
            row.add(String.valueOf(chapter.getId()));
            row.add(chapter.getChapterName() != null ? chapter.getChapterName() : "");
            row.add(chapter.getSubjectName() != null ? chapter.getSubjectName() : "");
            row.add(String.valueOf(chapter.getId())); // Assuming this method exists
            row.add(getClassNameById(selectedClassId)); // Current selected class

            tableData.add(row);
            count++;
        }

        return tableData;
    }

    // Method to export only search results
    private void exportSearchResults() {
        if (filteredChapterList.isEmpty()) {
            showToast("No search results to export");
            return;
        }

        ArrayList<ArrayList<String>> tableData = prepareTableData();

        // Add search query information to filename
        String searchQuery = binding.edtSearch.getText().toString().trim();
        String fileName = "chapters_search_results";

        if (!searchQuery.isEmpty()) {
            fileName += "_" + searchQuery.replaceAll("[^a-zA-Z0-9]", "_");
        }

        fileName += "_" + System.currentTimeMillis();

        try {
            DataExportHelper exportHelper = new DataExportHelper(this);
            exportHelper.exportData(tableData, "excel", fileName); // Default to Excel for search results
            showToast("Search results exported successfully");
        } catch (Exception e) {
            Log.e(TAG, "Export search results error: " + e.getMessage());
            showToast("Export failed: " + e.getMessage());
        }
    }
}