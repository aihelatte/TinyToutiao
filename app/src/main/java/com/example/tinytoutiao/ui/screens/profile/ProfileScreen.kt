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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 个人中心屏幕
 * 更新：增加了 onHistoryClick 回调
 */
@Composable
fun ProfileScreen(
    onHistoryClick: () -> Unit // 🔥 新增回调
) {
    // 模拟登录状态
    var isLoggedIn by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        ProfileHeader(isLoggedIn) { isLoggedIn = !isLoggedIn }

        Spacer(modifier = Modifier.height(16.dp))

        // 我的收藏 (暂时还没有做收藏页，先留空)
        ProfileMenuItem(icon = Icons.Default.Favorite, title = "我的收藏", subtitle = "0 篇") {}
        HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray)

        // 浏览历史 (点击触发回调)
        ProfileMenuItem(
            icon = Icons.Default.List,
            title = "浏览历史",
            subtitle = "刚刚",
            onClick = onHistoryClick // 🔥 绑定点击事件
        )
        HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray)

        Spacer(modifier = Modifier.height(16.dp))

        ProfileMenuItem(icon = Icons.Default.Settings, title = "系统设置", subtitle = "清除缓存") {}
    }
}

// ... (ProfileHeader 和 ProfileMenuItem 组件代码保持不变，与之前一样)
// 为了文件完整性，我把下面的辅助组件也贴上，防止你直接复制覆盖后报错

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
            modifier = Modifier.size(72.dp).clip(CircleShape)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = if (isLoggedIn) "字节跳动练习生" else "点击登录",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
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
        Icon(imageVector = icon, contentDescription = null, tint = Color.DarkGray, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = title, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
        if (subtitle != null) {
            Text(text = subtitle, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        }
        Icon(imageVector = Icons.Default.KeyboardArrowRight, contentDescription = null, tint = Color.LightGray)
    }
}