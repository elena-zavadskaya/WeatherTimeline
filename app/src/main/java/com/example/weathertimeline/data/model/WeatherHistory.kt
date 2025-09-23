package com.example.weathertimeline.domain.model

import com.example.weathertimeline.data.local.entity.WeatherHistoryEntity
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class WeatherHistory(
    val id: Long = 0,
    val dateTime: LocalDateTime,
    val latitude: Double,
    val longitude: Double,
    val temperature: Double,
    val weatherCode: Int,
    val windSpeed: Double,
    val humidity: Int,
    val weatherDescription: String,
    val locationName: String? = null,
    val isCurrentLocation: Boolean = true
) {
    fun getFormattedDateTime(): String {
        return dateTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))
    }

    fun getFormattedDate(): String {
        return dateTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
    }

    fun getFormattedTime(): String {
        return dateTime.format(DateTimeFormatter.ofPattern("HH:mm"))
    }

    fun getFormattedTemperature(): String {
        return "${temperature.toInt()}°C"
    }

    fun getFormattedWindSpeed(): String {
        return "${windSpeed.toInt()} km/h"
    }

    fun getFormattedHumidity(): String {
        return "$humidity%"
    }

    fun toWeatherHistoryEntity(): WeatherHistoryEntity {
        return WeatherHistoryEntity(
            id = id,
            dateTime = dateTime,
            latitude = latitude,
            longitude = longitude,
            temperature = temperature,
            weatherCode = weatherCode,
            windSpeed = windSpeed,
            humidity = humidity,
            weatherDescription = weatherDescription,
            locationName = locationName,
            isCurrentLocation = isCurrentLocation
        )
    }
}