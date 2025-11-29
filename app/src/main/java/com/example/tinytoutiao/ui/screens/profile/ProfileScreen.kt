package com.example.tinytoutiao.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

/**
 * 个人中心屏幕 (完整版)
 */
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = viewModel(factory = ProfileViewModel.Factory),
    onHistoryClick: () -> Unit,
    onFavoritesClick: () -> Unit // 🔥 新增：收藏点击回调
) {
    // 模拟登录状态
    var isLoggedIn by remember { mutableStateOf(false) }

    // 🔥 实时收集收藏数据流
    val favorites by viewModel.favoritesFlow.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        ProfileHeader(isLoggedIn) { isLoggedIn = !isLoggedIn }

        Spacer(modifier = Modifier.height(16.dp))

        // 1. 我的收藏
        ProfileMenuItem(
            icon = Icons.Default.Favorite,
            title = "我的收藏",
            subtitle = "${favorites.size} 篇", // 🔥 动态显示数量
            onClick = {
                onFavoritesClick() // 跳转到收藏列表
            }
        )
        HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray)

        // 2. 浏览历史
        ProfileMenuItem(
            icon = Icons.Default.List,
            title = "浏览历史",
            subtitle = "刚刚",
            onClick = onHistoryClick
        )
        HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray)

        Spacer(modifier = Modifier.height(16.dp))

        // 3. 系统设置
        ProfileMenuItem(
            icon = Icons.Default.Settings,
            title = "系统设置",
            subtitle = "清除缓存",
            onClick = { /* TODO: 清除缓存逻辑 */ }
        )
    }
}

@Composable
fun ProfileHeader(isLoggedIn: Boolean, onLoginClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .clickable { onLoginClick() }
            .padding(24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = null,
            tint = if (isLoggedIn) MaterialTheme.colorScheme.primary else Color.Gray,
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = if (isLoggedIn) "字节跳动练习生" else "点击登录",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (isLoggedIn) "头条号: 20251206" else "登录后体验更多功能",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.DarkGray,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        if (subtitle != null) {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }
        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = null,
            tint = Color.LightGray
        )
    }
}