package com.example.tinytoutiao.data.model.db

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 数据库实体 V2.0
 * 对应本地数据库的 "articles" 表
 */
@Entity(tableName = "articles")
data class ArticleEntity(
    // 这样同一条新闻 URL 可以被多次存入数据库（实现无限列表）
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val url: String,

    val title: String,
    val description: String?,
    val content: String?,
    val imageUrl: String?,
    val publishedAt: String?,
    val sourceName: String?,
    val sourceUrl: String?,
    val itemType: Int = 0,
    val coverImages: List<String> = emptyList(),
    val isViewed: Boolean = false,
    val viewedAt: Long? = null,
    val isLiked: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)