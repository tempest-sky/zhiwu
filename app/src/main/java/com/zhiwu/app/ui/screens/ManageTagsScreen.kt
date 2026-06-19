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
import com.zhiwu.app.data.entity.Tag
import com.zhiwu.app.ui.components.GlassCard
import com.zhiwu.app.viewmodel.ItemViewModel

/**
 * 标签管理页面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageTagsScreen(
    viewModel: ItemViewModel,
    onNavigateBack: () -> Unit
) {
    val tags by viewModel.tags.collectAsState()
    
    var showAddDialog by remember { mutableStateOf(false) }
    var editingTag by remember { mutableStateOf<Tag?>(null) }
    var deletingTag by remember { mutableStateOf<Tag?>(null) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("管理标签") },
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
                Icon(Icons.Default.Add, "添加标签")
            }
        }
    ) { paddingValues ->
        if (tags.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LabelOff,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                    )
                    Text(
                        text = "暂无标签",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "点击右下角的 + 按钮添加标签",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(tags) { tag ->
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Label,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                            
                            Spacer(modifier = Modifier.width(16.dp))
                            
                            Text(
                                text = tag.name,
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.weight(1f)
                            )
                            
                            IconButton(
                                onClick = { editingTag = tag }
                            ) {
                                Icon(Icons.Default.Edit, "编辑")
                            }
                            
                            IconButton(
                                onClick = { deletingTag = tag }
                            ) {
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
        }
    }
    
    // 添加标签对话框
    if (showAddDialog) {
        TagDialog(
            title = "添加标签",
            initialName = "",
            onDismiss = { showAddDialog = false },
            onConfirm = { name ->
                viewModel.saveTag(name)
                showAddDialog = false
            }
        )
    }
    
    // 编辑标签对话框
    editingTag?.let { tag ->
        TagDialog(
            title = "编辑标签",
            initialName = tag.name,
            onDismiss = { editingTag = null },
            onConfirm = { name ->
                viewModel.updateTag(tag.copy(name = name))
                editingTag = null
            }
        )
    }
    
    // 删除确认对话框
    deletingTag?.let { tag ->
        AlertDialog(
            onDismissRequest = { deletingTag = null },
            title = { Text("确认删除") },
            text = { Text("确定要删除标签「${tag.name}」吗？") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteTag(tag)
                        deletingTag = null
                    }
                ) {
                    Text("删除", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { deletingTag = null }) {
                    Text("取消")
                }
            }
        )
    }
}

@Composable
private fun TagDialog(
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
                label = { Text("标签名称") },
                isError = isError,
                supportingText = if (isError) {
                    { Text("请输入标签名称") }
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