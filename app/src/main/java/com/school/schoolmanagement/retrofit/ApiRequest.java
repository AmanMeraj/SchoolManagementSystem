package com.school.schoolmanagement.retrofit;

import com.school.schoolmanagement.GlobalResponse.LoginResponse;
import com.school.schoolmanagement.Model.Login;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface ApiRequest {


    @Headers({"Accept: application/json"})
    @POST("custom/v1/register")
    Call<LoginResponse> postSignup(
            @Body Login signup
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
