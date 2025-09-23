package com.example.weathertimeline.presentation.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weathertimeline.domain.model.WeatherHistory
import com.example.weathertimeline.domain.usecase.DeleteWeatherHistoryUseCase
import com.example.weathertimeline.domain.usecase.GetWeatherHistoryUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class HistoryViewModel(
    private val getWeatherHistoryUseCase: GetWeatherHistoryUseCase,
    private val deleteWeatherHistoryUseCase: DeleteWeatherHistoryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    init {
        loadHistory()
    }

    private fun loadHistory() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                getWeatherHistoryUseCase().collect { historyList ->
                    val historyItems = historyList.map { weatherHistory ->
                        WeatherHistoryItem(
                            id = weatherHistory.id,
                            dateTime = weatherHistory.dateTime,
                            locationName = formatLocationName(weatherHistory),
                            temperature = "${weatherHistory.temperature.toInt()}°C",
                            description = weatherHistory.weatherDescription,
                            windSpeed = "${weatherHistory.windSpeed.toInt()} km/h",
                            humidity = "${weatherHistory.humidity}%",
                            coordinates = formatCoordinates(weatherHistory.latitude, weatherHistory.longitude)
                        )
                    }
                    _uiState.value = _uiState.value.copy(
                        weatherHistory = historyItems,
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Ошибка загрузки истории: ${e.message}"
                )
            }
        }
    }

    fun deleteHistoryItem(id: Long) {
        viewModelScope.launch {
            try {
                deleteWeatherHistoryUseCase(id)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Ошибка удаления: ${e.message}"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun refreshHistory() {
        loadHistory()
    }

    private fun formatLocationName(weatherHistory: WeatherHistory): String {
        return if (weatherHistory.isCurrentLocation) {
            "Текущее местоположение"
        } else {
            "Широта: ${"%.4f".format(weatherHistory.latitude)}, Долгота: ${"%.4f".format(weatherHistory.longitude)}"
        }
    }

    private fun formatCoordinates(latitude: Double, longitude: Double): String {
        val latDirection = if (latitude >= 0) "с.ш." else "ю.ш."
        val lonDirection = if (longitude >= 0) "в.д." else "з.д."
        return "${"%.4f".format(kotlin.math.abs(latitude))}° $latDirection, ${"%.4f".format(kotlin.math.abs(longitude))}° $lonDirection"
    }
}

data class HistoryUiState(
    val weatherHistory: List<WeatherHistoryItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

data class WeatherHistoryItem(
    val id: Long,
    val dateTime: LocalDateTime,
    val locationName: String,
    val temperature: String,
    val description: String,
    val windSpeed: String,
    val humidity: String,
    val coordinates: String
)