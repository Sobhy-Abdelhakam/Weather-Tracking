package dev.sobhy.weathertracking.data.mappers

import dev.sobhy.weathertracking.R
import dev.sobhy.weathertracking.data.remote.ForecastDto
import dev.sobhy.weathertracking.data.remote.ForecastResponseDto
import dev.sobhy.weathertracking.data.remote.HourlyDto
import dev.sobhy.weathertracking.data.remote.WeatherResponseDto
import dev.sobhy.weathertracking.domain.model.ForecastDay
import dev.sobhy.weathertracking.domain.weather.HourlyWeather
import dev.sobhy.weathertracking.domain.weather.WeatherData
import java.text.SimpleDateFormat
import java.util.Locale

fun HourlyDto.toHourlyWeather(): HourlyWeather {
    return HourlyWeather(
        time = convertTo12HourFormat(time),
        temperature = temperature,
        description = description,
        icon = convertIconStringToResource(icon),
        pressure = pressure,
        windSpeed = windSpeed,
        humidity = humidity

    )
}

fun WeatherResponseDto.toWeatherData(): WeatherData {
    return WeatherData(
        currentWeatherData = currentHour.toHourlyWeather(),
        minTemp = minTemp,
        maxTemp = maxTemp,
        weatherDuringTheDay = hours.map { it.toHourlyWeather() }
    )
}

fun ForecastResponseDto.toForecastDays(): List<ForecastDay> {
    return forecastDays.map { it.toForecastDay() }
}

fun ForecastDto.toForecastDay(): ForecastDay {
    return ForecastDay(
        date = date,
        maxTemp = maxTemp,
        minTemp = minTemp,
        description = description,
        icon = convertIconStringToResource(icon),

        )
}

fun convertTo12HourFormat(time24: String): String {
    val inputFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    val outputFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
    val date = inputFormat.parse(time24)
    return outputFormat.format(date!!)
}

fun convertIconStringToResource(iconName: String): Int {
    return when (iconName) {
        "clear-day" -> R.drawable.clear_day
        "clear-night" -> R.drawable.clear_night
        "cloudy" -> R.drawable.cloudy
        "fog" -> R.drawable.fog
        "hail" -> R.drawable.hail
        "partly-cloudy-day" -> R.drawable.partly_cloudy_day
        "partly-cloudy-night" -> R.drawable.partly_cloudy_night
        "rain" -> R.drawable.rain
        "rain-snow" -> R.drawable.rain_snow
        "rain-snow-showers-day" -> R.drawable.rain_snow_showers_day
        "rain-snow-showers-night" -> R.drawable.rain_snow_showers_night
        "showers-day" -> R.drawable.showers_day
        "showers-night" -> R.drawable.showers_night
        "sleet" -> R.drawable.sleet
        "snow" -> R.drawable.snow
        "snow-showers-day" -> R.drawable.snow_showers_day
        "snow-showers-night" -> R.drawable.snow_showers_night
        "thunder" -> R.drawable.thunder
        "thunder-rain" -> R.drawable.thunder_rain
        "thunder-showers-day" -> R.drawable.thunder_showers_day
        "thunder-showers-night" -> R.drawable.thunder_showers_night
        "wind" -> R.drawable.wind
        else -> R.drawable.fog
    }
}