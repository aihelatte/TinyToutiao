package com.example.tinytoutiao

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.tinytoutiao.ui.MainScreen
import com.example.tinytoutiao.ui.screens.splash.SplashScreen
import com.example.tinytoutiao.ui.theme.TinyToutiaoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TinyToutiaoTheme {
                // 定义一个状态来控制是否显示开屏
                var showSplash by remember { mutableStateOf(true) }

                if (showSplash) {
                    // 显示开屏页，并传入回调函数
                    SplashScreen(
                        onSplashFinished = {
                            // 当开屏结束时，把状态改为 false，UI 会自动重绘显示 MainScreen
                            showSplash = false
                        }
                    )
                } else {
                    // 显示主页
                    MainScreen()
                }
            }
        }
    }
}