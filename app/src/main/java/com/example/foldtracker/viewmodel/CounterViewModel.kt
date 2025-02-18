package com.example.foldtracker.viewmodel

import android.content.Context
import android.os.Build
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

    // Use today's date as a String in "YYYY-MM-DD" format.
    private val today: String = LocalDate.now().toString()

    init {
        viewModelScope.launch {
            // Load the total counter from DataStore.
            _counter.value = repository.getCounter()

            // If the last saved date is empty or not equal to today,
            // reset daily folds and update the last updated date.
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

    /**
     * Increments both the total and daily fold counters.
     */
    fun incrementCounter(context: Context) {
        viewModelScope.launch {
            val newCounter = _counter.value + 1
            val newDailyCount = _dailyFolds.value + 1

            _counter.value = newCounter
            _dailyFolds.value = newDailyCount

            repository.updateCounter(newCounter)
            repository.updateDailyCount(today, newDailyCount)

            updateAchievementsAndProgress(newCounter)

            // Update the widget if integrated (pass context if needed).
            FoldCountWidget.updateWidget(context)
        }
    }

    /**
     * Resets both the total and daily fold counters.
     */
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

    /**
     * Updates achievements and progress based on the total fold count.
     */
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

    /**
     * Initializes data by checking if the stored last updated date matches today.
     * If not, resets daily folds.
     */
    fun initializeData(context: Context) {
        viewModelScope.launch {
            val lastSavedDate = repository.getLastUpdatedDate()
            if (lastSavedDate.isEmpty() || lastSavedDate != today) {
                repository.updateDailyCount(today, 0)
                repository.updateLastUpdatedDate(today)
                _dailyFolds.value = 0
            } else {
                _dailyFolds.value = repository.getDailyCount(today)
            }
            _counter.value = repository.getCounter()
            updateAchievementsAndProgress(_counter.value)
            FoldCountWidget.updateWidget(context)
        }
    }
}
