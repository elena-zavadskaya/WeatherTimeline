package com.example.weathertimeline.di

import com.example.weathertimeline.data.repository.LocationRepositoryImpl
import com.example.weathertimeline.domain.repository.LocationRepository
import com.example.weathertimeline.domain.usecase.GetCurrentLocationUseCase
import com.example.weathertimeline.presentation.screens.history.HistoryViewModel
import com.example.weathertimeline.presentation.screens.home.HomeViewModel
import com.example.weathertimeline.presentation.utils.PermissionManager
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val locationModule = module {
    single<LocationRepository> { LocationRepositoryImpl(androidContext()) }
    single { GetCurrentLocationUseCase(get()) }
    single { PermissionManager(androidContext()) }
    viewModel { HomeViewModel(get(), get(), get(), get()) }
    viewModel { HistoryViewModel(get(), get()) }
}