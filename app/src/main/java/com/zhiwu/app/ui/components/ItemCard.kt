package com.zhiwu.app.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.zhiwu.app.data.entity.ItemStatus
import com.zhiwu.app.data.entity.ItemWithDetails
import com.zhiwu.app.ui.animation.clickScale
import com.zhiwu.app.ui.theme.PriceColor
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * 物品列表卡片
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ItemListCard(
    itemWithDetails: ItemWithDetails,
    isSelected: Boolean = false,
    isSelectionMode: Boolean = false,
    onClick: () -> Unit,
    onLongClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val dateFormat = remember { SimpleDateFormat("yy/MM/dd", Locale.getDefault()) }
    val itemStatus = remember { ItemStatus.valueOf(itemWithDetails.item.status) }
    
    GlassCard(
        modifier = modifier
            .clickScale(interactionSource)
            .combinedClickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
                onLongClick = onLongClick
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 选择模式下的复选框
            if (isSelectionMode) {
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = { onClick() }
                )
            }
            
            // 图片 - 增大尺寸
            ItemImage(
                imagePath = itemWithDetails.item.imagePath,
                size = 72
            )
            
            // 信息区域
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // 第一行：物品名称
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = itemWithDetails.item.name,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    
                    // 物品状态标签
                    if (itemStatus != ItemStatus.IN_USE) {
                        ItemStatusChip(status = itemStatus)
                    }
                }
                
                // 第二行：分类 + 日期
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = itemWithDetails.category.name,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    Text(
                        text = dateFormat.format(Date(itemWithDetails.item.purchaseDate)),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }
                
                // 第三行：已购天数 + 日均成本
                val holdingDays = itemWithDetails.item.getHoldingDays()
                val dailyPrice = itemWithDetails.item.calculateDailyPrice()
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "已购${holdingDays}天",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    if (holdingDays > 0) {
                        Text(
                            text = "日均¥${String.format("%.1f", dailyPrice)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                
                if (itemWithDetails.tags.isNotEmpty()) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        itemWithDetails.tags.take(3).forEach { tag ->
                            TagChip(name = tag.name)
                        }
                        if (itemWithDetails.tags.size > 3) {
                            TagChip(name = "+${itemWithDetails.tags.size - 3}")
                        }
                    }
                }
            }
            
            // 价格 - 靠顶部显示，只占一行高度
            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier.align(Alignment.Top)
            ) {
                Text(
                    text = "¥${String.format("%.2f", itemWithDetails.item.price)}",
                    style = MaterialTheme.typography.titleMedium,
                    color = PriceColor
                )
                
                // 售出价格
                if (itemWithDetails.item.soldPrice != null) {
                    Text(
                        text = "售¥${String.format("%.0f", itemWithDetails.item.soldPrice)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }
}

/**
 * 物品网格卡片
 */
@Composable
fun ItemGridCard(
    itemWithDetails: ItemWithDetails,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val dateFormat = remember { SimpleDateFormat("yy/MM/dd", Locale.getDefault()) }
    val itemStatus = remember { ItemStatus.valueOf(itemWithDetails.item.status) }
    
    GlassCard(
        modifier = modifier
            .clickScale(interactionSource)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
    ) {
        Column {
            // 图片区域
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            ) {
                if (itemWithDetails.item.imagePath != null) {
                    AsyncImage(
                        model = File(itemWithDetails.item.imagePath),
                        contentDescription = itemWithDetails.item.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }
                
                // 状态标签
                if (itemStatus != ItemStatus.IN_USE) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                    ) {
                        ItemStatusChip(status = itemStatus)
                    }
                }
            }
            
            // 信息区域
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = itemWithDetails.item.name,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = itemWithDetails.category.name,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                    
                    Text(
                        text = "¥${String.format("%.0f", itemWithDetails.item.price)}",
                        style = MaterialTheme.typography.titleSmall,
                        color = PriceColor
                    )
                }
                
                Text(
                    text = dateFormat.format(Date(itemWithDetails.item.purchaseDate)),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
        }
    }
}

/**
 * 物品图片组件
 */
@Composable
fun ItemImage(
    imagePath: String?,
    size: Int = 56,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(12.dp)
    
    Box(
        modifier = modifier
            .size(size.dp)
            .clip(shape),
        contentAlignment = Alignment.Center
    ) {
        if (imagePath != null) {
            AsyncImage(
                model = File(imagePath),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Surface(
                modifier = Modifier.fillMaxSize(),
                shape = shape,
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Icon(
                    imageVector = Icons.Default.Image,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                    modifier = Modifier
                        .padding((size / 4).dp)
                )
            }
        }
    }
}
