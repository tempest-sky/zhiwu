package com.zhiwu.app.data.dao

import androidx.room.*
import com.zhiwu.app.data.entity.Tag
import kotlinx.coroutines.flow.Flow

/**
 * 标签数据访问对象
 */
@Dao
interface TagDao {
    
    /** 获取所有标签 */
    @Query("SELECT * FROM tags ORDER BY sortOrder ASC, id ASC")
    fun getAllTags(): Flow<List<Tag>>
    
    /** 根据ID获取标签 */
    @Query("SELECT * FROM tags WHERE id = :tagId")
    suspend fun getTagById(tagId: Long): Tag?
    
    /** 根据物品ID获取标签 */
    @Query("""
        SELECT tags.* FROM tags
        INNER JOIN item_tag_cross_ref ON tags.id = item_tag_cross_ref.tagId
        WHERE item_tag_cross_ref.itemId = :itemId
    """)
    fun getTagsByItemId(itemId: Long): Flow<List<Tag>>
    
    /** 插入标签 */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTag(tag: Tag): Long
    
    /** 更新标签 */
    @Update
    suspend fun updateTag(tag: Tag)
    
    /** 删除标签 */
    @Delete
    suspend fun deleteTag(tag: Tag)
    
    /** 获取标签数量 */
    @Query("SELECT COUNT(*) FROM tags")
    suspend fun getTagCount(): Int
}