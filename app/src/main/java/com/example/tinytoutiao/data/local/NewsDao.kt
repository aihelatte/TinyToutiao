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

    @Query("DELETE FROM articles")
    suspend fun clearAll()

    @Query("SELECT * FROM articles ORDER BY createdAt ASC")
    fun getArticles(): PagingSource<Int, ArticleEntity>

    @Query("UPDATE articles SET isViewed = 1, viewedAt = :timestamp WHERE url = :url")
    suspend fun markAsViewed(url: String, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE articles SET isLiked = CASE WHEN isLiked = 1 THEN 0 ELSE 1 END WHERE url = :url")
    suspend fun toggleLike(url: String)

    @Query("SELECT * FROM articles WHERE isViewed = 1 ORDER BY viewedAt DESC")
    fun getViewedArticles(): Flow<List<ArticleEntity>>

    @Query("SELECT * FROM articles WHERE isLiked = 1 ORDER BY createdAt DESC")
    fun getLikedArticles(): Flow<List<ArticleEntity>>

    @Query("SELECT * FROM articles WHERE url = :url")
    fun getArticle(url: String): Flow<ArticleEntity?>

    @Query("SELECT url FROM articles WHERE isLiked = 1")
    suspend fun getLikedArticleUrls(): List<String>
}