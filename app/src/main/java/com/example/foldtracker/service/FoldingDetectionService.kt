package com.example.foldtracker.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.window.layout.FoldingFeature
import androidx.window.layout.WindowInfoTracker
import androidx.window.layout.WindowLayoutInfo
import com.example.foldtracker.R
import com.example.foldtracker.repository.CounterRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

class FoldingDetectionService : Service() {

    companion object {
        const val CHANNEL_ID = "FoldTrackerChannel"
        const val NOTIFICATION_ID = 1
    }

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    @Inject
    lateinit var repository: CounterRepository

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(
            NOTIFICATION_ID,
            buildNotification("Tracking Fold Events")
        )
        trackFoldingEvents()
    }

    private fun buildNotification(content: String): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Fold Tracker")
            .setContentText(content)
            .setSmallIcon(R.drawable.ic_launcher_background)//TODO("Replace with app icon")
            .setOngoing(true)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Fold Tracker Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Tracks fold events in the background."
            }
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

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