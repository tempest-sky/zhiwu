package com.zhiwu.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.activity.compose.BackHandler
import com.zhiwu.app.data.entity.ItemStatus
import com.zhiwu.app.data.entity.ItemWithDetails
import com.zhiwu.app.ui.animation.AnimationTokens
import com.zhiwu.app.ui.components.*
import com.zhiwu.app.viewmodel.ItemViewModel

/**
 * 物品列表主页面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemListScreen(
    viewModel: ItemViewModel,
    onNavigateToAddItem: () -> Unit,
    onNavigateToEditItem: (Long) -> Unit,
    onNavigateToStatistics: () -> Unit,
    onNavigateToManageCategories: () -> Unit,
    onNavigateToManageTags: () -> Unit,
    onNavigateToWishList: () -> Unit
) {
    val items by viewModel.items.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val tags by viewModel.tags.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategoryId by viewModel.selectedCategoryId.collectAsState()
    val selectedStatus by viewModel.selectedStatus.collectAsState()
    val isGridView by viewModel.isGridView.collectAsState()
    val wishItemCount by viewModel.wishItemCount.collectAsState()
    val warrantyExpiringItems by viewModel.warrantyExpiringItems.collectAsState()
    val shelfLifeExpiringItems by viewModel.shelfLifeExpiringItems.collectAsState()
    
    var showMenu by remember { mutableStateOf(false) }
    var isDataLoaded by remember { mutableStateOf(false) }
    
    // 批量选择状态
    var selectedItems by remember { mutableStateOf(setOf<Long>()) }
    var isSelectionMode by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    
    // 处理返回键 - 退出选择模式而非退出应用
    BackHandler(enabled = isSelectionMode) {
        isSelectionMode = false
        selectedItems = emptySet()
    }
    
    // 监听数据加载完成
    LaunchedEffect(items) {
        if (items.isNotEmpty() || (searchQuery.isEmpty() && selectedCategoryId == null && selectedStatus == null)) {
            isDataLoaded = true
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isSelectionMode) "已选${selectedItems.size}项" else "值物",
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                navigationIcon = {
                    if (isSelectionMode) {
                        IconButton(onClick = {
                            isSelectionMode = false
                            selectedItems = emptySet()
                        }) {
                            Icon(Icons.Default.Close, "取消选择")
                        }
                    }
                },
                actions = {
                    if (isSelectionMode) {
                        // 全选/取消全选
                        IconButton(onClick = {
                            if (selectedItems.size == items.size) {
                                selectedItems = emptySet()
                            } else {
                                selectedItems = items.map { it.item.id }.toSet()
                            }
                        }) {
                            Icon(
                                imageVector = if (selectedItems.size == items.size) Icons.Default.CheckBox 
                                    else Icons.Default.CheckBoxOutlineBlank,
                                contentDescription = "全选"
                            )
                        }
                        
                        // 删除按钮
                        IconButton(onClick = { showDeleteConfirmDialog = true }) {
                            Icon(
                                Icons.Default.Delete,
                                "删除",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    } else {
                        // 心愿清单按钮
                        BadgedBox(
                            badge = {
                                if (wishItemCount > 0) {
                                    Badge(
                                        modifier = Modifier.offset(x = (-4).dp, y = 4.dp)
                                    ) {
                                        Text("$wishItemCount")
                                    }
                                }
                            }
                        ) {
                            IconButton(onClick = onNavigateToWishList) {
                                Icon(
                                    imageVector = Icons.Default.FavoriteBorder,
                                    contentDescription = "心愿清单"
                                )
                            }
                        }
                        
                        // 统计按钮
                        IconButton(onClick = onNavigateToStatistics) {
                            Icon(
                                imageVector = Icons.Outlined.BarChart,
                                contentDescription = "统计"
                            )
                        }
                        
                        // 更多菜单
                        Box {
                            IconButton(onClick = { showMenu = true }) {
                                Icon(
                                    imageVector = Icons.Default.MoreVert,
                                    contentDescription = "更多"
                                )
                            }
                            
                            DropdownMenu(
                                expanded = showMenu,
                                onDismissRequest = { showMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("管理分类") },
                                    onClick = {
                                        showMenu = false
                                        onNavigateToManageCategories()
                                    },
                                    leadingIcon = {
                                        Icon(Icons.Default.Category, null)
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("管理标签") },
                                    onClick = {
                                        showMenu = false
                                        onNavigateToManageTags()
                                    },
                                    leadingIcon = {
                                        Icon(Icons.Default.Label, null)
                                    }
                                )
                            }
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            // 底部悬浮栏：视图切换 + 添加按钮
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 视图切换按钮
                GlassFloatingActionButton(
                    onClick = { viewModel.toggleViewMode() },
                    icon = if (isGridView) Icons.Default.ViewList else Icons.Default.GridView,
                    contentDescription = if (isGridView) "列表视图" else "网格视图"
                )
                
                // 添加按钮
                GlassFloatingActionButton(
                    onClick = onNavigateToAddItem,
                    icon = Icons.Default.Add,
                    contentDescription = "添加物品"
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 搜索栏
            GlassSearchBar(
                query = searchQuery,
                onQueryChange = { viewModel.setSearchQuery(it) },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            
            // 分类筛选
            CategoryPicker(
                categories = categories,
                selectedCategoryId = selectedCategoryId,
                onCategorySelected = { viewModel.setSelectedCategory(it) },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
            
            // 状态筛选
            StatusFilterChips(
                selectedStatus = selectedStatus,
                onStatusSelected = { viewModel.setSelectedStatus(it) },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 内容区域
            when {
                !isDataLoaded -> {
                    SkeletonLoader(
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
                items.isEmpty() -> {
                    EmptyState()
                }
                else -> {
                    AnimatedContent(
                        targetState = isGridView,
                        transitionSpec = {
                            fadeIn(
                                animationSpec = tween(AnimationTokens.DURATION_MEDIUM1)
                            ) togetherWith fadeOut(
                                animationSpec = tween(AnimationTokens.DURATION_MEDIUM1)
                            )
                        },
                        label = "contentMode"
                    ) { isGrid ->
                        if (isGrid) {
                            ItemGridView(
                                items = items,
                                onItemClick = { onNavigateToEditItem(it.item.id) }
                            )
                        } else {
                            ItemListView(
                                items = items,
                                warrantyExpiringItems = warrantyExpiringItems,
                                shelfLifeExpiringItems = shelfLifeExpiringItems,
                                selectedItems = selectedItems,
                                isSelectionMode = isSelectionMode,
                                onItemClick = { onNavigateToEditItem(it.item.id) },
                                onLongPress = { itemId ->
                                    isSelectionMode = true
                                    selectedItems = setOf(itemId)
                                },
                                onToggleSelect = { itemId ->
                                    selectedItems = if (itemId in selectedItems) {
                                        selectedItems - itemId
                                    } else {
                                        selectedItems + itemId
                                    }
                                    if (selectedItems.isEmpty()) {
                                        isSelectionMode = false
                                    }
                                },
                                onDeleteItem = { viewModel.deleteItem(it) }
                            )
                        }
                    }
                }
            }
        }
    }
    
    // 批量删除确认对话框
    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = { Text("确认删除") },
            text = { Text("确定要删除选中的 ${selectedItems.size} 个物品吗？") },
            confirmButton = {
                TextButton(
                    onClick = {
                        // 批量删除
                        items.filter { it.item.id in selectedItems }.forEach {
                            viewModel.deleteItem(it.item)
                        }
                        selectedItems = emptySet()
                        isSelectionMode = false
                        showDeleteConfirmDialog = false
                    }
                ) {
                    Text("删除", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = false }) {
                    Text("取消")
                }
            }
        )
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatusFilterChips(
    selectedStatus: String?,
    onStatusSelected: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            FilterChip(
                selected = selectedStatus == null,
                onClick = { onStatusSelected(null) },
                label = { Text("全部") }
            )
        }
        
        items(ItemStatus.values().toList()) { status ->
            FilterChip(
                selected = selectedStatus == status.name,
                onClick = { onStatusSelected(status.name) },
                label = { Text(status.displayName) },
                leadingIcon = {
                    Icon(
                        imageVector = status.icon,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ItemListView(
    items: List<ItemWithDetails>,
    warrantyExpiringItems: List<ItemWithDetails>,
    shelfLifeExpiringItems: List<ItemWithDetails>,
    selectedItems: Set<Long>,
    isSelectionMode: Boolean,
    onItemClick: (ItemWithDetails) -> Unit,
    onLongPress: (Long) -> Unit,
    onToggleSelect: (Long) -> Unit,
    onDeleteItem: (com.zhiwu.app.data.entity.Item) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // 到期提醒
        if (warrantyExpiringItems.isNotEmpty() || shelfLifeExpiringItems.isNotEmpty()) {
            item(key = "expiry_alerts") {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (warrantyExpiringItems.isNotEmpty()) {
                        ExpiryAlertCard(
                            title = "保修期即将到期",
                            items = warrantyExpiringItems.map { it.item.name }
                        )
                    }
                    if (shelfLifeExpiringItems.isNotEmpty()) {
                        ExpiryAlertCard(
                            title = "保质期即将到期",
                            items = shelfLifeExpiringItems.map { it.item.name }
                        )
                    }
                }
            }
        }
        
        items(
            items = items,
            key = { it.item.id }
        ) { itemWithDetails ->
            val isSelected = itemWithDetails.item.id in selectedItems
            
            ItemListCard(
                itemWithDetails = itemWithDetails,
                isSelected = isSelected,
                isSelectionMode = isSelectionMode,
                onClick = {
                    if (isSelectionMode) {
                        onToggleSelect(itemWithDetails.item.id)
                    } else {
                        onItemClick(itemWithDetails)
                    }
                },
                onLongClick = { onLongPress(itemWithDetails.item.id) },
                modifier = Modifier.animateItemPlacement(
                    animationSpec = tween(
                        durationMillis = AnimationTokens.DURATION_MEDIUM2,
                        easing = AnimationTokens.EASING_STANDARD
                    )
                )
            )
        }
    }
}

@Composable
private fun ItemGridView(
    items: List<ItemWithDetails>,
    onItemClick: (ItemWithDetails) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = items,
            key = { it.item.id }
        ) { itemWithDetails ->
            ItemGridCard(
                itemWithDetails = itemWithDetails,
                onClick = { onItemClick(itemWithDetails) }
            )
        }
    }
}
