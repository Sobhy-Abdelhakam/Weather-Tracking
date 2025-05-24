package dev.sobhy.weathertracking.presentation.forecast

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import dev.sobhy.weathertracking.WeatherApplication
import dev.sobhy.weathertracking.domain.location.LocationTracker
import dev.sobhy.weathertracking.domain.repository.WeatherRepository
import dev.sobhy.weathertracking.domain.util.Resource
import kotlinx.coroutines.launch

class ForecastViewModel(
    private val repository: WeatherRepository,
    private val locationTracker: LocationTracker,
) : ViewModel() {
    var uiState by mutableStateOf(ForecastUiState())
        private set

    init {
        loadForecast()
    }

    fun loadForecast() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null)
            val location = locationTracker.getCurrentLocation()

            when (val result = repository.getForecastWeather(
                lat = location!!.latitude,
                long = location.longitude
            )) {
                is Resource.Success -> {
                    uiState = uiState.copy(
                        isLoading = false,
                        error = null,
                        forecast = result.data ?: emptyList()
                    )
                }

                is Resource.Error -> {
                    uiState = uiState.copy(
                        isLoading = false,
                        error = result.message,
                        forecast = emptyList()
                    )
                }
            }

        }


    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = checkNotNull(extras[APPLICATION_KEY])
                val repository = (application as WeatherApplication).repository
                val locationTracker = application.locationTracker
                return ForecastViewModel(
                    repository = repository,
                    locationTracker = locationTracker
                ) as T
            }
        }
    }
}