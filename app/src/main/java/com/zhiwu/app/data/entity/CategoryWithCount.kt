package com.zhiwu.app.data.entity

/**
 * 分类统计信息
 */
data class CategoryWithCount(
    val categoryId: Long,
    val categoryName: String,
    val itemCount: Int,
    val totalCost: Double
)