package dev.sobhy.weathertracking.domain.model

data class WeatherInfo(
    val temperature: Double,
    val minTemp: Double,
    val maxTemp: Double,
    val description: String,
    val icon: String,
    val dateTime: String,
    val windSpeed: Double,
    val humidity: Double
)
