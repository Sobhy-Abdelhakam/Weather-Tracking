package dev.sobhy.weathertracking

import android.app.Application
import dev.sobhy.weathertracking.data.local.WeatherCacheManager
import dev.sobhy.weathertracking.data.repository.WeatherRepositoryImpl
import dev.sobhy.weathertracking.domain.repository.WeatherRepository

class WeatherApplication: Application() {
    lateinit var repository: WeatherRepository
        private set

    override fun onCreate() {
        super.onCreate()
        val cacheManager = WeatherCacheManager(this)
        repository = WeatherRepositoryImpl(cacheManager)
    }
}