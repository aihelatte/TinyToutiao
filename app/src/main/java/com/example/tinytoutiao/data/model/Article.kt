package com.example.tinytoutiao.data.model

import com.example.tinytoutiao.data.model.api.ArticleDto
import com.example.tinytoutiao.data.model.db.ArticleEntity
import kotlin.random.Random

/**
 * Domain Model (业务模型)
 * UI 层直接使用这个对象进行渲染
 */
data class Article(
    val url: String,
    val title: String,
    val description: String,
    val imageUrl: String,
    val sourceName: String,
    val publishedAt: String,

    // --- UI 状态字段 ---
    val itemType: Int, // 0=标准, 1=三图, 2=纯文
    val coverImages: List<String>, // 三图模式用的图片列表
    val isViewed: Boolean, // 是否已读(变灰)
    val isLiked: Boolean   // 是否收藏(红心)
)

// --- Mappers (转换器) ---

/**
 * 网络数据(DTO) -> 数据库实体(Entity)
 * 🔥 核心逻辑：在这里进行数据的"伪装"和"随机化"
 */
fun ArticleDto.toEntity(): ArticleEntity? {
    // 过滤脏数据
    if (url.isNullOrEmpty() || title.isNullOrEmpty()) return null

    // 1. 随机生成卡片类型 (伪随机算法)
    // 0..6 (70%): 标准模式
    // 7..8 (20%): 三图模式
    // 9    (10%): 纯文模式
    val randomType = when (Random.nextInt(10)) {
        in 0..6 -> 0
        in 7..8 -> 1
        else -> 2
    }

    // 2. 构造三图数据
    // 因为 GNews 只有一张图，如果是三图模式，我们把这一张图复制 3 份来模拟
    // (或者你可以稍微改改 URL 参数来模拟不同图片，但这取决于图片服务)
    val imageList = if (randomType == 1 && !imageUrl.isNullOrEmpty()) {
        listOf(imageUrl, imageUrl, imageUrl)
    } else {
        emptyList()
    }

    return ArticleEntity(
        url = url,
        title = title,
        description = description,
        content = content,
        imageUrl = imageUrl,
        publishedAt = publishedAt,
        sourceName = source?.name,
        sourceUrl = source?.url,

        // 赋值新字段
        itemType = randomType,
        coverImages = imageList,
        isViewed = false, // 刚拉下来的新闻肯定没读过
        isLiked = false,
        viewedAt = null
    )
}

/**
 * 数据库实体(Entity) -> 业务模型(Domain)
 */
fun ArticleEntity.toDomain(): Article {
    return Article(
        url = url,
        title = title,
        description = description ?: "",
        imageUrl = imageUrl ?: "",
        sourceName = sourceName ?: "TinyNews",
        publishedAt = publishedAt ?: "",

        // 透传数据库里的状态给 UI
        itemType = itemType,
        coverImages = coverImages,
        isViewed = isViewed,
        isLiked = isLiked
    )
}