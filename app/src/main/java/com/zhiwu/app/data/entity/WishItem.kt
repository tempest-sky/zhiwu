package com.zhiwu.app.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * 心愿清单实体类
 * 记录用户想要购买的物品
 */
@Entity(
    tableName = "wish_items",
    indices = [
        Index(value = ["categoryId"]),
        Index(value = ["createdAt"])
    ]
)
data class WishItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    /** 物品名称 */
    val name: String,
    
    /** 预期价格（单位：元） */
    val expectedPrice: Double? = null,
    
    /** 分类ID */
    val categoryId: Long? = null,
    
    /** 图片路径或URL */
    val imagePath: String? = null,
    
    /** 相关链接 */
    val relatedLink: String? = null,
    
    /** 备注/想要的原因 */
    val notes: String? = null,
    
    /** 冷静期结束时间（时间戳，毫秒，null表示无冷静期） */
    val cooldownUntil: Long? = null,
    
    /** 优先级 (1-5, 5最高) */
    val priority: Int = 3,
    
    /** 是否已实现 */
    val isAchieved: Boolean = false,
    
    /** 实现时间（转为正式物品的时间） */
    val achievedDate: Long? = null,
    
    /** 创建时间 */
    val createdAt: Long = System.currentTimeMillis(),
    
    /** 更新时间 */
    val updatedAt: Long = System.currentTimeMillis()
) {
    /**
     * 检查冷静期是否已过
     */
    fun isCooldownPassed(): Boolean {
        if (cooldownUntil == null) return true
        return System.currentTimeMillis() >= cooldownUntil
    }
    
    /**
     * 获取剩余冷静期天数
     */
    fun getRemainingCooldownDays(): Int {
        if (cooldownUntil == null) return 0
        val remaining = ((cooldownUntil - System.currentTimeMillis()) / (1000 * 60 * 60 * 24)).toInt()
        return remaining.coerceAtLeast(0)
    }
}
