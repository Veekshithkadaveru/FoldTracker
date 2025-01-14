package com.example.foldtracker

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.lifecycleScope
import androidx.window.layout.FoldingFeature
import androidx.window.layout.WindowInfoTracker
import androidx.window.layout.WindowLayoutInfo
import com.example.foldtracker.ui.navigation.AppNavigation
import com.example.foldtracker.viewmodel.CounterViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import androidx.hilt.navigation.compose.hiltViewModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val viewModel: CounterViewModel = hiltViewModel()

            // Pass the ViewModel to the app's navigation
            AppNavigation(viewModel = viewModel)

            // Track folding events
            TrackFoldingEvents(viewModel = viewModel)
        }
    }

    @Composable
    @RequiresApi(Build.VERSION_CODES.O)
    private fun TrackFoldingEvents(viewModel: CounterViewModel) {
        val windowInfoTracker = WindowInfoTracker.getOrCreate(this)
        val layoutInfoFlow = windowInfoTracker.windowLayoutInfo(this)

        // Observe folding events in a lifecycle-aware scope
        LaunchedEffect(Unit) {
            layoutInfoFlow.collect { layoutInfo: WindowLayoutInfo ->
                // Log layout info for debugging
                Log.d("FoldTracker", "WindowLayoutInfo: $layoutInfo")

                // Detect if the device is folded
                val isFolded = layoutInfo.displayFeatures.any { feature ->
                    feature is FoldingFeature && feature.state == FoldingFeature.State.HALF_OPENED
                }

                if (isFolded) {
                    Log.d("FoldTracker", "Device is folded!")
                    viewModel.incrementCounter()
                } else {
                    Log.d("FoldTracker", "Device is NOT folded.")
                }
            }
        }
    }
}
