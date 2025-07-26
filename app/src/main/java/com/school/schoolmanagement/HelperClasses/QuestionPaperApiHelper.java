package com.school.schoolmanagement.HelperClasses;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import com.school.schoolmanagement.Admin.Model.CreateQuestionPaper;
import com.school.schoolmanagement.Admin.Model.QuestionPaperResponse;
import com.school.schoolmanagement.GlobalRepository.GlobalRepository;
import com.school.schoolmanagement.GlobalViewModel.ViewModel;
import com.school.schoolmanagement.Model.EmployeeResponse;

/**
 * Helper class for Question Paper API operations
 */
public class QuestionPaperApiHelper {

    public interface QuestionPaperCreationCallback {
        void onQuestionPaperCreationSuccess(EmployeeResponse response);
        void onQuestionPaperCreationError(String errorMessage, int errorCode);
        void onQuestionPaperCreationNetworkFailure();
        void showQuestionPaperCreationLoading();
        void hideQuestionPaperCreationLoading();
    }

    public interface QuestionPaperFetchCallback {
        void onQuestionPaperFetchSuccess(QuestionPaperResponse response);
        void onQuestionPaperFetchError(String errorMessage, int errorCode);
        void onQuestionPaperFetchNetworkFailure();
        void showQuestionPaperFetchLoading();
        void hideQuestionPaperFetchLoading();
    }

    public interface QuestionPaperUpdateCallback {
        void onQuestionPaperUpdateSuccess(EmployeeResponse response);
        void onQuestionPaperUpdateError(String errorMessage, int errorCode);
        void onQuestionPaperUpdateNetworkFailure();
        void showQuestionPaperUpdateLoading();
        void hideQuestionPaperUpdateLoading();
    }

    public interface QuestionPaperDeleteCallback {
        void onQuestionPaperDeleteSuccess(EmployeeResponse response);
        void onQuestionPaperDeleteError(String errorMessage, int errorCode);
        void onQuestionPaperDeleteNetworkFailure();
        void showQuestionPaperDeleteLoading();
        void hideQuestionPaperDeleteLoading();
    }

    private ViewModel viewModel;
    private LifecycleOwner lifecycleOwner;
    private QuestionPaperCreationCallback creationCallback;
    private QuestionPaperFetchCallback fetchCallback;
    private QuestionPaperUpdateCallback updateCallback;
    private QuestionPaperDeleteCallback deleteCallback;

    public QuestionPaperApiHelper(ViewModel viewModel, LifecycleOwner lifecycleOwner) {
        this.viewModel = viewModel;
        this.lifecycleOwner = lifecycleOwner;
    }

    public void setCreationCallback(QuestionPaperCreationCallback callback) {
        this.creationCallback = callback;
    }

    public void setFetchCallback(QuestionPaperFetchCallback callback) {
        this.fetchCallback = callback;
    }

    public void setUpdateCallback(QuestionPaperUpdateCallback callback) {
        this.updateCallback = callback;
    }

    public void setDeleteCallback(QuestionPaperDeleteCallback callback) {
        this.deleteCallback = callback;
    }

    /**
     * Create a new question paper
     * @param auth Authorization token (Bearer token)
     * @param createQuestionPaper Question paper data object
     */
    public void createQuestionPaper(String auth, CreateQuestionPaper createQuestionPaper) {
        if (creationCallback == null) {
            throw new IllegalStateException("Creation callback must be set before making API calls");
        }

        // Validate inputs
        if (!isValidInput(auth, createQuestionPaper)) {
            return;
        }

        // Format auth header
        String formattedAuth = formatAuthHeader(auth);

        // Show loading
        creationCallback.showQuestionPaperCreationLoading();

        // Make the API call
        viewModel.addQuestionPaper(formattedAuth, createQuestionPaper).observe(lifecycleOwner, new Observer<GlobalRepository.ApiResponse<EmployeeResponse>>() {
            @Override
            public void onChanged(GlobalRepository.ApiResponse<EmployeeResponse> response) {
                creationCallback.hideQuestionPaperCreationLoading();

                if (response != null) {
                    if (response.isSuccess && response.data != null) {
                        creationCallback.onQuestionPaperCreationSuccess(response.data);
                    } else {
                        String errorMessage = response.message;
                        int errorCode = response.code;

                        if (errorCode == -1) {
                            creationCallback.onQuestionPaperCreationNetworkFailure();
                        } else {
                            String userFriendlyMessage = getQuestionPaperCreationErrorMessage(errorCode, errorMessage);
                            creationCallback.onQuestionPaperCreationError(userFriendlyMessage, errorCode);
                        }
                    }
                } else {
                    creationCallback.onQuestionPaperCreationError("No response received", -1);
                }
            }
        });
    }

    /**
     * Fetch all question papers
     * @param auth Authorization token (Bearer token)
     */
    public void getQuestionPaper(String auth) {
        if (fetchCallback == null) {
            throw new IllegalStateException("Fetch callback must be set before making API calls");
        }

        // Validate auth token
        if (auth == null || auth.trim().isEmpty()) {
            fetchCallback.onQuestionPaperFetchError("Authorization token is required", 401);
            return;
        }

        // Format auth header
        String formattedAuth = formatAuthHeader(auth);

        // Show loading
        fetchCallback.showQuestionPaperFetchLoading();

        // Make the API call
        viewModel.getQuestionPaper(formattedAuth).observe(lifecycleOwner, new Observer<GlobalRepository.ApiResponse<QuestionPaperResponse>>() {
            @Override
            public void onChanged(GlobalRepository.ApiResponse<QuestionPaperResponse> response) {
                fetchCallback.hideQuestionPaperFetchLoading();

                if (response != null) {
                    if (response.isSuccess && response.data != null) {
                        fetchCallback.onQuestionPaperFetchSuccess(response.data);
                    } else {
                        String errorMessage = response.message;
                        int errorCode = response.code;

                        if (errorCode == -1) {
                            fetchCallback.onQuestionPaperFetchNetworkFailure();
                        } else {
                            String userFriendlyMessage = getQuestionPaperFetchErrorMessage(errorCode, errorMessage);
                            fetchCallback.onQuestionPaperFetchError(userFriendlyMessage, errorCode);
                        }
                    }
                } else {
                    fetchCallback.onQuestionPaperFetchError("No response received", -1);
                }
            }
        });
    }

    /**
     * Update an existing question paper
     * @param auth Authorization token (Bearer token)
     * @param questionPaperId The ID of the question paper to update
     * @param createQuestionPaper Updated question paper data object
     */
    public void updateQuestionPaper(String auth, int questionPaperId, CreateQuestionPaper createQuestionPaper) {
        if (updateCallback == null) {
            throw new IllegalStateException("Update callback must be set before making API calls");
        }

        // Validate inputs
        if (!isValidUpdateInput(auth, questionPaperId, createQuestionPaper)) {
            return;
        }

        // Format auth header
        String formattedAuth = formatAuthHeader(auth);

        // Show loading
        updateCallback.showQuestionPaperUpdateLoading();

        // Make the API call
        viewModel.updateQuestionPaper(formattedAuth, questionPaperId, createQuestionPaper).observe(lifecycleOwner, new Observer<GlobalRepository.ApiResponse<EmployeeResponse>>() {
            @Override
            public void onChanged(GlobalRepository.ApiResponse<EmployeeResponse> response) {
                updateCallback.hideQuestionPaperUpdateLoading();

                if (response != null) {
                    if (response.isSuccess && response.data != null) {
                        updateCallback.onQuestionPaperUpdateSuccess(response.data);
                    } else {
                        String errorMessage = response.message;
                        int errorCode = response.code;

                        if (errorCode == -1) {
                            updateCallback.onQuestionPaperUpdateNetworkFailure();
                        } else {
                            String userFriendlyMessage = getQuestionPaperUpdateErrorMessage(errorCode, errorMessage);
                            updateCallback.onQuestionPaperUpdateError(userFriendlyMessage, errorCode);
                        }
                    }
                } else {
                    updateCallback.onQuestionPaperUpdateError("No response received", -1);
                }
            }
        });
    }

    /**
     * Delete a question paper
     * @param auth Authorization token (Bearer token)
     * @param questionPaperId The ID of the question paper to delete
     */
    public void deleteQuestionPaper(String auth, int questionPaperId) {
        if (deleteCallback == null) {
            throw new IllegalStateException("Delete callback must be set before making API calls");
        }

        // Validate inputs
        if (!isValidDeleteInput(auth, questionPaperId)) {
            return;
        }

        // Format auth header
        String formattedAuth = formatAuthHeader(auth);

        // Show loading
        deleteCallback.showQuestionPaperDeleteLoading();

        // Make the API call
        viewModel.deleteQuestionPaper(formattedAuth, questionPaperId).observe(lifecycleOwner, new Observer<GlobalRepository.ApiResponse<EmployeeResponse>>() {
            @Override
            public void onChanged(GlobalRepository.ApiResponse<EmployeeResponse> response) {
                deleteCallback.hideQuestionPaperDeleteLoading();

                if (response != null) {
                    if (response.isSuccess && response.data != null) {
                        deleteCallback.onQuestionPaperDeleteSuccess(response.data);
                    } else {
                        String errorMessage = response.message;
                        int errorCode = response.code;

                        if (errorCode == -1) {
                            deleteCallback.onQuestionPaperDeleteNetworkFailure();
                        } else {
                            String userFriendlyMessage = getQuestionPaperDeleteErrorMessage(errorCode, errorMessage);
                            deleteCallback.onQuestionPaperDeleteError(userFriendlyMessage, errorCode);
                        }
                    }
                } else {
                    deleteCallback.onQuestionPaperDeleteError("No response received", -1);
                }
            }
        });
    }

    /**
     * Validate input parameters for question paper creation
     */
    private boolean isValidInput(String auth, CreateQuestionPaper createQuestionPaper) {
        if (auth == null || auth.trim().isEmpty()) {
            creationCallback.onQuestionPaperCreationError("Authorization token is required", 401);
            return false;
        }

        if (createQuestionPaper == null) {
            creationCallback.onQuestionPaperCreationError("Question paper data cannot be null", 400);
            return false;
        }

        // Add specific validation based on CreateQuestionPaper model fields
        // Example validations (adjust based on your model):
        /*
        if (createQuestionPaper.getTitle() == null || createQuestionPaper.getTitle().trim().isEmpty()) {
            creationCallback.onQuestionPaperCreationError("Question paper title is required", 400);
            return false;
        }

        if (createQuestionPaper.getSubject() == null || createQuestionPaper.getSubject().trim().isEmpty()) {
            creationCallback.onQuestionPaperCreationError("Subject is required", 400);
            return false;
        }
        */

        return true;
    }

    /**
     * Validate input parameters for question paper update
     */
    private boolean isValidUpdateInput(String auth, int questionPaperId, CreateQuestionPaper createQuestionPaper) {
        if (auth == null || auth.trim().isEmpty()) {
            updateCallback.onQuestionPaperUpdateError("Authorization token is required", 401);
            return false;
        }

        if (questionPaperId <= 0) {
            updateCallback.onQuestionPaperUpdateError("Valid question paper ID is required", 400);
            return false;
        }

        if (createQuestionPaper == null) {
            updateCallback.onQuestionPaperUpdateError("Question paper data cannot be null", 400);
            return false;
        }

        return true;
    }

    /**
     * Validate input parameters for question paper deletion
     */
    private boolean isValidDeleteInput(String auth, int questionPaperId) {
        if (auth == null || auth.trim().isEmpty()) {
            deleteCallback.onQuestionPaperDeleteError("Authorization token is required", 401);
            return false;
        }

        if (questionPaperId <= 0) {
            deleteCallback.onQuestionPaperDeleteError("Valid question paper ID is required", 400);
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
     * Get user-friendly error message for question paper creation
     */
    public static String getQuestionPaperCreationErrorMessage(int errorCode, String originalMessage) {
        switch (errorCode) {
            case 400:
                return "Invalid question paper data provided. Please check your input.";
            case 401:
                return "Authentication failed. Please login again.";
            case 403:
                return "You don't have permission to create question papers.";
            case 404:
                return "The requested resource was not found.";
            case 409:
                return "A question paper with similar details already exists.";
            case 422:
                return "The question paper data provided is not valid.";
            case 500:
                return "Server error occurred while creating question paper. Please try again later.";
            case 503:
                return "Service is temporarily unavailable. Please try again later.";
            default:
                return originalMessage != null && !originalMessage.isEmpty()
                        ? originalMessage
                        : "Failed to create question paper. Please try again.";
        }
    }

    /**
     * Get user-friendly error message for question paper fetch operations
     */
    public static String getQuestionPaperFetchErrorMessage(int errorCode, String originalMessage) {
        switch (errorCode) {
            case 400:
                return "Invalid request parameters.";
            case 401:
                return "Authentication failed. Please login again.";
            case 403:
                return "You don't have permission to view question papers.";
            case 404:
                return "No question papers found.";
            case 500:
                return "Server error occurred while fetching question papers. Please try again later.";
            case 503:
                return "Service is temporarily unavailable. Please try again later.";
            default:
                return originalMessage != null && !originalMessage.isEmpty()
                        ? originalMessage
                        : "Failed to fetch question papers. Please try again.";
        }
    }

    /**
     * Get user-friendly error message for question paper update operations
     */
    public static String getQuestionPaperUpdateErrorMessage(int errorCode, String originalMessage) {
        switch (errorCode) {
            case 400:
                return "Invalid question paper data or ID provided. Please check your input.";
            case 401:
                return "Authentication failed. Please login again.";
            case 403:
                return "You don't have permission to update question papers.";
            case 404:
                return "Question paper not found. It may have been deleted.";
            case 409:
                return "Update conflict. The question paper may have been modified by another user.";
            case 422:
                return "The question paper data provided is not valid.";
            case 500:
                return "Server error occurred while updating question paper. Please try again later.";
            case 503:
                return "Service is temporarily unavailable. Please try again later.";
            default:
                return originalMessage != null && !originalMessage.isEmpty()
                        ? originalMessage
                        : "Failed to update question paper. Please try again.";
        }
    }

    /**
     * Get user-friendly error message for question paper delete operations
     */
    public static String getQuestionPaperDeleteErrorMessage(int errorCode, String originalMessage) {
        switch (errorCode) {
            case 400:
                return "Invalid question paper ID provided.";
            case 401:
                return "Authentication failed. Please login again.";
            case 403:
                return "You don't have permission to delete question papers.";
            case 404:
                return "Question paper not found. It may have already been deleted.";
            case 409:
                return "Cannot delete question paper. It may be in use by active exams.";
            case 500:
                return "Server error occurred while deleting question paper. Please try again later.";
            case 503:
                return "Service is temporarily unavailable. Please try again later.";
            default:
                return originalMessage != null && !originalMessage.isEmpty()
                        ? originalMessage
                        : "Failed to delete question paper. Please try again.";
        }
    }
}