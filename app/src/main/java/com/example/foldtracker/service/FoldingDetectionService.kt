package com.example.foldtracker.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.window.layout.FoldingFeature
import androidx.window.layout.WindowInfoTracker
import androidx.window.layout.WindowLayoutInfo
import com.example.foldtracker.repository.CounterRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

class FoldingDetectionService : Service() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    @Inject
    lateinit var repository: CounterRepository

    private fun trackFoldingEvents() {
        val windowInfoTracker = WindowInfoTracker.getOrCreate(this)
        val layoutInfoFlow = windowInfoTracker.windowLayoutInfo(this)

        serviceScope.launch {
            layoutInfoFlow.collect { layoutInfo: WindowLayoutInfo ->
                val isFolded = layoutInfo.displayFeatures.any { feature ->
                    feature is FoldingFeature && feature.state == FoldingFeature.State.HALF_OPENED
                }
                if (isFolded) {
                    repository.updateCounter(repository.getCounter() + 1)
                    Log.d("Fold Tracker", "Device is Folded")
                } else {
                    Log.d("Fold Tracker", "Device is not Folded")
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
}