package com.example.tinytoutiao.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.example.tinytoutiao.data.local.AppDatabase
import com.example.tinytoutiao.data.model.Article
import com.example.tinytoutiao.data.model.toDomain
import com.example.tinytoutiao.data.paging.NewsRemoteMediator
import com.example.tinytoutiao.data.remote.RetrofitClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NewsRepository(
    private val database: AppDatabase
) {
    private val apiService = RetrofitClient.service

    @OptIn(ExperimentalPagingApi::class)
    fun getNewsStream(): Flow<PagingData<Article>> {
        return Pager(
            config = PagingConfig(pageSize = 10, enablePlaceholders = false),
            remoteMediator = NewsRemoteMediator(apiService, database),
            pagingSourceFactory = { database.newsDao().getArticles() }
        ).flow.map { pagingData ->
            pagingData.map { it.toDomain() }
        }
    }

    suspend fun markAsViewed(url: String) {
        database.newsDao().markAsViewed(url)
    }

    // --- 🔥 新增：收藏与历史逻辑 ---

    suspend fun toggleLike(url: String) {
        database.newsDao().toggleLike(url)
    }

    // 获取历史记录流 (Entity -> Domain)
    fun getHistoryStream(): Flow<List<Article>> {
        return database.newsDao().getViewedArticles().map { list ->
            list.map { it.toDomain() }
        }
    }

    // 获取收藏列表流
    fun getFavoritesStream(): Flow<List<Article>> {
        return database.newsDao().getLikedArticles().map { list ->
            list.map { it.toDomain() }
        }
    }
}