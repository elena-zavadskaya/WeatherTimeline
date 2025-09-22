package com.example.weathertimeline

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.weathertimeline.presentation.WeatherApp
import com.example.weathertimeline.presentation.ui.theme.WeatherTimelineTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WeatherTimelineTheme {
                WeatherApp()
            }
        }
    }
}