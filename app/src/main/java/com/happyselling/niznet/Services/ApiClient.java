package com.happyselling.niznet.Services;

import android.content.Context;

import com.readystatesoftware.chuck.ChuckInterceptor;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    public static Retrofit getClient(String baseUrl, Context context){
        Retrofit retrofit = null;
//        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
//        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
//                .addInterceptor(new ChuckInterceptor(context))
                .connectTimeout(2, TimeUnit.MINUTES)
                .writeTimeout(2,TimeUnit.MINUTES)
                .readTimeout(2,TimeUnit.MINUTES);

        if(retrofit == null) {
            retrofit = new Retrofit.Builder().baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(httpClient.build())
                    .build();
        }
        return retrofit;
    }

    public static Retrofit getClientWithToken(String baseUrl, final String token, Context context) {
        Retrofit retrofit = null;
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
//                .addInterceptor(new ChuckInterceptor(context))
                .connectTimeout(2, TimeUnit.MINUTES)
                .writeTimeout(2,TimeUnit.MINUTES)
                .readTimeout(2,TimeUnit.MINUTES);
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request().newBuilder().addHeader("Authorization", "Bearer "+token).build();
                return chain.proceed(request);
            }
        });
        if (retrofit == null) {
            retrofit = new Retrofit.Builder().baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(httpClient.build())
                    .build();
        }

        return retrofit;
    }
}
