package com.zhiwu.app.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * 物品实体类
 * 记录用户购买的物品信息
 */
@Entity(
    tableName = "items",
    indices = [
        Index(value = ["categoryId"]),
        Index(value = ["createdAt"])
    ]
)
data class Item(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    /** 物品名称 */
    val name: String,
    
    /** 购买价格（单位：元） */
    val price: Double,
    
    /** 购买时间（时间戳，毫秒） */
    val purchaseDate: Long,
    
    /** 分类ID */
    val categoryId: Long,
    
    /** 图片路径（本地文件路径） */
    val imagePath: String? = null,
    
    /** 备注 */
    val notes: String? = null,
    
    /** 创建时间 */
    val createdAt: Long = System.currentTimeMillis(),
    
    /** 更新时间 */
    val updatedAt: Long = System.currentTimeMillis()
)