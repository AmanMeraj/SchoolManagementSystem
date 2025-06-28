package com.school.schoolmanagement.GlobalViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.school.schoolmanagement.Admin.Adapter.CreateSubjectAdapter;
import com.school.schoolmanagement.Admin.Model.AccountGet;
import com.school.schoolmanagement.Admin.Model.AddIncome;
import com.school.schoolmanagement.Admin.Model.AddStudentMark;
import com.school.schoolmanagement.Admin.Model.AllEmployees;
import com.school.schoolmanagement.Admin.Model.ClassListResponse;
import com.school.schoolmanagement.Admin.Model.ClassModel;
import com.school.schoolmanagement.Admin.Model.ClassesWithSubjectsResponse;
import com.school.schoolmanagement.Admin.Model.CreateChart;
import com.school.schoolmanagement.Admin.Model.CreateClass;
import com.school.schoolmanagement.Admin.Model.CreateExam;
import com.school.schoolmanagement.Admin.Model.CreateHomework;
import com.school.schoolmanagement.Admin.Model.Employee2;
import com.school.schoolmanagement.Admin.Model.EmployeeList;
import com.school.schoolmanagement.Admin.Model.ExamModel;
import com.school.schoolmanagement.Admin.Model.FeesStructure;
import com.school.schoolmanagement.Admin.Model.GetStudentTest;
import com.school.schoolmanagement.Admin.Model.StudentMarksRequest;
import com.school.schoolmanagement.Admin.Model.SubjectCreationResponse;
import com.school.schoolmanagement.Admin.Model.SubjectRequestBody;

import com.school.schoolmanagement.Admin.Model.SubjectUpdateRequest;
import com.school.schoolmanagement.GlobalRepository.GlobalRepository;
import com.school.schoolmanagement.GlobalResponse.LoginResponse;
import com.school.schoolmanagement.Model.AccountStatement;
import com.school.schoolmanagement.Model.AllStudentResponse;
import com.school.schoolmanagement.Model.ClassTestResult;
import com.school.schoolmanagement.Model.CreateTest;
import com.school.schoolmanagement.Model.Employee;
import com.school.schoolmanagement.Model.EmployeeResponse;
import com.school.schoolmanagement.Model.HomeworkDetails;
import com.school.schoolmanagement.Model.Login;
import com.school.schoolmanagement.Model.ModelResponse;
import com.school.schoolmanagement.Model.PromotionBody;
import com.school.schoolmanagement.Model.SalaryPaidResponse;
import com.school.schoolmanagement.Model.StudentFields;

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
    public LiveData<GlobalRepository.ApiResponse<EmployeeResponse>> createStudents(String auth, StudentFields student) {
        responseLiveDataCreateEmployee = repository.createStudent(auth, student);
        return responseLiveDataCreateEmployee;
    }

    public LiveData<GlobalRepository.ApiResponse<EmployeeResponse>> updateStudents(String auth,int studentId, StudentFields student) {
        responseLiveDataCreateEmployee = repository.updateStudents(auth,studentId, student);
        return responseLiveDataCreateEmployee;
    }
    public LiveData<GlobalRepository.ApiResponse<EmployeeResponse>> promoteStudents(String auth,int classId, List<Long> studentIds) {
        responseLiveDataCreateEmployee = repository.promoteStudents(auth,classId, studentIds);
        return responseLiveDataCreateEmployee;
    }
    public LiveData<GlobalRepository.ApiResponse<EmployeeResponse>> updateExam(String auth,int examId, CreateExam createExam) {
        responseLiveDataCreateEmployee = repository.updateExam(auth,examId, createExam);
        return responseLiveDataCreateEmployee;
    }

//    public LiveData<GlobalRepository.ApiResponse<EmployeeResponse>> updateEmployee(String auth, int employeeId, Employee employee) {
//        return repository.updateEmployee(auth, employeeId, employee);
//    }
    public LiveData<GlobalRepository.ApiResponse<EmployeeList>> getEmployeeList(String auth) {
        return repository.getEmployeeList(auth);
    }
    public LiveData<GlobalRepository.ApiResponse<HomeworkDetails>> getAllHomework(String auth) {
        return repository.getAllHomework(auth);
    }

    public LiveData<GlobalRepository.ApiResponse<HomeworkDetails>> getFilteredHomework(String auth, Integer classId, String teacherId, String homeworkDate) {
        return repository.getFilteredHomework(auth, classId, teacherId, homeworkDate);
    }
    public LiveData<GlobalRepository.ApiResponse<EmployeeResponse>> postClass(String auth, CreateClass createClass) {
        return repository.postClass(auth,createClass);
    }
    public LiveData<GlobalRepository.ApiResponse<EmployeeResponse>> createHomework(String auth, CreateHomework createHomework) {
        return repository.createHomework(auth,createHomework);
    }
    public LiveData<GlobalRepository.ApiResponse<EmployeeResponse>> addStudentMarks(String auth, StudentMarksRequest studentMarksRequest) {
        return repository.addStudentMarks(auth,studentMarksRequest);
    }
    public LiveData<GlobalRepository.ApiResponse<EmployeeResponse>> createChart(String auth, CreateChart createChart) {
        return repository.createChart(auth,createChart);
    }
    public LiveData<GlobalRepository.ApiResponse<EmployeeResponse>> editAccountChart(String auth,int id, CreateChart createChart) {
        return repository.editAccountChart(auth,id,createChart);
    }
    public LiveData<GlobalRepository.ApiResponse<EmployeeResponse>> addIncome(String auth, AddIncome addIncome) {
        return repository.addIncome(auth,addIncome);
    }
    public LiveData<GlobalRepository.ApiResponse<EmployeeResponse>> addExpense(String auth, AddIncome addIncome) {
        return repository.addExpense(auth,addIncome);
    }
    public LiveData<GlobalRepository.ApiResponse<GetStudentTest>> getTestMarks(String auth, int classId, int studentId, String date) {
        return repository.getTestMarks(auth, classId,studentId,date);
    }
    public LiveData<GlobalRepository.ApiResponse<EmployeeResponse>> postTestMarks(String auth, CreateTest createTest) {
        return repository.postTestMarks(auth, createTest);
    }
    public LiveData<GlobalRepository.ApiResponse<EmployeeResponse>> updateTestMarks(String auth, CreateTest createTest) {
        return repository.updateTestMarks(auth, createTest);
    }

    public LiveData<GlobalRepository.ApiResponse<EmployeeResponse>> createExam(String auth, CreateExam createExam) {
        return repository.createExam(auth,createExam);
    }

    public LiveData<GlobalRepository.ApiResponse<ClassModel>> getAllClasses(String auth) {
        return repository.getAllClasses(auth);
    }
    public LiveData<GlobalRepository.ApiResponse<ExamModel>> getAllExams(String auth) {
        return repository.getAllExams(auth);
    }
    public LiveData<GlobalRepository.ApiResponse<AccountGet>> getAccountChart(String auth) {
        return repository.getAccountChart(auth);
    }
    public LiveData<GlobalRepository.ApiResponse<AddStudentMark>> getStudentMarks(String auth, int examId,int studentId, int classId) {
        return repository.getStudentMarks(auth,examId,studentId,classId);
    }
    public LiveData<GlobalRepository.ApiResponse<AccountStatement>> getAccountStatement(String auth) {
        return repository.getAccountStatement(auth);
    }
    public LiveData<GlobalRepository.ApiResponse<AddStudentMark>> getClassMarks(String auth, int examId, int classId) {
        return repository.getClassMarks(auth,examId,classId);
    }
    public LiveData<GlobalRepository.ApiResponse<AllStudentResponse>> getAllStudents(String auth) {
        return repository.getAllStudents(auth);
    }

    public LiveData<GlobalRepository.ApiResponse<AllStudentResponse>> getBasicList(String auth,int classId) {
        return repository.getBasicList(auth,classId);
    }
    public LiveData<GlobalRepository.ApiResponse<ClassTestResult>> getClassSubjectWise(String auth, int classId, int subjectId) {
        return repository.getClassSubjectWise(auth,classId,subjectId);
    }
    public LiveData<GlobalRepository.ApiResponse<ClassTestResult>> getStudentSubjectWise(String auth, int studentId, int subjectId) {
        return repository.getStudentSubjectWise(auth,studentId,subjectId);
    }
    public LiveData<GlobalRepository.ApiResponse<ClassTestResult>> getDateRangeWise(String auth, String startDate, String endDate) {
        return repository.getDateRangeWise(auth,startDate,endDate);
    }
    public LiveData<GlobalRepository.ApiResponse<AllStudentResponse>> getStudentsDetails(String auth,int studentID) {
        return repository.getStudentsDetails(auth,studentID);
    }

    public LiveData<GlobalRepository.ApiResponse<EmployeeResponse>> paySalary(String auth,Integer employeeId,  String salaryMonth, String dueDate, String fixedSalary, String bonus, String deduction) {
        return repository.paySalary(auth,employeeId,salaryMonth,dueDate,fixedSalary,bonus,deduction);
    }

    public LiveData<GlobalRepository.ApiResponse<SalaryPaidResponse>> salaryDetails(String auth, Integer employeeId) {
        return repository.salaryDetails(auth, employeeId);
    }
    public LiveData<GlobalRepository.ApiResponse<SalaryPaidResponse>> getSalaryReport(String auth, String salaryMonth, Integer employeeId, String dateRange) {
        return repository.getSalaryReport(auth, salaryMonth, employeeId, dateRange);
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

    public LiveData<GlobalRepository.ApiResponse<EmployeeResponse>> deleteStudent( String auth,int studentId) {
        return repository.deleteStudent( auth,studentId);
    }
    public LiveData<GlobalRepository.ApiResponse<EmployeeResponse>> deleteTest( String auth,int testId) {
        return repository.deleteTest( auth,testId);
    }

    public LiveData<EmployeeResponse> updateClass(String auth, int classId, CreateClass createClass) {
        return repository.updateClass(auth, classId, createClass);
    }

    public LiveData<EmployeeResponse> updateClassFees(String auth, int classId, FeesStructure feesStructure) {
        return repository.updateClassFees(auth, classId, feesStructure);
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

    public LiveData<GlobalRepository.ApiResponse<ModelResponse>> deleteExam(String auth, int examId) {
        return repository.deleteExam(auth,examId);
    }
    public LiveData<GlobalRepository.ApiResponse<ModelResponse>> deleteAccountChart(String auth, int id) {
        return repository.deleteAccountChart(auth,id);
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