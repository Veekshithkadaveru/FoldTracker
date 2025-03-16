package com.example.foldtracker.repository

import kotlinx.coroutines.flow.Flow
import androidx.datastore.preferences.core.Preferences

interface CounterRepository {
    suspend fun getCounter(): Int
    suspend fun updateCounter(newValue: Int)
    suspend fun getDailyCount(date: String): Int
    suspend fun updateDailyCount(date: String, newValue: Int)
    suspend fun getLastUpdatedDate(): String
    suspend fun initializeDefaultValues()
    suspend fun updateLastUpdatedDate(date: String)
    suspend fun getDailyFoldCounts(days: Int): List<Int>
    suspend fun clearDailyFoldCounts()
    suspend fun getAllDailyFoldCounts(): List<Int>
    suspend fun updateHingeAngle(angle: Int)
    suspend fun getHingeAngle(): Int
    suspend fun setDailyLimit(limit: Int)
    suspend fun getDailyLimit(): Int
    fun getTodayDate(): String
    suspend fun sendDailyLimitNotification(dailyLimit: Int)
    fun observePreferences(): Flow<Preferences>
    suspend fun isNotificationPermissionRequested(): Boolean
    suspend fun setNotificationPermissionRequested(requested: Boolean)
}
