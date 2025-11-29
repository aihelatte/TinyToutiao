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
    // ... insertAll, getArticles, clearAll 保持不变 ...

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(articles: List<ArticleEntity>)

    @Query("DELETE FROM articles")
    suspend fun clearAll()

    // 这里的 ORDER BY 记得保持 ASC (上次改的)
    @Query("SELECT * FROM articles ORDER BY createdAt ASC")
    fun getArticles(): PagingSource<Int, ArticleEntity>

    // --- 状态更新 (逻辑无需大改，因为 WHERE url = :url 会自动匹配所有重复项) ---

    @Query("UPDATE articles SET isViewed = 1, viewedAt = :timestamp WHERE url = :url")
    suspend fun markAsViewed(url: String, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE articles SET isLiked = CASE WHEN isLiked = 1 THEN 0 ELSE 1 END WHERE url = :url")
    suspend fun toggleLike(url: String)

    // ... 其他 Flow 方法保持不变 ...
    @Query("SELECT * FROM articles WHERE isViewed = 1 ORDER BY viewedAt DESC")
    fun getViewedArticles(): Flow<List<ArticleEntity>>

    @Query("SELECT * FROM articles WHERE isLiked = 1 ORDER BY createdAt DESC")
    fun getLikedArticles(): Flow<List<ArticleEntity>>

    @Query("SELECT * FROM articles WHERE url = :url")
    fun getArticle(url: String): Flow<ArticleEntity?>
}