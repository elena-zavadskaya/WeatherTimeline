package com.example.weathertimeline.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    // Здесь будет логика для работы с датой, временем, местоположением и API погоды

    init {
        // Инициализация ViewModel
    }

    // Методы для обновления состояния
    fun updateDate(date: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(selectedDate = date)
        }
    }

    fun updateTime(time: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(selectedTime = time)
        }
    }
}

data class HomeUiState(
    val selectedDate: String = "",
    val selectedTime: String = "",
    val location: String = "",
    val temperature: String = "",
    val weatherDescription: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)