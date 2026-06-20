package com.zhiwu.app.data.dao

import androidx.room.*
import com.zhiwu.app.data.entity.WishItem
import kotlinx.coroutines.flow.Flow

/**
 * 心愿清单数据访问对象
 */
@Dao
interface WishItemDao {
    
    /** 获取所有心愿物品（按创建时间倒序） */
    @Query("SELECT * FROM wish_items WHERE isAchieved = 0 ORDER BY priority DESC, createdAt DESC")
    fun getAllWishItems(): Flow<List<WishItem>>
    
    /** 获取已实现的心愿物品 */
    @Query("SELECT * FROM wish_items WHERE isAchieved = 1 ORDER BY achievedDate DESC")
    fun getAchievedWishItems(): Flow<List<WishItem>>
    
    /** 根据ID获取心愿物品 */
    @Query("SELECT * FROM wish_items WHERE id = :id")
    suspend fun getWishItemById(id: Long): WishItem?
    
    /** 根据ID获取心愿物品（Flow） */
    @Query("SELECT * FROM wish_items WHERE id = :id")
    fun getWishItemFlow(id: Long): Flow<WishItem?>
    
    /** 获取心愿物品数量 */
    @Query("SELECT COUNT(*) FROM wish_items WHERE isAchieved = 0")
    fun getWishItemCount(): Flow<Int>
    
    /** 获取冷静期已过的心愿物品 */
    @Query("SELECT * FROM wish_items WHERE isAchieved = 0 AND (cooldownUntil IS NULL OR cooldownUntil <= :now) ORDER BY priority DESC")
    fun getReadyToBuyWishItems(now: Long = System.currentTimeMillis()): Flow<List<WishItem>>
    
    /** 插入心愿物品 */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWishItem(wishItem: WishItem): Long
    
    /** 更新心愿物品 */
    @Update
    suspend fun updateWishItem(wishItem: WishItem)
    
    /** 删除心愿物品 */
    @Delete
    suspend fun deleteWishItem(wishItem: WishItem)
    
    /** 根据ID删除心愿物品 */
    @Query("DELETE FROM wish_items WHERE id = :id")
    suspend fun deleteWishItemById(id: Long)
    
    /** 标记心愿物品为已实现 */
    @Query("UPDATE wish_items SET isAchieved = 1, achievedDate = :achievedDate, updatedAt = :now WHERE id = :id")
    suspend fun markAsAchieved(id: Long, achievedDate: Long = System.currentTimeMillis(), now: Long = System.currentTimeMillis())
}
