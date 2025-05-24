package dev.sobhy.weathertracking

import android.app.Application
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dev.sobhy.weathertracking.data.local.WeatherCacheManager
import dev.sobhy.weathertracking.data.local.WeatherDatabaseHelper
import dev.sobhy.weathertracking.data.location.DefaultLocationTracker
import dev.sobhy.weathertracking.data.remote.WeatherApiService
import dev.sobhy.weathertracking.data.repository.WeatherRepositoryImpl
import dev.sobhy.weathertracking.domain.location.LocationTracker
import dev.sobhy.weathertracking.domain.repository.WeatherRepository

class WeatherApplication: Application() {
    val weatherDatabaseHelper : WeatherDatabaseHelper by lazy {
        WeatherDatabaseHelper(this)
    }
val repository: WeatherRepository by lazy {
    val cacheManager = WeatherCacheManager(weatherDatabaseHelper)
    val apiService = WeatherApiService()
    WeatherRepositoryImpl(apiService, cacheManager)
}

    val locationProviderClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(this)
    }

    val locationTracker: LocationTracker by lazy {
        DefaultLocationTracker(locationProviderClient, this)
    }

    override fun onCreate() {
        super.onCreate()
    }
}