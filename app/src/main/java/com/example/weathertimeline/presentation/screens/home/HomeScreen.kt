package com.example.weathertimeline.presentation.screens.home

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.Manifest
import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.TextFieldDefaults
import org.koin.androidx.compose.koinViewModel

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
                        snackbarHostState = snackbarHostState
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
                                kotlinx.coroutines.delay(1000)
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
                    context = context,
                    isLocationPermissionGranted = locationPermissionState.status.isGranted
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
                    isLocationPermissionGranted = locationPermissionState.status.isGranted
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionRequestCard(
    locationPermissionState: com.google.accompanist.permissions.PermissionState,
    snackbarHostState: SnackbarHostState
) {
    val scope = rememberCoroutineScope()

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Требуется разрешение",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onErrorContainer
            )

            Text(
                text = "Для определения местоположения необходимо предоставить разрешение на доступ к GPS",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer
            )

            Button(
                onClick = {
                    scope.launch {
                        locationPermissionState.launchPermissionRequest()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                )
            ) {
                Text("Предоставить разрешение")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputFormCard(
    selectedDate: LocalDate,
    selectedTime: LocalTime,
    onDateSelected: (LocalDate) -> Unit,
    onTimeSelected: (LocalTime) -> Unit,
    onGetWeather: () -> Unit,
    isLoading: Boolean,
    context: android.content.Context,
    isLocationPermissionGranted: Boolean
) {
    val formattedDate = selectedDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
    val formattedTime = selectedTime.format(DateTimeFormatter.ofPattern("HH:mm"))

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Выберите дату и время",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            DatePickerField(
                value = formattedDate,
                label = "Дата",
                icon = Icons.Default.CalendarToday,
                onDateSelected = onDateSelected,
                context = context
            )

            TimePickerField(
                value = formattedTime,
                label = "Время",
                icon = Icons.Default.Schedule,
                onTimeSelected = onTimeSelected,
                context = context
            )

            Text(
                text = if (isLocationPermissionGranted) {
                    "📍 Местоположение определяется автоматически"
                } else {
                    "📍 Для определения местоположения предоставьте разрешение"
                },
                style = MaterialTheme.typography.bodySmall,
                color = if (isLocationPermissionGranted) {
                    MaterialTheme.colorScheme.onSurfaceVariant
                } else {
                    MaterialTheme.colorScheme.error
                },
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Button(
                onClick = onGetWeather,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                enabled = !isLoading && isLocationPermissionGranted
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Cloud,
                        contentDescription = "Погода",
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                Text(
                    text = when {
                        isLoading -> "Загрузка..."
                        !isLocationPermissionGranted -> "Разрешение требуется"
                        else -> "Узнать погоду"
                    },
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerField(
    value: String,
    label: String,
    icon: ImageVector,
    onDateSelected: (LocalDate) -> Unit,
    context: android.content.Context
) {
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    val minDateCalendar = Calendar.getInstance().apply {
        set(2022, 0, 1)
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                val datePickerDialog = DatePickerDialog(
                    context,
                    { _, selectedYear, selectedMonth, selectedDay ->
                        val selectedDate = LocalDate.of(selectedYear, selectedMonth + 1, selectedDay)
                        onDateSelected(selectedDate)
                    },
                    year,
                    month,
                    day
                )

                datePickerDialog.datePicker.minDate = minDateCalendar.timeInMillis

                datePickerDialog.datePicker.maxDate = calendar.timeInMillis

                datePickerDialog.show()
            },
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            label = { Text(label) },
            leadingIcon = {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            enabled = false,
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                disabledBorderColor = MaterialTheme.colorScheme.outline,
                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledLeadingIconColor = MaterialTheme.colorScheme.primary
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerField(
    value: String,
    label: String,
    icon: ImageVector,
    onTimeSelected: (LocalTime) -> Unit,
    context: android.content.Context
) {
    val calendar = Calendar.getInstance()
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                // Создаем TimePickerDialog
                val timePickerDialog = TimePickerDialog(
                    context,
                    { _, selectedHour, selectedMinute ->
                        val selectedTime = LocalTime.of(selectedHour, selectedMinute)
                        onTimeSelected(selectedTime)
                    },
                    hour,
                    minute,
                    true
                )

                timePickerDialog.show()
            },
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            label = { Text(label) },
            leadingIcon = {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            enabled = false,
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                disabledBorderColor = MaterialTheme.colorScheme.outline,
                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledLeadingIconColor = MaterialTheme.colorScheme.primary
            )
        )
    }
}

@Composable
fun WeatherResultCard(
    selectedDate: LocalDate?,
    selectedTime: LocalTime?,
    temperature: String,
    weatherDescription: String,
    windSpeed: String,
    humidity: String,
    isLoading: Boolean,
    error: String?,
    locationData: com.example.weathertimeline.domain.model.LocationData?,
    isLocationPermissionGranted: Boolean
) {
    val minDate = LocalDate.of(2022, 1, 1)
    val isDateValid = selectedDate != null && !selectedDate.isBefore(minDate)

    val formattedDateTime = if (isDateValid && selectedTime != null) {
        val dateTime = LocalDateTime.of(selectedDate, selectedTime)
        dateTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))
    } else if (!isDateValid) {
        "Дата должна быть не ранее 01.01.2022"
    } else {
        "Выберите дату и время"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (!isLocationPermissionGranted) {
                Icon(
                    imageVector = Icons.Default.Cloud,
                    contentDescription = "Разрешение требуется",
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.error
                )
                Text(
                    text = "Разрешение на местоположение не предоставлено",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Предоставьте разрешение на доступ к местоположению для получения данных о погоде",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                )
            } else if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(64.dp),
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Загрузка данных...",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            } else if (error != null) {
                Icon(
                    imageVector = Icons.Default.Cloud,
                    contentDescription = "Ошибка",
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.error
                )
                Text(
                    text = "Ошибка",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error
                )
                Text(
                    text = error,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Cloud,
                    contentDescription = "Погода",
                    modifier = Modifier.size(64.dp),
                    tint = if (isDateValid) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.error
                )

                Text(
                    text = if (isDateValid) "Данные о погоде" else "Некорректная дата",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (isDateValid) MaterialTheme.colorScheme.onSurface
                    else MaterialTheme.colorScheme.error
                )

                Text(
                    text = formattedDateTime,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = if (isDateValid) MaterialTheme.colorScheme.onSurface
                    else MaterialTheme.colorScheme.error
                )

                if (isDateValid) {
                    locationData?.let { location ->
                        Text(
                            text = "Широта: ${"%.4f".format(location.latitude)}, Долгота: ${"%.4f".format(location.longitude)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Text(
                        text = temperature,
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = weatherDescription,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Скорость ветра: $windSpeed",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Влажность: $humidity",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    Text(
                        text = "Пожалуйста, выберите дату не ранее 1 января 2022 года",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    MaterialTheme {
        HomeScreen()
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun HomeScreenPreviewSmall() {
    MaterialTheme {
        HomeScreen()
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun HomeScreenPreviewDark() {
    MaterialTheme {
        HomeScreen()
    }
}