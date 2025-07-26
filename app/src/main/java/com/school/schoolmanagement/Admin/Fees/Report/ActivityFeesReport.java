package com.school.schoolmanagement.Admin.Fees.Report;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.school.schoolmanagement.Admin.Adapter.AdapterFeesReport;
import com.school.schoolmanagement.Admin.Model.ClassModel;
import com.school.schoolmanagement.Admin.Model.FeesReportModel;
import com.school.schoolmanagement.Admin.Model.FeesReportResponse;
import com.school.schoolmanagement.Admin.Model.Pagination;
import com.school.schoolmanagement.GlobalViewModel.ViewModel;
import com.school.schoolmanagement.HelperClasses.DataExportHelper;
import com.school.schoolmanagement.Model.StudentDetails;
import com.school.schoolmanagement.R;
import com.school.schoolmanagement.Utils.Utility;
import com.school.schoolmanagement.databinding.ActivityFeesReportBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ActivityFeesReport extends Utility implements View.OnClickListener {

    private static final String TAG = "ActivityFeesReport";

    // View Binding
    private ActivityFeesReportBinding binding;

    // Adapter and Data
    private AdapterFeesReport adapter;
    private List<FeesReportModel> feesReportList;
    private ArrayList<FeesReportModel> filteredReportList;

    // API and Auth
    private String authToken;
    private ViewModel viewModel;

    // Class and Student Selection Variables
    private ArrayList<ClassModel.Data> classList;
    private List<StudentDetails> studentList;
    private List<StudentDetails> filteredStudentList;
    private int selectedClassId = -1;
    private int selectedStudentId = -1;
    private String selectedStudentName = "";

    // Date Range Variables
    private String startDate = "";
    private String endDate = "";
    private Calendar startCalendar;
    private Calendar endCalendar;
    private SimpleDateFormat dateFormat;

    // Pagination Variables
    private int currentPage = 0;
    int size = 20;
    private int totalPages = 1;
    private boolean hasNext = false;
    private boolean hasPrevious = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityFeesReportBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupEdgeToEdge();
        initializeComponents();
        setClickListeners();
        setupRecyclerView();
        setupSearchFunctionality();
        setupExportButtons();

        // Initialize class data if internet is connected
        if (isInternetConnected(this)) {
            initializeClassData();
        } else {
            showToast("No Internet Connection.");
        }
    }

    private void setupEdgeToEdge() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    private void setupSearchFunctionality() {
        try {
            binding.edtSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    // Only filter when 3 or more characters are entered, or when field is empty
                    if (s.length() >= 3 || s.length() == 0) {
                        filterReports(s.toString());
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        } catch (Exception e) {
            Log.e(TAG, "Error setting up search functionality: " + e.getMessage(), e);
        }
    }

    private void initializeComponents() {
        try {
            // Initialize ViewModel
            viewModel = new ViewModelProvider(this).get(ViewModel.class);

            // Initialize Auth Token
            authToken = "Bearer " + pref.getPrefString(this, pref.user_token);
            if (!authToken.startsWith("Bearer ")) {
                authToken = "Bearer " + authToken;
            }

            // Initialize Lists
            feesReportList = new ArrayList<>();
            filteredReportList = new ArrayList<>();
            classList = new ArrayList<>();
            studentList = new ArrayList<>();
            filteredStudentList = new ArrayList<>();

            // Initialize Date Components
            startCalendar = Calendar.getInstance();
            endCalendar = Calendar.getInstance();
            dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

            // Set default date range (current month)
            setDefaultDateRange();

        } catch (Exception e) {
            Log.e(TAG, "Error initializing components: " + e.getMessage(), e);
            showToast("Error initializing the activity");
        }
    }

    private void setDefaultDateRange() {
        try {
            Calendar cal = Calendar.getInstance();

            // Set start date to first day of current month
            cal.set(Calendar.DAY_OF_MONTH, 1);
            startCalendar.setTime(cal.getTime());
            startDate = dateFormat.format(startCalendar.getTime());

            // Set end date to last day of current month
            cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
            endCalendar.setTime(cal.getTime());
            endDate = dateFormat.format(endCalendar.getTime());

            // Update UI
            updateDateRangeDisplay();

        } catch (Exception e) {
            Log.e(TAG, "Error setting default date range: " + e.getMessage(), e);
        }
    }

    private void updateDateRangeDisplay() {
        try {
            String displayText = startDate + " - " + endDate;
            binding.edtDateRange.setText(displayText);
        } catch (Exception e) {
            Log.e(TAG, "Error updating date range display: " + e.getMessage(), e);
        }
    }

    private void setClickListeners() {
        binding.toolbar.backBtn.setOnClickListener(view -> finish());
        binding.edtSelectClass.setOnClickListener(this);
        binding.edtSearchStudnet.setOnClickListener(this);
        binding.edtDateRange.setOnClickListener(this);
        binding.btnSearch.setOnClickListener(this);
        binding.prevButton.setOnClickListener(this);
        binding.nextButton.setOnClickListener(this);

        // Set fields as non-editable
        binding.edtSelectClass.setFocusable(false);
        binding.edtSearchStudnet.setFocusable(false);
        binding.edtDateRange.setFocusable(false);
    }

    private void setupRecyclerView() {
        try {
            adapter = new AdapterFeesReport(this, filteredReportList);
            binding.rcFeesReport.setAdapter(adapter);
        } catch (Exception e) {
            Log.e(TAG, "Error setting up RecyclerView: " + e.getMessage(), e);
        }
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.edt_select_class) {
            if (classList.isEmpty()) {
                initializeClassData();
            } else {
                showClassSelectionDialog();
            }
        } else if (id == R.id.edt_search_studnet) {
            if (selectedClassId == -1) {
                showToast("Please select a class first");
                return;
            }
            showStudentSelectionDialog();
        } else if (id == R.id.edt_date_range) {
            showDateRangePicker();
        } else if (id == R.id.btn_search) {
            searchFeesReport();
        } else if (id == R.id.prev_button) {
            if (hasPrevious && currentPage > 0) {
                Log.d(TAG, "Going to previous page: " + (currentPage - 1));
                loadFeesReport(currentPage - 1, size);
            }
        } else if (id == R.id.next_button) {
            if (hasNext && currentPage < totalPages - 1) {
                Log.d(TAG, "Going to next page: " + (currentPage + 1));
                loadFeesReport(currentPage + 1, size);
            }
        }
    }

    private void initializeClassData() {
        try {
            showLoading(true);
            viewModel.getAllClasses(authToken).observe(this, response -> {
                showLoading(false);
                if (response != null && response.data != null) {
                    classList.clear();
                    classList.addAll(response.data.getData());
                    Log.d(TAG, "Classes loaded: " + classList.size());
                } else {
                    showToast("No classes found.");
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error loading classes: " + e.getMessage(), e);
            showLoading(false);
        }
    }

    private void showClassSelectionDialog() {
        try {
            if (classList.isEmpty()) {
                showToast("No classes available");
                return;
            }

            String[] classNames = new String[classList.size()];
            for (int i = 0; i < classList.size(); i++) {
                classNames[i] = classList.get(i).getClassName();
            }

            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("Select Class")
                    .setItems(classNames, (dialogInterface, position) -> {
                        ClassModel.Data selectedClass = classList.get(position);
                        onClassSelected(selectedClass);
                        dialogInterface.dismiss();
                    })
                    .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss())
                    .create();

            dialog.show();
        } catch (Exception e) {
            Log.e(TAG, "Error showing class selection dialog: " + e.getMessage(), e);
        }
    }

    private void onClassSelected(ClassModel.Data selectedClass) {
        try {
            selectedClassId = selectedClass.getClassId();
            binding.edtSelectClass.setText(selectedClass.getClassName());

            // Reset student selection
            selectedStudentId = -1;
            selectedStudentName = "";
            binding.edtSearchStudnet.setText("");
            binding.edtSearchStudnet.setHint("Search Student");

            // Load students for selected class
            loadStudentsByClass(selectedClassId);

            Log.d(TAG, "Selected Class ID: " + selectedClassId);
            showToast("Selected: " + selectedClass.getClassName());
        } catch (Exception e) {
            Log.e(TAG, "Error handling class selection: " + e.getMessage(), e);
        }
    }

    private void loadStudentsByClass(int classId) {
        try {
            showLoading(true);
            viewModel.getBasicList(authToken, classId).observe(this, response -> {
                try {
                    showLoading(false);
                    if (response != null && response.isSuccess && response.data.getData() != null) {
                        handleStudentsResponse(response.data.getData());
                    } else {
                        String errorMessage = response != null && response.message != null ?
                                response.message : "Failed to fetch students for selected class";
                        Log.e(TAG, "API Error: " + errorMessage);
                        showToast(errorMessage);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error handling students response: " + e.getMessage(), e);
                    showLoading(false);
                    showToast("Error processing server response");
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error loading students by class: " + e.getMessage(), e);
            showLoading(false);
        }
    }

    private void handleStudentsResponse(Object data) {
        try {
            studentList.clear();
            filteredStudentList.clear();

            if (data instanceof List) {
                List<?> responseList = (List<?>) data;
                for (Object item : responseList) {
                    if (item instanceof StudentDetails) {
                        studentList.add((StudentDetails) item);
                    }
                }
            }

            filteredStudentList.addAll(studentList);
            Log.d(TAG, "Successfully loaded " + studentList.size() + " students");
        } catch (Exception e) {
            Log.e(TAG, "Error handling students response: " + e.getMessage(), e);
        }
    }

    private void showStudentSelectionDialog() {
        try {
            if (filteredStudentList.isEmpty()) {
                showToast("No students found for selected class");
                return;
            }

            String[] studentNames = new String[filteredStudentList.size() + 1];
            studentNames[0] = "All Students"; // Add option for all students

            for (int i = 0; i < filteredStudentList.size(); i++) {
                studentNames[i + 1] = filteredStudentList.get(i).getStudentName();
            }

            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("Select Student")
                    .setItems(studentNames, (dialogInterface, position) -> {
                        if (position == 0) {
                            // All students selected
                            selectedStudentId = -1;
                            selectedStudentName = "All Students";
                            binding.edtSearchStudnet.setText("All Students");
                        } else {
                            StudentDetails selectedStudent = filteredStudentList.get(position - 1);
                            selectedStudentId = selectedStudent.getStudentId();
                            selectedStudentName = selectedStudent.getStudentName();
                            binding.edtSearchStudnet.setText(selectedStudentName);
                        }

                        Log.d(TAG, "Selected Student ID: " + selectedStudentId + ", Name: " + selectedStudentName);
                        showToast("Selected: " + selectedStudentName);
                        dialogInterface.dismiss();
                    })
                    .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss())
                    .create();

            dialog.show();
        } catch (Exception e) {
            Log.e(TAG, "Error showing student selection dialog: " + e.getMessage(), e);
        }
    }

    private void showDateRangePicker() {
        try {
            // Show start date picker first
            DatePickerDialog startDatePicker = new DatePickerDialog(this,
                    (DatePicker view, int year, int month, int dayOfMonth) -> {
                        startCalendar.set(year, month, dayOfMonth);
                        startDate = dateFormat.format(startCalendar.getTime());

                        // After selecting start date, show end date picker
                        showEndDatePicker();
                    },
                    startCalendar.get(Calendar.YEAR),
                    startCalendar.get(Calendar.MONTH),
                    startCalendar.get(Calendar.DAY_OF_MONTH));

            startDatePicker.setTitle("Select Start Date");
            startDatePicker.show();

        } catch (Exception e) {
            Log.e(TAG, "Error showing date range picker: " + e.getMessage(), e);
        }
    }

    private void showEndDatePicker() {
        try {
            DatePickerDialog endDatePicker = new DatePickerDialog(this,
                    (DatePicker view, int year, int month, int dayOfMonth) -> {
                        endCalendar.set(year, month, dayOfMonth);
                        endDate = dateFormat.format(endCalendar.getTime());

                        // Validate date range
                        if (endCalendar.before(startCalendar)) {
                            showToast("End date cannot be before start date");
                            return;
                        }

                        // Update display
                        updateDateRangeDisplay();
                        Log.d(TAG, "Date range selected: " + startDate + " to " + endDate);
                    },
                    endCalendar.get(Calendar.YEAR),
                    endCalendar.get(Calendar.MONTH),
                    endCalendar.get(Calendar.DAY_OF_MONTH));

            endDatePicker.setTitle("Select End Date");
            endDatePicker.getDatePicker().setMinDate(startCalendar.getTimeInMillis());
            endDatePicker.show();

        } catch (Exception e) {
            Log.e(TAG, "Error showing end date picker: " + e.getMessage(), e);
        }
    }

    private void searchFeesReport() {
        try {
            if (selectedClassId == -1) {
                showToast("Please select a class first");
                return;
            }

            if (startDate.isEmpty() || endDate.isEmpty()) {
                showToast("Please select date range");
                return;
            }

            Log.d(TAG, "Starting new search - resetting to page 0");

            // Reset pagination for new search
            currentPage = 0;
            totalPages = 1;
            hasNext = false;
            hasPrevious = false;

            // Load fees report from page 0
            loadFeesReport(0, size);

        } catch (Exception e) {
            Log.e(TAG, "Error searching fees report: " + e.getMessage(), e);
            showToast("Error initiating search");
        }
    }

    private void loadFeesReport(int page, int size) {
        try {
            showLoading(true);

            Log.d(TAG, "Loading fees report - Page: " + page + ", Size: " + size);

            viewModel.getFeesReport(authToken, selectedClassId, selectedStudentId, startDate, endDate, page, size)
                    .observe(this, response -> {
                        try {
                            Log.d(TAG, "Fees report response received for page: " + page);
                            handleFeesReportResponse(response.data, response.code, page);
                        } catch (Exception e) {
                            Log.e(TAG, "Error processing fees report response: " + e.getMessage(), e);
                            showLoading(false);
                            showToast("Error processing response");
                        }
                    });

        } catch (Exception e) {
            Log.e(TAG, "Error loading fees report: " + e.getMessage(), e);
            showLoading(false);
            showToast("Error loading fees report");
        }
    }

    private void handleFeesReportResponse(FeesReportResponse response, int code, int requestedPage) {
        try {
            showLoading(false);

            if (response == null) {
                showToast("No response from server");
                Log.e(TAG, "Response is null");
                return;
            }

            Log.d(TAG, "handleFeesReportResponse: " + response.getStatus() + " for page: " + requestedPage);

            if (code == 500) {
                String errorMessage = response.getMessage() != null ? response.getMessage() : "Failed to load fees report";
                showToast(errorMessage);
                Log.e(TAG, "API Error: " + errorMessage);
                return;
            }

            if (response.getEntries() != null && !response.getEntries().isEmpty()) {
                Log.d(TAG, "Processing " + response.getEntries().size() + " entries for page: " + requestedPage);
                updateFeesReportData(response, requestedPage);
            } else if (response.getEntries() != null && response.getEntries().isEmpty()) {
                showToast("No fees data found for the selected criteria");
                feesReportList.clear();
                filteredReportList.clear();
                adapter.notifyDataSetChanged();
                // Still update pagination info even if no data
                if (response.getPagination() != null) {
                    currentPage = requestedPage;
                    updatePaginationInfo(response.getPagination());
                }
                Log.d(TAG, "Success response but no entries found for page: " + requestedPage);
            } else {
                showToast("Invalid response format");
                Log.e(TAG, "Response has no entries field and no error status");
            }

        } catch (Exception e) {
            Log.e(TAG, "Error handling fees report response: " + e.getMessage(), e);
            showLoading(false);
            showToast("Error processing server response");
        }
    }

    // Option 1: Use adapter's updateData method instead of notifyDataSetChanged
    private void updateFeesReportData(FeesReportResponse response, int requestedPage) {
        try {
            // Create temporary lists
            List<FeesReportModel> tempReportList = new ArrayList<>();

            Log.d(TAG, "Updating fees report data with " + response.getEntries().size() + " entries for page: " + requestedPage);

            // Convert API response to FeesReportModel
            for (FeesReportResponse.EntryInfo entry : response.getEntries()) {
                FeesReportModel reportModel = new FeesReportModel(
                        String.valueOf(entry.getStudentId()),
                        entry.getStudentName(),
                        entry.getDueDate(),
                        String.format(Locale.getDefault(), "$%.1f", entry.getTotalAmount()),
                        String.format(Locale.getDefault(), "$%.1f", entry.getDepositedAmount()),
                        String.format(Locale.getDefault(), "$%.1f", entry.getTotalAmount()-entry.getDepositedAmount()),
                        entry.getDepositedAmount() >= entry.getTotalAmount() ? "Paid" : "Unpaid"
                );
                tempReportList.add(reportModel);
                Log.d(TAG, "Added report model: " + entry.getStudentName());
            }

            // Update the main lists
            feesReportList.clear();
            feesReportList.addAll(tempReportList);

            filteredReportList.clear();
            filteredReportList.addAll(tempReportList);

            // IMPORTANT: Update currentPage BEFORE updating pagination info
            currentPage = requestedPage;

            // Update pagination info
            if (response.getPagination() != null) {
                updatePaginationInfo(response.getPagination());
                Log.d(TAG, "Pagination updated - Current Page: " + currentPage + ", Total Pages: " + totalPages);
            }

            // Update adapter with new data instead of just notifying
            runOnUiThread(() -> {
                Log.d(TAG, "About to update adapter with " + tempReportList.size() + " items");
                adapter.updateData(tempReportList); // Use this instead of notifyDataSetChanged()
                Log.d(TAG, "Adapter updated with " + tempReportList.size() + " items for page: " + currentPage);
            });

            Log.d(TAG, "Fees report loaded successfully. Total entries: " + feesReportList.size() + " on page: " + currentPage);

        } catch (Exception e) {
            Log.e(TAG, "Error updating fees report data: " + e.getMessage(), e);
            showToast("Error processing fees report data");
        }
    }
    private void updatePaginationInfo(Pagination pagination) {
        try {
            // Get pagination values from response
            int responsePage = pagination.currentPage;
            totalPages = pagination.totalPages;
            hasNext = pagination.hasNext;
            hasPrevious = pagination.hasPrevious;

            Log.d(TAG, "Pagination from response - Page: " + responsePage +
                    ", Total: " + totalPages + ", HasNext: " + hasNext + ", HasPrev: " + hasPrevious);

            // Update UI
            updatePaginationUI();

        } catch (Exception e) {
            Log.e(TAG, "Error updating pagination info: " + e.getMessage(), e);
        }
    }

    private void updatePaginationUI() {
        try {
            // Update page indicator (show 1-based indexing to user)
            String pageText = "Showing page " + (currentPage + 1) + " of " + totalPages;
            binding.pageTv.setText(pageText);

            // Update button states based on actual pagination values
            binding.prevButton.setAlpha(hasPrevious ? 1.0f : 0.5f);
            binding.nextButton.setAlpha(hasNext ? 1.0f : 0.5f);

            binding.prevButton.setEnabled(hasPrevious);
            binding.nextButton.setEnabled(hasNext);

            Log.d(TAG, "UI Updated - Showing page " + (currentPage + 1) + " of " + totalPages +
                    ", Prev enabled: " + hasPrevious + ", Next enabled: " + hasNext);

        } catch (Exception e) {
            Log.e(TAG, "Error updating pagination UI: " + e.getMessage(), e);
        }
    }

    private void filterReports(String query) {
        try {
            filteredReportList.clear();

            if (query == null || query.isEmpty()) {
                filteredReportList.addAll(feesReportList);
            } else {
                String lowerCaseQuery = query.toLowerCase();
                for (FeesReportModel report : feesReportList) {
                    // Search across all fields
                    if (report.getName().toLowerCase().contains(lowerCaseQuery) ||
                            report.getId().toLowerCase().contains(lowerCaseQuery) ||
                            report.getDueDate().toLowerCase().contains(lowerCaseQuery) ||
                            report.getAmount().toLowerCase().contains(lowerCaseQuery) ||
                            report.getDepositedAmount().toLowerCase().contains(lowerCaseQuery) ||
                            report.getDueAmount().toLowerCase().contains(lowerCaseQuery) ||
                            report.getStatus().toLowerCase().contains(lowerCaseQuery)) {
                        filteredReportList.add(report);
                    }
                }
            }

            adapter.notifyDataSetChanged();
            Log.d(TAG, "Filtered reports: " + filteredReportList.size() + " items for query: " + query);

        } catch (Exception e) {
            Log.e(TAG, "Error filtering reports: " + e.getMessage(), e);
        }
    }
    private void showLoading(boolean isLoading) {
        try {
            // Implement your loading indicator here
            // Example: binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            Log.d(TAG, "Loading state: " + isLoading);
        } catch (Exception e) {
            Log.e(TAG, "Error showing/hiding loader: " + e.getMessage(), e);
        }
    }

    private void showToast(String message) {
        try {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Toast shown: " + message);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error showing toast: " + e.getMessage(), e);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
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

    // New method to handle export functionality
    private void handleExport(ArrayList<ArrayList<String>> tableData, String action) {
        if (tableData.size() <= 1) { // Only headers, no data
            showToast("No data to export");
            return;
        }

        try {
            // Create DataExportHelper instance with context
            DataExportHelper exportHelper = new DataExportHelper(this);

            // Generate dynamic filename based on report type and selected criteria
            String fileName = generateDynamicFileName();

            // Use the exportData method with dynamic filename
            exportHelper.exportData(tableData, action, fileName);

        } catch (Exception e) {
            Log.e(TAG, "Export error: " + e.getMessage());
            showToast("Export failed: " + e.getMessage());
        }
    }

    // New method to generate dynamic filename based on report type
    private String generateDynamicFileName() {
        String fileName = "fees_report";

        // Include date range in filename
        if (startDate != null && endDate != null) {
            fileName += "_" + startDate.replace("-", "") + "_to_" + endDate.replace("-", "");
        }

        // Include class info if available
        if (selectedClassId != -1) {
            fileName += "_class" + selectedClassId;
        }

        // Include student info if specific student selected
        if (selectedStudentId != -1 && !selectedStudentName.isEmpty()) {
            String cleanStudentName = selectedStudentName.replaceAll("[^a-zA-Z0-9]", "_");
            fileName += "_" + cleanStudentName;
        }

        // Add timestamp for uniqueness
        fileName += "_" + System.currentTimeMillis();

        return fileName;
    }

    // New method to prepare table data for export
    private ArrayList<ArrayList<String>> prepareTableData() {
        ArrayList<ArrayList<String>> tableData = new ArrayList<>();

        // Add header row based on FeesReportModel structure
        ArrayList<String> headers = new ArrayList<>();
        headers.add("Sr");
        headers.add("Student ID");
        headers.add("Student Name");
        headers.add("Due Date");
        headers.add("Total Amount");
        headers.add("Deposited Amount");
        headers.add("Remaining Amount");
        headers.add("Status");
        tableData.add(headers);

        int count = 1;
        // Add data rows from filteredReportList (shows current filtered data)
        for (FeesReportModel report : filteredReportList) {
            ArrayList<String> row = new ArrayList<>();
            row.add(String.valueOf(count));
            row.add(report.getId() != null ? report.getId() : "");
            row.add(report.getName() != null ? report.getName() : "");
            row.add(report.getDueDate() != null ? report.getDueDate() : "");
            row.add(report.getAmount() != null ? report.getAmount() : "");
            row.add(report.getDepositedAmount() != null ? report.getDepositedAmount() : "");
            row.add(report.getDueAmount() != null ? report.getDueAmount() : "");
            row.add(report.getStatus() != null ? report.getStatus() : "");

            tableData.add(row);
            count++;
        }

        return tableData;
    }

}