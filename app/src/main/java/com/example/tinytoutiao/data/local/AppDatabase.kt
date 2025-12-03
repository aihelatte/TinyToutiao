package com.example.tinytoutiao.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.tinytoutiao.data.local.converters.StringListConverter
import com.example.tinytoutiao.data.model.db.ArticleEntity

/**
 * 数据库总控中心 V2
 * 变化点：
 * 1. version = 2
 * 2. @TypeConverters 注册转换器
 */
@Database(
    entities = [ArticleEntity::class],
    version = 3, // ⚠️ 注意版本号升级
    exportSchema = false
)
@TypeConverters(StringListConverter::class) // 注册我们刚写的转换器
abstract class AppDatabase : RoomDatabase() {

    abstract fun newsDao(): NewsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "tinytoutiao_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}