package com.school.schoolmanagement.retrofit;

import com.school.schoolmanagement.Admin.Model.AllEmployees;
import com.school.schoolmanagement.Admin.Model.AllEmployeesResponse;
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

    //Employee apis endpoints
    @Headers({"Accept: application/json"})
    @GET("employee/get-employee")
    Call<Employee2> getEmployee(
            @Header("Authorization") String auth,
            @Query("id") int employeeId
    );




    @Headers({"Accept: application/json"})
    @GET("employee/get-all-employees")
    Call<AllEmployeesResponse> getAllEmployees(
            @Header("Authorization") String auth
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
    @Multipart
    @PATCH("employee/update-employee/{employeeId}")
    Call<EmployeeResponse> updateEmployee(
            @Header("Authorization") String auth,
            @Path("employeeId") int employeeId,
            @Part("employee") RequestBody employeeBody,
            @Part MultipartBody.Part profilePicture
    );

    @Headers({"Accept: application/json"})
    @DELETE("employee/delete-employee/{employeeId}")
    Call<ModelResponse> deleteEmployee(
            @Header("Authorization") String authorization,
            @Path("employeeId") int employeeId
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

    @DELETE("class/delete-class/{classId}")
    Call<EmployeeResponse> deleteClass(
            @Header("Authorization") String auth,
            @Path("classId") int classId
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
    @Headers({"Accept: application/json"})
    @GET("class/get-classes-with-subjects")
    Call<ClassesWithSubjectsResponse> getClassesWithSubjects(
            @Header("Authorization") String auth
    );

    @GET("class/get-all-classes")
    Call<ClassListResponse> getAllClassList(@Header("Authorization") String authorization);

    @POST("class/create-subjects/{classId}")
    Call<SubjectCreationResponse> createSubjects(
            @Header("Authorization") String authorization,
            @Path("classId") Integer classId,
            @Body List<SubjectRequestBody> subjects);

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
