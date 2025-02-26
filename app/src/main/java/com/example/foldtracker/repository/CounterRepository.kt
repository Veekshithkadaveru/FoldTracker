package com.example.foldtracker.repository

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.example.foldtracker.datastore.DataStoreKeys
import com.example.foldtracker.datastore.DataStoreKeys.COUNTER_KEY
import com.example.foldtracker.datastore.DataStoreKeys.HINGE_ANGLE_KEY
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CounterRepository @Inject constructor(
    val dataStore: DataStore<Preferences>
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
        return dataStore.data.map { it[DataStoreKeys.LAST_UPDATED_DATE_KEY] ?: getTodayDate() }.first()
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

    suspend fun updateHingeAngle(angle: Float) {
        dataStore.edit { preferences ->
            preferences[HINGE_ANGLE_KEY] = angle
        }
    }
    suspend fun getHingeAngle(): Float {
        return dataStore.data.map { it[HINGE_ANGLE_KEY] ?: 0f }.first()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getTodayDate(): String = LocalDate.now().toString()
}
