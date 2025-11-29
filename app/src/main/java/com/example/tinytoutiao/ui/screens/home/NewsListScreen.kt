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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.tinytoutiao.data.model.Channel
import com.example.tinytoutiao.ui.components.ActionBottomSheetContent
import com.example.tinytoutiao.ui.components.NewsItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsListScreen(
    viewModel: NewsViewModel = viewModel(factory = NewsViewModel.Factory),
    onNewsClick: (String) -> Unit,
    onChannelManageClick: () -> Unit,
    onSearchClick: () -> Unit
) {
    // 数据流
    val newsItems = viewModel.newsPagingFlow.collectAsLazyPagingItems()
    val channels by viewModel.myChannels.collectAsState()

    // Tab 状态
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    // 🔥 抽屉状态
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    // 🔥 记录当前操作的是哪条新闻 (备用，虽然只弹Toast)
    var selectedArticleUrl by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            Column {
                HomeSearchBar(onClick = onSearchClick)
                if (channels.isNotEmpty()) {
                    HomeChannelTabs(
                        channels = channels,
                        selectedIndex = selectedTabIndex,
                        onTabSelected = { index ->
                            selectedTabIndex = index
                            if (index < channels.size) {
                                viewModel.onCategoryChange(channels[index].code)
                            }
                        },
                        onManageClick = onChannelManageClick
                    )
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(
                count = newsItems.itemCount,
                key = { index ->
                    val item = newsItems.peek(index)
                    if (item != null) "${item.url}-$index" else index
                }
            ) { index ->
                val article = newsItems[index]
                if (article != null) {
                    NewsItem(
                        article = article,
                        onClick = {
                            viewModel.onNewsClicked(article.url)
                            onNewsClick(article.url)
                        },
                        // 🔥 绑定三个点事件
                        onMoreClick = {
                            selectedArticleUrl = article.url
                            showBottomSheet = true
                        }
                    )
                }
            }

            item {
                when (newsItems.loadState.append) {
                    is LoadState.Loading -> LoadingItem()
                    is LoadState.Error -> ErrorItem("加载失败") { newsItems.retry() }
                    else -> {}
                }
            }
        }

        // 🔥 底部抽屉
        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState,
                containerColor = Color.White
            ) {
                ActionBottomSheetContent(
                    onDismiss = { showBottomSheet = false }
                )
            }
        }
    }
}

// --- 以下组件保持原有逻辑不变 ---
@Composable
fun HomeSearchBar(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(MaterialTheme.colorScheme.primary)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White)
                .clickable { onClick() }
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
                Text("搜你感兴趣的内容...", color = Color.Gray, fontSize = 14.sp)
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text("发布", color = Color.White, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun HomeChannelTabs(
    channels: List<Channel>,
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit,
    onManageClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ScrollableTabRow(
            selectedTabIndex = selectedIndex,
            edgePadding = 16.dp,
            containerColor = Color.White,
            contentColor = Color.Black,
            divider = {},
            indicator = { tabPositions ->
                if (selectedIndex < tabPositions.size) {
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedIndex]),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            },
            modifier = Modifier.weight(1f)
        ) {
            channels.forEachIndexed { index, channel ->
                Tab(
                    selected = selectedIndex == index,
                    onClick = { onTabSelected(index) },
                    text = {
                        Text(
                            text = channel.name,
                            fontSize = if (selectedIndex == index) 17.sp else 16.sp,
                            fontWeight = if (selectedIndex == index) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedIndex == index) MaterialTheme.colorScheme.primary else Color.Black
                        )
                    }
                )
            }
        }

        IconButton(
            onClick = onManageClick,
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

@Composable
fun LoadingItem() {
    Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(strokeWidth = 2.dp, color = Color.Gray)
    }
}

@Composable
fun ErrorItem(msg: String, onRetry: () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth().clickable { onRetry() }.padding(16.dp), contentAlignment = Alignment.Center) {
        Text(msg, color = Color.Red)
    }
}