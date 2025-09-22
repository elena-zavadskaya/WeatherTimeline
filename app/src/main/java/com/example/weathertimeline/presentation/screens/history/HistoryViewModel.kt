package com.example.weathertimeline.presentation.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HistoryViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    // Здесь будет логика для работы с историей запросов

    init {
        loadHistory()
    }

    private fun loadHistory() {
        // Загрузка истории из базы данных или кэша
    }
}

data class HistoryUiState(
    val weatherHistory: List<WeatherHistoryItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

data class WeatherHistoryItem(
    val id: Long,
    val date: String,
    val time: String,
    val location: String,
    val temperature: String,
    val description: String
)