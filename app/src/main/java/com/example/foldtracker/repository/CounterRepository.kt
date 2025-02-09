package com.example.foldtracker.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.example.foldtracker.datastore.DataStoreKeys
import com.example.foldtracker.datastore.DataStoreKeys.COUNTER_KEY
import com.example.foldtracker.widget.FoldCountWidget.Companion.updateWidget
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

class CounterRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    suspend fun getCounter(): Int {
        return dataStore.data.map { it[COUNTER_KEY] ?: 0 }.first()
    }

    suspend fun updateCounter(newValue: Int, context: Context) {
        dataStore.edit { it[COUNTER_KEY] = newValue }

        CoroutineScope(Dispatchers.IO).launch {
            updateWidget(context)
        }
    }

    suspend fun getDailyCount(date: String): Int {
        val key = DataStoreKeys.dailyCountKey(date)
        return dataStore.data.map { preferences ->
            preferences[key] ?: 0
        }.first()
    }


    suspend fun updateDailyCount(date: String, newCount: Int, context: Context) {
        val key = DataStoreKeys.dailyCountKey(date)
        dataStore.edit { preferences ->
            preferences[key] = newCount
        }
    }

    suspend fun getLastUpdatedDate(): String? {
        return dataStore.data.map { preferences ->
            preferences[DataStoreKeys.LAST_UPDATED_DATE_KEY]
        }.first()
    }

    suspend fun updateLastUpdatedDate(date: String, context: Context) {
        dataStore.edit { preferences ->
            preferences[DataStoreKeys.LAST_UPDATED_DATE_KEY] = date
        }
    }

}