package com.example.tinytoutiao.ui.screens.search

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.tinytoutiao.ui.components.NewsItem
import com.example.tinytoutiao.ui.screens.home.ErrorItem
import com.example.tinytoutiao.ui.screens.home.LoadingItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel = viewModel(factory = SearchViewModel.Factory),
    onBackClick: () -> Unit,
    onNewsClick: (String) -> Unit
) {
    val query by viewModel.searchQuery.collectAsState()
    val searchResults = viewModel.searchResultFlow.collectAsLazyPagingItems()
    val focusManager = LocalFocusManager.current

    Scaffold(
        topBar = {
            // 顶部搜索栏
            TopAppBar(
                title = {
                    TextField(
                        value = query,
                        onValueChange = { viewModel.onQueryChange(it) },
                        placeholder = { Text("搜索感兴趣的内容...") },
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onSearch = {
                            focusManager.clearFocus() // 点击软键盘搜索收起键盘
                        }),
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onQueryChange("") }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear")
                        }
                    } else {
                        IconButton(onClick = { /* 这里的点击逻辑其实由键盘触发 */ }) {
                            Icon(Icons.Default.Search, contentDescription = "Search")
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentPadding = PaddingValues(16.dp)
        ) {
            // 渲染搜索结果
            items(
                count = searchResults.itemCount,
                key = { index ->
                    val item = searchResults.peek(index)
                    if (item != null) "${item.url}-$index" else index
                }
            ) { index ->
                val article = searchResults[index]
                if (article != null) {
                    NewsItem(
                        article = article,
                        onClick = {
                            viewModel.onNewsClicked(article.url)
                            onNewsClick(article.url)
                        }
                    )
                }
            }

            // 加载状态
            item {
                when (searchResults.loadState.refresh) {
                    is LoadState.Loading -> LoadingItem()
                    is LoadState.Error -> ErrorItem("搜索失败") { searchResults.retry() }
                    else -> {
                        // 如果搜不到结果，且不在加载中
                        if (searchResults.itemCount == 0 && query.isNotEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxWidth().padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("没有找到相关内容", color = Color.Gray)
                            }
                        }
                    }
                }
            }
        }
    }
}