package com.example.foldtracker.repository

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import com.example.foldtracker.model.CounterPreferences
import com.example.foldtracker.model.dataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.json.JSONObject
import javax.inject.Inject

class CounterRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    companion object {
        val COUNTER_KEY = intPreferencesKey("counter_key")
        val THEME_KEY = booleanPreferencesKey("theme_key")
    }

    // Fetch the current counter value
    suspend fun getCounter(): Int {
        return dataStore.data.map { preferences ->
            preferences[COUNTER_KEY] ?: 0
        }.first()
    }

    // Update the counter value
    suspend fun updateCounter(newValue: Int) {
        dataStore.edit { preferences ->
            preferences[COUNTER_KEY] = newValue
        }
    }

    // Fetch the current theme state
    suspend fun getThemeState(): Boolean {
        return dataStore.data.map { preferences ->
            preferences[THEME_KEY] ?: false
        }.first()
    }

    // Update the theme state
    suspend fun updateThemeState(isDarkTheme: Boolean) {
        dataStore.edit { preferences ->
            preferences[THEME_KEY] = isDarkTheme
        }
    }
}

