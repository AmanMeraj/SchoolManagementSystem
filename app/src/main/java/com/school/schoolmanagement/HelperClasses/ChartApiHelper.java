package com.school.schoolmanagement.HelperClasses;

import static androidx.lifecycle.LiveDataKt.observe;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import com.school.schoolmanagement.Admin.Model.CreateChart;
import com.school.schoolmanagement.Admin.Model.AccountGet;
import com.school.schoolmanagement.GlobalRepository.GlobalRepository;
import com.school.schoolmanagement.GlobalViewModel.ViewModel;
import com.school.schoolmanagement.Model.EmployeeResponse;
import com.school.schoolmanagement.Model.ModelResponse;

/**
 * Helper class for Chart of Accounts API operations
 */
public class ChartApiHelper {

    public interface ChartCreationCallback {
        void onSuccess(EmployeeResponse response);
        void onError(String errorMessage, int errorCode);
        void onNetworkFailure();
        void showLoading();
        void hideLoading();
    }

    public interface ChartFetchCallback {
        void onSuccess(AccountGet response);
        void onError(String errorMessage, int errorCode);
        void onNetworkFailure();
        void showLoading();
        void hideLoading();
    }

    public interface ChartDeleteCallback {
        void onSuccess(ModelResponse response);
        void onError(String errorMessage, int errorCode);
        void onNetworkFailure();
        void showLoading();
        void hideLoading();
    }

    private ViewModel viewModel;
    private LifecycleOwner lifecycleOwner;
    private ChartCreationCallback creationCallback;
    private ChartFetchCallback fetchCallback;
    private ChartDeleteCallback deleteCallback;

    public ChartApiHelper(ViewModel viewModel, LifecycleOwner lifecycleOwner) {
        this.viewModel = viewModel;
        this.lifecycleOwner = lifecycleOwner;
    }

    public void setCreationCallback(ChartCreationCallback callback) {
        this.creationCallback = callback;
    }

    public void setFetchCallback(ChartFetchCallback callback) {
        this.fetchCallback = callback;
    }

    public void setDeleteCallback(ChartDeleteCallback callback) {
        this.deleteCallback = callback;
    }

    // Keep the original setCallback method for backward compatibility
    public void setCallback(ChartCreationCallback callback) {
        this.creationCallback = callback;
    }

    /**
     * Create a new chart of account
     * @param auth Authorization token (Bearer token)
     * @param headName Name of the account head
     * @param headType Type of account (INCOME/EXPENSE)
     */
    public void createChart(String auth, String headName, String headType) {
        if (creationCallback == null) {
            throw new IllegalStateException("Creation callback must be set before making API calls");
        }

        // Validate inputs
        if (!isValidInput(headName, headType)) {
            return;
        }

        // Show loading
        creationCallback.showLoading();

        // Create the request object
        CreateChart createChart = new CreateChart();
        createChart.setChartName(headName);
        createChart.setChartType(headType);
        // Add any other required fields for CreateChart model

        // Make the API call
        viewModel.createChart(auth, createChart).observe(lifecycleOwner, new Observer<GlobalRepository.ApiResponse<EmployeeResponse>>() {
            @Override
            public void onChanged(GlobalRepository.ApiResponse<EmployeeResponse> response) {
                creationCallback.hideLoading();

                if (response != null) {
                    if (response.isSuccess && response.data != null) {
                        creationCallback.onSuccess(response.data);
                    } else {
                        String errorMessage = response.message;
                        int errorCode = response.code;

                        if (errorCode == -1) {
                            creationCallback.onNetworkFailure();
                        } else {
                            creationCallback.onError(errorMessage != null ? errorMessage : "Unknown error occurred", errorCode);
                        }
                    }
                } else {
                    creationCallback.onError("No response received", -1);
                }
            }
        });
    }

    /**
     * Edit  account charts
     * @param auth Authorization token (Bearer token)
     * @param id Chart id
     * @body Create Chart
     */
    public void editAccountChart(String auth,int id,String headName, String headType){
        if (fetchCallback == null) {
            throw new IllegalStateException("Fetch callback must be set before making API calls");
        }

        // Validate auth token
        if (auth == null || auth.trim().isEmpty()) {
            fetchCallback.onError("Authorization token is required", 401);
            return;
        }
        CreateChart createChart = new CreateChart();
        createChart.setChartName(headName);
        createChart.setChartType(headType);

        // Format auth header
        String formattedAuth = formatAuthHeader(auth);

        // Show loading
        fetchCallback.showLoading();
        viewModel.editAccountChart(formattedAuth,id,createChart).observe(lifecycleOwner, new Observer<GlobalRepository.ApiResponse<EmployeeResponse>>() {
            @Override
            public void onChanged(GlobalRepository.ApiResponse<EmployeeResponse> response) {

                if (response != null) {
                    if (response.isSuccess && response.data != null) {
                        creationCallback.onSuccess(response.data);
                    } else {
                        String errorMessage = response.message;
                        int errorCode = response.code;

                        if (errorCode == -1) {
                            creationCallback.onNetworkFailure();
                        } else {
                            creationCallback.onError(errorMessage != null ? errorMessage : "Unknown error occurred", errorCode);
                        }
                    }
                } else {
                    creationCallback.onError("No response received", -1);
                }

            }
        });
    }


    /**
     * Fetch all account charts
     * @param auth Authorization token (Bearer token)
     */

    public void getAccountChart(String auth) {
        if (fetchCallback == null) {
            throw new IllegalStateException("Fetch callback must be set before making API calls");
        }

        // Validate auth token
        if (auth == null || auth.trim().isEmpty()) {
            fetchCallback.onError("Authorization token is required", 401);
            return;
        }

        // Format auth header
        String formattedAuth = formatAuthHeader(auth);

        // Show loading
        fetchCallback.showLoading();

        // Make the API call
        viewModel.getAccountChart(formattedAuth).observe(lifecycleOwner, new Observer<GlobalRepository.ApiResponse<AccountGet>>() {
            @Override
            public void onChanged(GlobalRepository.ApiResponse<AccountGet> response) {
                fetchCallback.hideLoading();

                if (response != null) {
                    if (response.isSuccess && response.data != null) {
                        fetchCallback.onSuccess(response.data);
                    } else {
                        String errorMessage = response.message;
                        int errorCode = response.code;

                        if (errorCode == -1) {
                            fetchCallback.onNetworkFailure();
                        } else {
                            String userFriendlyMessage = getUserFriendlyErrorMessage(errorCode, errorMessage);
                            fetchCallback.onError(userFriendlyMessage, errorCode);
                        }
                    }
                } else {
                    fetchCallback.onError("No response received", -1);
                }
            }
        });
    }

    /**
     * Delete a chart of account
     * @param auth Authorization token (Bearer token)
     * @param chartId ID of the chart to delete
     */
    public void deleteAccountChart(String auth, int chartId) {
        if (deleteCallback == null) {
            throw new IllegalStateException("Delete callback must be set before making API calls");
        }

        // Validate inputs
        if (!isValidDeleteInput(auth, chartId)) {
            return;
        }

        // Format auth header
        String formattedAuth = formatAuthHeader(auth);

        // Show loading
        deleteCallback.showLoading();

        // Make the API call
        viewModel.deleteAccountChart(formattedAuth, chartId).observe(lifecycleOwner, new Observer<GlobalRepository.ApiResponse<ModelResponse>>() {
            @Override
            public void onChanged(GlobalRepository.ApiResponse<ModelResponse> response) {
                deleteCallback.hideLoading();

                if (response != null) {
                    if (response.isSuccess && response.data != null) {
                        deleteCallback.onSuccess(response.data);
                    } else {
                        String errorMessage = response.message;
                        int errorCode = response.code;

                        if (errorCode == -1) {
                            deleteCallback.onNetworkFailure();
                        } else {
                            String userFriendlyMessage = getDeleteErrorMessage(errorCode, errorMessage);
                            deleteCallback.onError(userFriendlyMessage, errorCode);
                        }
                    }
                } else {
                    deleteCallback.onError("No response received", -1);
                }
            }
        });
    }

    /**
     * Validate input parameters for chart creation
     */
    private boolean isValidInput(String headName, String headType) {
        if (headName == null || headName.trim().isEmpty()) {
            creationCallback.onError("Head name cannot be empty", 400);
            return false;
        }

        if (headName.trim().length() < 2) {
            creationCallback.onError("Head name must be at least 2 characters", 400);
            return false;
        }

        if (headType == null || headType.trim().isEmpty()) {
            creationCallback.onError("Head type must be selected", 400);
            return false;
        }

        if (!headType.equals("INCOME") && !headType.equals("EXPENSE")) {
            creationCallback.onError("Invalid head type. Must be INCOME or EXPENSE", 400);
            return false;
        }

        return true;
    }

    /**
     * Validate input parameters for chart deletion
     */
    private boolean isValidDeleteInput(String auth, int chartId) {
        if (auth == null || auth.trim().isEmpty()) {
            deleteCallback.onError("Authorization token is required", 401);
            return false;
        }

        if (chartId <= 0) {
            deleteCallback.onError("Invalid chart ID provided", 400);
            return false;
        }

        return true;
    }

    /**
     * Format auth header with Bearer prefix if not already present
     */
    public static String formatAuthHeader(String token) {
        if (token == null || token.isEmpty()) {
            return "";
        }

        if (token.startsWith("Bearer ")) {
            return token;
        }

        return "Bearer " + token;
    }

    /**
     * Check if error is due to authentication
     */
    public static boolean isAuthError(int errorCode) {
        return errorCode == 401 || errorCode == 403;
    }

    /**
     * Check if error is due to server issues
     */
    public static boolean isServerError(int errorCode) {
        return errorCode >= 500 && errorCode < 600;
    }

    /**
     * Get user-friendly error message based on error code
     */
    public static String getUserFriendlyErrorMessage(int errorCode, String originalMessage) {
        switch (errorCode) {
            case 400:
                return "Invalid data provided. Please check your input.";
            case 401:
                return "Authentication failed. Please login again.";
            case 403:
                return "You don't have permission to perform this action.";
            case 404:
                return "The requested resource was not found.";
            case 409:
                return "This account head already exists.";
            case 422:
                return "The data provided is not valid.";
            case 500:
                return "Server error occurred. Please try again later.";
            case 503:
                return "Service is temporarily unavailable. Please try again later.";
            default:
                return originalMessage != null && !originalMessage.isEmpty()
                        ? originalMessage
                        : "An unexpected error occurred. Please try again.";
        }
    }

    /**
     * Get user-friendly error message for delete operations
     */
    public static String getDeleteErrorMessage(int errorCode, String originalMessage) {
        switch (errorCode) {
            case 400:
                return "Invalid chart ID provided.";
            case 401:
                return "Authentication failed. Please login again.";
            case 403:
                return "You don't have permission to delete this chart.";
            case 404:
                return "Chart not found. It may have been already deleted.";
            case 409:
                return "Cannot delete chart. It may be in use by other records.";
            case 422:
                return "Chart cannot be deleted due to data constraints.";
            case 500:
                return "Server error occurred while deleting. Please try again later.";
            case 503:
                return "Service is temporarily unavailable. Please try again later.";
            default:
                return originalMessage != null && !originalMessage.isEmpty()
                        ? originalMessage
                        : "Failed to delete chart. Please try again.";
        }
    }
}