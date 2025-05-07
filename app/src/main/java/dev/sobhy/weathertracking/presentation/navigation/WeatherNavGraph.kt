package dev.sobhy.weathertracking.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.sobhy.weathertracking.presentation.currentweather.WeatherScreen
import dev.sobhy.weathertracking.presentation.forecast.ForecastScreen

@Composable
fun WeatherNavGraph(
    latitude: Double,
    longitude: Double
) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.CurrentWeather.route) {
        composable(route = Screen.CurrentWeather.route) {
            WeatherScreen(latitude, longitude){
                navController.navigate(Screen.Forecast.route)
            }
        }
        composable(route = Screen.Forecast.route) {
            ForecastScreen(latitude, longitude)
        }
    }
}