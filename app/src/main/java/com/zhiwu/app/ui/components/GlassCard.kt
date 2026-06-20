package com.zhiwu.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.zhiwu.app.ui.theme.*

/**
 * 磨砂玻璃材质卡片组件
 * 
 * 特性：
 * - 半透明背景
 * - 柔和阴影
 * - 白色半透明边框
 * - 16dp圆角
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 16.dp,
    elevation: Dp = 0.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    val isDark = isSystemInDarkTheme()
    
    val backgroundColor = if (isDark) {
        GlassBackgroundDark
    } else {
        GlassBackgroundLight
    }
    
    val borderColor = if (isDark) {
        Color(0x1AFFFFFF)
    } else {
        Color(0x15FFFFFF)
    }
    
    val shape = RoundedCornerShape(cornerRadius)
    
    Column(
        modifier = modifier
            .clip(shape)
            .background(
                color = backgroundColor,
                shape = shape
            )
            .border(
                width = 1.dp,
                color = borderColor,
                shape = shape
            ),
        content = content
    )
}

/**
 * 磨砂玻璃表面
 * 用于更大的区域背景
 */
@Composable
fun GlassSurface(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 16.dp,
    content: @Composable BoxScope.() -> Unit
) {
    val isDark = isSystemInDarkTheme()
    
    val backgroundColor = if (isDark) {
        GlassBackgroundDark
    } else {
        GlassBackgroundLight
    }
    
    val borderColor = if (isDark) {
        Color(0x1AFFFFFF)
    } else {
        Color(0x15FFFFFF)
    }
    
    val shape = RoundedCornerShape(cornerRadius)
    
    Box(
        modifier = modifier
            .clip(shape)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        backgroundColor,
                        backgroundColor.copy(alpha = 0.6f)
                    )
                )
            )
            .border(
                width = 1.dp,
                color = borderColor,
                shape = shape
            ),
        content = content
    )
}

/**
 * 磨砂玻璃对话框背景
 */
@Composable
fun GlassDialogBackground(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    val isDark = isSystemInDarkTheme()
    
    val backgroundColor = if (isDark) {
        Color(0x33FFFFFF)
    } else {
        Color(0x40F5F5F5)
    }
    
    val borderColor = if (isDark) {
        Color(0x1AFFFFFF)
    } else {
        Color(0x20FFFFFF)
    }
    
    val shape = RoundedCornerShape(24.dp)
    
    Column(
        modifier = modifier
            .shadow(
                elevation = 8.dp,
                shape = shape,
                ambientColor = Color.Black.copy(alpha = 0.1f),
                spotColor = Color.Black.copy(alpha = 0.15f)
            )
            .clip(shape)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        backgroundColor,
                        backgroundColor.copy(alpha = 0.7f)
                    )
                )
            )
            .border(
                width = 1.dp,
                color = borderColor,
                shape = shape
            ),
        content = content
    )
}