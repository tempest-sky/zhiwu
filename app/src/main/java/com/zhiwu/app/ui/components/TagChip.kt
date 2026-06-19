package com.zhiwu.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.zhiwu.app.ui.animation.AnimationTokens
import com.zhiwu.app.ui.theme.TagColorDark
import com.zhiwu.app.ui.theme.TagColorLight

/**
 * 标签芯片组件
 */
@Composable
fun TagChip(
    name: String,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    onClick: (() -> Unit)? = null,
    onRemove: (() -> Unit)? = null
) {
    val isDark = isSystemInDarkTheme()
    
    val backgroundColor by animateColorAsState(
        targetValue = when {
            selected && isDark -> MaterialTheme.colorScheme.primaryContainer
            selected -> MaterialTheme.colorScheme.primaryContainer
            isDark -> TagColorDark
            else -> TagColorLight
        },
        animationSpec = AnimationTokens.SPRING_EFFECTS,
        label = "tagBg"
    )
    
    val textColor by animateColorAsState(
        targetValue = when {
            selected -> MaterialTheme.colorScheme.onPrimaryContainer
            isDark -> MaterialTheme.colorScheme.onSurface
            else -> MaterialTheme.colorScheme.primary
        },
        animationSpec = AnimationTokens.SPRING_EFFECTS,
        label = "tagText"
    )
    
    val borderColor = if (selected) {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
    } else {
        androidx.compose.ui.graphics.Color.Transparent
    }
    
    val shape = RoundedCornerShape(12.dp)
    
    Row(
        modifier = modifier
            .clip(shape)
            .background(backgroundColor)
            .border(1.dp, borderColor, shape)
            .then(
                if (onClick != null) Modifier.clickable { onClick() } else Modifier
            )
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.labelMedium,
            color = textColor
        )
        
        if (onRemove != null) {
            Spacer(modifier = Modifier.width(2.dp))
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "移除",
                tint = textColor.copy(alpha = 0.7f),
                modifier = Modifier
                    .size(14.dp)
                    .clickable { onRemove() }
            )
        }
    }
}

/**
 * 分类芯片组件
 */
@Composable
fun CategoryChip(
    name: String,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    onClick: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    
    val backgroundColor by animateColorAsState(
        targetValue = if (selected) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            if (isDark) TagColorDark else TagColorLight
        },
        animationSpec = AnimationTokens.SPRING_EFFECTS,
        label = "categoryBg"
    )
    
    val textColor by animateColorAsState(
        targetValue = if (selected) {
            MaterialTheme.colorScheme.onPrimaryContainer
        } else {
            MaterialTheme.colorScheme.onSurface
        },
        animationSpec = AnimationTokens.SPRING_EFFECTS,
        label = "categoryText"
    )
    
    val shape = RoundedCornerShape(20.dp)
    
    Surface(
        modifier = modifier,
        shape = shape,
        color = backgroundColor,
        border = if (selected) {
            BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
        } else null,
        onClick = onClick
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.labelLarge,
            color = textColor,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}