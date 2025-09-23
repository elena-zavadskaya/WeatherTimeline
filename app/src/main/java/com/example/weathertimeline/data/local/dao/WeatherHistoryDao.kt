package com.example.weathertimeline.data.local.dao

import androidx.room.*
import com.example.weathertimeline.data.local.entity.WeatherHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherHistoryDao {

    @Query("SELECT * FROM weather_history ORDER BY dateTime DESC")
    fun getAllWeatherHistory(): Flow<List<WeatherHistoryEntity>>

    @Query("SELECT * FROM weather_history WHERE id = :id")
    suspend fun getWeatherHistoryById(id: Long): WeatherHistoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeatherHistory(weatherHistory: WeatherHistoryEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllWeatherHistory(weatherHistoryList: List<WeatherHistoryEntity>)

    @Update
    suspend fun updateWeatherHistory(weatherHistory: WeatherHistoryEntity)

    @Delete
    suspend fun deleteWeatherHistory(weatherHistory: WeatherHistoryEntity)

    @Query("DELETE FROM weather_history WHERE id = :id")
    suspend fun deleteWeatherHistoryById(id: Long)

    @Query("DELETE FROM weather_history")
    suspend fun deleteAllWeatherHistory()

    @Query("SELECT COUNT(*) FROM weather_history")
    suspend fun getWeatherHistoryCount(): Int

    @Query("SELECT * FROM weather_history WHERE dateTime BETWEEN :startDate AND :endDate ORDER BY dateTime DESC")
    fun getWeatherHistoryByDateRange(startDate: String, endDate: String): Flow<List<WeatherHistoryEntity>>

    @Query("SELECT * FROM weather_history WHERE weatherDescription LIKE :query ORDER BY dateTime DESC")
    fun searchWeatherHistory(query: String): Flow<List<WeatherHistoryEntity>>
}