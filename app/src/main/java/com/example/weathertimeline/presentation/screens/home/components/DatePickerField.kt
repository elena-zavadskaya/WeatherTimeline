package com.example.weathertimeline.presentation.screens.home.components

import android.app.DatePickerDialog
import android.content.Context
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import java.time.LocalDate
import java.util.Calendar
import androidx.compose.foundation.clickable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerField(
    value: String,
    label: String,
    icon: ImageVector = Icons.Default.CalendarToday,
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    val minDateCalendar = Calendar.getInstance().apply {
        set(2022, 0, 1)
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                showDatePickerDialog(context, year, month, day, minDateCalendar, calendar, onDateSelected)
            },
        shape = TextFieldDefaults.outlinedShape,
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
            shape = TextFieldDefaults.outlinedShape,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                disabledBorderColor = MaterialTheme.colorScheme.outline,
                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledLeadingIconColor = MaterialTheme.colorScheme.primary
            )
        )
    }
}

private fun showDatePickerDialog(
    context: Context,
    year: Int,
    month: Int,
    day: Int,
    minDateCalendar: Calendar,
    maxDateCalendar: Calendar,
    onDateSelected: (LocalDate) -> Unit
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
    datePickerDialog.datePicker.maxDate = maxDateCalendar.timeInMillis
    datePickerDialog.show()
}