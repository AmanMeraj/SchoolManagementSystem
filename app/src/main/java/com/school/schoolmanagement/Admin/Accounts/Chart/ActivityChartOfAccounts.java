package com.school.schoolmanagement.Admin.Accounts.Chart;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.school.schoolmanagement.Adapters.ChartAdapter;
import com.school.schoolmanagement.Adapters.ChartItemClickListener;
import com.school.schoolmanagement.Admin.Model.AccountGet;
import com.school.schoolmanagement.GlobalViewModel.ViewModel;
import com.school.schoolmanagement.HelperClasses.ChartApiHelper;
import com.school.schoolmanagement.Model.EmployeeResponse;
import com.school.schoolmanagement.Model.ModelResponse;
import com.school.schoolmanagement.R;
import com.school.schoolmanagement.Utils.Utility;
import com.school.schoolmanagement.databinding.ActivityChartOfAccountsBinding;

import java.util.ArrayList;
import java.util.List;

public class ActivityChartOfAccounts extends Utility implements ChartItemClickListener {

    ActivityChartOfAccountsBinding binding;
    private String[] headTypes = {"INCOME", "EXPENSE"};
    private ArrayAdapter<String> headTypeAdapter;

    // Adapter and data
    private ChartAdapter chartAdapter;
    private List<AccountGet.Datum> originalChartList = new ArrayList<>();
    private List<AccountGet.Datum> filteredChartList = new ArrayList<>();

    // API related
    private ViewModel viewModel;
    private ChartApiHelper apiHelper;
    private String authToken;

    // Edit mode variables
    private boolean isEditMode = false;
    private AccountGet.Datum editingItem = null;
    private int editingPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityChartOfAccountsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeApiComponents();
        initViews();
        setupRecyclerView();
        setupClickListeners();
        setupSearchFunctionality();
        loadChartData(); // Load initial data
    }

    private void initializeApiComponents() {
        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(ViewModel.class);

        // Initialize API Helper
        apiHelper = new ChartApiHelper(viewModel, this);

        // Set BOTH callbacks - this is crucial!
        apiHelper.setCreationCallback(new ChartApiHelper.ChartCreationCallback() {
            @Override
            public void onSuccess(EmployeeResponse response) {
                runOnUiThread(() -> {
                    if (isEditMode) {
                        showSuccess("Chart of account updated successfully!");
                        exitEditMode();
                    } else {
                        showSuccess("Chart of account created successfully!");
                        clearForm();
                    }
                    refreshChartList();
                });
            }

            @Override
            public void onError(String errorMessage, int errorCode) {
                runOnUiThread(() -> {
                    String userFriendlyMessage = ChartApiHelper.getUserFriendlyErrorMessage(errorCode, errorMessage);
                    showError(userFriendlyMessage);

                    // Handle specific error cases
                    if (ChartApiHelper.isAuthError(errorCode)) {
                        handleAuthError();
                    }
                });
            }

            @Override
            public void onNetworkFailure() {
                runOnUiThread(() -> {
                    showError("Network connection failed. Please check your internet connection and try again.");
                });
            }

            @Override
            public void showLoading() {
                runOnUiThread(() -> {
                    if (isEditMode) {
                        binding.btnEdit.setEnabled(false);
                        binding.editTxt.setText("Updating...");
                    } else {
                        binding.btnSaveHead.setEnabled(false);
                        binding.submitTxt.setText("Saving...");
                    }
                });
            }

            @Override
            public void hideLoading() {
                runOnUiThread(() -> {
                    if (isEditMode) {
                        binding.btnEdit.setEnabled(true);
                        binding.editTxt.setText("Edit Head");
                    } else {
                        binding.btnSaveHead.setEnabled(true);
                        binding.submitTxt.setText("Save Head");
                    }
                });
            }
        });

        // Set fetch callback for loading chart data
        apiHelper.setFetchCallback(new ChartApiHelper.ChartFetchCallback() {
            @Override
            public void onSuccess(AccountGet response) {
                runOnUiThread(() -> {
                    if (response != null && response.getData() != null) {
                        onChartDataReceived(response.getData());
                        showSuccess("Chart data loaded successfully!");
                    } else {
                        showError("No chart data found");
                    }
                });
            }

            @Override
            public void onError(String errorMessage, int errorCode) {
                runOnUiThread(() -> {
                    String userFriendlyMessage = ChartApiHelper.getUserFriendlyErrorMessage(errorCode, errorMessage);
                    showError("Failed to load chart data: " + userFriendlyMessage);

                    // Handle specific error cases
                    if (ChartApiHelper.isAuthError(errorCode)) {
                        handleAuthError();
                    }
                });
            }

            @Override
            public void onNetworkFailure() {
                runOnUiThread(() -> {
                    showError("Network connection failed. Unable to load chart data.");
                });
            }

            @Override
            public void showLoading() {
                runOnUiThread(() -> {
                    // Show loading indicator for data fetch
                    binding.tvNoData.setText("Loading chart data...");
                    binding.tvNoData.setVisibility(View.VISIBLE);
                    binding.rcChart.setVisibility(View.GONE);
                });
            }

            @Override
            public void hideLoading() {
                runOnUiThread(() -> {
                    // Hide loading indicator
                    updateNoDataVisibility();
                });
            }
        });

        // Get auth token
        authToken = getAuthToken();
    }

    private void initViews() {
        // Setup dropdown for head type
        headTypeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, headTypes);
        binding.edtHeadType.setAdapter(headTypeAdapter);

        // Make the AutoCompleteTextView show dropdown on click
        binding.edtHeadType.setOnClickListener(v -> {
            binding.edtHeadType.showDropDown();
        });

        // Set threshold to 0 to show all items
        binding.edtHeadType.setThreshold(0);
    }

    private void setupRecyclerView() {
        // Initialize adapter with empty list
        chartAdapter = new ChartAdapter(filteredChartList, this);

        // Setup RecyclerView
        binding.rcChart.setLayoutManager(new LinearLayoutManager(this));
        binding.rcChart.setAdapter(chartAdapter);

        // Initially show no data message
        updateNoDataVisibility();
    }

    private void setupClickListeners() {
        // Save button click listener
        binding.btnSaveHead.setOnClickListener(v -> validateAndSaveHead());

        // Edit button click listener
        binding.btnEdit.setOnClickListener(v -> validateAndEditHead());

        // Cancel button click listener
        binding.btnCancel.setOnClickListener(v -> exitEditMode());

        // Pagination buttons
        binding.prevButton.setOnClickListener(v -> {
            // Handle previous page
            Toast.makeText(this, "Previous page clicked", Toast.LENGTH_SHORT).show();
        });

        binding.nextButton.setOnClickListener(v -> {
            // Handle next page
            Toast.makeText(this, "Next page clicked", Toast.LENGTH_SHORT).show();
        });
    }

    private void setupSearchFunctionality() {
        binding.edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterChartList(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Not needed
            }
        });

        // Optional: Handle search action button
        binding.edtSearch.setOnEditorActionListener((v, actionId, event) -> {
            performSearch();
            return true;
        });
    }

    private void filterChartList(String searchText) {
        filteredChartList.clear();

        if (TextUtils.isEmpty(searchText)) {
            // If search text is empty, show all items
            filteredChartList.addAll(originalChartList);
        } else {
            // Filter items based on search text
            String lowerCaseSearch = searchText.toLowerCase().trim();

            for (AccountGet.Datum item : originalChartList) {
                boolean matchesName = item.getChartName() != null &&
                        item.getChartName().toLowerCase().contains(lowerCaseSearch);
                boolean matchesType = item.getChartType() != null &&
                        item.getChartType().toLowerCase().contains(lowerCaseSearch);

                if (matchesName || matchesType) {
                    filteredChartList.add(item);
                }
            }
        }

        // Update adapter
        chartAdapter.notifyDataSetChanged();
        updateNoDataVisibility();
        updatePaginationInfo();
    }

    private void updateNoDataVisibility() {
        if (filteredChartList.isEmpty()) {
            binding.tvNoData.setText("No chart data available");
            binding.tvNoData.setVisibility(View.VISIBLE);
            binding.rcChart.setVisibility(View.GONE);
        } else {
            binding.tvNoData.setVisibility(View.GONE);
            binding.rcChart.setVisibility(View.VISIBLE);
        }
    }

    private void updatePaginationInfo() {
        int totalItems = filteredChartList.size();
        String paginationText = "Showing " + totalItems + " items";
        binding.pageTv.setText(paginationText);
    }

    private void loadChartData() {
        if (!TextUtils.isEmpty(authToken)) {
            String formattedAuth = ChartApiHelper.formatAuthHeader(authToken);
            apiHelper.getAccountChart(formattedAuth);
        } else {
            showError("Authentication token not found. Please login again.");
        }
    }

    private void updateChartList() {
        filteredChartList.clear();
        filteredChartList.addAll(originalChartList);
        chartAdapter.notifyDataSetChanged();
        updateNoDataVisibility();
        updatePaginationInfo();
    }

    // ChartItemClickListener implementation
    @Override
    public void onEditClick(AccountGet.Datum item, int position) {
        // Handle edit click - enter edit mode
        enterEditMode(item, position);
    }

    @Override
    public void onDeleteClick(AccountGet.Datum item, int position) {
        // Handle delete click
        showDeleteConfirmation(item, position);
    }

    private void enterEditMode(AccountGet.Datum item, int position) {
        isEditMode = true;
        editingItem = item;
        editingPosition = position;

        // Populate form with item data
        binding.edtHeadName.setText(item.getChartName());
        binding.edtHeadType.setText(item.getChartType());

        // Update UI for edit mode
        binding.btnSaveHead.setVisibility(View.GONE);
        binding.editBtnLinear.setVisibility(View.VISIBLE);

        // Scroll to top to show the form
        binding.scrollView.fullScroll(View.FOCUS_UP);

        Toast.makeText(this, "Edit mode activated for: " + item.getChartName(), Toast.LENGTH_SHORT).show();
    }

    private void exitEditMode() {
        isEditMode = false;
        editingItem = null;
        editingPosition = -1;

        // Clear form
        clearForm();

        // Update UI for normal mode
        binding.btnSaveHead.setVisibility(View.VISIBLE);
        binding.editBtnLinear.setVisibility(View.GONE);

        Toast.makeText(this, "Edit mode cancelled", Toast.LENGTH_SHORT).show();
    }

    private void showDeleteConfirmation(AccountGet.Datum item, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Confirmation");
        builder.setMessage("Are you sure you want to delete '" + item.getChartName() + "'?");

        builder.setPositiveButton("Delete", (dialog, which) -> {
            deleteChartItem(item, position);
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void deleteChartItem(AccountGet.Datum item, int position) {
        String authToken = getAuthToken();

        // Set the delete callback before making the API call
        apiHelper.setDeleteCallback(new ChartApiHelper.ChartDeleteCallback() {
            @Override
            public void onSuccess(ModelResponse response) {
                // Handle successful deletion
                runOnUiThread(() -> {
                    // Remove from original list
                    originalChartList.remove(item);

                    // Update filtered list and adapter
                    filterChartList(binding.edtSearch.getText().toString());

                    Toast.makeText(ActivityChartOfAccounts.this,
                            "Chart deleted successfully", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onError(String errorMessage, int errorCode) {
                // Handle error
                runOnUiThread(() -> {
                    Toast.makeText(ActivityChartOfAccounts.this,
                            "Failed to delete: " + errorMessage, Toast.LENGTH_LONG).show();
                });
            }

            @Override
            public void onNetworkFailure() {
                // Handle network failure
                runOnUiThread(() -> {
                    Toast.makeText(ActivityChartOfAccounts.this,
                            "Network error. Please check your connection.", Toast.LENGTH_LONG).show();
                });
            }

            @Override
            public void showLoading() {
                // Show loading indicator
                runOnUiThread(() -> {
                    // Show your loading dialog or progress bar
                    // For example: showProgressDialog("Deleting...");
                });
            }

            @Override
            public void hideLoading() {
                // Hide loading indicator
                runOnUiThread(() -> {
                    // Hide your loading dialog or progress bar
                    // For example: hideProgressDialog();
                });
            }
        });

        // Now make the API call
        apiHelper.deleteAccountChart(authToken, item.getId());
    }

    private void validateAndSaveHead() {
        String headName = binding.edtHeadName.getText().toString().trim();
        String headType = binding.edtHeadType.getText().toString().trim();

        // Basic UI validation
        if (TextUtils.isEmpty(headName)) {
            binding.edtHeadName.setError("Head name is required");
            binding.edtHeadName.requestFocus();
            return;
        }

        if (headName.length() < 2) {
            binding.edtHeadName.setError("Head name must be at least 2 characters");
            binding.edtHeadName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(headType)) {
            binding.edtHeadType.setError("Please select head type");
            binding.edtHeadType.requestFocus();
            binding.edtHeadType.showDropDown();
            return;
        }

        if (!headType.equals("INCOME") && !headType.equals("EXPENSE")) {
            binding.edtHeadType.setError("Please select a valid head type");
            binding.edtHeadType.requestFocus();
            binding.edtHeadType.showDropDown();
            return;
        }

        // Check if auth token is available
        if (TextUtils.isEmpty(authToken)) {
            showError("Authentication token not found. Please login again.");
            return;
        }

        // If validation passes, call API through helper
        String formattedAuth = ChartApiHelper.formatAuthHeader(authToken);
        apiHelper.createChart(formattedAuth, headName, headType);
    }

    private void validateAndEditHead() {
        if (editingItem == null) {
            showError("No item selected for editing");
            return;
        }

        String headName = binding.edtHeadName.getText().toString().trim();
        String headType = binding.edtHeadType.getText().toString().trim();

        // Basic UI validation
        if (TextUtils.isEmpty(headName)) {
            binding.edtHeadName.setError("Head name is required");
            binding.edtHeadName.requestFocus();
            return;
        }

        if (headName.length() < 2) {
            binding.edtHeadName.setError("Head name must be at least 2 characters");
            binding.edtHeadName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(headType)) {
            binding.edtHeadType.setError("Please select head type");
            binding.edtHeadType.requestFocus();
            binding.edtHeadType.showDropDown();
            return;
        }

        if (!headType.equals("INCOME") && !headType.equals("EXPENSE")) {
            binding.edtHeadType.setError("Please select a valid head type");
            binding.edtHeadType.requestFocus();
            binding.edtHeadType.showDropDown();
            return;
        }

        // Check if auth token is available
        if (TextUtils.isEmpty(authToken)) {
            showError("Authentication token not found. Please login again.");
            return;
        }

        // If validation passes, call edit API through helper
        String formattedAuth = ChartApiHelper.formatAuthHeader(authToken);
        apiHelper.editAccountChart(formattedAuth, editingItem.getId(), headName, headType);
    }

    private void clearForm() {
        binding.edtHeadName.setText("");
        binding.edtHeadType.setText("");
        binding.edtHeadName.clearFocus();
        binding.edtHeadType.clearFocus();
    }

    private void performSearch() {
        String searchQuery = binding.edtSearch.getText().toString().trim();

        if (TextUtils.isEmpty(searchQuery)) {
            // Clear search
            filterChartList("");
        } else {
            // Filter is already applied through TextWatcher
            Toast.makeText(this, "Searching for: " + searchQuery, Toast.LENGTH_SHORT).show();
        }
    }

    private void refreshChartList() {
        // Reload data from API
        loadChartData();
    }

    public void onChartDataReceived(List<AccountGet.Datum> chartData) {
        // Call this method when you receive data from API
        originalChartList.clear();
        if (chartData != null) {
            originalChartList.addAll(chartData);
        }
        updateChartList();
    }

    // Helper methods
    private String getAuthToken() {
        // Replace this with your actual token retrieval logic
        String token = pref.getPrefString(this, pref.user_token);
        return !TextUtils.isEmpty(token) ? token : "";
    }

    private void handleAuthError() {
        showError("Session expired. Please login again.");
        // You might want to redirect to login activity here
        // Intent intent = new Intent(this, LoginActivity.class);
        // startActivity(intent);
        // finish();
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void showSuccess(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (apiHelper != null) {
            // Clean up callbacks to prevent memory leaks
            apiHelper.setCreationCallback(null);
            apiHelper.setFetchCallback(null);
        }
        binding = null;
    }
}