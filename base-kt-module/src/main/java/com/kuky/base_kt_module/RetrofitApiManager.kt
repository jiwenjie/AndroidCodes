package com.kuky.base_kt_module

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * @author kuky.
 * @description
 */
object RetrofitApiManager {
    private const val TAG = "RetrofitApiManager"
    private const val CONN_TIME = 5
    private const val READ_TIME = 10
    private const val WRITE_TIME = 30
    private var mRetrofit: Retrofit? = null

    fun provideClient(baseUrl: String): Retrofit {
        if (mRetrofit == null) {
            mRetrofit = Retrofit.Builder()
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(genericOkClient())
                    .build()
        }

        return mRetrofit!!
    }

    private fun genericOkClient(): OkHttpClient {
        val httpLoggingInterceptor = HttpLoggingInterceptor {
            HttpLoggingInterceptor.Logger { message -> Log.i(TAG, message) }
        }

        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        return OkHttpClient.Builder()
                .connectTimeout(CONN_TIME.toLong(), TimeUnit.SECONDS)
                .readTimeout(READ_TIME.toLong(), TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIME.toLong(), TimeUnit.SECONDS)
                .addInterceptor(httpLoggingInterceptor)
                .build()
    }
}