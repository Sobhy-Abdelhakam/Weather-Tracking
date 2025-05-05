package dev.sobhy.weathertracking.domain.repository

import dev.sobhy.weathertracking.domain.model.ForecastDay
import dev.sobhy.weathertracking.domain.model.WeatherInfo

interface WeatherRepository {
    fun getCurrentWeather(lat: Double, long: Double, onSuccess: (WeatherInfo) -> Unit, onError: (String) -> Unit)
    fun getForecastWeather(lat: Double, long: Double, onSuccess: (List<ForecastDay>) -> Unit, onError: (String) -> Unit)
}