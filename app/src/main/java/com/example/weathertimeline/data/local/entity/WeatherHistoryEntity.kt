package com.example.weathertimeline.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.weathertimeline.domain.model.WeatherHistory
import java.time.LocalDateTime

@Entity(tableName = "weather_history")
data class WeatherHistoryEntity(
    @PrimaryKey(autoGenerate = true)
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
    fun toWeatherHistory(): WeatherHistory {
        return WeatherHistory(
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