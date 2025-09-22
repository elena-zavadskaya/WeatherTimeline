package com.example.weathertimeline.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.weathertimeline.presentation.screens.history.HistoryScreen
import com.example.weathertimeline.presentation.screens.home.HomeScreen

fun NavGraphBuilder.weatherAppNavGraph(navController: NavHostController) {
    composable(Screen.Home.route) {
        HomeScreen()
    }
    composable(Screen.History.route) {
        HistoryScreen()
    }
}