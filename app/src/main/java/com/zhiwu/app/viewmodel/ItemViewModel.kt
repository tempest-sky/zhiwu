package com.zhiwu.app.viewmodel

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.zhiwu.app.data.database.ZhiWuDatabase
import com.zhiwu.app.data.entity.*
import com.zhiwu.app.data.repository.CategoryRepository
import com.zhiwu.app.data.repository.ItemRepository
import com.zhiwu.app.data.repository.TagRepository
import com.zhiwu.app.data.repository.WishItemRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

/**
 * 物品视图模型
 * 管理物品相关的UI状态和业务逻辑
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ItemViewModel(application: Application) : AndroidViewModel(application) {
    
    private val database = ZhiWuDatabase.getDatabase(application)
    private val itemRepository = ItemRepository(database.itemDao())
    private val categoryRepository = CategoryRepository(database.categoryDao())
    private val tagRepository = TagRepository(database.tagDao())
    private val wishItemRepository = WishItemRepository(database.wishItemDao())
    
    // ==================== 筛选状态 ====================
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    private val _selectedCategoryId = MutableStateFlow<Long?>(null)
    val selectedCategoryId: StateFlow<Long?> = _selectedCategoryId.asStateFlow()
    
    private val _selectedTagId = MutableStateFlow<Long?>(null)
    val selectedTagId: StateFlow<Long?> = _selectedTagId.asStateFlow()
    
    private val _isGridView = MutableStateFlow(false)
    val isGridView: StateFlow<Boolean> = _isGridView.asStateFlow()
    
    private val _selectedStatus = MutableStateFlow<String?>(null)
    val selectedStatus: StateFlow<String?> = _selectedStatus.asStateFlow()
    
    // ==================== 数据流 ====================
    
    // 使用Eagerly策略，确保数据在页面返回时立即可用，无需重新加载
    val categories: StateFlow<List<Category>> = categoryRepository.allCategories
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    
    val tags: StateFlow<List<Tag>> = tagRepository.allTags
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    
    val totalItemCount: StateFlow<Int> = itemRepository.totalItemCount
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0)
    
    val inUseItemCount: StateFlow<Int> = itemRepository.getInUseItemCount()
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0)
    
    val totalCost: StateFlow<Double> = itemRepository.totalCost
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0.0)
    
    val totalSoldIncome: StateFlow<Double> = itemRepository.getTotalSoldIncome()
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0.0)
    
    val categoryStats: StateFlow<List<CategoryWithCount>> = itemRepository.categoryStats
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    
    // 心愿清单
    val wishItems: StateFlow<List<WishItem>> = wishItemRepository.allWishItems
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    
    val achievedWishItems: StateFlow<List<WishItem>> = wishItemRepository.achievedWishItems
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    
    val wishItemCount: StateFlow<Int> = wishItemRepository.wishItemCount
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0)
    
    val readyToBuyWishItems: StateFlow<List<WishItem>> = wishItemRepository.readyToBuyWishItems
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    
    // 即将到期提醒
    val warrantyExpiringItems: StateFlow<List<ItemWithDetails>> = itemRepository.getWarrantyExpiringItems()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    
    val shelfLifeExpiringItems: StateFlow<List<ItemWithDetails>> = itemRepository.getShelfLifeExpiringItems()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    
    /** 物品列表（根据筛选条件动态变化） */
    val items: StateFlow<List<ItemWithDetails>> = combine(
        _searchQuery,
        _selectedCategoryId,
        _selectedTagId,
        _selectedStatus
    ) { query, categoryId, tagId, status ->
        arrayOf(query, categoryId, tagId, status)
    }.flatMapLatest { (query, categoryId, tagId, status) ->
        when {
            query.toString().isNotBlank() -> itemRepository.searchItems(query.toString())
            categoryId != null -> itemRepository.getItemsByCategory(categoryId as Long)
            tagId != null -> itemRepository.getItemsByTag(tagId as Long)
            status != null -> itemRepository.getItemsByStatus(status.toString())
            else -> itemRepository.allItems
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    
    // ==================== 操作方法 ====================
    
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }
    
    fun setSelectedCategory(categoryId: Long?) {
        _selectedCategoryId.value = categoryId
        _selectedTagId.value = null
        _selectedStatus.value = null
    }
    
    fun setSelectedTag(tagId: Long?) {
        _selectedTagId.value = tagId
        _selectedCategoryId.value = null
        _selectedStatus.value = null
    }
    
    fun setSelectedStatus(status: String?) {
        _selectedStatus.value = status
        _selectedCategoryId.value = null
        _selectedTagId.value = null
    }
    
    fun clearFilters() {
        _searchQuery.value = ""
        _selectedCategoryId.value = null
        _selectedTagId.value = null
        _selectedStatus.value = null
    }
    
    fun toggleViewMode() {
        _isGridView.value = !_isGridView.value
    }
    
    fun getItemWithDetails(itemId: Long): Flow<ItemWithDetails?> {
        return itemRepository.getItemWithDetails(itemId)
    }
    
    fun saveItem(
        name: String,
        price: Double,
        purchaseDate: Long,
        categoryId: Long,
        tagIds: List<Long>,
        imagePath: String?,
        notes: String?,
        existingItem: Item? = null,
        warrantyExpiry: Long? = null,
        shelfLifeExpiry: Long? = null,
        purchaseChannel: String? = null,
        relatedLink: String? = null
    ) {
        viewModelScope.launch {
            val item = if (existingItem != null) {
                existingItem.copy(
                    name = name,
                    price = price,
                    purchaseDate = purchaseDate,
                    categoryId = categoryId,
                    imagePath = imagePath,
                    notes = notes,
                    warrantyExpiry = warrantyExpiry,
                    shelfLifeExpiry = shelfLifeExpiry,
                    purchaseChannel = purchaseChannel,
                    relatedLink = relatedLink,
                    updatedAt = System.currentTimeMillis()
                )
            } else {
                Item(
                    name = name,
                    price = price,
                    purchaseDate = purchaseDate,
                    categoryId = categoryId,
                    imagePath = imagePath,
                    notes = notes,
                    warrantyExpiry = warrantyExpiry,
                    shelfLifeExpiry = shelfLifeExpiry,
                    purchaseChannel = purchaseChannel,
                    relatedLink = relatedLink
                )
            }
            
            if (existingItem != null) {
                itemRepository.updateItem(item, tagIds)
            } else {
                itemRepository.insertItem(item, tagIds)
            }
        }
    }
    
    fun deleteItem(item: Item) {
        viewModelScope.launch {
            itemRepository.deleteItem(item)
        }
    }
    
    // 物品状态管理
    fun markItemAsSold(itemId: Long, soldPrice: Double) {
        viewModelScope.launch {
            itemRepository.markAsSold(itemId, soldPrice)
        }
    }
    
    fun markItemAsIdle(itemId: Long) {
        viewModelScope.launch {
            itemRepository.markAsIdle(itemId)
        }
    }
    
    fun markItemAsInUse(itemId: Long) {
        viewModelScope.launch {
            itemRepository.markAsInUse(itemId)
        }
    }
    
    fun incrementUsageCount(itemId: Long) {
        viewModelScope.launch {
            itemRepository.incrementUsageCount(itemId)
        }
    }
    
    // 心愿清单操作
    fun saveWishItem(
        name: String,
        expectedPrice: Double?,
        categoryId: Long?,
        notes: String?,
        cooldownDays: Int?,
        priority: Int,
        existingWishItem: WishItem? = null
    ) {
        viewModelScope.launch {
            val cooldownUntil = cooldownDays?.let {
                System.currentTimeMillis() + it.toLong() * 24 * 60 * 60 * 1000
            }
            
            val wishItem = if (existingWishItem != null) {
                existingWishItem.copy(
                    name = name,
                    expectedPrice = expectedPrice,
                    categoryId = categoryId,
                    notes = notes,
                    cooldownUntil = cooldownUntil,
                    priority = priority,
                    updatedAt = System.currentTimeMillis()
                )
            } else {
                WishItem(
                    name = name,
                    expectedPrice = expectedPrice,
                    categoryId = categoryId,
                    notes = notes,
                    cooldownUntil = cooldownUntil,
                    priority = priority
                )
            }
            
            wishItemRepository.insertWishItem(wishItem)
        }
    }
    
    fun deleteWishItem(wishItem: WishItem) {
        viewModelScope.launch {
            wishItemRepository.deleteWishItem(wishItem)
        }
    }
    
    fun markWishItemAsAchieved(wishItemId: Long) {
        viewModelScope.launch {
            wishItemRepository.markAsAchieved(wishItemId)
        }
    }
    
    fun getWishItemById(id: Long): Flow<WishItem?> {
        return wishItemRepository.getWishItemFlow(id)
    }
    
    // 分类操作
    fun saveCategory(name: String, icon: String = "category") {
        viewModelScope.launch {
            categoryRepository.insertCategory(Category(name = name, icon = icon))
        }
    }
    
    fun updateCategory(category: Category) {
        viewModelScope.launch {
            categoryRepository.updateCategory(category)
        }
    }
    
    fun deleteCategory(category: Category) {
        viewModelScope.launch {
            categoryRepository.deleteCategory(category)
        }
    }
    
    fun updateCategorySortOrders(categories: List<Category>) {
        viewModelScope.launch {
            categoryRepository.updateSortOrders(categories)
        }
    }
    
    // 标签操作
    fun saveTag(name: String) {
        viewModelScope.launch {
            tagRepository.insertTag(Tag(name = name))
        }
    }
    
    fun updateTag(tag: Tag) {
        viewModelScope.launch {
            tagRepository.updateTag(tag)
        }
    }
    
    fun deleteTag(tag: Tag) {
        viewModelScope.launch {
            tagRepository.deleteTag(tag)
        }
    }
    
    /**
     * 保存图片到应用内部存储
     * @return 保存后的文件路径
     */
    fun saveImageToInternal(context: Context, uri: Uri): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val imageDir = File(context.filesDir, "images")
            if (!imageDir.exists()) imageDir.mkdirs()
            
            val fileName = "img_${System.currentTimeMillis()}.jpg"
            val file = File(imageDir, fileName)
            
            FileOutputStream(file).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
            inputStream.close()
            
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    /**
     * 删除图片文件
     */
    fun deleteImageFile(imagePath: String?) {
        imagePath?.let { path ->
            val file = File(path)
            if (file.exists()) {
                file.delete()
            }
        }
    }
}
