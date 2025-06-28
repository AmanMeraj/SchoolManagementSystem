package com.school.schoolmanagement.HelperClasses;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import com.school.schoolmanagement.GlobalRepository.GlobalRepository;
import com.school.schoolmanagement.GlobalViewModel.ViewModel;
import com.school.schoolmanagement.Model.ClassTestResult;

/**
 * Helper class for Class Test Result API operations
 */
public class ClassTestResultApiHelper {

    public interface ClassSubjectCallback {
        void onClassSubjectSuccess(ClassTestResult response);
        void onClassSubjectError(String errorMessage, int errorCode);
        void onClassSubjectNetworkFailure();
        void showClassSubjectLoading();
        void hideClassSubjectLoading();
    }

    public interface StudentSubjectCallback {
        void onStudentSubjectSuccess(ClassTestResult response);
        void onStudentSubjectError(String errorMessage, int errorCode);
        void onStudentSubjectNetworkFailure();
        void showStudentSubjectLoading();
        void hideStudentSubjectLoading();
    }

    public interface DateRangeCallback {
        void onDateRangeSuccess(ClassTestResult response);
        void onDateRangeError(String errorMessage, int errorCode);
        void onDateRangeNetworkFailure();
        void showDateRangeLoading();
        void hideDateRangeLoading();
    }

    private ViewModel viewModel;
    private LifecycleOwner lifecycleOwner;
    private ClassSubjectCallback classSubjectCallback;
    private StudentSubjectCallback studentSubjectCallback;
    private DateRangeCallback dateRangeCallback;

    public ClassTestResultApiHelper(ViewModel viewModel, LifecycleOwner lifecycleOwner) {
        this.viewModel = viewModel;
        this.lifecycleOwner = lifecycleOwner;
    }

    public void setClassSubjectCallback(ClassSubjectCallback callback) {
        this.classSubjectCallback = callback;
    }

    public void setStudentSubjectCallback(StudentSubjectCallback callback) {
        this.studentSubjectCallback = callback;
    }

    public void setDateRangeCallback(DateRangeCallback callback) {
        this.dateRangeCallback = callback;
    }

    /**
     * Get class test results by class and subject
     * @param auth Authorization token (Bearer token)
     * @param classId ID of the class
     * @param subjectId ID of the subject
     */
    public void getClassSubjectWise(String auth, int classId, int subjectId) {
        if (classSubjectCallback == null) {
            throw new IllegalStateException("Class subject callback must be set before making API calls");
        }

        // Validate inputs
        if (!isValidClassSubjectInput(auth, classId, subjectId)) {
            return;
        }

        // Format auth header
        String formattedAuth = formatAuthHeader(auth);

        // Show loading
        classSubjectCallback.showClassSubjectLoading();

        // Make the API call
        viewModel.getClassSubjectWise(formattedAuth, classId, subjectId).observe(lifecycleOwner, new Observer<GlobalRepository.ApiResponse<ClassTestResult>>() {
            @Override
            public void onChanged(GlobalRepository.ApiResponse<ClassTestResult> response) {
                classSubjectCallback.hideClassSubjectLoading();

                if (response != null) {
                    if (response.isSuccess && response.data != null) {
                        classSubjectCallback.onClassSubjectSuccess(response.data);
                    } else {
                        String errorMessage = response.message;
                        int errorCode = response.code;

                        if (errorCode == -1) {
                            classSubjectCallback.onClassSubjectNetworkFailure();
                        } else {
                            String userFriendlyMessage = getUserFriendlyErrorMessage(errorCode, errorMessage);
                            classSubjectCallback.onClassSubjectError(userFriendlyMessage, errorCode);
                        }
                    }
                } else {
                    classSubjectCallback.onClassSubjectError("No response received", -1);
                }
            }
        });
    }

    /**
     * Get student test results by student and subject
     * @param auth Authorization token (Bearer token)
     * @param studentId ID of the student
     * @param subjectId ID of the subject
     */
    public void getStudentSubjectWise(String auth, int studentId, int subjectId) {
        if (studentSubjectCallback == null) {
            throw new IllegalStateException("Student subject callback must be set before making API calls");
        }

        // Validate inputs
        if (!isValidStudentSubjectInput(auth, studentId, subjectId)) {
            return;
        }

        // Format auth header
        String formattedAuth = formatAuthHeader(auth);

        // Show loading
        studentSubjectCallback.showStudentSubjectLoading();

        // Make the API call
        viewModel.getStudentSubjectWise(formattedAuth, studentId, subjectId).observe(lifecycleOwner, new Observer<GlobalRepository.ApiResponse<ClassTestResult>>() {
            @Override
            public void onChanged(GlobalRepository.ApiResponse<ClassTestResult> response) {
                studentSubjectCallback.hideStudentSubjectLoading();

                if (response != null) {
                    if (response.isSuccess && response.data != null) {
                        studentSubjectCallback.onStudentSubjectSuccess(response.data);
                    } else {
                        String errorMessage = response.message;
                        int errorCode = response.code;

                        if (errorCode == -1) {
                            studentSubjectCallback.onStudentSubjectNetworkFailure();
                        } else {
                            String userFriendlyMessage = getUserFriendlyErrorMessage(errorCode, errorMessage);
                            studentSubjectCallback.onStudentSubjectError(userFriendlyMessage, errorCode);
                        }
                    }
                } else {
                    studentSubjectCallback.onStudentSubjectError("No response received", -1);
                }
            }
        });
    }

    /**
     * Get test results by date range
     * @param auth Authorization token (Bearer token)
     * @param startDate Start date in proper format (e.g., "2024-01-01")
     * @param endDate End date in proper format (e.g., "2024-12-31")
     */
    public void getDateRangeWise(String auth, String startDate, String endDate) {
        if (dateRangeCallback == null) {
            throw new IllegalStateException("Date range callback must be set before making API calls");
        }

        // Validate inputs
        if (!isValidDateRangeInput(auth, startDate, endDate)) {
            return;
        }

        // Format auth header
        String formattedAuth = formatAuthHeader(auth);

        // Show loading
        dateRangeCallback.showDateRangeLoading();

        // Make the API call
        viewModel.getDateRangeWise(formattedAuth, startDate, endDate).observe(lifecycleOwner, new Observer<GlobalRepository.ApiResponse<ClassTestResult>>() {
            @Override
            public void onChanged(GlobalRepository.ApiResponse<ClassTestResult> response) {
                dateRangeCallback.hideDateRangeLoading();

                if (response != null) {
                    if (response.isSuccess && response.data != null) {
                        dateRangeCallback.onDateRangeSuccess(response.data);
                    } else {
                        String errorMessage = response.message;
                        int errorCode = response.code;

                        if (errorCode == -1) {
                            dateRangeCallback.onDateRangeNetworkFailure();
                        } else {
                            String userFriendlyMessage = getUserFriendlyErrorMessage(errorCode, errorMessage);
                            dateRangeCallback.onDateRangeError(userFriendlyMessage, errorCode);
                        }
                    }
                } else {
                    dateRangeCallback.onDateRangeError("No response received", -1);
                }
            }
        });
    }

    /**
     * Validate input parameters for class subject wise API
     */
    private boolean isValidClassSubjectInput(String auth, int classId, int subjectId) {
        if (auth == null || auth.trim().isEmpty()) {
            classSubjectCallback.onClassSubjectError("Authorization token is required", 401);
            return false;
        }

        if (classId <= 0) {
            classSubjectCallback.onClassSubjectError("Valid class ID is required", 400);
            return false;
        }

        if (subjectId <= 0) {
            classSubjectCallback.onClassSubjectError("Valid subject ID is required", 400);
            return false;
        }

        return true;
    }

    /**
     * Validate input parameters for student subject wise API
     */
    private boolean isValidStudentSubjectInput(String auth, int studentId, int subjectId) {
        if (auth == null || auth.trim().isEmpty()) {
            studentSubjectCallback.onStudentSubjectError("Authorization token is required", 401);
            return false;
        }

        if (studentId <= 0) {
            studentSubjectCallback.onStudentSubjectError("Valid student ID is required", 400);
            return false;
        }

        if (subjectId <= 0) {
            studentSubjectCallback.onStudentSubjectError("Valid subject ID is required", 400);
            return false;
        }

        return true;
    }

    /**
     * Validate input parameters for date range wise API
     */
    private boolean isValidDateRangeInput(String auth, String startDate, String endDate) {
        if (auth == null || auth.trim().isEmpty()) {
            dateRangeCallback.onDateRangeError("Authorization token is required", 401);
            return false;
        }

        if (startDate == null || startDate.trim().isEmpty()) {
            dateRangeCallback.onDateRangeError("Start date is required", 400);
            return false;
        }

        if (endDate == null || endDate.trim().isEmpty()) {
            dateRangeCallback.onDateRangeError("End date is required", 400);
            return false;
        }

        // Basic date format validation (you can make this more robust)
        if (!isValidDateFormat(startDate)) {
            dateRangeCallback.onDateRangeError("Invalid start date format. Use YYYY-MM-DD", 400);
            return false;
        }

        if (!isValidDateFormat(endDate)) {
            dateRangeCallback.onDateRangeError("Invalid end date format. Use YYYY-MM-DD", 400);
            return false;
        }

        return true;
    }

    /**
     * Basic date format validation
     */
    private boolean isValidDateFormat(String date) {
        // Basic check for YYYY-MM-DD format
        if (date.length() != 10) return false;
        if (date.charAt(4) != '-' || date.charAt(7) != '-') return false;

        try {
            String[] parts = date.split("-");
            if (parts.length != 3) return false;

            int year = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);
            int day = Integer.parseInt(parts[2]);

            return year > 1900 && year < 3000 &&
                    month >= 1 && month <= 12 &&
                    day >= 1 && day <= 31;
        } catch (NumberFormatException e) {
            return false;
        }
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
                return "Invalid parameters provided. Please check your input.";
            case 401:
                return "Authentication failed. Please login again.";
            case 403:
                return "You don't have permission to access this data.";
            case 404:
                return "No test results found for the specified criteria.";
            case 422:
                return "The parameters provided are not valid.";
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
}