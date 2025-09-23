package com.example.weathertimeline.domain.usecase

import com.example.weathertimeline.domain.model.WeatherData
import com.example.weathertimeline.domain.repository.WeatherHistoryRepository
import java.time.LocalDateTime

class SaveWeatherHistoryUseCase (
    private val weatherHistoryRepository: WeatherHistoryRepository
) {
    suspend operator fun invoke(weatherData: WeatherData) {
        val weatherHistory = com.example.weathertimeline.domain.model.WeatherHistory(
            dateTime = LocalDateTime.now(),
            latitude = weatherData.location.latitude,
            longitude = weatherData.location.longitude,
            temperature = weatherData.temperature,
            weatherCode = weatherData.weatherCode,
            windSpeed = weatherData.windSpeed,
            humidity = weatherData.humidity,
            weatherDescription = weatherData.getWeatherDescription(),
            isCurrentLocation = true
        )

        weatherHistoryRepository.saveWeatherHistory(weatherHistory)
    }
}

class GetWeatherHistoryUseCase (
    private val weatherHistoryRepository: WeatherHistoryRepository
) {
    operator fun invoke() = weatherHistoryRepository.getAllWeatherHistory()
}

class DeleteWeatherHistoryUseCase (
    private val weatherHistoryRepository: WeatherHistoryRepository
) {
    suspend operator fun invoke(id: Long) {
        weatherHistoryRepository.deleteWeatherHistoryById(id)
    }
}

class ClearAllWeatherHistoryUseCase (
    private val weatherHistoryRepository: WeatherHistoryRepository
) {
    suspend operator fun invoke() {
        weatherHistoryRepository.deleteAllWeatherHistory()
    }
}