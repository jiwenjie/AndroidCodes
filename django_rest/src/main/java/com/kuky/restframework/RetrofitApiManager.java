package com.kuky.restframework;

import android.support.annotation.NonNull;
import android.util.Log;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author kuky
 * @description
 */
public class RetrofitApiManager {
    private static final String TAG = "RetrofitApiManager";
    private static final int CONN_TIME = 5;
    private static final int READ_TIME = 10;
    private static final int WRITE_TIME = 30;
    private static Retrofit mRetrofit;

    private RetrofitApiManager() {

    }

    public static Retrofit provideClient(String baseUrl, HashMap<String, String> headers) {
        if (mRetrofit == null) {
            synchronized (RetrofitApiManager.class) {
                if (mRetrofit == null) {
                    mRetrofit = new Retrofit.Builder()
                            .baseUrl(baseUrl)
                            .client(genericOkClient(headers))
                            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                }
            }
        }
        return mRetrofit;
    }

    /**
     * 创建 OkHttpClient 实例
     *
     * @param headers 添加头部信息
     * @return
     */
    private static OkHttpClient genericOkClient(final HashMap<String, String> headers) {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(@NonNull String message) {
                Log.i(RetrofitApiManager.class.getSimpleName(), "Interceptor message =>" + message);
            }
        });

        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        return new OkHttpClient.Builder()
                .connectTimeout(CONN_TIME, TimeUnit.SECONDS)
                .readTimeout(READ_TIME, TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIME, TimeUnit.SECONDS)
                .addNetworkInterceptor(loggingInterceptor)
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request.Builder builder = chain.request().newBuilder();
                        if (headers != null) {
                            for (String key : headers.keySet()) {
                                builder.addHeader(key, headers.get(key));
                            }
                        }
                        return chain.proceed(builder.build());
                    }
                }).build();
    }
}
