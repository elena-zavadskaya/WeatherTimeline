package com.example.weathertimeline.domain.usecase

import com.example.weathertimeline.domain.model.LocationData
import com.example.weathertimeline.domain.repository.LocationRepository
import kotlinx.coroutines.flow.Flow

class GetCurrentLocationUseCase (
    private val locationRepository: LocationRepository
) {
    suspend operator fun invoke(): LocationData? {
        return locationRepository.getCurrentLocation()
    }

    fun getLocationUpdates(): Flow<LocationData> {
        return locationRepository.getLocationUpdates()
    }

    suspend fun getLastKnownLocation(): LocationData? {
        return locationRepository.getLastKnownLocation()
    }
}