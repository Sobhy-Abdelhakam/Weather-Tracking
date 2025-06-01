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
import dev.sobhy.weathertracking.data.local.SharedPreferencesManager
import dev.sobhy.weathertracking.domain.usecase.GetForecastUseCase
import dev.sobhy.weathertracking.domain.util.Resource
import dev.sobhy.weathertracking.helper.Constant.LATITUDE
import dev.sobhy.weathertracking.helper.Constant.LONGITUDE
import kotlinx.coroutines.launch

class ForecastViewModel(
    private val forecastUseCase: GetForecastUseCase,
) : ViewModel() {
    var uiState by mutableStateOf(ForecastUiState())
        private set

    var latitude by mutableStateOf<Double?>(null)
    var longitude by mutableStateOf<Double?>(null)
    private fun getPreferencePosition(){
        latitude = SharedPreferencesManager.getString(LATITUDE, null)?.toDoubleOrNull()
        longitude = SharedPreferencesManager.getString(LONGITUDE, null)?.toDoubleOrNull()
    }

    init {
        loadForecast()
    }

    fun loadForecast() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null)
            getPreferencePosition()
            val result = forecastUseCase(latitude, longitude)
            uiState = uiState.copy(
                isLoading = false,
                forecast = (result as? Resource.Success)?.data ?: emptyList(),
                error = (result as? Resource.Error)?.message
            )
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = checkNotNull(extras[APPLICATION_KEY])
                val forecastUseCase = (application as WeatherApplication).forecastUseCase
                return ForecastViewModel(forecastUseCase) as T
            }
        }
    }
}