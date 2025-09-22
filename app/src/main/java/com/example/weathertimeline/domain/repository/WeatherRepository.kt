package com.example.weathertimeline.domain.repository

import com.example.weathertimeline.domain.model.WeatherData
import java.time.LocalDate
import java.time.LocalTime

interface WeatherRepository {
    suspend fun getWeatherData(
        latitude: Double,
        longitude: Double,
        date: LocalDate,
        time: LocalTime?
    ): Result<WeatherData>
}