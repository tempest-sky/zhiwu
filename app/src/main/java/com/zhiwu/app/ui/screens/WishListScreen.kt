package com.zhiwu.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zhiwu.app.data.entity.WishItem
import com.zhiwu.app.ui.components.*
import com.zhiwu.app.viewmodel.ItemViewModel

/**
 * 心愿清单页面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WishListScreen(
    viewModel: ItemViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToAddWish: () -> Unit
) {
    val wishItems by viewModel.wishItems.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("心愿清单") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "返回")
                    }
                },
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.Add, "添加心愿")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (wishItems.isEmpty()) {
            // 空状态
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FavoriteBorder,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                    Text(
                        text = "还没有心愿物品",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "点击右上角添加你想要的物品",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = wishItems,
                    key = { it.id }
                ) { wishItem ->
                    WishItemCard(
                        wishItem = wishItem,
                        onAchieve = { viewModel.markWishItemAsAchieved(wishItem.id) },
                        onDelete = { viewModel.deleteWishItem(wishItem) }
                    )
                }
            }
        }
    }
    
    // 添加心愿对话框
    if (showAddDialog) {
        AddWishItemDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { name, expectedPrice, priority, cooldownDays, notes ->
                viewModel.saveWishItem(
                    name = name,
                    expectedPrice = expectedPrice,
                    categoryId = null,
                    notes = notes,
                    cooldownDays = cooldownDays,
                    priority = priority
                )
                showAddDialog = false
            }
        )
    }
}

/**
 * 心愿物品卡片
 */
@Composable
fun WishItemCard(
    wishItem: WishItem,
    onAchieve: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = wishItem.name,
                        style = MaterialTheme.typography.titleMedium
                    )
                    if (wishItem.expectedPrice != null) {
                        Text(
                            text = "预期价格: ¥${String.format("%.2f", wishItem.expectedPrice)}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                // 优先级显示
                PriorityIndicator(priority = wishItem.priority)
            }
            
            if (wishItem.notes != null) {
                Text(
                    text = wishItem.notes,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // 冷静期状态
            if (wishItem.cooldownUntil != null) {
                val remainingDays = wishItem.getRemainingCooldownDays()
                if (remainingDays > 0) {
                    Text(
                        text = "冷静期剩余 ${remainingDays} 天",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                } else {
                    Text(
                        text = "冷静期已过，可以购买",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            // 操作按钮
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = { showDeleteDialog = true }
                ) {
                    Text("删除", color = MaterialTheme.colorScheme.error)
                }
                
                Spacer(Modifier.width(8.dp))
                
                Button(
                    onClick = onAchieve,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(Icons.Default.Check, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("已入手")
                }
            }
        }
    }
    
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("确认删除") },
            text = { Text("确定要删除心愿「${wishItem.name}」吗？") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
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

/**
 * 优先级指示器
 */
@Composable
fun PriorityIndicator(priority: Int) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        repeat(5) { index ->
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = if (index < priority) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                }
            )
        }
    }
}

/**
 * 添加心愿对话框
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddWishItemDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, expectedPrice: Double?, priority: Int, cooldownDays: Int?, notes: String?) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var priceText by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf(3) }
    var cooldownDaysText by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var nameError by remember { mutableStateOf(false) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("添加心愿物品") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        nameError = false
                    },
                    label = { Text("物品名称 *") },
                    isError = nameError,
                    supportingText = if (nameError) {
                        { Text("请输入物品名称") }
                    } else null,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = priceText,
                    onValueChange = { priceText = it },
                    label = { Text("预期价格") },
                    prefix = { Text("¥") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                
                // 优先级选择
                Text(
                    text = "优先级",
                    style = MaterialTheme.typography.bodyMedium
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    (1..5).forEach { level ->
                        FilterChip(
                            selected = priority == level,
                            onClick = { priority = level },
                            label = { Text("$level") }
                        )
                    }
                }
                
                OutlinedTextField(
                    value = cooldownDaysText,
                    onValueChange = { cooldownDaysText = it },
                    label = { Text("冷静期天数") },
                    suffix = { Text("天") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("备注/想要的原因") },
                    minLines = 2,
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    nameError = name.isBlank()
                    if (!nameError) {
                        onConfirm(
                            name.trim(),
                            priceText.toDoubleOrNull(),
                            priority,
                            cooldownDaysText.toIntOrNull(),
                            notes.ifBlank { null }
                        )
                    }
                }
            ) {
                Text("添加")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}
