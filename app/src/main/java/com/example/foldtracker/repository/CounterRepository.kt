package com.example.foldtracker.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.example.foldtracker.model.CounterPreferences
import com.example.foldtracker.model.dataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import org.json.JSONObject
import javax.inject.Inject

class CounterRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val dataStore: DataStore<Preferences> = context.dataStore

    suspend fun getCounter(): Int {
        val preferences = dataStore.data.first()
        return preferences[CounterPreferences.COUNTER_KEY] ?: 0
    }

    suspend fun incrementCounter(currentCount: Int) {
        dataStore.edit { preferences ->
            preferences[CounterPreferences.COUNTER_KEY] = currentCount + 1

        }
    }

    suspend fun resetCounter() {
        dataStore.edit { preferences ->
            preferences[CounterPreferences.COUNTER_KEY] = 0

        }
    }

    suspend fun getDailyStats(): Map<String, Int> {
        val preferences = dataStore.data.first()
        val statsJson = preferences[CounterPreferences.STATS_KEY]
        return statsJson?.let { jsonToMap(it) } ?: emptyMap()
    }


    suspend fun updateDailyStats(updatedStats: Map<String, Int>) {
        dataStore.edit { preferences ->
            preferences[CounterPreferences.STATS_KEY] = mapToJson(updatedStats)

        }
    }


    suspend fun isDarkTheme(): Boolean {
        val preferences = dataStore.data.first()
        return preferences[CounterPreferences.THEME_KEY] ?: false
    }

    suspend fun toggleTheme(currentTheme: Boolean) {
        dataStore.edit { preferences ->
            preferences[CounterPreferences.THEME_KEY] = !currentTheme
        }
    }

    private fun mapToJson(map: Map<String, Int>): String {
        return JSONObject(map as Map<*, *>).toString()
    }

    private fun jsonToMap(json: String): Map<String, Int> {
        val map = mutableMapOf<String, Int>()
        val jsonObject = JSONObject(json)
        jsonObject.keys().forEach { key ->
            map[key] = jsonObject.getInt(key)

        }
        return map
    }
}