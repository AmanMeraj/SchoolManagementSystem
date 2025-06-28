package com.school.schoolmanagement.HelperClasses;

import android.content.Context;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import com.school.schoolmanagement.Admin.Model.GetStudentTest;
import com.school.schoolmanagement.Admin.Model.StudentMark;
import com.school.schoolmanagement.Admin.Model.TestMarkModel;
import com.school.schoolmanagement.GlobalViewModel.ViewModel;
import com.school.schoolmanagement.Model.CreateTest;
import com.school.schoolmanagement.Model.EmployeeResponse;
import com.school.schoolmanagement.Utils.Utility;
import java.util.ArrayList;
import java.util.List;

public class TestApiHelper extends Utility {

    private ViewModel viewModel;
    private Context context;
    private String authToken;

    public TestApiHelper(Context context) {
        this.context = context;
        this.authToken = "Bearer " + pref.getPrefString(context, pref.user_token);

        if (context instanceof ViewModelStoreOwner) {
            this.viewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(ViewModel.class);
        }
    }

    // Interface for callbacks
    public interface TestApiCallback {
        void onTestMarksLoaded(GetStudentTest testData, List<TestMarkModel> mappedTestMarks);
        void onTestMarksSubmitted(EmployeeResponse response);
        void onError(String errorMessage);
        void onLoading(boolean isLoading);
    }

    /**
     * Fetch existing test marks for a class and date
     */
    public void getTestMarks(int classId, int subjectId, String testDate, TestApiCallback callback) {
        if (!isInternetConnected(context)) {
            callback.onError("No Internet Connection!");
            return;
        }

        callback.onLoading(true);

        if (context instanceof LifecycleOwner) {
            viewModel.getTestMarks(authToken, classId, subjectId, testDate)
                    .observe((LifecycleOwner) context, response -> {
                        callback.onLoading(false);

                        if (response != null && response.isSuccess && response.data != null) {
                            // Map GetStudentTest to TestMarkModel list
                            List<TestMarkModel> mappedTestMarks = mapToTestMarkModelList(response.data);
                            callback.onTestMarksLoaded(response.data, mappedTestMarks);
                        } else {
                            String errorMsg = response != null && response.message != null
                                    ? response.message
                                    : "Failed to load test marks";
                            callback.onError(errorMsg);
                        }
                    });
        }
    }

    /**
     * Submit/Create new test marks
     */
    public void submitTestMarks(int classId, int subjectId, String testDate, int totalMarks,
                                List<TestMarkModel> testMarksList, TestApiCallback callback) {
        if (!isInternetConnected(context)) {
            callback.onError("No Internet Connection!");
            return;
        }

        // Create CreateTest object for submission
        CreateTest submitData = createSubmissionData(classId, subjectId, testDate, totalMarks, testMarksList);

        callback.onLoading(true);

        if (context instanceof LifecycleOwner) {
            viewModel.postTestMarks(authToken, submitData)
                    .observe((LifecycleOwner) context, response -> {
                        callback.onLoading(false);

                        if (response != null && response.isSuccess && response.data != null) {
                            callback.onTestMarksSubmitted(response.data);
                        } else {
                            String errorMsg = response != null && response.message != null
                                    ? response.message
                                    : "Failed to submit test marks";
                            callback.onError(errorMsg);
                        }
                    });
        }
    }

    /**
     * Delete existing test
     */
    public void deleteTest(int testId, TestApiCallback callback) {
        if (!isInternetConnected(context)) {
            callback.onError("No Internet Connection!");
            return;
        }

        callback.onLoading(true);

        if (context instanceof LifecycleOwner) {
            viewModel.deleteTest(authToken, testId)
                    .observe((LifecycleOwner) context, response -> {
                        callback.onLoading(false);

                        if (response != null && response.isSuccess) {
                            callback.onTestMarksSubmitted(response.data);
                        } else {
                            String errorMsg = response != null && response.message != null
                                    ? response.message
                                    : "Failed to delete test";
                            callback.onError(errorMsg);
                        }
                    });
        }
    }

    /**
     * Validate test ID for deletion
     */
    public boolean validateTestId(int testId) {
        return testId > 0;
    }

    /**
     * Update existing test marks
     */
    public void updateTestMarks(int classId, int subjectId, String testDate, int totalMarks,
                                List<TestMarkModel> updatedTestMarksList, TestApiCallback callback) {
        if (!isInternetConnected(context)) {
            callback.onError("No Internet Connection!");
            return;
        }

        // Create CreateTest object for update submission
        CreateTest updateData = createSubmissionData(classId, subjectId, testDate, totalMarks, updatedTestMarksList);

        callback.onLoading(true);

        if (context instanceof LifecycleOwner) {
            viewModel.updateTestMarks(authToken, updateData)
                    .observe((LifecycleOwner) context, response -> {
                        callback.onLoading(false);

                        if (response != null && response.isSuccess && response.data != null) {
                            callback.onTestMarksSubmitted(response.data);
                        } else {
                            String errorMsg = response != null && response.message != null
                                    ? response.message
                                    : "Failed to update test marks";
                            callback.onError(errorMsg);
                        }
                    });
        }
    }

    /**
     * Map GetStudentTest response to TestMarkModel list
     */
    private List<TestMarkModel> mapToTestMarkModelList(GetStudentTest testData) {
        List<TestMarkModel> testMarksList = new ArrayList<>();

        if (testData.getData() != null && testData.getData().getStudentMarks() != null) {
            for (StudentMark studentMark : testData.getData().getStudentMarks()) {
                TestMarkModel testMarkModel = new TestMarkModel();
                testMarkModel.setStudentId(studentMark.getStudentId());
                testMarkModel.setStudentName(studentMark.getStudentName());
                testMarkModel.setObtainedMarks(studentMark.getObtainedMarks());
                testMarkModel.setTotal(testData.getData().getTotalMarks());

                testMarksList.add(testMarkModel);
            }
        }

        return testMarksList;
    }

    /**
     * Create CreateTest object for submission (Fixed to use proper CreateTest model)
     */
    private CreateTest createSubmissionData(int classId, int subjectId, String testDate,
                                            int totalMarks, List<TestMarkModel> testMarksList) {
        CreateTest submitData = new CreateTest();

        // Set class details
        submitData.setClassId(classId);
        submitData.setSubjectId(subjectId);
        submitData.setTestDate(testDate);
        submitData.setTotalMarks(totalMarks);

        // Create student marks list
        ArrayList<StudentMark> studentMarks = new ArrayList<>();
        for (TestMarkModel testMark : testMarksList) {
            StudentMark studentMark = new StudentMark();
            studentMark.setStudentId(testMark.getStudentId());
            studentMark.setStudentName(testMark.getStudentName());
            studentMark.setObtainedMarks(testMark.getObtainedMarks());
            // Set total marks in each StudentMark if needed
            studentMark.setTotalMarks(totalMarks);

            studentMarks.add(studentMark);
        }

        submitData.setStudentMarks(studentMarks);

        return submitData;
    }

    /**
     * Validate test marks before submission
     */
    public boolean validateTestMarks(List<TestMarkModel> testMarksList, int totalMarks) {
        if (testMarksList == null || testMarksList.isEmpty()) {
            return false;
        }

        for (TestMarkModel testMark : testMarksList) {
            if (testMark.getObtainedMarks() < 0 || testMark.getObtainedMarks() > totalMarks) {
                return false;
            }

            // Validate student ID and name
            if (testMark.getStudentId() <= 0 || testMark.getStudentName() == null || testMark.getStudentName().trim().isEmpty()) {
                return false;
            }
        }

        return true;
    }

    /**
     * Validate required parameters for test submission
     */
    public boolean validateTestParameters(int classId, int subjectId, String testDate, int totalMarks) {
        return classId > 0 &&
                subjectId > 0 &&
                testDate != null && !testDate.trim().isEmpty() &&
                totalMarks > 0;
    }

    /**
     * Get test marks summary
     */
    public String getTestMarksSummary(List<TestMarkModel> testMarksList, int totalMarks) {
        if (testMarksList == null || testMarksList.isEmpty()) {
            return "No test marks available";
        }

        int totalStudents = testMarksList.size();
        int passedStudents = 0;
        int totalObtainedMarks = 0;
        int passingMarks = (int) (totalMarks * 0.4); // Assuming 40% is passing

        for (TestMarkModel testMark : testMarksList) {
            totalObtainedMarks += testMark.getObtainedMarks();
            if (testMark.getObtainedMarks() >= passingMarks) {
                passedStudents++;
            }
        }

        double averageMarks = (double) totalObtainedMarks / totalStudents;
        double passPercentage = ((double) passedStudents / totalStudents) * 100;

        return String.format("Students: %d | Passed: %d (%.1f%%) | Average: %.1f/%d",
                totalStudents, passedStudents, passPercentage, averageMarks, totalMarks);
    }

    /**
     * Get highest score from test marks
     */
    public int getHighestScore(List<TestMarkModel> testMarksList) {
        if (testMarksList == null || testMarksList.isEmpty()) {
            return 0;
        }

        int highest = 0;
        for (TestMarkModel testMark : testMarksList) {
            if (testMark.getObtainedMarks() > highest) {
                highest = testMark.getObtainedMarks();
            }
        }
        return highest;
    }

    /**
     * Get lowest score from test marks
     */
    public int getLowestScore(List<TestMarkModel> testMarksList) {
        if (testMarksList == null || testMarksList.isEmpty()) {
            return 0;
        }

        int lowest = Integer.MAX_VALUE;
        for (TestMarkModel testMark : testMarksList) {
            if (testMark.getObtainedMarks() < lowest) {
                lowest = testMark.getObtainedMarks();
            }
        }
        return lowest == Integer.MAX_VALUE ? 0 : lowest;
    }
}