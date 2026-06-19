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
    
    /** 获取总花费 */
    @Query("SELECT COALESCE(SUM(price), 0.0) FROM items")
    fun getTotalCost(): Flow<Double>
    
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
}