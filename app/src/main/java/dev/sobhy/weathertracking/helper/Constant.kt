package dev.sobhy.weathertracking.helper

import dev.sobhy.weathertracking.BuildConfig

object Constant {
    const val BASE_URL = "https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline"
    const val API_KEY = BuildConfig.WEATHER_API_KEY
    const val TODAY_WEATHER_TABLE = "today_weather"
    const val FORECAST_TABLE = "forecast"
}