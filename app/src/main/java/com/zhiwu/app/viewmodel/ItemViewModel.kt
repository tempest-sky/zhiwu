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
    
    // ==================== 筛选状态 ====================
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    private val _selectedCategoryId = MutableStateFlow<Long?>(null)
    val selectedCategoryId: StateFlow<Long?> = _selectedCategoryId.asStateFlow()
    
    private val _selectedTagId = MutableStateFlow<Long?>(null)
    val selectedTagId: StateFlow<Long?> = _selectedTagId.asStateFlow()
    
    private val _isGridView = MutableStateFlow(false)
    val isGridView: StateFlow<Boolean> = _isGridView.asStateFlow()
    
    // ==================== 数据流 ====================
    
    val categories: StateFlow<List<Category>> = categoryRepository.allCategories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    
    val tags: StateFlow<List<Tag>> = tagRepository.allTags
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    
    val totalItemCount: StateFlow<Int> = itemRepository.totalItemCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    
    val totalCost: StateFlow<Double> = itemRepository.totalCost
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)
    
    val categoryStats: StateFlow<List<CategoryWithCount>> = itemRepository.categoryStats
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    
    /** 物品列表（根据筛选条件动态变化） */
    val items: StateFlow<List<ItemWithDetails>> = combine(
        _searchQuery,
        _selectedCategoryId,
        _selectedTagId
    ) { query, categoryId, tagId ->
        Triple(query, categoryId, tagId)
    }.flatMapLatest { (query, categoryId, tagId) ->
        when {
            query.isNotBlank() -> itemRepository.searchItems(query)
            categoryId != null -> itemRepository.getItemsByCategory(categoryId)
            tagId != null -> itemRepository.getItemsByTag(tagId)
            else -> itemRepository.allItems
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    
    // ==================== 操作方法 ====================
    
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }
    
    fun setSelectedCategory(categoryId: Long?) {
        _selectedCategoryId.value = categoryId
        _selectedTagId.value = null
    }
    
    fun setSelectedTag(tagId: Long?) {
        _selectedTagId.value = tagId
        _selectedCategoryId.value = null
    }
    
    fun clearFilters() {
        _searchQuery.value = ""
        _selectedCategoryId.value = null
        _selectedTagId.value = null
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
        existingItem: Item? = null
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
                    updatedAt = System.currentTimeMillis()
                )
            } else {
                Item(
                    name = name,
                    price = price,
                    purchaseDate = purchaseDate,
                    categoryId = categoryId,
                    imagePath = imagePath,
                    notes = notes
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