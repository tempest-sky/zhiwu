package com.zhiwu.app.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.zhiwu.app.ui.animation.AnimationTokens
import com.zhiwu.app.ui.screens.*
import com.zhiwu.app.viewmodel.ItemViewModel

/**
 * 导航路由定义
 */
object Routes {
    const val ITEM_LIST = "item_list"
    const val ADD_ITEM = "add_item"
    const val EDIT_ITEM = "edit_item/{itemId}"
    const val STATISTICS = "statistics"
    const val MANAGE_CATEGORIES = "manage_categories"
    const val MANAGE_TAGS = "manage_tags"
    
    fun editItem(itemId: Long) = "edit_item/$itemId"
}

/**
 * 应用导航图
 */
@Composable
fun ZhiWuNavGraph(
    navController: NavHostController,
    viewModel: ItemViewModel = viewModel()
) {
    NavHost(
        navController = navController,
        startDestination = Routes.ITEM_LIST,
        enterTransition = {
            fadeIn(
                animationSpec = tween(
                    durationMillis = AnimationTokens.DURATION_MEDIUM2,
                    easing = AnimationTokens.EASING_EMPHASIZED_DECELERATE
                )
            ) + slideInHorizontally(
                initialOffsetX = { it / 4 },
                animationSpec = tween(
                    durationMillis = AnimationTokens.DURATION_MEDIUM2,
                    easing = AnimationTokens.EASING_EMPHASIZED_DECELERATE
                )
            )
        },
        exitTransition = {
            fadeOut(
                animationSpec = tween(
                    durationMillis = AnimationTokens.DURATION_MEDIUM1,
                    easing = AnimationTokens.EASING_EMPHASIZED_ACCELERATE
                )
            )
        },
        popEnterTransition = {
            fadeIn(
                animationSpec = tween(
                    durationMillis = AnimationTokens.DURATION_MEDIUM2,
                    easing = AnimationTokens.EASING_EMPHASIZED_DECELERATE
                )
            ) + slideInHorizontally(
                initialOffsetX = { -it / 4 },
                animationSpec = tween(
                    durationMillis = AnimationTokens.DURATION_MEDIUM2,
                    easing = AnimationTokens.EASING_EMPHASIZED_DECELERATE
                )
            )
        },
        popExitTransition = {
            fadeOut(
                animationSpec = tween(
                    durationMillis = AnimationTokens.DURATION_MEDIUM1,
                    easing = AnimationTokens.EASING_EMPHASIZED_ACCELERATE
                )
            ) + slideOutHorizontally(
                targetOffsetX = { it / 4 },
                animationSpec = tween(
                    durationMillis = AnimationTokens.DURATION_MEDIUM2,
                    easing = AnimationTokens.EASING_EMPHASIZED_ACCELERATE
                )
            )
        }
    ) {
        // 物品列表主页
        composable(Routes.ITEM_LIST) {
            ItemListScreen(
                viewModel = viewModel,
                onNavigateToAddItem = {
                    navController.navigate(Routes.ADD_ITEM)
                },
                onNavigateToEditItem = { itemId ->
                    navController.navigate(Routes.editItem(itemId))
                },
                onNavigateToStatistics = {
                    navController.navigate(Routes.STATISTICS)
                },
                onNavigateToManageCategories = {
                    navController.navigate(Routes.MANAGE_CATEGORIES)
                },
                onNavigateToManageTags = {
                    navController.navigate(Routes.MANAGE_TAGS)
                }
            )
        }
        
        // 添加物品
        composable(Routes.ADD_ITEM) {
            AddEditItemScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        // 编辑物品
        composable(
            route = Routes.EDIT_ITEM,
            arguments = listOf(
                navArgument("itemId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val itemId = backStackEntry.arguments?.getLong("itemId") ?: 0L
            AddEditItemScreen(
                viewModel = viewModel,
                itemId = itemId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        // 统计页面
        composable(Routes.STATISTICS) {
            StatisticsScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        // 管理分类
        composable(Routes.MANAGE_CATEGORIES) {
            ManageCategoriesScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        // 管理标签
        composable(Routes.MANAGE_TAGS) {
            ManageTagsScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}