package com.example.tinytoutiao.ui.screens.channel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.tinytoutiao.TinyToutiaoApplication
import com.example.tinytoutiao.data.model.Channel
import com.example.tinytoutiao.data.repository.ChannelRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class ChannelViewModel(
    private val repository: ChannelRepository
) : ViewModel() {

    // 我的频道 (UI 观察这个流)
    val myChannels = repository.myChannels.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // 推荐频道
    val otherChannels = repository.otherChannels.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // 动作：添加
    fun addChannel(channel: Channel) {
        repository.addChannel(channel)
    }

    // 动作：移除
    fun removeChannel(channel: Channel) {
        repository.removeChannel(channel)
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = (this[APPLICATION_KEY] as TinyToutiaoApplication)
                ChannelViewModel(app.container.channelRepository)
            }
        }
    }
}