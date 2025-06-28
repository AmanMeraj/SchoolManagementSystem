package com.school.schoolmanagement.HelperClasses;

import android.content.Context;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import com.school.schoolmanagement.Admin.Model.ClassesWithSubjectParentModel;
import com.school.schoolmanagement.Admin.Model.SubjectModel;
import com.school.schoolmanagement.GlobalViewModel.ViewModel;
import com.school.schoolmanagement.Utils.Utility;
import java.util.ArrayList;
import java.util.List;

public class SubjectApiHelper extends Utility {

    private ViewModel viewModel;
    private Context context;

    public SubjectApiHelper(Context context) {
        this.context = context;
        if (context instanceof ViewModelStoreOwner) {
            this.viewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(ViewModel.class);
        }
    }

    // Interface for callback
    public interface SubjectListCallback {
        void onSubjectsSuccess(ArrayList<SubjectModel> subjectList);
        void onSubjectsError(String errorMessage);
        void onSubjectsLoading(boolean isLoading);
    }

    // Interface for all classes with subjects callback
    public interface ClassesWithSubjectsCallback {
        void onSuccess(List<ClassesWithSubjectParentModel> classesWithSubjects);
        void onError(String errorMessage);
        void onLoading(boolean isLoading);
    }

    // Method to fetch subjects for a specific class
    public void fetchSubjectsForClass(int classId, SubjectListCallback callback) {
        if (!isInternetConnected(context)) {
            callback.onSubjectsError("No Internet Connection!");
            return;
        }

        callback.onSubjectsLoading(true);
        String auth = "Bearer " + pref.getPrefString(context, pref.user_token);

        if (context instanceof LifecycleOwner) {
            viewModel.getClassesWithSubjects(auth).observe((LifecycleOwner) context, response -> {
                callback.onSubjectsLoading(false);
                if (response != null && response.isSuccess) {
                    if (response.data != null && response.data.getData() != null) {
                        // Find the specific class and return its subjects
                        List<ClassesWithSubjectParentModel> allClasses = response.data.getData();
                        for (ClassesWithSubjectParentModel classModel : allClasses) {
                            if (classModel.getClassId() == classId) {
                                ArrayList<SubjectModel> subjects = new ArrayList<>();
                                if (classModel.getSubjects() != null) {
                                    subjects.addAll(classModel.getSubjects());
                                }
                                callback.onSubjectsSuccess(subjects);
                                return;
                            }
                        }
                        // If class not found
                        callback.onSubjectsError("Class not found");
                    } else {
                        callback.onSubjectsError(response.message != null ? response.message : "No data found");
                    }
                } else {
                    callback.onSubjectsError(response != null && response.message != null ? response.message : "Failed to fetch subjects");
                }
            });
        }
    }

    // Method to fetch all classes with their subjects
    public void fetchAllClassesWithSubjects(ClassesWithSubjectsCallback callback) {
        if (!isInternetConnected(context)) {
            callback.onError("No Internet Connection!");
            return;
        }

        callback.onLoading(true);
        String auth = "Bearer " + pref.getPrefString(context, pref.user_token);

        if (context instanceof LifecycleOwner) {
            viewModel.getClassesWithSubjects(auth).observe((LifecycleOwner) context, response -> {
                callback.onLoading(false);
                if (response != null && response.isSuccess) {
                    if (response.data != null && response.data.getData() != null) {
                        callback.onSuccess(response.data.getData());
                    } else {
                        callback.onError(response.message != null ? response.message : "No data found");
                    }
                } else {
                    callback.onError(response != null && response.message != null ? response.message : "Failed to fetch data");
                }
            });
        }
    }

    // Method to get subject names for spinner/dropdown
    public ArrayList<String> getSubjectNames(ArrayList<SubjectModel> subjectList) {
        ArrayList<String> subjectNames = new ArrayList<>();
        subjectNames.add("Select Subject"); // Default option

        for (SubjectModel subject : subjectList) {
            subjectNames.add(subject.getName()); // Assuming getName() method exists
        }

        return subjectNames;
    }

    // Method to get subject ID by position (considering first item is "Select Subject")
    public int getSubjectIdByPosition(ArrayList<SubjectModel> subjectList, int position) {
        if (position > 0 && position <= subjectList.size()) {
            return subjectList.get(position - 1).getId();
        }
        return -1; // Invalid selection
    }

    // Method to get subject data by ID
    public SubjectModel getSubjectById(ArrayList<SubjectModel> subjectList, int subjectId) {
        for (SubjectModel subject : subjectList) {
            if (subject.getId() == subjectId) {
                return subject;
            }
        }
        return null;
    }

    // Method to find position by subject ID (useful for pre-selection)
    public int getPositionBySubjectId(ArrayList<SubjectModel> subjectList, int subjectId) {
        for (int i = 0; i < subjectList.size(); i++) {
            if (subjectList.get(i).getId() == subjectId) {
                return i + 1; // +1 because first item is "Select Subject"
            }
        }
        return 0; // Return to "Select Subject" if not found
    }

    // Method to get subjects by class name (if you have class name instead of ID)
    public void fetchSubjectsByClassName(String className, SubjectListCallback callback) {
        if (!isInternetConnected(context)) {
            callback.onSubjectsError("No Internet Connection!");
            return;
        }

        callback.onSubjectsLoading(true);
        String auth = "Bearer " + pref.getPrefString(context, pref.user_token);

        if (context instanceof LifecycleOwner) {
            viewModel.getClassesWithSubjects(auth).observe((LifecycleOwner) context, response -> {
                callback.onSubjectsLoading(false);
                if (response != null && response.isSuccess) {
                    if (response.data != null && response.data.getData() != null) {
                        // Find the specific class by name and return its subjects
                        List<ClassesWithSubjectParentModel> allClasses = response.data.getData();
                        for (ClassesWithSubjectParentModel classModel : allClasses) {
                            if (classModel.getClassName().equalsIgnoreCase(className)) {
                                ArrayList<SubjectModel> subjects = new ArrayList<>();
                                if (classModel.getSubjects() != null) {
                                    subjects.addAll(classModel.getSubjects());
                                }
                                callback.onSubjectsSuccess(subjects);
                                return;
                            }
                        }
                        // If class not found
                        callback.onSubjectsError("Class '" + className + "' not found");
                    } else {
                        callback.onSubjectsError(response.message != null ? response.message : "No data found");
                    }
                } else {
                    callback.onSubjectsError(response != null && response.message != null ? response.message : "Failed to fetch subjects");
                }
            });
        }
    }

    // Method to check if a class has subjects
    public void checkClassHasSubjects(int classId, ClassHasSubjectsCallback callback) {
        fetchSubjectsForClass(classId, new SubjectListCallback() {
            @Override
            public void onSubjectsSuccess(ArrayList<SubjectModel> subjectList) {
                callback.onResult(subjectList != null && !subjectList.isEmpty(), subjectList != null ? subjectList.size() : 0);
            }

            @Override
            public void onSubjectsError(String errorMessage) {
                callback.onError(errorMessage);
            }

            @Override
            public void onSubjectsLoading(boolean isLoading) {
                callback.onLoading(isLoading);
            }
        });
    }

    // Interface for checking if class has subjects
    public interface ClassHasSubjectsCallback {
        void onResult(boolean hasSubjects, int subjectCount);
        void onError(String errorMessage);
        void onLoading(boolean isLoading);
    }
}