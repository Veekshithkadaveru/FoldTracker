package com.example.foldtracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foldtracker.repository.CounterRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CounterViewModel @Inject constructor(
    private val repository: CounterRepository
) : ViewModel() {

    // Counter state
    private val _counter = MutableStateFlow(0)
    val counter: StateFlow<Int> = _counter

    // Dark theme toggle
    private val _isDarkTheme = MutableStateFlow(false)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme

    // Achievements
    private val _achievements = MutableStateFlow<List<String>>(emptyList())
    val achievements: StateFlow<List<String>> = _achievements

    init {
        viewModelScope.launch {
            // Fetch initial counter and theme state
            _counter.value = repository.getCounter()
            _isDarkTheme.value = repository.getThemeState()

            // Update achievements based on initial counter value
            updateAchievements(_counter.value)
        }
    }

    // Increment counter and check achievements
    fun incrementCounter() {
        viewModelScope.launch {
            val newCounter = _counter.value + 1
            _counter.value = newCounter
            repository.updateCounter(newCounter)
            updateAchievements(newCounter)
        }
    }

    // Toggle dark/light theme
    fun toggleTheme() {
        viewModelScope.launch {
            val newThemeState = !_isDarkTheme.value
            _isDarkTheme.value = newThemeState
            repository.updateThemeState(newThemeState)
        }
    }

    // Unlock achievements based on fold count
    private fun updateAchievements(count: Int) {
        val newAchievements = mutableListOf<String>()

        if (count >= 10) newAchievements.add("First 10 Folds")
        if (count >= 50) newAchievements.add("Fold Enthusiast (50 Folds)")
        if (count >= 100) newAchievements.add("Centurion (100 Folds)")
        if (count >= 500) newAchievements.add("Fold Master (500 Folds)")

        _achievements.value = newAchievements
    }
}
