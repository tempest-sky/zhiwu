package com.zhiwu.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
    onNavigateToManageTags: () -> Unit
) {
    val items by viewModel.items.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val tags by viewModel.tags.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategoryId by viewModel.selectedCategoryId.collectAsState()
    val isGridView by viewModel.isGridView.collectAsState()
    
    var showMenu by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }
    
    // 模拟初始加载
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(500)
        isLoading = false
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "值物",
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                actions = {
                    // 视图切换按钮
                    IconButton(onClick = { viewModel.toggleViewMode() }) {
                        AnimatedContent(
                            targetState = isGridView,
                            transitionSpec = {
                                fadeIn(
                                    animationSpec = tween(AnimationTokens.DURATION_SHORT3)
                                ) togetherWith fadeOut(
                                    animationSpec = tween(AnimationTokens.DURATION_SHORT3)
                                )
                            },
                            label = "viewMode"
                        ) { isGrid ->
                            Icon(
                                imageVector = if (isGrid) Icons.Default.ViewList 
                                    else Icons.Default.GridView,
                                contentDescription = if (isGrid) "列表视图" else "网格视图"
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
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddItem,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
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
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 内容区域
            when {
                isLoading -> {
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
                                onItemClick = { onNavigateToEditItem(it.item.id) },
                                onDeleteItem = { viewModel.deleteItem(it) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ItemListView(
    items: List<ItemWithDetails>,
    onItemClick: (ItemWithDetails) -> Unit,
    onDeleteItem: (com.zhiwu.app.data.entity.Item) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = items,
            key = { it.item.id }
        ) { itemWithDetails ->
            var showDeleteDialog by remember { mutableStateOf(false) }
            
            ItemListCard(
                itemWithDetails = itemWithDetails,
                onClick = { onItemClick(itemWithDetails) },
                modifier = Modifier.animateItemPlacement(
                    animationSpec = tween(
                        durationMillis = AnimationTokens.DURATION_MEDIUM2,
                        easing = AnimationTokens.EASING_STANDARD
                    )
                )
            )
            
            if (showDeleteDialog) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    title = { Text("确认删除") },
                    text = { Text("确定要删除「${itemWithDetails.item.name}」吗？") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                onDeleteItem(itemWithDetails.item)
                                showDeleteDialog = false
                            }
                        ) {
                            Text("删除", color = MaterialTheme.colorScheme.error)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDeleteDialog = false }) {
                            Text("取消")
                        }
                    }
                )
            }
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