package com.example.foldtracker.feature.counter

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foldtracker.repository.CounterRepository
import com.example.foldtracker.feature.widget.FoldCountWidget
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class CounterViewModel @Inject constructor(
    private val repository: CounterRepository
) : ViewModel() {

    // StateFlows
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
            loadData()
            calculateStats()
            updateAchievements()
        }
    }

    private fun observeDataStoreChanges() {
        viewModelScope.launch {
            repository.observePreferences().collect { 
                refreshData() 
            }
        }
    }

    private suspend fun loadData() {
        _counter.value = repository.getCounter()
        _dailyFolds.value = getDailyFolds()
        _dailyLimit.value = repository.getDailyLimit()
        _hingeAngle.value = repository.getHingeAngle()
    }

    private suspend fun getDailyFolds(): Int {
        val lastSavedDate = repository.getLastUpdatedDate()
        Log.d("CounterViewModel", "Last updated date: '$lastSavedDate', Today: '$today'")
        return if (lastSavedDate == today) repository.getDailyCount(today) else {
            repository.updateDailyCount(today, 0)
            repository.updateLastUpdatedDate(today)
            0
        }
    }

    private suspend fun calculateStats() {
        _averageFolds.value = repository.getDailyFoldCounts(7).averageOrNull() ?: 0.0
        _yearlyProjection.value = calculateYearlyProjectionValue()
    }

    private suspend fun calculateYearlyProjectionValue(): Int {
        val allDailyCounts = repository.getAllDailyFoldCounts()
        val averageFoldsPerDay = if (allDailyCounts.isNotEmpty()) allDailyCounts.average() else 0.0
        return (averageFoldsPerDay * 365).toInt()
    }

    private fun updateAchievements() {
        val milestones = listOf(10, 50, 100, 500)
        _achievements.value =
            milestones.filter { _counter.value >= it }.map { "Unlocked $it folds!" }
        val nextMilestone = milestones.firstOrNull { it > _counter.value } ?: milestones.last()
        _progressToNextAchievement.value = _counter.value.toFloat() / nextMilestone
    }

    fun resetCounter(context: Context) {
        viewModelScope.launch {
            _counter.value = 0
            _dailyFolds.value = 0
            _averageFolds.value = 0.0
            repository.updateCounter(0)
            repository.updateDailyCount(today, 0)
            repository.clearDailyFoldCounts()
            updateAchievements()
            calculateStats()
            FoldCountWidget.updateWidget(context)
        }
    }

    fun initializeData(context: Context) {
        viewModelScope.launch {
            loadData()
            calculateStats()
            updateAchievements()
            FoldCountWidget.updateWidget(context)
        }
    }

    fun updateDailyLimit(newLimit: Int) {
        viewModelScope.launch {
            repository.setDailyLimit(newLimit)
            _dailyLimit.value = newLimit
        }
    }

    suspend fun isNotificationPermissionRequested(): Boolean {
        return repository.isNotificationPermissionRequested()
    }

    fun setNotificationPermissionRequested(requested: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.setNotificationPermissionRequested(requested)
        }
    }

    private fun List<Int>.averageOrNull(): Double? =
        if (isNotEmpty()) average() else null
}