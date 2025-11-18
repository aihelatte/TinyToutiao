package com.example.tinytoutiao.data.model.db

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room 数据库表：articles
 * 我们使用新闻的 url 作为主键，因为它是唯一的。
 */
@Entity(tableName = "articles")
data class ArticleEntity(
    @PrimaryKey
    val url: String, // 使用 URL 作为唯一标识符

    val title: String,
    val description: String?,
    val content: String?,
    val imageUrl: String?,
    val publishedAt: String?,

    // 将 Source 对象拍平存储
    val sourceName: String?,
    val sourceUrl: String?,

    // 用于 Paging 3 的排序字段 (后续在 RemoteMediator 中会用到)
    // 记录插入数据库的时间，保证列表顺序不乱
    val createdAt: Long = System.currentTimeMillis()
)