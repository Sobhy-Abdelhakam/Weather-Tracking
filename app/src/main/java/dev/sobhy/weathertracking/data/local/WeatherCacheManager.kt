package dev.sobhy.weathertracking.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class WeatherCacheManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun saveCurrentWeatherJson(json: String){
        saveData(KEY_CURRENT_WEATHER, json)
    }
    fun getCachedCurrentWeatherJson(): String? {
        return loadData(KEY_CURRENT_WEATHER)
    }
    fun saveForecastWeatherJson(json: String){
        saveData(KEY_FORECAST_WEATHER, json)
    }
    fun getCachedForecastWeatherJson(): String? {
        return loadData(KEY_FORECAST_WEATHER)
    }

    private fun saveData(key: String, value: String){
        prefs.edit {
            putString(key, value)
            apply()
        }
    }
    private fun loadData(key: String): String? {
        return prefs.getString(key, null)
    }

    companion object{
        private const val PREF_NAME = "weather_cache"
        private const val KEY_CURRENT_WEATHER = "current_weather_json"
        private const val KEY_FORECAST_WEATHER = "forecast_weather_json"
    }
}