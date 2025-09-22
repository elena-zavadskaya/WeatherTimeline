package com.example.weathertimeline.presentation.screens.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun InputFormCard(
    selectedDate: LocalDate,
    selectedTime: LocalTime,
    onDateSelected: (LocalDate) -> Unit,
    onTimeSelected: (LocalTime) -> Unit,
    onGetWeather: () -> Unit,
    isLoading: Boolean,
    isLocationPermissionGranted: Boolean,
    modifier: Modifier = Modifier
) {
    val formattedDate = selectedDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
    val formattedTime = selectedTime.format(DateTimeFormatter.ofPattern("HH:mm"))

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = CardDefaults.shape,
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
                onDateSelected = onDateSelected
            )

            TimePickerField(
                value = formattedTime,
                label = "Время",
                onTimeSelected = onTimeSelected
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
                shape = ButtonDefaults.shape,
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