package com.example.foldtracker.repository.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "fold_prefs")

// Object to hold all DataStore keys
object DataStoreKeys {
    val COUNTER_KEY = intPreferencesKey("counter_key")
    fun dailyCountKey(date: String) = intPreferencesKey("daily_count_$date")
    val LAST_UPDATED_DATE_KEY = stringPreferencesKey("last_updated_date_key")
    val FIRST_LAUNCH_KEY = booleanPreferencesKey("first_launch_key")
    val HINGE_ANGLE_KEY = intPreferencesKey("hinge_angle_key")
    val DAILY_LIMIT_KEY = intPreferencesKey("daily_limit_key")
    val LAST_NOTIFIED_DATE_KEY = stringPreferencesKey("last_notified_date_key")
    val NOTIFICATION_PERMISSION_REQUESTED_KEY = booleanPreferencesKey("notification_permission_requested_key")
    
    // Add keys for widget refresh
    val WIDGET_REFRESH_TIMESTAMP_KEY = longPreferencesKey("widget_refresh_timestamp")
    val WIDGET_LAST_REFRESH_KEY = longPreferencesKey("widget_last_refresh")
}

// Preferences Manager
class FoldPreferencesManager(private val context: Context) {

    suspend fun saveLastNotifiedDate(date: String) {
        context.dataStore.edit { preferences ->
            preferences[DataStoreKeys.LAST_NOTIFIED_DATE_KEY] = date
        }
    }

    fun getLastNotifiedDate(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[DataStoreKeys.LAST_NOTIFIED_DATE_KEY]
        }
    }
}





