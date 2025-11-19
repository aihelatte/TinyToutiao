package com.example.tinytoutiao.data.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.tinytoutiao.data.model.db.ArticleEntity

@Dao
interface NewsDao {

    /**
     * 插入一组新闻
     * OnConflictStrategy.REPLACE: 如果插入的新闻 URL 已经存在，就覆盖旧的
     * 这保证了如果内容有更新，我们会显示最新的
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(articles: List<ArticleEntity>)

    /**
     * 查询所有新闻
     * 返回 PagingSource：这是专门给 Paging 3 库使用的对象
     * 它可以自动帮我们处理分页读取（比如数据库有1万条，它一次只读20条给UI，省内存）
     * ORDER BY createdAt DESC: 保证最新插入的新闻显示在最前面
     */
    @Query("SELECT * FROM articles")
    fun getArticles(): PagingSource<Int, ArticleEntity>

    /**
     * 清空所有缓存
     * 当用户下拉刷新时，我们需要先清空旧数据，再插入新数据
     */
    @Query("DELETE FROM articles")
    suspend fun clearAll()
}