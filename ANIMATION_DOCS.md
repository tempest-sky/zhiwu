# 值物应用 - 动画实现说明文档

## 概述

本应用遵循 Material Design Motion 规范，参考 GSAP 动画设计理念，实现了流畅、自然的动画效果。

## 动画时长规范 (Motion Tokens)

### Material Design 标准时长

| Token | 时长 | 用途 |
|-------|------|------|
| `DURATION_SHORT2` | 100ms | 涟漪反馈、开关切换 |
| `DURATION_SHORT3` | 150ms | 微交互（按钮点击、缩放） |
| `DURATION_MEDIUM1` | 250ms | 次要过渡（视图切换） |
| `DURATION_MEDIUM2` | 300ms | 主要过渡（页面导航） |
| `DURATION_LONG1` | 500ms | 大型过渡（全屏动画） |

### 使用位置

```
页面导航过渡: 300ms (DURATION_MEDIUM2)
视图模式切换: 250ms (DURATION_MEDIUM1)
列表项点击: 150ms (DURATION_SHORT3)
按钮涟漪: 100ms (DURATION_SHORT2)
```

## 缓动曲线 (Easing Curves)

### 标准缓动曲线

```kotlin
// 标准缓动 - 通用过渡
EASING_STANDARD = CubicBezierEasing(0.2f, 0.0f, 0.0f, 1.0f)

// 强调缓动 - 突出元素
EASING_EMPHASIZED = CubicBezierEasing(0.2f, 0.0f, 0.0f, 1.0f)

// 强调减速 - 进入动画（元素到达目的地）
EASING_EMPHASIZED_DECELERATE = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1.0f)

// 强调加速 - 退出动画（元素离开）
EASING_EMPHASIZED_ACCELERATE = CubicBezierEasing(0.3f, 0.0f, 0.8f, 0.15f)
```

### 使用场景

| 场景 | 缓动曲线 | 说明 |
|------|----------|------|
| 页面进入 | EMPHASIZED_DECELERATE | 元素到达目标时减速 |
| 页面退出 | EMPHASIZED_ACCELERATE | 元素离开时加速 |
| 列表动画 | STANDARD | 匀变速运动 |
| 微交互 | STANDARD | 自然反馈感 |

## 弹簧动画 (Spring Animations)

### 空间运动弹簧 (defaultSpatial)

```kotlin
SPRING_SPATIAL = spring<Float>(
    dampingRatio = Spring.DampingRatioMediumBouncy,  // 0.5
    stiffness = Spring.StiffnessMedium               // 1500
)
```

**用途**：元素在空间中的位移、缩放运动
- FAB按钮出现
- 卡片展开/折叠
- 列表项位移

### 效果变化弹簧 (defaultEffects)

```kotlin
SPRING_EFFECTS = spring<Float>(
    dampingRatio = Spring.DampingRatioNoBouncy,  // 1.0
    stiffness = Spring.StiffnessMedium            // 1500
)
```

**用途**：透明度、颜色等效果变化
- 淡入淡出
- 颜色过渡
- 标签选中状态

### 轻弹弹簧

```kotlin
SPRING_LIGHT = spring<Float>(
    dampingRatio = Spring.DampingRatioLowBouncy,  // 0.75
    stiffness = Spring.StiffnessHigh              // 10000
)
```

**用途**：快速反馈动画
- 按钮点击缩放
- 触摸反馈

## 页面过渡动画

### Navigation Compose 过渡配置

```kotlin
// 进入动画：从右侧滑入 + 淡入
enterTransition = {
    fadeIn(tween(300ms, EMPHASIZED_DECELERATE)) +
    slideInHorizontally(tween(300ms, EMPHASIZED_DECELERATE)) { it / 4 }
}

// 退出动画：淡出
exitTransition = {
    fadeOut(tween(250ms, EMPHASIZED_ACCELERATE))
}

// 返回进入：从左侧滑入 + 淡入
popEnterTransition = {
    fadeIn(tween(300ms, EMPHASIZED_DECELERATE)) +
    slideInHorizontally(tween(300ms, EMPHASIZED_DECELERATE)) { -it / 4 }
}

// 返回退出：向右滑出 + 淡出
popExitTransition = {
    fadeOut(tween(250ms, EMPHASIZED_ACCELERATE)) +
    slideOutHorizontally(tween(300ms, EMPHASIZED_ACCELERATE)) { it / 4 }
}
```

### Material Container Transform 模式

适用于：
- 列表项 → 详情页
- 卡片 → 全屏视图

实现方式：共享元素过渡 + 尺寸动画

### Shared Axis 模式

适用于：
- 同层级页面切换
- Tab切换

### Fade Through 模式

适用于：
- 不同层级页面切换
- 内容替换

## 微交互动画

### 列表项点击反馈

```kotlin
fun Modifier.clickScale(interactionSource: MutableInteractionSource): Modifier {
    val isPressed by interactionSource.collectIsPressedAsState()
    
    // 缩放：1.0 → 0.96（按下时缩小）
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = SPRING_SPATIAL
    )
    
    // 透明度：1.0 → 0.8（按下时变暗）
    val alpha by animateFloatAsState(
        targetValue = if (isPressed) 0.8f else 1f,
        animationSpec = tween(DURATION_SHORT3)
    )
    
    return this.scale(scale).graphicsLayer { this.alpha = alpha }
}
```

**效果**：
- 按下时：轻微缩小(96%) + 变暗(80%)
- 释放时：弹性恢复到正常状态
- 使用弹簧动画，有轻微弹跳感

### 视图模式切换

```kotlin
AnimatedContent(
    targetState = isGridView,
    transitionSpec = {
        fadeIn(tween(DURATION_MEDIUM1)) togetherWith 
        fadeOut(tween(DURATION_MEDIUM1))
    }
) { isGrid ->
    if (isGrid) ItemGridView(...) else ItemListView(...)
}
```

**效果**：
- 列表 → 网格：淡出旧视图，淡入新视图
- 250ms平滑过渡

### 搜索栏清除按钮

```kotlin
AnimatedVisibility(
    visible = query.isNotEmpty(),
    enter = fadeIn() + scaleIn(initialScale = 0.8f, SPRING_EFFECTS),
    exit = fadeOut() + scaleOut(targetScale = 0.8f, SPRING_EFFECTS)
) {
    IconButton(onClick = { onQueryChange("") }) {
        Icon(Icons.Default.Clear, ...)
    }
}
```

**效果**：
- 有搜索内容时：从80%缩放淡入
- 清空时：缩小到80%淡出

### 骨架屏动画

```kotlin
// Shimmer效果
val shimmerBrush = Brush.linearGradient(
    colors = listOf(baseColor, highlightColor, baseColor),
    start = Offset(shimmerOffset - 300f, 0f),
    end = Offset(shimmerOffset, 0f)
)
```

**效果**：
- 光泽从左到右扫描
- 1200ms循环周期
- 线性缓动，匀速运动

### 空状态浮动动画

```kotlin
val offsetY by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = -8f,
    animationSpec = infiniteRepeatable(
        animation = tween(2000ms, EASING_STANDARD),
        repeatMode = RepeatMode.Reverse
    )
)
```

**效果**：
- 图标上下缓慢浮动
- 8dp幅度，2秒周期
- 标准缓动，自然呼吸感

## 性能优化

### 1. 硬件加速

```kotlin
// 使用graphicsLayer进行硬件加速的transform
.graphicsLayer {
    this.scaleX = scale
    this.scaleY = scale
    this.alpha = alpha
}
```

### 2. 动画标签

```kotlin
// 为所有animate*AsState添加label，便于调试
val scale by animateFloatAsState(
    targetValue = ...,
    label = "clickScale"  // 调试标签
)
```

### 3. 避免昂贵计算

```kotlin
// ✅ 正确：使用graphicsLayer
Modifier.graphicsLayer { alpha = animatedAlpha }

// ❌ 错误：在动画中使用background
Modifier.background(Color.copy(alpha = animatedAlpha))
```

### 4. 动画取消

```kotlin
// 使用LaunchedEffect确保动画在组件销毁时取消
LaunchedEffect(key) {
    animate(...)  // 当key变化或组件销毁时自动取消
}
```

### 5. 无障碍支持

```kotlin
// 遵循系统动画时长缩放设置
// Android会自动处理全局动画时长缩放
// 用户可以在系统设置中调整动画时长
```

## 动画实现架构

```
AnimationTokens.kt
├── 时长常量 (DURATION_*)
├── 缓动曲线 (EASING_*)
├── 弹簧参数 (SPRING_*)
├── 辅助函数
│   ├── enterTweenSpec()
│   ├── exitTweenSpec()
│   ├── clickScale()
│   ├── fadeInSlideIn()
│   └── fadeOutSlideOut()
└── 扩展函数
    └── Modifier.clickScale()
```

## 参考资料

- [Material Design Motion](https://m3.material.io/styles/motion/overview)
- [Material Motion Token](https://m3.material.io/styles/motion/easing-and-duration/tokens-specs)
- [Android Animation Guide](https://developer.android.com/guide/topics/graphics/prop-animation)
- [Jetpack Compose Animation](https://developer.android.com/jetpack/compose/animation)
- [GSAP Animation Principles](https://gsap.com/docs/v3/)

## 调试技巧

### 开启GPU渲染分析
```
开发者选项 → GPU呈现模式 → 在屏幕上显示为条形图
```

### 查看动画帧率
```kotlin
// 在应用中添加帧率监控
Choreographer.getInstance().postFrameCallback { frameTimeNanos ->
    // 计算帧率
}
```

### 常见问题

1. **动画卡顿**
   - 检查是否在动画中进行了昂贵的计算
   - 使用`graphicsLayer`而非`background`进行alpha/transform动画
   - 检查是否有重组过于频繁

2. **动画不生效**
   - 确保动画值确实在变化
   - 检查`animationSpec`是否正确
   - 确认`targetValue`是否正确设置

3. **弹簧动画过于弹跳**
   - 调整`dampingRatio`（增大减少弹跳）
   - 调整`stiffness`（增大加快动画）