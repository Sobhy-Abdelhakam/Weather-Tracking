package dev.sobhy.weathertracking.data.remote

import dev.sobhy.weathertracking.helper.Constant.API_KEY
import dev.sobhy.weathertracking.helper.Constant.BASE_URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class WeatherApiService {

    suspend fun fetchTodayWeather(lat: Double, long: Double): WeatherResponseDto = withContext(Dispatchers.IO) {
        val url = "$BASE_URL/$lat,$long/today?unitGroup=metric&key=$API_KEY"
        val response = fetchJson(url)
        val jsonObject = JSONObject(response)
        WeatherResponseDto.fromJson(jsonObject)
    }

    suspend fun fetchForecastJson(lat: Double, long: Double): ForecastResponseDto = withContext(Dispatchers.IO) {
        val urlStr = "$BASE_URL/$lat,$long?unitGroup=metric&key=$API_KEY&include=days"
        val response = fetchJson(urlStr)
        val jsonObject = JSONObject(response)
        ForecastResponseDto.fromJson(jsonObject)
    }

    private fun fetchJson(urlStr: String): String {
        val url = URL(urlStr)
        val connection = (url.openConnection() as HttpURLConnection).apply {
            connectTimeout = 5000
            readTimeout = 5000
        }

        val reader = BufferedReader(InputStreamReader(connection.inputStream))
        val response = StringBuilder()
        reader.useLines { lines -> lines.forEach { response.append(it) } }

        connection.disconnect()
        return response.toString()
    }
}