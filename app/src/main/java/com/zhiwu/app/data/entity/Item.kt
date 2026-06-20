package com.zhiwu.app.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * 物品状态枚举
 */
enum class ItemStatus {
    /** 使用中 */
    IN_USE,
    /** 闲置 */
    IDLE,
    /** 已售出 */
    SOLD,
    /** 已丢弃 */
    DISCARDED
}

/**
 * 物品实体类
 * 记录用户购买的物品信息
 */
@Entity(
    tableName = "items",
    indices = [
        Index(value = ["categoryId"]),
        Index(value = ["createdAt"]),
        Index(value = ["status"])
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
    
    /** 物品状态 */
    val status: String = ItemStatus.IN_USE.name,
    
    /** 保修期到期时间（时间戳，毫秒，null表示无保修期） */
    val warrantyExpiry: Long? = null,
    
    /** 保质期到期时间（时间戳，毫秒，null表示无保质期） */
    val shelfLifeExpiry: Long? = null,
    
    /** 使用次数 */
    val usageCount: Int = 0,
    
    /** 售出价格（单位：元，null表示未售出） */
    val soldPrice: Double? = null,
    
    /** 售出时间（时间戳，毫秒，null表示未售出） */
    val soldDate: Long? = null,
    
    /** 入手渠道 */
    val purchaseChannel: String? = null,
    
    /** 相关链接 */
    val relatedLink: String? = null,
    
    /** 创建时间 */
    val createdAt: Long = System.currentTimeMillis(),
    
    /** 更新时间 */
    val updatedAt: Long = System.currentTimeMillis()
) {
    /**
     * 计算日均价格
     * @return 日均价格，如果物品已售出则返回购入价与售出价的差值除以持有天数
     */
    fun calculateDailyPrice(): Double {
        val endDate = soldDate ?: System.currentTimeMillis()
        val days = ((endDate - purchaseDate) / (1000 * 60 * 60 * 24)).toInt()
            .coerceAtLeast(1)
        return price / days
    }
    
    /**
     * 获取持有天数
     */
    fun getHoldingDays(): Int {
        val endDate = soldDate ?: System.currentTimeMillis()
        return ((endDate - purchaseDate) / (1000 * 60 * 60 * 24)).toInt()
            .coerceAtLeast(0)
    }
    
    /**
     * 检查保修期是否即将到期（7天内）
     */
    fun isWarrantyExpiringSoon(): Boolean {
        if (warrantyExpiry == null) return false
        val daysUntilExpiry = ((warrantyExpiry - System.currentTimeMillis()) / (1000 * 60 * 60 * 24)).toInt()
        return daysUntilExpiry in 0..7
    }
    
    /**
     * 检查保质期是否即将到期（7天内）
     */
    fun isShelfLifeExpiringSoon(): Boolean {
        if (shelfLifeExpiry == null) return false
        val daysUntilExpiry = ((shelfLifeExpiry - System.currentTimeMillis()) / (1000 * 60 * 60 * 24)).toInt()
        return daysUntilExpiry in 0..7
    }
}
