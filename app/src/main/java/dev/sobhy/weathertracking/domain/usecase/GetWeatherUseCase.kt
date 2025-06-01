package dev.sobhy.weathertracking.domain.usecase

import dev.sobhy.weathertracking.domain.repository.WeatherRepository

class GetWeatherUseCase(
    private val weatherRepository: WeatherRepository,
) {
    suspend operator fun invoke(lat: Double?, long: Double?) =
        weatherRepository.getTodayWeather(lat, long)
}