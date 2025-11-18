package com.example.tinytoutiao.data.model

import com.example.tinytoutiao.data.model.api.ArticleDto
import com.example.tinytoutiao.data.model.db.ArticleEntity

/**
 * 业务层使用的纯净模型
 * UI 层只跟这个类打交道
 */
data class Article(
    val title: String,
    val description: String,
    val url: String,
    val imageUrl: String,
    val sourceName: String,
    val publishedAt: String
)

// --- Mappers (数据转换器) ---

/**
 * 将网络数据 (DTO) 转换为 数据库实体 (Entity)
 */
fun ArticleDto.toEntity(): ArticleEntity? {
    // 如果 url 或 title 为空，这天新闻是无效的，直接丢弃 (返回 null)
    if (url.isNullOrEmpty() || title.isNullOrEmpty()) return null

    return ArticleEntity(
        url = url,
        title = title,
        description = description,
        content = content,
        imageUrl = imageUrl,
        publishedAt = publishedAt,
        sourceName = source?.name,
        sourceUrl = source?.url
    )
}

/**
 * 将 数据库实体 (Entity) 转换为 业务模型 (Domain)
 */
fun ArticleEntity.toDomain(): Article {
    return Article(
        title = title,
        description = description ?: "暂无摘要",
        url = url,
        imageUrl = imageUrl ?: "", // 如果没有图片，给空字符串，UI 层会处理占位图
        sourceName = sourceName ?: "未知来源",
        publishedAt = publishedAt ?: ""
    )
}