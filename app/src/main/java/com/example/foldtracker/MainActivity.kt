package com.example.foldtracker

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.window.layout.FoldingFeature
import androidx.window.layout.WindowInfoTracker
import com.example.foldtracker.service.FoldTrackerService
import com.example.foldtracker.ui.navigation.AppNavigation
import com.example.foldtracker.viewmodel.CounterViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startFoldTrackingService()
        setContent {
            val viewModel: CounterViewModel = hiltViewModel()

            val context = LocalContext.current
            viewModel.initializeData(context)
            AppNavigation(viewModel = viewModel)
        }
    }



    @RequiresApi(Build.VERSION_CODES.O)
    private fun startFoldTrackingService() {
        val serviceIntent = Intent(this, FoldTrackerService::class.java)
        ContextCompat.startForegroundService(this, serviceIntent)
    }
}