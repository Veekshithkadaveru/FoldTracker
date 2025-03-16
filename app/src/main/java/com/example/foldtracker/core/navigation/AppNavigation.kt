package com.example.foldtracker.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.foldtracker.feature.counter.ui.CounterScreen
import com.example.foldtracker.feature.stats.ui.StatsScreen
import com.example.foldtracker.feature.counter.CounterViewModel

@Composable
fun AppNavigation(viewModel: CounterViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "counter_screen") {
        composable("counter_screen") {
            CounterScreen(viewModel = viewModel, navController)
        }

        composable("stats_screen") {
            StatsScreen(viewModel = viewModel, navController = navController)
        }
    }
}