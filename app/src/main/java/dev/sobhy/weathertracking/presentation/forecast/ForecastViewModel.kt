package dev.sobhy.weathertracking.presentation.forecast

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.CreationExtras
import dev.sobhy.weathertracking.WeatherApplication
import dev.sobhy.weathertracking.domain.repository.WeatherRepository

class ForecastViewModel(
    private val repository: WeatherRepository,
): ViewModel() {
    var uiState by mutableStateOf(ForecastUiState())
        private set

    fun loadForecast(lat: Double, long: Double) {
        uiState = uiState.copy(isLoading = true, error = null)

        repository.getForecastWeather(
            lat = lat,
            long = long,
            onSuccess = {
                uiState = ForecastUiState(forecast = it, isLoading = false)
            },
            onError = {
                uiState = ForecastUiState(error = it, isLoading = false)
            }
        )
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = checkNotNull(extras[APPLICATION_KEY])
                val repository = (application as WeatherApplication).repository
                return ForecastViewModel(
                    repository = repository
                ) as T
            }
        }
    }
}