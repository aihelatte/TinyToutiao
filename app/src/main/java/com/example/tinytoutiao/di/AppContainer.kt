package com.example.tinytoutiao.di

import android.content.Context
import com.example.tinytoutiao.data.local.AppDatabase
import com.example.tinytoutiao.data.repository.NewsRepository
import com.example.tinytoutiao.data.repository.ChannelRepository

/**
 * 依赖注入容器 (Dependency Injection Container)
 * 作用：集中管理全局单例。
 * 整个 App 只有一份 Database 和 Repository 实例，都在这里面。
 */
class AppContainer(context: Context) {

    // 1. 初始化数据库 (单例)
    private val database: AppDatabase by lazy {
        AppDatabase.getDatabase(context)
    }

    // 2. 初始化仓库 (单例)
    // 仓库依赖于数据库，所以我们把 database 传进去
    val newsRepository: NewsRepository by lazy {
        NewsRepository(database)
    }

    val channelRepository: ChannelRepository by lazy {
        ChannelRepository(context)
    }
}