package dev.sobhy.weathertracking.presentation.currentweather

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import dev.sobhy.weathertracking.WeatherApplication
import dev.sobhy.weathertracking.data.local.SharedPreferencesManager
import dev.sobhy.weathertracking.domain.usecase.GetWeatherUseCase
import dev.sobhy.weathertracking.domain.util.Resource
import dev.sobhy.weathertracking.helper.Constant.LATITUDE
import dev.sobhy.weathertracking.helper.Constant.LOCATION_NAME
import dev.sobhy.weathertracking.helper.Constant.LONGITUDE
import kotlinx.coroutines.launch

class WeatherViewModel(private val weatherUseCase: GetWeatherUseCase) : ViewModel() {
    var state by mutableStateOf(WeatherUiState())
        private set

    var locationName by mutableStateOf("")
        private set
    private var latitude: Double? = null
    private var longitude: Double? = null

    private fun loadStoredCoordinates() {
        latitude = SharedPreferencesManager.getString(LATITUDE, null)?.toDoubleOrNull()
        longitude = SharedPreferencesManager.getString(LONGITUDE, null)?.toDoubleOrNull()
        locationName = SharedPreferencesManager.getString(LOCATION_NAME, "") ?: ""
    }

    init {
        loadWeather()
    }

    fun loadWeather() {
        viewModelScope.launch {
            state = state.copy(isLoading = true, error = null)
            loadStoredCoordinates()
            if (latitude != null && longitude != null) {
                val result = weatherUseCase(latitude, longitude)

                state = state.copy(
                    isLoading = false,
                    weatherData = (result as? Resource.Success)?.data,
                    error = (result as? Resource.Error)?.message
                )
            } else {
                state = state.copy(
                    isLoading = false,
                    weatherData = null,
                    error = "No cached data available."
                )
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = checkNotNull(extras[APPLICATION_KEY]) as WeatherApplication
                return WeatherViewModel(weatherUseCase = application.weatherUseCase) as T
            }
        }
    }
}