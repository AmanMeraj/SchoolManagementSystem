package com.school.schoolmanagement.GlobalViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.school.schoolmanagement.Admin.Adapter.CreateSubjectAdapter;
import com.school.schoolmanagement.Admin.Model.AllEmployees;
import com.school.schoolmanagement.Admin.Model.ClassListResponse;
import com.school.schoolmanagement.Admin.Model.ClassModel;
import com.school.schoolmanagement.Admin.Model.ClassesWithSubjectsResponse;
import com.school.schoolmanagement.Admin.Model.CreateClass;
import com.school.schoolmanagement.Admin.Model.Employee2;
import com.school.schoolmanagement.Admin.Model.EmployeeList;
import com.school.schoolmanagement.Admin.Model.SubjectCreationResponse;
import com.school.schoolmanagement.Admin.Model.SubjectRequestBody;

import com.school.schoolmanagement.Admin.Model.SubjectUpdateRequest;
import com.school.schoolmanagement.GlobalRepository.GlobalRepository;
import com.school.schoolmanagement.GlobalResponse.LoginResponse;
import com.school.schoolmanagement.Model.Employee;
import com.school.schoolmanagement.Model.EmployeeResponse;
import com.school.schoolmanagement.Model.Login;
import com.school.schoolmanagement.Model.ModelResponse;

import java.util.ArrayList;
import java.util.List;

public class ViewModel extends AndroidViewModel {
    private final GlobalRepository repository;
    private LiveData<GlobalRepository.ApiResponse<LoginResponse>> loginLiveData = new MutableLiveData<>();
    private LiveData<GlobalRepository.ApiResponse<EmployeeResponse>> responseLiveDataCreateEmployee;
    private LiveData<GlobalRepository.ApiResponse<Employee2>> responseLiveDataGetEmployee;
    private LiveData<GlobalRepository.ApiResponse<List<AllEmployees>>> responseLiveDataAllEmployees;

    public ViewModel(@NonNull Application application) {
        super(application);
        repository = new GlobalRepository();
    }

    public LiveData<GlobalRepository.ApiResponse<LoginResponse>> postLogin(Login login) {
        loginLiveData = repository.login(login);
        return loginLiveData;
    }

    /**
     * Creates a new employee.
     *
     * @param auth Authorization token.
     * @param employee Employee data to be created.
     * @return LiveData containing ApiResponse wrapped in EmployeeResponse.
     */
    public LiveData<GlobalRepository.ApiResponse<EmployeeResponse>> createEmployee(String auth, Employee employee) {
        responseLiveDataCreateEmployee = repository.createEmployee(auth, employee);
        return responseLiveDataCreateEmployee;
    }

//    public LiveData<GlobalRepository.ApiResponse<EmployeeResponse>> updateEmployee(String auth, int employeeId, Employee employee) {
//        return repository.updateEmployee(auth, employeeId, employee);
//    }
    public LiveData<GlobalRepository.ApiResponse<EmployeeList>> getEmployeeList(String auth) {
        return repository.getEmployeeList(auth);
    }
    public LiveData<GlobalRepository.ApiResponse<EmployeeResponse>> postClass(String auth, CreateClass createClass) {
        return repository.postClass(auth,createClass);
    }

    public LiveData<GlobalRepository.ApiResponse<ClassModel>> getAllClasses(String auth) {
        return repository.getAllClasses(auth);
    }

    public LiveData<GlobalRepository.ApiResponse<SubjectCreationResponse>> createSubjects(
            String auth,
            Integer classId,
            List<CreateSubjectAdapter.SubjectItem> subjectItems) {

        // Convert SubjectItems to SubjectRequestBody objects
        List<SubjectRequestBody> requestBodies = new ArrayList<>();
        for (CreateSubjectAdapter.SubjectItem item : subjectItems) {
            String name = item.getSubjectName();
            int totalMarks = Integer.parseInt(item.getTotalMarks());
            requestBodies.add(new SubjectRequestBody(name, totalMarks));
        }

        return repository.createSubjects(auth, classId, requestBodies);
    }

    public LiveData<GlobalRepository.ApiResponse<SubjectCreationResponse>> updateSubjects(String token, int classId, List<SubjectUpdateRequest> subjects) {
        return repository.updateSubjects(token, classId, subjects);
    }

    public LiveData<GlobalRepository.ApiResponse<EmployeeResponse>> deleteClass( String auth,int classId) {
        return repository.deleteClass( auth,classId);
    }

    public LiveData<EmployeeResponse> updateClass(String auth, int classId, CreateClass createClass) {
        return repository.updateClass(auth, classId, createClass);
    }



    /**
     * Gets an employee by ID.
     *
     * @param auth Authorization token.
     * @param employeeId ID of the employee to fetch.
     * @return LiveData containing ApiResponse wrapped in Employee.
     */
    public LiveData<GlobalRepository.ApiResponse<Employee2>> getEmployee(String auth, int employeeId) {
        responseLiveDataGetEmployee = repository.getEmployee(auth, employeeId);
        return responseLiveDataGetEmployee;
    }

    public LiveData<GlobalRepository.ApiResponse<ModelResponse>> deleteEmployee(String auth, int employeeId) {
        return repository.deleteEmployee(auth, employeeId);
    }
    public LiveData<GlobalRepository.ApiResponse<ClassesWithSubjectsResponse>> getClassesWithSubjects(String auth) {
        return repository.getClassesWithSubjects(auth);
    }
    public LiveData<GlobalRepository.ApiResponse<ClassListResponse>> getAllClassList(String auth) {
        return repository.getAllClassList(auth);
    }
    /**
     * Gets all employees.
     *
     * @param auth Authorization token.
     * @return LiveData containing ApiResponse wrapped in List of AllEmployees.
     */
    public LiveData<GlobalRepository.ApiResponse<List<AllEmployees>>> getAllEmployees(String auth) {
        responseLiveDataAllEmployees = repository.getAllEmployees(auth);
        return responseLiveDataAllEmployees;
    }

//    /**
//     * Searches employees by name.
//     *
//     * @param auth Authorization token.
//     * @param query Search query string.
//     * @return LiveData containing ApiResponse wrapped in List of AllEmployees.
//     */
//    public LiveData<GlobalRepository.ApiResponse<List<AllEmployees>>> searchEmployees(String auth, String query) {
//        responseLiveDataAllEmployees = repository.searchEmployees(auth, query);
//        return responseLiveDataAllEmployees;
//    }
//
//    /**
//     * Filters employees by role.
//     *
//     * @param auth Authorization token.
//     * @param role Employee role to filter by.
//     * @return LiveData containing ApiResponse wrapped in List of AllEmployees.
//     */
//    public LiveData<GlobalRepository.ApiResponse<List<AllEmployees>>> filterEmployeesByRole(String auth, String role) {
//        responseLiveDataAllEmployees = repository.filterEmployeesByRole(auth, role);
//        return responseLiveDataAllEmployees;
//    }
}