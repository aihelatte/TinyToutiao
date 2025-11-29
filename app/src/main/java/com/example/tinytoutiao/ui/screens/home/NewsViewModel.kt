package com.example.tinytoutiao.ui.screens.home

import kotlinx.coroutines.flow.Flow

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.paging.cachedIn
import com.example.tinytoutiao.TinyToutiaoApplication
import com.example.tinytoutiao.data.repository.ChannelRepository
import com.example.tinytoutiao.data.repository.NewsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.example.tinytoutiao.data.model.Article

/**
 * 首页 ViewModel (重构版)
 * 职责：
 * 1. 管理频道 Tab 数据
 * 2. 根据当前选中的频道，切换 Paging 数据流
 * 3. 处理点击交互
 */
class NewsViewModel(
    private val newsRepository: NewsRepository,
    private val channelRepository: ChannelRepository // 🔥 新增：频道仓库
) : ViewModel() {

    // 1. 真实的频道列表 (供 UI 显示 Tab)
    val myChannels = channelRepository.myChannels.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // 2. 当前选中的频道代码 (默认为 "general")
    private val _selectedCategory = MutableStateFlow("general")

    // 3. 🔥 动态 Paging 数据流
    // 当 _selectedCategory 变化时，flatMapLatest 会取消旧的流，创建新的流
    // 这意味着：切换频道 -> 数据库清空 -> 加载新频道数据
    @OptIn(ExperimentalCoroutinesApi::class)
    val newsPagingFlow = _selectedCategory.flatMapLatest { category ->
        // ⚠️ 注意：这里需要 NewsRepository 支持接收 category 参数
        newsRepository.getNewsStream(category)
    }.cachedIn(viewModelScope)

    // 动作：切换频道
    fun onCategoryChange(category: String) {
        if (_selectedCategory.value != category) {
            _selectedCategory.value = category
        }
    }

    // 动作：点击新闻 (变灰)
    fun onNewsClicked(url: String) {
        viewModelScope.launch {
            newsRepository.markAsViewed(url)
        }
    }

    // 获取文章详情流
    fun getArticle(url: String): Flow<Article?> {
        return newsRepository.getArticleStream(url)
    }

    // 点赞 (之前只有 onNewsClicked，现在补上点赞)
    fun toggleLike(url: String) {
        viewModelScope.launch {
            newsRepository.toggleLike(url)
        }
    }

    // 依赖注入工厂
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = (this[APPLICATION_KEY] as TinyToutiaoApplication)
                NewsViewModel(
                    app.container.newsRepository,
                    app.container.channelRepository // 🔥 注入 ChannelRepository
                )
            }
        }
    }
}