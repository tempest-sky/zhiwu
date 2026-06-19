package com.zhiwu.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * 骨架屏加载动画
 * 用于数据加载时的占位显示
 */
@Composable
fun SkeletonLoader(
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    val baseColor = if (isDark) Color(0xFF2A2A2A) else Color(0xFFE0E0E0)
    val highlightColor = if (isDark) Color(0xFF3A3A3A) else Color(0xFFF0F0F0)
    
    val infiniteTransition = rememberInfiniteTransition(label = "skeleton")
    val shimmerOffset by infiniteTransition.animateFloat(
        initialValue = -1000f,
        targetValue = 2000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1200,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )
    
    val shimmerBrush = Brush.linearGradient(
        colors = listOf(
            baseColor,
            highlightColor,
            baseColor
        ),
        start = Offset(shimmerOffset - 300f, 0f),
        end = Offset(shimmerOffset, 0f)
    )
    
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        repeat(5) {
            SkeletonItem(brush = shimmerBrush)
        }
    }
}

@Composable
private fun SkeletonItem(brush: Brush) {
    val shape = RoundedCornerShape(16.dp)
    
    GlassCard {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 图片占位
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(brush)
            )
            
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 标题占位
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(16.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(brush)
                )
                
                // 副标题占位
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.4f)
                        .height(12.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(brush)
                )
                
                // 标签占位
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .width(48.dp)
                            .height(20.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(brush)
                    )
                    Box(
                        modifier = Modifier
                            .width(64.dp)
                            .height(20.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(brush)
                    )
                }
            }
            
            // 价格占位
            Box(
                modifier = Modifier
                    .width(60.dp)
                    .height(20.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(brush)
            )
        }
    }
}