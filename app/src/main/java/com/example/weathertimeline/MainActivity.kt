package com.example.weathertimeline

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.weathertimeline.di.databaseModule
import com.example.weathertimeline.di.locationModule
import com.example.weathertimeline.di.networkModule
import com.example.weathertimeline.presentation.WeatherApp
import com.example.weathertimeline.presentation.ui.theme.WeatherTimelineTheme
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startKoin {
            androidContext(this@MainActivity)
            modules(networkModule, locationModule, databaseModule)
        }

        setContent {
            WeatherTimelineTheme {
                WeatherApp()
            }
        }
    }
}