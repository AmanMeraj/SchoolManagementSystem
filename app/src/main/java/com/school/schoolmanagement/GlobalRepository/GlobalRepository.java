package com.school.schoolmanagement.GlobalRepository;

import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.school.schoolmanagement.Admin.Model.AllEmployees;
import com.school.schoolmanagement.Admin.Model.AllEmployeesResponse;
import com.school.schoolmanagement.Admin.Model.ClassListResponse;
import com.school.schoolmanagement.Admin.Model.ClassModel;
import com.school.schoolmanagement.Admin.Model.ClassesWithSubjectsResponse;
import com.school.schoolmanagement.Admin.Model.CreateClass;
import com.school.schoolmanagement.Admin.Model.Employee2;
import com.school.schoolmanagement.Admin.Model.EmployeeList;
import com.school.schoolmanagement.Admin.Model.SubjectCreationResponse;
import com.school.schoolmanagement.Admin.Model.SubjectRequestBody;
import com.school.schoolmanagement.Admin.Model.SubjectUpdateRequest;
import com.school.schoolmanagement.GlobalResponse.LoginResponse;
import com.school.schoolmanagement.Model.Employee;
import com.school.schoolmanagement.Model.EmployeeRequestDto;
import com.school.schoolmanagement.Model.EmployeeResponse;
import com.school.schoolmanagement.Model.Login;
import com.school.schoolmanagement.Model.ModelResponse;
import com.school.schoolmanagement.retrofit.ApiRequest;
import com.school.schoolmanagement.retrofit.RetrofitRequest;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GlobalRepository {
    private static final String TAG = GlobalRepository.class.getSimpleName();
    private final ApiRequest apiRequest;
    private static final Object SESSION_LOCK = new Object();
    private static volatile boolean isHandlingSessionExpiry = false;

    public static final int ERROR_SESSION_EXPIRED = 401;
    // Add loading state
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    public GlobalRepository() {
        apiRequest = RetrofitRequest.getRetrofitInstance().create(ApiRequest.class);
    }

    // Getter for loading state
    public LiveData<Boolean> getLoadingState() {
        return isLoading;
    }

    // Method to set loading status
    private void setLoading(boolean loading) {
        isLoading.postValue(loading);
    }


    public LiveData<ApiResponse<LoginResponse>> login(Login login) {
        final MutableLiveData<ApiResponse<LoginResponse>> liveData = new MutableLiveData<>();

        // Set loading to true before API call
        setLoading(true);

        Call<LoginResponse> call = apiRequest.postLogin(login);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                // Set loading to false when response is received
                setLoading(false);

                if (response.isSuccessful() && response.body()!=null) {
                    liveData.setValue(new ApiResponse<>(response.body(), true, null, -1));
                } else if (response.code() == ERROR_SESSION_EXPIRED) {
                    handleSessionExpiry(liveData);
                } else {
                    handleErrorResponseLogin(response, liveData);
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                // Set loading to false on failure
                setLoading(false);

                handleNetworkFailure(call, t, liveData);
            }
        });

        return liveData;
    }

    /**
     * Creates a new employee using the provided data.
     *
     * @param auth Authorization token.
     * @param employee Employee data object.
     * @return LiveData containing ApiResponse wrapped in EmployeeResponse.
     */
    public LiveData<ApiResponse<EmployeeResponse>> createEmployee(String auth, Employee employee) {
        final MutableLiveData<ApiResponse<EmployeeResponse>> liveData = new MutableLiveData<>();

        // Create multipart form data
        MultipartBody.Part imagePart = null;

        // Check if profile image data exists
        if (employee.getProfileImageData() != null) {
            RequestBody requestFile = RequestBody.create(
                    MediaType.parse("image/jpeg"),
                    employee.getProfileImageData()
            );

            imagePart = MultipartBody.Part.createFormData(
                    "profilePicture", "profile_image.jpg", requestFile
            );
        }

        // Convert the employee object to JSON
        Gson gson = new Gson();
        String employeeJson = gson.toJson(employee);

        // Wrap the JSON into a RequestBody
        RequestBody employeeBody = RequestBody.create(
                MediaType.parse("application/json"), employeeJson
        );

        // Make the API call
        apiRequest.createEmployee(auth, employeeBody, imagePart)
                .enqueue(new Callback<EmployeeResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<EmployeeResponse> call, @NonNull Response<EmployeeResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            liveData.setValue(new ApiResponse<>(response.body(), true, null, -1));
                        } else {
                            handleErrorResponse(response, liveData);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<EmployeeResponse> call, @NonNull Throwable t) {
                        Log.e(TAG, "API call failed: " + t.getMessage());
                        liveData.setValue(new ApiResponse<>(null, false, "Failed to connect. Please check your network.", -1));
                    }
                });

        return liveData;
    }


//    public LiveData<ApiResponse<EmployeeResponse>> updateEmployee(String auth, int employeeId, Employee employee) {
//        MutableLiveData<ApiResponse<EmployeeResponse>> liveData = new MutableLiveData<>();
//
//        try {
//
//            // Convert DTO to JSON
//            Gson gson = new Gson();
//            String employeeJson = gson.toJson(employeeDto);
//            RequestBody employeeBody = RequestBody.create(MediaType.parse("application/json"), employeeJson);
//
//            // Handle profile image
//            MultipartBody.Part profileImagePart = null;
//            if (employee.getProfileImageData() != null && employee.getProfileImageData().length > 0) {
//                RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), employee.getProfileImageData());
//                profileImagePart = MultipartBody.Part.createFormData("profilePicture", "profile.jpg", requestFile);
//            }
//
//            // Make API call
//            apiRequest.updateEmployee(auth, employeeId, employeeBody, profileImagePart)
//                    .enqueue(new Callback<EmployeeResponse>() {
//                        @Override
//                        public void onResponse(Call<EmployeeResponse> call, Response<EmployeeResponse> response) {
//                            if (response.isSuccessful() && response.body() != null) {
//                                liveData.setValue(new ApiResponse<>(true, response.body(), "Employee updated successfully"));
//                            } else {
//                                try {
//                                    String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
//                                    liveData.setValue(new ApiResponse<>(false, null, errorBody));
//                                } catch (IOException e) {
//                                    liveData.setValue(new ApiResponse<>(false, null, e.getMessage()));
//                                }
//                            }
//                        }
//
//                        @Override
//                        public void onFailure(Call<EmployeeResponse> call, Throwable t) {
//                            liveData.setValue(new ApiResponse<>(false, null, t.getMessage()));
//                        }
//                    });
//        } catch (Exception e) {
//            liveData.setValue(new ApiResponse<>(false, null, e.getMessage()));
//        }
//
//        return liveData;
//    }


    // Helper method to create RequestBody
    private RequestBody createRequestBody(String value) {
        return value != null ? RequestBody.create(MediaType.parse("text/plain"), value) : null;
    }

    // Helper method to add a field to the map if it's not null
    private void addFieldIfNotNull(Map<String, RequestBody> map, String key, String value) {
        if (value != null) {
            map.put(key, createRequestBody(value));
        }
    }

    /**
     * Get an employee by ID.
     *
     * @param auth Authorization token.
     * @param employeeId ID of the employee to fetch.
     * @return LiveData containing ApiResponse wrapped in Employee.
     */
    public LiveData<ApiResponse<Employee2>> getEmployee(String auth, int employeeId) {
        final MutableLiveData<ApiResponse<Employee2>> liveData = new MutableLiveData<>();

        Log.d(TAG, "Making API call for employee ID: " + employeeId + " with auth: " +
                (auth != null ? (auth.substring(0, Math.min(15, auth.length())) + "...") : "null"));

        apiRequest.getEmployee(auth, employeeId).enqueue(new Callback<Employee2>() {
            @Override
            public void onResponse(@NonNull Call<Employee2> call, @NonNull Response<Employee2> response) {
                Log.d(TAG, "Response received - Status Code: " + response.code() + ", Message: " + response.message());

                if (response.isSuccessful()) {
                    Employee2 employeeData = response.body();

                    if (employeeData != null) {
                        // Log each field individually to see what's coming through
                        Log.d(TAG, "Employee response details:");
                        Log.d(TAG, "- Name: " + employeeData.data.getName());
                        Log.d(TAG, "- Mobile: " + employeeData.data.getMobileNumber());
                        Log.d(TAG, "- Email: " + employeeData.data.getEmailAddress());
                        Log.d(TAG, "- Role: " + employeeData.data.getEmployeeRole());
                        Log.d(TAG, "- Join Date: " + employeeData.data.getDateOfJoining());
                        Log.d(TAG, "- Salary: " + employeeData.data.getMonthlySalary());
                        // Add more fields as needed

                        // Also log the full JSON for comparison
                        String fullJson = new Gson().toJson(employeeData);
                        Log.d(TAG, "Full employee data JSON: " + fullJson);

                        // Check if essential fields are missing
                        boolean hasEssentialData =
                                employeeData.data.getName() != null &&
                                        employeeData.data.getMobileNumber() != null &&
                                        employeeData.data.getEmployeeRole() != null;

                        if (!hasEssentialData) {
                            Log.w(TAG, "WARNING: Employee data missing essential fields!");
                        }

                        liveData.setValue(new ApiResponse<>(employeeData, true, null, -1));
                    } else {
                        Log.e(TAG, "Response body is null despite successful response (code " + response.code() + ")");
                        liveData.setValue(new ApiResponse<>(null, false, "Empty employee data received from server", -1));
                    }
                } else {
                    // For error responses, try to get the error body as well
                    String errorBody = "No error body";
                    try {
                        if (response.errorBody() != null) {
                            errorBody = response.errorBody().string();
                        }
                    } catch (IOException e) {
                        errorBody = "Error reading error body: " + e.getMessage();
                    }

                    Log.e(TAG, "API Error: Code " + response.code() + ", Error Body: " + errorBody);
                    handleEmployeeErrorResponse2(response, liveData);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Employee2> call, @NonNull Throwable t) {
                Log.e(TAG, "API call failed: " + t.getMessage(), t);
                liveData.setValue(new ApiResponse<>(null, false, "Failed to connect. Please check your network.", -1));
            }
        });

        return liveData;
    }

    public LiveData<ApiResponse<List<AllEmployees>>> getAllEmployees(String auth) {
        final MutableLiveData<ApiResponse<List<AllEmployees>>> liveData = new MutableLiveData<>();

        // Validate auth token
        if (auth == null || auth.isEmpty()) {
            liveData.setValue(new ApiResponse<>(null, false, "Authentication token is missing", 401));
            return liveData;
        }

        Log.d(TAG, "Making API call to get all employees");

        apiRequest.getAllEmployees(auth).enqueue(new Callback<AllEmployeesResponse>() {
            @Override
            public void onResponse(@NonNull Call<AllEmployeesResponse> call, @NonNull Response<AllEmployeesResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Success: Return data list from response body
                    AllEmployeesResponse responseBody = response.body();
                    Log.d(TAG, "API call successful. Status: " + responseBody.status);

                    if (responseBody.data != null) {
                        Log.d(TAG, "Received " + responseBody.data.size() + " employees");
                        liveData.setValue(new ApiResponse<>(responseBody.data, true,
                                responseBody.message, responseBody.status));
                    } else {
                        Log.d(TAG, "Received empty data list");
                        liveData.setValue(new ApiResponse<>(null, true,
                                "No employees found", responseBody.status));
                    }
                } else {
                    handleAllEmployeesErrorResponse(response, liveData);
                }
            }

            @Override
            public void onFailure(@NonNull Call<AllEmployeesResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "API call failed: " + t.getMessage(), t);
                String errorMessage = "Network error. Please check your connection.";

                if (t instanceof IOException) {
                    errorMessage = "Connection error. Please check your internet.";
                }

                liveData.setValue(new ApiResponse<>(null, false, errorMessage, -1));
            }
        });

        return liveData;
    }
    public LiveData<ApiResponse<EmployeeList>> getEmployeeList(String auth) {
        final MutableLiveData<ApiResponse<EmployeeList>> liveData = new MutableLiveData<>();

        if (auth == null || auth.isEmpty()) {
            liveData.setValue(new ApiResponse<>(null, false, "Authentication token is missing", 401));
            return liveData;
        }

        Log.d(TAG, "Making API call to get all employees");

        // Fix: Ensure the generic type used in enqueue and Call matches the expected response class
        Call<EmployeeList> call = apiRequest.getEmployeeList(auth);

        call.enqueue(new Callback<EmployeeList>() {
            @Override
            public void onResponse(@NonNull Call<EmployeeList> call, @NonNull Response<EmployeeList> response) {
                if (response.isSuccessful() && response.body() != null) {
                    EmployeeList responseBody = response.body();
                    Log.d(TAG, "API call successful. Status: " + responseBody.status);

                    if (responseBody.data != null) {
                        Log.d(TAG, "Received " + responseBody.data.size() + " employees");
                        liveData.setValue(new ApiResponse<>(responseBody, true, responseBody.message, responseBody.status));
                    } else {
                        Log.d(TAG, "Received empty data list");
                        liveData.setValue(new ApiResponse<>(null, true, "No employees found", responseBody.status));
                    }
                } else {
                    handleAllEmployeesListErrorResponse(response, liveData);
                }
            }

            @Override
            public void onFailure(@NonNull Call<EmployeeList> call, @NonNull Throwable t) {
                Log.e(TAG, "API call failed: " + t.getMessage(), t);
                String errorMessage = (t instanceof IOException) ?
                        "Connection error. Please check your internet." :
                        "Network error. Please try again.";
                liveData.setValue(new ApiResponse<>(null, false, errorMessage, -1));
            }
        });

        return liveData;
    }


    public LiveData<ApiResponse<ModelResponse>> deleteEmployee(String auth, int employeeId) {
        final MutableLiveData<ApiResponse<ModelResponse>> liveData = new MutableLiveData<>();

        apiRequest.deleteEmployee(auth, employeeId).enqueue(new Callback<ModelResponse>() {
            @Override
            public void onResponse(@NonNull Call<ModelResponse> call, @NonNull Response<ModelResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(new ApiResponse<>(response.body(), true, null, response.code()));
                } else {
                    String errorMessage = "Failed to delete employee";
                    try {
                        if (response.errorBody() != null) {
                            errorMessage = response.errorBody().string();
                        }
                    } catch (IOException e) {
                        errorMessage = "Error reading error response";
                        Log.e(TAG, "deleteEmployee: " + e.getMessage());
                    }
                    liveData.setValue(new ApiResponse<>(null, false, errorMessage, response.code()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ModelResponse> call, @NonNull Throwable t) {
                liveData.setValue(new ApiResponse<>(null, false, "Network failure. Try again.", -1));
            }
        });

        return liveData;
    }

    public LiveData<ApiResponse<EmployeeResponse>> postClass(String auth, CreateClass createClass) {
        final MutableLiveData<ApiResponse<EmployeeResponse>> liveData = new MutableLiveData<>();

        apiRequest.postClass(auth, createClass).enqueue(new Callback<EmployeeResponse>() {
            @Override
            public void onResponse(@NonNull Call<EmployeeResponse> call, @NonNull Response<EmployeeResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(new ApiResponse<>(response.body(), true, null, response.code()));
                } else {
                    String errorMessage = "Failed to create class";
                    try {
                        if (response.errorBody() != null) {
                            errorMessage = response.errorBody().string();
                        }
                    } catch (IOException e) {
                        errorMessage = "Error reading error response";
                        Log.e(TAG, "postClass: " + e.getMessage());
                    }
                    liveData.setValue(new ApiResponse<>(null, false, errorMessage, response.code()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<EmployeeResponse> call, @NonNull Throwable t) {
                liveData.setValue(new ApiResponse<>(null, false, "Network failure. Try again.", -1));
            }
        });

        return liveData;
    }

    public LiveData<ApiResponse<ClassModel>> getAllClasses(String auth) {
        MutableLiveData<ApiResponse<ClassModel>> liveData = new MutableLiveData<>();

        apiRequest.getAllClasses(auth).enqueue(new Callback<ClassModel>() {
            @Override
            public void onResponse(@NonNull Call<ClassModel> call, @NonNull Response<ClassModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(new ApiResponse<>(response.body(), true, null, response.code()));
                } else {
                    String error = "Failed to fetch classes";
                    try {
                        if (response.errorBody() != null) {
                            error = response.errorBody().string();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "getAllClasses: " + e.getMessage());
                    }
                    liveData.setValue(new ApiResponse<>(null, false, error, response.code()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ClassModel> call, @NonNull Throwable t) {
                liveData.setValue(new ApiResponse<>(null, false, "Network failure. Try again.", -1));
            }
        });

        return liveData;
    }

    public LiveData<ApiResponse<ClassListResponse>> getAllClassList(String auth) {
        MutableLiveData<ApiResponse<ClassListResponse>> liveData = new MutableLiveData<>();

        apiRequest.getAllClassList(auth).enqueue(new Callback<ClassListResponse>() {
            @Override
            public void onResponse(@NonNull Call<ClassListResponse> call,
                                   @NonNull Response<ClassListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(new ApiResponse<>(response.body(), true, null, response.code()));
                } else {
                    String error = "Failed to fetch classes";
                    try {
                        if (response.errorBody() != null) {
                            error = response.errorBody().string();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "getAllClasses: " + e.getMessage());
                    }
                    liveData.setValue(new ApiResponse<>(null, false, error, response.code()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ClassListResponse> call, @NonNull Throwable t) {
                liveData.setValue(new ApiResponse<>(null, false, "Network failure. Try again.", -1));
            }
        });

        return liveData;
    }

    public LiveData<ApiResponse<SubjectCreationResponse>> createSubjects(String auth, Integer classId, List<SubjectRequestBody> subjects) {
        MutableLiveData<ApiResponse<SubjectCreationResponse>> liveData = new MutableLiveData<>();

        apiRequest.createSubjects(auth, classId, subjects).enqueue(new Callback<SubjectCreationResponse>() {
            @Override
            public void onResponse(@NonNull Call<SubjectCreationResponse> call,
                                   @NonNull Response<SubjectCreationResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(new ApiResponse<>(response.body(), true, response.body().getMessage(), response.code()));
                } else {
                    String error = "Failed to create subjects";
                    try {
                        if (response.errorBody() != null) {
                            error = response.errorBody().string();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "createSubjects: " + e.getMessage());
                    }
                    liveData.setValue(new ApiResponse<>(null, false, error, response.code()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<SubjectCreationResponse> call, @NonNull Throwable t) {
                liveData.setValue(new ApiResponse<>(null, false, "Network failure. Try again.", -1));
            }
        });

        return liveData;
    }
    public LiveData<ApiResponse<SubjectCreationResponse>> updateSubjects(String auth, int classId, List<SubjectUpdateRequest> subjects) {
        MutableLiveData<ApiResponse<SubjectCreationResponse>> liveData = new MutableLiveData<>();

        apiRequest.updateSubjects(auth, classId, subjects).enqueue(new Callback<SubjectCreationResponse>() {
            @Override
            public void onResponse(@NonNull Call<SubjectCreationResponse> call, @NonNull Response<SubjectCreationResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(new ApiResponse<>(response.body(), true, response.body().getMessage(), response.code()));
                } else {
                    String error = "Failed to update subjects";
                    try {
                        if (response.errorBody() != null) {
                            error = response.errorBody().string();
                        }
                    } catch (IOException e) {
                        Log.e("UpdateSubjects", "Error: " + e.getMessage());
                    }
                    liveData.setValue(new ApiResponse<>(null, false, error, response.code()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<SubjectCreationResponse> call, @NonNull Throwable t) {
                liveData.setValue(new ApiResponse<>(null, false, "Network failure. Try again.", -1));
            }
        });

        return liveData;
    }


    public LiveData<ApiResponse<ClassesWithSubjectsResponse>> getClassesWithSubjects(String auth) {
        MutableLiveData<ApiResponse<ClassesWithSubjectsResponse>> liveData = new MutableLiveData<>();

        apiRequest.getClassesWithSubjects(auth).enqueue(new Callback<ClassesWithSubjectsResponse>() {
            @Override
            public void onResponse(@NonNull Call<ClassesWithSubjectsResponse> call,
                                   @NonNull Response<ClassesWithSubjectsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(new ApiResponse<>(response.body(), true, null, response.code()));
                } else {
                    String error = "Failed to fetch classes with subjects";
                    try {
                        if (response.errorBody() != null) {
                            error = response.errorBody().string();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "getClassesWithSubjects: " + e.getMessage());
                    }
                    liveData.setValue(new ApiResponse<>(null, false, error, response.code()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ClassesWithSubjectsResponse> call, @NonNull Throwable t) {
                liveData.setValue(new ApiResponse<>(null, false, "Network failure. Try again.", -1));
            }
        });

        return liveData;
    }


    public LiveData<ApiResponse<EmployeeResponse>> deleteClass(String auth,int classId) {
        MutableLiveData<ApiResponse<EmployeeResponse>> liveData = new MutableLiveData<>();

        apiRequest.deleteClass(auth,classId).enqueue(new Callback<EmployeeResponse>() {
            @Override
            public void onResponse(@NonNull Call<EmployeeResponse> call, @NonNull Response<EmployeeResponse> response) {
                if (response.isSuccessful()) {
                    liveData.setValue(new ApiResponse<>(null, true, null, response.code()));
                } else {
                    String error = "Failed to delete class";
                    try {
                        if (response.errorBody() != null) {
                            error = response.errorBody().string();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "deleteClass: " + e.getMessage());
                    }
                    liveData.setValue(new ApiResponse<>(null, false, error, response.code()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<EmployeeResponse> call, @NonNull Throwable t) {
                liveData.setValue(new ApiResponse<>(null, false, "Network failure. Try again.", -1));
            }
        });

        return liveData;
    }

    public LiveData<EmployeeResponse> updateClass(String auth, int classId, CreateClass createClass) {
        MutableLiveData<EmployeeResponse> liveData = new MutableLiveData<>();

        apiRequest.updateClass(auth, classId, createClass).enqueue(new Callback<EmployeeResponse>() {
            @Override
            public void onResponse(Call<EmployeeResponse> call, Response<EmployeeResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.postValue(response.body());
                } else {
                    EmployeeResponse errorResponse = new EmployeeResponse();
                    errorResponse.setSuccess(false);
                    errorResponse.setMessage("Update failed: " + response.message());
                    liveData.postValue(errorResponse);
                }
            }

            @Override
            public void onFailure(Call<EmployeeResponse> call, Throwable t) {
                EmployeeResponse errorResponse = new EmployeeResponse();
                errorResponse.setSuccess(false);
                errorResponse.setMessage(t.getMessage());
                liveData.postValue(errorResponse);
            }
        });

        return liveData;
    }




    private void handleAllEmployeesErrorResponse(Response<?> response, MutableLiveData<ApiResponse<List<AllEmployees>>> liveData) {
        String errorMessage;

        try {
            if (response.errorBody() != null) {
                errorMessage = response.errorBody().string();
            } else {
                errorMessage = "Unknown error occurred";
            }
        } catch (IOException e) {
            errorMessage = "Error processing response";
            Log.e(TAG, "Error reading error body", e);
        }

        Log.e(TAG, "API error: " + response.code() + " - " + errorMessage);

        switch (response.code()) {
            case 401:
                errorMessage = "Authentication failed. Please login again.";
                break;
            case 403:
                errorMessage = "You don't have permission to access this resource.";
                break;
            case 404:
                errorMessage = "Resource not found.";
                break;
            case 500:
                errorMessage = "Server error. Please try again later.";
                break;
        }

        liveData.setValue(new ApiResponse<>(null, false, errorMessage, response.code()));
    }
    private void handleAllEmployeesListErrorResponse(Response<?> response, MutableLiveData<ApiResponse<EmployeeList>> liveData) {
        String errorMessage;

        try {
            if (response.errorBody() != null) {
                errorMessage = response.errorBody().string();
            } else {
                errorMessage = "Unknown error occurred";
            }
        } catch (IOException e) {
            errorMessage = "Error processing response";
            Log.e(TAG, "Error reading error body", e);
        }

        Log.e(TAG, "API error: " + response.code() + " - " + errorMessage);

        switch (response.code()) {
            case 401:
                errorMessage = "Authentication failed. Please login again.";
                break;
            case 403:
                errorMessage = "You don't have permission to access this resource.";
                break;
            case 404:
                errorMessage = "Resource not found.";
                break;
            case 500:
                errorMessage = "Server error. Please try again later.";
                break;
        }

        liveData.setValue(new ApiResponse<>(null, false, errorMessage, response.code()));
    }

    /**
     * Extracts an error message and updates LiveData accordingly.
     */
    private void handleErrorResponse(Response<?> response, MutableLiveData<ApiResponse<EmployeeResponse>> liveData) {
        try {
            if (response.code() == ERROR_SESSION_EXPIRED) {
                // Session expired, handle appropriately
                synchronized (SESSION_LOCK) {
                    if (!isHandlingSessionExpiry) {
                        isHandlingSessionExpiry = true;
                        liveData.setValue(new ApiResponse<>(null, false, "Your Login has expired, please login again.", -1));
                    } else {
                        // Don't notify again if already handling
                        Log.d(TAG, "Already handling session expiry, suppressing duplicate notification");
                    }
                }
            } else if (response.errorBody() != null) {
                String errorBody = response.errorBody().string();
                String errorMessage = extractDynamicErrorMessage(errorBody);
                liveData.setValue(new ApiResponse<>(null, false, errorMessage, -1));
            } else {
                liveData.setValue(new ApiResponse<>(null, false, "An unknown error occurred.", -1));
            }
        } catch (Exception e) {
            Log.e(TAG, "Error parsing error response: " + e.getMessage());
            liveData.setValue(new ApiResponse<>(null, false, "An unknown error occurred.", -1));
        }
    }

    private void handleErrorResponseLogin(Response<?> response, MutableLiveData<ApiResponse<LoginResponse>> liveData) {
        try {
            if (response.code() == ERROR_SESSION_EXPIRED) {
                // Session expired, handle appropriately
                synchronized (SESSION_LOCK) {
                    if (!isHandlingSessionExpiry) {
                        isHandlingSessionExpiry = true;
                        liveData.setValue(new ApiResponse<>(null, false, "Your Login has expired, please login again.", -1));
                    } else {
                        // Don't notify again if already handling
                        Log.d(TAG, "Already handling session expiry, suppressing duplicate notification");
                    }
                }
            } else if (response.errorBody() != null) {
                String errorBody = response.errorBody().string();
                String errorMessage = extractDynamicErrorMessage(errorBody);
                liveData.setValue(new ApiResponse<>(null, false, errorMessage, -1));
            } else {
                liveData.setValue(new ApiResponse<>(null, false, "An unknown error occurred.", -1));
            }
        } catch (Exception e) {
            Log.e(TAG, "Error parsing error response: " + e.getMessage());
            liveData.setValue(new ApiResponse<>(null, false, "An unknown error occurred.", -1));
        }
    }

    /**
     * Extracts an error message and updates LiveData accordingly for Employee responses.
     */
    private void handleEmployeeErrorResponse(Response<?> response, MutableLiveData<ApiResponse<Employee>> liveData) {
        try {
            if (response.code() == ERROR_SESSION_EXPIRED) {
                // Session expired, handle appropriately
                synchronized (SESSION_LOCK) {
                    if (!isHandlingSessionExpiry) {
                        isHandlingSessionExpiry = true;
                        liveData.setValue(new ApiResponse<>(null, false, "Your Login has expired, please login again.", -1));
                    } else {
                        // Don't notify again if already handling
                        Log.d(TAG, "Already handling session expiry, suppressing duplicate notification");
                    }
                }
            } else if (response.errorBody() != null) {
                String errorBody = response.errorBody().string();
                String errorMessage = extractDynamicErrorMessage(errorBody);
                liveData.setValue(new ApiResponse<>(null, false, errorMessage, -1));
            } else {
                liveData.setValue(new ApiResponse<>(null, false, "An unknown error occurred.", -1));
            }
        } catch (Exception e) {
            Log.e(TAG, "Error parsing error response: " + e.getMessage());
            liveData.setValue(new ApiResponse<>(null, false, "An unknown error occurred.", -1));
        }
    }

    private void handleEmployeeErrorResponse2(Response<?> response, MutableLiveData<ApiResponse<Employee2>> liveData) {
        try {
            if (response.code() == ERROR_SESSION_EXPIRED) {
                // Session expired, handle appropriately
                synchronized (SESSION_LOCK) {
                    if (!isHandlingSessionExpiry) {
                        isHandlingSessionExpiry = true;
                        liveData.setValue(new ApiResponse<>(null, false, "Your Login has expired, please login again.", -1));
                    } else {
                        // Don't notify again if already handling
                        Log.d(TAG, "Already handling session expiry, suppressing duplicate notification");
                    }
                }
            } else if (response.errorBody() != null) {
                String errorBody = response.errorBody().string();
                String errorMessage = extractDynamicErrorMessage(errorBody);
                liveData.setValue(new ApiResponse<>(null, false, errorMessage, -1));
            } else {
                liveData.setValue(new ApiResponse<>(null, false, "An unknown error occurred.", -1));
            }
        } catch (Exception e) {
            Log.e(TAG, "Error parsing error response: " + e.getMessage());
            liveData.setValue(new ApiResponse<>(null, false, "An unknown error occurred.", -1));
        }
    }

    /**
     * Extracts a dynamic error message from JSON or HTML.
     */
    private String extractDynamicErrorMessage(String errorBody) {
        try {
            if (errorBody.trim().startsWith("{")) { // Check if JSON
                JSONObject jsonObject = new JSONObject(errorBody);
                return jsonObject.optString("message", "An error occurred.");
            }
            Document document = Jsoup.parse(errorBody);
            return document.body() != null ? document.body().text().trim() : "An unknown error occurred.";
        } catch (Exception e) {
            Log.e(TAG, "Error while parsing the error message: " + e.getMessage());
            return "An unknown error occurred.";
        }
    }

    private <T> void handleSessionExpiry(MutableLiveData<ApiResponse<T>> liveData) {
        synchronized (SESSION_LOCK) {
            if (!isHandlingSessionExpiry) {
                isHandlingSessionExpiry = true;
                liveData.setValue(new ApiResponse<>(null, false, "Your Login has expired, please login again.", ERROR_SESSION_EXPIRED));
            } else {
                // Don't notify again if already handling
                Log.d(TAG, "Already handling session expiry, suppressing duplicate notification");
            }
        }
    }

    private <T> void handleNetworkFailure(Call<?> call, Throwable t, MutableLiveData<ApiResponse<T>> liveData) {
        Log.e(TAG, "API call failed: " + t.getMessage(), t);
        String errorMessage = call.isCanceled() ?
                "Request was canceled" :
                "Failed to connect. Please check your network.";
        liveData.setValue(new ApiResponse<>(null, false, errorMessage, -1));
    }

    /**
     * Wrapper class to handle API responses.
     */
    public static class ApiResponse<T> {
        public final T data;
        public final boolean isSuccess;
        public final String message;
        public final int code;

        public ApiResponse(T data, boolean isSuccess, String message, int code) {
            this.data = data;
            this.isSuccess = isSuccess;
            this.message = message;
            this.code = code;
        }

        // Additional constructor for the updateEmployee response
        public ApiResponse(boolean isSuccess, T data, String message) {
            this.data = data;
            this.isSuccess = isSuccess;
            this.message = message;
            this.code = -1;
        }
    }
}