package com.example.foldtracker.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foldtracker.repository.CounterRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class CounterViewModel @Inject constructor(private val repository: CounterRepository) :
    ViewModel() {

    private val _counter = MutableStateFlow(0)
    val counter: StateFlow<Int> = _counter.asStateFlow()

    private val _isDarkTheme = MutableStateFlow(false)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    private val _dailyStats = MutableStateFlow<Map<String, Int>>(emptyMap())
    val dailyStats: StateFlow<Map<String, Int>> = _dailyStats.asStateFlow()

    private val _achievements = MutableStateFlow<List<String>>(emptyList())
    val achievements: StateFlow<List<String>> = _achievements.asStateFlow()

    init {
        loadPreferences()
    }

    private fun loadPreferences() {
        viewModelScope.launch {
            _counter.value = repository.getCounter()
            _isDarkTheme.value = repository.isDarkTheme()
            _dailyStats.value = repository.getDailyStats()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun incrementCounter() {
        viewModelScope.launch {
            val newCount = _counter.value + 1
            val today = getCurrentDate()

            repository.incrementCounter(_counter.value)
            _counter.value = newCount

            val updatedStats = _dailyStats.value.toMutableMap()
            updatedStats[today] = (updatedStats[today] ?: 0) + 1
            repository.updateDailyStats(updatedStats)
            _dailyStats.value = updatedStats

            checkAchievements(newCount, updatedStats[today] ?: 0)

        }
    }


    fun resetCounter() {
        viewModelScope.launch {
            repository.resetCounter()
            _counter.value = 0
        }
    }

    fun toggleTheme() {
        viewModelScope.launch {
            repository.toggleTheme(_isDarkTheme.value)
            _isDarkTheme.value = !_isDarkTheme.value
        }
    }

    private fun checkAchievements(totalFolds: Int, todayFolds: Int) {
        val newAchievements = mutableListOf<String>()

        if (totalFolds >= 50 && "First 50 Folds" !in _achievements.value) {
            newAchievements.add("First 50 Folds")
        }

        if (totalFolds >= 100 && "First 100 Folds" !in _achievements.value) {
            newAchievements.add("First 100 Folds")
        }

        _achievements.value += newAchievements

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getCurrentDate(): String {
        return LocalDate.now().toString()

    }
}