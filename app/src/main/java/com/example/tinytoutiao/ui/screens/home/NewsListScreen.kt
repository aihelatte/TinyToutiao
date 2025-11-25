package com.example.tinytoutiao.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.tinytoutiao.ui.components.NewsItem

/**
 * 首页新闻列表屏幕 (完整版)
 * 包含：搜索栏 + 频道Tab + 新闻列表
 * 功能：
 * 1. 展示分页新闻
 * 2. 处理点击事件 (变灰 + 跳转)
 * 3. 处理加载状态 (Loading / Error)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsListScreen(
    viewModel: NewsViewModel = viewModel(factory = NewsViewModel.Factory),
    onNewsClick: (String) -> Unit
) {
    // 1. 收集分页数据流
    val newsItems = viewModel.newsPagingFlow.collectAsLazyPagingItems()

    // 模拟频道数据 (后续会从数据库读取)
    val channels = listOf("推荐", "热榜", "抗疫", "要闻", "新时代", "娱乐", "体育", "科技")
    var selectedTabIndex by remember { mutableIntStateOf(0) } // 当前选中的 Tab

    Scaffold(
        topBar = {
            Column {
                // 1. 顶部搜索栏
                HomeSearchBar()
                // 2. 频道 Tab 栏
                HomeChannelTabs(
                    channels = channels,
                    selectedIndex = selectedTabIndex,
                    onTabSelected = { index -> selectedTabIndex = index }
                )
            }
        }
    ) { innerPadding ->
        // 3. 新闻列表区域
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp)
        ) {
            // 渲染新闻列表
            items(newsItems.itemCount) { index ->
                val article = newsItems[index]
                if (article != null) {
                    NewsItem(
                        article = article,
                        onClick = {
                            // 🔥 核心修改点：
                            // 1. 先通知 ViewModel 更新数据库状态 (触发标题变灰)
                            viewModel.onNewsClicked(article.url)
                            // 2. 再执行跳转回调
                            onNewsClick(article.url)
                        }
                    )
                }
            }

            // 处理加载状态
            newsItems.apply {
                when {
                    // 首次加载时显示 Loading
                    loadState.refresh is LoadState.Loading -> {
                        item { LoadingItem() }
                    }
                    // 底部加载更多时显示 Loading
                    loadState.append is LoadState.Loading -> {
                        item { LoadingItem() }
                    }
                    // 首次加载失败
                    loadState.refresh is LoadState.Error -> {
                        item { ErrorItem("网络错误，点击重试") { retry() } }
                    }
                    // 加载更多失败
                    loadState.append is LoadState.Error -> {
                        item { ErrorItem("加载失败，点击重试") { retry() } }
                    }
                }
            }
        }
    }
}

// --- 组件：顶部搜索栏 ---
@Composable
fun HomeSearchBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp) // 标准 Toolbar 高度
            .background(MaterialTheme.colorScheme.primary) // 主题色背景 (通常是红色)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 伪造的搜索框 (点击跳转搜索页)
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White)
                .padding(horizontal = 12.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "搜你感兴趣的内容...",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // 右侧发布/更多按钮
        Text(
            text = "发布",
            color = Color.White,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
        )
    }
}

// --- 组件：频道 Tab 栏 ---
@Composable
fun HomeChannelTabs(
    channels: List<String>,
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 左侧可滑动 Tab
        ScrollableTabRow(
            selectedTabIndex = selectedIndex,
            edgePadding = 16.dp,
            containerColor = Color.White,
            contentColor = Color.Black, // 选中颜色
            divider = {}, // 去掉底部分割线
            indicator = { tabPositions ->
                // 使用 Material 3 的指示器
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedIndex]),
                    color = MaterialTheme.colorScheme.primary // 红色指示器
                )
            },
            modifier = Modifier.weight(1f)
        ) {
            channels.forEachIndexed { index, title ->
                Tab(
                    selected = selectedIndex == index,
                    onClick = { onTabSelected(index) },
                    text = {
                        Text(
                            text = title,
                            // 选中加粗，变大
                            fontSize = if (selectedIndex == index) 17.sp else 16.sp,
                            fontWeight = if (selectedIndex == index) androidx.compose.ui.text.font.FontWeight.Bold else androidx.compose.ui.text.font.FontWeight.Normal,
                            color = if (selectedIndex == index) MaterialTheme.colorScheme.primary else Color.Black
                        )
                    }
                )
            }
        }

        // 右侧频道管理按钮 (三道杠)
        IconButton(
            onClick = { /* TODO: 跳转频道管理 */ },
            modifier = Modifier.padding(horizontal = 4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = "Channels",
                tint = Color.Gray
            )
        }
    }
}

// --- 组件：加载指示器 ---
@Composable
fun LoadingItem() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(strokeWidth = 2.dp, color = Color.Gray)
    }
}

// --- 组件：错误重试按钮 ---
@Composable
fun ErrorItem(msg: String, onRetry: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onRetry() }
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = msg, color = Color.Red)
    }
}