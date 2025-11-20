package com.example.tinytoutiao

import android.app.Application
import com.example.tinytoutiao.di.AppContainer

/**
 * 自定义 Application 类
 * 这是全 App 存活时间最长的对象。
 */
class TinyToutiaoApplication : Application() {

    // 暴露容器给 Activity/ViewModel 使用
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        // App 启动时，初始化依赖容器
        container = AppContainer(this)
    }
}