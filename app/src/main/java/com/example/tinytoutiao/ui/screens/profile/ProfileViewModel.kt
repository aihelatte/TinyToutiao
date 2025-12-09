package com.example.tinytoutiao.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.tinytoutiao.TinyToutiaoApplication
import com.example.tinytoutiao.data.repository.NewsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class ProfileViewModel(
    private val repository: NewsRepository
) : ViewModel() {

    /**
     * 浏览历史流
     * stateIn: 将冷流转换为热流，UI 订阅时立即获得最新数据
     */
    val historyFlow = repository.getHistoryStream()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    /**
     * 我的收藏流
     */
    val favoritesFlow = repository.getFavoritesStream()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as TinyToutiaoApplication)
                val repository = application.container.newsRepository
                ProfileViewModel(repository)
            }
        }
    }
}