package com.example.tinytoutiao.ui.screens.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tinytoutiao.R
import kotlinx.coroutines.delay

// 文艺宋体 (优雅、有文化感，类似参考图风格)
val FontOption = FontFamily.Serif

// 复刻截图的微光背景
val ToutiaoSplashBrush = Brush.linearGradient(
    colors = listOf(
        Color(0xFFE0F0FA), // 左上：淡雅蓝 (参考截图左上)
        Color(0xFFFDF2F2), // 中间：柔和粉 (参考截图中间)
        Color(0xFFF2F3F7)  // 右下：静谧灰 (参考截图底部)
    ),
    start = Offset(0f, 0f),
    end = Offset(1000f, 2000f) // 斜向拉伸渐变
)

@Composable
fun SplashScreen(onSplashFinished: () -> Unit) {
    val currentOnSplashFinished by rememberUpdatedState(onSplashFinished)

    // 停留 2 秒
    LaunchedEffect(true) {
        delay(3000)
        currentOnSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ToutiaoSplashBrush), // 应用背景
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            // 整体稍微往上提一点，视觉重心更稳
            modifier = Modifier.offset(y = (-50).dp)
        ) {
            // 1. Logo 图标
            Image(
                painter = painterResource(id = R.drawable.ic_toutiao_logo),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(72.dp) // 图标大小
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 2. Slogan 文字 "看见更大的世界"
            Text(
                text = "看见更大的世界",
                fontSize = 24.sp,
                color = Color(0xFF222222),
                letterSpacing = 2.sp,

                fontFamily = FontOption,

                fontWeight = FontWeight.SemiBold
            )
        }
    }
}