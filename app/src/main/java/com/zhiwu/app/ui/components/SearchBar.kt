package com.zhiwu.app.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import com.zhiwu.app.ui.animation.AnimationTokens
import com.zhiwu.app.ui.theme.GlassBackgroundDark
import com.zhiwu.app.ui.theme.GlassBackgroundLight

/**
 * 磨砂玻璃风格搜索栏
 */
@Composable
fun GlassSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "搜索物品..."
) {
    val isDark = isSystemInDarkTheme()
    val shape = RoundedCornerShape(16.dp)
    
    val backgroundColor = if (isDark) GlassBackgroundDark else GlassBackgroundLight
    val borderColor = if (isDark) {
        androidx.compose.ui.graphics.Color(0x1AFFFFFF)
    } else {
        androidx.compose.ui.graphics.Color(0x15FFFFFF)
    }
    
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(backgroundColor)
            .border(1.dp, borderColor, shape)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "搜索",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Box(modifier = Modifier.weight(1f)) {
            if (query.isEmpty()) {
                Text(
                    text = placeholder,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }
            
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface
                ),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }
        
        AnimatedVisibility(
            visible = query.isNotEmpty(),
            enter = fadeIn() + scaleIn(
                initialScale = 0.8f,
                animationSpec = AnimationTokens.SPRING_EFFECTS
            ),
            exit = fadeOut() + scaleOut(
                targetScale = 0.8f,
                animationSpec = AnimationTokens.SPRING_EFFECTS
            )
        ) {
            IconButton(
                onClick = { onQueryChange("") },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = "清除",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}