package com.example.tinytoutiao.data.remote

import com.example.tinytoutiao.data.model.api.NewsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApiService {

    /**
     * 获取头条新闻
     * 对应 URL: https://gnews.io/api/v4/top-headlines
     * 示例: https://gnews.io/api/v4/top-headlines?category=general&lang=zh&country=cn&max=10&apikey=...
     */
    @GET("top-headlines")
    suspend fun getTopHeadlines(
        @Query("category") category: String = "general",
        @Query("lang") lang: String = "zh",
        @Query("country") country: String = "cn",
        @Query("max") max: Int = 10,
        @Query("apikey") apiKey: String
    ): NewsResponse
}