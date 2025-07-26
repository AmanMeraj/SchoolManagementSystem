package com.school.schoolmanagement.HelperClasses;
import android.content.Context;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.school.schoolmanagement.Admin.Model.AddQuestionBank;
import com.school.schoolmanagement.GlobalViewModel.ViewModel;
import com.school.schoolmanagement.Utils.Utility;
import com.school.schoolmanagement.Admin.Model.QuestionBankModel;
import com.school.schoolmanagement.Admin.Model.Entry;
import java.util.ArrayList;

public class QuestionBankApiHelper extends Utility {

    private ViewModel viewModel;
    private Context context;

    public QuestionBankApiHelper(Context context) {
        this.context = context;
        if (context instanceof ViewModelStoreOwner) {
            this.viewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(ViewModel.class);
        }
    }

    // Interface for callback when fetching question banks
    public interface QuestionBankApiListCallback {
        void onQuestionBankApiSuccess(ArrayList<Entry> questionBankList);
        void onQuestionBankApiError(String errorMessage);
        void onQuestionBankApiLoading(boolean isLoading);
    }

    // Interface for callback when adding question bank
    public interface QuestionBankApiAddCallback {
        void onQuestionBankApiSuccess(String message);
        void onQuestionBankApiError(String errorMessage);
        void onQuestionBankApiLoading(boolean isLoading);
    }

    // Interface for callback when updating question bank
    public interface QuestionBankApiUpdateCallback {
        void onQuestionBankApiSuccess(String message);
        void onQuestionBankApiError(String errorMessage);
        void onQuestionBankApiLoading(boolean isLoading);
    }

    // Interface for callback when deleting question bank
    public interface QuestionBankApiDeleteCallback {
        void onQuestionBankApiSuccess(String message);
        void onQuestionBankApiError(String errorMessage);
        void onQuestionBankApiLoading(boolean isLoading);
    }

    // Method to fetch all question banks
    public void fetchQuestionBanks(QuestionBankApiListCallback callback) {
        if (!isInternetConnected(context)) {
            callback.onQuestionBankApiError("No Internet Connection!");
            return;
        }

        callback.onQuestionBankApiLoading(true);
        String auth = "Bearer " + pref.getPrefString(context, pref.user_token);

        if (context instanceof LifecycleOwner) {
            viewModel.getQuestionBank(auth).observe((LifecycleOwner) context, response -> {
                callback.onQuestionBankApiLoading(false);
                if (response != null) {
                    if (response.data != null && response.data.getData().entries != null) {
                        callback.onQuestionBankApiSuccess(response.data.data.getEntries());
                    } else {
                        callback.onQuestionBankApiError(response.message != null ? response.message : "No question banks found");
                    }
                } else {
                    callback.onQuestionBankApiError("Failed to fetch question banks");
                }
            });
        }
    }

    // Method to add a new question bank
    public void addNewQuestionBank(AddQuestionBank addQuestionBank, QuestionBankApiAddCallback callback) {
        if (!isInternetConnected(context)) {
            callback.onQuestionBankApiError("No Internet Connection!");
            return;
        }

        callback.onQuestionBankApiLoading(true);
        String auth = "Bearer " + pref.getPrefString(context, pref.user_token);

        if (context instanceof LifecycleOwner) {
            viewModel.addQuestionBank(auth, addQuestionBank).observe((LifecycleOwner) context, response -> {
                callback.onQuestionBankApiLoading(false);
                if (response != null) {
                    if (response.isSuccess) { // Assuming success status codes
                        callback.onQuestionBankApiSuccess("Question bank added successfully");
                    } else {
                        callback.onQuestionBankApiError(response.message != null ? response.message : "Failed to add question bank");
                    }
                } else {
                    callback.onQuestionBankApiError("Failed to add question bank");
                }
            });
        }
    }

    // Method to update an existing question bank
    public void updateQuestionBank(int questionBankId, AddQuestionBank addQuestionBank, QuestionBankApiUpdateCallback callback) {
        if (!isInternetConnected(context)) {
            callback.onQuestionBankApiError("No Internet Connection!");
            return;
        }

        callback.onQuestionBankApiLoading(true);
        String auth = "Bearer " + pref.getPrefString(context, pref.user_token);

        if (context instanceof LifecycleOwner) {
            viewModel.updateQuestionBank(auth, questionBankId, addQuestionBank).observe((LifecycleOwner) context, response -> {
                callback.onQuestionBankApiLoading(false);
                if (response != null) {
                    if (response.isSuccess) { // Assuming success status code
                        callback.onQuestionBankApiSuccess("Question bank updated successfully");
                    } else {
                        callback.onQuestionBankApiError(response.message != null ? response.message : "Failed to update question bank");
                    }
                } else {
                    callback.onQuestionBankApiError("Failed to update question bank");
                }
            });
        }
    }

    // Method to delete a question bank
    public void deleteQuestionBank(int questionBankId, QuestionBankApiDeleteCallback callback) {
        if (!isInternetConnected(context)) {
            callback.onQuestionBankApiError("No Internet Connection!");
            return;
        }

        callback.onQuestionBankApiLoading(true);
        String auth = "Bearer " + pref.getPrefString(context, pref.user_token);

        if (context instanceof LifecycleOwner) {
            viewModel.deleteQuestionBank(auth, questionBankId).observe((LifecycleOwner) context, response -> {
                callback.onQuestionBankApiLoading(false);
                if (response != null) {
                    if (response.isSuccess) { // Assuming success status code
                        callback.onQuestionBankApiSuccess("Question bank deleted successfully");
                    } else {
                        callback.onQuestionBankApiError(response.message != null ? response.message : "Failed to delete question bank");
                    }
                } else {
                    callback.onQuestionBankApiError("Failed to delete question bank");
                }
            });
        }
    }

    // Method to get exam titles for spinner/dropdown
    public ArrayList<String> getExamTitles(ArrayList<Entry> questionBankList) {
        ArrayList<String> examTitles = new ArrayList<>();
        examTitles.add("Select Exam"); // Default option

        for (Entry entry : questionBankList) {
            if (!examTitles.contains(entry.getExamTitle())) {
                examTitles.add(entry.getExamTitle());
            }
        }

        return examTitles;
    }

    // Method to get subjects for spinner/dropdown
    public ArrayList<String> getSubjects(ArrayList<Entry> questionBankList) {
        ArrayList<String> subjects = new ArrayList<>();
        subjects.add("Select Subject"); // Default option

        for (Entry entry : questionBankList) {
            if (!subjects.contains(entry.getSubject())) {
                subjects.add(entry.getSubject());
            }
        }

        return subjects;
    }

    // Method to get class names for spinner/dropdown
    public ArrayList<String> getClassNames(ArrayList<Entry> questionBankList) {
        ArrayList<String> classNames = new ArrayList<>();
        classNames.add("Select Class"); // Default option

        for (Entry entry : questionBankList) {
            if (!classNames.contains(entry.getClassName())) {
                classNames.add(entry.getClassName());
            }
        }

        return classNames;
    }

    // Method to get question types for spinner/dropdown
    public ArrayList<String> getQuestionTypes(ArrayList<Entry> questionBankList) {
        ArrayList<String> questionTypes = new ArrayList<>();
        questionTypes.add("Select Question Type"); // Default option

        for (Entry entry : questionBankList) {
            if (!questionTypes.contains(entry.getQuestionType())) {
                questionTypes.add(entry.getQuestionType());
            }
        }

        return questionTypes;
    }

    // Method to get entries by exam title
    public ArrayList<Entry> getEntriesByExamTitle(ArrayList<Entry> questionBankList, String examTitle) {
        ArrayList<Entry> filteredList = new ArrayList<>();

        for (Entry entry : questionBankList) {
            if (entry.getExamTitle().equals(examTitle)) {
                filteredList.add(entry);
            }
        }

        return filteredList;
    }

    // Method to get entries by subject
    public ArrayList<Entry> getEntriesBySubject(ArrayList<Entry> questionBankList, String subject) {
        ArrayList<Entry> filteredList = new ArrayList<>();

        for (Entry entry : questionBankList) {
            if (entry.getSubject().equals(subject)) {
                filteredList.add(entry);
            }
        }

        return filteredList;
    }

    // Method to get entries by class name
    public ArrayList<Entry> getEntriesByClassName(ArrayList<Entry> questionBankList, String className) {
        ArrayList<Entry> filteredList = new ArrayList<>();

        for (Entry entry : questionBankList) {
            if (entry.getClassName().equals(className)) {
                filteredList.add(entry);
            }
        }

        return filteredList;
    }

    // Method to get entries by question type
    public ArrayList<Entry> getEntriesByQuestionType(ArrayList<Entry> questionBankList, String questionType) {
        ArrayList<Entry> filteredList = new ArrayList<>();

        for (Entry entry : questionBankList) {
            if (entry.getQuestionType().equals(questionType)) {
                filteredList.add(entry);
            }
        }

        return filteredList;
    }

    // Method to filter entries by multiple criteria
    public ArrayList<Entry> filterEntries(ArrayList<Entry> questionBankList, String examTitle, String subject, String className, String questionType) {
        ArrayList<Entry> filteredList = new ArrayList<>(questionBankList);

        if (examTitle != null && !examTitle.equals("Select Exam")) {
            filteredList = getEntriesByExamTitle(filteredList, examTitle);
        }

        if (subject != null && !subject.equals("Select Subject")) {
            filteredList = getEntriesBySubject(filteredList, subject);
        }

        if (className != null && !className.equals("Select Class")) {
            filteredList = getEntriesByClassName(filteredList, className);
        }

        if (questionType != null && !questionType.equals("Select Question Type")) {
            filteredList = getEntriesByQuestionType(filteredList, questionType);
        }

        return filteredList;
    }

    // Method to search entries by question text
    public ArrayList<Entry> searchEntriesByQuestion(ArrayList<Entry> questionBankList, String searchQuery) {
        ArrayList<Entry> filteredList = new ArrayList<>();

        if (searchQuery == null || searchQuery.trim().isEmpty()) {
            return questionBankList;
        }

        String query = searchQuery.toLowerCase().trim();
        for (Entry entry : questionBankList) {
            if (entry.getQuestion().toLowerCase().contains(query)) {
                filteredList.add(entry);
            }
        }

        return filteredList;
    }

    // Method to get total marks for a list of entries
    public int getTotalMarks(ArrayList<Entry> questionBankList) {
        int totalMarks = 0;
        for (Entry entry : questionBankList) {
            try {
                totalMarks += Integer.parseInt(entry.getMark());
            } catch (NumberFormatException e) {
                // Handle case where mark is not a valid integer
                e.printStackTrace();
            }
        }
        return totalMarks;
    }

    // Method to get questions count
    public int getQuestionsCount(ArrayList<Entry> questionBankList) {
        return questionBankList != null ? questionBankList.size() : 0;
    }

    // Method to sort entries by marks (ascending/descending)
    public ArrayList<Entry> sortEntriesByMarks(ArrayList<Entry> questionBankList, boolean ascending) {
        ArrayList<Entry> sortedList = new ArrayList<>(questionBankList);

        if (ascending) {
            sortedList.sort((e1, e2) -> {
                try {
                    int mark1 = Integer.parseInt(e1.getMark());
                    int mark2 = Integer.parseInt(e2.getMark());
                    return Integer.compare(mark1, mark2);
                } catch (NumberFormatException e) {
                    return 0;
                }
            });
        } else {
            sortedList.sort((e1, e2) -> {
                try {
                    int mark1 = Integer.parseInt(e1.getMark());
                    int mark2 = Integer.parseInt(e2.getMark());
                    return Integer.compare(mark2, mark1);
                } catch (NumberFormatException e) {
                    return 0;
                }
            });
        }

        return sortedList;
    }

    // Method to sort entries by exam title alphabetically
    public ArrayList<Entry> sortEntriesByExamTitle(ArrayList<Entry> questionBankList, boolean ascending) {
        ArrayList<Entry> sortedList = new ArrayList<>(questionBankList);

        if (ascending) {
            sortedList.sort((e1, e2) -> e1.getExamTitle().compareToIgnoreCase(e2.getExamTitle()));
        } else {
            sortedList.sort((e1, e2) -> e2.getExamTitle().compareToIgnoreCase(e1.getExamTitle()));
        }

        return sortedList;
    }

    // Method to check if entry exists by question text
    public boolean isQuestionExists(ArrayList<Entry> questionBankList, String questionText) {
        for (Entry entry : questionBankList) {
            if (entry.getQuestion().equals(questionText)) {
                return true;
            }
        }
        return false;
    }

    // Method to get entry by question text
    public Entry getEntryByQuestion(ArrayList<Entry> questionBankList, String questionText) {
        for (Entry entry : questionBankList) {
            if (entry.getQuestion().equals(questionText)) {
                return entry;
            }
        }
        return null;
    }

    // Method to get entries with specific mark value
    public ArrayList<Entry> getEntriesByMark(ArrayList<Entry> questionBankList, String markValue) {
        ArrayList<Entry> filteredList = new ArrayList<>();

        for (Entry entry : questionBankList) {
            if (entry.getMark().equals(markValue)) {
                filteredList.add(entry);
            }
        }

        return filteredList;
    }

    // Method to get unique mark values
    public ArrayList<String> getUniqueMarks(ArrayList<Entry> questionBankList) {
        ArrayList<String> uniqueMarks = new ArrayList<>();

        for (Entry entry : questionBankList) {
            if (!uniqueMarks.contains(entry.getMark())) {
                uniqueMarks.add(entry.getMark());
            }
        }

        return uniqueMarks;
    }
}