package dev.sobhy.weathertracking.presentation.currentweather

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.CreationExtras
import dev.sobhy.weathertracking.WeatherApplication
import dev.sobhy.weathertracking.domain.repository.WeatherRepository

class WeatherViewModel(
    private val repository: WeatherRepository,
): ViewModel() {
    var state by mutableStateOf(WeatherUiState())
        private set

    fun loadWeather(lat: Double, long: Double) {
        state = state.copy(isLoading = true, error = null)

        repository.getCurrentWeather(
            lat, long,
            onSuccess = { weather ->
                state = state.copy(
                    isLoading = false,
                    weather = weather
                )
            },
            onError = { error ->
                state = state.copy(
                    isLoading = false,
                    error = error
                )
            }
        )
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                // Get the Application object from extras
                val application = checkNotNull(extras[APPLICATION_KEY])
                val repository = (application as WeatherApplication).repository
                return WeatherViewModel(
                    repository = repository
                ) as T
            }
        }
    }
}