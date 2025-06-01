package dev.sobhy.weathertracking.domain.model

data class WeatherData(
    val currentWeatherData: HourlyWeather,
    val minTemp: Double,
    val maxTemp: Double,
    val weatherDuringTheDay: List<HourlyWeather>
)
