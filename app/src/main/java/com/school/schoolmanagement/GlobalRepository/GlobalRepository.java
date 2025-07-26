package com.school.schoolmanagement.GlobalRepository;

import android.util.Base64;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.school.schoolmanagement.Admin.Model.AccountGet;
import com.school.schoolmanagement.Admin.Model.AccountReport;
import com.school.schoolmanagement.Admin.Model.AccountSettings;
import com.school.schoolmanagement.Admin.Model.AccountSettingsResponse;
import com.school.schoolmanagement.Admin.Model.AddIncome;
import com.school.schoolmanagement.Admin.Model.AddQuestionBank;
import com.school.schoolmanagement.Admin.Model.AddStudentMark;
import com.school.schoolmanagement.Admin.Model.AdminDashBoardClassGraph;
import com.school.schoolmanagement.Admin.Model.AdminDashboardAbsentStudents;
import com.school.schoolmanagement.Admin.Model.AdminDashboardAccountsOverview;
import com.school.schoolmanagement.Admin.Model.AdminDashboardClassInfo;
import com.school.schoolmanagement.Admin.Model.AdminDashboardEmployeeInfo;
import com.school.schoolmanagement.Admin.Model.AdminDashboardExpenseInfo;
import com.school.schoolmanagement.Admin.Model.AdminDashboardFeesInfo;
import com.school.schoolmanagement.Admin.Model.AdminDashboardNewAdmission;
import com.school.schoolmanagement.Admin.Model.AdminDashboardPresentEmployee;
import com.school.schoolmanagement.Admin.Model.AdminDashboardPresentPercent;
import com.school.schoolmanagement.Admin.Model.AdminDashboardSchoolInfo;
import com.school.schoolmanagement.Admin.Model.AdminDashboardStudentInfo;
import com.school.schoolmanagement.Admin.Model.AdminDashboardSubjectInfo;
import com.school.schoolmanagement.Admin.Model.AllEmployees;
import com.school.schoolmanagement.Admin.Model.AllEmployeesResponse;
import com.school.schoolmanagement.Admin.Model.ClassListResponse;
import com.school.schoolmanagement.Admin.Model.ClassModel;
import com.school.schoolmanagement.Admin.Model.ClassModel2;
import com.school.schoolmanagement.Admin.Model.ClassWiseAttendanceReport;
import com.school.schoolmanagement.Admin.Model.ClassesWithSubjectsResponse;
import com.school.schoolmanagement.Admin.Model.CreateChart;
import com.school.schoolmanagement.Admin.Model.CreateClass;
import com.school.schoolmanagement.Admin.Model.CreateExam;
import com.school.schoolmanagement.Admin.Model.CreateHomework;
import com.school.schoolmanagement.Admin.Model.CreateQuestionPaper;
import com.school.schoolmanagement.Admin.Model.DashboardApiEstimatedFees;
import com.school.schoolmanagement.Admin.Model.Employee2;
import com.school.schoolmanagement.Admin.Model.EmployeeList;
import com.school.schoolmanagement.Admin.Model.ExamModel;
import com.school.schoolmanagement.Admin.Model.FeesRecordResponse;
import com.school.schoolmanagement.Admin.Model.FeesReportResponse;
import com.school.schoolmanagement.Admin.Model.FeesStructure;
import com.school.schoolmanagement.Admin.Model.GetStudentTest;
import com.school.schoolmanagement.Admin.Model.QuestionBankModel;
import com.school.schoolmanagement.Admin.Model.QuestionPaperResponse;
import com.school.schoolmanagement.Admin.Model.StudentId;
import com.school.schoolmanagement.Admin.Model.StudentInfoReport;
import com.school.schoolmanagement.Admin.Model.StudentMarksRequest;
import com.school.schoolmanagement.Admin.Model.SubjectCreationResponse;
import com.school.schoolmanagement.Admin.Model.SubjectRequestBody;
import com.school.schoolmanagement.Admin.Model.SubjectUpdateRequest;
import com.school.schoolmanagement.Admin.Model.SubmitFees;
import com.school.schoolmanagement.GlobalResponse.LoginResponse;
import com.school.schoolmanagement.Model.AccountStatement;
import com.school.schoolmanagement.Model.AddChapter;
import com.school.schoolmanagement.Model.AllStudentResponse;
import com.school.schoolmanagement.Model.AttendanceReport;
import com.school.schoolmanagement.Model.BankAccountFields;
import com.school.schoolmanagement.Model.CertificateResponse;
import com.school.schoolmanagement.Model.ChapterResponse;
import com.school.schoolmanagement.Model.ClassTestResult;
import com.school.schoolmanagement.Model.CreateTest;
import com.school.schoolmanagement.Model.Employee;
import com.school.schoolmanagement.Model.EmployeeAttendanceReport;
import com.school.schoolmanagement.Model.EmployeeDashboardResponse;
import com.school.schoolmanagement.Model.EmployeeProfile;
import com.school.schoolmanagement.Model.EmployeeRequestDto;
import com.school.schoolmanagement.Model.EmployeeResponse;
import com.school.schoolmanagement.Model.FeesCollectionReport;
import com.school.schoolmanagement.Model.FeesDefaulterModel;
import com.school.schoolmanagement.Model.GetAttendanceResponse;
import com.school.schoolmanagement.Model.GetEmployeeAttendance;
import com.school.schoolmanagement.Model.HomeworkDetails;
import com.school.schoolmanagement.Model.Login;
import com.school.schoolmanagement.Model.ManageLoginEmployee;
import com.school.schoolmanagement.Model.ModelResponse;
import com.school.schoolmanagement.Model.PostEmployeeAttendance;
import com.school.schoolmanagement.Model.PostStudentAttendance;
import com.school.schoolmanagement.Model.PromotionBody;
import com.school.schoolmanagement.Model.ResultPdfResponse;
import com.school.schoolmanagement.Model.SalaryPaidResponse;
import com.school.schoolmanagement.Model.StudentExamListResponse;
import com.school.schoolmanagement.Model.StudentFields;
import com.school.schoolmanagement.Model.StudentsAdmissionConfirmationResponse;
import com.school.schoolmanagement.Model.StudentsAttendanceReport;
import com.school.schoolmanagement.Students.Model.LastSubmittedFeesResponse;
import com.school.schoolmanagement.Students.Model.StudentDashboardApiResponse1;
import com.school.schoolmanagement.Students.Model.StudentDashboardApiResponse2;
import com.school.schoolmanagement.Students.Model.StudentDashboardApiResponse3;
import com.school.schoolmanagement.Students.Model.StudentHomeworkResponse;
import com.school.schoolmanagement.Students.Model.StudentsExamResultResponse;
import com.school.schoolmanagement.Students.Model.StudentsProfileResponse;
import com.school.schoolmanagement.Students.Model.StudentsTestResultResponse;
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
import retrofit2.http.Body;

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
    public LiveData<ApiResponse<EmployeeResponse>> createStudent(String auth, StudentFields student) {
        final MutableLiveData<ApiResponse<EmployeeResponse>> liveData = new MutableLiveData<>();

        // Create multipart form data
        MultipartBody.Part imagePart = null;

        // Check if profile image data exists
        if (student.getProfileImageData() != null) {
            RequestBody requestFile = RequestBody.create(
                    MediaType.parse("image/jpeg"),
                    student.getProfileImageData()
            );

            imagePart = MultipartBody.Part.createFormData(
                    "profilePicture", "profile_image.jpg", requestFile
            );
        }

        // Convert the employee object to JSON
        Gson gson = new Gson();
        String studentJson = gson.toJson(student);

        // Wrap the JSON into a RequestBody
        RequestBody studentBody = RequestBody.create(
                MediaType.parse("application/json"), studentJson
        );

        // Make the API call
        apiRequest.createStudent(auth, studentBody, imagePart)
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
    public LiveData<ApiResponse<EmployeeResponse>> updateStudents(String auth,int StudentsId, StudentFields student) {
        final MutableLiveData<ApiResponse<EmployeeResponse>> liveData = new MutableLiveData<>();

        // Create multipart form data
        MultipartBody.Part imagePart = null;

        // Check if profile image data exists
        if (student.getProfileImageData() != null) {
            RequestBody requestFile = RequestBody.create(
                    MediaType.parse("image/jpeg"),
                    student.getProfileImageData()
            );

            imagePart = MultipartBody.Part.createFormData(
                    "profilePicture", "profile_image.jpg", requestFile
            );
        }

        // Convert the employee object to JSON
        Gson gson = new Gson();
        String studentJson = gson.toJson(student);

        // Wrap the JSON into a RequestBody
        RequestBody studentBody = RequestBody.create(
                MediaType.parse("application/json"), studentJson
        );

        // Make the API call
        apiRequest.updateStudent(auth,StudentsId, studentBody, imagePart)
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
    public LiveData<ApiResponse<EmployeeResponse>> promoteStudents(String auth,int classId, List<Long> studentIds) {
        MutableLiveData<ApiResponse<EmployeeResponse>> liveData = new MutableLiveData<>();

        apiRequest.promoteStudents(auth,classId, studentIds).enqueue(new Callback<EmployeeResponse>() {
            @Override
            public void onResponse(@NonNull Call<EmployeeResponse> call, @NonNull Response<EmployeeResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(new ApiResponse<>(response.body(), true, null, response.code()));
                } else {
                    String errorMessage = "Failed to promote students";
                    try {
                        if (response.errorBody() != null) {
                            errorMessage = response.errorBody().string();
                        }
                    } catch (IOException e) {
                        errorMessage = "Error reading error response";
                        Log.e("API_ERROR", "promoteStudents: " + e.getMessage());
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
    public LiveData<ApiResponse<EmployeeResponse>> updateExam(String auth,int examId,CreateExam createExam) {
        MutableLiveData<ApiResponse<EmployeeResponse>> liveData = new MutableLiveData<>();

        apiRequest.updateExam(auth,examId, createExam).enqueue(new Callback<EmployeeResponse>() {
            @Override
            public void onResponse(@NonNull Call<EmployeeResponse> call, @NonNull Response<EmployeeResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(new ApiResponse<>(response.body(), true, null, response.code()));
                } else {
                    String errorMessage = "Failed to promote students";
                    try {
                        if (response.errorBody() != null) {
                            errorMessage = response.errorBody().string();
                        }
                    } catch (IOException e) {
                        errorMessage = "Error reading error response";
                        Log.e("API_ERROR", "promoteStudents: " + e.getMessage());
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
    public LiveData<ApiResponse<ManageLoginEmployee>> getEmployee2(String auth) {
        final MutableLiveData<ApiResponse<ManageLoginEmployee>> liveData = new MutableLiveData<>();

        if (auth == null || auth.isEmpty()) {
            liveData.setValue(new ApiResponse<>(null, false, "Authentication token is missing", 401));
            return liveData;
        }

        Log.d(TAG, "Making API call to get all employees");

        // Fix: Ensure the generic type used in enqueue and Call matches the expected response class
        Call<ManageLoginEmployee> call = apiRequest.getEmployee2(auth);

        call.enqueue(new Callback<ManageLoginEmployee>() {
            @Override
            public void onResponse(@NonNull Call<ManageLoginEmployee> call, @NonNull Response<ManageLoginEmployee> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ManageLoginEmployee responseBody = response.body();
                    Log.d(TAG, "API call successful. Status: " + responseBody.status);

                    if (responseBody.data != null) {
                        Log.d(TAG, "Received " + responseBody.data.size() + " employees");
                        liveData.setValue(new ApiResponse<>(responseBody, true, responseBody.message, responseBody.status));
                    } else {
                        Log.d(TAG, "Received empty data list");
                        liveData.setValue(new ApiResponse<>(null, true, "No employees found", responseBody.status));
                    }
                } else {
                    handleAllEmployeesListErrorResponse2(response, liveData);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ManageLoginEmployee> call, @NonNull Throwable t) {
                Log.e(TAG, "API call failed: " + t.getMessage(), t);
                String errorMessage = (t instanceof IOException) ?
                        "Connection error. Please check your internet." :
                        "Network error. Please try again.";
                liveData.setValue(new ApiResponse<>(null, false, errorMessage, -1));
            }
        });

        return liveData;
    }
    public LiveData<ApiResponse<HomeworkDetails>> getAllHomework(String auth) {
        final MutableLiveData<ApiResponse<HomeworkDetails>> liveData = new MutableLiveData<>();

        if (auth == null || auth.isEmpty()) {
            liveData.setValue(new ApiResponse<>(null, false, "Authentication token is missing", 401));
            return liveData;
        }

        Log.d(TAG, "Making API call to get all homework");

        Call<HomeworkDetails> call = apiRequest.getAllHomework(auth);

        call.enqueue(new Callback<HomeworkDetails>() {
            @Override
            public void onResponse(@NonNull Call<HomeworkDetails> call, @NonNull Response<HomeworkDetails> response) {
                if (response.isSuccessful() && response.body() != null) {
                    HomeworkDetails responseBody = response.body();
                    Log.d(TAG, "API call successful. Status: " + responseBody.status);

                    if (responseBody.data != null) {
                        Log.d(TAG, "Received homework data");
                        liveData.setValue(new ApiResponse<>(responseBody, true, responseBody.message, responseBody.status));
                    } else {
                        Log.d(TAG, "Received empty homework data");
                        liveData.setValue(new ApiResponse<>(null, true, "No homework found", responseBody.status));
                    }
                } else {
                    handleHomeworkErrorResponse(response, liveData);
                }
            }

            @Override
            public void onFailure(@NonNull Call<HomeworkDetails> call, @NonNull Throwable t) {
                Log.e(TAG, "API call failed: " + t.getMessage(), t);
                String errorMessage = (t instanceof IOException) ?
                        "Connection error. Please check your internet." :
                        "Network error. Please try again.";
                liveData.setValue(new ApiResponse<>(null, false, errorMessage, -1));
            }
        });

        return liveData;
    }

    public LiveData<ApiResponse<HomeworkDetails>> getFilteredHomework(String auth, Integer classId, String teacherId, String homeworkDate) {
        final MutableLiveData<ApiResponse<HomeworkDetails>> liveData = new MutableLiveData<>();

        if (auth == null || auth.isEmpty()) {
            liveData.setValue(new ApiResponse<>(null, false, "Authentication token is missing", 401));
            return liveData;
        }

        Log.d(TAG, "Making API call to get filtered homework");
        Log.d(TAG, "Filters - Class ID: " + classId + ", Teacher ID: " + teacherId +
                ", Date: " + homeworkDate + ", Search: ");

        Call<HomeworkDetails> call = apiRequest.getFilteredHomework(auth, classId, teacherId, homeworkDate);

        call.enqueue(new Callback<HomeworkDetails>() {
            @Override
            public void onResponse(@NonNull Call<HomeworkDetails> call, @NonNull Response<HomeworkDetails> response) {
                if (response.isSuccessful() && response.body() != null) {
                    HomeworkDetails responseBody = response.body();
                    Log.d(TAG, "Filtered API call successful. Status: " + responseBody.status);

                    if (responseBody.data != null) {
                        Log.d(TAG, "Received filtered homework data: " + responseBody.data.size() + " items");
                        liveData.setValue(new ApiResponse<>(responseBody, true, responseBody.message, responseBody.status));
                    } else {
                        Log.d(TAG, "No homework found with current filters");
                        liveData.setValue(new ApiResponse<>(responseBody, true, "No homework found with current filters", responseBody.status));
                    }
                } else {
                    handleHomeworkErrorResponse(response, liveData);
                }
            }

            @Override
            public void onFailure(@NonNull Call<HomeworkDetails> call, @NonNull Throwable t) {
                Log.e(TAG, "Filtered API call failed: " + t.getMessage(), t);
                String errorMessage = (t instanceof IOException) ?
                        "Connection error. Please check your internet." :
                        "Network error. Please try again.";
                liveData.setValue(new ApiResponse<>(null, false, errorMessage, -1));
            }
        });

        return liveData;
    }

    private void handleHomeworkErrorResponse(Response<HomeworkDetails> response, MutableLiveData<ApiResponse<HomeworkDetails>> liveData) {
        String errorMessage;
        int statusCode = response.code();

        switch (statusCode) {
            case 401:
                errorMessage = "Unauthorized. Please login again.";
                break;
            case 403:
                errorMessage = "Access forbidden.";
                break;
            case 404:
                errorMessage = "Homework not found.";
                break;
            case 500:
                errorMessage = "Server error. Please try again later.";
                break;
            default:
                errorMessage = "Failed to fetch homework. Error code: " + statusCode;
        }

        Log.e(TAG, "API Error: " + statusCode + " - " + errorMessage);
        liveData.setValue(new ApiResponse<>(null, false, errorMessage, statusCode));
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
    public LiveData<ApiResponse<ModelResponse>> deleteExam(String auth, int examId) {
        final MutableLiveData<ApiResponse<ModelResponse>> liveData = new MutableLiveData<>();

        apiRequest.deleteExam(auth, examId).enqueue(new Callback<ModelResponse>() {
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
    public LiveData<ApiResponse<ModelResponse>> deleteAccountChart(String auth, int id) {
        final MutableLiveData<ApiResponse<ModelResponse>> liveData = new MutableLiveData<>();

        apiRequest.deleteAccountChart(auth, id).enqueue(new Callback<ModelResponse>() {
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
    public LiveData<ApiResponse<EmployeeResponse>> addChapter(String auth, AddChapter addChapter) {
        final MutableLiveData<ApiResponse<EmployeeResponse>> liveData = new MutableLiveData<>();

        apiRequest.addChapter(auth, addChapter).enqueue(new Callback<EmployeeResponse>() {
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
    public LiveData<ApiResponse<EmployeeResponse>> updateChapter(String auth,int chapterId, AddChapter addChapter) {
        final MutableLiveData<ApiResponse<EmployeeResponse>> liveData = new MutableLiveData<>();

        apiRequest.updateChapter(auth,chapterId, addChapter).enqueue(new Callback<EmployeeResponse>() {
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
    public LiveData<ApiResponse<EmployeeResponse>> deleteChapter(String auth,int chapterId) {
        final MutableLiveData<ApiResponse<EmployeeResponse>> liveData = new MutableLiveData<>();

        apiRequest.deleteChapter(auth,chapterId).enqueue(new Callback<EmployeeResponse>() {
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
    public LiveData<ApiResponse<ChapterResponse>> getChapters(String auth, int subjectId) {
        MutableLiveData<ApiResponse<ChapterResponse>> liveData = new MutableLiveData<>();

        apiRequest.getChapters(auth,subjectId).enqueue(new Callback<ChapterResponse>() {
            @Override
            public void onResponse(@NonNull Call<ChapterResponse> call, @NonNull Response<ChapterResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(new ApiResponse<>(response.body(), true, null, response.code()));
                } else {
                    String error = "Failed to fetch Students";
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
            public void onFailure(@NonNull Call<ChapterResponse> call, @NonNull Throwable t) {
                liveData.setValue(new ApiResponse<>(null, false, "Network failure. Try again.", -1));
            }
        });

        return liveData;
    }



    public LiveData<ApiResponse<EmployeeResponse>> addQuestionBank(String auth, AddQuestionBank addQuestionBank) {
        final MutableLiveData<ApiResponse<EmployeeResponse>> liveData = new MutableLiveData<>();

        apiRequest.addQuestionBank(auth, addQuestionBank).enqueue(new Callback<EmployeeResponse>() {
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
    public LiveData<ApiResponse<EmployeeResponse>> addQuestionPaper(String auth, CreateQuestionPaper createQuestionPaper) {
        final MutableLiveData<ApiResponse<EmployeeResponse>> liveData = new MutableLiveData<>();

        apiRequest.addQuestionPaper(auth, createQuestionPaper).enqueue(new Callback<EmployeeResponse>() {
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
    public LiveData<ApiResponse<QuestionPaperResponse>> getQuestionPaper(String auth) {
        MutableLiveData<ApiResponse<QuestionPaperResponse>> liveData = new MutableLiveData<>();

        apiRequest.getQuestionPaper(auth).enqueue(new Callback<QuestionPaperResponse>() {
            @Override
            public void onResponse(@NonNull Call<QuestionPaperResponse> call, @NonNull Response<QuestionPaperResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(new ApiResponse<>(response.body(), true, null, response.code()));
                } else {
                    String error = "Failed to fetch Students";
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
            public void onFailure(@NonNull Call<QuestionPaperResponse> call, @NonNull Throwable t) {
                liveData.setValue(new ApiResponse<>(null, false, "Network failure. Try again.", -1));
            }
        });

        return liveData;
    }
    public LiveData<ApiResponse<StudentInfoReport>> getStudentReport(String auth) {
        MutableLiveData<ApiResponse<StudentInfoReport>> liveData = new MutableLiveData<>();

        apiRequest.getStudentReport(auth).enqueue(new Callback<StudentInfoReport>() {
            @Override
            public void onResponse(@NonNull Call<StudentInfoReport> call, @NonNull Response<StudentInfoReport> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(new ApiResponse<>(response.body(), true, null, response.code()));
                } else {
                    String error = "Failed to fetch Students";
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
            public void onFailure(@NonNull Call<StudentInfoReport> call, @NonNull Throwable t) {
                liveData.setValue(new ApiResponse<>(null, false, "Network failure. Try again.", -1));
            }
        });

        return liveData;
    }
    public LiveData<ApiResponse<EmployeeResponse>> generatePdfResult(String auth, StudentId studentId) {
        MutableLiveData<ApiResponse<EmployeeResponse>> liveData = new MutableLiveData<>();

        apiRequest.generatePdfResult(auth,studentId).enqueue(new Callback<EmployeeResponse>() {
            @Override
            public void onResponse(@NonNull Call<EmployeeResponse> call, @NonNull Response<EmployeeResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(new ApiResponse<>(response.body(), true, null, response.code()));
                } else {
                    String error = "Failed to fetch Students";
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
            public void onFailure(@NonNull Call<EmployeeResponse> call, @NonNull Throwable t) {
                liveData.setValue(new ApiResponse<>(null, false, "Network failure. Try again.", -1));
            }
        });

        return liveData;
    }
    public LiveData<ApiResponse<EmployeeResponse>> addBankAccount(String auth, BankAccountFields accountFields) {
        MutableLiveData<ApiResponse<EmployeeResponse>> liveData = new MutableLiveData<>();

        // Create RequestBody objects directly inline
        RequestBody namePart = RequestBody.create(MediaType.parse("text/plain"),
                accountFields.getName() != null ? accountFields.getName() : "");
        RequestBody addressPart = RequestBody.create(MediaType.parse("text/plain"),
                accountFields.getAddress() != null ? accountFields.getAddress() : "");
        RequestBody accNumberPart = RequestBody.create(MediaType.parse("text/plain"),
                accountFields.getAccNumber() != null ? accountFields.getAccNumber() : "");
        RequestBody instructionPart = RequestBody.create(MediaType.parse("text/plain"),
                accountFields.getInstruction() != null ? accountFields.getInstruction() : "");

        // Image Part
        MultipartBody.Part imagePart = null;
        if (accountFields.getImageBase64() != null && !accountFields.getImageBase64().isEmpty()) {
            byte[] imageBytes = Base64.decode(accountFields.getImageBase64(), Base64.DEFAULT);
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), imageBytes);
            imagePart = MultipartBody.Part.createFormData("image", "upload.jpg", requestFile);
        }

        // API Call
        apiRequest.addBankAccount(auth, namePart, addressPart, accNumberPart, instructionPart, imagePart)
                .enqueue(new Callback<EmployeeResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<EmployeeResponse> call, @NonNull Response<EmployeeResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            liveData.setValue(new ApiResponse<>(response.body(), true, null, -1));
                        } else {
                            liveData.setValue(new ApiResponse<>(null, false, "Error: " + response.code(), response.code()));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<EmployeeResponse> call, @NonNull Throwable t) {
                        liveData.setValue(new ApiResponse<>(null, false, t.getMessage(), -1));
                    }
                });

        return liveData;
    }
    public LiveData<ApiResponse<EmployeeResponse>> updateAccountSettings(String auth, int id, AccountSettings accountSettings) {
        MutableLiveData<ApiResponse<EmployeeResponse>> liveData = new MutableLiveData<>();

        apiRequest.updateAccountSettings(auth, id, accountSettings).enqueue(new Callback<EmployeeResponse>() {
            @Override
            public void onResponse(@NonNull Call<EmployeeResponse> call, @NonNull Response<EmployeeResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(new ApiResponse<>(response.body(), true, null, response.code()));
                } else {
                    String error = "Failed to update account settings";
                    try {
                        if (response.errorBody() != null) {
                            error = response.errorBody().string();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "updateAccountSettings: " + e.getMessage());
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

    public LiveData<ApiResponse<EmployeeResponse>> deleteAccountSettings(String auth, int id) {
        MutableLiveData<ApiResponse<EmployeeResponse>> liveData = new MutableLiveData<>();

        apiRequest.deleteAccountSettings(auth, id).enqueue(new Callback<EmployeeResponse>() {
            @Override
            public void onResponse(@NonNull Call<EmployeeResponse> call, @NonNull Response<EmployeeResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(new ApiResponse<>(response.body(), true, null, response.code()));
                } else {
                    String error = "Failed to delete account settings";
                    try {
                        if (response.errorBody() != null) {
                            error = response.errorBody().string();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "deleteAccountSettings: " + e.getMessage());
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
    public LiveData<ApiResponse<AccountSettingsResponse>> getAccountSettings(String auth) {
        MutableLiveData<ApiResponse<AccountSettingsResponse>> liveData = new MutableLiveData<>();

        apiRequest.getAccountSettings(auth).enqueue(new Callback<AccountSettingsResponse>() {
            @Override
            public void onResponse(@NonNull Call<AccountSettingsResponse> call, @NonNull Response<AccountSettingsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(new ApiResponse<>(response.body(), true, null, response.code()));
                } else {
                    String error = "Failed to delete account settings";
                    try {
                        if (response.errorBody() != null) {
                            error = response.errorBody().string();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "deleteAccountSettings: " + e.getMessage());
                    }
                    liveData.setValue(new ApiResponse<>(null, false, error, response.code()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<AccountSettingsResponse> call, @NonNull Throwable t) {
                liveData.setValue(new ApiResponse<>(null, false, "Network failure. Try again.", -1));
            }
        });

        return liveData;
    }
    public LiveData<ApiResponse<EmployeeResponse>> postAccountSettings(String auth, AccountSettings accountSettings) {
        MutableLiveData<ApiResponse<EmployeeResponse>> liveData = new MutableLiveData<>();

        apiRequest.postAccountSettings(auth,accountSettings).enqueue(new Callback<EmployeeResponse>() {
            @Override
            public void onResponse(@NonNull Call<EmployeeResponse> call, @NonNull Response<EmployeeResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(new ApiResponse<>(response.body(), true, null, response.code()));
                } else {
                    String error = "Failed to fetch Students";
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
            public void onFailure(@NonNull Call<EmployeeResponse> call, @NonNull Throwable t) {
                liveData.setValue(new ApiResponse<>(null, false, "Network failure. Try again.", -1));
            }
        });

        return liveData;
    }
    public LiveData<ApiResponse<EmployeeResponse>> postRulesAndRegulations(String auth, StudentId studentId) {
        MutableLiveData<ApiResponse<EmployeeResponse>> liveData = new MutableLiveData<>();

        apiRequest.postRulesAndRegulations(auth,studentId).enqueue(new Callback<EmployeeResponse>() {
            @Override
            public void onResponse(@NonNull Call<EmployeeResponse> call, @NonNull Response<EmployeeResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(new ApiResponse<>(response.body(), true, null, response.code()));
                } else {
                    String error = "Failed to fetch Students";
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
            public void onFailure(@NonNull Call<EmployeeResponse> call, @NonNull Throwable t) {
                liveData.setValue(new ApiResponse<>(null, false, "Network failure. Try again.", -1));
            }
        });

        return liveData;
    }

    public LiveData<ApiResponse<CertificateResponse>> getLeaveCertificate(String auth, String date, int classId, int studentId) {
        MutableLiveData<ApiResponse<CertificateResponse>> liveData = new MutableLiveData<>();

        apiRequest.getLeaveCertificate(auth, date, classId, studentId).enqueue(new Callback<CertificateResponse>() {
            @Override
            public void onResponse(@NonNull Call<CertificateResponse> call, @NonNull Response<CertificateResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(new ApiResponse<>(response.body(), true, null, response.code()));
                } else {
                    String error = "Failed to fetch Leave Certificate";
                    try {
                        if (response.errorBody() != null) {
                            error = response.errorBody().string();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "getLeaveCertificate: " + e.getMessage());
                    }
                    liveData.setValue(new ApiResponse<>(null, false, error, response.code()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<CertificateResponse> call, @NonNull Throwable t) {
                liveData.setValue(new ApiResponse<>(null, false, "Network failure. Try again.", -1));
            }
        });

        return liveData;
    }

    public LiveData<ApiResponse<CertificateResponse>> getCharacterCertificate(String auth, String date, int classId, int studentId) {
        MutableLiveData<ApiResponse<CertificateResponse>> liveData = new MutableLiveData<>();

        apiRequest.getCharacterCertificate(auth, date, classId, studentId).enqueue(new Callback<CertificateResponse>() {
            @Override
            public void onResponse(@NonNull Call<CertificateResponse> call, @NonNull Response<CertificateResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(new ApiResponse<>(response.body(), true, null, response.code()));
                } else {
                    String error = "Failed to fetch Character Certificate";
                    try {
                        if (response.errorBody() != null) {
                            error = response.errorBody().string();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "getCharacterCertificate: " + e.getMessage());
                    }
                    liveData.setValue(new ApiResponse<>(null, false, error, response.code()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<CertificateResponse> call, @NonNull Throwable t) {
                liveData.setValue(new ApiResponse<>(null, false, "Network failure. Try again.", -1));
            }
        });

        return liveData;
    }
    public LiveData<ApiResponse<ResultPdfResponse>> getPdfResult(String auth, int studentId) {
        MutableLiveData<ApiResponse<ResultPdfResponse>> liveData = new MutableLiveData<>();

        apiRequest.getPdfResult(auth,studentId).enqueue(new Callback<ResultPdfResponse>() {
            @Override
            public void onResponse(@NonNull Call<ResultPdfResponse> call, @NonNull Response<ResultPdfResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(new ApiResponse<>(response.body(), true, null, response.code()));
                } else {
                    String error = "Failed to fetch Students";
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
            public void onFailure(@NonNull Call<ResultPdfResponse> call, @NonNull Throwable t) {
                liveData.setValue(new ApiResponse<>(null, false, "Network failure. Try again.", -1));
            }
        });

        return liveData;
    }
    public LiveData<ApiResponse<StudentInfoReport>> getStudentReportClassWise(String auth,int classId) {
        MutableLiveData<ApiResponse<StudentInfoReport>> liveData = new MutableLiveData<>();

        apiRequest.getStudentReportClassWise(auth,classId).enqueue(new Callback<StudentInfoReport>() {
            @Override
            public void onResponse(@NonNull Call<StudentInfoReport> call, @NonNull Response<StudentInfoReport> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(new ApiResponse<>(response.body(), true, null, response.code()));
                } else {
                    String error = "Failed to fetch Students";
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
            public void onFailure(@NonNull Call<StudentInfoReport> call, @NonNull Throwable t) {
                liveData.setValue(new ApiResponse<>(null, false, "Network failure. Try again.", -1));
            }
        });

        return liveData;
    }
    public LiveData<ApiResponse<ClassWiseAttendanceReport>> getStudentAttendanceClassWise(String auth, String date) {
        MutableLiveData<ApiResponse<ClassWiseAttendanceReport>> liveData = new MutableLiveData<>();

        apiRequest.getStudentAttendanceClassWise(auth,date).enqueue(new Callback<ClassWiseAttendanceReport>() {
            @Override
            public void onResponse(@NonNull Call<ClassWiseAttendanceReport> call, @NonNull Response<ClassWiseAttendanceReport> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(new ApiResponse<>(response.body(), true, null, response.code()));
                } else {
                    String error = "Failed to fetch Students";
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
            public void onFailure(@NonNull Call<ClassWiseAttendanceReport> call, @NonNull Throwable t) {
                liveData.setValue(new ApiResponse<>(null, false, "Network failure. Try again.", -1));
            }
        });

        return liveData;
    }
    // Repository Method
    public LiveData<ApiResponse<StudentsAttendanceReport>> getStudentAttendanceReport(String auth, int classId, String startDate, String endDate) {
        MutableLiveData<ApiResponse<StudentsAttendanceReport>> liveData = new MutableLiveData<>();

        apiRequest.getStudentAttendanceReport(auth, classId, startDate, endDate).enqueue(new Callback<StudentsAttendanceReport>() {
            @Override
            public void onResponse(@NonNull Call<StudentsAttendanceReport> call, @NonNull Response<StudentsAttendanceReport> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(new ApiResponse<>(response.body(), true, null, response.code()));
                } else {
                    String error = "Failed to fetch student attendance report";
                    try {
                        if (response.errorBody() != null) {
                            error = response.errorBody().string();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "getStudentAttendanceReport: " + e.getMessage());
                    }
                    liveData.setValue(new ApiResponse<>(null, false, error, response.code()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<StudentsAttendanceReport> call, @NonNull Throwable t) {
                liveData.setValue(new ApiResponse<>(null, false, "Network failure. Try again.", -1));
            }
        });

        return liveData;
    }
    public LiveData<ApiResponse<EmployeeAttendanceReport>> getEmployeeAttendanceReport(String auth, String startDate, String endDate) {
        MutableLiveData<ApiResponse<EmployeeAttendanceReport>> liveData = new MutableLiveData<>();

        apiRequest.getEmployeeAttendanceReport(auth, startDate, endDate).enqueue(new Callback<EmployeeAttendanceReport>() {
            @Override
            public void onResponse(@NonNull Call<EmployeeAttendanceReport> call, @NonNull Response<EmployeeAttendanceReport> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(new ApiResponse<>(response.body(), true, null, response.code()));
                } else {
                    String error = "Failed to fetch student attendance report";
                    try {
                        if (response.errorBody() != null) {
                            error = response.errorBody().string();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "getStudentAttendanceReport: " + e.getMessage());
                    }
                    liveData.setValue(new ApiResponse<>(null, false, error, response.code()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<EmployeeAttendanceReport> call, @NonNull Throwable t) {
                liveData.setValue(new ApiResponse<>(null, false, "Network failure. Try again.", -1));
            }
        });

        return liveData;
    }
    public LiveData<ApiResponse<AttendanceReport>> getStudentsMonthlyAttendance(
            String auth,
            String month,
            String year,
            int page,
            int limit,
            int classId) {

        MutableLiveData<ApiResponse<AttendanceReport>> liveData = new MutableLiveData<>();

        apiRequest.getStudentsMonthlyAttendance(auth, month, year, page, limit, classId)
                .enqueue(new Callback<AttendanceReport>() {
                    @Override
                    public void onResponse(@NonNull Call<AttendanceReport> call, @NonNull Response<AttendanceReport> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            liveData.setValue(new ApiResponse<>(response.body(), true, null, response.code()));
                        } else {
                            String error = "Failed to fetch Students Monthly Attendance Report";
                            try {
                                if (response.errorBody() != null) {
                                    error = response.errorBody().string();
                                }
                            } catch (IOException e) {
                                Log.e(TAG, "getStudentsMonthlyAttendance: " + e.getMessage());
                            }
                            liveData.setValue(new ApiResponse<>(null, false, error, response.code()));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<AttendanceReport> call, @NonNull Throwable t) {
                        liveData.setValue(new ApiResponse<>(null, false, "Network failure. Try again.", -1));
                    }
                });

        return liveData;
    }

    public LiveData<ApiResponse<AccountReport>> getAccountReport(
            String auth,
            String startDate,
            String endDate,
            int page,
            int limit
            ) {

        MutableLiveData<ApiResponse<AccountReport>> liveData = new MutableLiveData<>();

        apiRequest.getAccountReport(auth, startDate, endDate, page, limit)
                .enqueue(new Callback<AccountReport>() {
                    @Override
                    public void onResponse(@NonNull Call<AccountReport> call, @NonNull Response<AccountReport> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            liveData.setValue(new ApiResponse<>(response.body(), true, null, response.code()));
                        } else {
                            String error = "Failed to fetch Students Monthly Attendance Report";
                            try {
                                if (response.errorBody() != null) {
                                    error = response.errorBody().string();
                                }
                            } catch (IOException e) {
                                Log.e(TAG, "getStudentsMonthlyAttendance: " + e.getMessage());
                            }
                            liveData.setValue(new ApiResponse<>(null, false, error, response.code()));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<AccountReport> call, @NonNull Throwable t) {
                        liveData.setValue(new ApiResponse<>(null, false, "Network failure. Try again.", -1));
                    }
                });

        return liveData;
    }
    public LiveData<ApiResponse<FeesCollectionReport>> getStudentFeesCollectionReport(
            String auth,
            String startDate,
            String endDate,
            int page,
            int limit,
            int studentId
            ) {

        MutableLiveData<ApiResponse<FeesCollectionReport>> liveData = new MutableLiveData<>();

        apiRequest.getStudentFeesCollectionReport(auth, startDate, endDate, page, limit,studentId)
                .enqueue(new Callback<FeesCollectionReport>() {
                    @Override
                    public void onResponse(@NonNull Call<FeesCollectionReport> call, @NonNull Response<FeesCollectionReport> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            liveData.setValue(new ApiResponse<>(response.body(), true, null, response.code()));
                        } else {
                            String error = "Failed to fetch Students Monthly Attendance Report";
                            try {
                                if (response.errorBody() != null) {
                                    error = response.errorBody().string();
                                }
                            } catch (IOException e) {
                                Log.e(TAG, "getStudentsMonthlyAttendance: " + e.getMessage());
                            }
                            liveData.setValue(new ApiResponse<>(null, false, error, response.code()));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<FeesCollectionReport> call, @NonNull Throwable t) {
                        liveData.setValue(new ApiResponse<>(null, false, "Network failure. Try again.", -1));
                    }
                });

        return liveData;
    }
    public LiveData<ApiResponse<GetAttendanceResponse>> getStudentsAttendance(
            String auth,
            String startDate,
            String endDate,
            int classId
    ) {

        MutableLiveData<ApiResponse<GetAttendanceResponse>> liveData = new MutableLiveData<>();

        apiRequest.getStudentsAttendance(auth, startDate, endDate, classId)
                .enqueue(new Callback<GetAttendanceResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<GetAttendanceResponse> call, @NonNull Response<GetAttendanceResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            liveData.setValue(new ApiResponse<>(response.body(), true, null, response.code()));
                        } else {
                            String error = "Failed to fetch Students Attendance";
                            try {
                                if (response.errorBody() != null) {
                                    error = response.errorBody().string();
                                }
                            } catch (IOException e) {
                                Log.e(TAG, "getStudentsAttendance: " + e.getMessage());
                            }
                            liveData.setValue(new ApiResponse<>(null, false, error, response.code()));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<GetAttendanceResponse> call, @NonNull Throwable t) {
                        liveData.setValue(new ApiResponse<>(null, false, "Network failure. Try again.", -1));
                    }
                });

        return liveData;
    }



    public LiveData<ApiResponse<GetEmployeeAttendance>> getEmployeeAttendance(
            String auth,
            String startDate
    ) {

        MutableLiveData<ApiResponse<GetEmployeeAttendance>> liveData = new MutableLiveData<>();

        apiRequest.getEmployeeAttendance(auth, startDate)
                .enqueue(new Callback<GetEmployeeAttendance>() {
                    @Override
                    public void onResponse(@NonNull Call<GetEmployeeAttendance> call, @NonNull Response<GetEmployeeAttendance> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            liveData.setValue(new ApiResponse<>(response.body(), true, null, response.code()));
                        } else {
                            String error = "Failed to fetch Students Attendance";
                            try {
                                if (response.errorBody() != null) {
                                    error = response.errorBody().string();
                                }
                            } catch (IOException e) {
                                Log.e(TAG, "getStudentsAttendance: " + e.getMessage());
                            }
                            liveData.setValue(new ApiResponse<>(null, false, error, response.code()));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<GetEmployeeAttendance> call, @NonNull Throwable t) {
                        liveData.setValue(new ApiResponse<>(null, false, "Network failure. Try again.", -1));
                    }
                });

        return liveData;
    }
    public LiveData<ApiResponse<FeesCollectionReport>> getClassFeesCollectionReport(
            String auth,
            String startDate,
            String endDate,
            int page,
            int limit,
            int classId
            ) {

        MutableLiveData<ApiResponse<FeesCollectionReport>> liveData = new MutableLiveData<>();

        apiRequest.getClassFeesCollectionReport(auth, startDate, endDate, page, limit,classId)
                .enqueue(new Callback<FeesCollectionReport>() {
                    @Override
                    public void onResponse(@NonNull Call<FeesCollectionReport> call, @NonNull Response<FeesCollectionReport> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            liveData.setValue(new ApiResponse<>(response.body(), true, null, response.code()));
                        } else {
                            String error = "Failed to fetch Students Monthly Attendance Report";
                            try {
                                if (response.errorBody() != null) {
                                    error = response.errorBody().string();
                                }
                            } catch (IOException e) {
                                Log.e(TAG, "getStudentsMonthlyAttendance: " + e.getMessage());
                            }
                            liveData.setValue(new ApiResponse<>(null, false, error, response.code()));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<FeesCollectionReport> call, @NonNull Throwable t) {
                        liveData.setValue(new ApiResponse<>(null, false, "Network failure. Try again.", -1));
                    }
                });

        return liveData;
    }
    public LiveData<ApiResponse<AttendanceReport>> getEmployeeMonthlyAttendance(
            String auth,
            String month,
            String year,
            int page,
            int limit,
            String role) {

        MutableLiveData<ApiResponse<AttendanceReport>> liveData = new MutableLiveData<>();

        apiRequest.getEmployeeMonthlyAttendance(auth, month, year, page, limit, role)
                .enqueue(new Callback<AttendanceReport>() {
                    @Override
                    public void onResponse(@NonNull Call<AttendanceReport> call, @NonNull Response<AttendanceReport> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            liveData.setValue(new ApiResponse<>(response.body(), true, null, response.code()));
                        } else {
                            String error = "Failed to fetch Students Monthly Attendance Report";
                            try {
                                if (response.errorBody() != null) {
                                    error = response.errorBody().string();
                                }
                            } catch (IOException e) {
                                Log.e(TAG, "getStudentsMonthlyAttendance: " + e.getMessage());
                            }
                            liveData.setValue(new ApiResponse<>(null, false, error, response.code()));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<AttendanceReport> call, @NonNull Throwable t) {
                        liveData.setValue(new ApiResponse<>(null, false, "Network failure. Try again.", -1));
                    }
                });

        return liveData;
    }

    public LiveData<ApiResponse<EmployeeResponse>> updateQuestionPaper(String auth,int questionPaperId, CreateQuestionPaper createQuestionPaper) {
        final MutableLiveData<ApiResponse<EmployeeResponse>> liveData = new MutableLiveData<>();

        apiRequest.updateQuestionPaper(auth,questionPaperId, createQuestionPaper).enqueue(new Callback<EmployeeResponse>() {
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
    public LiveData<ApiResponse<EmployeeResponse>> deleteQuestionPaper(String auth,int questionPaperId) {
        MutableLiveData<ApiResponse<EmployeeResponse>> liveData = new MutableLiveData<>();

        apiRequest.deleteQuestionPaper(auth,questionPaperId).enqueue(new Callback<EmployeeResponse>() {
            @Override
            public void onResponse(@NonNull Call<EmployeeResponse> call, @NonNull Response<EmployeeResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(new ApiResponse<>(response.body(), true, null, response.code()));
                } else {
                    String error = "Failed to fetch Students";
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
            public void onFailure(@NonNull Call<EmployeeResponse> call, @NonNull Throwable t) {
                liveData.setValue(new ApiResponse<>(null, false, "Network failure. Try again.", -1));
            }
        });

        return liveData;
    }
    public LiveData<ApiResponse<EmployeeResponse>> updateQuestionBank(String auth,int questionBankId, AddQuestionBank addQuestionBank) {
        final MutableLiveData<ApiResponse<EmployeeResponse>> liveData = new MutableLiveData<>();

        apiRequest.updateQuestionBank(auth,questionBankId, addQuestionBank).enqueue(new Callback<EmployeeResponse>() {
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
    public LiveData<ApiResponse<EmployeeResponse>> deleteQuestionBAnk(String auth,int questionBankId) {
        final MutableLiveData<ApiResponse<EmployeeResponse>> liveData = new MutableLiveData<>();

        apiRequest.deleteQuestionBank(auth,questionBankId).enqueue(new Callback<EmployeeResponse>() {
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
    public LiveData<ApiResponse<QuestionBankModel>> getQuestionBank(String auth) {
        MutableLiveData<ApiResponse<QuestionBankModel>> liveData = new MutableLiveData<>();

        apiRequest.getQuestionBank(auth).enqueue(new Callback<QuestionBankModel>() {
            @Override
            public void onResponse(@NonNull Call<QuestionBankModel> call, @NonNull Response<QuestionBankModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(new ApiResponse<>(response.body(), true, null, response.code()));
                } else {
                    String error = "Failed to fetch Students";
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
            public void onFailure(@NonNull Call<QuestionBankModel> call, @NonNull Throwable t) {
                liveData.setValue(new ApiResponse<>(null, false, "Network failure. Try again.", -1));
            }
        });

        return liveData;
    }

    public LiveData<ApiResponse<EmployeeResponse>> saveStudentAttendance(String auth, PostStudentAttendance attendanceRequest) {
        final MutableLiveData<ApiResponse<EmployeeResponse>> liveData = new MutableLiveData<>();

        apiRequest.saveStudentAttendance(auth, attendanceRequest).enqueue(new Callback<EmployeeResponse>() {
            @Override
            public void onResponse(@NonNull Call<EmployeeResponse> call, @NonNull Response<EmployeeResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(new ApiResponse<>(response.body(), true, null, response.code()));
                } else {
                    String errorMessage = "Failed to save student attendance";
                    try {
                        if (response.errorBody() != null) {
                            errorMessage = response.errorBody().string();
                        }
                    } catch (IOException e) {
                        errorMessage = "Error reading error response";
                        Log.e(TAG, "saveStudentAttendance: " + e.getMessage());
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
    public LiveData<ApiResponse<EmployeeResponse>> saveEmployeeAttendance(String auth, PostEmployeeAttendance attendanceRequest) {
        final MutableLiveData<ApiResponse<EmployeeResponse>> liveData = new MutableLiveData<>();

        apiRequest.saveEmployeeAttendance(auth, attendanceRequest).enqueue(new Callback<EmployeeResponse>() {
            @Override
            public void onResponse(@NonNull Call<EmployeeResponse> call, @NonNull Response<EmployeeResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(new ApiResponse<>(response.body(), true, null, response.code()));
                } else {
                    String errorMessage = "Failed to save student attendance";
                    try {
                        if (response.errorBody() != null) {
                            errorMessage = response.errorBody().string();
                        }
                    } catch (IOException e) {
                        errorMessage = "Error reading error response";
                        Log.e(TAG, "saveStudentAttendance: " + e.getMessage());
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
    public LiveData<ApiResponse<EmployeeResponse>> updateStudentAttendance(String auth, PostStudentAttendance attendanceRequest) {
        final MutableLiveData<ApiResponse<EmployeeResponse>> liveData = new MutableLiveData<>();

        apiRequest.updateStudentAttendance(auth, attendanceRequest).enqueue(new Callback<EmployeeResponse>() {
            @Override
            public void onResponse(@NonNull Call<EmployeeResponse> call, @NonNull Response<EmployeeResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(new ApiResponse<>(response.body(), true, null, response.code()));
                } else {
                    String errorMessage = "Failed to save student attendance";
                    try {
                        if (response.errorBody() != null) {
                            errorMessage = response.errorBody().string();
                        }
                    } catch (IOException e) {
                        errorMessage = "Error reading error response";
                        Log.e(TAG, "saveStudentAttendance: " + e.getMessage());
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
    public LiveData<ApiResponse<EmployeeResponse>> updateEmployeeAttendance(String auth, PostEmployeeAttendance attendanceRequest) {
        final MutableLiveData<ApiResponse<EmployeeResponse>> liveData = new MutableLiveData<>();

        apiRequest.updateEmployeeAttendance(auth, attendanceRequest).enqueue(new Callback<EmployeeResponse>() {
            @Override
            public void onResponse(@NonNull Call<EmployeeResponse> call, @NonNull Response<EmployeeResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(new ApiResponse<>(response.body(), true, null, response.code()));
                } else {
                    String errorMessage = "Failed to save student attendance";
                    try {
                        if (response.errorBody() != null) {
                            errorMessage = response.errorBody().string();
                        }
                    } catch (IOException e) {
                        errorMessage = "Error reading error response";
                        Log.e(TAG, "saveStudentAttendance: " + e.getMessage());
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
    public LiveData<ApiResponse<EmployeeResponse>> createHomework(String auth, CreateHomework createHomework) {
        final MutableLiveData<ApiResponse<EmployeeResponse>> liveData = new MutableLiveData<>();

        apiRequest.createHomework(auth, createHomework).enqueue(new Callback<EmployeeResponse>() {
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
    public LiveData<ApiResponse<EmployeeResponse>> addStudentMarks(String auth, StudentMarksRequest studentMarksRequest) {
        final MutableLiveData<ApiResponse<EmployeeResponse>> liveData = new MutableLiveData<>();

        apiRequest.addStudentMarks(auth, studentMarksRequest).enqueue(new Callback<EmployeeResponse>() {
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
    public LiveData<ApiResponse<EmployeeResponse>> createChart(String auth, CreateChart createChart) {
        final MutableLiveData<ApiResponse<EmployeeResponse>> liveData = new MutableLiveData<>();

        apiRequest.createChart(auth, createChart).enqueue(new Callback<EmployeeResponse>() {
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
    public LiveData<ApiResponse<EmployeeResponse>> editAccountChart(String auth,int id, CreateChart createChart) {
        final MutableLiveData<ApiResponse<EmployeeResponse>> liveData = new MutableLiveData<>();

        apiRequest.editAccountChart(auth,id,createChart).enqueue(new Callback<EmployeeResponse>() {
            @Override
            public void onResponse(@NonNull Call<EmployeeResponse> call, @NonNull Response<EmployeeResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(new ApiResponse<>(response.body(), true, null, response.code()));
                } else {
                    String errorMessage = "Failed to Edit Chart";
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
    public LiveData<ApiResponse<EmployeeResponse>> addIncome(String auth, AddIncome addIncome) {
        final MutableLiveData<ApiResponse<EmployeeResponse>> liveData = new MutableLiveData<>();

        apiRequest.addIncome(auth, addIncome).enqueue(new Callback<EmployeeResponse>() {
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
    public LiveData<ApiResponse<EmployeeResponse>> addExpense(String auth, AddIncome addIncome) {
        final MutableLiveData<ApiResponse<EmployeeResponse>> liveData = new MutableLiveData<>();

        apiRequest.addExpense(auth, addIncome).enqueue(new Callback<EmployeeResponse>() {
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
    public LiveData<ApiResponse<GetStudentTest>> getTestMarks(String auth, int classId, int studentId, String date) {
        final MutableLiveData<ApiResponse<GetStudentTest>> liveData = new MutableLiveData<>();

        apiRequest.getTestMarks(auth, classId,studentId,date).enqueue(new Callback<GetStudentTest>() {
            @Override
            public void onResponse(@NonNull Call<GetStudentTest> call, @NonNull Response<GetStudentTest> response) {
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
            public void onFailure(@NonNull Call<GetStudentTest> call, @NonNull Throwable t) {
                liveData.setValue(new ApiResponse<>(null, false, "Network failure. Try again.", -1));
            }
        });

        return liveData;
    }
    public LiveData<ApiResponse<EmployeeResponse>> postTestMarks(String auth, CreateTest createTest) {
        final MutableLiveData<ApiResponse<EmployeeResponse>> liveData = new MutableLiveData<>();

        apiRequest.addClassTest(auth,createTest).enqueue(new Callback<EmployeeResponse>() {
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
    public LiveData<ApiResponse<EmployeeResponse>> updateTestMarks(String auth, CreateTest createTest) {
        final MutableLiveData<ApiResponse<EmployeeResponse>> liveData = new MutableLiveData<>();

        apiRequest.updateClassTest(auth,createTest).enqueue(new Callback<EmployeeResponse>() {
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
    public LiveData<ApiResponse<EmployeeResponse>> createExam(String auth, CreateExam createExam) {
        final MutableLiveData<ApiResponse<EmployeeResponse>> liveData = new MutableLiveData<>();

        apiRequest.addExam(auth, createExam).enqueue(new Callback<EmployeeResponse>() {
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
    public LiveData<ApiResponse<ClassModel2>> getAllClassesWithId(String auth, int classId) {
        MutableLiveData<ApiResponse<ClassModel2>> liveData = new MutableLiveData<>();

        apiRequest.getAllClassesWithId(auth,classId).enqueue(new Callback<ClassModel2>() {
            @Override
            public void onResponse(@NonNull Call<ClassModel2> call, @NonNull Response<ClassModel2> response) {
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
            public void onFailure(@NonNull Call<ClassModel2> call, @NonNull Throwable t) {
                liveData.setValue(new ApiResponse<>(null, false, "Network failure. Try again.", -1));
            }
        });

        return liveData;
    }
    public LiveData<ApiResponse<ExamModel>> getAllExams(String auth) {
        MutableLiveData<ApiResponse<ExamModel>> liveData = new MutableLiveData<>();

        apiRequest.getExams(auth).enqueue(new Callback<ExamModel>() {
            @Override
            public void onResponse(@NonNull Call<ExamModel> call, @NonNull Response<ExamModel> response) {
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
            public void onFailure(@NonNull Call<ExamModel> call, @NonNull Throwable t) {
                liveData.setValue(new ApiResponse<>(null, false, "Network failure. Try again.", -1));
            }
        });

        return liveData;
    }
    public LiveData<ApiResponse<AccountGet>> getAccountChart(String auth) {
        MutableLiveData<ApiResponse<AccountGet>> liveData = new MutableLiveData<>();

        apiRequest.getAccountChart(auth).enqueue(new Callback<AccountGet>() {
            @Override
            public void onResponse(@NonNull Call<AccountGet> call, @NonNull Response<AccountGet> response) {
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
            public void onFailure(@NonNull Call<AccountGet> call, @NonNull Throwable t) {
                liveData.setValue(new ApiResponse<>(null, false, "Network failure. Try again.", -1));
            }
        });

        return liveData;
    }
    public LiveData<ApiResponse<AddStudentMark>> getStudentMarks(String auth, int examId, int studentId, int classId) {
        MutableLiveData<ApiResponse<AddStudentMark>> liveData = new MutableLiveData<>();

        apiRequest.getStudentMarks(auth,examId,studentId,classId).enqueue(new Callback<AddStudentMark>() {
            @Override
            public void onResponse(@NonNull Call<AddStudentMark> call, @NonNull Response<AddStudentMark> response) {
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
            public void onFailure(@NonNull Call<AddStudentMark> call, @NonNull Throwable t) {
                liveData.setValue(new ApiResponse<>(null, false, "Network failure. Try again.", -1));
            }
        });

        return liveData;
    }
    public LiveData<ApiResponse<AccountStatement>> getAccountStatement(String auth) {
        MutableLiveData<ApiResponse<AccountStatement>> liveData = new MutableLiveData<>();

        apiRequest.getAccountStatement(auth).enqueue(new Callback<AccountStatement>() {
            @Override
            public void onResponse(@NonNull Call<AccountStatement> call, @NonNull Response<AccountStatement> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(new ApiResponse<>(response.body(), true, null, response.code()));
                } else {
                    String error = "Failed to fetch Account Statement";
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
            public void onFailure(@NonNull Call<AccountStatement> call, @NonNull Throwable t) {
                liveData.setValue(new ApiResponse<>(null, false, "Network failure. Try again.", -1));
            }
        });

        return liveData;
    }
    public LiveData<ApiResponse<AddStudentMark>> getClassMarks(String auth, int examId,int classId) {
        MutableLiveData<ApiResponse<AddStudentMark>> liveData = new MutableLiveData<>();

        apiRequest.getClassMarks(auth,examId,classId).enqueue(new Callback<AddStudentMark>() {
            @Override
            public void onResponse(@NonNull Call<AddStudentMark> call, @NonNull Response<AddStudentMark> response) {
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
            public void onFailure(@NonNull Call<AddStudentMark> call, @NonNull Throwable t) {
                liveData.setValue(new ApiResponse<>(null, false, "Network failure. Try again.", -1));
            }
        });

        return liveData;
    }
    public LiveData<ApiResponse<AllStudentResponse>> getAllStudents(String auth) {
        MutableLiveData<ApiResponse<AllStudentResponse>> liveData = new MutableLiveData<>();

        apiRequest.getAllStudents(auth).enqueue(new Callback<AllStudentResponse>() {
            @Override
            public void onResponse(@NonNull Call<AllStudentResponse> call, @NonNull Response<AllStudentResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(new ApiResponse<>(response.body(), true, null, response.code()));
                } else {
                    String error = "Failed to fetch students";
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
            public void onFailure(@NonNull Call<AllStudentResponse> call, @NonNull Throwable t) {
                liveData.setValue(new ApiResponse<>(null, false, "Network failure. Try again.", -1));
            }
        });

        return liveData;
    }
    public LiveData<ApiResponse<AllStudentResponse>> getStudent(String auth,int studentId) {
        MutableLiveData<ApiResponse<AllStudentResponse>> liveData = new MutableLiveData<>();

        apiRequest.getStudent(auth,studentId).enqueue(new Callback<AllStudentResponse>() {
            @Override
            public void onResponse(@NonNull Call<AllStudentResponse> call, @NonNull Response<AllStudentResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(new ApiResponse<>(response.body(), true, null, response.code()));
                } else {
                    String error = "Failed to fetch students";
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
            public void onFailure(@NonNull Call<AllStudentResponse> call, @NonNull Throwable t) {
                liveData.setValue(new ApiResponse<>(null, false, "Network failure. Try again.", -1));
            }
        });

        return liveData;
    }
    public LiveData<ApiResponse<AllStudentResponse>> getBasicList(String auth,int classId) {
        MutableLiveData<ApiResponse<AllStudentResponse>> liveData = new MutableLiveData<>();

        apiRequest.getBasicList(auth,classId).enqueue(new Callback<AllStudentResponse>() {
            @Override
            public void onResponse(@NonNull Call<AllStudentResponse> call, @NonNull Response<AllStudentResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(new ApiResponse<>(response.body(), true, null, response.code()));
                } else {
                    String error = "Failed to fetch Students";
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
            public void onFailure(@NonNull Call<AllStudentResponse> call, @NonNull Throwable t) {
                liveData.setValue(new ApiResponse<>(null, false, "Network failure. Try again.", -1));
            }
        });

        return liveData;
    }
    public LiveData<ApiResponse<ClassTestResult>> getClassSubjectWise(String auth, int classId, int subjectId) {
        MutableLiveData<ApiResponse<ClassTestResult>> liveData = new MutableLiveData<>();

        apiRequest.getClassSubjectWise(auth,classId,subjectId).enqueue(new Callback<ClassTestResult>() {
            @Override
            public void onResponse(@NonNull Call<ClassTestResult> call, @NonNull Response<ClassTestResult> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(new ApiResponse<>(response.body(), true, null, response.code()));
                } else {
                    String error = "Failed to fetch Students";
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
            public void onFailure(@NonNull Call<ClassTestResult> call, @NonNull Throwable t) {
                liveData.setValue(new ApiResponse<>(null, false, "Network failure. Try again.", -1));
            }
        });

        return liveData;
    }
    public LiveData<ApiResponse<ClassTestResult>> getStudentSubjectWise(String auth, int studentId, int subjectId) {
        MutableLiveData<ApiResponse<ClassTestResult>> liveData = new MutableLiveData<>();

        apiRequest.getStudentSubjectWise(auth,studentId,subjectId).enqueue(new Callback<ClassTestResult>() {
            @Override
            public void onResponse(@NonNull Call<ClassTestResult> call, @NonNull Response<ClassTestResult> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(new ApiResponse<>(response.body(), true, null, response.code()));
                } else {
                    String error = "Failed to fetch Students";
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
            public void onFailure(@NonNull Call<ClassTestResult> call, @NonNull Throwable t) {
                liveData.setValue(new ApiResponse<>(null, false, "Network failure. Try again.", -1));
            }
        });

        return liveData;
    }
    public LiveData<ApiResponse<ClassTestResult>> getDateRangeWise(String auth, String startDate,String endDate) {
        MutableLiveData<ApiResponse<ClassTestResult>> liveData = new MutableLiveData<>();

        apiRequest.getDateRangeWise(auth,startDate,endDate).enqueue(new Callback<ClassTestResult>() {
            @Override
            public void onResponse(@NonNull Call<ClassTestResult> call, @NonNull Response<ClassTestResult> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(new ApiResponse<>(response.body(), true, null, response.code()));
                } else {
                    String error = "Failed to fetch Students";
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
            public void onFailure(@NonNull Call<ClassTestResult> call, @NonNull Throwable t) {
                liveData.setValue(new ApiResponse<>(null, false, "Network failure. Try again.", -1));
            }
        });

        return liveData;
    }
    public LiveData<ApiResponse<AllStudentResponse>> getStudentsDetails(String auth,int studentID) {
        MutableLiveData<ApiResponse<AllStudentResponse>> liveData = new MutableLiveData<>();

        apiRequest.getStudentDetails(auth,studentID).enqueue(new Callback<AllStudentResponse>() {
            @Override
            public void onResponse(@NonNull Call<AllStudentResponse> call, @NonNull Response<AllStudentResponse> response) {
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
            public void onFailure(@NonNull Call<AllStudentResponse> call, @NonNull Throwable t) {
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
    public LiveData<ApiResponse<SalaryPaidResponse>> salaryDetails(String auth, Integer employeeId) {
        MutableLiveData<ApiResponse<SalaryPaidResponse>> liveData = new MutableLiveData<>();

        apiRequest.getEmployeeSalaryDetails(auth, employeeId).enqueue(new Callback<SalaryPaidResponse>() {
            @Override
            public void onResponse(@NonNull Call<SalaryPaidResponse> call,
                                   @NonNull Response<SalaryPaidResponse> response) {
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
            public void onFailure(@NonNull Call<SalaryPaidResponse> call, @NonNull Throwable t) {
                liveData.setValue(new ApiResponse<>(null, false, "Network failure. Try again.", -1));
            }
        });

        return liveData;
    }
    public LiveData<ApiResponse<EmployeeResponse>> submitFees(String auth, SubmitFees submitFees) {
        MutableLiveData<ApiResponse<EmployeeResponse>> liveData = new MutableLiveData<>();

        apiRequest.postFees(auth, submitFees).enqueue(new Callback<EmployeeResponse>() {
            @Override
            public void onResponse(@NonNull Call<EmployeeResponse> call,
                                   @NonNull Response<EmployeeResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(new ApiResponse<>(response.body(), true, response.body().getMessage(), response.code()));
                } else {
                    String error = "Failed to submit fees";
                    try {
                        if (response.errorBody() != null) {
                            error = response.errorBody().string();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "submitFees: " + e.getMessage());
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
    public LiveData<ApiResponse<SalaryPaidResponse>> getSalaryReport(String auth, String salaryMonth, Integer employeeId, String dateRange) {
        MutableLiveData<ApiResponse<SalaryPaidResponse>> liveData = new MutableLiveData<>();

        apiRequest.getEmployeeSalaryList(auth, salaryMonth, employeeId, dateRange).enqueue(new Callback<SalaryPaidResponse>() {
            @Override
            public void onResponse(@NonNull Call<SalaryPaidResponse> call,
                                   @NonNull Response<SalaryPaidResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(new ApiResponse<>(response.body(), true, response.body().getMessage(), response.code()));
                } else {
                    String error = "Failed to load salary report";
                    try {
                        if (response.errorBody() != null) {
                            error = response.errorBody().string();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "getSalaryReport error: " + e.getMessage());
                    }
                    liveData.setValue(new ApiResponse<>(null, false, error, response.code()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<SalaryPaidResponse> call, @NonNull Throwable t) {
                liveData.setValue(new ApiResponse<>(null, false, "Network failure. Try again.", -1));
            }
        });

        return liveData;
    }
    public LiveData<ApiResponse<EmployeeResponse>> paySalary(String auth, Integer employeeId,  String salaryMonth, String dueDate, String fixedSalary, String bonus, String deduction ) {
        MutableLiveData<ApiResponse<EmployeeResponse>> liveData = new MutableLiveData<>();

        apiRequest.paySalary(auth, employeeId,salaryMonth,dueDate,fixedSalary,bonus,deduction).enqueue(new Callback<EmployeeResponse>() {
            @Override
            public void onResponse(@NonNull Call<EmployeeResponse> call,
                                   @NonNull Response<EmployeeResponse> response) {
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
            public void onFailure(@NonNull Call<EmployeeResponse> call, @NonNull Throwable t) {
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
    public LiveData<ApiResponse<FeesReportResponse>> getFeesReport(String auth, Integer classId,
                                                                   Integer studentId, String startDate, String endDate, Integer page, Integer size) {
        MutableLiveData<ApiResponse<FeesReportResponse>> liveData = new MutableLiveData<>();

        apiRequest.getFeesReport(auth, classId, studentId, startDate, endDate, page, size)
                .enqueue(new Callback<FeesReportResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<FeesReportResponse> call,
                                           @NonNull Response<FeesReportResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            liveData.setValue(new ApiResponse<>(response.body(), true,
                                    response.message(), response.code()));
                        } else {
                            String error = "Failed to fetch fees report";
                            try {
                                if (response.errorBody() != null) {
                                    error = response.errorBody().string();
                                }
                            } catch (IOException e) {
                                Log.e(TAG, "getFeesReport: " + e.getMessage());
                            }
                            liveData.setValue(new ApiResponse<>(null, false, error, response.code()));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<FeesReportResponse> call, @NonNull Throwable t) {
                        liveData.setValue(new ApiResponse<>(null, false, "Network failure. Try again.", -1));
                    }
                });

        return liveData;
    }
    public LiveData<ApiResponse<FeesRecordResponse>> getFeesRecord(String auth, Integer studentId, String feesMonth) {
        MutableLiveData<ApiResponse<FeesRecordResponse>> liveData = new MutableLiveData<>();

        apiRequest.getFeesRecord(auth, studentId, feesMonth)
                .enqueue(new Callback<FeesRecordResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<FeesRecordResponse> call,
                                           @NonNull Response<FeesRecordResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            liveData.setValue(new ApiResponse<>(response.body(), true,
                                    response.message(), response.code()));
                        } else {
                            String error = "Failed to fetch fees record";
                            try {
                                if (response.errorBody() != null) {
                                    error = response.errorBody().string();
                                }
                            } catch (IOException e) {
                                Log.e(TAG, "getFeesRecord: " + e.getMessage());
                            }
                            liveData.setValue(new ApiResponse<>(null, false, error, response.code()));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<FeesRecordResponse> call, @NonNull Throwable t) {
                        liveData.setValue(new ApiResponse<>(null, false, "Network failure. Try again.", -1));
                    }
                });

        return liveData;
    }
    public LiveData<ApiResponse<FeesDefaulterModel>> getFeesDefaulter(String auth, String feesMonth) {
        MutableLiveData<ApiResponse<FeesDefaulterModel>> liveData = new MutableLiveData<>();

        apiRequest.getDefaulter(auth, feesMonth)
                .enqueue(new Callback<FeesDefaulterModel>() {
                    @Override
                    public void onResponse(@NonNull Call<FeesDefaulterModel> call,
                                           @NonNull Response<FeesDefaulterModel> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            liveData.setValue(new ApiResponse<>(response.body(), true,
                                    response.message(), response.code()));
                        } else {
                            String error = "Failed to fetch fees defaulters";
                            try {
                                if (response.errorBody() != null) {
                                    error = response.errorBody().string();
                                }
                            } catch (IOException e) {
                                Log.e(TAG, "getFeesDefaulter: " + e.getMessage());
                            }
                            liveData.setValue(new ApiResponse<>(null, false, error, response.code()));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<FeesDefaulterModel> call, @NonNull Throwable t) {
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
    public LiveData<ApiResponse<EmployeeResponse>> deleteStudent(String auth,int studentId) {
        MutableLiveData<ApiResponse<EmployeeResponse>> liveData = new MutableLiveData<>();

        apiRequest.deleteStudent(auth,studentId).enqueue(new Callback<EmployeeResponse>() {
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
    public LiveData<ApiResponse<EmployeeResponse>> deleteTest(String auth,int testId) {
        MutableLiveData<ApiResponse<EmployeeResponse>> liveData = new MutableLiveData<>();

        apiRequest.deleteTest(auth,testId).enqueue(new Callback<EmployeeResponse>() {
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
    public LiveData<EmployeeResponse> updateClassFees(String auth, int classId, FeesStructure feesStructure) {
        MutableLiveData<EmployeeResponse> liveData = new MutableLiveData<>();

        apiRequest.updateClassFees(auth, classId, feesStructure).enqueue(new Callback<EmployeeResponse>() {
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

    public LiveData<ApiResponse<AdminDashboardClassInfo>> getClassInfo(String auth) {
        final MutableLiveData<ApiResponse<AdminDashboardClassInfo>> liveData = new MutableLiveData<>();

        apiRequest.getClassInfo(auth).enqueue(new Callback<AdminDashboardClassInfo>() {
            @Override
            public void onResponse(@NonNull Call<AdminDashboardClassInfo> call, @NonNull Response<AdminDashboardClassInfo> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(new ApiResponse<>(response.body(), true, null, response.code()));
                } else {
                    String errorMessage = "Failed to get class info";
                    try {
                        if (response.errorBody() != null) {
                            errorMessage = response.errorBody().string();
                        }
                    } catch (IOException e) {
                        errorMessage = "Error reading error response";
                        Log.e(TAG, "getClassInfo: " + e.getMessage());
                    }
                    liveData.setValue(new ApiResponse<>(null, false, errorMessage, response.code()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<AdminDashboardClassInfo> call, @NonNull Throwable t) {
                liveData.setValue(new ApiResponse<>(null, false, "Network failure. Try again.", -1));
            }
        });

        return liveData;
    }

    public LiveData<ApiResponse<AdminDashboardEmployeeInfo>> getEmployeeInfo(String auth) {
        final MutableLiveData<ApiResponse<AdminDashboardEmployeeInfo>> liveData = new MutableLiveData<>();

        apiRequest.getEmployeeInfo(auth).enqueue(new Callback<AdminDashboardEmployeeInfo>() {
            @Override
            public void onResponse(@NonNull Call<AdminDashboardEmployeeInfo> call, @NonNull Response<AdminDashboardEmployeeInfo> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(new ApiResponse<>(response.body(), true, null, response.code()));
                } else {
                    String errorMessage = "Failed to get employee info";
                    try {
                        if (response.errorBody() != null) {
                            errorMessage = response.errorBody().string();
                        }
                    } catch (IOException e) {
                        errorMessage = "Error reading error response";
                        Log.e(TAG, "getEmployeeInfo: " + e.getMessage());
                    }
                    liveData.setValue(new ApiResponse<>(null, false, errorMessage, response.code()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<AdminDashboardEmployeeInfo> call, @NonNull Throwable t) {
                liveData.setValue(new ApiResponse<>(null, false, "Network failure. Try again.", -1));
            }
        });

        return liveData;
    }
    public LiveData<ApiResponse<DashboardApiEstimatedFees>> getEstimatedFees(String auth) {
        final MutableLiveData<ApiResponse<DashboardApiEstimatedFees>> liveData = new MutableLiveData<>();

        apiRequest.getEstimatedFees(auth).enqueue(new Callback<DashboardApiEstimatedFees>() {
            @Override
            public void onResponse(@NonNull Call<DashboardApiEstimatedFees> call, @NonNull Response<DashboardApiEstimatedFees> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(new ApiResponse<>(response.body(), true, null, response.code()));
                } else {
                    String errorMessage = "Failed to get employee info";
                    try {
                        if (response.errorBody() != null) {
                            errorMessage = response.errorBody().string();
                        }
                    } catch (IOException e) {
                        errorMessage = "Error reading error response";
                        Log.e(TAG, "getEmployeeInfo: " + e.getMessage());
                    }
                    liveData.setValue(new ApiResponse<>(null, false, errorMessage, response.code()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<DashboardApiEstimatedFees> call, @NonNull Throwable t) {
                liveData.setValue(new ApiResponse<>(null, false, "Network failure. Try again.", -1));
            }
        });

        return liveData;
    }
    public LiveData<ApiResponse<AdminDashboardAbsentStudents>> getAbsentStudents(String auth) {
        final MutableLiveData<ApiResponse<AdminDashboardAbsentStudents>> liveData = new MutableLiveData<>();

        apiRequest.getAbsentStudents(auth).enqueue(new Callback<AdminDashboardAbsentStudents>() {
            @Override
            public void onResponse(@NonNull Call<AdminDashboardAbsentStudents> call, @NonNull Response<AdminDashboardAbsentStudents> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(new ApiResponse<>(response.body(), true, null, response.code()));
                } else {
                    String errorMessage = "Failed to get absent students";
                    try {
                        if (response.errorBody() != null) {
                            errorMessage = response.errorBody().string();
                        }
                    } catch (IOException e) {
                        errorMessage = "Error reading error response";
                        Log.e(TAG, "getAbsentStudents: " + e.getMessage());
                    }
                    liveData.setValue(new ApiResponse<>(null, false, errorMessage, response.code()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<AdminDashboardAbsentStudents> call, @NonNull Throwable t) {
                liveData.setValue(new ApiResponse<>(null, false, "Network failure. Try again.", -1));
            }
        });

        return liveData;
    }
    public LiveData<ApiResponse<AdminDashboardPresentEmployee>> getPresentEmployee(String auth) {
        final MutableLiveData<ApiResponse<AdminDashboardPresentEmployee>> liveData = new MutableLiveData<>();

        apiRequest.getPresentEmployee(auth).enqueue(new Callback<AdminDashboardPresentEmployee>() {
            @Override
            public void onResponse(@NonNull Call<AdminDashboardPresentEmployee> call, @NonNull Response<AdminDashboardPresentEmployee> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(new ApiResponse<>(response.body(), true, null, response.code()));
                } else {
                    String errorMessage = "Failed to get absent students";
                    try {
                        if (response.errorBody() != null) {
                            errorMessage = response.errorBody().string();
                        }
                    } catch (IOException e) {
                        errorMessage = "Error reading error response";
                        Log.e(TAG, "getAbsentStudents: " + e.getMessage());
                    }
                    liveData.setValue(new ApiResponse<>(null, false, errorMessage, response.code()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<AdminDashboardPresentEmployee> call, @NonNull Throwable t) {
                liveData.setValue(new ApiResponse<>(null, false, "Network failure. Try again.", -1));
            }
        });

        return liveData;
    }
    public LiveData<ApiResponse<AdminDashboardAccountsOverview>> getAccountsOverview(String auth) {
        final MutableLiveData<ApiResponse<AdminDashboardAccountsOverview>> liveData = new MutableLiveData<>();

        apiRequest.getAccountsOverview(auth).enqueue(new Callback<AdminDashboardAccountsOverview>() {
            @Override
            public void onResponse(@NonNull Call<AdminDashboardAccountsOverview> call, @NonNull Response<AdminDashboardAccountsOverview> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(new ApiResponse<>(response.body(), true, null, response.code()));
                } else {
                    String errorMessage = "Failed to get accounts overview";
                    try {
                        if (response.errorBody() != null) {
                            errorMessage = response.errorBody().string();
                        }
                    } catch (IOException e) {
                        errorMessage = "Error reading error response";
                        Log.e(TAG, "getAccountsOverview: " + e.getMessage());
                    }
                    liveData.setValue(new ApiResponse<>(null, false, errorMessage, response.code()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<AdminDashboardAccountsOverview> call, @NonNull Throwable t) {
                liveData.setValue(new ApiResponse<>(null, false, "Network failure. Try again.", -1));
            }
        });

        return liveData;
    }
    public LiveData<ApiResponse<AdminDashboardNewAdmission>> getNewAdmission(String auth) {
        final MutableLiveData<ApiResponse<AdminDashboardNewAdmission>> liveData = new MutableLiveData<>();

        apiRequest.getNewAdmission(auth).enqueue(new Callback<AdminDashboardNewAdmission>() {
            @Override
            public void onResponse(@NonNull Call<AdminDashboardNewAdmission> call, @NonNull Response<AdminDashboardNewAdmission> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(new ApiResponse<>(response.body(), true, null, response.code()));
                } else {
                    String errorMessage = "Failed to get absent students";
                    try {
                        if (response.errorBody() != null) {
                            errorMessage = response.errorBody().string();
                        }
                    } catch (IOException e) {
                        errorMessage = "Error reading error response";
                        Log.e(TAG, "getAbsentStudents: " + e.getMessage());
                    }
                    liveData.setValue(new ApiResponse<>(null, false, errorMessage, response.code()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<AdminDashboardNewAdmission> call, @NonNull Throwable t) {
                liveData.setValue(new ApiResponse<>(null, false, "Network failure. Try again.", -1));
            }
        });

        return liveData;
    }
    public LiveData<ApiResponse<AdminDashboardExpenseInfo>> getExpenseInfo(String auth) {
        final MutableLiveData<ApiResponse<AdminDashboardExpenseInfo>> liveData = new MutableLiveData<>();

        apiRequest.getExpenseInfo(auth).enqueue(new Callback<AdminDashboardExpenseInfo>() {
            @Override
            public void onResponse(@NonNull Call<AdminDashboardExpenseInfo> call, @NonNull Response<AdminDashboardExpenseInfo> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(new ApiResponse<>(response.body(), true, null, response.code()));
                } else {
                    String errorMessage = "Failed to get expense info";
                    try {
                        if (response.errorBody() != null) {
                            errorMessage = response.errorBody().string();
                        }
                    } catch (IOException e) {
                        errorMessage = "Error reading error response";
                        Log.e(TAG, "getExpenseInfo: " + e.getMessage());
                    }
                    liveData.setValue(new ApiResponse<>(null, false, errorMessage, response.code()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<AdminDashboardExpenseInfo> call, @NonNull Throwable t) {
                liveData.setValue(new ApiResponse<>(null, false, "Network failure. Try again.", -1));
            }
        });

        return liveData;
    }
    public LiveData<ApiResponse<List<AdminDashBoardClassGraph>>> getClassGraph(String auth) {
        final MutableLiveData<ApiResponse<List<AdminDashBoardClassGraph>>> liveData = new MutableLiveData<>();

        apiRequest.getClassGraph(auth).enqueue(new Callback<List<AdminDashBoardClassGraph>>() {
            @Override
            public void onResponse(@NonNull Call<List<AdminDashBoardClassGraph>> call,
                                   @NonNull Response<List<AdminDashBoardClassGraph>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(new ApiResponse<>(response.body(), true, null, response.code()));
                } else {
                    String errorMessage = "Failed to get class graph data";
                    try {
                        if (response.errorBody() != null) {
                            errorMessage = response.errorBody().string();
                        }
                    } catch (IOException e) {
                        errorMessage = "Error reading error response";
                        Log.e(TAG, "getClassGraph: " + e.getMessage());
                    }
                    liveData.setValue(new ApiResponse<>(null, false, errorMessage, response.code()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<AdminDashBoardClassGraph>> call, @NonNull Throwable t) {
                liveData.setValue(new ApiResponse<>(null, false, "Network failure. Try again.", -1));
            }
        });

        return liveData;
    }
    public LiveData<ApiResponse<AdminDashboardPresentPercent>> getPresentPercent(String auth) {
        final MutableLiveData<ApiResponse<AdminDashboardPresentPercent>> liveData = new MutableLiveData<>();

        apiRequest.getPresentPercent(auth).enqueue(new Callback<AdminDashboardPresentPercent>() {
            @Override
            public void onResponse(@NonNull Call<AdminDashboardPresentPercent> call, @NonNull Response<AdminDashboardPresentPercent> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(new ApiResponse<>(response.body(), true, null, response.code()));
                } else {
                    String errorMessage = "Failed to get present percent data";
                    try {
                        if (response.errorBody() != null) {
                            errorMessage = response.errorBody().string();
                        }
                    } catch (IOException e) {
                        errorMessage = "Error reading error response";
                        Log.e(TAG, "getPresentPercent: " + e.getMessage());
                    }
                    liveData.setValue(new ApiResponse<>(null, false, errorMessage, response.code()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<AdminDashboardPresentPercent> call, @NonNull Throwable t) {
                liveData.setValue(new ApiResponse<>(null, false, "Network failure. Try again.", -1));
            }
        });

        return liveData;
    }
    public LiveData<ApiResponse<AdminDashboardFeesInfo>> getFeesInfo(String auth) {
        final MutableLiveData<ApiResponse<AdminDashboardFeesInfo>> liveData = new MutableLiveData<>();

        apiRequest.getFeesInfo(auth).enqueue(new Callback<AdminDashboardFeesInfo>() {
            @Override
            public void onResponse(@NonNull Call<AdminDashboardFeesInfo> call, @NonNull Response<AdminDashboardFeesInfo> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(new ApiResponse<>(response.body(), true, null, response.code()));
                } else {
                    String errorMessage = "Failed to get fees info";
                    try {
                        if (response.errorBody() != null) {
                            errorMessage = response.errorBody().string();
                        }
                    } catch (IOException e) {
                        errorMessage = "Error reading error response";
                        Log.e(TAG, "getFeesInfo: " + e.getMessage());
                    }
                    liveData.setValue(new ApiResponse<>(null, false, errorMessage, response.code()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<AdminDashboardFeesInfo> call, @NonNull Throwable t) {
                liveData.setValue(new ApiResponse<>(null, false, "Network failure. Try again.", -1));
            }
        });

        return liveData;
    }
    public LiveData<ApiResponse<AdminDashboardSchoolInfo>> getSchoolInfo(String auth) {
        final MutableLiveData<ApiResponse<AdminDashboardSchoolInfo>> liveData = new MutableLiveData<>();

        apiRequest.getSchoolInfo(auth).enqueue(new Callback<AdminDashboardSchoolInfo>() {
            @Override
            public void onResponse(@NonNull Call<AdminDashboardSchoolInfo> call, @NonNull Response<AdminDashboardSchoolInfo> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(new ApiResponse<>(response.body(), true, null, response.code()));
                } else {
                    String errorMessage = "Failed to get school info";
                    try {
                        if (response.errorBody() != null) {
                            errorMessage = response.errorBody().string();
                        }
                    } catch (IOException e) {
                        errorMessage = "Error reading error response";
                        Log.e(TAG, "getSchoolInfo: " + e.getMessage());
                    }
                    liveData.setValue(new ApiResponse<>(null, false, errorMessage, response.code()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<AdminDashboardSchoolInfo> call, @NonNull Throwable t) {
                liveData.setValue(new ApiResponse<>(null, false, "Network failure. Try again.", -1));
            }
        });

        return liveData;
    }
    public LiveData<ApiResponse<AdminDashboardStudentInfo>> getStudentInfo(String auth) {
        final MutableLiveData<ApiResponse<AdminDashboardStudentInfo>> liveData = new MutableLiveData<>();

        apiRequest.getStudentInfo(auth).enqueue(new Callback<AdminDashboardStudentInfo>() {
            @Override
            public void onResponse(@NonNull Call<AdminDashboardStudentInfo> call, @NonNull Response<AdminDashboardStudentInfo> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(new ApiResponse<>(response.body(), true, null, response.code()));
                } else {
                    String errorMessage = "Failed to get student info";
                    try {
                        if (response.errorBody() != null) {
                            errorMessage = response.errorBody().string();
                        }
                    } catch (IOException e) {
                        errorMessage = "Error reading error response";
                        Log.e(TAG, "getStudentInfo: " + e.getMessage());
                    }
                    liveData.setValue(new ApiResponse<>(null, false, errorMessage, response.code()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<AdminDashboardStudentInfo> call, @NonNull Throwable t) {
                liveData.setValue(new ApiResponse<>(null, false, "Network failure. Try again.", -1));
            }
        });

        return liveData;
    }
    public LiveData<ApiResponse<AdminDashboardSubjectInfo>> getSubjectInfo(String auth) {
        final MutableLiveData<ApiResponse<AdminDashboardSubjectInfo>> liveData = new MutableLiveData<>();

        apiRequest.getSubjectInfo(auth).enqueue(new Callback<AdminDashboardSubjectInfo>() {
            @Override
            public void onResponse(@NonNull Call<AdminDashboardSubjectInfo> call, @NonNull Response<AdminDashboardSubjectInfo> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(new ApiResponse<>(response.body(), true, null, response.code()));
                } else {
                    String errorMessage = "Failed to get subject info";
                    try {
                        if (response.errorBody() != null) {
                            errorMessage = response.errorBody().string();
                        }
                    } catch (IOException e) {
                        errorMessage = "Error reading error response";
                        Log.e(TAG, "getSubjectInfo: " + e.getMessage());
                    }
                    liveData.setValue(new ApiResponse<>(null, false, errorMessage, response.code()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<AdminDashboardSubjectInfo> call, @NonNull Throwable t) {
                liveData.setValue(new ApiResponse<>(null, false, "Network failure. Try again.", -1));
            }
        });

        return liveData;
    }
    public LiveData<ApiResponse<EmployeeDashboardResponse>> getEmployeeDashboard(String auth) {
        final MutableLiveData<ApiResponse<EmployeeDashboardResponse>> liveData = new MutableLiveData<>();

        apiRequest.getEmployeeDashboard(auth).enqueue(new Callback<EmployeeDashboardResponse>() {
            @Override
            public void onResponse(@NonNull Call<EmployeeDashboardResponse> call, @NonNull Response<EmployeeDashboardResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(new ApiResponse<>(response.body(), true, null, response.code()));
                } else {
                    String errorMessage = "Failed to fetch employee dashboard data";
                    try {
                        if (response.errorBody() != null) {
                            errorMessage = response.errorBody().string();
                        }
                    } catch (IOException e) {
                        errorMessage = "Error parsing error response";
                        Log.e(TAG, "getEmployeeDashboard: " + e.getMessage());
                    }
                    liveData.setValue(new ApiResponse<>(null, false, errorMessage, response.code()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<EmployeeDashboardResponse> call, @NonNull Throwable t) {
                liveData.setValue(new ApiResponse<>(null, false, "Network failure. Try again.", -1));
            }
        });

        return liveData;
    }
    public LiveData<ApiResponse<EmployeeProfile>> getEmployeeProfile(String auth, int employeeId) {
        final MutableLiveData<ApiResponse<EmployeeProfile>> liveData = new MutableLiveData<>();

        apiRequest.getEmployeeProfile(auth, employeeId).enqueue(new Callback<EmployeeProfile>() {
            @Override
            public void onResponse(@NonNull Call<EmployeeProfile> call, @NonNull Response<EmployeeProfile> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(new ApiResponse<>(response.body(), true, null, response.code()));
                } else {
                    String errorMessage = "Failed to fetch employee profile data";
                    try {
                        if (response.errorBody() != null) {
                            errorMessage = response.errorBody().string();
                        }
                    } catch (IOException e) {
                        errorMessage = "Error parsing error response";
                        Log.e(TAG, "getEmployeeProfile: " + e.getMessage());
                    }
                    liveData.setValue(new ApiResponse<>(null, false, errorMessage, response.code()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<EmployeeProfile> call, @NonNull Throwable t) {
                liveData.setValue(new ApiResponse<>(null, false, "Network failure. Try again.", -1));
            }
        });

        return liveData;
    }


    public LiveData<ApiResponse<StudentDashboardApiResponse2>> getStudentDashboard2(String auth) {
        final MutableLiveData<ApiResponse<StudentDashboardApiResponse2>> liveData = new MutableLiveData<>();

        apiRequest.GetStudentDashboard2(auth).enqueue(new Callback<StudentDashboardApiResponse2>() {
            @Override
            public void onResponse(@NonNull Call<StudentDashboardApiResponse2> call,
                                   @NonNull Response<StudentDashboardApiResponse2> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(new ApiResponse<>(response.body(), true, null, response.code()));
                } else {
                    String errorMessage = "Failed to fetch student class test report data";
                    try {
                        if (response.errorBody() != null) {
                            errorMessage = response.errorBody().string();
                        }
                    } catch (IOException e) {
                        errorMessage = "Error parsing error response";
                        Log.e(TAG, "getStudentDashboard2: " + e.getMessage());
                    }
                    liveData.setValue(new ApiResponse<>(null, false, errorMessage, response.code()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<StudentDashboardApiResponse2> call, @NonNull Throwable t) {
                Log.e(TAG, "getStudentDashboard2 onFailure: " + t.getMessage());
                liveData.setValue(new ApiResponse<>(null, false, "Network failure. Try again.", -1));
            }
        });

        return liveData;
    }
    public LiveData<ApiResponse<StudentDashboardApiResponse3>> getStudentDashboard3(String auth) {
        final MutableLiveData<ApiResponse<StudentDashboardApiResponse3>> liveData = new MutableLiveData<>();

        apiRequest.GetStudentDashboard3(auth).enqueue(new Callback<StudentDashboardApiResponse3>() {
            @Override
            public void onResponse(@NonNull Call<StudentDashboardApiResponse3> call,
                                   @NonNull Response<StudentDashboardApiResponse3> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(new ApiResponse<>(response.body(), true, null, response.code()));
                } else {
                    String errorMessage = "Failed to fetch student dashboard data";
                    try {
                        if (response.errorBody() != null) {
                            errorMessage = response.errorBody().string();
                        }
                    } catch (IOException e) {
                        errorMessage = "Error parsing error response";
                        Log.e(TAG, "getStudentDashboard3: " + e.getMessage());
                    }
                    liveData.setValue(new ApiResponse<>(null, false, errorMessage, response.code()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<StudentDashboardApiResponse3> call, @NonNull Throwable t) {
                Log.e(TAG, "getStudentDashboard3 onFailure: " + t.getMessage());
                liveData.setValue(new ApiResponse<>(null, false, "Network failure. Try again.", -1));
            }
        });

        return liveData;
    }
    public LiveData<ApiResponse<StudentsProfileResponse>> getStudentProfile(String auth) {
        final MutableLiveData<ApiResponse<StudentsProfileResponse>> liveData = new MutableLiveData<>();

        apiRequest.getStudentProfile(auth).enqueue(new Callback<StudentsProfileResponse>() {
            @Override
            public void onResponse(@NonNull Call<StudentsProfileResponse> call,
                                   @NonNull Response<StudentsProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(new ApiResponse<>(response.body(), true, null, response.code()));
                } else {
                    String errorMessage = "Failed to fetch student dashboard data";
                    try {
                        if (response.errorBody() != null) {
                            errorMessage = response.errorBody().string();
                        }
                    } catch (IOException e) {
                        errorMessage = "Error parsing error response";
                        Log.e(TAG, "getStudentDashboard3: " + e.getMessage());
                    }
                    liveData.setValue(new ApiResponse<>(null, false, errorMessage, response.code()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<StudentsProfileResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "getStudentDashboard3 onFailure: " + t.getMessage());
                liveData.setValue(new ApiResponse<>(null, false, "Network failure. Try again.", -1));
            }
        });

        return liveData;
    }
    public LiveData<ApiResponse<StudentsTestResultResponse>> getStudentTestResult(String auth) {
        final MutableLiveData<ApiResponse<StudentsTestResultResponse>> liveData = new MutableLiveData<>();

        apiRequest.getStudentTestResult(auth).enqueue(new Callback<StudentsTestResultResponse>() {
            @Override
            public void onResponse(@NonNull Call<StudentsTestResultResponse> call,
                                   @NonNull Response<StudentsTestResultResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(new ApiResponse<>(response.body(), true, null, response.code()));
                } else {
                    String errorMessage = "Failed to fetch student dashboard data";
                    try {
                        if (response.errorBody() != null) {
                            errorMessage = response.errorBody().string();
                        }
                    } catch (IOException e) {
                        errorMessage = "Error parsing error response";
                        Log.e(TAG, "getStudentDashboard3: " + e.getMessage());
                    }
                    liveData.setValue(new ApiResponse<>(null, false, errorMessage, response.code()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<StudentsTestResultResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "getStudentDashboard3 onFailure: " + t.getMessage());
                liveData.setValue(new ApiResponse<>(null, false, "Network failure. Try again.", -1));
            }
        });

        return liveData;
    }
    public LiveData<ApiResponse<LastSubmittedFeesResponse>> getLastSubmittedFees(String auth) {
        final MutableLiveData<ApiResponse<LastSubmittedFeesResponse>> liveData = new MutableLiveData<>();

        apiRequest.getLastSubmittedFees(auth).enqueue(new Callback<LastSubmittedFeesResponse>() {
            @Override
            public void onResponse(@NonNull Call<LastSubmittedFeesResponse> call,
                                   @NonNull Response<LastSubmittedFeesResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(new ApiResponse<>(response.body(), true, null, response.code()));
                } else {
                    String errorMessage = "Failed to fetch student dashboard data";
                    try {
                        if (response.errorBody() != null) {
                            errorMessage = response.errorBody().string();
                        }
                    } catch (IOException e) {
                        errorMessage = "Error parsing error response";
                        Log.e(TAG, "getStudentDashboard3: " + e.getMessage());
                    }
                    liveData.setValue(new ApiResponse<>(null, false, errorMessage, response.code()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<LastSubmittedFeesResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "getStudentDashboard3 onFailure: " + t.getMessage());
                liveData.setValue(new ApiResponse<>(null, false, "Network failure. Try again.", -1));
            }
        });

        return liveData;
    }
    public LiveData<ApiResponse<StudentHomeworkResponse>> getStudentHomework(String auth, String date) {
        final MutableLiveData<ApiResponse<StudentHomeworkResponse>> liveData = new MutableLiveData<>();

        apiRequest.getStudentHomework(auth, date).enqueue(new Callback<StudentHomeworkResponse>() {
            @Override
            public void onResponse(@NonNull Call<StudentHomeworkResponse> call,
                                   @NonNull Response<StudentHomeworkResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(new ApiResponse<>(response.body(), true, null, response.code()));
                } else {
                    String errorMessage = "Failed to fetch student homework";
                    try {
                        if (response.errorBody() != null) {
                            errorMessage = response.errorBody().string();
                        }
                    } catch (IOException e) {
                        errorMessage = "Error parsing error response";
                        Log.e(TAG, "getStudentHomework: " + e.getMessage());
                    }
                    liveData.setValue(new ApiResponse<>(null, false, errorMessage, response.code()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<StudentHomeworkResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "getStudentHomework onFailure: " + t.getMessage());
                liveData.setValue(new ApiResponse<>(null, false, "Network failure. Try again.", -1));
            }
        });

        return liveData;
    }
    public LiveData<ApiResponse<StudentsAdmissionConfirmationResponse>> getStudentAdmissionConfirmation(String auth) {
        final MutableLiveData<ApiResponse<StudentsAdmissionConfirmationResponse>> liveData = new MutableLiveData<>();

        apiRequest.getStudentAdmissionConfirmation(auth).enqueue(new Callback<StudentsAdmissionConfirmationResponse>() {
            @Override
            public void onResponse(@NonNull Call<StudentsAdmissionConfirmationResponse> call,
                                   @NonNull Response<StudentsAdmissionConfirmationResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(new ApiResponse<>(response.body(), true, null, response.code()));
                } else {
                    String errorMessage = "Failed to fetch student dashboard data";
                    try {
                        if (response.errorBody() != null) {
                            errorMessage = response.errorBody().string();
                        }
                    } catch (IOException e) {
                        errorMessage = "Error parsing error response";
                        Log.e(TAG, "getStudentDashboard3: " + e.getMessage());
                    }
                    liveData.setValue(new ApiResponse<>(null, false, errorMessage, response.code()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<StudentsAdmissionConfirmationResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "getStudentDashboard3 onFailure: " + t.getMessage());
                liveData.setValue(new ApiResponse<>(null, false, "Network failure. Try again.", -1));
            }
        });

        return liveData;
    }
    public LiveData<ApiResponse<StudentExamListResponse>> getStudentExamList(String auth) {
        final MutableLiveData<ApiResponse<StudentExamListResponse>> liveData = new MutableLiveData<>();

        apiRequest.getStudentExamList(auth).enqueue(new Callback<StudentExamListResponse>() {
            @Override
            public void onResponse(@NonNull Call<StudentExamListResponse> call,
                                   @NonNull Response<StudentExamListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(new ApiResponse<>(response.body(), true, null, response.code()));
                } else {
                    String errorMessage = "Failed to fetch student dashboard data";
                    try {
                        if (response.errorBody() != null) {
                            errorMessage = response.errorBody().string();
                        }
                    } catch (IOException e) {
                        errorMessage = "Error parsing error response";
                        Log.e(TAG, "getStudentDashboard3: " + e.getMessage());
                    }
                    liveData.setValue(new ApiResponse<>(null, false, errorMessage, response.code()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<StudentExamListResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "getStudentDashboard3 onFailure: " + t.getMessage());
                liveData.setValue(new ApiResponse<>(null, false, "Network failure. Try again.", -1));
            }
        });

        return liveData;
    }
    public LiveData<ApiResponse<EmployeeResponse>> updateStudentPassword(String auth, String newPassword) {
        final MutableLiveData<ApiResponse<EmployeeResponse>> liveData = new MutableLiveData<>();

        apiRequest.updateStudentPassword(auth, newPassword).enqueue(new Callback<EmployeeResponse>() {
            @Override
            public void onResponse(@NonNull Call<EmployeeResponse> call,
                                   @NonNull Response<EmployeeResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(new ApiResponse<>(response.body(), true, null, response.code()));
                } else {
                    String errorMessage = "Failed to update password";
                    try {
                        if (response.errorBody() != null) {
                            errorMessage = response.errorBody().string();
                        }
                    } catch (IOException e) {
                        errorMessage = "Error parsing error response";
                        Log.e(TAG, "updateStudentPassword: " + e.getMessage());
                    }
                    liveData.setValue(new ApiResponse<>(null, false, errorMessage, response.code()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<EmployeeResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "updateStudentPassword onFailure: " + t.getMessage());
                liveData.setValue(new ApiResponse<>(null, false, "Network failure. Try again.", -1));
            }
        });

        return liveData;
    }
    public LiveData<ApiResponse<EmployeeResponse>> updateEmployeePassword(String auth, String newPassword) {
        final MutableLiveData<ApiResponse<EmployeeResponse>> liveData = new MutableLiveData<>();

        apiRequest.updateEmployeePassword(auth, newPassword).enqueue(new Callback<EmployeeResponse>() {
            @Override
            public void onResponse(@NonNull Call<EmployeeResponse> call,
                                   @NonNull Response<EmployeeResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(new ApiResponse<>(response.body(), true, null, response.code()));
                } else {
                    String errorMessage = "Failed to update password";
                    try {
                        if (response.errorBody() != null) {
                            errorMessage = response.errorBody().string();
                        }
                    } catch (IOException e) {
                        errorMessage = "Error parsing error response";
                        Log.e(TAG, "updateStudentPassword: " + e.getMessage());
                    }
                    liveData.setValue(new ApiResponse<>(null, false, errorMessage, response.code()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<EmployeeResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "updateStudentPassword onFailure: " + t.getMessage());
                liveData.setValue(new ApiResponse<>(null, false, "Network failure. Try again.", -1));
            }
        });

        return liveData;
    }
    public LiveData<ApiResponse<StudentsExamResultResponse>> getStudentExamResult(String auth,int examId) {
        final MutableLiveData<ApiResponse<StudentsExamResultResponse>> liveData = new MutableLiveData<>();

        apiRequest.getStudentExamResult(auth,examId).enqueue(new Callback<StudentsExamResultResponse>() {
            @Override
            public void onResponse(@NonNull Call<StudentsExamResultResponse> call,
                                   @NonNull Response<StudentsExamResultResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(new ApiResponse<>(response.body(), true, null, response.code()));
                } else {
                    String errorMessage = "Failed to fetch student dashboard data";
                    try {
                        if (response.errorBody() != null) {
                            errorMessage = response.errorBody().string();
                        }
                    } catch (IOException e) {
                        errorMessage = "Error parsing error response";
                        Log.e(TAG, "getStudentDashboard3: " + e.getMessage());
                    }
                    liveData.setValue(new ApiResponse<>(null, false, errorMessage, response.code()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<StudentsExamResultResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "getStudentDashboard3 onFailure: " + t.getMessage());
                liveData.setValue(new ApiResponse<>(null, false, "Network failure. Try again.", -1));
            }
        });

        return liveData;
    }
    public LiveData<ApiResponse<StudentDashboardApiResponse1>> getStudentDashboard(String auth) {
        final MutableLiveData<ApiResponse<StudentDashboardApiResponse1>> liveData = new MutableLiveData<>();

        apiRequest.GetStudentDashboard1(auth).enqueue(new Callback<StudentDashboardApiResponse1>() {
            @Override
            public void onResponse(@NonNull Call<StudentDashboardApiResponse1> call,
                                   @NonNull Response<StudentDashboardApiResponse1> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(new ApiResponse<>(response.body(), true, null, response.code()));
                } else {
                    String errorMessage = "Failed to fetch student dashboard data";
                    try {
                        if (response.errorBody() != null) {
                            errorMessage = response.errorBody().string();
                        }
                    } catch (IOException e) {
                        errorMessage = "Error parsing error response";
                        Log.e(TAG, "getStudentDashboard: " + e.getMessage());
                    }
                    liveData.setValue(new ApiResponse<>(null, false, errorMessage, response.code()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<StudentDashboardApiResponse1> call, @NonNull Throwable t) {
                Log.e(TAG, "getStudentDashboard onFailure: " + t.getMessage());
                liveData.setValue(new ApiResponse<>(null, false, "Network failure. Try again.", -1));
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
    private void handleAllEmployeesListErrorResponse2(Response<?> response, MutableLiveData<ApiResponse<ManageLoginEmployee>> liveData) {
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