package com.example.weathertimeline.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WeatherApiResponse(
    @Json(name = "latitude") val latitude: Double,
    @Json(name = "longitude") val longitude: Double,
    @Json(name = "hourly") val hourly: HourlyData,
    @Json(name = "hourly_units") val hourlyUnits: HourlyUnits
)

@JsonClass(generateAdapter = true)
data class HourlyData(
    @Json(name = "time") val time: List<String>,
    @Json(name = "temperature_2m") val temperature2m: List<Double>,
    @Json(name = "weathercode") val weatherCode: List<Int>,
    @Json(name = "windspeed_10m") val windSpeed10m: List<Double>,
    @Json(name = "relativehumidity_2m") val relativeHumidity2m: List<Int>
)

@JsonClass(generateAdapter = true)
data class HourlyUnits(
    @Json(name = "temperature_2m") val temperature2m: String,
    @Json(name = "windspeed_10m") val windSpeed10m: String,
    @Json(name = "relativehumidity_2m") val relativeHumidity2m: String
)

@JsonClass(generateAdapter = true)
data class WeatherRequest(
    val latitude: Double,
    val longitude: Double,
    val start_date: String,
    val end_date: String,
    val hourly: String = "temperature_2m,weathercode,windspeed_10m,relativehumidity_2m",
    val timezone: String = "auto"
)