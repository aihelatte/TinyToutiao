package com.example.tinytoutiao.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.tinytoutiao.data.model.db.ArticleEntity

/**
 * 数据库总控中心
 * entities: 告诉数据库有哪些表 (目前只有 ArticleEntity)
 * version: 1 (如果以后修改了 ArticleEntity 的字段，这里版本号要 +1)
 */
@Database(
    entities = [ArticleEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    // 暴露 Dao 给 Repository 使用
    abstract fun newsDao(): NewsDao

    companion object {
        // 单例模式：保证全 App 只有一个数据库连接，避免内存泄漏
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            // 双重检查锁定，确保线程安全
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "tinytoutiao_db" // 手机里生成的数据库文件名
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}