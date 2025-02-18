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
            TrackFoldingEvents(viewModel = viewModel)
        }
    }

    @Composable
    @RequiresApi(Build.VERSION_CODES.O)
    private fun TrackFoldingEvents(viewModel: CounterViewModel) {
        val context = LocalContext.current
        val windowInfoTracker = WindowInfoTracker.getOrCreate(LocalContext.current)
        val activity = LocalContext.current as Activity

        LaunchedEffect(activity) {
            val layoutInfoFlow = windowInfoTracker.windowLayoutInfo(activity)

            layoutInfoFlow.collect { layoutInfo ->
                // Log all display features for debugging
                layoutInfo.displayFeatures.forEach { feature ->
                    Log.d("FoldTracker", "Feature: $feature")
                    if (feature is FoldingFeature) {
                        Log.d(
                            "FoldTracker",
                            "State: ${feature.state}, Orientation: ${feature.orientation}, Bounds: ${feature.bounds}"
                        )
                    }
                }

                // Handle "folded" detection
                val isFolded = layoutInfo.displayFeatures.any { feature ->
                    feature is FoldingFeature && (
                            feature.state == FoldingFeature.State.HALF_OPENED ||  // Common folded state
                                    (Build.MANUFACTURER.equals("Google", ignoreCase = true) &&
                                            feature.state == FoldingFeature.State.FLAT) // Pixel Fold adjustment
                            )
                }

                // Log and update counter
                if (isFolded) {
                    Log.d("FoldTracker", "Device is folded!")
                    viewModel.incrementCounter(context)
                } else {
                    Log.d("FoldTracker", "Device is NOT folded.")
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startFoldTrackingService() {
        val serviceIntent = Intent(this, FoldTrackerService::class.java)
        ContextCompat.startForegroundService(this, serviceIntent)
    }
}