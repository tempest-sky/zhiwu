package com.zhiwu.app.data.repository

import com.zhiwu.app.data.dao.CategoryDao
import com.zhiwu.app.data.entity.Category
import kotlinx.coroutines.flow.Flow

/**
 * 分类仓库
 */
class CategoryRepository(private val categoryDao: CategoryDao) {
    
    /** 所有分类 */
    val allCategories: Flow<List<Category>> = categoryDao.getAllCategories()
    
    /** 根据ID获取分类 */
    suspend fun getCategoryById(categoryId: Long): Category? {
        return categoryDao.getCategoryById(categoryId)
    }
    
    /** 插入分类 */
    suspend fun insertCategory(category: Category): Long {
        return categoryDao.insertCategory(category)
    }
    
    /** 更新分类 */
    suspend fun updateCategory(category: Category) {
        categoryDao.updateCategory(category)
    }
    
    /** 删除分类 */
    suspend fun deleteCategory(category: Category) {
        categoryDao.deleteCategory(category)
    }
    
    /** 检查是否为预设分类 */
    suspend fun isPresetCategory(categoryId: Long): Boolean {
        val category = categoryDao.getCategoryById(categoryId)
        return category?.isPreset ?: false
    }
    
    /** 更新分类排序顺序 */
    suspend fun updateSortOrders(categories: List<Category>) {
        categoryDao.updateSortOrders(categories)
    }
}