package com.school.schoolmanagement.HelperClasses;

import android.content.Context;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import com.school.schoolmanagement.Admin.Model.ExamModel;
import com.school.schoolmanagement.GlobalViewModel.ViewModel;
import com.school.schoolmanagement.Model.StudentExamListResponse;
import com.school.schoolmanagement.Utils.Utility;
import java.util.ArrayList;

public class ExamApiHelper extends Utility {

    private ViewModel viewModel;
    private Context context;

    public ExamApiHelper(Context context) {
        this.context = context;
        if (context instanceof ViewModelStoreOwner) {
            this.viewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(ViewModel.class);
        }
    }

    // Interface for callback
    public interface ExamListCallback {
        void onExamSuccess(ArrayList<ExamModel.Datum> examList);
        void onError(String errorMessage);
        void onLoading(boolean isLoading);
    }
    public interface StudentExamListCallback {
        void onStudentExamSuccess(ArrayList<StudentExamListResponse.Datum> examList);
        void onStudentError(String errorMessage);
        void onStudentLoading(boolean isLoading);
    }

    // Interface for single exam callback
    public interface SingleExamCallback {
        void onSuccess(ExamModel.Datum exam);
        void onError(String errorMessage);
        void onLoading(boolean isLoading);
    }

    // Interface for checking if exams exist
    public interface ExamExistsCallback {
        void onResult(boolean hasExams, int examCount);
        void onError(String errorMessage);
        void onLoading(boolean isLoading);
    }

    // Method to fetch all exams - FIXED
    public void fetchAllExams(ExamListCallback callback) {
        if (!isInternetConnected(context)) {
            callback.onError("No Internet Connection!");
            return;
        }

        callback.onLoading(true);
        String auth = "Bearer " + pref.getPrefString(context, pref.user_token);

        if (context instanceof LifecycleOwner) {
            viewModel.getAllExams(auth).observe((LifecycleOwner) context, response -> {
                callback.onLoading(false);
                if (response != null && response.code == 200) { // Check status code
                    if (response.data != null && !response.data.data.isEmpty()) {
                        // Return the actual ArrayList<Datum> from response
                        callback.onExamSuccess(response.data.data);
                    } else {
                        callback.onError("No exams found");
                    }
                } else {
                    String errorMessage = response != null && response.message != null
                            ? response.message
                            : "Failed to fetch exams";
                    callback.onError(errorMessage);
                }
            });
        } else {
            callback.onLoading(false);
            callback.onError("Context is not a LifecycleOwner");
        }
    }
    public void fetchStudentExams(StudentExamListCallback callback) {
        if (!isInternetConnected(context)) {
            callback.onStudentError("No Internet Connection!");
            return;
        }

        callback.onStudentLoading(true);
        String auth = "Bearer " + pref.getPrefString(context, pref.user_token);

        if (context instanceof LifecycleOwner) {
            viewModel.getStudentExamList(auth).observe((LifecycleOwner) context, response -> {
                callback.onStudentLoading(false);
                if (response != null && response.code == 200) { // Check status code
                    if (response.data != null && !response.data.data.isEmpty()) {
                        // Return the actual ArrayList<Datum> from response
                        callback.onStudentExamSuccess(response.data.data);
                    } else {
                        callback.onStudentError("No exams found");
                    }
                } else {
                    String errorMessage = response != null && response.message != null
                            ? response.message
                            : "Failed to fetch exams";
                    callback.onStudentError(errorMessage);
                }
            });
        } else {
            callback.onStudentLoading(false);
            callback.onStudentError("Context is not a LifecycleOwner");
        }
    }

    // Method to fetch only valid exams (with complete data)
    public void fetchValidExams(ExamListCallback callback) {
        fetchAllExams(new ExamListCallback() {
            @Override
            public void onExamSuccess(ArrayList<ExamModel.Datum> examList) {
                ArrayList<ExamModel.Datum> validExams = new ArrayList<>();
                if (examList != null) {
                    for (ExamModel.Datum exam : examList) {
                        if (exam != null && exam.isValidExam()) {
                            validExams.add(exam);
                        }
                    }
                }
                callback.onExamSuccess(validExams);
            }

            @Override
            public void onError(String errorMessage) {
                callback.onError(errorMessage);
            }

            @Override
            public void onLoading(boolean isLoading) {
                callback.onLoading(isLoading);
            }
        });
    }

    // Method to get exam by ID
    public void fetchExamById(int examId, SingleExamCallback callback) {
        fetchAllExams(new ExamListCallback() {
            @Override
            public void onExamSuccess(ArrayList<ExamModel.Datum> examList) {
                if (examList != null) {
                    for (ExamModel.Datum exam : examList) {
                        if (exam != null && exam.getExamId() == examId) {
                            callback.onSuccess(exam);
                            return;
                        }
                    }
                }
                callback.onError("Exam with ID " + examId + " not found");
            }

            @Override
            public void onError(String errorMessage) {
                callback.onError(errorMessage);
            }

            @Override
            public void onLoading(boolean isLoading) {
                callback.onLoading(isLoading);
            }
        });
    }

    // Method to get exam names for spinner/dropdown
    public ArrayList<String> getExamNames(ArrayList<ExamModel.Datum> examList) {
        ArrayList<String> examNames = new ArrayList<>();
        examNames.add("Select Exam"); // Default option

        if (examList != null) {
            for (ExamModel.Datum exam : examList) {
                if (exam != null) {
                    examNames.add(exam.getDisplayName());
                }
            }
        }

        return examNames;
    }

    // Method to get exam names for spinner/dropdown (only valid exams)
    public ArrayList<String> getValidExamNames(ArrayList<ExamModel.Datum> examList) {
        ArrayList<String> examNames = new ArrayList<>();
        examNames.add("Select Exam"); // Default option

        if (examList != null) {
            for (ExamModel.Datum exam : examList) {
                if (exam != null && exam.isValidExam()) {
                    examNames.add(exam.getDisplayName());
                }
            }
        }

        return examNames;
    }  public ArrayList<String> getValidStudentsExamNames(ArrayList<StudentExamListResponse.Datum> examList) {
        ArrayList<String> examNames = new ArrayList<>();
        examNames.add("Select Exam"); // Default option

        if (examList != null) {
            for (StudentExamListResponse.Datum exam : examList) {
                if (exam != null ) {
                    examNames.add(exam.getExaminationName());
                }
            }
        }

        return examNames;
    }

    // Method to get exam ID by position (considering first item is "Select Exam")
    public int getExamIdByPosition(ArrayList<ExamModel.Datum> examList, int position) {
        if (examList != null && position > 0 && position <= examList.size()) {
            ExamModel.Datum exam = examList.get(position - 1);
            return exam != null ? exam.getExamId() : -1;
        }
        return -1; // Invalid selection
    }

    // Method to get exam ID by position for valid exams only
    public int getValidExamIdByPosition(ArrayList<ExamModel.Datum> examList, int position) {
        if (examList != null && position > 0) {
            ArrayList<ExamModel.Datum> validExams = new ArrayList<>();
            for (ExamModel.Datum exam : examList) {
                if (exam != null && exam.isValidExam()) {
                    validExams.add(exam);
                }
            }
            if (position <= validExams.size()) {
                ExamModel.Datum exam = validExams.get(position - 1);
                return exam != null ? exam.getExamId() : -1;
            }
        }
        return -1; // Invalid selection
    }
    public int getValidStudentExamIdByPosition(ArrayList<StudentExamListResponse.Datum> examList, int position) {
        if (examList != null && position > 0) {
            ArrayList<StudentExamListResponse.Datum> validExams = new ArrayList<>();
            for (StudentExamListResponse.Datum exam : examList) {
                if (exam != null) {
                    validExams.add(exam);
                }
            }
            if (position <= validExams.size()) {
                StudentExamListResponse.Datum exam = validExams.get(position - 1);
                return exam != null ? exam.getExamId() : -1;
            }
        }
        return -1; // Invalid selection
    }

    // Method to get exam data by ID
    public ExamModel.Datum getExamById(ArrayList<ExamModel.Datum> examList, int examId) {
        if (examList != null) {
            for (ExamModel.Datum exam : examList) {
                if (exam != null && exam.getExamId() == examId) {
                    return exam;
                }
            }
        }
        return null;
    }
    public StudentExamListResponse.Datum getStudentExamById(ArrayList<StudentExamListResponse.Datum> examList, int examId) {
        if (examList != null) {
            for (StudentExamListResponse.Datum exam : examList) {
                if (exam != null && exam.getExamId() == examId) {
                    return exam;
                }
            }
        }
        return null;
    }

    // Method to find position by exam ID (useful for pre-selection)
    public int getPositionByExamId(ArrayList<ExamModel.Datum> examList, int examId) {
        if (examList != null) {
            for (int i = 0; i < examList.size(); i++) {
                ExamModel.Datum exam = examList.get(i);
                if (exam != null && exam.getExamId() == examId) {
                    return i + 1; // +1 because first item is "Select Exam"
                }
            }
        }
        return 0; // Return to "Select Exam" if not found
    }

    // Method to find position by exam ID for valid exams only
    public int getValidExamPositionByExamId(ArrayList<ExamModel.Datum> examList, int examId) {
        if (examList != null) {
            int position = 1; // Start from 1 because 0 is "Select Exam"
            for (ExamModel.Datum exam : examList) {
                if (exam != null && exam.isValidExam()) {
                    if (exam.getExamId() == examId) {
                        return position;
                    }
                    position++;
                }
            }
        }
        return 0; // Return to "Select Exam" if not found
    }

    // Method to check if exams exist
    public void checkExamsExist(ExamExistsCallback callback) {
        fetchAllExams(new ExamListCallback() {
            @Override
            public void onExamSuccess(ArrayList<ExamModel.Datum> examList) {
                boolean hasExams = examList != null && !examList.isEmpty();
                int examCount = hasExams ? examList.size() : 0;
                callback.onResult(hasExams, examCount);
            }

            @Override
            public void onError(String errorMessage) {
                callback.onError(errorMessage);
            }

            @Override
            public void onLoading(boolean isLoading) {
                callback.onLoading(isLoading);
            }
        });
    }

    // Method to check if valid exams exist
    public void checkValidExamsExist(ExamExistsCallback callback) {
        fetchAllExams(new ExamListCallback() {
            @Override
            public void onExamSuccess(ArrayList<ExamModel.Datum> examList) {
                int validExamCount = 0;
                if (examList != null) {
                    for (ExamModel.Datum exam : examList) {
                        if (exam != null && exam.isValidExam()) {
                            validExamCount++;
                        }
                    }
                }
                callback.onResult(validExamCount > 0, validExamCount);
            }

            @Override
            public void onError(String errorMessage) {
                callback.onError(errorMessage);
            }

            @Override
            public void onLoading(boolean isLoading) {
                callback.onLoading(isLoading);
            }
        });
    }

    // Method to get exam by name
    public ExamModel.Datum getExamByName(ArrayList<ExamModel.Datum> examList, String examName) {
        if (examList != null && examName != null) {
            for (ExamModel.Datum exam : examList) {
                if (exam != null && exam.getExaminationName() != null &&
                        exam.getExaminationName().equalsIgnoreCase(examName.trim())) {
                    return exam;
                }
            }
        }
        return null;
    }

    // Method to filter exams by date range
    public ArrayList<ExamModel.Datum> getExamsByDateRange(ArrayList<ExamModel.Datum> examList, String startDate, String endDate) {
        ArrayList<ExamModel.Datum> filteredExams = new ArrayList<>();

        if (examList != null) {
            for (ExamModel.Datum exam : examList) {
                if (exam != null && exam.isValidExam()) {
                    // You can add date comparison logic here based on your date format
                    // For now, just adding exams that have valid dates
                    filteredExams.add(exam);
                }
            }
        }

        return filteredExams;
    }

    // Method to get upcoming exams (you'll need to implement date comparison)
    public ArrayList<ExamModel.Datum> getUpcomingExams(ArrayList<ExamModel.Datum> examList) {
        ArrayList<ExamModel.Datum> upcomingExams = new ArrayList<>();

        if (examList != null) {
            // Add your date comparison logic here
            // For now, returning all valid exams
            for (ExamModel.Datum exam : examList) {
                if (exam != null && exam.isValidExam()) {
                    upcomingExams.add(exam);
                }
            }
        }

        return upcomingExams;
    }

    // Additional helper methods

    // Method to get total exam count
    public int getTotalExamCount(ArrayList<ExamModel.Datum> examList) {
        return examList != null ? examList.size() : 0;
    }

    // Method to get valid exam count
    public int getValidExamCount(ArrayList<ExamModel.Datum> examList) {
        if (examList == null) return 0;

        int count = 0;
        for (ExamModel.Datum exam : examList) {
            if (exam != null && exam.isValidExam()) {
                count++;
            }
        }
        return count;
    }

    // Method to check if exam list is empty or null
    public boolean isExamListEmpty(ArrayList<ExamModel.Datum> examList) {
        return examList == null || examList.isEmpty();
    }

    // Method to get exam at specific position (0-based index)
    public ExamModel.Datum getExamAtPosition(ArrayList<ExamModel.Datum> examList, int position) {
        if (examList != null && position >= 0 && position < examList.size()) {
            return examList.get(position);
        }
        return null;
    }
}