package com.school.schoolmanagement.HelperClasses;
import android.content.Context;
import android.util.Log;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.google.gson.Gson;
import com.school.schoolmanagement.Admin.Model.StudentInsertMarks;
import com.school.schoolmanagement.Admin.Model.StudentMarksRequest;
import com.school.schoolmanagement.Admin.Model.Subject;
import com.school.schoolmanagement.GlobalRepository.GlobalRepository;
import com.school.schoolmanagement.GlobalViewModel.ViewModel;
import com.school.schoolmanagement.Utils.Utility;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import okhttp3.RequestBody;
import retrofit2.Call;

public class StudentMarksApiHelper extends Utility {

    private static final String TAG = "StudentMarksApiHelper";
    private ViewModel viewModel;
    private Context context;

    public StudentMarksApiHelper(Context context) {
        this.context = context;
        if (context instanceof ViewModelStoreOwner) {
            this.viewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(ViewModel.class);
        }
    }

    // Data class to hold parsed response
    public static class StudentMarksResponse {
        private List<StudentInsertMarks> students;
        private List<Subject> subjects;
        private boolean success;
        private String message;

        public StudentMarksResponse(List<StudentInsertMarks> students, List<Subject> subjects, boolean success, String message) {
            this.students = students;
            this.subjects = subjects;
            this.success = success;
            this.message = message;
        }

        // Getters
        public List<StudentInsertMarks> getStudents() { return students; }
        public List<Subject> getSubjects() { return subjects; }
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
    }

    // Interface for callback
    public interface StudentMarksCallback {
        void onSuccess(StudentMarksResponse response);
        void onError(String errorMessage);
        void onLoading(boolean isLoading);
    }

    // Interface for save marks callback
    public interface SaveMarksCallback {
        void onSuccess(String message);
        void onError(String errorMessage);
        void onLoading(boolean isLoading);
    }

    // Method to fetch student marks for entire class
    public void fetchClassMarks(String examId, String classId, StudentMarksCallback callback) {
        if (!isInternetConnected(context)) {
            callback.onError("No Internet Connection!");
            return;
        }

        // Validate input parameters
        if (examId == null || examId.trim().isEmpty()) {
            callback.onError("Exam ID is required");
            return;
        }

        if (classId == null || classId.trim().isEmpty()) {
            callback.onError("Class ID is required");
            return;
        }

        // Validate that the IDs are numeric
        int parsedExamId;
        int parsedClassId;

        try {
            parsedExamId = Integer.parseInt(examId.trim());
        } catch (NumberFormatException e) {
            callback.onError("Invalid Exam ID format");
            return;
        }

        try {
            parsedClassId = Integer.parseInt(classId.trim());
        } catch (NumberFormatException e) {
            callback.onError("Invalid Class ID format");
            return;
        }

        callback.onLoading(true);
        String auth = "Bearer " + pref.getPrefString(context, pref.user_token);

        if (context instanceof LifecycleOwner) {
            // Use the validated parsed integers
            viewModel.getClassMarks(auth, parsedExamId, parsedClassId).observe((LifecycleOwner) context, response -> {
                callback.onLoading(false);
                if (response != null) {
                    // Add logging to see what we're receiving
                    Log.d(TAG, "Raw API Response: " + response.toString());

                    // Extract JSON string from ApiResponse object
                    String jsonResponse = extractJsonFromApiResponse(response);
                    if (jsonResponse != null) {
                        StudentMarksResponse parsedResponse = parseApiResponse(jsonResponse);
                        if (parsedResponse.isSuccess()) {
                            callback.onSuccess(parsedResponse);
                        } else {
                            callback.onError(parsedResponse.getMessage());
                        }
                    } else {
                        callback.onError("Failed to extract response data");
                    }
                } else {
                    callback.onError("Failed to fetch student marks");
                }
            });
        }
    }

    // Method to fetch marks for specific student
    public void fetchStudentMarks(String examId, String classId, String registrationNumber, StudentMarksCallback callback) {
        if (!isInternetConnected(context)) {
            callback.onError("No Internet Connection!");
            return;
        }

        // Validate input parameters
        if (examId == null || examId.trim().isEmpty()) {
            callback.onError("Exam ID is required");
            return;
        }

        if (classId == null || classId.trim().isEmpty()) {
            callback.onError("Class ID is required");
            return;
        }

        if (registrationNumber == null || registrationNumber.trim().isEmpty()) {
            callback.onError("Registration Number is required");
            return;
        }

        // Validate that the IDs are numeric
        int parsedExamId;
        int parsedClassId;
        int parsedRegistrationNumber;

        try {
            parsedExamId = Integer.parseInt(examId.trim());
        } catch (NumberFormatException e) {
            callback.onError("Invalid Exam ID format");
            return;
        }

        try {
            parsedClassId = Integer.parseInt(classId.trim());
        } catch (NumberFormatException e) {
            callback.onError("Invalid Class ID format");
            return;
        }

        try {
            parsedRegistrationNumber = Integer.parseInt(registrationNumber.trim());
        } catch (NumberFormatException e) {
            callback.onError("Invalid Registration Number format");
            return;
        }

        callback.onLoading(true);
        String auth = "Bearer " + pref.getPrefString(context, pref.user_token);

        if (context instanceof LifecycleOwner) {
            // Use the validated parsed integers
            viewModel.getStudentMarks(auth, parsedExamId, parsedClassId, parsedRegistrationNumber).observe((LifecycleOwner) context, response -> {
                callback.onLoading(false);
                if (response != null) {
                    // Add logging to see what we're receiving
                    Log.d(TAG, "Raw API Response: " + response.toString());

                    // Extract JSON string from ApiResponse object
                    String jsonResponse = extractJsonFromApiResponse(response);
                    if (jsonResponse != null) {
                        StudentMarksResponse parsedResponse = parseApiResponse(jsonResponse);
                        if (parsedResponse.isSuccess()) {
                            callback.onSuccess(parsedResponse);
                        } else {
                            callback.onError(parsedResponse.getMessage());
                        }
                    } else {
                        callback.onError("Failed to extract response data");
                    }
                } else {
                    callback.onError("Failed to fetch student marks");
                }
            });
        }
    }

    // Method to extract JSON string from ApiResponse object
    private String extractJsonFromApiResponse(Object apiResponse) {
        try {
            Class<?> responseClass = apiResponse.getClass();

            // Get the 'data' field which contains the actual response
            java.lang.reflect.Field dataField = responseClass.getDeclaredField("data");
            dataField.setAccessible(true);
            Object dataValue = dataField.get(apiResponse);

            if (dataValue != null) {
                Log.d(TAG, "Data field type: " + dataValue.getClass().getName());
                String jsonString = dataValue.toString();
                Log.d(TAG, "Extracted data field content: " + jsonString);

                // Check if it looks like JSON
                if (jsonString.trim().startsWith("{") || jsonString.trim().startsWith("[")) {
                    return jsonString;
                } else {
                    Log.w(TAG, "Data field doesn't contain JSON format, trying to convert to JSON");

                    // If data is already parsed (e.g., as Map or custom object), try to convert it to JSON
                    try {
                        // Using Gson to convert object to JSON (if you have Gson in your project)
                        com.google.gson.Gson gson = new com.google.gson.Gson();
                        String convertedJson = gson.toJson(dataValue);
                        Log.d(TAG, "Converted to JSON: " + convertedJson);
                        return convertedJson;
                    } catch (Exception gsonException) {
                        Log.e(TAG, "Failed to convert data to JSON using Gson: " + gsonException.getMessage());
                    }
                }
            } else {
                Log.w(TAG, "Data field is null");
            }

            // Fallback: Also check other fields for debugging
            java.lang.reflect.Field codeField = responseClass.getDeclaredField("code");
            codeField.setAccessible(true);
            int code = codeField.getInt(apiResponse);

            java.lang.reflect.Field messageField = responseClass.getDeclaredField("message");
            messageField.setAccessible(true);
            String message = (String) messageField.get(apiResponse);

            java.lang.reflect.Field successField = responseClass.getDeclaredField("isSuccess");
            successField.setAccessible(true);
            boolean isSuccess = successField.getBoolean(apiResponse);

            Log.d(TAG, "ApiResponse details - Code: " + code + ", Message: " + message + ", Success: " + isSuccess);

        } catch (Exception e) {
            Log.e(TAG, "Error extracting JSON from ApiResponse: " + e.getMessage(), e);
        }

        return null;
    }

    // Enhanced method to parse API response with better error handling
    private StudentMarksResponse parseApiResponse(String jsonResponse) {
        Log.d(TAG, "Starting to parse response: " + jsonResponse);

        try {
            JSONObject response = new JSONObject(jsonResponse);
            Log.d(TAG, "JSON Object created successfully");

            // Check if status exists and is integer
            if (!response.has("status")) {
                Log.e(TAG, "Response missing 'status' field");
                return new StudentMarksResponse(new ArrayList<>(), new ArrayList<>(), false, "Invalid response format - missing status");
            }

            int status = response.getInt("status");
            Log.d(TAG, "Status: " + status);

            if (status == 1) {
                // Check if data array exists
                if (!response.has("data")) {
                    Log.e(TAG, "Response missing 'data' field");
                    return new StudentMarksResponse(new ArrayList<>(), new ArrayList<>(), false, "Invalid response format - missing data");
                }

                JSONArray dataArray = response.getJSONArray("data");
                Log.d(TAG, "Data array length: " + dataArray.length());

                // Initialize lists
                List<StudentInsertMarks> students = new ArrayList<>();
                Set<String> uniqueSubjects = new HashSet<>();

                // Process each student
                for (int i = 0; i < dataArray.length(); i++) {
                    JSONObject studentData = dataArray.getJSONObject(i);
                    Log.d(TAG, "Processing student " + i + ": " + studentData.toString());

                    // Safely get student ID and name
                    if (!studentData.has("studentId") || !studentData.has("studentName")) {
                        Log.w(TAG, "Student data missing required fields at index " + i);
                        continue;
                    }

                    String studentId = String.valueOf(studentData.getInt("studentId"));
                    String studentName = studentData.getString("studentName");
                    Log.d(TAG, "Student: " + studentName + " (ID: " + studentId + ")");

                    // Create student object
                    StudentInsertMarks student = new StudentInsertMarks(studentId, studentName, "");

                    // Check if subjectMarks exists
                    if (studentData.has("subjectMarks")) {
                        JSONArray subjectMarks = studentData.getJSONArray("subjectMarks");
                        Log.d(TAG, "Subject marks count for " + studentName + ": " + subjectMarks.length());

                        for (int j = 0; j < subjectMarks.length(); j++) {
                            JSONObject subjectMark = subjectMarks.getJSONObject(j);

                            if (subjectMark.has("subject") && subjectMark.has("marks")) {
                                String subject = subjectMark.getString("subject");
                                double marks = subjectMark.getDouble("marks");

                                Log.d(TAG, "Subject: " + subject + ", Marks: " + marks);

                                // Add subject to unique subjects set
                                uniqueSubjects.add(subject);

                                // Store marks in student object
                                student.setSubjectMark(subject, String.valueOf(marks));
                            } else {
                                Log.w(TAG, "Subject mark missing required fields at index " + j);
                            }
                        }
                    } else {
                        Log.w(TAG, "No subjectMarks found for student: " + studentName);
                    }

                    students.add(student);
                }

                // Create subjects list for header
                List<Subject> subjects = new ArrayList<>();
                int subjectIndex = 1;
                for (String subjectName : uniqueSubjects) {
                    subjects.add(new Subject("SUB" + subjectIndex, subjectName));
                    subjectIndex++;
                }

                Log.d(TAG, "Successfully parsed " + students.size() + " students and " + subjects.size() + " subjects");

                String message = response.has("message") ? response.getString("message") : "Success";
                return new StudentMarksResponse(students, subjects, true, message);

            } else {
                String errorMessage = response.has("message") ? response.getString("message") : "Unknown error";
                Log.e(TAG, "API Error (Status " + status + "): " + errorMessage);
                return new StudentMarksResponse(new ArrayList<>(), new ArrayList<>(), false, errorMessage);
            }

        } catch (JSONException e) {
            Log.e(TAG, "JSON Parsing Error: " + e.getMessage(), e);
            return new StudentMarksResponse(new ArrayList<>(), new ArrayList<>(), false, "Failed to parse response: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Unexpected error during parsing: " + e.getMessage(), e);
            return new StudentMarksResponse(new ArrayList<>(), new ArrayList<>(), false, "Unexpected error: " + e.getMessage());
        }
    }

    // Private method to create save marks request
    private JSONObject createSaveMarksRequest(String examId, String classId, List<StudentInsertMarks> students) {
        try {
            JSONObject requestBody = new JSONObject();
            requestBody.put("examId", examId);
            requestBody.put("classId", classId);

            JSONArray studentsArray = new JSONArray();

            for (StudentInsertMarks student : students) {
                JSONObject studentObj = new JSONObject();
                studentObj.put("studentId", student.getId());

                JSONArray marksArray = new JSONArray();
                HashMap<String, String> allMarks = student.getAllSubjects();

                for (Map.Entry<String, String> entry : allMarks.entrySet()) {
                    if (!entry.getValue().isEmpty()) {
                        JSONObject markObj = new JSONObject();
                        markObj.put("subject", entry.getKey());
                        markObj.put("marks", Double.parseDouble(entry.getValue()));
                        marksArray.put(markObj);
                    }
                }

                studentObj.put("subjectMarks", marksArray);
                studentsArray.put(studentObj);
            }

            requestBody.put("students", studentsArray);
            return requestBody;

        } catch (JSONException e) {
            Log.e(TAG, "Error creating save marks request: " + e.getMessage());
            return new JSONObject();
        }
    }

    // Private method to create individual mark request
    private JSONObject createIndividualMarkRequest(String examId, String studentId, String subjectId, String marks) {
        try {
            JSONObject requestBody = new JSONObject();
            requestBody.put("examId", examId);
            requestBody.put("studentId", studentId);
            requestBody.put("subjectId", subjectId);
            requestBody.put("marks", Double.parseDouble(marks));
            return requestBody;
        } catch (JSONException e) {
            Log.e(TAG, "Error creating individual mark request: " + e.getMessage());
            return new JSONObject();
        }
    }

    // Utility method to validate marks input
    public boolean isValidMark(String mark) {
        if (mark == null || mark.trim().isEmpty()) {
            return true; // Empty marks are allowed
        }

        try {
            double markValue = Double.parseDouble(mark.trim());
            return markValue >= 0 && markValue <= 100; // Assuming marks are between 0-100
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Utility method to calculate total marks for a student
    public double calculateTotalMarks(StudentInsertMarks student) {
        double total = 0;
        HashMap<String, String> allMarks = student.getAllSubjects();

        for (String mark : allMarks.values()) {
            if (!mark.isEmpty()) {
                try {
                    total += Double.parseDouble(mark);
                } catch (NumberFormatException e) {
                    Log.w(TAG, "Invalid mark format: " + mark);
                }
            }
        }

        return total;
    }

    // Utility method to calculate percentage
    public double calculatePercentage(StudentInsertMarks student, int totalSubjects) {
        double totalMarks = calculateTotalMarks(student);
        double maxMarks = totalSubjects * 100; // Assuming each subject is out of 100
        return (totalMarks / maxMarks) * 100;
    }

    // Utility method to get grade based on percentage
    public String getGrade(double percentage) {
        if (percentage >= 90) return "A+";
        else if (percentage >= 80) return "A";
        else if (percentage >= 70) return "B+";
        else if (percentage >= 60) return "B";
        else if (percentage >= 50) return "C";
        else if (percentage >= 40) return "D";
        else return "F";
    }
    // Redesigned saveStudentMarks method using ViewModel and Repository pattern
    public void saveStudentMarks(StudentMarksRequest request, SaveMarksCallback callback) {
        // Validate internet connection
        if (!isInternetConnected(context)) {
            callback.onError("No Internet Connection!");
            return;
        }

        // Validate request object
        if (request == null) {
            callback.onError("Invalid request data");
            return;
        }

        // Validate that context is a LifecycleOwner to use ViewModel
        if (!(context instanceof LifecycleOwner)) {
            callback.onError("Context must implement LifecycleOwner to save marks");
            return;
        }

        try {
            callback.onLoading(true);

            // Get auth token
            String auth = "Bearer " + pref.getPrefString(context, pref.user_token);

            Log.d(TAG, "Saving student marks with request: " + new Gson().toJson(request));

            // Use ViewModel to make the API call
            viewModel.addStudentMarks(auth, request).observe((LifecycleOwner) context, apiResponse -> {
                callback.onLoading(false);

                if (apiResponse != null) {
                    Log.d(TAG, "Save marks API response received - Success: " + apiResponse.isSuccess +
                            ", Code: " + apiResponse.code + ", Message: " + apiResponse.message);

                    if (apiResponse.isSuccess && apiResponse.data != null) {
                        // Successfully saved marks
                        String successMessage = "Marks saved successfully";

                        // Try to get message from response data if available
                        try {
                            // Assuming EmployeeResponse has a getMessage() method
                            // Adjust this based on your EmployeeResponse structure
                            if (apiResponse.data.getMessage() != null &&
                                    !apiResponse.data.getMessage().isEmpty()) {
                                successMessage = apiResponse.data.getMessage();
                            }
                        } catch (Exception e) {
                            Log.w(TAG, "Could not extract message from response data: " + e.getMessage());
                        }

                        callback.onSuccess(successMessage);

                    } else {
                        // Handle API error
                        String errorMessage = "Failed to save marks";

                        if (apiResponse.message != null && !apiResponse.message.isEmpty()) {
                            errorMessage = apiResponse.message;
                        }

                        Log.e(TAG, "API Error - " + errorMessage + " (Code: " + apiResponse.code + ")");
                        callback.onError(errorMessage);
                    }
                } else {
                    // Handle null response
                    Log.e(TAG, "Received null response from API");
                    callback.onError("Failed to save marks - no response received");
                }
            });

        } catch (Exception e) {
            callback.onLoading(false);
            Log.e(TAG, "Error in saveStudentMarks: " + e.getMessage(), e);
            callback.onError("Error preparing request: " + e.getMessage());
        }
    }

    // Optional: Helper method to validate StudentMarksRequest before sending
    private boolean isValidStudentMarksRequest(StudentMarksRequest request) {
        if (request == null) {
            Log.e(TAG, "StudentMarksRequest is null");
            return false;
        }

        // Add validation based on your StudentMarksRequest structure
        // Example validations:
        try {
            if (request.getExamId() == -1) {
                Log.e(TAG, "Exam ID is missing in request");
                return false;
            }

            if (request.getClassId() == -1 ) {
                Log.e(TAG, "Class ID is missing in request");
                return false;
            }

            // Add more validations based on your StudentMarksRequest fields
            // if (request.getStudents() == null || request.getStudents().isEmpty()) {
            //     Log.e(TAG, "No students data in request");
            //     return false;
            // }

            return true;

        } catch (Exception e) {
            Log.e(TAG, "Error validating StudentMarksRequest: " + e.getMessage());
            return false;
        }
    }

    // Enhanced version with validation
    public void saveStudentMarksWithValidation(StudentMarksRequest request, SaveMarksCallback callback) {
        // Validate internet connection
        if (!isInternetConnected(context)) {
            callback.onError("No Internet Connection!");
            return;
        }

        // Validate request
        if (!isValidStudentMarksRequest(request)) {
            callback.onError("Invalid request data provided");
            return;
        }

        // Validate that context is a LifecycleOwner
        if (!(context instanceof LifecycleOwner)) {
            callback.onError("Context must implement LifecycleOwner to save marks");
            return;
        }

        try {
            callback.onLoading(true);

            String auth = "Bearer " + pref.getPrefString(context, pref.user_token);

            Log.d(TAG, "Saving validated student marks");

            viewModel.addStudentMarks(auth, request).observe((LifecycleOwner) context, apiResponse -> {
                callback.onLoading(false);

                if (apiResponse != null) {
                    if (apiResponse.isSuccess && apiResponse.data != null) {
                        String successMessage = "Marks saved successfully";

                        // Extract success message if available
                        try {
                            if (apiResponse.data.getMessage() != null) {
                                successMessage = apiResponse.data.getMessage();
                            }
                        } catch (Exception e) {
                            Log.w(TAG, "Could not extract success message: " + e.getMessage());
                        }

                        callback.onSuccess(successMessage);

                    } else {
                        assert apiResponse.data != null;
                        String errorMessage = apiResponse.data.getMessage() != null ?
                                apiResponse.data.getMessage() : "Failed to save marks";
                        callback.onError(errorMessage);
                    }
                } else {
                    callback.onError("Failed to save marks - no response received");
                }
            });

        } catch (Exception e) {
            callback.onLoading(false);
            Log.e(TAG, "Error in saveStudentMarksWithValidation: " + e.getMessage(), e);
            callback.onError("Error: " + e.getMessage());
        }
    }


}