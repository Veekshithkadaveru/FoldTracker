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

    private val today: String = LocalDate.now().toString()

    init {
        viewModelScope.launch {
            val storedCounter = repository.getCounter()


            if (!repository.isFirstLaunchDone()) {
                repository.updateCounter(0)
                repository.setFirstLaunchDone()
                _counter.value = 0
            } else {
                _counter.value = storedCounter
            }

            val lastSavedDate = repository.getLastUpdatedDate()

            if (lastSavedDate.isEmpty() || lastSavedDate != today) {
                repository.updateDailyCount(today, 0)
                repository.updateLastUpdatedDate(today)
                _dailyFolds.value = 0
            } else {
                _dailyFolds.value = repository.getDailyCount(today)
            }

            updateAchievementsAndProgress(_counter.value)
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

            updateAchievementsAndProgress(newCounter)
            FoldCountWidget.updateWidget(context)
        }
    }

    fun resetCounter(context: Context) {
        viewModelScope.launch {
            _counter.value = 0
            _dailyFolds.value = 0
            repository.updateCounter(0)
            repository.updateDailyCount(today, 0)
            updateAchievementsAndProgress(0)
            FoldCountWidget.updateWidget(context)
        }
    }

    private fun updateAchievementsAndProgress(count: Int) {
        val newAchievements = mutableListOf<String>()
        val milestones = listOf(10, 50, 100, 500)

        milestones.forEach { milestone ->
            if (count >= milestone) {
                newAchievements.add("Unlocked $milestone folds!")
            }
        }

        _achievements.value = newAchievements

        val nextMilestone = milestones.firstOrNull { it > count } ?: milestones.last()
        _progressToNextAchievement.value = if (count < nextMilestone) {
            count.toFloat() / nextMilestone
        } else {
            1f
        }
    }
}
