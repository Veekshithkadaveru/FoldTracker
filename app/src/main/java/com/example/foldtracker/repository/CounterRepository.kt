package com.example.foldtracker.repository

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
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
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CounterRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    suspend fun getCounter(): Int {
        return dataStore.data.map { it[DataStoreKeys.COUNTER_KEY] ?: 0 }.first()
    }

    suspend fun updateCounter(newValue: Int) {
        dataStore.edit { it[DataStoreKeys.COUNTER_KEY] = newValue }
    }

    suspend fun getDailyCount(date: String): Int {
        return dataStore.data.map { it[DataStoreKeys.dailyCountKey(date)] ?: 0 }.first()
    }

    suspend fun updateDailyCount(date: String, newCount: Int) {
        dataStore.edit { it[DataStoreKeys.dailyCountKey(date)] = newCount }
    }

    suspend fun getLastUpdatedDate(): String {
        return dataStore.data.map { it[DataStoreKeys.LAST_UPDATED_DATE_KEY] ?: "" }.first()
    }

    suspend fun updateLastUpdatedDate(date: String) {
        dataStore.edit { it[DataStoreKeys.LAST_UPDATED_DATE_KEY] = date }
    }

    suspend fun isFirstLaunchDone(): Boolean {
        return dataStore.data.map { it[DataStoreKeys.FIRST_LAUNCH_KEY] ?: false }.first()
    }

    suspend fun setFirstLaunchDone() {
        dataStore.edit { it[DataStoreKeys.FIRST_LAUNCH_KEY] = true }
    }
}
