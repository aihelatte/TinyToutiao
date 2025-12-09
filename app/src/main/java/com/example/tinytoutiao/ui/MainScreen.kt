package com.example.tinytoutiao.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
// 导入所有 Screen
import com.example.tinytoutiao.ui.screens.channel.ChannelScreen
import com.example.tinytoutiao.ui.screens.detail.NewsDetailScreen
import com.example.tinytoutiao.ui.screens.home.NewsListScreen
import com.example.tinytoutiao.ui.screens.profile.FavoritesScreen
import com.example.tinytoutiao.ui.screens.profile.HistoryScreen
import com.example.tinytoutiao.ui.screens.profile.ProfileScreen
import com.example.tinytoutiao.ui.screens.search.SearchScreen
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

// 定义全局头条红颜色
val ToutiaoRed = Color(0xFFFF3D3C)

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

            // 仅在主页面显示底部导航
            if (currentRoute in routes) {
                NavigationBar(
                    containerColor = Color.White,
                    contentColor = ToutiaoRed // 默认内容色
                ) {
                    items.forEachIndexed { index, item ->
                        val route = routes[index]
                        val isSelected = currentRoute == route

                        NavigationBarItem(
                            icon = { Icon(icons[index], contentDescription = item) },
                            label = {
                                Text(
                                    text = item,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 12.sp // 稍微调大一点 (默认是 11sp 左右)
                                )
                            },
                            selected = isSelected,
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = ToutiaoRed,
                                selectedTextColor = ToutiaoRed,
                                indicatorColor = Color.Transparent, // 去掉选中时的胶囊背景
                                unselectedIconColor = Color.Gray,
                                unselectedTextColor = Color.Gray
                            ),
                            onClick = {
                                navController.navigate(route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
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
                    },
                    onChannelManageClick = {
                        navController.navigate("channel_manage")
                    },
                    onSearchClick = {
                        navController.navigate("search")
                    }
                )
            }

            // 2. 个人中心
            composable("profile") {
                ProfileScreen(
                    onHistoryClick = { navController.navigate("history") },
                    onFavoritesClick = { navController.navigate("favorites") }
                )
            }

            // 3. 详情页
            composable(
                route = "detail/{newsUrl}",
                arguments = listOf(navArgument("newsUrl") { type = NavType.StringType })
            ) { backStackEntry ->
                val encodedUrl = backStackEntry.arguments?.getString("newsUrl") ?: ""
                val decodedUrl = URLDecoder.decode(encodedUrl, StandardCharsets.UTF_8.toString())
                NewsDetailScreen(
                    url = decodedUrl,
                    onBackClick = { navController.popBackStack() }
                )
            }

            // 4. 历史记录
            composable("history") {
                HistoryScreen(
                    onBackClick = { navController.popBackStack() },
                    onNewsClick = { url ->
                        val encodedUrl = URLEncoder.encode(url, StandardCharsets.UTF_8.toString())
                        navController.navigate("detail/$encodedUrl")
                    }
                )
            }

            // 5. 频道管理
            composable("channel_manage") {
                ChannelScreen(
                    onBackClick = { navController.popBackStack() }
                )
            }

            // 6. 搜索页
            composable("search") {
                SearchScreen(
                    onBackClick = { navController.popBackStack() },
                    onNewsClick = { url ->
                        val encodedUrl = URLEncoder.encode(url, StandardCharsets.UTF_8.toString())
                        navController.navigate("detail/$encodedUrl")
                    }
                )
            }

            // 7. 我的收藏
            composable("favorites") {
                FavoritesScreen(
                    onBackClick = { navController.popBackStack() },
                    onNewsClick = { url ->
                        val encodedUrl = URLEncoder.encode(url, StandardCharsets.UTF_8.toString())
                        navController.navigate("detail/$encodedUrl")
                    }
                )
            }
        }
    }
}