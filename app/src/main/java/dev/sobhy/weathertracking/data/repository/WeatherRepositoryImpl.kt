package dev.sobhy.weathertracking.data.repository

import dev.sobhy.weathertracking.data.local.WeatherCacheManager
import dev.sobhy.weathertracking.data.mappers.toForecastDays
import dev.sobhy.weathertracking.data.mappers.toWeatherData
import dev.sobhy.weathertracking.data.remote.WeatherApiService
import dev.sobhy.weathertracking.domain.weather.ForecastDay
import dev.sobhy.weathertracking.domain.repository.WeatherRepository
import dev.sobhy.weathertracking.domain.util.Resource
import dev.sobhy.weathertracking.domain.weather.WeatherData

class WeatherRepositoryImpl(
    private val apiService: WeatherApiService,
    private val cacheManager: WeatherCacheManager
): WeatherRepository {

    override suspend fun getTodayWeather(lat: Double?, long: Double?): Resource<WeatherData> {
        return try {
            when {
                lat != null && long != null -> {
                    val dto = apiService.fetchTodayWeather(lat, long)
                    cacheManager.cacheTodayWeather(dto)
                    Resource.Success(dto.toWeatherData())
                }
                else -> {
                    val dto = cacheManager.getCachedTodayWeather()
                    dto?.let {
                        Resource.Success(it.toWeatherData())
                    } ?: Resource.Error("No cached data available.")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Error(e.localizedMessage ?: "An unknown error occurred.")
        }
    }

    override suspend fun getForecastWeather(
        lat: Double,
        long: Double,
    ): Resource<List<ForecastDay>>{
        return try {
            val freshForecast = apiService.fetchForecastJson(lat, long)
            cacheManager.cacheForecast(freshForecast)

            Resource.Success(freshForecast.toForecastDays())
        } catch (e: Exception) {
            e.printStackTrace()

            // Try fallback to cached data
            val cachedForecast = cacheManager.getCachedForecast()
            if (cachedForecast != null) {
                Resource.Success(cachedForecast.toForecastDays())
            } else {
                Resource.Error(e.localizedMessage ?: "An unknown error occurred.")
            }
        }

    }
}