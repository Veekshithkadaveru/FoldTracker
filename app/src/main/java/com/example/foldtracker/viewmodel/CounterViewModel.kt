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

    // Progress to next achievement
    private val _progressToNextAchievement = MutableStateFlow(0f)
    val progressToNextAchievement: StateFlow<Float> = _progressToNextAchievement

    init {
        viewModelScope.launch {

            _counter.value = repository.getCounter()

            updateAchievementsAndProgress(_counter.value)
        }
    }

    fun incrementCounter() {
        viewModelScope.launch {
            val newCounter = _counter.value + 1
            _counter.value = newCounter
            repository.updateCounter(newCounter)
            updateAchievementsAndProgress(newCounter)
        }
    }

    fun resetCounter() {
        viewModelScope.launch {
            _counter.value = 0
            repository.updateCounter(0)
            updateAchievementsAndProgress(0)
        }
    }


    private fun updateAchievementsAndProgress(count: Int) {
        val newAchievements = mutableListOf<String>()


        val milestones = listOf(10, 50, 100, 500)

        milestones.forEach { milestone ->
            if (count >= milestone) newAchievements.add("Unlocked $milestone folds!")
        }


        _achievements.value = newAchievements

        val nextMilestone = milestones.firstOrNull { it > count } ?: milestones.last()
        val progress = if (count < nextMilestone) {
            count.toFloat() / nextMilestone
        } else {
            1f
        }
        _progressToNextAchievement.value = progress
    }
}
