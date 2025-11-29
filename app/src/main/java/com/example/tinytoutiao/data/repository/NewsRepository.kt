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

    // 1. 获取普通新闻流 (带频道)
    @OptIn(ExperimentalPagingApi::class)
    fun getNewsStream(category: String): Flow<PagingData<Article>> {
        return Pager(
            config = PagingConfig(pageSize = 10, prefetchDistance = 3, enablePlaceholders = false),
            remoteMediator = NewsRemoteMediator(apiService, database, category = category),
            pagingSourceFactory = { database.newsDao().getArticles() }
        ).flow.map { pagingData ->
            pagingData.map { it.toDomain() }
        }
    }

    // 2. 获取搜索新闻流
    @OptIn(ExperimentalPagingApi::class)
    fun searchNewsStream(query: String): Flow<PagingData<Article>> {
        return Pager(
            config = PagingConfig(pageSize = 10, prefetchDistance = 3, enablePlaceholders = false),
            remoteMediator = NewsRemoteMediator(apiService, database, query = query),
            pagingSourceFactory = { database.newsDao().getArticles() }
        ).flow.map { pagingData ->
            pagingData.map { it.toDomain() }
        }
    }

    // 3. 获取单条新闻 (用于详情页观察点赞状态)
    fun getArticleStream(url: String): Flow<Article?> {
        return database.newsDao().getArticle(url).map { it?.toDomain() }
    }

    // 4. 操作：标记已读
    suspend fun markAsViewed(url: String) {
        database.newsDao().markAsViewed(url)
    }

    // 5. 操作：切换点赞
    suspend fun toggleLike(url: String) {
        database.newsDao().toggleLike(url)
    }

    // 6. 获取历史记录
    fun getHistoryStream(): Flow<List<Article>> {
        return database.newsDao().getViewedArticles().map { list ->
            list.map { it.toDomain() }
        }
    }

    // 7. 获取收藏列表
    fun getFavoritesStream(): Flow<List<Article>> {
        return database.newsDao().getLikedArticles().map { list ->
            list.map { it.toDomain() }
        }
    }
}