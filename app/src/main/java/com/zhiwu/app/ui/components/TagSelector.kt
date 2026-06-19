package com.zhiwu.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zhiwu.app.data.entity.Tag

/**
 * 标签选择器
 * 支持多选标签
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TagSelector(
    tags: List<Tag>,
    selectedTagIds: Set<Long>,
    onTagToggle: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "标签",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            tags.forEach { tag ->
                val isSelected = tag.id in selectedTagIds
                FilterChip(
                    selected = isSelected,
                    onClick = { onTagToggle(tag.id) },
                    label = {
                        Text(
                            text = tag.name,
                            style = MaterialTheme.typography.labelMedium
                        )
                    },
                    leadingIcon = if (isSelected) {
                        {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    } else null
                )
            }
        }
    }
}