package dev.sobhy.weathertracking.data.repository

import android.os.Handler
import android.os.Looper
import dev.sobhy.weathertracking.data.local.WeatherCacheManager
import dev.sobhy.weathertracking.data.remote.WeatherApiService
import dev.sobhy.weathertracking.domain.model.ForecastDay
import dev.sobhy.weathertracking.domain.model.WeatherInfo
import dev.sobhy.weathertracking.domain.repository.WeatherRepository

class WeatherRepositoryImpl(
    private val cacheManager: WeatherCacheManager
): WeatherRepository {

    override fun getCurrentWeather(
        lat: Double,
        long: Double,
        onSuccess: (WeatherInfo) -> Unit,
        onError: (String) -> Unit
    ) {
        Thread{
            val jsonFromNetwork = WeatherApiService.fetchWeatherJson(lat, long)
            val finalJson = jsonFromNetwork ?: cacheManager.getCachedCurrentWeatherJson()
            jsonFromNetwork?.let { cacheManager.saveCurrentWeatherJson(it) }

            val weatherData =  WeatherApiService.parseWeatherJson(finalJson)
            Handler(Looper.getMainLooper()).post {
                if (weatherData != null) {
                    onSuccess(weatherData)
                } else {
                    onError("Failed to fetch weather data")
                }
            }
        }.start()

    }

    override fun getForecastWeather(
        lat: Double,
        long: Double,
        onSuccess: (List<ForecastDay>) -> Unit,
        onError: (String) -> Unit
    ){
        Thread {
            val jsonFromNetwork = WeatherApiService.fetchForecastJson(lat, long)
            val finalJson = jsonFromNetwork ?: cacheManager.getCachedForecastWeatherJson()
            jsonFromNetwork?.let { cacheManager.saveForecastWeatherJson(it) }

            val forecastData =  WeatherApiService.parseForecastJson(finalJson)

            Handler(Looper.getMainLooper()).post {
                if (forecastData != null) {
                    onSuccess(forecastData)
                } else {
                    onError("failed to fetch forecast data")
                }
            }
        }.start()

    }
}