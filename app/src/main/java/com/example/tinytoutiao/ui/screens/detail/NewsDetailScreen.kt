package com.example.tinytoutiao.ui.screens.detail

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

/**
 * 新闻详情页 (WebView)
 * 职责：加载并展示 URL
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsDetailScreen(
    url: String,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "详情",
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: 分享功能 */ }) {
                        Icon(Icons.Default.Share, contentDescription = "Share")
                    }
                }
            )
        }
    ) { innerPadding ->
        // AndroidView 允许在 Compose 中使用传统 View
        AndroidView(
            modifier = Modifier.padding(innerPadding),
            factory = { context ->
                WebView(context).apply {
                    // 配置 WebView
                    settings.javaScriptEnabled = true // 允许 JS (现代网页必须)
                    settings.domStorageEnabled = true

                    // 关键：设置 WebViewClient，否则点击链接会跳出 App 去浏览器打开
                    webViewClient = WebViewClient()

                    // 加载网页
                    loadUrl(url)
                }
            },
            update = { webView ->
                // 如果 url 变了，重新加载 (虽然在本场景下 url 不会变)
                // webView.loadUrl(url)
            }
        )
    }
}