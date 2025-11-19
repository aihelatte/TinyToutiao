package com.example.tinytoutiao.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private const val BASE_URL = "https://gnews.io/api/v4/"

    // 配置 OkHttpClient (添加日志拦截器)
    private val okHttpClient by lazy {
        val logging = HttpLoggingInterceptor().apply {
            // BODY 级别会打印请求头、请求体、响应头、响应体 (最详细)
            // 生产环境通常建议设为 NONE 或 BASIC，这里为了教学调试设为 BODY
            level = HttpLoggingInterceptor.Level.BODY
        }

        OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(15, TimeUnit.SECONDS) // 连接超时
            .readTimeout(15, TimeUnit.SECONDS)    // 读取超时
            .build()
    }

    // 创建 Retrofit 实例
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // 暴露 ApiService 给外部使用
    val service: NewsApiService by lazy {
        retrofit.create(NewsApiService::class.java)
    }
}