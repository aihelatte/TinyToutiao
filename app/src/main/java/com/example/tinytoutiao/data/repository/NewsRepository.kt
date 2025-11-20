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

/**
 * 仓库层 (Repository) - 单一数据源入口
 * 职责：
 * 1. 配置 Paging 3 的加载策略 (每页几条)。
 * 2. 组装 RemoteMediator (大脑) 和 PagingSource (数据库)。
 * 3. 将底层数据库实体 (Entity) 转换为 上层业务对象 (Domain Model)。
 */
class NewsRepository(
    private val database: AppDatabase
) {
    // 获取网络服务实例
    private val apiService = RetrofitClient.service

    /**
     * 获取新闻流
     * 返回类型 Flow<PagingData<Article>>：
     */
    @OptIn(ExperimentalPagingApi::class)
    fun getNewsStream(): Flow<PagingData<Article>> {
        return Pager(
            // 1. 配置工厂：定义分页行为
            config = PagingConfig(
                pageSize = 10, // 每页加载 10 条数据
                enablePlaceholders = false, // 不显示占位符 (还没加载出来时显示灰色方块)，我们简化处理设为 false
                initialLoadSize = 10 // 第一次加载的数量
            ),
            // 2. 注入大脑：处理网络和本地的协调逻辑 (包含你的随机算法)
            remoteMediator = NewsRemoteMediator(
                apiService = apiService,
                database = database
            ),
            // 3. 定义源头：数据真正从哪里来？从 Room 数据库来！
            pagingSourceFactory = { database.newsDao().getArticles() }
        ).flow.map { pagingData ->
            // 4. 数据转换：Entity -> Domain Model
            // 这一步让 ViewModel 拿到的数据是纯净的，不包含任何数据库实现细节
            pagingData.map { entity -> entity.toDomain() }
        }
    }
}