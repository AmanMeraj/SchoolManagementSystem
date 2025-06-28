package com.school.schoolmanagement.HelperClasses;

import android.content.Context;
import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.school.schoolmanagement.Admin.Model.AllEmployees;
import com.school.schoolmanagement.GlobalViewModel.ViewModel;
import com.school.schoolmanagement.Utils.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Singleton class for managing employee data
 * Handles API calls and provides filtered employee lists
 */
public class EmployeeManager {
    private static final String TAG = "EmployeeManager";
    private static EmployeeManager instance;

    private List<AllEmployees> allEmployees;
    private boolean isDataLoaded = false;
    private boolean isLoading = false;
    private ViewModel viewModel;
    private Context context;
    private Utility utility;

    // LiveData for observing loading state
    private MutableLiveData<Boolean> loadingState = new MutableLiveData<>(false);
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();

    // Private constructor for singleton
    private EmployeeManager() {
        allEmployees = new ArrayList<>();
    }

    /**
     * Get singleton instance
     */
    public static synchronized EmployeeManager getInstance() {
        if (instance == null) {
            instance = new EmployeeManager();
        }
        return instance;
    }

    /**
     * Initialize the manager with context and ViewModel
     */
    public void initialize(Context context, ViewModelStoreOwner owner) {
        this.context = context;
        this.utility = new Utility();
        this.viewModel = new ViewModelProvider(owner).get(ViewModel.class);
    }

    /**
     * Load all employees from API
     */
    public void loadAllEmployees(EmployeeLoadCallback callback) {
        if (isLoading) {
            Log.d(TAG, "Already loading employees");
            return;
        }

        if (isDataLoaded && !allEmployees.isEmpty()) {
            Log.d(TAG, "Data already loaded, returning cached data");
            callback.onSuccess(new ArrayList<>(allEmployees));
            return;
        }

        isLoading = true;
        loadingState.setValue(true);

        String authToken = "Bearer " + utility.pref.getPrefString(context, utility.pref.user_token);

        if (authToken == null || authToken.isEmpty()) {
            isLoading = false;
            loadingState.setValue(false);
            callback.onError("Authentication error. Please login again.");
            return;
        }

        Log.d(TAG, "Loading employees from API");

        viewModel.getAllEmployees(authToken).observe((androidx.lifecycle.LifecycleOwner) context, response -> {
            isLoading = false;
            loadingState.setValue(false);

            if (response == null) {
                callback.onError("Server error. Please try again later.");
                return;
            }

            if (response.isSuccess) {
                List<AllEmployees> employees = response.data;
                if (employees != null && !employees.isEmpty()) {
                    allEmployees.clear();
                    allEmployees.addAll(employees);
                    isDataLoaded = true;
                    Log.d(TAG, "Employees loaded successfully: " + employees.size());
                    callback.onSuccess(new ArrayList<>(allEmployees));
                } else {
                    callback.onError("No employees found");
                }
            } else {
                String errorMsg = response.message != null ? response.message : "Unknown error occurred";
                callback.onError(errorMsg);
            }
        });
    }

    /**
     * Get filtered employees by role/department
     */
    public List<AllEmployees> getEmployeesByRole(String role) {
        List<AllEmployees> filteredList = new ArrayList<>();

        if (allEmployees == null || allEmployees.isEmpty()) {
            Log.w(TAG, "No employees data available for filtering");
            return filteredList;
        }

        for (AllEmployees employee : allEmployees) {
            if (employee != null && employee.getEmployeeRole() != null &&
                    employee.getEmployeeRole().toLowerCase().contains(role.toLowerCase())) {
                filteredList.add(employee);
            }
        }

        Log.d(TAG, "Filtered employees by role '" + role + "': " + filteredList.size());
        return filteredList;
    }

    /**
     * Get filtered employees by department
     */
//    public List<AllEmployees> getEmployeesByDepartment(String department) {
//        List<AllEmployees> filteredList = new ArrayList<>();
//
//        if (allEmployees == null || allEmployees.isEmpty()) {
//            Log.w(TAG, "No employees data available for filtering");
//            return filteredList;
//        }
//
//        for (AllEmployees employee : allEmployees) {
//            if (employee != null && employee.get != null &&
//                    employee.getDepartment().toLowerCase().contains(department.toLowerCase())) {
//                filteredList.add(employee);
//            }
//        }
//
//        Log.d(TAG, "Filtered employees by department '" + department + "': " + filteredList.size());
//        return filteredList;
//    }

    /**
     * Search employees by name
     */
    public List<AllEmployees> searchEmployeesByName(String query) {
        List<AllEmployees> filteredList = new ArrayList<>();

        if (allEmployees == null || allEmployees.isEmpty()) {
            Log.w(TAG, "No employees data available for searching");
            return filteredList;
        }

        String lowerQuery = query.toLowerCase();
        for (AllEmployees employee : allEmployees) {
            if (employee != null && employee.getName() != null &&
                    employee.getName().toLowerCase().contains(lowerQuery)) {
                filteredList.add(employee);
            }
        }

        Log.d(TAG, "Search results for '" + query + "': " + filteredList.size());
        return filteredList;
    }

    /**
     * Get all employees (cached)
     */
    public List<AllEmployees> getAllEmployees() {
        return new ArrayList<>(allEmployees);
    }

    /**
     * Check if data is loaded
     */
    public boolean isDataLoaded() {
        return isDataLoaded;
    }

    /**
     * Get loading state LiveData
     */
    public LiveData<Boolean> getLoadingState() {
        return loadingState;
    }

    /**
     * Get error message LiveData
     */
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    /**
     * Force refresh data
     */
    public void refreshData(EmployeeLoadCallback callback) {
        isDataLoaded = false;
        allEmployees.clear();
        loadAllEmployees(callback);
    }

    /**
     * Clear cached data
     */
    public void clearCache() {
        allEmployees.clear();
        isDataLoaded = false;
        isLoading = false;
    }

    /**
     * Callback interface for employee loading
     */
    public interface EmployeeLoadCallback {
        void onSuccess(List<AllEmployees> employees);
        void onError(String error);
    }
}
