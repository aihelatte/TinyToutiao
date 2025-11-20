package com.example.tinytoutiao.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.tinytoutiao.data.local.AppDatabase
import com.example.tinytoutiao.data.model.db.ArticleEntity
import com.example.tinytoutiao.data.model.toEntity
import com.example.tinytoutiao.data.remote.NewsApiService
import retrofit2.HttpException
import java.io.IOException

/**
 * 核心大脑：NewsRemoteMediator
 * 职责：协调 数据库 (Room) 和 网络 (Retrofit)
 */
@OptIn(ExperimentalPagingApi::class)
class NewsRemoteMediator(
    private val apiService: NewsApiService,
    private val database: AppDatabase
) : RemoteMediator<Int, ArticleEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ArticleEntity>
    ): MediatorResult {
        return try {
            // 1. 计算页码逻辑 (保持不变)
            val page = when (loadType) {
                LoadType.REFRESH -> 1
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val itemCount = state.pages.sumOf { it.data.size }
                    val lastPage = itemCount / state.config.pageSize
                    lastPage + 1
                }
            }

            // 2. 发起网络请求 (Retrofit 开始工作)
            // 这个方法是 suspend (挂起) 的，代码运行到这行会暂停，直到服务器返回数据
            val response = apiService.getTopHeadlines(
                apiKey = "7dff73ac505c3cf795c88a8ef30e156c",
            )

            val articles = response.articles
            val endOfPaginationReached = articles.isEmpty()

            // 3. 数据库事务 (存入 Room)
            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    // 如果是下拉刷新，先清空旧缓存
                    database.newsDao().clearAll()
                }

                // --- 核心修改点：伪随机排序 ---
                // articles: 网络返回的原始顺序 (按时间)
                // shuffled(): Kotlin 自带的洗牌函数，打乱这 10 条的顺序
                // mapNotNull: 转换成数据库实体，并过滤无效数据
                val entities = articles.shuffled().mapNotNull { it.toEntity() }

                // 将打乱后的数据插入数据库
                // 因为我们是一次性插入，Room 会按这个顺序存储，
                // 之后 UI 读取时也就是这个随机后的顺序了。
                database.newsDao().insertAll(entities)
            }

            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)

        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }
    }
}