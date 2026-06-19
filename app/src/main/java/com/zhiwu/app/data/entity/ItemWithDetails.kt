package com.zhiwu.app.data.entity

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

/**
 * 物品及其关联信息（分类和标签）
 */
data class ItemWithDetails(
    @Embedded val item: Item,
    
    @Relation(
        parentColumn = "categoryId",
        entityColumn = "id"
    )
    val category: Category,
    
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = ItemTagCrossRef::class,
            parentColumn = "itemId",
            entityColumn = "tagId"
        )
    )
    val tags: List<Tag>
)