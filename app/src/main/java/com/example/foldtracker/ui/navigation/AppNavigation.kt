package com.example.foldtracker.ui.navigation

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.foldtracker.ui.CounterScreen
import com.example.foldtracker.ui.StatsScreen
import com.example.foldtracker.viewmodel.CounterViewModel

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun AppNavigation(viewModel: CounterViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "counter_screen") {
        composable("counter_screen") {
            CounterScreen(viewModel = viewModel,navController)
        }

        composable("stats_screen"){
            StatsScreen(viewModel = viewModel)
        }
    }
}