package com.example.tinytoutiao.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.tinytoutiao.data.model.Article

/**
 * 智能新闻卡片组件 (Heterogeneous Item)
 * 职责：根据 article.itemType 自动选择渲染模式
 */
@Composable
fun NewsItem(
    article: Article,
    onClick: () -> Unit,
    onMoreClick: () -> Unit = {} // 🔥 新增：更多操作回调
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // 根据 itemType 分发到不同的子组件
        when (article.itemType) {
            1 -> ThreeImagesNewsItem(article) // 三图模式
            2 -> TextOnlyNewsItem(article)    // 纯文模式
            else -> StandardNewsItem(article) // 默认标准模式
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 底部信息栏 (来源、时间、更多)
        NewsMetaInfo(article, onMoreClick)

        Spacer(modifier = Modifier.height(8.dp))
        // 分割线
        HorizontalDivider(color = Color.LightGray, thickness = 0.5.dp)
    }
}

// --- 1. 标准模式 (左文右图) ---
@Composable
fun StandardNewsItem(article: Article) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // 左侧标题
        Text(
            text = article.title,
            style = MaterialTheme.typography.titleMedium,
            color = if (article.isViewed) Color.Gray else Color.Black,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f).padding(end = 8.dp)
        )

        // 右侧图片 (如果 URL 不为空)
        if (article.imageUrl.isNotEmpty()) {
            AsyncImage(
                model = article.imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(100.dp, 70.dp)
                    .clip(RoundedCornerShape(4.dp))
            )
        }
    }
}

// --- 2. 三图模式 (图集) ---
@Composable
fun ThreeImagesNewsItem(article: Article) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // 顶部标题
        Text(
            text = article.title,
            style = MaterialTheme.typography.titleMedium,
            color = if (article.isViewed) Color.Gray else Color.Black,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(8.dp))

        // 三张图片并排
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // 遍历图片列表，最多取前3张
            article.coverImages.take(3).forEach { imgUrl ->
                AsyncImage(
                    model = imgUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .weight(1f) // 三等分宽度
                        .aspectRatio(1.5f) // 固定宽高比 3:2
                        .clip(RoundedCornerShape(4.dp))
                )
            }
        }
    }
}

// --- 3. 纯文模式 (快讯) ---
@Composable
fun TextOnlyNewsItem(article: Article) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = article.title,
            style = MaterialTheme.typography.titleMedium,
            color = if (article.isViewed) Color.Gray else Color.Black,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        if (article.description.isNotEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = article.description,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.DarkGray,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// --- 底部元数据 (来源、时间、更多) ---
@Composable
fun NewsMetaInfo(
    article: Article,
    onMoreClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween // 两端对齐
    ) {
        // 左侧：来源 + 时间
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = article.sourceName,
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray,
                fontSize = 10.sp
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = article.publishedAt.take(10), // 只取日期部分
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray,
                fontSize = 10.sp
            )
        }

        // 右侧：三个点 (更多操作)
        Icon(
            imageVector = Icons.Default.MoreVert,
            contentDescription = "More",
            tint = Color.LightGray,
            modifier = Modifier
                .size(16.dp)
                .clickable { onMoreClick() }
        )
    }
}