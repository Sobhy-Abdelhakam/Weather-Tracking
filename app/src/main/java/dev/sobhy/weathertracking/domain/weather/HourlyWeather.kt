package dev.sobhy.weathertracking.domain.weather


data class HourlyWeather(
    val time: String,
    val temperature: Double,
    val description: String,
    val icon: Int,
    val pressure: Int,
    val windSpeed: Double,
    val humidity: Double
)
