package com.example.tinytoutiao.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.tinytoutiao.ui.screens.detail.NewsDetailScreen
import com.example.tinytoutiao.ui.screens.home.NewsListScreen
import com.example.tinytoutiao.ui.screens.profile.HistoryScreen // 🔥 导入 HistoryScreen
import com.example.tinytoutiao.ui.screens.profile.ProfileScreen
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    val items = listOf("首页", "我的")
    val icons = listOf(Icons.Default.Home, Icons.Default.Person)
    val routes = listOf("home", "profile")

    Scaffold(
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            // 只有在主 Tab 页才显示底部导航，进入详情页或历史页隐藏
            if (currentRoute in routes) {
                NavigationBar(
                    containerColor = Color.White,
                    contentColor = MaterialTheme.colorScheme.primary
                ) {
                    items.forEachIndexed { index, item ->
                        val route = routes[index]
                        NavigationBarItem(
                            icon = { Icon(icons[index], contentDescription = item) },
                            label = { Text(item) },
                            selected = currentRoute == route,
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = Color.Transparent
                            ),
                            onClick = {
                                navController.navigate(route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            // 1. 首页
            composable("home") {
                NewsListScreen(
                    onNewsClick = { url ->
                        val encodedUrl = URLEncoder.encode(url, StandardCharsets.UTF_8.toString())
                        navController.navigate("detail/$encodedUrl")
                    }
                )
            }

            // 2. 个人中心
            composable("profile") {
                ProfileScreen(
                    // 🔥 处理跳转到历史记录
                    onHistoryClick = { navController.navigate("history") }
                )
            }

            // 3. 详情页
            composable(
                route = "detail/{newsUrl}",
                arguments = listOf(navArgument("newsUrl") { type = NavType.StringType })
            ) { backStackEntry ->
                val encodedUrl = backStackEntry.arguments?.getString("newsUrl") ?: ""
                val decodedUrl = URLDecoder.decode(encodedUrl, StandardCharsets.UTF_8.toString())
                NewsDetailScreen(url = decodedUrl, onBackClick = { navController.popBackStack() })
            }

            // 4. 🔥 历史记录页路由
            composable("history") {
                HistoryScreen(
                    onBackClick = { navController.popBackStack() },
                    onNewsClick = { url ->
                        // 历史记录里的新闻点击后，也跳去详情页
                        val encodedUrl = URLEncoder.encode(url, StandardCharsets.UTF_8.toString())
                        navController.navigate("detail/$encodedUrl")
                    }
                )
            }
        }
    }
}