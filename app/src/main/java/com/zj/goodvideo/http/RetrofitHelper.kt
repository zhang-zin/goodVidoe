package com.zj.goodvideo.http

import com.zj.goodvideo.BuildConfig
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitHelper {

    val apiServer: ApiServer =
        Retrofit.Builder()
            .baseUrl(AppUrl.API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(genericOKClient()).build()
            .create(ApiServer::class.java)

    private fun genericOKClient() = OkHttpClient.Builder()
        .addInterceptor(KtHttpLogInterceptor() {
            setTag("goodVideo")
            if (BuildConfig.DEBUG) {
                setLogLevel(KtHttpLogInterceptor.LogLevel.BODY)
            } else {
                setLogLevel(KtHttpLogInterceptor.LogLevel.NONE)
            }
        })
        /*.addInterceptor(HttpLoggingInterceptor().apply {
            if (BuildConfig.DEBUG) {
                setLevel(HttpLoggingInterceptor.Level.BODY)
            } else {
                setLevel(HttpLoggingInterceptor.Level.NONE)
            }
        })*/
        .connectTimeout(5_000L, TimeUnit.MILLISECONDS)
        .readTimeout(10_000, TimeUnit.MILLISECONDS)
        .writeTimeout(30_000, TimeUnit.MILLISECONDS)
        .build()
}