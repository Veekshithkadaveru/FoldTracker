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
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.foldtracker.repository.CounterRepository
import com.example.foldtracker.viewmodel.CounterViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable.isActive
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.*

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
        Log.d("FoldTrackerService", "Service started")

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        hingeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HINGE_ANGLE)

        startForeground(1, createNotification())

        if (hingeSensor != null) {
            sensorManager.registerListener(this, hingeSensor, SensorManager.SENSOR_DELAY_NORMAL)
            Log.d("FoldTrackerService", "Hinge sensor registered.")
        } else {
            Log.e("FoldTrackerService", "Hinge sensor not available; using fallback simulation.")
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_HINGE_ANGLE) {
            val angle = event.values[0].toInt()
            Log.d("FoldTrackerService", "Hinge angle: $angle")

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
    
    private suspend fun updateCounts() {
        try {
            val today = LocalDate.now().toString()
            val lastSavedDate = repository.getLastUpdatedDate()

            if (lastSavedDate != today) {
                repository.updateDailyCount(today, 0)
                repository.updateLastUpdatedDate(today)
                Log.d("FoldTrackerService", "New day detected. Daily count reset.")
            }

            val currentDaily = repository.getDailyCount(today)
            val newDaily = currentDaily + 1
            repository.updateDailyCount(today, newDaily)

            val currentTotal = repository.getCounter()
            val newTotal = currentTotal + 1
            repository.updateCounter(newTotal)

            Log.d("FoldTrackerService", "Fold count updated: Total=$newTotal, Daily=$newDaily")

            if (newDaily >= repository.getDailyLimit()) {
                CoroutineScope(Dispatchers.IO).launch {
                    repository.sendDailyLimitNotification(repository.getDailyLimit())
                    Log.d("FoldTrackerService", "Daily limit reached. Notification sent.")
                }
            }

            Log.d("FoldTrackerService", "Counts updated successfully.")

        } catch (e: Exception) {
            Log.e("FoldTrackerService", "Error updating counts: ${e.message}")
            e.printStackTrace()
        }
    }
    
    private fun createNotification(): Notification {
        val channelId = "fold_tracker_service_channel"
        val channelName = "Fold Tracker Service"
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW)
        manager.createNotificationChannel(channel)

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Fold Tracker Running")
            .setContentText("Monitoring your device folds")
            .setSmallIcon(android.R.drawable.ic_menu_info_details)
            .build()
    }

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)
        serviceScope.cancel()
        Log.d("FoldTrackerService", "Service stopped")
    }

    override fun onBind(intent: Intent?): IBinder? = null
}