package com.example.weathertimeline.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weathertimeline.domain.model.LocationData
import com.example.weathertimeline.domain.usecase.GetCurrentLocationUseCase
import com.example.weathertimeline.domain.usecase.GetWeatherUseCase
import com.example.weathertimeline.presentation.utils.PermissionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

class HomeViewModel(
    private val getCurrentLocationUseCase: GetCurrentLocationUseCase,
    private val getWeatherUseCase: GetWeatherUseCase,
    private val permissionManager: PermissionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _locationState = MutableStateFlow<LocationState>(LocationState.Idle)
    val locationState: StateFlow<LocationState> = _locationState.asStateFlow()

    init {
        checkLocationPermission()
    }

    fun updateDate(date: LocalDate) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                selectedDate = date,
                selectedTime = _uiState.value.selectedTime ?: LocalTime.now()
            )
        }
    }

    fun updateTime(time: LocalTime) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                selectedTime = time,
                selectedDate = _uiState.value.selectedDate ?: LocalDate.now()
            )
        }
    }

    fun checkLocationPermission() {
        val hasPermission = permissionManager.hasLocationPermission()
        _locationState.value = if (hasPermission) {
            LocationState.PermissionGranted
        } else {
            LocationState.PermissionRequired
        }

        if (hasPermission) {
            getCurrentLocation()
        }
    }

    fun onPermissionResult(granted: Boolean) {
        if (granted) {
            _locationState.value = LocationState.PermissionGranted
            getCurrentLocation()
        } else {
            _locationState.value = LocationState.PermissionDenied
        }
    }

    fun getCurrentLocation() {
        viewModelScope.launch {
            _locationState.value = LocationState.Loading
            try {
                val location = getCurrentLocationUseCase()
                if (location != null && location.isValid()) {
                    _uiState.value = _uiState.value.copy(locationData = location)
                    _locationState.value = LocationState.Success(location)
                } else {
                    _locationState.value = LocationState.Error("Не удалось определить местоположение")
                }
            } catch (e: Exception) {
                _locationState.value = LocationState.Error("Ошибка получения местоположения: ${e.message}")
            }
        }
    }

    fun fetchWeatherData() {
        viewModelScope.launch {
            val currentState = _uiState.value
            val location = currentState.locationData
            val date = currentState.selectedDate

            if (location == null) {
                _uiState.value = currentState.copy(
                    error = "Определение местоположения... Попробуйте еще раз через несколько секунд."
                )
                return@launch
            }

            if (date == null) {
                _uiState.value = currentState.copy(
                    error = "Необходимо выбрать дату"
                )
                return@launch
            }

            _uiState.value = currentState.copy(isLoading = true, error = null)

            try {
                val result = getWeatherUseCase(
                    latitude = location.latitude,
                    longitude = location.longitude,
                    date = date,
                    time = currentState.selectedTime
                )

                if (result.isSuccess) {
                    val weatherData = result.getOrThrow()
                    _uiState.value = currentState.copy(
                        isLoading = false,
                        temperature = "${weatherData.temperature.toInt()}°C",
                        weatherDescription = weatherData.getWeatherDescription(),
                        windSpeed = "${weatherData.windSpeed.toInt()} km/h",
                        humidity = "${weatherData.humidity}%",
                        error = null
                    )
                } else {
                    _uiState.value = currentState.copy(
                        isLoading = false,
                        error = "Ошибка получения данных о погоде: ${result.exceptionOrNull()?.message}"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = currentState.copy(
                    isLoading = false,
                    error = "Ошибка: ${e.message}"
                )
            }
        }
    }
}

data class HomeUiState(
    val selectedDate: LocalDate? = null,
    val selectedTime: LocalTime? = null,
    val locationData: LocationData? = null,
    val temperature: String = "--",
    val weatherDescription: String = "Неизвестно",
    val windSpeed: String = "--",
    val humidity: String = "--",
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class LocationState {
    object Idle : LocationState()
    object PermissionRequired : LocationState()
    object PermissionGranted : LocationState()
    object PermissionDenied : LocationState()
    object Loading : LocationState()
    data class Success(val location: LocationData) : LocationState()
    data class Error(val message: String) : LocationState()
}