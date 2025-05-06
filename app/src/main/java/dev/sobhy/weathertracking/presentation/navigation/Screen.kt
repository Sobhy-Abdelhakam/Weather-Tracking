package dev.sobhy.weathertracking.presentation.navigation

sealed class Screen(val route: String) {
    object CurrentWeather : Screen("current_weather")
    object Forecast : Screen("forecast")
}