package com.example.tinytoutiao.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.*
import com.example.tinytoutiao.R

@Composable
fun LottieLikeButton(
    isLiked: Boolean,
    onClick: () -> Unit
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.like))

    // 只有点赞时，才播放动画；取消点赞时，不播放（直接变回灰心）
    val progress by animateLottieCompositionAsState(
        composition = composition,
        isPlaying = isLiked,
        // 这里的 speed 取决于你的动画，如果是标准的 like 动画，1f 即可
        speed = 1f
    )

    Box(
        modifier = Modifier
            .size(48.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        // 🔥 核心修复逻辑：混合显示
        if (isLiked) {
            // 状态1：已点赞 -> 显示 Lottie 动画 (红色)
            LottieAnimation(
                composition = composition,
                progress = { progress },
                modifier = Modifier.size(50.dp)
            )
        } else {
            // 状态2：未点赞 -> 显示原生灰色空心图标
            // 这样能保证初始状态绝对正确，不受 JSON 文件颜色影响
            Icon(
                imageVector = Icons.Default.FavoriteBorder,
                contentDescription = "Unliked",
                tint = Color.Gray,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}