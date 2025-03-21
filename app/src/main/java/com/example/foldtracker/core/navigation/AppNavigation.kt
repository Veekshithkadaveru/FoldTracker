package com.example.foldtracker.core.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.foldtracker.feature.counter.CounterViewModel
import com.example.foldtracker.feature.counter.ui.CounterScreen
import com.example.foldtracker.feature.stats.ui.StatsScreen

@Composable
fun AppNavigation(viewModel: CounterViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "counter_screen") {
        composable(
            route = "counter_screen",
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(300, easing = EaseInOut)
                ) + fadeIn(animationSpec = tween(300))
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(300, easing = EaseInOut)
                ) + fadeOut(animationSpec = tween(300))
            }
        ) {
            CounterScreen(viewModel = viewModel, navController)
        }

        composable(
            route = "stats_screen",
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(300, easing = EaseInOut)
                ) + fadeIn(animationSpec = tween(300))
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(300, easing = EaseInOut)
                ) + fadeOut(animationSpec = tween(300))
            }
        ) {
            StatsScreen(viewModel = viewModel, navController = navController)
        }
    }
}