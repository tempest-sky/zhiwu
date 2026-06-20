package com.zhiwu.app.data.repository

import com.zhiwu.app.data.dao.ItemDao
import com.zhiwu.app.data.entity.CategoryWithCount
import com.zhiwu.app.data.entity.Item
import com.zhiwu.app.data.entity.ItemTagCrossRef
import com.zhiwu.app.data.entity.ItemWithDetails
import kotlinx.coroutines.flow.Flow

/**
 * 物品仓库
 * 封装数据访问逻辑
 */
class ItemRepository(private val itemDao: ItemDao) {
    
    /** 所有物品 */
    val allItems: Flow<List<ItemWithDetails>> = itemDao.getAllItemsWithDetails()
    
    /** 物品总数 */
    val totalItemCount: Flow<Int> = itemDao.getTotalItemCount()
    
    /** 总花费 */
    val totalCost: Flow<Double> = itemDao.getTotalCost()
    
    /** 分类统计 */
    val categoryStats: Flow<List<CategoryWithCount>> = itemDao.getCategoryStats()
    
    /** 获取物品详情 */
    fun getItemWithDetails(itemId: Long): Flow<ItemWithDetails?> {
        return itemDao.getItemWithDetails(itemId)
    }
    
    /** 搜索物品 */
    fun searchItems(query: String): Flow<List<ItemWithDetails>> {
        return itemDao.searchItems(query)
    }
    
    /** 按分类筛选 */
    fun getItemsByCategory(categoryId: Long): Flow<List<ItemWithDetails>> {
        return itemDao.getItemsByCategory(categoryId)
    }
    
    /** 按标签筛选 */
    fun getItemsByTag(tagId: Long): Flow<List<ItemWithDetails>> {
        return itemDao.getItemsByTag(tagId)
    }
    
    /** 按状态筛选 */
    fun getItemsByStatus(status: String): Flow<List<ItemWithDetails>> {
        return itemDao.getItemsByStatus(status)
    }
    
    /** 获取使用中物品数量 */
    fun getInUseItemCount(): Flow<Int> {
        return itemDao.getInUseItemCount()
    }
    
    /** 获取已售出物品总收入 */
    fun getTotalSoldIncome(): Flow<Double> {
        return itemDao.getTotalSoldIncome()
    }
    
    /** 获取保修期即将到期的物品 */
    fun getWarrantyExpiringItems(): Flow<List<ItemWithDetails>> {
        return itemDao.getWarrantyExpiringItems()
    }
    
    /** 获取保质期即将到期的物品 */
    fun getShelfLifeExpiringItems(): Flow<List<ItemWithDetails>> {
        return itemDao.getShelfLifeExpiringItems()
    }
    
    /** 插入物品 */
    suspend fun insertItem(item: Item, tagIds: List<Long>): Long {
        val itemId = itemDao.insertItem(item)
        tagIds.forEach { tagId ->
            itemDao.insertItemTagCrossRef(ItemTagCrossRef(itemId, tagId))
        }
        return itemId
    }
    
    /** 更新物品 */
    suspend fun updateItem(item: Item, tagIds: List<Long>) {
        itemDao.updateItem(item)
        itemDao.deleteItemTags(item.id)
        tagIds.forEach { tagId ->
            itemDao.insertItemTagCrossRef(ItemTagCrossRef(item.id, tagId))
        }
    }
    
    /** 删除物品 */
    suspend fun deleteItem(item: Item) {
        itemDao.deleteItemTags(item.id)
        itemDao.deleteItem(item)
    }
    
    /** 根据ID删除物品 */
    suspend fun deleteItemById(itemId: Long) {
        itemDao.deleteItemTags(itemId)
        itemDao.deleteItemById(itemId)
    }
    
    /** 标记物品为已售出 */
    suspend fun markAsSold(itemId: Long, soldPrice: Double) {
        itemDao.markAsSold(itemId, soldPrice)
    }
    
    /** 标记物品为闲置 */
    suspend fun markAsIdle(itemId: Long) {
        itemDao.markAsIdle(itemId)
    }
    
    /** 标记物品为使用中 */
    suspend fun markAsInUse(itemId: Long) {
        itemDao.markAsInUse(itemId)
    }
    
    /** 增加使用次数 */
    suspend fun incrementUsageCount(itemId: Long) {
        itemDao.incrementUsageCount(itemId)
    }
}
