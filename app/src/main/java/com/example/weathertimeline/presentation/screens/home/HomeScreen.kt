package com.example.weathertimeline.presentation.screens.home

import android.Manifest
import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import com.example.weathertimeline.presentation.screens.home.components.PermissionRequestCard
import com.example.weathertimeline.presentation.screens.home.components.InputFormCard
import com.example.weathertimeline.presentation.screens.home.components.WeatherResultCard
import java.time.LocalDate
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen() {
    val viewModel: HomeViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val locationState by viewModel.locationState.collectAsState()

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val locationPermissionState = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    } else {
        rememberPermissionState(Manifest.permission.ACCESS_COARSE_LOCATION)
    }

    LaunchedEffect(Unit) {
        if (!locationPermissionState.status.isGranted) {
            locationPermissionState.launchPermissionRequest()
        }
    }

    LaunchedEffect(locationPermissionState.status) {
        when {
            locationPermissionState.status.isGranted -> {
                viewModel.checkLocationPermission()
            }
            locationPermissionState.status.shouldShowRationale -> {
                snackbarHostState.showSnackbar(
                    "Для определения погоды необходимо разрешение на доступ к местоположению"
                )
            }
            else -> {
                snackbarHostState.showSnackbar(
                    "Разрешение на доступ к местоположению отклонено. Некоторые функции могут быть недоступны."
                )
            }
        }
    }

    LaunchedEffect(locationState) {
        when {
            locationState is LocationState.PermissionGranted -> {
                viewModel.getCurrentLocation()
            }
            locationState is LocationState.Error -> {
                val errorState = locationState as LocationState.Error
                snackbarHostState.showSnackbar(errorState.message)
            }
            else -> {}
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Weather Timeline",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Text(
                    text = "Узнайте погоду в любой момент времени",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                )

                if (!locationPermissionState.status.isGranted) {
                    PermissionRequestCard(
                        locationPermissionState = locationPermissionState,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                InputFormCard(
                    selectedDate = uiState.selectedDate ?: LocalDate.now(),
                    selectedTime = uiState.selectedTime ?: LocalTime.now(),
                    onDateSelected = { viewModel.updateDate(it) },
                    onTimeSelected = { viewModel.updateTime(it) },
                    onGetWeather = {
                        if (locationPermissionState.status.isGranted) {
                            if (uiState.locationData == null) {
                                viewModel.getCurrentLocation()
                            }
                            scope.launch {
                                delay(1000)
                                viewModel.fetchWeatherData()
                            }
                        } else {
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    "Сначала предоставьте разрешение на доступ к местоположению"
                                )
                            }
                        }
                    },
                    isLoading = uiState.isLoading,
                    isLocationPermissionGranted = locationPermissionState.status.isGranted,
                    modifier = Modifier.fillMaxWidth()
                )

                WeatherResultCard(
                    selectedDate = uiState.selectedDate,
                    selectedTime = uiState.selectedTime,
                    temperature = uiState.temperature,
                    weatherDescription = uiState.weatherDescription,
                    windSpeed = uiState.windSpeed,
                    humidity = uiState.humidity,
                    isLoading = uiState.isLoading,
                    error = uiState.error,
                    locationData = uiState.locationData,
                    isLocationPermissionGranted = locationPermissionState.status.isGranted,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}