package com.zhiwu.app.data.repository

import com.zhiwu.app.data.dao.TagDao
import com.zhiwu.app.data.entity.Tag
import kotlinx.coroutines.flow.Flow

/**
 * 标签仓库
 */
class TagRepository(private val tagDao: TagDao) {
    
    /** 所有标签 */
    val allTags: Flow<List<Tag>> = tagDao.getAllTags()
    
    /** 根据ID获取标签 */
    suspend fun getTagById(tagId: Long): Tag? {
        return tagDao.getTagById(tagId)
    }
    
    /** 根据物品ID获取标签 */
    fun getTagsByItemId(itemId: Long): Flow<List<Tag>> {
        return tagDao.getTagsByItemId(itemId)
    }
    
    /** 插入标签 */
    suspend fun insertTag(tag: Tag): Long {
        return tagDao.insertTag(tag)
    }
    
    /** 更新标签 */
    suspend fun updateTag(tag: Tag) {
        tagDao.updateTag(tag)
    }
    
    /** 删除标签 */
    suspend fun deleteTag(tag: Tag) {
        tagDao.deleteTag(tag)
    }
}