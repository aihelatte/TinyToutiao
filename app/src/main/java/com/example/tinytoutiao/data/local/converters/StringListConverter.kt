package com.example.tinytoutiao.data.local.converters

import androidx.room.TypeConverter

/**
 * Room 类型转换器
 * 作用：让数据库能够存储 List<String> 类型的字段 (比如三图模式的图片列表)
 * 原理：存的时候把 List 拼成字符串 "url1,url2,url3"，取的时候再劈开。
 */
class StringListConverter {
    @TypeConverter
    fun fromString(value: String?): List<String> {
        // 如果是空的，返回空列表；否则按逗号分割
        return value?.split(",")?.map { it.trim() } ?: emptyList()
    }

    @TypeConverter
    fun fromList(list: List<String>?): String {
        // 把列表用逗号连接成一个字符串
        return list?.joinToString(",") ?: ""
    }
}