package dev.sobhy.weathertracking.data.remote

import org.json.JSONObject

data class HourlyDto(
    val time: String,
    val temperature: Double,
    val description: String,
    val icon: String,
    val pressure: Int,
    val windSpeed: Double,
    val humidity: Double
) {
    companion object {
        fun fromJson(json: JSONObject): HourlyDto {
            return HourlyDto(
                time = json.getString("datetime"),
                temperature = json.getDouble("temp"),
                description = json.getString("conditions"),
                icon = json.getString("icon"),
                pressure = json.getInt("pressure"),
                windSpeed = json.getDouble("windspeed"),
                humidity = json.getDouble("humidity")
            )
        }

        fun toJson(hourlyDto: HourlyDto): JSONObject {
            return JSONObject().apply {
                put("datetime", hourlyDto.time)
                put("temp", hourlyDto.temperature)
                put("conditions", hourlyDto.description)
                put("icon", hourlyDto.icon)
                put("pressure", hourlyDto.pressure)
                put("windspeed", hourlyDto.windSpeed)
                put("humidity", hourlyDto.humidity)
            }
        }
    }
}
