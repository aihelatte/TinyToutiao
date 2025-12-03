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
import retrofit2.HttpException
import java.io.IOException
import kotlin.random.Random

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
            // 1. 计算页码
            val page = when (loadType) {
                LoadType.REFRESH -> 1
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val itemCount = state.pages.sumOf { it.data.size }
                    val lastPage = itemCount / state.config.pageSize
                    lastPage + 1
                }
            }

            // 2. 获取数据 (分流处理：热榜走纯 Mock，其他走 网络 -> Mock 兜底)
            var articles = if (category == "hot") {
                // 热榜特殊通道：直接生成纯文字榜单，不请求网络
                generateHotRankData(page)
            } else {
                // 普通频道/搜索：尝试请求网络
                try {
                    val response = apiService.getTopHeadlines(
                        apiKey = "7dff73ac505c3cf795c88a8ef30e156c", // 你的 Key
                        page = page,
                        // 如果有搜索词，category 传 null (根据 GNews 规则)
                        category = if (query.isNullOrEmpty()) category else null,
                        query = query
                    )
                    response.articles
                } catch (e: Exception) {
                    // 网络失败或额度耗尽，返回空，触发兜底
                    emptyList()
                }
            }

            // 3. 备胎 Mock 逻辑 (非热榜频道，且网络没数据时)
            if (category != "hot" && articles.isEmpty()) {
                articles = generateMockData(page, category, query)
            }

            // 4. 存入数据库
            database.withTransaction {
                // 在清空数据库之前，先由 NewsDao 把所有“红心”新闻的 URL 取出来
                // (注意：这里我们查全表，对于 APPEND 操作，虽然不 clearAll，但也能防止重复数据覆盖掉点赞状态)
                val likedUrls = database.newsDao().getLikedArticleUrls()

                if (loadType == LoadType.REFRESH) {
                    database.newsDao().clearAll()

                    // 伪乱序优化
                    if (category != "hot") {
                        articles = articles.shuffled()
                    }
                }

                val baseTime = System.currentTimeMillis()

                // 转换 Entity 并插入
                val entities = articles.mapIndexedNotNull { index, dto ->
                    val entity = dto.toEntity()
                    entity?.copy(
                        createdAt = baseTime + index,
                        itemType = if (category == "hot") 3 else entity.itemType,

                        // 如果这条新闻的 URL 在名单里，强行把 isLiked 设为 true
                        isLiked = likedUrls.contains(dto.url)
                    )
                }
                database.newsDao().insertAll(entities)
            }

            // 永远返回 false，实现无限滚动
            MediatorResult.Success(endOfPaginationReached = false)

        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }
    }

    // --- 1. 普通 Mock 生成器 (带图，随机性强) ---
    private fun generateMockData(page: Int, category: String, query: String?): List<ArticleDto> {
        val mockList = mutableListOf<ArticleDto>()

        // 标题前缀
        val categoryName = when(category) {
            "general" -> "推荐"; "sports" -> "体育"; "technology" -> "科技"
            "entertainment" -> "娱乐"; "science" -> "科学"; "business" -> "财经"
            "health" -> "健康"; "world" -> "国际"; "nation" -> "国内"
            else -> "资讯"
        }
        val prefix = if (!query.isNullOrEmpty()) "搜索:$query" else categoryName

        // 本次请求的随机 ID (让每次刷新标题都不一样)
        val refreshRandomId = Random.nextInt(1000, 9999)

        for (i in 1..10) {
            mockList.add(
                ArticleDto(
                    title = "【$prefix】第${page}页：字节跳动工程训练营模拟新闻 No.$i (ID:$refreshRandomId)",
                    description = "当前模式：$prefix。由于API额度耗尽，系统自动切换为本地高性能模拟数据源...",
                    content = "这里是新闻正文内容...",
                    // 图片随机种子
                    imageUrl = "https://picsum.photos/400/300?random=${prefix.hashCode() + page * 10 + i + refreshRandomId}",
                    // URL 保持唯一性
                    url = "https://mock.com/$category/$page/$i/$refreshRandomId",
                    publishedAt = "2025-11-30",
                    source = SourceDto("MockSource", "Local")
                )
            )
        }
        return mockList
    }

    // ---2. 热榜 Mock 生成器 (纯文字，固定格式) ---
    private fun generateHotRankData(page: Int): List<ArticleDto> {
        val mockList = mutableListOf<ArticleDto>()
        val startRank = (page - 1) * 10 + 1

        // 模拟一些热搜词库
        val hotTopics = listOf(
            "字节跳动工程训练营结营", "DeepSeek发布新模型", "Android 16 预览版发布",
            "周杰伦新专辑", "原神新版本前瞻", "SpaceX 星舰发射",
            "英伟达股价新高", "阿根廷中国行", "考研倒计时", "冬至吃饺子"
        )

        for (i in 0 until 10) {
            val rank = startRank + i
            // 循环使用标题
            val topic = hotTopics[i % hotTopics.size]
            // 随机生成热度值 (比如 500.1 万)
            val heat = String.format("%.1f", (1000 - (rank % 100) * 10) + Math.random() * 10)

            mockList.add(
                ArticleDto(
                    // 格式： "1. 标题" (Item 会解析这个点号)
                    title = "$rank. $topic (实时热榜)",
                    // 把热度值存在 description 里，UI 层去取
                    description = heat,
                    content = "",
                    imageUrl = "", // 热榜没有图
                    url = "https://mock.hot/$rank/${System.currentTimeMillis()}", // 唯一URL
                    publishedAt = "",
                    source = SourceDto("Hot", "")
                )
            )
        }
        return mockList
    }
}