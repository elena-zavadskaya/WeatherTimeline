package com.example.weathertimeline.domain.usecase

import com.example.weathertimeline.domain.model.WeatherData
import com.example.weathertimeline.domain.repository.WeatherRepository
import java.time.LocalDate
import java.time.LocalTime

class GetWeatherUseCase (
    private val weatherRepository: WeatherRepository
) {
    suspend operator fun invoke(
        latitude: Double,
        longitude: Double,
        date: LocalDate,
        time: LocalTime?
    ): Result<WeatherData> {
        return weatherRepository.getWeatherData(latitude, longitude, date, time)
    }
}