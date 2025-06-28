package com.school.schoolmanagement.retrofit;

import com.school.schoolmanagement.Admin.Model.AccountGet;
import com.school.schoolmanagement.Admin.Model.AddIncome;
import com.school.schoolmanagement.Admin.Model.AddStudentMark;
import com.school.schoolmanagement.Admin.Model.AllEmployees;
import com.school.schoolmanagement.Admin.Model.AllEmployeesResponse;
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
import com.school.schoolmanagement.Model.UpdateStudentsResponse;

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
            @Body CreateClass createClass);

    @Headers({"Accept: application/json"})
    @GET("class/get-all-classes")
    Call<ClassModel> getAllClasses(
            @Header("Authorization") String auth
    );

    @Headers({"Accept: application/json"})
    @GET("student/fetch-student")
    Call<AllStudentResponse> getAllStudents(
            @Header("Authorization") String auth
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







//
//    @Headers({"Accept: application/json"})
//    @POST("custom/v1/verify-otp")
//    Call<OtpResponse> postOtp(
//            @Body Otp otp
//    );
//
//    @Headers({"Accept: application/json"})
//    @POST("jwt-auth/v1/token")
//    Call<SigninResponse> postLogin(
//            @Body Login login
//    );
//
//    @Headers({"Accept: application/json"})
//    @POST("custom/v1/wishlist/add")
//    Call<WishlistAddResponse> addWishlist(
//            @Header("Authorization") String authorization,
//            @Body Wishlist wishlist
//    );
//
//    @Headers({"Accept: application/json"})
//    @HTTP(method = "DELETE", path = "custom/v1/wishlist/delete", hasBody = true)
//    Call<WishlistDeleteResponse> deleteWishlist(
//            @Header("Authorization") String authorization,
//            @Body Wishlist wishlist
//    );
//
//    @Headers({"Accept: application/json"})
//    @GET("wc/store/v1/products")
//    Call<ArrayList<HomeResponse>> getHome(
//            @Query("type") String type,
//            @Query("category") int category
//    );
//
//    @Headers({"Accept: application/json"})
//    @GET("custom/v1/wishlist")
//    Call<ArrayList<WishlistResponse>> getWishlist(
//            @Header("Authorization") String authorization
//    );
//
//    @Headers({"Accept: application/json"})
//    @GET("custom/v1/customer-orders")
//    Call<ArrayList<MyOrdersResponse>> getOrders(
//            @Header("Authorization") String authorization
//    );
//
//    @Headers({"Accept: application/json"})
//    @GET("wc/store/v1/products/categories")
//    Call<ArrayList<Category>> getCategory(
//    );
//
//    @Headers({"Accept: application/json"})
//    @GET("custom/v1/draws")
//    Call<ArrayList<DrawResponse>> getDraw();
//
//    @Headers({"Accept: application/json"})
//    @GET("custom/v1/winners")
//    Call<ArrayList<WinnerResponse>> getWinner();
//
//
//    @Headers({"Accept: application/json"})
//    @GET("wc/store/v1/products")
//    Call<List<ShopResponse>> getShop(
//            @QueryMap Map<String, String> filters,
//            @Query("category") int id,
//            @Query("page") int page,
//            @Query("per_page") int perPage
//    );
//
//    @Headers({"Accept: application/json"})
//    @GET("wc/store/v1/products/{id}")  // Add {id} as a placeholder in the URL
//    Call<ShopResponse> getShopWishList(@Path("id") int id); // Pass the ID dynamically
//
//
//
//
//    @Headers({"Accept: application/json"})
//    @GET("wc/store/v1/cart")
//    Call<CartResponse> getCart(
//            @Header("Authorization") String authorization
//    );
//
//    @Headers({"Accept: application/json"})
//    @GET("custom/v1/app-version")
//    Call<AppVersion> getAppVersion(
//    );



}
