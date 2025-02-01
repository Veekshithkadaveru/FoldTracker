package com.example.foldtracker.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.example.foldtracker.datastore.DataStoreKeys.COUNTER_KEY
import com.example.foldtracker.datastore.DataStoreKeys.DAILY_COUNT_KEY
import com.example.foldtracker.widget.FoldCountWidget
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import android.content.Context

class CounterRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    suspend fun getCounter(): Int {
        return dataStore.data.map { it[COUNTER_KEY] ?: 0 }.first()
    }

    suspend fun updateCounter(newValue: Int,context: Context) {
        dataStore.edit { it[COUNTER_KEY] = newValue }

        CoroutineScope(Dispatchers.IO).launch {
            FoldCountWidget.updateWidget(context)
        }
    }

    suspend fun getDailyCount(date: String): Int {
        return dataStore.data.map { preferences ->
            val dailyCountsMap = preferences[DAILY_COUNT_KEY]?.let { json ->
                Json.decodeFromString<Map<String, Int>>(json)
            } ?: emptyMap()
            dailyCountsMap[date] ?: 0
        }.first()
    }

    suspend fun updateDailyCount(date: String, newValue: Int,context: Context) {
        dataStore.edit { preferences ->
            val dailyCountsMap = preferences[DAILY_COUNT_KEY]?.let { json ->
                Json.decodeFromString<Map<String, Int>>(json)
            } ?: emptyMap()
            val updatedMap = dailyCountsMap.toMutableMap().apply { put(date, newValue) }
            preferences[DAILY_COUNT_KEY] = Json.encodeToString(updatedMap)
        }

        CoroutineScope(Dispatchers.IO).launch {
            FoldCountWidget.updateWidget(context)
        }
    }
}
