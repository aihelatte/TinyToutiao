package com.example.tinytoutiao.ui.components

import androidx.compose.foundation.background // 🔥 修复报错的关键导入
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
    onMoreClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // 🔥 分发逻辑：新增 itemType == 3 (热榜模式)
        when (article.itemType) {
            1 -> ThreeImagesNewsItem(article) // 三图
            2 -> TextOnlyNewsItem(article)    // 纯文
            3 -> HotRankItem(article)         // 🔥 热榜
            else -> StandardNewsItem(article) // 默认标准
        }

        // 热榜模式不需要常规的底部信息栏
        if (article.itemType != 3) {
            Spacer(modifier = Modifier.height(8.dp))
            NewsMetaInfo(article, onMoreClick)
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = Color.LightGray, thickness = 0.5.dp)
        } else {
            // 热榜自带分割线
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color(0xFFEEEEEE), thickness = 0.5.dp)
        }
    }
}

// --- 🔥 新增：热榜条目组件 ---
@Composable
fun HotRankItem(article: Article) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 提取标题里的排名数字 (格式: "1. 标题")
        // 如果提取失败，默认为 ""
        val rank = article.title.substringBefore(".", missingDelimiterValue = "")
        val isTop3 = rank in listOf("1", "2", "3")

        // 排名数字
        Text(
            text = rank,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = if (isTop3) Color.Red else Color.Gray,
            modifier = Modifier.width(32.dp),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.width(8.dp))

        // 标题和热度
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = article.title.substringAfter(". "), // 去掉前缀
                style = MaterialTheme.typography.titleMedium,
                color = Color.Black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            // 显示热度值
            Text(
                text = "热度 ${article.description} 万",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }

        // 右侧 "热" 标签 (仅前三名)
        if (isTop3) {
            Text(
                text = "热",
                color = Color.White,
                fontSize = 10.sp,
                modifier = Modifier
                    .background(Color(0xFFF44336), RoundedCornerShape(2.dp)) // 🔥 这里就是之前报错的地方
                    .padding(horizontal = 4.dp, vertical = 1.dp)
            )
        }
    }
}

// --- 1. 标准模式 (左文右图) ---
@Composable
fun StandardNewsItem(article: Article) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = article.title,
            style = MaterialTheme.typography.titleMedium,
            color = if (article.isViewed) Color.Gray else Color.Black,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f).padding(end = 8.dp)
        )

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
        Text(
            text = article.title,
            style = MaterialTheme.typography.titleMedium,
            color = if (article.isViewed) Color.Gray else Color.Black,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            article.coverImages.take(3).forEach { imgUrl ->
                AsyncImage(
                    model = imgUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1.5f)
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

// --- 底部元数据 ---
@Composable
fun NewsMetaInfo(
    article: Article,
    onMoreClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = article.sourceName,
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray,
                fontSize = 10.sp
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = article.publishedAt.take(10),
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray,
                fontSize = 10.sp
            )
        }

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