package com.example.tinytoutiao.ui.components

import android.widget.Toast
import androidx.compose.foundation.Image // 🔥 导入 Image 组件
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale // 🔥 导入缩放模式
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tinytoutiao.R

/**
 * 通用底部抽屉内容组件
 */
@Composable
fun ActionBottomSheetContent(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 32.dp)
    ) {
        // --- 1. 顶部把手 ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(4.dp)
                    .background(Color.LightGray, RoundedCornerShape(2.dp))
            )
        }

        // --- 2. 分享行 ---
        Text(
            text = "分享到",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        ShareRow(onItemClick = { name ->
            Toast.makeText(context, "已分享到：$name", Toast.LENGTH_SHORT).show()
            onDismiss()
        })

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 16.dp),
            thickness = 0.5.dp,
            color = Color.LightGray
        )

        // --- 3. 操作列表 ---
        ActionList(onItemClick = { action ->
            Toast.makeText(context, "操作成功：$action", Toast.LENGTH_SHORT).show()
            onDismiss()
        })

        // --- 4. 取消按钮 ---
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onDismiss() }
                .padding(vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("取消", fontSize = 16.sp)
        }
    }
}

// --- 分享部分 ---

data class ShareItem(val name: String, val iconResId: Int)

@Composable
fun ShareRow(onItemClick: (String) -> Unit) {
    val shareItems = listOf(
        // 🔥 修改：去掉了后面的 Color(...) 参数
        ShareItem("微信好友", R.drawable.ic_share_wechat),
        ShareItem("朋友圈", R.drawable.ic_share_moments),
        ShareItem("QQ好友", R.drawable.ic_share_qq),
        ShareItem("QQ空间", R.drawable.ic_share_qzone),
        ShareItem("抖音", R.drawable.ic_share_tiktok),
        ShareItem("复制链接", R.drawable.ic_share_link)
    )

    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        items(shareItems) { item ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable { onItemClick(item.name) }
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF5F5F5)),
                    contentAlignment = Alignment.Center
                ) {
                    // 🔥 核心修改：使用 Image 代替 Icon
                    Image(
                        painter = painterResource(id = item.iconResId),
                        contentDescription = item.name,
                        // 🔥 关键：使用 Fit 模式，让图片保持比例缩放放入框内，解决抖音过大问题
                        contentScale = ContentScale.Fit,
                        // 🔥 关键：Image 默认不染色，解决了朋友圈和QQ空间纯色问题
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = item.name, fontSize = 11.sp, color = Color.Gray)
            }
        }
    }
}

// --- 操作部分 (这部分保持不变，使用 Icon + 灰色 tint) ---
@Composable
fun ActionList(onItemClick: (String) -> Unit) {
    val actions = listOf(
        Pair(R.drawable.ic_action_dislike, "不感兴趣"),
        Pair(R.drawable.ic_action_report, "举报内容"),
        Pair(R.drawable.ic_action_block, "不看该作者")
    )

    Column {
        actions.forEach { (iconResId, text) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onItemClick(text) }
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = iconResId),
                    contentDescription = null,
                    tint = Color.Gray, // 这里依然需要灰色染色
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(text = text, fontSize = 15.sp, color = Color.Black)
            }
        }
    }
}