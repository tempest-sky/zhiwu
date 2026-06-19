package com.zhiwu.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 标签实体类
 */
@Entity(tableName = "tags")
data class Tag(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    /** 标签名称 */
    val name: String,
    
    /** 排序顺序 */
    val sortOrder: Int = 0
)