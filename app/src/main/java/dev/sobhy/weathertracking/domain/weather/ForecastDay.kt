package dev.sobhy.weathertracking.domain.weather

data class ForecastDay(
    val date: String,
    val maxTemp: Double,
    val minTemp: Double,
    val description: String,
    val icon: Int
)