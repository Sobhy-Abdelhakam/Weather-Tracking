package dev.sobhy.weathertracking.data.repository

import dev.sobhy.weathertracking.data.local.WeatherCacheManager
import dev.sobhy.weathertracking.data.mappers.toForecastDays
import dev.sobhy.weathertracking.data.mappers.toWeatherData
import dev.sobhy.weathertracking.data.remote.WeatherApiService
import dev.sobhy.weathertracking.domain.model.ForecastDay
import dev.sobhy.weathertracking.domain.model.WeatherData
import dev.sobhy.weathertracking.domain.repository.WeatherRepository
import dev.sobhy.weathertracking.domain.util.Resource

class WeatherRepositoryImpl(
    private val apiService: WeatherApiService,
    private val cacheManager: WeatherCacheManager,
) : WeatherRepository {

    override suspend fun getTodayWeather(lat: Double?, long: Double?): Resource<WeatherData> {
        return try {
            val data = apiService.fetchTodayWeather(lat!!, long!!)
            cacheManager.cacheTodayWeather(data)
            Resource.Success(data.toWeatherData())
        } catch (e: Exception) {
            e.printStackTrace()
            val cached = cacheManager.getCachedTodayWeather()
            cached?.let {
                Resource.Success(it.toWeatherData())
            } ?: Resource.Error(e.localizedMessage ?: "An unknown error occurred.")
        }
    }

    override suspend fun getForecastWeather(
        lat: Double?,
        long: Double?,
    ): Resource<List<ForecastDay>> {
        return try {
            val freshForecast = apiService.fetchForecastJson(lat!!, long!!)
            cacheManager.cacheForecast(freshForecast)

            Resource.Success(freshForecast.toForecastDays())

        } catch (e: Exception) {
            e.printStackTrace()
            val cachedForecast = cacheManager.getCachedForecast()
            cachedForecast?.let {
                Resource.Success(cachedForecast.toForecastDays())
            } ?: Resource.Error(e.localizedMessage ?: "An unknown error occurred.")
        }

    }
}