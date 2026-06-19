package com.zhiwu.app.ui.animation

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer

/**
 * Material Motion 动画Token
 * 参考GSAP动画规范和Material Design Motion系统
 */
object AnimationTokens {
    // ==================== 动画时长 ====================
    /** 主要过渡：300ms */
    const val DURATION_MEDIUM2 = 300
    
    /** 次要过渡：250ms */
    const val DURATION_MEDIUM1 = 250
    
    /** 微交互：150ms */
    const val DURATION_SHORT3 = 150
    
    /** 额外短动画：100ms */
    const val DURATION_SHORT2 = 100
    
    /** 长过渡：500ms */
    const val DURATION_LONG1 = 500
    
    // ==================== 缓动曲线 ====================
    /** 标准缓动 */
    val EASING_STANDARD = CubicBezierEasing(0.2f, 0.0f, 0.0f, 1.0f)
    
    /** 强调缓动 */
    val EASING_EMPHASIZED = CubicBezierEasing(0.2f, 0.0f, 0.0f, 1.0f)
    
    /** 强调减速 */
    val EASING_EMPHASIZED_DECELERATE = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1.0f)
    
    /** 强调加速 */
    val EASING_EMPHASIZED_ACCELERATE = CubicBezierEasing(0.3f, 0.0f, 0.8f, 0.15f)
    
    /** 减速缓动 */
    val EASING_DECELERATE = CubicBezierEasing(0.0f, 0.0f, 0.0f, 1.0f)
    
    /** 加速缓动 */
    val EASING_ACCELERATE = CubicBezierEasing(0.3f, 0.0f, 1.0f, 1.0f)
    
    // ==================== 弹簧动画参数 ====================
    /** 空间运动弹簧 */
    val SPRING_SPATIAL = spring<Float>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessMedium
    )
    
    /** 效果变化弹簧 */
    val SPRING_EFFECTS = spring<Float>(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessMedium
    )
    
    /** 轻弹弹簧 */
    val SPRING_LIGHT = spring<Float>(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness = Spring.StiffnessHigh
    )
}

/**
 * 页面进入动画规格
 */
@OptIn(ExperimentalAnimationApi::class)
fun <T> enterTweenSpec(duration: Int = AnimationTokens.DURATION_MEDIUM2) = tween<T>(
    durationMillis = duration,
    easing = AnimationTokens.EASING_EMPHASIZED_DECELERATE
)

/**
 * 页面退出动画规格
 */
@OptIn(ExperimentalAnimationApi::class)
fun <T> exitTweenSpec(duration: Int = AnimationTokens.DURATION_MEDIUM2) = tween<T>(
    durationMillis = duration,
    easing = AnimationTokens.EASING_EMPHASIZED_ACCELERATE
)

/**
 * 列表项点击缩放修饰符
 */
@Composable
fun Modifier.clickScale(interactionSource: MutableInteractionSource): Modifier {
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "clickScale"
    )
    val alpha by animateFloatAsState(
        targetValue = if (isPressed) 0.8f else 1f,
        animationSpec = tween(
            durationMillis = AnimationTokens.DURATION_SHORT3,
            easing = AnimationTokens.EASING_STANDARD
        ),
        label = "clickAlpha"
    )
    return this
        .scale(scale)
        .graphicsLayer { this.alpha = alpha }
}

/**
 * 淡入滑入动画
 */
@Composable
fun fadeInSlideIn(
    initialOffsetY: Int = 30,
    duration: Int = AnimationTokens.DURATION_MEDIUM2
): EnterTransition {
    return fadeIn(
        animationSpec = tween(
            durationMillis = duration,
            easing = AnimationTokens.EASING_EMPHASIZED_DECELERATE
        )
    ) + slideInVertically(
        animationSpec = tween(
            durationMillis = duration,
            easing = AnimationTokens.EASING_EMPHASIZED_DECELERATE
        ),
        initialOffsetY = { initialOffsetY }
    )
}

/**
 * 淡出滑出动画
 */
@Composable
fun fadeOutSlideOut(
    targetOffsetY: Int = -30,
    duration: Int = AnimationTokens.DURATION_MEDIUM2
): ExitTransition {
    return fadeOut(
        animationSpec = tween(
            durationMillis = duration,
            easing = AnimationTokens.EASING_EMPHASIZED_ACCELERATE
        )
    ) + slideOutVertically(
        animationSpec = tween(
            durationMillis = duration,
            easing = AnimationTokens.EASING_EMPHASIZED_ACCELERATE
        ),
        targetOffsetY = { targetOffsetY }
    )
}