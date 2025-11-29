package com.example.tinytoutiao.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.tinytoutiao.data.local.AppDatabase
import com.example.tinytoutiao.data.model.api.ArticleDto
import com.example.tinytoutiao.data.model.api.SourceDto
import com.example.tinytoutiao.data.model.db.ArticleEntity
import com.example.tinytoutiao.data.model.toEntity
import com.example.tinytoutiao.data.remote.NewsApiService

@OptIn(ExperimentalPagingApi::class)
class NewsRemoteMediator(
    private val apiService: NewsApiService,
    private val database: AppDatabase,
    private val category: String = "general", // 默认频道
    private val query: String? = null         // 搜索词 (可选)
) : RemoteMediator<Int, ArticleEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ArticleEntity>
    ): MediatorResult {
        return try {
            val page = when (loadType) {
                LoadType.REFRESH -> 1
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val itemCount = state.pages.sumOf { it.data.size }
                    val lastPage = itemCount / state.config.pageSize
                    lastPage + 1
                }
            }

            // 1. 尝试网络请求
            var articles = try {
                val response = apiService.getTopHeadlines(
                    apiKey = "7dff73ac505c3cf795c88a8ef30e156c", // 你的 Key
                    page = page,
                    // 如果有搜索词，就不传 category (GNews 规则通常如此，或者根据 API 文档调整)
                    category = if (query.isNullOrEmpty()) category else null,
                    query = query
                )
                response.articles
            } catch (e: Exception) {
                // 网络失败或额度耗尽 (403)，返回空列表，触发备胎逻辑
                emptyList()
            }

            // 2. 备胎逻辑：如果 API 没数据，生成 Mock 数据演示无限流
            if (articles.isEmpty()) {
                articles = generateMockData(page, category, query)
            }

            // 3. 存入数据库
            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    // 刷新时清空数据库 (简单粗暴模式，实际项目可能需要按频道分表)
                    database.newsDao().clearAll()
                }

                val baseTime = System.currentTimeMillis()

                // 转换 Entity 并插入
                val entities = articles.mapIndexedNotNull { index, dto ->
                    dto.toEntity()?.copy(
                        // 使用 "基准时间 + 索引" 保证绝对顺序，防止列表跳动
                        createdAt = baseTime + index
                    )
                }
                database.newsDao().insertAll(entities)
            }

            // 永远返回 false，假装还有更多数据 (无限滚动)
            MediatorResult.Success(endOfPaginationReached = false)

        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }

    // --- 🛠️ 模拟数据生成器 ---
    private fun generateMockData(page: Int, category: String, query: String?): List<ArticleDto> {
        val mockList = mutableListOf<ArticleDto>()

        // 标题前缀逻辑
        val categoryName = when(category) {
            "general" -> "推荐"; "sports" -> "体育"; "technology" -> "科技"
            "entertainment" -> "娱乐"; "science" -> "科学"; "business" -> "财经"
            "health" -> "健康"; "world" -> "国际"; "nation" -> "国内"
            else -> "资讯"
        }
        val prefix = if (!query.isNullOrEmpty()) "搜索:$query" else categoryName

        for (i in 1..10) {
            mockList.add(
                ArticleDto(
                    title = "【$prefix】第${page}页：字节跳动工程训练营模拟新闻 No.$i",
                    description = "当前模式：$prefix。由于API额度耗尽，系统自动切换为本地高性能模拟数据源，确保列表滑动丝滑流畅...",
                    content = "这里是新闻正文内容...",
                    // 图片随机种子加上 hash，保证不同频道图片不一样
                    imageUrl = "https://picsum.photos/400/300?random=${prefix.hashCode() + page * 10 + i}",
                    // 🔥 关键：URL 必须包含 page 和 i，甚至 category，防止主键冲突 (虽然后面用了自增ID，但 URL 还是做唯一好)
                    url = "https://mock.com/$category/$page/$i",
                    publishedAt = "2025-11-28",
                    source = SourceDto("MockSource", "Local")
                )
            )
        }
        return mockList
    }
}