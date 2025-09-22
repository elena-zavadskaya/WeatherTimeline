package com.example.weathertimeline.data.api

import com.example.weathertimeline.data.model.WeatherApiResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    @GET("forecast")
    suspend fun getWeather(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String,
        @Query("hourly") hourly: String = "temperature_2m,weathercode,windspeed_10m,relativehumidity_2m",
        @Query("timezone") timezone: String = "auto"
    ): WeatherApiResponse
}