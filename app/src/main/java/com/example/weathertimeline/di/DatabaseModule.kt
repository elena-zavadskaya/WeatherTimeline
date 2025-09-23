package com.example.weathertimeline.di

import com.example.weathertimeline.data.local.database.WeatherDatabase
import com.example.weathertimeline.data.repository.WeatherHistoryRepositoryImpl
import com.example.weathertimeline.domain.repository.WeatherHistoryRepository
import com.example.weathertimeline.domain.usecase.*
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {
    single { WeatherDatabase.getInstance(androidContext()) }
    single { get<WeatherDatabase>().weatherHistoryDao() }

    single<WeatherHistoryRepository> { WeatherHistoryRepositoryImpl(get()) }

    single { SaveWeatherHistoryUseCase(get()) }
    single { GetWeatherHistoryUseCase(get()) }
    single { DeleteWeatherHistoryUseCase(get()) }
    single { ClearAllWeatherHistoryUseCase(get()) }
}