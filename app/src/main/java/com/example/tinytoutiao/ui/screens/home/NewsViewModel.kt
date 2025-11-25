package com.example.tinytoutiao.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.paging.cachedIn
import com.example.tinytoutiao.TinyToutiaoApplication
import com.example.tinytoutiao.data.repository.NewsRepository
import kotlinx.coroutines.launch

/**
 * 首页新闻列表的 ViewModel
 * 职责：
 * 1. 提供分页数据流
 * 2. 处理业务逻辑 (如点击事件)
 */
class NewsViewModel(
    private val repository: NewsRepository
) : ViewModel() {

    // 分页数据流
    val newsPagingFlow = repository.getNewsStream()
        .cachedIn(viewModelScope)

    // --- 🔥 之前缺失的方法：处理点击事件 ---
    // 这个方法负责调用仓库层去更新数据库状态 (变灰)
    fun onNewsClicked(url: String) {
        viewModelScope.launch {
            // 这一步是异步的，不需要等待它完成再跳转
            repository.markAsViewed(url)
        }
    }

    // 依赖注入工厂
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as TinyToutiaoApplication)
                val repository = application.container.newsRepository
                NewsViewModel(repository)
            }
        }
    }
}