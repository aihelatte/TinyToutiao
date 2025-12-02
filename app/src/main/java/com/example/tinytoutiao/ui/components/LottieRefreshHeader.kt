package com.example.tinytoutiao.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.*
import com.example.tinytoutiao.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LottieRefreshHeader(
    state: PullToRefreshState,
    isRefreshing: Boolean
) {
    // 引用你的小火箭文件
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.rocket_launch))

    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        isPlaying = isRefreshing
    )

    if (state.verticalOffset > 0 || isRefreshing) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp) // 给火箭留够高度
                // 视差移动效果
                .offset(y = (state.verticalOffset / 2 - 50).dp.coerceAtLeast(0.dp)),
            contentAlignment = Alignment.Center
        ) {
            LottieAnimation(
                composition = composition,
                // 下拉时跟随手指进度，刷新时自动播放
                progress = { if(isRefreshing) progress else (state.verticalOffset / 200f).coerceIn(0f, 1f) },
                modifier = Modifier.size(80.dp) // 火箭大小
            )
        }
    }
}