package com.example.weathertimeline.domain.model

import java.time.LocalDateTime

data class WeatherData(
    val dateTime: LocalDateTime,
    val temperature: Double,
    val weatherCode: Int,
    val windSpeed: Double,
    val humidity: Int,
    val location: LocationData
) {
    fun getWeatherDescription(): String {
        return when (weatherCode) {
            0 -> "Ясно"
            1 -> "Преимущественно ясно"
            2 -> "Переменная облачность"
            3 -> "Пасмурно"
            45, 48 -> "Туман"
            51, 53, 55 -> "Морось"
            61, 63, 65 -> "Дождь"
            66, 67 -> "Ледяной дождь"
            71, 73, 75 -> "Снег"
            77 -> "Снежные зерна"
            80, 81, 82 -> "Ливень"
            85, 86 -> "Снегопад"
            95 -> "Гроза"
            96, 99 -> "Гроза с градом"
            else -> "Неизвестно"
        }
    }

    fun getWeatherIcon(): String {
        return when (weatherCode) {
            0 -> "☀️"
            1, 2 -> "⛅"
            3 -> "☁️"
            45, 48 -> "🌫️"
            51, 53, 55 -> "🌧️"
            61, 63, 65 -> "🌧️"
            66, 67 -> "🌧️❄️"
            71, 73, 75 -> "❄️"
            77 -> "🌨️"
            80, 81, 82 -> "⛈️"
            85, 86 -> "🌨️"
            95 -> "⛈️"
            96, 99 -> "⛈️🌨️"
            else -> "🌈"
        }
    }
}