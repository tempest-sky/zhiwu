package com.zhiwu.app.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.zhiwu.app.ui.animation.AnimationTokens

/**
 * 空状态组件
 * 当列表为空时显示
 */
@Composable
fun EmptyState(
    modifier: Modifier = Modifier,
    title: String = "暂无物品",
    subtitle: String = "点击右下角的 + 按钮添加第一个物品",
    icon: @Composable () -> Unit = {
        Icon(
            imageVector = Icons.Outlined.Inventory2,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
        )
    }
) {
    // 浮动动画
    val infiniteTransition = rememberInfiniteTransition(label = "emptyState")
    val offsetY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -8f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 2000,
                easing = AnimationTokens.EASING_STANDARD
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floatY"
    )
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier.offset(y = offsetY.dp)
        ) {
            icon()
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}