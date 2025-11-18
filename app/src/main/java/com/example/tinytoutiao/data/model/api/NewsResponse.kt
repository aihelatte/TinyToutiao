package com.example.tinytoutiao.data.model.api

import com.google.gson.annotations.SerializedName

/**
 * 对应 GNews API 的顶层响应
 * {
 * "totalArticles": 10,
 * "articles": [ ... ]
 * }
 */
data class NewsResponse(
    @SerializedName("totalArticles") val totalArticles: Int,
    @SerializedName("articles") val articles: List<ArticleDto>
)

/**
 * 对应 JSON 中的单条新闻对象 (Data Transfer Object)
 * 使用 @SerializedName 确保字段名与 JSON 严格一致
 */
data class ArticleDto(
    @SerializedName("title") val title: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("content") val content: String?,
    @SerializedName("url") val url: String?,
    @SerializedName("image") val imageUrl: String?,
    @SerializedName("publishedAt") val publishedAt: String?,
    @SerializedName("source") val source: SourceDto?
)

data class SourceDto(
    @SerializedName("name") val name: String?,
    @SerializedName("url") val url: String?
)