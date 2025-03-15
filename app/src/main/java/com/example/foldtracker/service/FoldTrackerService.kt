package com.example.foldtracker.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.foldtracker.logging.util.logd
import com.example.foldtracker.logging.util.loge
import com.example.foldtracker.repository.CounterRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import javax.inject.Inject

@AndroidEntryPoint
class FoldTrackerService : Service(), SensorEventListener {

    @Inject
    lateinit var repository: CounterRepository

    private lateinit var sensorManager: SensorManager
    private var hingeSensor: Sensor? = null

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var wasDeviceClosed = true
    
    override fun onCreate() {
        super.onCreate()
        logd("Service started")

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        hingeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HINGE_ANGLE)

        startForeground(1, createNotification())

        if (hingeSensor != null) {
            sensorManager.registerListener(this, hingeSensor, SensorManager.SENSOR_DELAY_NORMAL)
            logd("Hinge sensor registered.")
        } else {
            loge("Hinge sensor not available; using fallback simulation.")
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_HINGE_ANGLE) {
            val angle = event.values[0].toInt()
            logd("Hinge angle: $angle")

            serviceScope.launch {
                repository.updateHingeAngle(angle)
            }

            if (angle in 90..180) {
                if (wasDeviceClosed) {
                    serviceScope.launch { updateCounts() }
                    wasDeviceClosed = false
                }
            } else if (angle <= 10) {
                wasDeviceClosed = true
            }
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        // Not used
    }
    
    private fun createNotification(): Notification {
        val channelId = "fold_tracker_service_channel"
        val channelName = "Fold Tracker Service"
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW)
        manager.createNotificationChannel(channel)

        // Launch a coroutine to get the fold counts
        var totalFolds = 0
        var dailyFolds = 0
        
        runBlocking {
            try {
                totalFolds = repository.getCounter()
                val today = LocalDate.now().toString()
                dailyFolds = repository.getDailyCount(today)
            } catch (e: Exception) {
                loge("Error getting fold counts: ${e.message}")
            }
        }

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Fold Tracker Running")
            .setContentText("Total: $totalFolds folds | Today: $dailyFolds folds")
            .setSmallIcon(android.R.drawable.ic_menu_info_details)
            .build()
    }

    private fun updateNotification() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, createNotification())
    }

    private suspend fun updateCounts() {
        try {
            val today = LocalDate.now().toString()
            val lastSavedDate = repository.getLastUpdatedDate()

            if (lastSavedDate != today) {
                repository.updateDailyCount(today, 0)
                repository.updateLastUpdatedDate(today)
                logd("New day detected. Daily count reset.")
            }

            val currentDaily = repository.getDailyCount(today)
            val newDaily = currentDaily + 1
            repository.updateDailyCount(today, newDaily)

            val currentTotal = repository.getCounter()
            val newTotal = currentTotal + 1
            repository.updateCounter(newTotal)

            logd("Fold count updated: Total=$newTotal, Daily=$newDaily")

            // Update the notification with new fold counts
            updateNotification()

            if (newDaily >= repository.getDailyLimit()) {
                CoroutineScope(Dispatchers.IO).launch {
                    repository.sendDailyLimitNotification(repository.getDailyLimit())
                    logd("Daily limit reached. Notification sent.")
                }
            }

            logd("Counts updated successfully.")

        } catch (e: Exception) {
            loge("Error updating counts: ${e.message}")
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)
        serviceScope.cancel()
        logd("Service stopped")
    }

    override fun onBind(intent: Intent?): IBinder? = null
}