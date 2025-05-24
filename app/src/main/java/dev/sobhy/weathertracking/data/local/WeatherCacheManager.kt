package dev.sobhy.weathertracking.data.local

import android.content.ContentValues
import dev.sobhy.weathertracking.data.remote.ForecastDto
import dev.sobhy.weathertracking.data.remote.ForecastResponseDto
import dev.sobhy.weathertracking.data.remote.HourlyDto
import dev.sobhy.weathertracking.data.remote.WeatherResponseDto
import dev.sobhy.weathertracking.helper.Constant.FORECAST_TABLE
import dev.sobhy.weathertracking.helper.Constant.TODAY_WEATHER_TABLE
import org.json.JSONArray
import org.json.JSONObject

class WeatherCacheManager(private val dbHelper: WeatherDatabaseHelper) {

    fun cacheTodayWeather(weather: WeatherResponseDto) {
        val db = dbHelper.writableDatabase

        val values = ContentValues().apply {
            put("current_hour", HourlyDto.toJson(weather.currentHour).toString())
            put("min_temp", weather.minTemp)
            put("max_temp", weather.maxTemp)
            put("hours_json", JSONArray(weather.hours.map { HourlyDto.toJson(it) }).toString())
        }
        db.delete(TODAY_WEATHER_TABLE, null, null)
        db.insert(TODAY_WEATHER_TABLE, null, values)
    }

    fun getCachedTodayWeather(): WeatherResponseDto? {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TODAY_WEATHER_TABLE LIMIT 1", null)
        var weather: WeatherResponseDto? = null

        cursor.use {
            if (it.moveToFirst()) {
                val currentHourStr = it.getString(it.getColumnIndexOrThrow("current_hour"))
                val hoursStr = it.getString(it.getColumnIndexOrThrow("hours_json"))
                val minTemp = it.getDouble(it.getColumnIndexOrThrow("min_temp"))
                val maxTemp = it.getDouble(it.getColumnIndexOrThrow("max_temp"))

                val todayObj = JSONObject().apply {
                    put("tempmin", minTemp)
                    put("tempmax", maxTemp)
                    put("hours", JSONArray(hoursStr))
                }

                val json = JSONObject().apply {
                    put("currentConditions", JSONObject(currentHourStr))
                    put("days", JSONArray().put(todayObj))
                }

                weather = WeatherResponseDto.fromJson(json)
            }
        }
        return weather
    }

    fun cacheForecast(forecast: ForecastResponseDto) {
        val db = dbHelper.writableDatabase
        val forecastArray = JSONArray(forecast.forecastDays.map { ForecastDto.toJson(it) })

        val values = ContentValues().apply {
            put("forecast_json", forecastArray.toString())
        }
        db.delete(FORECAST_TABLE, null, null)
        db.insert(FORECAST_TABLE, null, values)
    }

    fun getCachedForecast(): ForecastResponseDto? {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $FORECAST_TABLE LIMIT 1", null)
        var forecast: ForecastResponseDto? = null

        cursor.use {
            if (it.moveToFirst()) {
                val forecastStr = it.getString(it.getColumnIndexOrThrow("forecast_json"))
                val forecastArray = JSONArray(forecastStr)

                val forecastList = mutableListOf<ForecastDto>()
                for (i in 0 until forecastArray.length()) {
                    forecastList.add(ForecastDto.fromJson(forecastArray.getJSONObject(i)))
                }
                forecast = ForecastResponseDto(forecastList)
            }
        }
        return forecast
    }
}