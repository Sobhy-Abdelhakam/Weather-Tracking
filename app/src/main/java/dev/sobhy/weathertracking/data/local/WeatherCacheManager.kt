package dev.sobhy.weathertracking.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import dev.sobhy.weathertracking.helper.Constant.KEY_CURRENT_WEATHER
import dev.sobhy.weathertracking.helper.Constant.KEY_FORECAST_WEATHER
import dev.sobhy.weathertracking.helper.Constant.PREF_NAME

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
}