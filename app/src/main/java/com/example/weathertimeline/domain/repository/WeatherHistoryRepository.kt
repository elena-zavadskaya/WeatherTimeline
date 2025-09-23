package com.example.weathertimeline.domain.repository

import com.example.weathertimeline.domain.model.WeatherHistory
import kotlinx.coroutines.flow.Flow

interface WeatherHistoryRepository {
    fun getAllWeatherHistory(): Flow<List<WeatherHistory>>
    suspend fun getWeatherHistoryById(id: Long): WeatherHistory?
    suspend fun saveWeatherHistory(weatherHistory: WeatherHistory): Long
    suspend fun deleteWeatherHistoryById(id: Long)
    suspend fun deleteAllWeatherHistory()
    suspend fun getWeatherHistoryCount(): Int
}