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

    private val _counter = MutableStateFlow(0)
    val counter: StateFlow<Int> = _counter

    private val _isDarkTheme = MutableStateFlow(false)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme

    init {
        viewModelScope.launch {
            // Fetch initial values
            repository.counter.collect { _counter.value = it }
            repository.themeState.collect { _isDarkTheme.value = it }
        }
    }

    // Increment counter
    fun incrementCounter() {
        viewModelScope.launch {
            val newCounter = _counter.value + 1
            repository.updateCounter(newCounter)
        }
    }

    // Toggle theme
    fun toggleTheme() {
        viewModelScope.launch {
            val newThemeState = !_isDarkTheme.value
            repository.updateThemeState(newThemeState)
        }
    }
}