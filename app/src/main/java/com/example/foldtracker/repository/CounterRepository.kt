package com.example.foldtracker.repository

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.example.foldtracker.R
import com.example.foldtracker.datastore.DataStoreKeys
import com.example.foldtracker.datastore.DataStoreKeys.COUNTER_KEY
import com.example.foldtracker.datastore.DataStoreKeys.HINGE_ANGLE_KEY
import com.example.foldtracker.datastore.FoldPreferencesManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CounterRepository @Inject constructor(
    val dataStore: DataStore<Preferences>,
    private val context: Context
) {

    suspend fun getCounter(): Int {
        return dataStore.data.map { it[COUNTER_KEY] ?: 0 }.first()
    }

    suspend fun updateCounter(newValue: Int) {
        dataStore.edit { preferences ->
            preferences[COUNTER_KEY] = newValue
        }
    }

    suspend fun getDailyCount(date: String): Int {
        return dataStore.data.map { it[DataStoreKeys.dailyCountKey(date)] ?: 0 }.first()
    }

    suspend fun updateDailyCount(date: String, newValue: Int) {
        dataStore.edit { preferences ->
            preferences[DataStoreKeys.dailyCountKey(date)] = newValue
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getLastUpdatedDate(): String {
        return dataStore.data.map { it[DataStoreKeys.LAST_UPDATED_DATE_KEY] ?: getTodayDate() }
            .first()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun initializeDefaultValues() {
        dataStore.edit { preferences ->

            val isFirstLaunch = preferences[DataStoreKeys.FIRST_LAUNCH_KEY] ?: true
            if (isFirstLaunch) {

                preferences[COUNTER_KEY] = 0
                preferences[DataStoreKeys.LAST_UPDATED_DATE_KEY] = getTodayDate()
                preferences[DataStoreKeys.FIRST_LAUNCH_KEY] = false
            }
        }
    }

    suspend fun updateLastUpdatedDate(date: String) {
        dataStore.edit { preferences ->
            preferences[DataStoreKeys.LAST_UPDATED_DATE_KEY] = date
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getDailyFoldCounts(days: Int): List<Int> {
        val today = LocalDate.now()
        return (0 until days).map { i ->
            val date = today.minusDays(i.toLong()).toString()
            dataStore.data.first()[DataStoreKeys.dailyCountKey(date)] ?: 0
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun clearDailyFoldCounts() {
        dataStore.edit { preferences ->
            val today = LocalDate.now()
            for (i in 0 until 7) {
                val date = today.minusDays(i.toLong()).toString()
                preferences.remove(DataStoreKeys.dailyCountKey(date))
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getAllDailyFoldCounts(): List<Int> {
        val preferences = dataStore.data.first()

        return preferences.asMap().entries
            .filter { it.key.name.startsWith("daily_count_") }
            .mapNotNull { it.value as? Int }
    }


    suspend fun updateHingeAngle(angle: Int) {
        dataStore.edit { preferences ->
            preferences[HINGE_ANGLE_KEY] = angle
        }
    }

    suspend fun getHingeAngle(): Int {
        return dataStore.data.map { it[HINGE_ANGLE_KEY] ?: 0 }.first()
    }

    suspend fun setDailyLimit(limit: Int) {
        dataStore.edit { preferences ->
            preferences[DataStoreKeys.DAILY_LIMIT_KEY] = limit
        }
    }

    suspend fun getDailyLimit(): Int {
        val preferences = dataStore.data.first()
        return preferences[DataStoreKeys.DAILY_LIMIT_KEY] ?: 50
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getTodayDate(): String = LocalDate.now().toString()

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun sendDailyLimitNotification(dailyLimit: Int) {
        withContext(Dispatchers.IO) {
            val todayDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val preferencesManager = FoldPreferencesManager(context)

            preferencesManager.getLastNotifiedDate().collect { lastNotifiedDate ->
                if (lastNotifiedDate == todayDate) {
                    Log.d("Notification", "Daily limit notification already sent today")
                    return@collect
                }

                val notificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val channel = NotificationChannel(
                        "daily_limit_channel",
                        "Daily Fold Limit",
                        NotificationManager.IMPORTANCE_HIGH
                    ).apply {
                        description = "Notifies when daily fold limit is reached"
                    }
                    notificationManager.createNotificationChannel(channel)
                }

                val notification = NotificationCompat.Builder(context, "daily_limit_channel")
                    .setSmallIcon(R.drawable.ic_foldable)
                    .setContentTitle("Daily Limit Reached!")
                    .setContentText("You've reached your daily fold limit of $dailyLimit folds.")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true)
                    .build()

                val notificationId = System.currentTimeMillis().toInt()
                notificationManager.notify(notificationId, notification)

                preferencesManager.saveLastNotifiedDate(todayDate)

                Log.d("Notification", "Daily limit notification sent with ID: $notificationId")
            }
        }
    }
}
