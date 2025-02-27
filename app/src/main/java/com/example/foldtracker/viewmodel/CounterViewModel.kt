package com.example.foldtracker.viewmodel

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foldtracker.repository.CounterRepository
import com.example.foldtracker.widget.FoldCountWidget
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
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

    private val _progressToNextAchievement = MutableStateFlow(0f)
    val progressToNextAchievement: StateFlow<Float> = _progressToNextAchievement

    private val _averageFolds = MutableStateFlow(0.0)
    val averageFolds: StateFlow<Double> = _averageFolds

    private val _hingeAngle = MutableStateFlow(0f)
    val hingeAngle: StateFlow<Float> = _hingeAngle

    private val today: String = LocalDate.now().toString()

    init {
        viewModelScope.launch {
            repository.initializeDefaultValues()
            loadStoredData()
            observeHingeAngle()
            calculateAverageFolds()
            updateAchievementsAndProgress(_counter.value)
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
    }

    private suspend fun observeHingeAngle() {
        repository.dataStore.data.collect {
            _hingeAngle.value = repository.getHingeAngle()
        }
    }

    fun incrementCounter(context: Context) {
        viewModelScope.launch {
            val newCounter = _counter.value + 1
            val newDailyCount = _dailyFolds.value + 1

            _counter.value = newCounter
            _dailyFolds.value = newDailyCount

            repository.updateCounter(newCounter)
            repository.updateDailyCount(today, newDailyCount)

            calculateAverageFolds()
            updateAchievementsAndProgress(newCounter)
            FoldCountWidget.updateWidget(context)
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

    private fun List<Int>.averageOrNull(): Double? =
        if (isNotEmpty()) average() else null
}
