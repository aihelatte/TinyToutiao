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
 * 我的收藏页面
 * 展示数据库中 isLiked = true 的新闻
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    // 复用 ProfileViewModel
    viewModel: ProfileViewModel = viewModel(factory = ProfileViewModel.Factory),
    onBackClick: () -> Unit,
    onNewsClick: (String) -> Unit
) {
    // 🔥 核心区别：这里收集的是 favoritesFlow (收藏列表)
    val favoriteList by viewModel.favoritesFlow.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("我的收藏") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        if (favoriteList.isEmpty()) {
            // 空状态
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "还没有收藏任何内容", color = Color.Gray)
            }
        } else {
            // 列表内容
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(
                    items = favoriteList,
                    key = { it.url } // 唯一键
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