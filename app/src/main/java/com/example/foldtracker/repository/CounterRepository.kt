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
import kotlinx.coroutines.flow.map

class CounterRepository(
    private val dataStore: DataStore<Preferences>
) {

    companion object {
        val COUNTER_KEY = intPreferencesKey("counter_key")
        val THEME_KEY = booleanPreferencesKey("theme_key")
        val STATS_KEY = stringPreferencesKey("stats_key")
    }

    // Retrieve the counter value
    val counter: Flow<Int> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences())
            else throw exception
        }
        .map { preferences ->
            preferences[COUNTER_KEY] ?: 0
        }

    // Retrieve the theme state
    val themeState: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences())
            else throw exception
        }
        .map { preferences ->
            preferences[THEME_KEY] ?: false
        }

    // Update the counter value
    suspend fun updateCounter(value: Int) {
        dataStore.edit { preferences ->
            preferences[COUNTER_KEY] = value
        }
    }

    // Update the theme state
    suspend fun updateThemeState(isDarkTheme: Boolean) {
        dataStore.edit { preferences ->
            preferences[THEME_KEY] = isDarkTheme
        }
    }
}