package com.example.foldtracker.model

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object CounterPreferences {
    val COUNTER_KEY = intPreferencesKey("counter_key")
    val THEME_KEY = booleanPreferencesKey("theme_key")
    val STATS_KEY = stringPreferencesKey("stats_key")
}