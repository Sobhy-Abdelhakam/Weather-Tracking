package dev.sobhy.weathertracking

data class MainUiState(
    val latitude: Double? = null,
    val longitude: Double? = null,
    val errorMessage: String? = null,
    val isLoading: Boolean = true
)
