package dev.sobhy.weathertracking.presentation.currentweather

import dev.sobhy.weathertracking.domain.model.WeatherData

data class WeatherUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val weatherData: WeatherData? = null
)