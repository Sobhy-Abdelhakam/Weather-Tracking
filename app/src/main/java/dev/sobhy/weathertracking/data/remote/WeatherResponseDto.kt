package dev.sobhy.weathertracking.data.remote

import org.json.JSONArray
import org.json.JSONObject

data class WeatherResponseDto(
    val currentHour: HourlyDto,
    val minTemp: Double,
    val maxTemp: Double,
    val hours: List<HourlyDto>,
) {
    companion object {
        fun fromJson(json: JSONObject): WeatherResponseDto {
            val current = json.getJSONObject("currentConditions")
            val todayObj = json.getJSONArray("days").getJSONObject(0)
            val hoursArray = todayObj.getJSONArray("hours")

            val hourlyList = mutableListOf<HourlyDto>()
            for (i in 0 until hoursArray.length()) {
                val hour = hoursArray.getJSONObject(i)
                hourlyList.add(HourlyDto.fromJson(hour))
            }
            return WeatherResponseDto(
                currentHour = HourlyDto.fromJson(current),
                minTemp = todayObj.getDouble("tempmin"),
                maxTemp = todayObj.getDouble("tempmax"),
                hours = hourlyList
            )
        }

        fun toJson(weather: WeatherResponseDto): JSONObject {
            return JSONObject().apply {
                put("currentConditions", HourlyDto.toJson(weather.currentHour).toString())
                put("tempmin", weather.minTemp)
                put("tempmax", weather.maxTemp)
                put("hours", JSONArray(weather.hours.map { HourlyDto.toJson(it) }).toString())
            }
        }
    }
}
