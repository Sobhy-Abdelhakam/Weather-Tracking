package dev.sobhy.weathertracking.data.remote

import android.util.Log
import dev.sobhy.weathertracking.domain.model.ForecastDay
import dev.sobhy.weathertracking.domain.model.WeatherInfo
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

object WeatherApiService {
    private const val BASE_URL =
        "https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline"
    private const val API_KEY = ""

    fun fetchWeatherJson(lat: Double, long: Double): String? {
        val urlStr = "$BASE_URL/$lat,$long/today?unitGroup=metric&key=$API_KEY&include=current"
        return fetchJson(urlStr)
    }

    fun fetchForecastJson(lat: Double, long: Double): String? {
        val urlStr = "$BASE_URL/$lat,$long?unitGroup=metric&key=$API_KEY&include=days"
        return fetchJson(urlStr)
    }

    private fun fetchJson(urlStr: String): String? {
        return try {
            val url = URL(urlStr)
            val connection = (url.openConnection() as HttpURLConnection).apply {
                connectTimeout = 5000
                readTimeout = 5000
            }

            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                connection.inputStream.bufferedReader().readText()
            } else null

        } catch (e: Exception) {
            Log.e("WeatherApiService", "Network error: ${e.localizedMessage}")
            null
        }
    }

    fun parseWeatherJson(json: String?): WeatherInfo? {
        return try {
            val obj = JSONObject(json ?: return null)
            val current = obj.getJSONObject("currentConditions")

            WeatherInfo(
                temperature = current.getDouble("temp"),
                description = current.getString("conditions"),
                icon = current.getString("icon"),
                dateTime = current.getString("datetime"),
            )
        } catch (e: Exception) {
            Log.e("WeatherApiService", "Error parsing JSON: ${e.localizedMessage}")
            null
        }
    }
    fun parseForecastJson(json: String?): List<ForecastDay>? {
        return try {
            val obj = JSONObject(json ?: return null)
            val daysArray = obj.getJSONArray("days")
            val forecastList = mutableListOf<ForecastDay>()

            for (i in 0 until minOf(5, daysArray.length())) {
                val day = daysArray.getJSONObject(i)
                forecastList.add(
                    ForecastDay(
                        date = day.getString("datetime"),
                        maxTemp = day.getDouble("tempmax"),
                        minTemp = day.getDouble("tempmin"),
                        description = day.getString("conditions"),
                        icon = day.getString("icon")
                    )
                )
            }
            forecastList
        } catch (e: Exception) {
            Log.e("WeatherApiService", "Error parsing JSON: ${e.localizedMessage}")
            null
        }
    }
}