package com.school.schoolmanagement.HelperClasses;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import com.school.schoolmanagement.Admin.Model.ClassModel;
import com.school.schoolmanagement.GlobalViewModel.ViewModel;
import com.school.schoolmanagement.Model.StudentDetails;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class for managing student data operations
 * Handles loading students for all classes or specific class
 * Provides filtering and search functionality
 */
public class StudentHelper {
    private static final String TAG = "StudentHelper";

    private Context context;
    private ViewModel viewModel;
    private String authToken;
    private LifecycleOwner lifecycleOwner;
    private StudentHelperCallback callback;

    // Data lists
    private List<StudentDetails> allStudentsList;
    private List<StudentDetails> filteredStudentsList;
    private ArrayList<ClassModel.Data> classList;

    // Current state
    private int selectedClassId = -1;
    private String currentSearchQuery = "";

    /**
     * Interface for callback methods
     */
    public interface StudentHelperCallback {
        void onLoadingStateChanged(boolean isLoading);
        void onStudentsLoaded(List<StudentDetails> students, int totalCount);
        void onError(String errorMessage);
        void onClassesLoaded(ArrayList<ClassModel.Data> classes);
    }

    /**
     * Constructor
     */
    public StudentHelper(Context context, ViewModel viewModel, String authToken,
                         LifecycleOwner lifecycleOwner, StudentHelperCallback callback) {
        this.context = context;
        this.viewModel = viewModel;
        this.authToken = authToken;
        this.lifecycleOwner = lifecycleOwner;
        this.callback = callback;

        // Initialize lists
        this.allStudentsList = new ArrayList<>();
        this.filteredStudentsList = new ArrayList<>();
        this.classList = new ArrayList<>();
    }

    /**
     * Initialize and load all classes
     */
    public void initializeClasses() {
        try {
            if (callback != null) {
                callback.onLoadingStateChanged(true);
            }

            viewModel.getAllClasses(authToken).observe(lifecycleOwner, response -> {
                if (callback != null) {
                    callback.onLoadingStateChanged(false);
                }

                if (response != null && response.data != null) {
                    classList.clear();

                    // Add "All Classes" option at position 0
                    ClassModel.Data allClassesOption = createAllClassesOption();
                    if (allClassesOption != null) {
                        classList.add(allClassesOption);
                    }

                    // Add actual classes
                    if (response.data.getData() != null) {
                        classList.addAll(response.data.getData());
                    }

                    // Set default selection to "All Classes"
                    selectedClassId = 0;

                    // Notify callback
                    if (callback != null) {
                        callback.onClassesLoaded(classList);
                    }

                    // Load all students initially
                    loadAllStudents();

                } else {
                    if (callback != null) {
                        callback.onError("No classes found");
                    }
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error initializing classes: " + e.getMessage(), e);
            if (callback != null) {
                callback.onLoadingStateChanged(false);
                callback.onError("Error loading classes");
            }
        }
    }

    /**
     * Load all students
     */
    public void loadAllStudents() {
        try {
            if (callback != null) {
                callback.onLoadingStateChanged(true);
            }

            viewModel.getAllStudents(authToken).observe(lifecycleOwner, response -> {
                if (callback != null) {
                    callback.onLoadingStateChanged(false);
                }

                if (response != null && response.isSuccess && response.data.getData() != null) {
                    handleStudentsResponse(response.data.getData());
                    selectedClassId = 0; // Set to "All Classes"
                } else {
                    String errorMessage = response != null && response.message != null ?
                            response.message : "Failed to fetch students";
                    if (callback != null) {
                        callback.onError(errorMessage);
                    }
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error loading all students: " + e.getMessage(), e);
            if (callback != null) {
                callback.onLoadingStateChanged(false);
                callback.onError("Error connecting to server");
            }
        }
    }

    /**
     * Load students by specific class
     */
    public void loadStudentsByClass(int classId) {
        try {
            if (classId == 0) {
                // Load all students if classId is 0 (All Classes)
                loadAllStudents();
                return;
            }

            if (callback != null) {
                callback.onLoadingStateChanged(true);
            }

            viewModel.getBasicList(authToken, classId).observe(lifecycleOwner, response -> {
                if (callback != null) {
                    callback.onLoadingStateChanged(false);
                }

                if (response != null && response.isSuccess && response.data.getData() != null) {
                    handleStudentsResponse(response.data.getData());
                    selectedClassId = classId;
                } else {
                    String errorMessage = response != null && response.message != null ?
                            response.message : "Failed to fetch students for selected class";
                    if (callback != null) {
                        callback.onError(errorMessage);
                    }
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error loading students by class: " + e.getMessage(), e);
            if (callback != null) {
                callback.onLoadingStateChanged(false);
                callback.onError("Error connecting to server");
            }
        }
    }

    /**
     * Handle students response from API
     */
    private void handleStudentsResponse(Object data) {
        try {
            allStudentsList.clear();

            if (data instanceof List) {
                List<?> responseList = (List<?>) data;
                for (Object item : responseList) {
                    if (item instanceof StudentDetails) {
                        allStudentsList.add((StudentDetails) item);
                    }
                }
            }

            // Apply current search filter
            applySearchFilter();

            Log.d(TAG, "Successfully loaded " + allStudentsList.size() + " students");

        } catch (Exception e) {
            Log.e(TAG, "Error handling students response: " + e.getMessage(), e);
            if (callback != null) {
                callback.onError("Error processing student data");
            }
        }
    }

    /**
     * Filter students based on search query
     */
    public void searchStudents(String query) {
        try {
            currentSearchQuery = query != null ? query.trim() : "";
            applySearchFilter();
        } catch (Exception e) {
            Log.e(TAG, "Error searching students: " + e.getMessage(), e);
        }
    }

    /**
     * Apply search filter to students list
     */
    private void applySearchFilter() {
        try {
            filteredStudentsList.clear();

            if (currentSearchQuery == null || currentSearchQuery.isEmpty()) {
                filteredStudentsList.addAll(allStudentsList);
            } else {
                String lowerCaseQuery = currentSearchQuery.toLowerCase();
                for (StudentDetails student : allStudentsList) {
                    if (student != null &&
                            student.getStudentName() != null &&
                            student.getStudentName().toLowerCase().contains(lowerCaseQuery)) {
                        filteredStudentsList.add(student);
                    }
                }
            }

            // Notify callback with filtered results
            if (callback != null) {
                callback.onStudentsLoaded(new ArrayList<>(filteredStudentsList), allStudentsList.size());
            }

            Log.d(TAG, "Filtered " + allStudentsList.size() + " students to " +
                    filteredStudentsList.size() + " results");

        } catch (Exception e) {
            Log.e(TAG, "Error applying search filter: " + e.getMessage(), e);
        }
    }

    /**
     * Get class by ID
     */
    public ClassModel.Data getClassById(int classId) {
        try {
            for (ClassModel.Data classData : classList) {
                if (classData.getClassId() == classId) {
                    return classData;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting class by ID: " + e.getMessage(), e);
        }
        return null;
    }

    /**
     * Get class name by ID
     */
    public String getClassNameById(int classId) {
        try {
            ClassModel.Data classData = getClassById(classId);
            return classData != null ? classData.getClassName() : "Unknown Class";
        } catch (Exception e) {
            Log.e(TAG, "Error getting class name by ID: " + e.getMessage(), e);
            return "Unknown Class";
        }
    }

    /**
     * Refresh data - reload current selection
     */
    public void refreshData() {
        try {
            currentSearchQuery = "";
            if (selectedClassId == 0 || selectedClassId == -1) {
                loadAllStudents();
            } else {
                loadStudentsByClass(selectedClassId);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error refreshing data: " + e.getMessage(), e);
            if (callback != null) {
                callback.onError("Error refreshing data");
            }
        }
    }

    /**
     * Reset all data and state
     */
    public void resetData() {
        try {
            currentSearchQuery = "";
            selectedClassId = 0;
            allStudentsList.clear();
            filteredStudentsList.clear();

            // Reload all students
            loadAllStudents();

        } catch (Exception e) {
            Log.e(TAG, "Error resetting data: " + e.getMessage(), e);
        }
    }

    /**
     * Remove student from lists (for delete operations)
     */
    public void removeStudent(StudentDetails student) {
        try {
            allStudentsList.remove(student);
            filteredStudentsList.remove(student);

            // Notify callback with updated list
            if (callback != null) {
                callback.onStudentsLoaded(new ArrayList<>(filteredStudentsList), allStudentsList.size());
            }

        } catch (Exception e) {
            Log.e(TAG, "Error removing student: " + e.getMessage(), e);
        }
    }

    /**
     * Create "All Classes" option
     */
    private ClassModel.Data createAllClassesOption() {
        try {
            ClassModel.Data allClasses = new ClassModel.Data();
            allClasses.setClassId(0);
            allClasses.setClassName("All Classes");
            return allClasses;
        } catch (Exception e) {
            Log.e(TAG, "Error creating All Classes option: " + e.getMessage(), e);
            return null;
        }
    }

    // Getters
    public List<StudentDetails> getAllStudentsList() {
        return new ArrayList<>(allStudentsList);
    }

    public List<StudentDetails> getFilteredStudentsList() {
        return new ArrayList<>(filteredStudentsList);
    }

    public ArrayList<ClassModel.Data> getClassList() {
        return new ArrayList<>(classList);
    }

    public int getSelectedClassId() {
        return selectedClassId;
    }

    public String getCurrentSearchQuery() {
        return currentSearchQuery;
    }

    public boolean isShowingAllClasses() {
        return selectedClassId == 0;
    }
}
