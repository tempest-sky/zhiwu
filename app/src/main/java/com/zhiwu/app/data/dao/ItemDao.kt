package com.zhiwu.app.data.dao

import androidx.room.*
import com.zhiwu.app.data.entity.Item
import com.zhiwu.app.data.entity.ItemTagCrossRef
import com.zhiwu.app.data.entity.ItemWithDetails
import kotlinx.coroutines.flow.Flow

/**
 * 物品数据访问对象
 */
@Dao
interface ItemDao {
    
    /** 获取所有物品（带详情）按创建时间倒序 */
    @Transaction
    @Query("SELECT * FROM items ORDER BY createdAt DESC")
    fun getAllItemsWithDetails(): Flow<List<ItemWithDetails>>
    
    /** 根据ID获取物品详情 */
    @Transaction
    @Query("SELECT * FROM items WHERE id = :itemId")
    fun getItemWithDetails(itemId: Long): Flow<ItemWithDetails?>
    
    /** 根据ID获取物品 */
    @Query("SELECT * FROM items WHERE id = :itemId")
    suspend fun getItemById(itemId: Long): Item?
    
    /** 搜索物品 */
    @Transaction
    @Query("""
        SELECT * FROM items 
        WHERE name LIKE '%' || :query || '%' 
        OR notes LIKE '%' || :query || '%'
        ORDER BY createdAt DESC
    """)
    fun searchItems(query: String): Flow<List<ItemWithDetails>>
    
    /** 按分类筛选物品 */
    @Transaction
    @Query("SELECT * FROM items WHERE categoryId = :categoryId ORDER BY createdAt DESC")
    fun getItemsByCategory(categoryId: Long): Flow<List<ItemWithDetails>>
    
    /** 按标签筛选物品 */
    @Transaction
    @Query("""
        SELECT items.* FROM items 
        INNER JOIN item_tag_cross_ref ON items.id = item_tag_cross_ref.itemId
        WHERE item_tag_cross_ref.tagId = :tagId
        ORDER BY items.createdAt DESC
    """)
    fun getItemsByTag(tagId: Long): Flow<List<ItemWithDetails>>
    
    /** 按状态筛选物品 */
    @Transaction
    @Query("SELECT * FROM items WHERE status = :status ORDER BY createdAt DESC")
    fun getItemsByStatus(status: String): Flow<List<ItemWithDetails>>
    
    /** 获取使用中的物品 */
    @Transaction
    @Query("SELECT * FROM items WHERE status = 'IN_USE' ORDER BY createdAt DESC")
    fun getInUseItems(): Flow<List<ItemWithDetails>>
    
    /** 获取闲置物品 */
    @Transaction
    @Query("SELECT * FROM items WHERE status = 'IDLE' ORDER BY createdAt DESC")
    fun getIdleItems(): Flow<List<ItemWithDetails>>
    
    /** 获取已售出物品 */
    @Transaction
    @Query("SELECT * FROM items WHERE status = 'SOLD' ORDER BY soldDate DESC")
    fun getSoldItems(): Flow<List<ItemWithDetails>>
    
    /** 获取保修期即将到期的物品（7天内） */
    @Transaction
    @Query("""
        SELECT * FROM items 
        WHERE warrantyExpiry IS NOT NULL 
        AND warrantyExpiry BETWEEN :now AND :sevenDaysLater
        AND status = 'IN_USE'
        ORDER BY warrantyExpiry ASC
    """)
    fun getWarrantyExpiringItems(now: Long = System.currentTimeMillis(), sevenDaysLater: Long = System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000): Flow<List<ItemWithDetails>>
    
    /** 获取保质期即将到期的物品（7天内） */
    @Transaction
    @Query("""
        SELECT * FROM items 
        WHERE shelfLifeExpiry IS NOT NULL 
        AND shelfLifeExpiry BETWEEN :now AND :sevenDaysLater
        AND status = 'IN_USE'
        ORDER BY shelfLifeExpiry ASC
    """)
    fun getShelfLifeExpiringItems(now: Long = System.currentTimeMillis(), sevenDaysLater: Long = System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000): Flow<List<ItemWithDetails>>
    
    /** 插入物品，返回ID */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: Item): Long
    
    /** 更新物品 */
    @Update
    suspend fun updateItem(item: Item)
    
    /** 删除物品 */
    @Delete
    suspend fun deleteItem(item: Item)
    
    /** 根据ID删除物品 */
    @Query("DELETE FROM items WHERE id = :itemId")
    suspend fun deleteItemById(itemId: Long)
    
    /** 插入物品-标签关联 */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItemTagCrossRef(crossRef: ItemTagCrossRef)
    
    /** 删除物品的所有标签关联 */
    @Query("DELETE FROM item_tag_cross_ref WHERE itemId = :itemId")
    suspend fun deleteItemTags(itemId: Long)
    
    /** 获取物品总数 */
    @Query("SELECT COUNT(*) FROM items")
    fun getTotalItemCount(): Flow<Int>
    
    /** 获取使用中物品数量 */
    @Query("SELECT COUNT(*) FROM items WHERE status = 'IN_USE'")
    fun getInUseItemCount(): Flow<Int>
    
    /** 获取总花费 */
    @Query("SELECT COALESCE(SUM(price), 0.0) FROM items")
    fun getTotalCost(): Flow<Double>
    
    /** 获取已售出物品总收入 */
    @Query("SELECT COALESCE(SUM(soldPrice), 0.0) FROM items WHERE soldPrice IS NOT NULL")
    fun getTotalSoldIncome(): Flow<Double>
    
    /** 获取各分类统计 */
    @Query("""
        SELECT c.id as categoryId, c.name as categoryName, 
               COUNT(i.id) as itemCount, COALESCE(SUM(i.price), 0.0) as totalCost
        FROM categories c
        LEFT JOIN items i ON c.id = i.categoryId
        GROUP BY c.id, c.name
        ORDER BY itemCount DESC
    """)
    fun getCategoryStats(): Flow<List<com.zhiwu.app.data.entity.CategoryWithCount>>
    
    /** 更新物品使用次数 */
    @Query("UPDATE items SET usageCount = usageCount + 1, updatedAt = :now WHERE id = :itemId")
    suspend fun incrementUsageCount(itemId: Long, now: Long = System.currentTimeMillis())
    
    /** 标记物品为已售出 */
    @Query("UPDATE items SET status = 'SOLD', soldPrice = :soldPrice, soldDate = :soldDate, updatedAt = :now WHERE id = :itemId")
    suspend fun markAsSold(itemId: Long, soldPrice: Double, soldDate: Long = System.currentTimeMillis(), now: Long = System.currentTimeMillis())
    
    /** 标记物品为闲置 */
    @Query("UPDATE items SET status = 'IDLE', updatedAt = :now WHERE id = :itemId")
    suspend fun markAsIdle(itemId: Long, now: Long = System.currentTimeMillis())
    
    /** 标记物品为使用中 */
    @Query("UPDATE items SET status = 'IN_USE', updatedAt = :now WHERE id = :itemId")
    suspend fun markAsInUse(itemId: Long, now: Long = System.currentTimeMillis())
}
