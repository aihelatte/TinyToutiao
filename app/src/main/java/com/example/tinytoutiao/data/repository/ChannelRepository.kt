package com.example.tinytoutiao.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.tinytoutiao.data.model.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * 频道管理仓库
 * 职责：管理 "我的频道" 和 "推荐频道"
 * 存储：使用 SharedPreferences 保存 "我的频道" 的 code 列表 (例如 "general,sports,technology")
 */
class ChannelRepository(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("channel_prefs", Context.MODE_PRIVATE)
    private val KEY_MY_CHANNELS = "my_channel_codes"

    // 默认初始频道 (如果用户第一次打开 App)
    private val DEFAULT_CHANNELS = listOf("general", "technology", "sports", "entertainment")

    // --- 内存中的状态 (StateFlow) ---
    // 1. 我的频道
    private val _myChannels = MutableStateFlow<List<Channel>>(emptyList())
    val myChannels: StateFlow<List<Channel>> = _myChannels.asStateFlow()

    // 2. 推荐频道 (即：所有频道 - 我的频道)
    private val _otherChannels = MutableStateFlow<List<Channel>>(emptyList())
    val otherChannels: StateFlow<List<Channel>> = _otherChannels.asStateFlow()

    init {
        // 初始化时加载数据
        loadChannels()
    }

    private fun loadChannels() {
        // 1. 从本地读取已保存的 code 字符串 (用逗号分隔)
        val savedCodesStr = prefs.getString(KEY_MY_CHANNELS, null)

        val myCodes = if (savedCodesStr == null) {
            // 第一次安装，使用默认列表
            DEFAULT_CHANNELS
        } else {
            // 解析逗号分隔的字符串
            savedCodesStr.split(",").filter { it.isNotEmpty() }
        }

        // 2. 映射回 Channel 对象
        val all = Channel.ALL_CHANNELS.associateBy { it.code } // 转成 Map 方便查找

        val my = myCodes.mapNotNull { all[it] }.toMutableList()

        // 强制保障： "推荐(general)" 必须在第一个，且不能被删掉
        if (my.none { it.code == "general" }) {
            my.add(0, Channel("general", "推荐"))
        }

        // 3. 计算 "推荐频道" (剩余的)
        val others = Channel.ALL_CHANNELS.filter { it.code !in myCodes }

        // 4. 更新流
        _myChannels.value = my
        _otherChannels.value = others
    }

    /**
     * 添加频道 (从推荐 -> 我的)
     */
    fun addChannel(channel: Channel) {
        val currentMy = _myChannels.value.toMutableList()
        if (channel !in currentMy) {
            currentMy.add(channel)
            saveAndRefresh(currentMy)
        }
    }

    /**
     * 移除频道 (从我的 -> 推荐)
     * 注意：推荐(general) 不能删
     */
    fun removeChannel(channel: Channel) {
        if (channel.code == "general") return // 保护逻辑

        val currentMy = _myChannels.value.toMutableList()
        currentMy.remove(channel)
        saveAndRefresh(currentMy)
    }

    /**
     * 持久化保存并刷新流
     */
    private fun saveAndRefresh(newMyList: List<Channel>) {
        // 1. 存入 SP
        val codesStr = newMyList.joinToString(",") { it.code }
        prefs.edit().putString(KEY_MY_CHANNELS, codesStr).apply()

        // 2. 重新计算 other 列表
        val myCodes = newMyList.map { it.code }.toSet()
        val others = Channel.ALL_CHANNELS.filter { it.code !in myCodes }

        // 3. 发射新数据
        _myChannels.value = newMyList
        _otherChannels.value = others
    }
}