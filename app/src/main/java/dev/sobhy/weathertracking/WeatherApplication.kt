package dev.sobhy.weathertracking

import android.app.Application
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dev.sobhy.weathertracking.data.local.SharedPreferencesManager
import dev.sobhy.weathertracking.data.local.WeatherCacheManager
import dev.sobhy.weathertracking.data.local.WeatherDatabaseHelper
import dev.sobhy.weathertracking.data.remote.WeatherApiService
import dev.sobhy.weathertracking.data.repository.WeatherRepositoryImpl
import dev.sobhy.weathertracking.domain.repository.WeatherRepository
import dev.sobhy.weathertracking.domain.usecase.GetForecastUseCase
import dev.sobhy.weathertracking.domain.usecase.GetWeatherUseCase

class WeatherApplication : Application() {
    val weatherDatabaseHelper: WeatherDatabaseHelper by lazy {
        WeatherDatabaseHelper(this)
    }
    val repository: WeatherRepository by lazy {
        val cacheManager = WeatherCacheManager(weatherDatabaseHelper)
        val apiService = WeatherApiService()
        WeatherRepositoryImpl(apiService, cacheManager)
    }
    val weatherUseCase: GetWeatherUseCase by lazy {
        GetWeatherUseCase(repository)
    }
    val forecastUseCase: GetForecastUseCase by lazy {
        GetForecastUseCase(repository)
    }

    override fun onCreate() {
        super.onCreate()
        SharedPreferencesManager.init(this)
    }
}