package com.zhiwu.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.zhiwu.app.data.entity.ItemStatus

/**
 * 物品状态选择器
 */
@Composable
fun ItemStatusSelector(
    selectedStatus: ItemStatus,
    onStatusSelected: (ItemStatus) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "物品状态",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(Modifier.height(8.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ItemStatus.values().forEach { status ->
                FilterChip(
                    selected = selectedStatus == status,
                    onClick = { onStatusSelected(status) },
                    label = { Text(status.displayName) },
                    leadingIcon = {
                        Icon(
                            imageVector = status.icon,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

/**
 * 物品状态标签
 */
@Composable
fun ItemStatusChip(
    status: ItemStatus,
    modifier: Modifier = Modifier
) {
    val color = when (status) {
        ItemStatus.IN_USE -> MaterialTheme.colorScheme.primary
        ItemStatus.IDLE -> MaterialTheme.colorScheme.tertiary
        ItemStatus.SOLD -> MaterialTheme.colorScheme.secondary
        ItemStatus.DISCARDED -> MaterialTheme.colorScheme.error
    }
    
    SuggestionChip(
        onClick = {},
        label = { Text(status.displayName) },
        icon = {
            Icon(
                imageVector = status.icon,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
        },
        modifier = modifier,
        colors = SuggestionChipDefaults.suggestionChipColors(
            containerColor = color.copy(alpha = 0.1f),
            labelColor = color,
            iconContentColor = color
        )
    )
}

/**
 * 物品状态扩展属性
 */
val ItemStatus.displayName: String
    get() = when (this) {
        ItemStatus.IN_USE -> "使用中"
        ItemStatus.IDLE -> "闲置"
        ItemStatus.SOLD -> "已售出"
        ItemStatus.DISCARDED -> "已丢弃"
    }

val ItemStatus.icon: ImageVector
    get() = when (this) {
        ItemStatus.IN_USE -> Icons.Default.CheckCircle
        ItemStatus.IDLE -> Icons.Default.PauseCircle
        ItemStatus.SOLD -> Icons.Default.AttachMoney
        ItemStatus.DISCARDED -> Icons.Default.Delete
    }

/**
 * 保修期/保质期设置组件
 */
@Composable
fun ExpiryDatePicker(
    label: String,
    expiryDate: Long?,
    onDateSelected: (Long?) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val dateFormat = remember { java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()) }
    
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(Modifier.height(4.dp))
        
        OutlinedTextField(
            value = if (expiryDate != null) {
                dateFormat.format(java.util.Date(expiryDate))
            } else {
                ""
            },
            onValueChange = {},
            label = { Text("选择日期") },
            readOnly = true,
            trailingIcon = {
                Row {
                    if (expiryDate != null) {
                        IconButton(onClick = { onDateSelected(null) }) {
                            Icon(Icons.Default.Clear, "清除")
                        }
                    }
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.CalendarMonth, "选择日期")
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
    
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = expiryDate ?: System.currentTimeMillis()
        )
        
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let {
                            onDateSelected(it)
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("确定")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("取消")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

/**
 * 日均价格显示组件
 */
@Composable
fun DailyPriceDisplay(
    purchasePrice: Double,
    purchaseDate: Long,
    soldDate: Long?,
    modifier: Modifier = Modifier
) {
    val holdingDays = remember(purchaseDate, soldDate) {
        val endDate = soldDate ?: System.currentTimeMillis()
        ((endDate - purchaseDate) / (1000 * 60 * 60 * 24)).toInt().coerceAtLeast(1)
    }
    
    val dailyPrice = remember(purchasePrice, holdingDays) {
        purchasePrice / holdingDays
    }
    
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "持有 ${holdingDays} 天",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "日均 ¥${String.format("%.2f", dailyPrice)}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

/**
 * 到期提醒卡片
 */
@Composable
fun ExpiryAlertCard(
    title: String,
    items: List<String>,
    modifier: Modifier = Modifier
) {
    if (items.isNotEmpty()) {
        GlassCard(modifier = modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                
                items.forEach { item ->
                    Text(
                        text = "• $item",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
