package com.school.schoolmanagement.retrofit;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitRequest {

    private static Retrofit retrofit;

    // Increased timeout values (in seconds)
    private static final int CONNECT_TIMEOUT = 120;
    private static final int READ_TIMEOUT = 120;
    private static final int WRITE_TIMEOUT = 120;

    public static Retrofit getRetrofitInstance() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                // Add timeout settings
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request();

                        // Log the request
                        Log.d("RETROFIT_REQUEST", "URL: " + request.url());

                        long startTime = System.currentTimeMillis();
                        Response response = chain.proceed(request);
                        long endTime = System.currentTimeMillis();

                        // Log response time
                        Log.d("RETROFIT_RESPONSE", "Time taken: " + (endTime - startTime) + " ms");
                        Log.d("RETROFIT_RESPONSE", "Response code: " + response.code());

                        Gson gson = new Gson();
                        String json = gson.toJson(response.message());
                        String json2 = gson.toJson(request.body());
                        Log.d("TAG", "getReview: " + json);
                        Log.d("INTERCEPT", "intercept: " + json2);

                        // Handle server errors
                        if (response.code() == 500) {
                            Log.e("RETROFIT_ERROR", "Server Error: " + response.message());
                            return response;
                        }

                        return response;
                    }
                })
                .retryOnConnectionFailure(true)  // Retry on connection failures
                .build();

        if (retrofit == null) {
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();

            retrofit = new Retrofit.Builder()
                    .baseUrl("http://139.59.72.25:8080/v1/api/sms/")
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }

    public static OkHttpClient getUnsafeOkHttpClient() {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(X509Certificate[] chain, String authType) {}

                        @Override
                        public void checkServerTrusted(X509Certificate[] chain, String authType) {}

                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            return new OkHttpClient.Builder()
                    .sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0])
                    .hostnameVerifier((hostname, session) -> true)
                    // Add timeout settings to unsafe client too
                    .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                    .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                    .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
                    .retryOnConnectionFailure(true)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Method to create a client with custom timeouts
    public static OkHttpClient getClientWithCustomTimeouts(int connectTimeout, int readTimeout, int writeTimeout) {
        return new OkHttpClient.Builder()
                .connectTimeout(connectTimeout, TimeUnit.SECONDS)
                .readTimeout(readTimeout, TimeUnit.SECONDS)
                .writeTimeout(writeTimeout, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build();
    }
}