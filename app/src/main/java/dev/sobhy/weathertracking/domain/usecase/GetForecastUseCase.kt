package dev.sobhy.weathertracking.domain.usecase

import dev.sobhy.weathertracking.domain.repository.WeatherRepository

class GetForecastUseCase(
    private val weatherRepository: WeatherRepository,
) {
    suspend operator fun invoke(lat: Double?, long: Double?) =
        weatherRepository.getForecastWeather(lat, long)
}