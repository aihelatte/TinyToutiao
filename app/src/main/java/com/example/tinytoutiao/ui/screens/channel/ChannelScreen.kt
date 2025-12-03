package com.example.tinytoutiao.ui.screens.channel

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tinytoutiao.data.model.Channel

/**
 * 频道管理全屏页面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChannelScreen(
    viewModel: ChannelViewModel = viewModel(factory = ChannelViewModel.Factory),
    onBackClick: () -> Unit
) {
    // 收集 ViewModel 中的数据流
    val myChannels by viewModel.myChannels.collectAsState()
    val otherChannels by viewModel.otherChannels.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("频道管理", fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            // 使用 LazyVerticalGrid 实现网格布局
            LazyVerticalGrid(
                columns = GridCells.Fixed(4), // 一行 4 个
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                // --- 1. 我的频道 标题 ---
                item(span = { GridItemSpan(4) }) { // 占满一行
                    SectionHeader(title = "我的频道", subtitle = "点击进入频道")
                }

                // --- 2. 我的频道 列表 ---
                items(myChannels) { channel ->
                    ChannelItem(
                        channel = channel,
                        isMyChannel = true,
                        // 只有非 "推荐" 频道才能点击删除
                        onClick = {
                            if (channel.code != "general") {
                                viewModel.removeChannel(channel)
                            }
                        }
                    )
                }

                // --- 3. 频道推荐 标题 ---
                item(span = { GridItemSpan(4) }) {
                    Spacer(modifier = Modifier.height(24.dp))
                    SectionHeader(title = "频道推荐", subtitle = "点击添加频道")
                }

                // --- 4. 频道推荐 列表 ---
                items(otherChannels) { channel ->
                    ChannelItem(
                        channel = channel,
                        isMyChannel = false,
                        onClick = {
                            viewModel.addChannel(channel)
                        }
                    )
                }
            }
        }
    }
}

/**
 * 分区标题组件
 */
@Composable
fun SectionHeader(title: String, subtitle: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = subtitle, fontSize = 12.sp, color = Color.Gray)
    }
}

/**
 * 单个频道胶囊组件
 * @param isMyChannel true=我的频道(显示X或灰色), false=推荐频道(显示+)
 */
@Composable
fun ChannelItem(
    channel: Channel,
    isMyChannel: Boolean,
    onClick: () -> Unit
) {
    // 推荐(general) 热榜(hot)频道特殊处理：灰色背景，不可点击
    val isFixed = channel.code == "general" || channel.code == "hot"

    Surface(
        onClick = onClick,
        enabled = !isFixed || !isMyChannel, // 如果是固定的且在"我的"里，不可点击
        shape = RoundedCornerShape(4.dp),
        color = if (isFixed && isMyChannel) Color(0xFFF0F0F0) else Color(0xFFF5F5F5),
        modifier = Modifier.height(40.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (isMyChannel && !isFixed) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Remove",
                        modifier = Modifier.size(12.dp),
                        tint = Color.LightGray
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                }

                // 原有的加号逻辑 (推荐频道)
                if (!isMyChannel) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add",
                        modifier = Modifier.size(12.dp),
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                }

                Text(
                    text = channel.name,
                    fontSize = 14.sp,
                    color = if (isFixed && isMyChannel) Color.Gray else Color.Black
                )
            }
        }
    }
}