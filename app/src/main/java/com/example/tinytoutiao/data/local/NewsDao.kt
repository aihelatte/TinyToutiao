package com.example.tinytoutiao.data.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.tinytoutiao.data.model.db.ArticleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NewsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(articles: List<ArticleEntity>)

    @Query("SELECT * FROM articles ORDER BY createdAt DESC")
    fun getArticles(): PagingSource<Int, ArticleEntity>

    @Query("DELETE FROM articles")
    suspend fun clearAll()

    // --- 状态更新 ---

    @Query("UPDATE articles SET isViewed = 1, viewedAt = :timestamp WHERE url = :url")
    suspend fun markAsViewed(url: String, timestamp: Long = System.currentTimeMillis())

    // 切换收藏状态 (如果原来是 1 改成 0，是 0 改成 1)
    @Query("UPDATE articles SET isLiked = CASE WHEN isLiked = 1 THEN 0 ELSE 1 END WHERE url = :url")
    suspend fun toggleLike(url: String)

    // --- 🔥 核心升级：返回 Flow 实现实时响应 ---

    // 获取浏览历史 (按阅读时间倒序)
    @Query("SELECT * FROM articles WHERE isViewed = 1 ORDER BY viewedAt DESC")
    fun getViewedArticles(): Flow<List<ArticleEntity>>

    // 获取我的收藏
    @Query("SELECT * FROM articles WHERE isLiked = 1 ORDER BY createdAt DESC")
    fun getLikedArticles(): Flow<List<ArticleEntity>>
}