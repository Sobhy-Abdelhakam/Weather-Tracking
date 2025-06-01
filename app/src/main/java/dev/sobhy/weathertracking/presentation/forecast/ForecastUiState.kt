package dev.sobhy.weathertracking.presentation.forecast

import dev.sobhy.weathertracking.domain.model.ForecastDay

data class ForecastUiState(
    val isLoading: Boolean = false,
    val forecast: List<ForecastDay> = emptyList(),
    val error: String? = null
)
