package com.example.tinytoutiao.data.model

/**
 * 频道模型
 * @param code 对应 GNews API 的 category 参数 (如 "sports")
 * @param name UI 显示的中文名称 (如 "体育")
 */
data class Channel(
    val code: String,
    val name: String
) {
    // 静态配置：这里定义所有 GNews 支持的频道
    // 确保与 API 文档一一对应
    companion object {
        val ALL_CHANNELS = listOf(
            Channel("general", "推荐"), // 默认综合
            Channel("hot", "热榜"),
            Channel("world", "国际"),
            Channel("nation", "国内"), // 对应 GNews 的 nation
            Channel("business", "财经"),
            Channel("technology", "科技"),
            Channel("entertainment", "娱乐"),
            Channel("sports", "体育"),
            Channel("science", "科学"),
            Channel("health", "健康")
        )
    }
}