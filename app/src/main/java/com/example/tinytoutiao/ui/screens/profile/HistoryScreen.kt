package com.example.tinytoutiao.ui.screens.profile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tinytoutiao.ui.components.NewsItem

/**
 * 浏览历史界面
 * 展示本地数据库中已读的新闻
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    // 使用 ProfileViewModel，它持有 historyFlow
    viewModel: ProfileViewModel = viewModel(factory = ProfileViewModel.Factory),
    onBackClick: () -> Unit,
    onNewsClick: (String) -> Unit
) {
    // 收集历史记录流 (List<Article>)
    // initialValue 在 ViewModel 里已经设为空列表
    val historyList by viewModel.historyFlow.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("浏览历史") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        if (historyList.isEmpty()) {
            // 空状态提示
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "暂无浏览记录", color = Color.Gray)
            }
        } else {
            // 渲染历史列表
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(
                    items = historyList,
                    key = { it.url } // 使用 URL 作为唯一键，优化性能
                ) { article ->
                    NewsItem(
                        article = article,
                        onClick = { onNewsClick(article.url) }
                    )
                }
            }
        }
    }
}