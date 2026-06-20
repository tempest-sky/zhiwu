package com.zhiwu.app.data.repository

import com.zhiwu.app.data.dao.WishItemDao
import com.zhiwu.app.data.entity.WishItem
import kotlinx.coroutines.flow.Flow

/**
 * 心愿清单仓库
 */
class WishItemRepository(private val wishItemDao: WishItemDao) {
    
    /** 所有心愿物品 */
    val allWishItems: Flow<List<WishItem>> = wishItemDao.getAllWishItems()
    
    /** 已实现的心愿物品 */
    val achievedWishItems: Flow<List<WishItem>> = wishItemDao.getAchievedWishItems()
    
    /** 心愿物品数量 */
    val wishItemCount: Flow<Int> = wishItemDao.getWishItemCount()
    
    /** 冷静期已过的心愿物品 */
    val readyToBuyWishItems: Flow<List<WishItem>> = wishItemDao.getReadyToBuyWishItems()
    
    /** 根据ID获取心愿物品 */
    suspend fun getWishItemById(id: Long): WishItem? {
        return wishItemDao.getWishItemById(id)
    }
    
    /** 根据ID获取心愿物品（Flow） */
    fun getWishItemFlow(id: Long): Flow<WishItem?> {
        return wishItemDao.getWishItemFlow(id)
    }
    
    /** 插入心愿物品 */
    suspend fun insertWishItem(wishItem: WishItem): Long {
        return wishItemDao.insertWishItem(wishItem)
    }
    
    /** 更新心愿物品 */
    suspend fun updateWishItem(wishItem: WishItem) {
        wishItemDao.updateWishItem(wishItem)
    }
    
    /** 删除心愿物品 */
    suspend fun deleteWishItem(wishItem: WishItem) {
        wishItemDao.deleteWishItem(wishItem)
    }
    
    /** 标记心愿物品为已实现 */
    suspend fun markAsAchieved(id: Long) {
        wishItemDao.markAsAchieved(id)
    }
}
