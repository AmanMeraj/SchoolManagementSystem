package com.school.schoolmanagement.retrofit;

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
import com.school.schoolmanagement.Admin.Model.AllEmployeesResponse;
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
import com.school.schoolmanagement.Model.UpdateStudentsResponse;
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
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface ApiRequest {

    @Headers({"Accept: application/json"})
    @POST("admin/login")
    Call<LoginResponse> postLogin(
            @Body Login login
    );

    @Headers({"Accept: application/json"})
    @POST("custom/v1/register")
    Call<LoginResponse> postSignup(
            @Body Login signup
    );

    @Headers({"Accept: application/json"})
    @POST("class/create-homework")
    Call<EmployeeResponse> createHomework(
            @Header("Authorization") String auth,
            @Body CreateHomework createHomework
    );

    @Headers({"Accept: application/json"})
    @POST("exams/add-student-marks")
    Call<EmployeeResponse> addStudentMarks(
            @Header("Authorization") String auth,
            @Body StudentMarksRequest studentMarksRequest
    );

    @Headers({"Accept: application/json"})
    @POST("account/chart/create")
    Call<EmployeeResponse> createChart(
            @Header("Authorization") String auth,
            @Body CreateChart createChart
    );

    @Headers({"Accept: application/json"})
    @POST("account/add/income")
    Call<EmployeeResponse> addIncome(
            @Header("Authorization") String auth,
            @Body AddIncome addIncome
    );
    @Headers({"Accept: application/json"})
    @POST("account/add/expense")
    Call<EmployeeResponse> addExpense(
            @Header("Authorization") String auth,
            @Body AddIncome addIncome
    );
    @Headers({"Accept: application/json"})
    @POST("class/create-test")
    Call<EmployeeResponse> addClassTest(
            @Header("Authorization") String auth,
            @Body CreateTest createTest
    );
    @Headers({"Accept: application/json"})
    @PATCH("class/update-test")
    Call<EmployeeResponse> updateClassTest(
            @Header("Authorization") String auth,
            @Body CreateTest createTest
    );

    @Headers({"Accept: application/json"})
    @POST("exams/create-exam")
    Call<EmployeeResponse> addExam(
            @Header("Authorization") String auth,
            @Body CreateExam createExam
            );

    //Employee apis endpoints
    @Headers({"Accept: application/json"})
    @GET("employee/get-employee")
    Call<Employee2> getEmployee(
            @Header("Authorization") String auth,
            @Query("id") int employeeId
    );
    @Headers({"Accept: application/json"})
    @GET("employee/get-employee")
    Call<ManageLoginEmployee> getEmployee2(
            @Header("Authorization") String auth
    );

    @Headers({"Accept: application/json"})
    @GET("exams/get-exams-details")
    Call<ExamModel> getExams(
            @Header("Authorization") String auth
    );
    @Headers({"Accept: application/json"})
    @GET("account/chart/getAll")
    Call<AccountGet> getAccountChart(
            @Header("Authorization") String auth
    );
    @Headers({"Accept: application/json"})
    @GET("exams/get-student-marks")
    Call<AddStudentMark> getStudentMarks(
            @Header("Authorization") String auth,
            @Query("examId") int examId,
            @Query("classId")int classId,
            @Query("studentId")int studentId
    );
    @Headers({"Accept: application/json"})
    @GET("account/statement")
    Call<AccountStatement> getAccountStatement(
            @Header("Authorization") String auth
    );
    @Headers({"Accept: application/json"})
    @GET("exams/get-student-marks")
    Call<AddStudentMark> getClassMarks(
            @Header("Authorization") String auth,
            @Query("examId") int examId,
            @Query("classId")int classId
    );




    @Headers({"Accept: application/json"})
    @GET("employee/get-all-employees")
    Call<AllEmployeesResponse> getAllEmployees(
            @Header("Authorization") String auth
    );
    @Headers({"Accept: application/json"})
    @GET("class/get-homework")
    Call<HomeworkDetails> getAllHomework(
            @Header("Authorization") String auth
    );
    @Headers({"Accept: application/json"})
    @GET("class/get-homework")
    Call<HomeworkDetails> getFilteredHomework(
            @Header("Authorization") String auth,
            @Query("classId") Integer classId,
            @Query("teacherId") String teacherId,
            @Query("homeworkDate") String homeworkDate
    );
    @Headers({"Accept: application/json"})
    @GET("employee/get-employees-list")
    Call<EmployeeList> getEmployeeList(
            @Header("Authorization") String auth
    );
    @Headers({"Accept: application/json"})
    @Multipart
    @POST("employee/create-employee") // Adjust endpoint URL as needed
    Call<EmployeeResponse> createEmployee(
            @Header("Authorization") String auth,
            @Part("employee") RequestBody employeeFields,
            @Part MultipartBody.Part profileImage
    );

    @Headers({"Accept: application/json"})
    @Multipart
    @POST("student/create-student") // Adjust endpoint URL as needed
    Call<EmployeeResponse> createStudent(
            @Header("Authorization") String auth,
            @Part("student") RequestBody studentFields,
            @Part MultipartBody.Part profileImage
    );

    @Headers({"Accept: application/json"})
    @GET("class/get-test") // Adjust endpoint URL as needed
    Call<GetStudentTest> getTestMarks(
            @Header("Authorization") String auth,
           @Query("classId")int classId,
           @Query("subjectId")int studentId,
           @Query("testDate") String date
    );

    @Headers({"Accept: application/json"})
    @PATCH("student/update-student/{classId}")
    Call<EmployeeResponse> updateStudent(
            @Header("Authorization") String auth,
            @Path("classId") int classId,
            @Part("student") RequestBody studentFields,
            @Part MultipartBody.Part profileImage
    );
    @Headers({"Accept: application/json"})
    @PUT("account/chart/{id}")
    Call<EmployeeResponse> editAccountChart(
            @Header("Authorization") String auth,
            @Path("id") int id,
            @Body CreateChart createChart
    );

    @Headers({"Accept: application/json"})
    @Multipart
    @PATCH("employee/update-employee/{employeeId}")
    Call<EmployeeResponse> updateEmployee(
            @Header("Authorization") String auth,
            @Path("employeeId") int employeeId,
            @Part("employee") RequestBody employeeBody,
            @Part MultipartBody.Part profilePicture
    );
    @Headers({"Accept: application/json"})
    @PATCH("student/promote-student")
    Call<EmployeeResponse> promoteStudents(
            @Header("Authorization") String auth,
            @Query("classId") int classId,
            @Body List<Long> studentIds
    );
    @Headers({"Accept: application/json"})
    @PATCH("exams/update-exam/{examId}")
    Call<EmployeeResponse> updateExam(
            @Header("Authorization") String auth,
            @Path("examId") int classId,
            @Body CreateExam createExam
    );

    @Headers({"Accept: application/json"})
    @DELETE("employee/delete-employee/{employeeId}")
    Call<ModelResponse> deleteEmployee(
            @Header("Authorization") String authorization,
            @Path("employeeId") int employeeId
    );
    @Headers({"Accept: application/json"})
    @DELETE("exams/delete-exam/{examId}")
    Call<ModelResponse> deleteExam(
            @Header("Authorization") String authorization,
            @Path("examId") int examId
    );
    @Headers({"Accept: application/json"})
    @DELETE("account/chart/{id}")
    Call<ModelResponse> deleteAccountChart(
            @Header("Authorization") String authorization,
            @Path("id") int id
    );

    //CLASS APIS ADMIN
    @Headers({"Accept: application/json"})
    @POST("class/create-class")
    Call<EmployeeResponse> postClass(
            @Header("Authorization") String auth,
            @Body CreateClass createClass
    );
    @Headers({"Accept: application/json"})
    @POST("subject/add-chapter")
    Call<EmployeeResponse> addChapter(
            @Header("Authorization") String auth,
            @Body AddChapter addChapter
    );

    @Headers({"Accept: application/json"})
    @GET("subject/chapters")
    Call<ChapterResponse> getChapters(
            @Header("Authorization") String auth,
            @Query("subjectId") int subjectId
    );
    @DELETE("subject/delete-chapter")
    Call<EmployeeResponse> deleteChapter(
            @Header("Authorization") String auth,
            @Query("chapterId") int chapterId
    );

    @PATCH("subject/update-chapter")
    Call<EmployeeResponse> updateChapter(
            @Header("Authorization") String auth,
            @Query("chapterId") int chapterId,
            @Body AddChapter addChapter
    );

    @Headers({"Accept: application/json"})
    @POST("question-bank/add")
    Call<EmployeeResponse> addQuestionBank(
            @Header("Authorization") String auth,
            @Body AddQuestionBank addQuestionBank
    );
    @Headers({"Accept: application/json"})
    @POST("question-papers/create")
    Call<EmployeeResponse> addQuestionPaper(
            @Header("Authorization") String auth,
            @Body CreateQuestionPaper createQuestionPaper
    );
    @Headers({"Accept: application/json"})
    @GET("question-papers/get")
    Call<QuestionPaperResponse> getQuestionPaper(
            @Header("Authorization") String auth
    );
    @Headers({"Accept: application/json"})
    @GET("report-card/student/monthly-attendance-report")
    Call<AttendanceReport> getStudentsMonthlyAttendance(
            @Header("Authorization") String auth,
            @Query("month") String month,
            @Query("year") String year,
            @Query("page") int page,
            @Query("limit") int limit,
            @Query("classId") int classId
    );

    @Headers({"Accept: application/json"})
    @GET("report-card/account-report")
    Call<AccountReport> getAccountReport(
            @Header("Authorization") String auth,
            @Query("startDate") String startDate,
            @Query("endDate") String endDate,
            @Query("page") int page,
            @Query("limit") int limit
    );
    @Headers({"Accept: application/json"})
    @GET("report-card/fees-collection")
    Call<FeesCollectionReport> getStudentFeesCollectionReport(
            @Header("Authorization") String auth,
            @Query("startDate") String startDate,
            @Query("endDate") String endDate,
            @Query("page") int page,
            @Query("limit") int limit,
            @Query("studentId") int studentId
    );
    @Headers({"Accept: application/json"})
    @GET("report-card/fees-collection")
    Call<FeesCollectionReport> getClassFeesCollectionReport(
            @Header("Authorization") String auth,
            @Query("startDate") String startDate,
            @Query("endDate") String endDate,
            @Query("page") int page,
            @Query("limit") int limit,
            @Query("classId") int classId
    );

    @Headers({"Accept: application/json"})
    @GET("attendance/get")
    Call<GetAttendanceResponse> getStudentsAttendance(
            @Header("Authorization") String auth,
            @Query("startDate") String startDate,
            @Query("endDate") String endDate,
            @Query("classId") int classId
    );

    @Headers({"Accept: application/json"})
    @GET("attendance/employee/status")
    Call<GetEmployeeAttendance> getEmployeeAttendance(
            @Header("Authorization") String auth,
            @Query("date") String startDate
    );
    @Headers({"Accept: application/json"})
    @POST("attendance/student/mark") // Replace with your actual endpoint
    Call<EmployeeResponse> saveStudentAttendance(
            @Header("Authorization") String auth,
            @Body PostStudentAttendance attendanceRequest
    );
    @Headers({"Accept: application/json"})
    @POST("attendance/employee/save") // Replace with your actual endpoint
    Call<EmployeeResponse> saveEmployeeAttendance(
            @Header("Authorization") String auth,
            @Body PostEmployeeAttendance attendanceRequest
    );

    @Headers({"Accept: application/json"})
    @PATCH("attendance/student/update") // Replace with your actual endpoint
    Call<EmployeeResponse> updateStudentAttendance(
            @Header("Authorization") String auth,
            @Body PostStudentAttendance attendanceRequest
    );
    @Headers({"Accept: application/json"})
    @PATCH("attendance/employee/update") // Replace with your actual endpoint
    Call<EmployeeResponse> updateEmployeeAttendance(
            @Header("Authorization") String auth,
            @Body PostEmployeeAttendance attendanceRequest
    );
    @Headers({"Accept: application/json"})
    @GET("report-card/student-info")
    Call<StudentInfoReport> getStudentReportClassWise(
            @Header("Authorization") String auth,
            @Query("classId") int classId
    );
    @Headers({"Accept: application/json"})
    @GET("attendance/report/class-wise")
    Call<ClassWiseAttendanceReport> getStudentAttendanceClassWise(
            @Header("Authorization") String auth,
            @Query("date") String date
    );
    @Headers({"Accept: application/json"})
    @GET("attendance/report/student-wise")
    Call<StudentsAttendanceReport> getStudentAttendanceReport(
            @Header("Authorization") String auth,
            @Query("classId") int classId,
            @Query("startDate") String starDate,
            @Query("endDate") String endDate
    );
    @Headers({"Accept: application/json"})
    @GET("attendance/report/employee-wise")
    Call<EmployeeAttendanceReport> getEmployeeAttendanceReport(
            @Header("Authorization") String auth,
            @Query("startDate") String starDate,
            @Query("endDate") String endDate
    );
    @Headers({"Accept: application/json"})
    @POST("report-card/student-info")
    Call<EmployeeResponse> generatePdfResult(
            @Header("Authorization") String auth,
            @Body StudentId studentId
            );
    @Headers({"Accept: application/json"})
    @Multipart
    @POST("admin/add-account")
    Call<EmployeeResponse> addBankAccount(
            @Header("Authorization") String auth,
            @Part("name") RequestBody name,
            @Part("address") RequestBody address,
            @Part("accNumber") RequestBody accNumber,
            @Part("instruction") RequestBody instruction,
            @Part MultipartBody.Part image
    );
    @Headers({"Accept: application/json"})
    @PATCH("account-setting/update/{id}")
    Call<EmployeeResponse> updateAccountSettings(
            @Header("Authorization") String auth,
            @Path("id") int id,
            @Body AccountSettings accountSettings
            );
    @DELETE("account-setting/delete/{Id}")
    Call<EmployeeResponse> deleteAccountSettings(
            @Header("Authorization") String auth,
            @Path("Id") int id
    );
    @Headers({"Accept: application/json"})
    @GET("account-setting/get")
    Call<AccountSettingsResponse> getAccountSettings(
            @Header("Authorization") String auth
    );
    @Headers({"Accept: application/json"})
    @POST("account-setting/create")
    Call<EmployeeResponse> postAccountSettings(
            @Header("Authorization") String auth,
            @Body AccountSettings accountSettings
    );
    @Headers({"Accept: application/json"})
    @GET("certificates/leave")
    Call<CertificateResponse> getLeaveCertificate(
            @Header("Authorization") String auth,
            @Query("date") String date,
            @Query("classId") int classId,
            @Query("studentId") int studentId
    );
    @Headers({"Accept: application/json"})
    @GET("certificates/character")
    Call<CertificateResponse> getCharacterCertificate(
            @Header("Authorization") String auth,
            @Query("date") String date,
            @Query("classId") int classId,
            @Query("studentId") int studentId
    );

    @Headers({"Accept: application/json"})
    @POST("admin/add-rules")
    Call<EmployeeResponse> postRulesAndRegulations(
            @Header("Authorization") String auth,
            @Body StudentId studentId
            );

    @Headers({"Accept: application/json"})
    @GET("report-card/download/{studentId}")
    Call<ResultPdfResponse> getPdfResult(
            @Header("Authorization") String auth,
            @Path("studentId")int studentId
            );
    @Headers({"Accept: application/json"})
    @GET("report-card/student-info")
    Call<StudentInfoReport> getStudentReport(
            @Header("Authorization") String auth
    );
    @Headers({"Accept: application/json"})
    @GET("report-card/employee/monthly-attendance-report")
    Call<AttendanceReport> getEmployeeMonthlyAttendance(
            @Header("Authorization") String auth,
            @Query("month") String month,
            @Query("year") String year,
            @Query("page") int page,
            @Query("limit") int limit,
            @Query("role") String role
    );
    @DELETE("question-papers/delete/{questionPaperId}")
    Call<EmployeeResponse> deleteQuestionPaper(
            @Header("Authorization") String auth,
            @Path("questionPaperId") int questionPaperId
    );

    @PATCH("question-papers/update/{questionPaperId}")
    Call<EmployeeResponse> updateQuestionPaper(
            @Header("Authorization") String auth,
            @Path("questionPaperId") int questionPaperId,
            @Body CreateQuestionPaper createQuestionPaper
    );

    @Headers({"Accept: application/json"})
    @GET("question-bank/get")
    Call<QuestionBankModel> getQuestionBank(
            @Header("Authorization") String auth
    );

    @DELETE("question-bank/delete/{questionBankId}")
    Call<EmployeeResponse> deleteQuestionBank(
            @Header("Authorization") String auth,
            @Path("questionBankId") int questionBankId
    );

    @PATCH("question-bank/update/{questionBankId}")
    Call<EmployeeResponse> updateQuestionBank(
            @Header("Authorization") String auth,
            @Path("questionBankId") int questionBankId,
            @Body AddQuestionBank addQuestionBank
    );
    @Headers({"Accept: application/json"})
    @GET("class/get-classes")
    Call<ClassModel> getAllClasses(
            @Header("Authorization") String auth
    );
    @Headers({"Accept: application/json"})
    @GET("class/get-classes")
    Call<ClassModel2> getAllClassesWithId(
            @Header("Authorization") String auth,
            @Query("classId") int classId
    );

    @Headers({"Accept: application/json"})
    @GET("student/fetch-student")
    Call<AllStudentResponse> getAllStudents(
            @Header("Authorization") String auth
    );
    @Headers({"Accept: application/json"})
    @GET("student/fetch-student")
    Call<AllStudentResponse> getStudent(
            @Header("Authorization") String auth,
            @Query("studentId") int studentId
    );

    @Headers({"Accept: application/json"})
    @GET("student/fetch-student")
    Call<AllStudentResponse> getBasicList(
            @Header("Authorization") String auth,
            @Query("classId") int classId
    );
    @Headers({"Accept: application/json"})
    @GET("class/test-result/class-subject")
    Call<ClassTestResult> getClassSubjectWise(
            @Header("Authorization") String auth,
            @Query("classId") int classId,
            @Query("subjectId") int subjectId
    );
    @Headers({"Accept: application/json"})
    @GET("class/test-result/student-subject")
    Call<ClassTestResult> getStudentSubjectWise(
            @Header("Authorization") String auth,
            @Query("studentId") int studentId,
            @Query("subjectId") int subjectId
    );

    @Headers({"Accept: application/json"})
    @GET("class/test-result/date-range")
    Call<ClassTestResult> getDateRangeWise(
            @Header("Authorization") String auth,
            @Query("startDate") String startDate,
            @Query("endDate") String endDate
    );
    @Headers({"Accept: application/json"})
    @GET("student/fetch-student")
    Call<AllStudentResponse> getStudentDetails(
            @Header("Authorization") String auth,
            @Query("studentId") int studentID
    );

    @DELETE("class/delete-class/{classId}")
    Call<EmployeeResponse> deleteClass(
            @Header("Authorization") String auth,
            @Path("classId") int classId
    );

    @DELETE("student/delete-student/{studentId}")
    Call<EmployeeResponse> deleteStudent(
            @Header("Authorization") String auth,
            @Path("studentId") int studentId
    );

    @DELETE("class/delete/{testId}")
    Call<EmployeeResponse> deleteTest(
            @Header("Authorization") String auth,
            @Path("testId") int testId
    );
    @Headers({
            "Accept: application/json",
            "Content-Type: application/json"
    })
    @PATCH("class/update-class/{classId}")
    Call<EmployeeResponse> updateClass(
            @Header("Authorization") String auth,
            @Path("classId") int classId,
            @Body CreateClass createClass
    );
    @PATCH("class/update-class/{classId}")
    Call<EmployeeResponse> updateClassFees(
            @Header("Authorization") String auth,
            @Path("classId") int classId,
            @Body FeesStructure feesStructure
    );


    @Headers({"Accept: application/json"})
    @GET("class/get-classes-with-subjects")
    Call<ClassesWithSubjectsResponse> getClassesWithSubjects(
            @Header("Authorization") String auth
    );

    @Headers({"Accept: application/json"})
    @GET("employee/employee-salary-details")
    Call<SalaryPaidResponse> getEmployeeSalaryDetails(
            @Header("Authorization") String auth,
            @Query("employeeId") int employeeId
    );

    @Headers({"Accept: application/json"})
    @GET("employee/employee-salary-details")
    Call<SalaryPaidResponse> getEmployeeSalaryList(
            @Header("Authorization") String auth,
            @Query("salaryMonth") String salaryMonth, // Mandatory
            @Query("employeeId") Integer employeeId, // Optional (can be null)
            @Query("dateRange") String dateRange // Optional (can be null)
    );

    @GET("class/get-all-classes")
    Call<ClassListResponse> getAllClassList(
            @Header("Authorization") String authorization
    );

    @POST("class/create-subjects/{classId}")
    Call<SubjectCreationResponse> createSubjects(
            @Header("Authorization") String authorization,
            @Path("classId") Integer classId,
            @Body List<SubjectRequestBody> subjects);
    @GET("fees/report")
    Call<FeesReportResponse> getFeesReport(
            @Header("Authorization") String authorization,
            @Query("classId") Integer classId,
            @Query("studentId") Integer studentId,
            @Query("startDate") String startDate,
            @Query("endDate") String endDate,
            @Query("page") Integer page,
            @Query("size") Integer size
    );
    @GET("fees/fees-record")
    Call<FeesRecordResponse> getFeesRecord(
            @Header("Authorization") String authorization,
            @Query("studentId") Integer studentId,
            @Query("feesMonth") String feesMonth
    );
    @GET("fees/fees-defaulters")
    Call<FeesDefaulterModel> getDefaulter(
            @Header("Authorization") String authorization,
            @Query("feesMonth") String feesMonth
    );
    @POST("fees/save")
    Call<EmployeeResponse> postFees(
            @Header("Authorization") String authorization,
            @Body SubmitFees submitFees);

    @POST("employee/employee-salary/{employeeId}")
    Call<EmployeeResponse> paySalary(
            @Header("Authorization") String authorization,
            @Path("employeeId") Integer classId,
            @Query("salaryMonth") String salaryMonth,
            @Query("dueDate") String dueDate,
            @Query("fixedSalary") String fixedSalary,
            @Query("bonus") String bonus,
            @Query("deduction") String deduction
            );

    @PATCH("class/update-subject/{classId}")
    Call<SubjectCreationResponse> updateSubjects(
            @Header("Authorization") String authorization,
            @Path("classId") int classId,
            @Body List<SubjectUpdateRequest> subjects
    );

    // DASHBOARD API ENDPOINTS HERE
    @Headers({"Accept: application/json"})
    @GET("dashboard/class-info")
    Call<AdminDashboardClassInfo> getClassInfo(
            @Header("Authorization") String auth
    );

    @Headers({"Accept: application/json"})
    @GET("dashboard/employee-info")
    Call<AdminDashboardEmployeeInfo> getEmployeeInfo(
            @Header("Authorization") String auth
    );
    @Headers({"Accept: application/json"})
    @GET("dashboard/estimated-fees")
    Call<DashboardApiEstimatedFees> getEstimatedFees(
            @Header("Authorization") String auth
    );

    @Headers({"Accept: application/json"})
    @GET("dashboard/expense-info")
    Call<AdminDashboardExpenseInfo> getExpenseInfo(
            @Header("Authorization") String auth
    );
    @Headers({"Accept: application/json"})
    @GET("dashboard/absent-students")
    Call<AdminDashboardAbsentStudents> getAbsentStudents(
            @Header("Authorization") String auth
    );
    @Headers({"Accept: application/json"})
    @GET("dashboard/present-staff")
    Call<AdminDashboardPresentEmployee> getPresentEmployee(
            @Header("Authorization") String auth
    );
    @Headers({"Accept: application/json"})
    @GET("dashboard/account-overview")
    Call<AdminDashboardAccountsOverview> getAccountsOverview(
            @Header("Authorization") String auth
    );
    @Headers({"Accept: application/json"})
    @GET("dashboard/percent-details")
    Call<AdminDashboardPresentPercent> getPresentPercent(
            @Header("Authorization") String auth
    );
    @Headers({"Accept: application/json"})
    @GET("dashboard/class-graph")
    Call<List<AdminDashBoardClassGraph>> getClassGraph(
            @Header("Authorization") String auth
    );
    @Headers({"Accept: application/json"})
    @GET("dashboard/new-admissions")
    Call<AdminDashboardNewAdmission> getNewAdmission(
            @Header("Authorization") String auth
    );

    @Headers({"Accept: application/json"})
    @GET("dashboard/fees-info")
    Call<AdminDashboardFeesInfo> getFeesInfo(
            @Header("Authorization") String auth
    );

    @Headers({"Accept: application/json"})
    @GET("dashboard/school-info")
    Call<AdminDashboardSchoolInfo> getSchoolInfo(
            @Header("Authorization") String auth
    );

    @Headers({"Accept: application/json"})
    @GET("dashboard/student-info")
    Call<AdminDashboardStudentInfo> getStudentInfo(
            @Header("Authorization") String auth
    );

    @Headers({"Accept: application/json"})
    @GET("dashboard/subject-info")
    Call<AdminDashboardSubjectInfo> getSubjectInfo(
            @Header("Authorization") String auth
    );

    // Employee API
    @Headers({"Accept: application/json"})
    @GET("dashboard/employee-dashboard")
    Call<EmployeeDashboardResponse> getEmployeeDashboard(
            @Header("Authorization") String auth
    );
    @Headers({"Accept: application/json"})
    @GET("employee/profile")
    Call<EmployeeProfile> getEmployeeProfile(
            @Header("Authorization") String auth,
            @Query("employeeId") int employeeId
    );


    //STUDENT API
    @Headers({"Accept: application/json"})
    @GET("dashboard/student-dashboard")
    Call<StudentDashboardApiResponse1> GetStudentDashboard1(
            @Header("Authorization") String auth
    );
    @Headers({"Accept: application/json"})
    @GET("dashboard/student/class-test-report")
    Call<StudentDashboardApiResponse2> GetStudentDashboard2(
            @Header("Authorization") String auth
    );
    @Headers({"Accept: application/json"})
    @GET("dashboard/student-fees")
    Call<StudentDashboardApiResponse3> GetStudentDashboard3(
            @Header("Authorization") String auth
    );
    @Headers({"Accept: application/json"})
    @GET("student/profile")
    Call<StudentsProfileResponse> getStudentProfile(
            @Header("Authorization") String auth
    );
    @Headers({"Accept: application/json"})
    @GET("student/tests-result")
    Call<StudentsTestResultResponse> getStudentTestResult(
            @Header("Authorization") String auth
    );
    @Headers({"Accept: application/json"})
    @GET("student/last-submitted")
    Call<LastSubmittedFeesResponse> getLastSubmittedFees(
            @Header("Authorization") String auth
    );
    @Headers({"Accept: application/json"})
    @GET("student/homework")
    Call<StudentHomeworkResponse> getStudentHomework(
            @Header("Authorization") String auth,
            @Query("date")String date
    );
    @Headers({"Accept: application/json"})
    @GET("student/exam-result")
    Call<StudentsExamResultResponse> getStudentExamResult(
            @Header("Authorization") String auth,
            @Query("examId")int examId
    );
    @Headers({"Accept: application/json"})
    @GET("student/admission-confirmation")
    Call<StudentsAdmissionConfirmationResponse> getStudentAdmissionConfirmation(
            @Header("Authorization") String auth
    );
    @Headers({"Accept: application/json"})
    @GET("student/student/get-exams-details")
    Call<StudentExamListResponse> getStudentExamList(
            @Header("Authorization") String auth
    );
    @Headers({"Accept: application/json"})
    @PATCH("student/update-password")
    Call<EmployeeResponse> updateStudentPassword(
            @Header("Authorization") String auth,
            @Query("newPassword") String newPassword
    );
    @Headers({"Accept: application/json"})
    @PATCH("employee/update-password")
    Call<EmployeeResponse> updateEmployeePassword(
            @Header("Authorization") String auth,
            @Query("newPassword") String newPassword
    );

}
