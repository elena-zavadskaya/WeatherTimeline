package com.example.weathertimeline.domain.repository

import com.example.weathertimeline.domain.model.LocationData
import kotlinx.coroutines.flow.Flow

interface LocationRepository {
    suspend fun getCurrentLocation(): LocationData?
    fun getLocationUpdates(): Flow<LocationData>
    suspend fun getLastKnownLocation(): LocationData?
}