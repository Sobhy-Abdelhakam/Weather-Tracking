package dev.sobhy.weathertracking

import android.location.Location
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class MainViewModel: ViewModel() {
    private val _uiState = mutableStateOf(MainUiState())
    val uiState: State<MainUiState> = _uiState

    fun updateLocation(location: Location) {
        _uiState.value = _uiState.value.copy(
            latitude = location.latitude,
            longitude = location.longitude,
            errorMessage = null,
            isLoading = false
        )
    }

    fun showError(message: String) {
        _uiState.value = _uiState.value.copy(
            errorMessage = message,
            isLoading = false
        )
    }

    fun setLoading() {
        _uiState.value = _uiState.value.copy(
            isLoading = true,
            errorMessage = null
        )
    }
}