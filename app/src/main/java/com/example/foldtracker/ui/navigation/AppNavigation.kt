package com.example.foldtracker.ui.navigation

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.foldtracker.ui.CounterScreen
import com.example.foldtracker.viewmodel.CounterViewModel

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun AppNavigation(viewModel: CounterViewModel){
    val navController=rememberNavController()
    
    NavHost(navController = navController, startDestination = Screen.Counter.route) {
        composable(Screen.Counter.route){
            CounterScreen(counter = viewModel.counter.value)
        }
        
    }
}