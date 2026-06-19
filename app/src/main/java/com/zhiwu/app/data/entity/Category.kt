package com.zhiwu.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 分类实体类
 */
@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    /** 分类名称 */
    val name: String,
    
    /** 图标名称（Material Icons） */
    val icon: String = "category",
    
    /** 是否为系统预设分类 */
    val isPreset: Boolean = false,
    
    /** 排序顺序 */
    val sortOrder: Int = 0
)