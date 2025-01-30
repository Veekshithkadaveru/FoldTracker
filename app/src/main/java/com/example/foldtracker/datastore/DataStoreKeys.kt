package com.example.foldtracker.datastore

import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object DataStoreKeys {
    val COUNTER_KEY = intPreferencesKey("counter_key")
    val DAILY_COUNT_KEY = stringPreferencesKey("daily_count_key")
    val LAST_UPDATED_DATE_KEY = stringPreferencesKey("last_updated_date_key")
}