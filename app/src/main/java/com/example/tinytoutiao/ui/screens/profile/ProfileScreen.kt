package com.example.tinytoutiao.ui.screens.profile

import androidx.compose.foundation.Image
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tinytoutiao.R

// 再次定义头条红 (确保颜色一致)
val ToutiaoRed = Color(0xFFFF3D3C)

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = viewModel(factory = ProfileViewModel.Factory),
    onHistoryClick: () -> Unit,
    onFavoritesClick: () -> Unit
) {
    // 模拟登录状态
    var isLoggedIn by remember { mutableStateOf(false) }

    // 实时收集收藏数据流
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
            subtitle = "${favorites.size} 篇",
            onClick = onFavoritesClick
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
        if (isLoggedIn) {
            Image(
                painter = painterResource(id = R.drawable.ic_toutiao_logo), // 使用你导入的 Logo
                contentDescription = "Avatar",
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape) // 裁切成圆形
            )
        } else {
            // 未登录显示默认灰头
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = if (isLoggedIn) "字节跳动练习生" else "点击登录",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (isLoggedIn) "头条号: 20251210" else "登录后体验更多功能",
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
        // 图标改为红色
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = ToutiaoRed,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))

        // 标题文字改为红色
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f),
            fontWeight = FontWeight.Medium
        )

        if (subtitle != null) {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray // 副标题通常保持灰色，以免视觉太杂
            )
        }
        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = null,
            tint = Color.LightGray
        )
    }
}