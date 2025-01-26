package com.example.foldtracker.repository

import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
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
