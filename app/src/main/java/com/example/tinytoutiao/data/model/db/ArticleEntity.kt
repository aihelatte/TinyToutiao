package com.example.tinytoutiao.data.model.db

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 数据库实体 V2.0
 * 对应本地数据库的 "articles" 表
 */
@Entity(tableName = "articles")
data class ArticleEntity(
    @PrimaryKey
    val url: String, // 唯一标识符

    val title: String,
    val description: String?,
    val content: String?,
    val imageUrl: String?, // 列表标准图
    val publishedAt: String?,
    val sourceName: String?,
    val sourceUrl: String?,

    // --- V2.0 新增字段 (核心秀肌肉点) ---

    /**
     * 卡片类型 (用于异构列表渲染)
     * 0 = 标准模式 (左文右图)
     * 1 = 三图模式 (图集)
     * 2 = 纯文模式 (快讯)
     */
    val itemType: Int = 0,

    /**
     * 图集数据 (仅当 itemType = 1 时使用)
     * 需要配合 StringListConverter 存储
     */
    val coverImages: List<String> = emptyList(),

    /**
     * 是否已读
     * true = 标题变灰
     */
    val isViewed: Boolean = false,

    /**
     * 阅读时间戳
     * 用于"历史记录"功能按时间排序
     */
    val viewedAt: Long? = null,

    /**
     * 是否收藏/点赞
     * true = 在"我的收藏"里显示
     */
    val isLiked: Boolean = false,

    // 插入时间 (用于分页排序)
    val createdAt: Long = System.currentTimeMillis()
)