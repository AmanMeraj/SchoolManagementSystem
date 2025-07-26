package com.school.schoolmanagement.GlobalViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.school.schoolmanagement.Admin.Adapter.CreateSubjectAdapter;
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
import com.school.schoolmanagement.Admin.Model.FeesDefaulter;
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
import com.school.schoolmanagement.GlobalRepository.GlobalRepository;
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

    public LiveData<GlobalRepository.ApiResponse<EmployeeResponse>> addBankAccount(String auth, BankAccountFields accountFields) {
        return repository.addBankAccount(auth, accountFields);
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
    public LiveData<GlobalRepository.ApiResponse<ManageLoginEmployee>> getEmployee2(String auth) {
        return repository.getEmployee2(auth);
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
    public LiveData<GlobalRepository.ApiResponse<EmployeeResponse>> addChapter(String auth, AddChapter addChapter) {
        return repository.addChapter(auth,addChapter);
    }
    public LiveData<GlobalRepository.ApiResponse<EmployeeResponse>> updateChapter(String auth, int chapterId,AddChapter addChapter) {
        return repository.updateChapter(auth,chapterId,addChapter);
    }
    public LiveData<GlobalRepository.ApiResponse<EmployeeResponse>> deleteChapter(String auth, int chapterId) {
        return repository.deleteChapter(auth,chapterId);
    }
    public LiveData<GlobalRepository.ApiResponse<ChapterResponse>> getChapter(String auth, int subjectId) {
        return repository.getChapters(auth,subjectId);
    }

    public LiveData<GlobalRepository.ApiResponse<EmployeeResponse>> addQuestionBank(String auth, AddQuestionBank addQuestionBank) {
        return repository.addQuestionBank(auth,addQuestionBank);
    }
    public LiveData<GlobalRepository.ApiResponse<EmployeeResponse>> addQuestionPaper(String auth, CreateQuestionPaper createQuestionPaper) {
        return repository.addQuestionPaper(auth,createQuestionPaper);
    }
    public LiveData<GlobalRepository.ApiResponse<EmployeeResponse>> updateQuestionPaper(String auth,int questionPaperId, CreateQuestionPaper createQuestionPaper) {
        return repository.updateQuestionPaper(auth,questionPaperId,createQuestionPaper);
    }
    public LiveData<GlobalRepository.ApiResponse<EmployeeResponse>> deleteQuestionPaper(String auth,int questionPaperId) {
        return repository.deleteQuestionPaper(auth,questionPaperId);
    }
    public LiveData<GlobalRepository.ApiResponse<QuestionPaperResponse>> getQuestionPaper(String auth) {
        return repository.getQuestionPaper(auth);
    }
    public LiveData<GlobalRepository.ApiResponse<StudentInfoReport>> getStudentReportClassWise(String auth, int classId) {
        return repository.getStudentReportClassWise(auth,classId);
    }
    public LiveData<GlobalRepository.ApiResponse<ClassWiseAttendanceReport>> getStudentReportClassWise(String auth, String date) {
        return repository.getStudentAttendanceClassWise(auth,date);
    }
    public LiveData<GlobalRepository.ApiResponse<StudentsAttendanceReport>> getStudentAttendanceReport(String auth, int classId, String startDate, String endDate) {
        return repository.getStudentAttendanceReport(auth, classId, startDate, endDate);
    }
    public LiveData<GlobalRepository.ApiResponse<EmployeeAttendanceReport>> getEmployeeAttendanceReport(String auth, String startDate, String endDate) {
        return repository.getEmployeeAttendanceReport(auth, startDate, endDate);
    }
    public LiveData<GlobalRepository.ApiResponse<StudentInfoReport>> getStudentReport(String auth) {
        return repository.getStudentReport(auth);
    }
    public LiveData<GlobalRepository.ApiResponse<EmployeeResponse>> generatePdfResult(String auth, StudentId studentId) {
        return repository.generatePdfResult(auth,studentId);
    }
    public LiveData<GlobalRepository.ApiResponse<EmployeeResponse>> postAccountSettings(String auth, AccountSettings accountSettings) {
        return repository.postAccountSettings(auth,accountSettings);
    }
    public LiveData<GlobalRepository.ApiResponse<EmployeeResponse>> updateAccountSettings(String auth, int id, AccountSettings accountSettings) {
        return repository.updateAccountSettings(auth, id, accountSettings);
    }

    public LiveData<GlobalRepository.ApiResponse<EmployeeResponse>> deleteAccountSettings(String auth, int id) {
        return repository.deleteAccountSettings(auth, id);
    }
    public LiveData<GlobalRepository.ApiResponse<AccountSettingsResponse>> getAccountSettings(String auth) {
        return repository.getAccountSettings(auth);
    }
    public LiveData<GlobalRepository.ApiResponse<EmployeeResponse>> postRulesAndRegulations(String auth, StudentId studentId) {
        return repository.postRulesAndRegulations(auth,studentId);
    }

    public LiveData<GlobalRepository.ApiResponse<ResultPdfResponse>> getPdfResult(String auth, int studentId) {
        return repository.getPdfResult(auth,studentId);
    }
    public LiveData<GlobalRepository.ApiResponse<CertificateResponse>> getLeaveCertificate(String auth, String date, int classId, int studentId) {
        return repository.getLeaveCertificate(auth, date, classId, studentId);
    }

    public LiveData<GlobalRepository.ApiResponse<CertificateResponse>> getCharacterCertificate(String auth, String date, int classId, int studentId) {
        return repository.getCharacterCertificate(auth, date, classId, studentId);
    }
    public LiveData<GlobalRepository.ApiResponse<AttendanceReport>> getStudentsMonthlyAttendance(
            String auth,
            String month,
            String year,
            int page,
            int limit,
            int classId) {

        return repository.getStudentsMonthlyAttendance(auth, month, year, page, limit, classId);
    }
    public LiveData<GlobalRepository.ApiResponse<AccountReport>> getAccountReport(
            String auth,
            String startDate,
            String endDate,
            int page,
            int limit) {

        return repository.getAccountReport(auth, startDate, endDate, page, limit);
    }
    public LiveData<GlobalRepository.ApiResponse<FeesCollectionReport>> getStudentFeesCollectionReport(
            String auth,
            String startDate,
            String endDate,
            int page,
            int limit,int studentId) {

        return repository.getStudentFeesCollectionReport(auth, startDate, endDate, page, limit,studentId);
    }
    public LiveData<GlobalRepository.ApiResponse<FeesCollectionReport>> getClassFeesCollectionReport(
            String auth,
            String startDate,
            String endDate,
            int page,
            int limit,int classId) {

        return repository.getClassFeesCollectionReport(auth, startDate, endDate, page, limit,classId);
    }
    public LiveData<GlobalRepository.ApiResponse<AttendanceReport>> getEmployeeMonthlyAttendance(
            String auth,
            String month,
            String year,
            int page,
            int limit,
            String role) {

        return repository.getEmployeeMonthlyAttendance(auth, month, year, page, limit, role);
    }
    public LiveData<GlobalRepository.ApiResponse<GetAttendanceResponse>> getStudentsAttendance(
            String auth,
            String startDate,
            String endDate,
            int classId) {

        return repository.getStudentsAttendance(auth, startDate, endDate, classId);
    }
    public LiveData<GlobalRepository.ApiResponse<GetEmployeeAttendance>> getEmployeeAttendance(
            String auth,
            String startDate) {

        return repository.getEmployeeAttendance(auth, startDate);
    }

        public LiveData<GlobalRepository.ApiResponse<EmployeeResponse>> updateQuestionBank(String auth, int questionBankId, AddQuestionBank addQuestionBank) {
        return repository.updateQuestionBank(auth,questionBankId,addQuestionBank);
    }
    public LiveData<GlobalRepository.ApiResponse<EmployeeResponse>> deleteQuestionBank(String auth, int questionBankId) {
        return repository.deleteQuestionBAnk(auth,questionBankId);
    }
    public LiveData<GlobalRepository.ApiResponse<QuestionBankModel>> getQuestionBank(String auth) {
        return repository.getQuestionBank(auth);
    }
    public LiveData<GlobalRepository.ApiResponse<EmployeeResponse>> createHomework(String auth, CreateHomework createHomework) {
        return repository.createHomework(auth,createHomework);
    }
    public LiveData<GlobalRepository.ApiResponse<EmployeeResponse>> saveStudentAttendance(String auth, PostStudentAttendance attendanceRequest) {
        return repository.saveStudentAttendance(auth, attendanceRequest);
    }
    public LiveData<GlobalRepository.ApiResponse<EmployeeResponse>> saveEmployeeAttendance(String auth, PostEmployeeAttendance attendanceRequest) {
        return repository.saveEmployeeAttendance(auth, attendanceRequest);
    }
    public LiveData<GlobalRepository.ApiResponse<EmployeeResponse>> updateStudentAttendance(String auth, PostStudentAttendance attendanceRequest) {
        return repository.updateStudentAttendance(auth, attendanceRequest);
    }
    public LiveData<GlobalRepository.ApiResponse<EmployeeResponse>> updateEmployeeAttendance(String auth, PostEmployeeAttendance attendanceRequest) {
        return repository.updateEmployeeAttendance(auth, attendanceRequest);
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
    public LiveData<GlobalRepository.ApiResponse<ClassModel2>> getAllClassesWithId(String auth, int classId) {
        return repository.getAllClassesWithId(auth,classId);
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
    public LiveData<GlobalRepository.ApiResponse<AllStudentResponse>> getStudent(String auth,int studentId) {
        return repository.getStudent(auth,studentId);
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
    public LiveData<GlobalRepository.ApiResponse<EmployeeResponse>> submitFees(String token, SubmitFees submitFees) {
        return repository.submitFees(token, submitFees);
    }
    public LiveData<GlobalRepository.ApiResponse<FeesReportResponse>> getFeesReport(String token, Integer classId,
                                                                                    Integer studentId, String startDate, String endDate, Integer page, Integer size) {
        return repository.getFeesReport(token, classId, studentId, startDate, endDate, page, size);
    }
    public LiveData<GlobalRepository.ApiResponse<FeesRecordResponse>> getFeesRecord(String token, Integer studentId, String feesMonth) {
        return repository.getFeesRecord(token, studentId, feesMonth);
    }
    public LiveData<GlobalRepository.ApiResponse<FeesDefaulterModel>> getFeesDefaulter(String token, String feesMonth) {
        return repository.getFeesDefaulter(token, feesMonth);
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

    // DASHBOARD API VIEW MODELS HERE

    public LiveData<GlobalRepository.ApiResponse<AdminDashboardClassInfo>> getClassInfo(String auth) {
        return repository.getClassInfo(auth);
    }

    public LiveData<GlobalRepository.ApiResponse<AdminDashboardEmployeeInfo>> getEmployeeInfo(String auth) {
        return repository.getEmployeeInfo(auth);
    }
    public LiveData<GlobalRepository.ApiResponse<DashboardApiEstimatedFees>> getEstimatedFees(String auth) {
        return repository.getEstimatedFees(auth);
    }

    public LiveData<GlobalRepository.ApiResponse<AdminDashboardExpenseInfo>> getExpenseInfo(String auth) {
        return repository.getExpenseInfo(auth);
    }
    public LiveData<GlobalRepository.ApiResponse<AdminDashboardAccountsOverview>> getAccountsOverview(String auth) {
        return repository.getAccountsOverview(auth);
    }
    public LiveData<GlobalRepository.ApiResponse<AdminDashboardFeesInfo>> getFeesInfo(String auth) {
        return repository.getFeesInfo(auth);
    }
    public LiveData<GlobalRepository.ApiResponse<AdminDashboardAbsentStudents>> getAbsentStudents(String auth) {
        return repository.getAbsentStudents(auth);
    }
    public LiveData<GlobalRepository.ApiResponse<AdminDashboardPresentEmployee>> getPresentEmployee(String auth) {
        return repository.getPresentEmployee(auth);
    }
    public LiveData<GlobalRepository.ApiResponse<List<AdminDashBoardClassGraph>>> getClassGraph(String auth) {
        return repository.getClassGraph(auth);
    }
    public LiveData<GlobalRepository.ApiResponse<AdminDashboardNewAdmission>> getNewAdmission(String auth) {
        return repository.getNewAdmission(auth);
    }
    public LiveData<GlobalRepository.ApiResponse<AdminDashboardPresentPercent>> getPresentPercent(String auth) {
        return repository.getPresentPercent(auth);
    }
    public LiveData<GlobalRepository.ApiResponse<AdminDashboardSchoolInfo>> getSchoolInfo(String auth) {
        return repository.getSchoolInfo(auth);
    }

    public LiveData<GlobalRepository.ApiResponse<AdminDashboardStudentInfo>> getStudentInfo(String auth) {
        return repository.getStudentInfo(auth);
    }

    public LiveData<GlobalRepository.ApiResponse<AdminDashboardSubjectInfo>> getSubjectInfo(String auth) {
        return repository.getSubjectInfo(auth);
    }
    public LiveData<GlobalRepository.ApiResponse<EmployeeDashboardResponse>> getEmployeeDashboard(String auth) {
        return repository.getEmployeeDashboard(auth);
    }
    public LiveData<GlobalRepository.ApiResponse<EmployeeProfile>> getEmployeeProfile(String auth, int employeeId) {
        return repository.getEmployeeProfile(auth, employeeId);
    }
    public LiveData<GlobalRepository.ApiResponse<StudentDashboardApiResponse1>> getStudentDashboard(String auth) {
        return repository.getStudentDashboard(auth);
    }
    // ViewModel Methods
    public LiveData<GlobalRepository.ApiResponse<StudentDashboardApiResponse2>> getStudentDashboard2(String auth) {
        return repository.getStudentDashboard2(auth);
    }

    public LiveData<GlobalRepository.ApiResponse<StudentDashboardApiResponse3>> getStudentDashboard3(String auth) {
        return repository.getStudentDashboard3(auth);
    }
    public LiveData<GlobalRepository.ApiResponse<StudentsProfileResponse>> getStudentProfile(String auth) {
        return repository.getStudentProfile(auth);
    }
    public LiveData<GlobalRepository.ApiResponse<StudentsTestResultResponse>> getStudentTestResult(String auth) {
        return repository.getStudentTestResult(auth);
    }
    public LiveData<GlobalRepository.ApiResponse<LastSubmittedFeesResponse>> getLastSubmittedFees(String auth) {
        return repository.getLastSubmittedFees(auth);
    }
    public LiveData<GlobalRepository.ApiResponse<StudentHomeworkResponse>> getStudentHomework(String auth, String date) {
        return repository.getStudentHomework(auth, date);
    }

    public LiveData<GlobalRepository.ApiResponse<StudentsExamResultResponse>> getStudentExamResult(String auth,int examId) {
        return repository.getStudentExamResult(auth,examId);
    }
    public LiveData<GlobalRepository.ApiResponse<StudentsAdmissionConfirmationResponse>> getStudentAdmissionConfirmation(String auth) {
        return repository.getStudentAdmissionConfirmation(auth);
    }
    public LiveData<GlobalRepository.ApiResponse<StudentExamListResponse>> getStudentExamList(String auth) {
        return repository.getStudentExamList(auth);
    }
    public LiveData<GlobalRepository.ApiResponse<EmployeeResponse>> updateStudentPassword(String auth, String newPassword) {
        return repository.updateStudentPassword(auth, newPassword);
    }  public LiveData<GlobalRepository.ApiResponse<EmployeeResponse>> updateEmployeePassword(String auth, String newPassword) {
        return repository.updateEmployeePassword(auth, newPassword);
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