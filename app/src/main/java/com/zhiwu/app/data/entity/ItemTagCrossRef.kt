package com.zhiwu.app.data.entity

import androidx.room.Entity

/**
 * 物品-标签关联表
 * 实现多对多关系
 */
@Entity(
    tableName = "item_tag_cross_ref",
    primaryKeys = ["itemId", "tagId"]
)
data class ItemTagCrossRef(
    val itemId: Long,
    val tagId: Long
)