package dev.sobhy.weathertracking.data.remote

import dev.sobhy.weathertracking.domain.model.ForecastDay
import org.json.JSONObject

data class ForecastDto(
    val date: String,
    val maxTemp: Double,
    val minTemp: Double,
    val description: String,
    val icon: String
) {
    companion object {
        fun fromJson(json: JSONObject): ForecastDto{
            return ForecastDto(
                date = json.getString("datetime"),
                maxTemp = json.getDouble("tempmax"),
                minTemp = json.getDouble("tempmin"),
                description = json.getString("conditions"),
                icon = json.getString("icon")
            )
        }

        fun toJson(forecastDto: ForecastDto): JSONObject {
            return JSONObject().apply {
                put("datetime", forecastDto.date)
                put("tempmax", forecastDto.maxTemp)
                put("tempmin", forecastDto.minTemp)
                put("conditions", forecastDto.description)
                put("icon", forecastDto.icon)
            }
        }
    }
}
