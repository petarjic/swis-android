package com.swis.android.ApiService;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.swis.android.App;
import com.swis.android.util.AppConstants;


import java.io.IOException;
import java.util.concurrent.TimeUnit;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;


public class ApiClient {
    public static final String BASE_URL = "https://api.swis.app/user/";
    public static final String DEV_URL = "https://devapi.swis.app/user/";

    private static Retrofit retrofit = null;

    public static OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build();


    public static Retrofit getClientNormal() {
        retrofit = new Retrofit.Builder()
                .baseUrl(DEV_URL)
                .client(client)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit;
    }
    public static Retrofit getClient() {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();

                Request request = original.newBuilder()

                      .header("Authorization", "Bearer "+ App.getInstance().getPrefsHelper().getPref(AppConstants.API_TOKEN))
                        .header("Content-TYPE", "application/json")
                        .header("Accept", "*/*")
                        .method(original.method(), original.body())
                        .build();
                return chain.proceed(request);
            }
        });

        OkHttpClient client = httpClient.build();

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        retrofit = new Retrofit.Builder()
                .baseUrl(DEV_URL)
                .client(client)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        return retrofit;
    }
    private static OkHttpClient createOkHttpClient() {
        final OkHttpClient.Builder httpClient =
                new OkHttpClient.Builder();
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {

                final Request original = chain.request();

                final HttpUrl originalHttpUrl = original.url();

                final HttpUrl url = originalHttpUrl.newBuilder()
//                        .addQueryParameter("username", "demo")
                        .build();

                // Request customization: add request headers
                final Request.Builder requestBuilder = original.newBuilder().addHeader("Content-TYPE", "application/json")
                        .url(url);

                final Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        });

        return httpClient.build();
    }
}
