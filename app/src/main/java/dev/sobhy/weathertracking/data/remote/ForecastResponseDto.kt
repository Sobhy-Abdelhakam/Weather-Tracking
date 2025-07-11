package dev.sobhy.weathertracking.data.remote

import org.json.JSONObject

data class ForecastResponseDto(
    val forecastDays: List<ForecastDto>
){
    companion object{
        fun fromJson(json: JSONObject): ForecastResponseDto {
            val daysArray = json.getJSONArray("days")
            val forecastList = mutableListOf<ForecastDto>()

            for (i in 1 until minOf(6, daysArray.length())) {
                val day = daysArray.getJSONObject(i)
                forecastList.add(ForecastDto.fromJson(day))
            }
            return ForecastResponseDto(forecastList)
        }
    }
}
