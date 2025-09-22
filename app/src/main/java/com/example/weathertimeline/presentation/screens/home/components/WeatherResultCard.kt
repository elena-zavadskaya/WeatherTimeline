package com.example.weathertimeline.presentation.screens.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weathertimeline.domain.model.LocationData
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

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
    locationData: LocationData?,
    isLocationPermissionGranted: Boolean,
    modifier: Modifier = Modifier
) {
    val minDate = LocalDate.of(2022, 1, 1)
    val isDateValid = selectedDate != null && !selectedDate.isBefore(minDate)

    val formattedDateTime = formatDateTime(selectedDate, selectedTime, isDateValid)

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = CardDefaults.shape,
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
            when {
                !isLocationPermissionGranted -> renderPermissionRequired()
                isLoading -> renderLoading()
                error != null -> renderError(error)
                else -> renderWeatherData(
                    isDateValid = isDateValid,
                    formattedDateTime = formattedDateTime,
                    locationData = locationData,
                    temperature = temperature,
                    weatherDescription = weatherDescription,
                    windSpeed = windSpeed,
                    humidity = humidity
                )
            }
        }
    }
}

@Composable
private fun renderPermissionRequired() {
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
}

@Composable
private fun renderLoading() {
    CircularProgressIndicator(
        modifier = Modifier.size(64.dp),
        color = MaterialTheme.colorScheme.primary
    )
    Text(
        text = "Загрузка данных...",
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurface
    )
}

@Composable
private fun renderError(error: String) {
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
}

@Composable
private fun renderWeatherData(
    isDateValid: Boolean,
    formattedDateTime: String,
    locationData: LocationData?,
    temperature: String,
    weatherDescription: String,
    windSpeed: String,
    humidity: String
) {
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

private fun formatDateTime(selectedDate: LocalDate?, selectedTime: LocalTime?, isDateValid: Boolean): String {
    return if (isDateValid && selectedTime != null) {
        val dateTime = LocalDateTime.of(selectedDate, selectedTime)
        dateTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))
    } else if (!isDateValid) {
        "Дата должна быть не ранее 01.01.2022"
    } else {
        "Выберите дату и время"
    }
}