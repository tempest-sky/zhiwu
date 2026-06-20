package com.zhiwu.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.zhiwu.app.ui.animation.AnimationTokens
import com.zhiwu.app.ui.theme.*

/**
 * 磨砂玻璃按钮
 * 注意事项：
 * - 使用remember缓存shape和brush避免重复创建
 * - 使用interactionSource追踪交互状态
 * - 动画使用硬件加速的graphicsLayer
 */
@Composable
fun GlassButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null,
    text: String? = null,
    cornerRadius: Dp = 16.dp
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    // 使用animateFloatAsState进行硬件加速动画
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = tween(AnimationTokens.DURATION_SHORT3),
        label = "buttonScale"
    )
    
    val alpha by animateFloatAsState(
        targetValue = if (isPressed) 0.8f else 1f,
        animationSpec = tween(AnimationTokens.DURATION_SHORT3),
        label = "buttonAlpha"
    )
    
    val isDark = isSystemInDarkTheme()
    
    // 缓存形状和画刷，避免每帧重新创建
    val shape = remember(cornerRadius) { RoundedCornerShape(cornerRadius) }
    
    val backgroundColor = remember(isDark) {
        if (isDark) GlassBackgroundDark else GlassBackgroundLight
    }
    
    val borderBrush = remember(isDark) {
        Brush.verticalGradient(
            colors = listOf(
                Color.White.copy(alpha = if (isDark) 0.15f else 0.3f),
                Color.White.copy(alpha = if (isDark) 0.05f else 0.1f)
            )
        )
    }
    
    val backgroundBrush = remember(isDark, backgroundColor) {
        Brush.verticalGradient(
            colors = listOf(
                backgroundColor,
                backgroundColor.copy(alpha = 0.7f)
            )
        )
    }
    
    Box(
        modifier = modifier
            .scale(scale)
            .shadow(
                elevation = 4.dp,
                shape = shape,
                ambientColor = Color.Black.copy(alpha = 0.08f),
                spotColor = Color.Black.copy(alpha = 0.12f)
            )
            .clip(shape)
            .background(
                brush = backgroundBrush,
                alpha = if (enabled) alpha else 0.5f
            )
            .border(
                width = 1.dp,
                brush = borderBrush,
                shape = shape
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick
            )
            .padding(horizontal = 16.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.onSurface.copy(
                        alpha = if (enabled) 1f else 0.5f
                    )
                )
            }
            if (text != null) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(
                        alpha = if (enabled) 1f else 0.5f
                    )
                )
            }
        }
    }
}

/**
 * 磨砂玻璃图标按钮
 */
@Composable
fun GlassIconButton(
    onClick: () -> Unit,
    icon: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    tint: Color = MaterialTheme.colorScheme.onSurface
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        animationSpec = tween(AnimationTokens.DURATION_SHORT3),
        label = "iconButtonScale"
    )
    
    val isDark = isSystemInDarkTheme()
    val shape = remember { RoundedCornerShape(12.dp) }
    
    val backgroundColor = remember(isDark) {
        if (isDark) GlassBackgroundDark else GlassBackgroundLight
    }
    
    Box(
        modifier = modifier
            .size(40.dp)
            .scale(scale)
            .shadow(
                elevation = 2.dp,
                shape = shape,
                ambientColor = Color.Black.copy(alpha = 0.06f),
                spotColor = Color.Black.copy(alpha = 0.1f)
            )
            .clip(shape)
            .background(
                color = backgroundColor.copy(alpha = if (isPressed) 0.8f else 1f)
            )
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = if (isDark) 0.1f else 0.2f),
                shape = shape
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(20.dp),
            tint = tint.copy(alpha = if (enabled) 1f else 0.5f)
        )
    }
}

/**
 * 磨砂玻璃浮动操作按钮
 */
@Composable
fun GlassFloatingActionButton(
    onClick: () -> Unit,
    icon: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = tween(AnimationTokens.DURATION_SHORT3),
        label = "fabScale"
    )
    
    val isDark = isSystemInDarkTheme()
    val shape = remember { RoundedCornerShape(16.dp) }
    
    val primaryColor = MaterialTheme.colorScheme.primary
    
    Box(
        modifier = modifier
            .size(56.dp)
            .scale(scale)
            .shadow(
                elevation = 8.dp,
                shape = shape,
                ambientColor = primaryColor.copy(alpha = 0.3f),
                spotColor = primaryColor.copy(alpha = 0.4f)
            )
            .clip(shape)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        primaryColor,
                        primaryColor.copy(alpha = 0.85f)
                    )
                )
            )
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.2f),
                shape = shape
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.onPrimary
        )
    }
}
