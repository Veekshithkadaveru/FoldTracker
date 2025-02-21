package com.example.foldtracker.datastore

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object DataStoreKeys {
    val COUNTER_KEY = intPreferencesKey("counter_key")
    fun dailyCountKey(date: String) = intPreferencesKey("daily_count_$date")
    val LAST_UPDATED_DATE_KEY = stringPreferencesKey("last_updated_date_key")
    val FIRST_LAUNCH_KEY = booleanPreferencesKey("first_launch_key")
}