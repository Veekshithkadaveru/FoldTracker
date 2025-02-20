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
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.foldtracker.repository.CounterRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.NonCancellable.isActive
import java.time.LocalDate
import javax.inject.Inject

@AndroidEntryPoint
class FoldTrackerService : Service(), SensorEventListener {

    @Inject
    lateinit var repository: CounterRepository

    private lateinit var sensorManager: SensorManager
    private var hingeSensor: Sensor? = null

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()
        Log.d("FoldTrackerService", "Service started")

        // Initialize sensor manager and try to get the hinge angle sensor.
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        hingeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HINGE_ANGLE)


        startForeground(1, createNotification())

        if (hingeSensor != null) {

            sensorManager.registerListener(this, hingeSensor, SensorManager.SENSOR_DELAY_NORMAL)
            Log.d("FoldTrackerService", "Hinge sensor registered.")
        } else {
            Log.d("FoldTrackerService", "Hinge sensor not available; using fallback simulation.")

            serviceScope.launch {
                trackFoldEventsSimulated()
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_HINGE_ANGLE) {
            val angle = event.values[0]
            Log.d("FoldTrackerService", "Hinge angle: $angle")

            if (angle < 10f) {

                serviceScope.launch { updateCounts() }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not used in this example.
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun updateCounts() {
        val today = LocalDate.now().toString()
        val currentDaily = repository.getDailyCount(today)
        repository.updateDailyCount(today, currentDaily + 1)
        val currentTotal = repository.getCounter()
        repository.updateCounter(currentTotal + 1)
        Log.d("FoldTrackerService", "Counts updated via sensor.")
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun trackFoldEventsSimulated() {
        while (isActive) {
            delay(5000)

            val simulatedFold = (System.currentTimeMillis() / 10000) % 2L == 0L
            if (simulatedFold) {
                updateCounts()
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotification(): Notification {
        val channelId = "fold_tracker_service_channel"
        val channelName = "Fold Tracker Service"
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel =
            NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW)
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

    companion object {

        fun startService(context: Context) {
            val intent = Intent(context, FoldTrackerService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }
    }
}