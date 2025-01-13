package com.example.foldtracker.ui.navigation

sealed class Screen(val route:String) {
    data object Counter : Screen("counter")
    data object Stats : Screen("stats")
    data object Achievements : Screen("achievements")
}