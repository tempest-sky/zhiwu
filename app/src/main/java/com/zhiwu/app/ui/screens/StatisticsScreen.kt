package com.zhiwu.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.zhiwu.app.data.entity.CategoryWithCount
import com.zhiwu.app.ui.components.GlassCard
import com.zhiwu.app.ui.theme.StatCardColors
import com.zhiwu.app.viewmodel.ItemViewModel

/**
 * 统计页面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    viewModel: ItemViewModel,
    onNavigateBack: () -> Unit
) {
    val totalItemCount by viewModel.totalItemCount.collectAsState()
    val totalCost by viewModel.totalCost.collectAsState()
    val categoryStats by viewModel.categoryStats.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("统计") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "返回")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 总览卡片
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 物品总数
                StatCard(
                    title = "物品总数",
                    value = totalItemCount.toString(),
                    icon = Icons.Default.Inventory2,
                    color = StatCardColors[0],
                    modifier = Modifier.weight(1f)
                )
                
                // 总花费
                StatCard(
                    title = "总花费",
                    value = "¥${String.format("%.2f", totalCost)}",
                    icon = Icons.Default.Payments,
                    color = StatCardColors[1],
                    modifier = Modifier.weight(1f)
                )
            }
            
            // 平均单价
            val avgPrice = if (totalItemCount > 0) totalCost / totalItemCount else 0.0
            StatCard(
                title = "平均单价",
                value = "¥${String.format("%.2f", avgPrice)}",
                icon = Icons.Default.TrendingUp,
                color = StatCardColors[2],
                modifier = Modifier.fillMaxWidth()
            )
            
            // 分类统计
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "分类统计",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    if (categoryStats.isNotEmpty()) {
                        // 饼图
                        CategoryPieChart(
                            stats = categoryStats,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // 分类列表
                        categoryStats.forEachIndexed { index, stat ->
                            CategoryStatItem(
                                name = stat.categoryName,
                                count = stat.itemCount,
                                cost = stat.totalCost,
                                color = StatCardColors[index % StatCardColors.size],
                                totalItems = totalItemCount
                            )
                            
                            if (index < categoryStats.size - 1) {
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    } else {
                        Text(
                            text = "暂无数据",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(32.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    GlassCard(modifier = modifier) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun CategoryPieChart(
    stats: List<CategoryWithCount>,
    modifier: Modifier = Modifier
) {
    val total = stats.sumOf { it.itemCount }.toFloat()
    if (total == 0f) return
    
    var animationProgress by remember { mutableFloatStateOf(0f) }
    
    LaunchedEffect(stats) {
        animate(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 800,
                easing = AnimationTokens.EASING_EMPHASIZED_DECELERATE
            )
        ) { value, _ ->
            animationProgress = value
        }
    }
    
    Canvas(modifier = modifier) {
        val strokeWidth = 40.dp.toPx()
        val radius = (size.minDimension - strokeWidth) / 2
        val center = Offset(size.width / 2, size.height / 2)
        
        var startAngle = -90f
        
        stats.forEachIndexed { index, stat ->
            val sweepAngle = (stat.itemCount / total) * 360f * animationProgress
            val color = StatCardColors[index % StatCardColors.size]
            
            drawArc(
                color = color,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
            
            startAngle += sweepAngle
        }
    }
}

@Composable
private fun CategoryStatItem(
    name: String,
    count: Int,
    cost: Double,
    color: Color,
    totalItems: Int
) {
    val percentage = if (totalItems > 0) (count.toFloat() / totalItems * 100) else 0f
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 颜色指示器
        Canvas(modifier = Modifier.size(12.dp)) {
            drawCircle(color = color)
        }
        
        // 分类名称
        Text(
            text = name,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
        
        // 数量
        Text(
            text = "${count}件",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        // 百分比
        Text(
            text = "${String.format("%.1f", percentage)}%",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        // 金额
        Text(
            text = "¥${String.format("%.0f", cost)}",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}