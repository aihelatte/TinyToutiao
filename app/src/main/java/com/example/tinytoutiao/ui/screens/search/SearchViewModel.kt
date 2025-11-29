package com.example.tinytoutiao.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.tinytoutiao.TinyToutiaoApplication
import com.example.tinytoutiao.data.model.Article
import com.example.tinytoutiao.data.repository.NewsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

class SearchViewModel(
    private val repository: NewsRepository
) : ViewModel() {

    // 搜索关键词
    val searchQuery = MutableStateFlow("")

    // 搜索结果流
    // 当 query 变化且不为空时，触发搜索
    @OptIn(ExperimentalCoroutinesApi::class)
    val searchResultFlow: Flow<PagingData<Article>> = searchQuery.flatMapLatest { query ->
        if (query.isBlank()) {
            flowOf(PagingData.empty()) // 空关键词不搜
        } else {
            repository.searchNewsStream(query)
        }
    }.cachedIn(viewModelScope)

    // 更新关键词
    fun onQueryChange(newQuery: String) {
        searchQuery.value = newQuery
    }

    // 点击新闻 (复用逻辑)
    fun onNewsClicked(url: String) {
        viewModelScope.launch {
            repository.markAsViewed(url)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = (this[APPLICATION_KEY] as TinyToutiaoApplication)
                SearchViewModel(app.container.newsRepository)
            }
        }
    }
}