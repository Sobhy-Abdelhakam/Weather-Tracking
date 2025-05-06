package dev.sobhy.weathertracking.data.remote

import android.os.Handler
import android.os.Looper
import android.util.Log
import dev.sobhy.weathertracking.BuildConfig
import dev.sobhy.weathertracking.domain.model.ForecastDay
import dev.sobhy.weathertracking.domain.model.WeatherInfo
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executors

object WeatherApiService {
    private const val BASE_URL =
        "https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline"
    private const val API_KEY = BuildConfig.WEATHER_API_KEY

    private val executor = Executors.newSingleThreadExecutor()
    private val handler = Handler(Looper.getMainLooper())

    interface ApiCallback {
        fun onSuccess(response: String)
        fun onFailure(errorMessage: String)
    }

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
//        executor.execute {
//            return try {
//                val url = URL(urlStr)
//                val connection = url.openConnection() as HttpURLConnection
//                connection.apply {
//                    requestMethod = "GET"
//                    connectTimeout = 10000
//                    readTimeout = 10000
//                    doInput = true
//                }
//
//
//                val responseCode = connection.responseCode
//                println("ResponseCode: $responseCode")
//                if (responseCode == HttpURLConnection.HTTP_OK) {
//                    val reader = connection.inputStream.bufferedReader()
//                    val response = StringBuilder()
//                    var line: String?
//                    while (reader.readLine().also { line = it } != null) {
//                        response.append(line)
//                    }
//                    reader.close()
//                    response
////                    handler.post { callback.onSuccess(response.toString()) }
////                    Log.d("WeatherApiService", "Response: ${response.toString()}")
//                } else {
//                    Log.e("WeatherApiService", "Unable to fetch data")
//                    null
//                    // Read error response
////                    val errorStream = connection.errorStream
////                    val errorResponse = if (errorStream != null) {
////                        val reader = BufferedReader(InputStreamReader(errorStream))
////                        reader.use { it.readText() }
////                    } else {
////                        "No error details provided"
////                    }
////                    handler.post {
////                        callback.onFailure("HTTP $responseCode: $errorResponse")
////                    }
//                }
//                connection.disconnect()
//            } catch (e: Exception) {
//                Log.e("WeatherService", "Error fetching weather data ${e.localizedMessage}")
//                null
////                handler.post {
////                    callback.onFailure("Network error: ${e.localizedMessage}")
////                }
//            }
//        }
    }

    fun parseWeatherJson(json: String?): WeatherInfo? {
        return try {
            val obj = JSONObject(json ?: return null)
            val current = obj.getJSONObject("currentConditions")
            val today = obj.getJSONArray("days").getJSONObject(0)

            WeatherInfo(
                temperature = current.getDouble("temp"),
                minTemp = today.getDouble("tempmin"),
                maxTemp = today.getDouble("tempmax"),
                description = current.getString("conditions"),
                icon = current.getString("icon"),
                dateTime = current.getString("datetime"),
                windSpeed = current.getDouble("windspeed"),
                humidity = current.getDouble("humidity")
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