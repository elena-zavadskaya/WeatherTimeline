package com.example.weathertimeline.data.repository

import com.example.weathertimeline.data.local.dao.WeatherHistoryDao
import com.example.weathertimeline.domain.model.WeatherHistory
import com.example.weathertimeline.domain.repository.WeatherHistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class WeatherHistoryRepositoryImpl (
    private val weatherHistoryDao: WeatherHistoryDao
) : WeatherHistoryRepository {

    override fun getAllWeatherHistory(): Flow<List<WeatherHistory>> {
        return weatherHistoryDao.getAllWeatherHistory().map { entities ->
            entities.map { it.toWeatherHistory() }
        }
    }

    override suspend fun getWeatherHistoryById(id: Long): WeatherHistory? {
        return weatherHistoryDao.getWeatherHistoryById(id)?.toWeatherHistory()
    }

    override suspend fun saveWeatherHistory(weatherHistory: WeatherHistory): Long {
        return weatherHistoryDao.insertWeatherHistory(weatherHistory.toWeatherHistoryEntity())
    }

    override suspend fun deleteWeatherHistoryById(id: Long) {
        weatherHistoryDao.deleteWeatherHistoryById(id)
    }

    override suspend fun deleteAllWeatherHistory() {
        weatherHistoryDao.deleteAllWeatherHistory()
    }

    override suspend fun getWeatherHistoryCount(): Int {
        return weatherHistoryDao.getWeatherHistoryCount()
    }
}