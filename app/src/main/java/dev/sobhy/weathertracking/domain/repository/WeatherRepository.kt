package dev.sobhy.weathertracking.domain.repository

import dev.sobhy.weathertracking.domain.model.ForecastDay
import dev.sobhy.weathertracking.domain.util.Resource
import dev.sobhy.weathertracking.domain.model.WeatherData

interface WeatherRepository {
    suspend fun getTodayWeather(lat: Double?, long: Double?): Resource<WeatherData>

    suspend fun getForecastWeather(lat: Double?, long: Double?): Resource<List<ForecastDay>>
}