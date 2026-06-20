package com.zhiwu.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zhiwu.app.data.entity.Category
import com.zhiwu.app.ui.components.GlassCard
import com.zhiwu.app.viewmodel.ItemViewModel

/**
 * 分类管理页面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageCategoriesScreen(
    viewModel: ItemViewModel,
    onNavigateBack: () -> Unit
) {
    val categories by viewModel.categories.collectAsState()
    
    var showAddDialog by remember { mutableStateOf(false) }
    var editingCategory by remember { mutableStateOf<Category?>(null) }
    var deletingCategory by remember { mutableStateOf<Category?>(null) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("管理分类") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "返回")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true }
            ) {
                Icon(Icons.Default.Add, "添加分类")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(
                items = categories,
                key = { _, category -> category.id }
            ) { index, category ->
                CategoryItem(
                    category = category,
                    index = index,
                    totalCount = categories.size,
                    onMoveUp = {
                        if (index > 0) {
                            val newList = categories.toMutableList()
                            val item = newList.removeAt(index)
                            newList.add(index - 1, item)
                            viewModel.updateCategorySortOrders(newList)
                        }
                    },
                    onMoveDown = {
                        if (index < categories.size - 1) {
                            val newList = categories.toMutableList()
                            val item = newList.removeAt(index)
                            newList.add(index + 1, item)
                            viewModel.updateCategorySortOrders(newList)
                        }
                    },
                    onEdit = { editingCategory = category },
                    onDelete = { deletingCategory = category }
                )
            }
        }
    }
    
    // 添加分类对话框
    if (showAddDialog) {
        CategoryDialog(
            title = "添加分类",
            initialName = "",
            onDismiss = { showAddDialog = false },
            onConfirm = { name ->
                viewModel.saveCategory(name)
                showAddDialog = false
            }
        )
    }
    
    // 编辑分类对话框
    editingCategory?.let { category ->
        CategoryDialog(
            title = "编辑分类",
            initialName = category.name,
            onDismiss = { editingCategory = null },
            onConfirm = { name ->
                viewModel.updateCategory(category.copy(name = name))
                editingCategory = null
            }
        )
    }
    
    // 删除确认对话框
    deletingCategory?.let { category ->
        AlertDialog(
            onDismissRequest = { deletingCategory = null },
            title = { Text("确认删除") },
            text = { Text("确定要删除分类「${category.name}」吗？") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteCategory(category)
                        deletingCategory = null
                    }
                ) {
                    Text("删除", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { deletingCategory = null }) {
                    Text("取消")
                }
            }
        )
    }
}

@Composable
private fun CategoryItem(
    category: Category,
    index: Int,
    totalCount: Int,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 排序按钮
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IconButton(
                    onClick = onMoveUp,
                    enabled = index > 0,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        Icons.Default.KeyboardArrowUp,
                        "上移",
                        modifier = Modifier.size(20.dp)
                    )
                }
                IconButton(
                    onClick = onMoveDown,
                    enabled = index < totalCount - 1,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        Icons.Default.KeyboardArrowDown,
                        "下移",
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Icon(
                imageVector = getCategoryIcon(category.icon),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = category.name,
                    style = MaterialTheme.typography.titleMedium
                )
                if (category.isPreset) {
                    Text(
                        text = "预设分类",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            if (!category.isPreset) {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, "编辑")
                }
                
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        "删除",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryDialog(
    title: String,
    initialName: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var name by remember { mutableStateOf(initialName) }
    var isError by remember { mutableStateOf(false) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it
                    isError = false
                },
                label = { Text("分类名称") },
                isError = isError,
                supportingText = if (isError) {
                    { Text("请输入分类名称") }
                } else null,
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isBlank()) {
                        isError = true
                    } else {
                        onConfirm(name.trim())
                    }
                }
            ) {
                Text("确定")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}

/**
 * 根据图标名称返回对应的Material Icon
 */
@Composable
private fun getCategoryIcon(iconName: String) = when (iconName) {
    "devices" -> Icons.Default.Devices
    "checkroom" -> Icons.Default.Checkroom
    "home" -> Icons.Default.Home
    "restaurant" -> Icons.Default.Restaurant
    "school" -> Icons.Default.School
    "more_horiz" -> Icons.Default.MoreHoriz
    else -> Icons.Default.Category
}
