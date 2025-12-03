package com.example.tinytoutiao.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.tinytoutiao.data.model.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ChannelRepository(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("channel_prefs", Context.MODE_PRIVATE)
    private val KEY_MY_CHANNELS = "my_channel_codes"

    private val DEFAULT_CHANNELS = listOf("general", "hot", "technology", "sports", "entertainment")

    private val _myChannels = MutableStateFlow<List<Channel>>(emptyList())
    val myChannels: StateFlow<List<Channel>> = _myChannels.asStateFlow()

    private val _otherChannels = MutableStateFlow<List<Channel>>(emptyList())
    val otherChannels: StateFlow<List<Channel>> = _otherChannels.asStateFlow()

    init {
        loadChannels()
    }

    private fun loadChannels() {
        val savedCodesStr = prefs.getString(KEY_MY_CHANNELS, null)

        val myCodes = if (savedCodesStr == null) {
            DEFAULT_CHANNELS
        } else {
            savedCodesStr.split(",").filter { it.isNotEmpty() }
        }.toMutableList()

        val all = Channel.ALL_CHANNELS.associateBy { it.code }
        val my = myCodes.mapNotNull { all[it] }.toMutableList()


        // 1. 确保 "推荐" 存在且在第 0 位
        val general = Channel("general", "推荐")
        my.removeAll { it.code == "general" } // 先删掉可能存在的乱序位置
        my.add(0, general)

        // 2. 确保 "热榜" 存在且在第 1 位
        val hot = Channel("hot", "热榜")
        my.removeAll { it.code == "hot" } // 先删掉
        if (my.size >= 1) {
            my.add(1, hot) // 插在推荐后面
        } else {
            my.add(hot)
        }

        // 3. 计算剩余频道
        val currentCodes = my.map { it.code }.toSet()
        val others = Channel.ALL_CHANNELS.filter { it.code !in currentCodes }

        _myChannels.value = my
        _otherChannels.value = others
    }

    fun addChannel(channel: Channel) {
        val currentMy = _myChannels.value.toMutableList()
        if (channel !in currentMy) {
            currentMy.add(channel)
            saveAndRefresh(currentMy)
        }
    }

    fun removeChannel(channel: Channel) {
        if (channel.code == "general" || channel.code == "hot") return

        val currentMy = _myChannels.value.toMutableList()
        currentMy.remove(channel)
        saveAndRefresh(currentMy)
    }

    private fun saveAndRefresh(newMyList: List<Channel>) {
        val codesStr = newMyList.joinToString(",") { it.code }
        prefs.edit().putString(KEY_MY_CHANNELS, codesStr).apply()

        val myCodes = newMyList.map { it.code }.toSet()
        val others = Channel.ALL_CHANNELS.filter { it.code !in myCodes }

        _myChannels.value = newMyList
        _otherChannels.value = others
    }
}