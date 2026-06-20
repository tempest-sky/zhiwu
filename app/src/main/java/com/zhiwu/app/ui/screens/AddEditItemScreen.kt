package com.zhiwu.app.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.zhiwu.app.data.entity.Category
import com.zhiwu.app.data.entity.Item
import com.zhiwu.app.data.entity.ItemWithDetails
import com.zhiwu.app.data.entity.Tag
import com.zhiwu.app.ui.components.GlassButton
import com.zhiwu.app.ui.components.GlassCard
import com.zhiwu.app.ui.components.TagSelector
import com.zhiwu.app.viewmodel.ItemViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * 添加/编辑物品页面
 */
@Suppress("DEPRECATION")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddEditItemScreen(
    viewModel: ItemViewModel,
    itemId: Long? = null,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val categories by viewModel.categories.collectAsState()
    val tags by viewModel.tags.collectAsState()
    
    // 表单状态
    var name by remember { mutableStateOf("") }
    var priceText by remember { mutableStateOf("") }
    var purchaseDate by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var selectedCategoryId by remember { mutableLongStateOf(0L) }
    var selectedTagIds by remember { mutableStateOf(setOf<Long>()) }
    var imagePath by remember { mutableStateOf<String?>(null) }
    var notes by remember { mutableStateOf("") }
    var existingItem by remember { mutableStateOf<Item?>(null) }
    
    // 表单验证
    var nameError by remember { mutableStateOf(false) }
    var priceError by remember { mutableStateOf(false) }
    var categoryError by remember { mutableStateOf(false) }
    
    var showDatePicker by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    
    val isEditing = itemId != null && itemId > 0
    val dateFormat = remember { SimpleDateFormat("yyyy年MM月dd日", Locale.getDefault()) }
    
    // 拍照相关
    var photoUri by remember { mutableStateOf<Uri?>(null) }
    
    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && photoUri != null) {
            scope.launch {
                imagePath = viewModel.saveImageToInternal(context, photoUri!!)
            }
        }
    }
    
    // 选择图片（带裁剪）
    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            scope.launch {
                imagePath = viewModel.saveImageToInternal(context, it)
            }
        }
    }
    
    // 加载已有物品数据
    LaunchedEffect(itemId) {
        if (isEditing) {
            viewModel.getItemWithDetails(itemId!!).collect { itemWithDetails ->
                itemWithDetails?.let {
                    existingItem = it.item
                    name = it.item.name
                    priceText = String.format("%.2f", it.item.price)
                    purchaseDate = it.item.purchaseDate
                    selectedCategoryId = it.item.categoryId
                    selectedTagIds = it.tags.map { tag -> tag.id }.toSet()
                    imagePath = it.item.imagePath
                    notes = it.item.notes ?: ""
                }
            }
        }
    }
    
    // 设置默认分类
    LaunchedEffect(categories) {
        if (!isEditing && selectedCategoryId == 0L && categories.isNotEmpty()) {
            selectedCategoryId = categories.first().id
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(if (isEditing) "编辑物品" else "添加物品")
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "返回")
                    }
                },
                actions = {
                    if (isEditing) {
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(
                                Icons.Default.Delete,
                                "删除",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
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
            // 图片区域
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (imagePath != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                        ) {
                            AsyncImage(
                                model = File(imagePath!!),
                                contentDescription = "物品图片",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            
                            // 删除图片按钮
                            IconButton(
                                onClick = {
                                    viewModel.deleteImageFile(imagePath)
                                    imagePath = null
                                },
                                modifier = Modifier.align(Alignment.TopEnd)
                            ) {
                                Icon(
                                    Icons.Default.Close,
                                    "删除图片",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    } else {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            GlassButton(
                                onClick = {
                                    val imageDir = File(context.filesDir, "images")
                                    if (!imageDir.exists()) imageDir.mkdirs()
                                    val file = File(imageDir, "temp_${System.currentTimeMillis()}.jpg")
                                    photoUri = FileProvider.getUriForFile(
                                        context,
                                        "${context.packageName}.fileprovider",
                                        file
                                    )
                                    takePictureLauncher.launch(photoUri!!)
                                },
                                icon = Icons.Default.CameraAlt,
                                text = "拍照"
                            )
                            
                            GlassButton(
                                onClick = { pickImageLauncher.launch("image/*") },
                                icon = Icons.Default.PhotoLibrary,
                                text = "从相册选择"
                            )
                        }
                    }
                }
            }
            
            // 基本信息
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "基本信息",
                        style = MaterialTheme.typography.titleMedium
                    )
                    
                    // 物品名称
                    OutlinedTextField(
                        value = name,
                        onValueChange = {
                            name = it
                            nameError = false
                        },
                        label = { Text("物品名称 *") },
                        isError = nameError,
                        supportingText = if (nameError) {
                            { Text("请输入物品名称") }
                        } else null,
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    // 价格
                    OutlinedTextField(
                        value = priceText,
                        onValueChange = {
                            priceText = it
                            priceError = false
                        },
                        label = { Text("购买价格 *") },
                        prefix = { Text("¥") },
                        isError = priceError,
                        supportingText = if (priceError) {
                            { Text("请输入有效的价格") }
                        } else null,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    // 购买日期
                    OutlinedTextField(
                        value = dateFormat.format(Date(purchaseDate)),
                        onValueChange = {},
                        label = { Text("购买时间") },
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { showDatePicker = true }) {
                                Icon(Icons.Default.CalendarMonth, "选择日期")
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    // 分类选择
                    Text(
                        text = "分类 *",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (categoryError) MaterialTheme.colorScheme.error
                            else MaterialTheme.colorScheme.onSurface
                    )
                    
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        categories.forEach { category ->
                            FilterChip(
                                selected = selectedCategoryId == category.id,
                                onClick = {
                                    selectedCategoryId = category.id
                                    categoryError = false
                                },
                                label = { Text(category.name) }
                            )
                        }
                    }
                    
                    if (categoryError) {
                        Text(
                            text = "请选择分类",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
            
            // 标签选择
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    TagSelector(
                        tags = tags,
                        selectedTagIds = selectedTagIds,
                        onTagToggle = { tagId ->
                            selectedTagIds = if (tagId in selectedTagIds) {
                                selectedTagIds - tagId
                            } else {
                                selectedTagIds + tagId
                            }
                        }
                    )
                }
            }
            
            // 备注
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("备注") },
                        minLines = 3,
                        maxLines = 5,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            
            // 保存按钮
            Button(
                onClick = {
                    // 验证表单
                    nameError = name.isBlank()
                    priceError = priceText.toDoubleOrNull() == null
                    categoryError = selectedCategoryId == 0L
                    
                    if (!nameError && !priceError && !categoryError) {
                        viewModel.saveItem(
                            name = name.trim(),
                            price = priceText.toDouble(),
                            purchaseDate = purchaseDate,
                            categoryId = selectedCategoryId,
                            tagIds = selectedTagIds.toList(),
                            imagePath = imagePath,
                            notes = notes.ifBlank { null },
                            existingItem = existingItem
                        )
                        onNavigateBack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = MaterialTheme.shapes.large,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(Icons.Default.Save, null, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text(
                    text = if (isEditing) "保存修改" else "添加物品",
                    style = MaterialTheme.typography.titleMedium
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
    
    // 日期选择器
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = purchaseDate
        )
        
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let {
                            purchaseDate = it
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("确定")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("取消")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
    
    // 删除确认对话框
    if (showDeleteDialog && existingItem != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("确认删除") },
            text = { Text("确定要删除「${existingItem!!.name}」吗？此操作不可撤销。") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteImageFile(imagePath)
                        viewModel.deleteItem(existingItem!!)
                        showDeleteDialog = false
                        onNavigateBack()
                    }
                ) {
                    Text("删除", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("取消")
                }
            }
        )
    }
}
