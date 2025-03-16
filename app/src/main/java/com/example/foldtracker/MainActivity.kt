package com.example.foldtracker

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.foldtracker.service.FoldTrackerService
import com.example.foldtracker.core.navigation.AppNavigation
import com.example.foldtracker.feature.counter.CounterViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

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

    private fun startFoldTrackingService() {
        val serviceIntent = Intent(this, FoldTrackerService::class.java)
        ContextCompat.startForegroundService(this, serviceIntent)
    }
}