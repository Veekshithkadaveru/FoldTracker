package com.example.foldtracker.viewmodel

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foldtracker.R
import com.example.foldtracker.datastore.FoldPreferencesManager
import com.example.foldtracker.repository.CounterRepository
import com.example.foldtracker.widget.FoldCountWidget
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class CounterViewModel @Inject constructor(
    private val repository: CounterRepository
) : ViewModel() {

    private val _counter = MutableStateFlow(0)
    val counter: StateFlow<Int> = _counter

    private val _dailyFolds = MutableStateFlow(0)
    val dailyFolds: StateFlow<Int> = _dailyFolds

    private val _achievements = MutableStateFlow<List<String>>(emptyList())
    val achievements: StateFlow<List<String>> = _achievements

    private val _dailyLimit = MutableStateFlow(50)
    val dailyLimit: StateFlow<Int> = _dailyLimit

    private val _progressToNextAchievement = MutableStateFlow(0f)
    val progressToNextAchievement: StateFlow<Float> = _progressToNextAchievement

    private val _averageFolds = MutableStateFlow(0.0)
    val averageFolds: StateFlow<Double> = _averageFolds

    private val _hingeAngle = MutableStateFlow(0)
    val hingeAngle: StateFlow<Int> = _hingeAngle

    private val _yearlyProjection = MutableStateFlow(0)
    val yearlyProjection: StateFlow<Int> = _yearlyProjection

    private val today: String = LocalDate.now().toString()

    init {
        refreshData()
        observeDataStoreChanges()
    }

    private fun refreshData() {
        viewModelScope.launch {
            repository.initializeDefaultValues()
            loadStoredData()
            calculateAverageFolds()
            calculateYearlyProjection()
            observeHingeAngle()
            updateAchievementsAndProgress(_counter.value)
        }
    }

    private fun observeDataStoreChanges() {
        viewModelScope.launch {
            repository.dataStore.data.collect {
                refreshData()
            }
        }
    }

    private suspend fun loadStoredData() {
        _counter.value = repository.getCounter()

        val lastSavedDate = repository.getLastUpdatedDate()
        Log.d("CounterViewModel", "Last updated date: '$lastSavedDate', Today: '$today'")

        _dailyFolds.value = if (lastSavedDate == today) {
            repository.getDailyCount(today)
        } else {
            repository.updateDailyCount(today, 0)
            repository.updateLastUpdatedDate(today)
            0
        }

        _dailyLimit.value = repository.getDailyLimit()
    }

    private suspend fun observeHingeAngle() {
        repository.dataStore.data.collect {
            _hingeAngle.value = repository.getHingeAngle()
        }
    }

    fun resetCounter(context: Context) {
        viewModelScope.launch {
            _counter.value = 0
            _dailyFolds.value = 0
            _averageFolds.value = 0.0

            repository.updateCounter(0)
            repository.updateDailyCount(today, 0)
            repository.clearDailyFoldCounts()
            updateAchievementsAndProgress(0)
            calculateYearlyProjection()
            FoldCountWidget.updateWidget(context)
        }
    }

    private fun updateAchievementsAndProgress(count: Int) {
        val milestones = listOf(10, 50, 100, 500)
        _achievements.value = milestones.filter { count >= it }.map { "Unlocked $it folds!" }

        val nextMilestone = milestones.firstOrNull { it > count } ?: milestones.last()
        _progressToNextAchievement.value = count.toFloat() / nextMilestone
    }

    fun initializeData(context: Context) {
        viewModelScope.launch {
            loadStoredData()
            calculateAverageFolds()
            updateAchievementsAndProgress(_counter.value)
            FoldCountWidget.updateWidget(context)
        }
    }

    private suspend fun calculateAverageFolds() {
        val dailyCounts = repository.getDailyFoldCounts(7)
        _averageFolds.value = dailyCounts.averageOrNull() ?: 0.0
    }

    private fun calculateYearlyProjection() {
        viewModelScope.launch {
            val allDailyCounts = repository.getAllDailyFoldCounts()
            val averageFoldsPerDay = if (allDailyCounts.isNotEmpty()) allDailyCounts.average() else 0.0
            _yearlyProjection.value = (averageFoldsPerDay * 365).toInt()
        }
    }

    fun updateDailyLimit(newLimit: Int) {
        viewModelScope.launch {
            repository.setDailyLimit(newLimit)
            _dailyLimit.value = newLimit
        }
    }

    private fun List<Int>.averageOrNull(): Double? =
        if (isNotEmpty()) average() else null
}