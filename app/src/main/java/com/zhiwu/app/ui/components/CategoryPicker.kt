package com.zhiwu.app.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zhiwu.app.data.entity.Category

/**
 * 分类选择器
 */
@Composable
fun CategoryPicker(
    categories: List<Category>,
    selectedCategoryId: Long?,
    onCategorySelected: (Long?) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "分类",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                CategoryChip(
                    name = "全部",
                    selected = selectedCategoryId == null,
                    onClick = { onCategorySelected(null) }
                )
            }
            
            items(categories) { category ->
                CategoryChip(
                    name = category.name,
                    selected = selectedCategoryId == category.id,
                    onClick = { onCategorySelected(category.id) }
                )
            }
        }
    }
}