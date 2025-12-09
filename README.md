# 📱 TinyToutiao (仿今日头条客户端)

> 🚀 字节跳动工程训练营项目 | ByteDance Engineering Bootcamp Project
>
> 基于 **Jetpack Compose** + **MVVM** + **Paging 3** + **Room** 构建的现代化 Android 新闻客户端。

## 📢 文件说明 (Deliverables)

* **📦 安装包**：Release 包存放于 [**`apk/`**](apk/) 目录。
* **📄 日报**：项目日报请查阅 [**`Daily_Report/`**](Daily_Report/) 目录。
* **📄 日报**：项目周报请查阅 [**`Weekly_Report/`**](Weekly_Report/) 目录。

---

## 📖 项目简介 (Introduction)

**TinyToutiao** 是一个仿“今日头条”的纯客户端 Android 应用。本项目严格遵循 Google 推荐的 **MAD (Modern Android Development)** 架构规范，实现了 **单一数据源 (Single Source of Truth)** 设计。

项目不仅实现了新闻流的无限加载与离线缓存，还重点加入了 **骨架屏加载**、**Lottie 动效**、**异构列表** 等UI细节。

## 📸 预览 (Screenshots)

| 🚀 开屏 | 📰 首页 & 异构列表 | 🔥 热榜 | 📺 频道 |
|:---:|:---:|:---:|:---:|
| <img src="https://pic1.imgdb.cn/item/6937b7eef9354404e33b429f.jpg" width="220"/> | <img src="https://pic1.imgdb.cn/item/6937b7fcf9354404e33b42b4.jpg" width="220"/> | <img src="https://pic1.imgdb.cn/item/6937b843f9354404e33b42cf.jpg" width="220"/> | <img src="https://pic1.imgdb.cn/item/6937b92000233646958ccc38.jpg" width="220"/> |
| **🛠 频道管理** | **📄 新闻详情页** | **👤 个人中心** | **❤️ 浏览历史 & 收藏** |
| <img src="https://pic1.imgdb.cn/item/6937b857f9354404e33b42ee.jpg" width="220"/> | <img src="https://pic1.imgdb.cn/item/6937b879f9354404e33b431a.jpg" width="220"/> | <img src="https://pic1.imgdb.cn/item/6937b8d400233646958ccc2a.jpg" width="220"/> | <img src="https://pic1.imgdb.cn/item/6937b8e700233646958ccc32.jpg" width="220"/> |

## 🛠 技术栈 (Tech Stack)

本项目采用全 Kotlin 开发，严格遵循 **Clean Architecture** 分层思想：

* **UI Layer**: [Jetpack Compose (Material3)](https://developer.android.com/jetpack/compose) - 声明式 UI
* **Architecture**: MVVM + Unidirectional Data Flow (UDF)
* **Data Flow**: [Kotlin Coroutines & Flow](https://kotlinlang.org/docs/coroutines-overview.html) - 异步数据流
* **Network**: [Retrofit2](https://square.github.io/retrofit/) + OkHttp3
* **Local Storage**: [Room](https://developer.android.com/training/data-storage/room) - 数据库 (SSOT 核心)
* **Pagination**: [Paging 3](https://developer.android.com/topic/libraries/architecture/paging/v3-overview) + RemoteMediator (网络+本地双重缓存)
* **Image Loading**: [Coil](https://coil-kt.github.io/coil/)
* **Animation**: [Lottie](https://airbnb.io/lottie/#/) - 下拉刷新与点赞动效

## ✨ 核心功能 (Features)

### 1. 首页信息流 (Infinite Feed)

- [x] **无限滚动**：基于 Paging 3 实现丝滑的分页加载。
- [x] **离线缓存**：遵循 SSOT 原则，断网情况下依然可以浏览缓存新闻。
- [x] **下拉刷新**：集成 **Lottie** 动画（小火箭发射），告别原生转圈。
- [x] **骨架屏**：首次加载展示 Shimmer 骨架屏，提升感官体验。

### 2. 异构列表 (Heterogeneous List)

- [x] 支持 4 种卡片类型：标准模式（左文右图）、三图模式、纯文快讯、**热榜模式**（带排名高亮）。
- [x] 智能适配：根据 API 数据结构自动匹配最佳展示模板。

### 3. 频道管理 (Channel Management)

- [x] 动态频道配置：支持添加/移除频道。
- [x] 数据持久化：用户偏好本地保存，重启 App 不丢失。

## 📂 项目结构 (Structure)

```text
com.example.tinytoutiao
├── data
│   ├── local        # Room 数据库与 DAO
│   ├── remote       # Retrofit 网络层
│   ├── repository   # 数据仓库 (SSOT)
│   └── paging       # Paging3 RemoteMediator (核心逻辑)
├── di               # 依赖注入容器 (AppContainer)
├── ui
│   ├── components   # 公共组件 (NewsItem, LottieHeader, Skeleton...)
│   ├── screens      # 页面 (Home, Detail, Profile...)
│   └── theme        # 主题配置
└── TinyToutiaoApplication.kt
```
