package jingya.com.base_class_module;

import android.content.RestrictionsManager;
import android.support.annotation.NonNull;


import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import jingya.com.base_class_module.BaseUtils.LogUtils;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author Kuky
 *         <p>
 *         Retrofit 管理类
 */
public class RetrofitApiManager {
    private static final String TAG = "RetrofitApiManager";
    private static final int CONN_TIME = 5;
    private static final int READ_TIME = 10;
    private static final int WRITE_TIME = 30;
    private static Retrofit mRetrofit;

    private RetrofitApiManager() {

    }

    /**
     * 带头部信息
     *
     * @param baseUrl
     * @param headers
     * @return
     */
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
     * 不带头部信息
     *
     * @param baseUrl
     * @return
     */
    public static Retrofit provideClient(String baseUrl) {
        if (mRetrofit == null) {
            synchronized (RetrofitApiManager.class) {
                if (mRetrofit == null) {
                    mRetrofit = new Retrofit.Builder()
                            .baseUrl(baseUrl)
                            .client(genericOkClient(null))
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
     * @param headers 添加头部信息 头部信息也可以通过 Retrofit Header 注解添加
     * @return
     */
    private static OkHttpClient genericOkClient(final HashMap<String, String> headers) {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(@NonNull String message) {
                LogUtils.i("Interceptor message =>" + message);
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
