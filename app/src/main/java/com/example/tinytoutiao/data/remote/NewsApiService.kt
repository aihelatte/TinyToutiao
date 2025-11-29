package com.example.tinytoutiao.data.remote

import com.example.tinytoutiao.data.model.api.NewsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApiService {

    @GET("top-headlines")
    suspend fun getTopHeadlines(
        // 🔥 修复点 1：改为 String? (可空)，并给默认值 null
        @Query("category") category: String? = null,

        // 🔥 修复点 2：确保加上了 query 参数 (搜索用)
        @Query("q") query: String? = null,

        @Query("lang") lang: String = "zh",
        @Query("country") country: String = "cn",
        @Query("max") max: Int = 10,
        @Query("apikey") apiKey: String,
        @Query("page") page: Int
    ): NewsResponse
}