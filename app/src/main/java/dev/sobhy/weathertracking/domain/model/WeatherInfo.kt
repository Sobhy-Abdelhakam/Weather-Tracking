package dev.sobhy.weathertracking.domain.model

data class WeatherInfo(
    val temperature: Double,
    val description: String,
    val icon: String,
    val dateTime: String,
)
