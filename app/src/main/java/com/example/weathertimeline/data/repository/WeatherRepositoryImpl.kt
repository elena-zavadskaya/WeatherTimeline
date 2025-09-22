package com.example.weathertimeline.data.repository

import com.example.weathertimeline.data.api.WeatherApiService
import com.example.weathertimeline.data.model.WeatherApiResponse
import com.example.weathertimeline.domain.model.LocationData
import com.example.weathertimeline.domain.model.WeatherData
import com.example.weathertimeline.domain.repository.WeatherRepository
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class WeatherRepositoryImpl(
    private val weatherApiService: WeatherApiService
) : WeatherRepository {

    override suspend fun getWeatherData(
        latitude: Double,
        longitude: Double,
        date: LocalDate,
        time: LocalTime?
    ): Result<WeatherData> {
        return try {
            val dateString = date.format(DateTimeFormatter.ISO_DATE)

            val response = weatherApiService.getWeather(
                latitude = latitude,
                longitude = longitude,
                startDate = dateString,
                endDate = dateString
            )

            val weatherData = mapApiResponseToWeatherData(response, date, time, latitude, longitude)
            Result.success(weatherData)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun mapApiResponseToWeatherData(
        response: WeatherApiResponse,
        date: LocalDate,
        time: LocalTime?,
        latitude: Double,
        longitude: Double
    ): WeatherData {
        val location = LocationData(latitude, longitude)

        val targetTime = time ?: LocalTime.NOON
        val targetDateTime = LocalDateTime.of(date, targetTime)

        val targetTimeString = targetDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)

        val timeIndex = response.hourly.time.indexOfFirst { timeString ->
            timeString.startsWith(targetTimeString.substring(0, 13))
        }

        val index = if (timeIndex >= 0) timeIndex else 0

        return WeatherData(
            dateTime = targetDateTime,
            temperature = response.hourly.temperature2m.getOrElse(index) { 0.0 },
            weatherCode = response.hourly.weatherCode.getOrElse(index) { 0 },
            windSpeed = response.hourly.windSpeed10m.getOrElse(index) { 0.0 },
            humidity = response.hourly.relativeHumidity2m.getOrElse(index) { 0 },
            location = location
        )
    }
}