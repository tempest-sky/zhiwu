package com.zhiwu.app.data.dao

import androidx.room.*
import com.zhiwu.app.data.entity.Category
import kotlinx.coroutines.flow.Flow

/**
 * 分类数据访问对象
 */
@Dao
interface CategoryDao {
    
    /** 获取所有分类 */
    @Query("SELECT * FROM categories ORDER BY sortOrder ASC, id ASC")
    fun getAllCategories(): Flow<List<Category>>
    
    /** 根据ID获取分类 */
    @Query("SELECT * FROM categories WHERE id = :categoryId")
    suspend fun getCategoryById(categoryId: Long): Category?
    
    /** 插入分类 */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: Category): Long
    
    /** 批量插入分类 */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCategories(categories: List<Category>)
    
    /** 更新分类 */
    @Update
    suspend fun updateCategory(category: Category)
    
    /** 删除分类 */
    @Delete
    suspend fun deleteCategory(category: Category)
    
    /** 获取分类数量 */
    @Query("SELECT COUNT(*) FROM categories")
    suspend fun getCategoryCount(): Int
    
    /** 获取预设分类数量 */
    @Query("SELECT COUNT(*) FROM categories WHERE isPreset = 1")
    suspend fun getPresetCategoryCount(): Int
    
    /** 更新分类排序顺序 */
    @Query("UPDATE categories SET sortOrder = :sortOrder WHERE id = :categoryId")
    suspend fun updateSortOrder(categoryId: Long, sortOrder: Int)
    
    /** 批量更新分类排序顺序 */
    @Transaction
    suspend fun updateSortOrders(categories: List<Category>) {
        categories.forEachIndexed { index, category ->
            updateSortOrder(category.id, index)
        }
    }
}