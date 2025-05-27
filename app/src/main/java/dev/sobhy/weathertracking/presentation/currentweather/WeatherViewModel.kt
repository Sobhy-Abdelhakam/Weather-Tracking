package dev.sobhy.weathertracking.presentation.currentweather

import android.Manifest
import android.content.Context
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.util.Log
import androidx.activity.result.IntentSenderRequest
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import dev.sobhy.weathertracking.WeatherApplication
import dev.sobhy.weathertracking.domain.location.LocationTracker
import dev.sobhy.weathertracking.domain.repository.WeatherRepository
import dev.sobhy.weathertracking.domain.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.Locale

class WeatherViewModel(
    private val repository: WeatherRepository,
    private val locationTracker: LocationTracker,
) : ViewModel() {
    var state by mutableStateOf(WeatherUiState())
        private set

    var locationName by mutableStateOf("")


    fun loadWeather(context: Context) {
        viewModelScope.launch {
            state = state.copy(isLoading = true, error = null)
            val location = locationTracker.getCurrentLocation()
            location?.let {
                locationName = getAddressText(context, it.latitude, it.longitude)
            }

            when (val result =
                repository.getTodayWeather(location?.latitude, location?.longitude)) {
                is Resource.Success -> {
                    state = state.copy(
                        isLoading = false,
                        error = null,
                        weatherData = result.data
                    )
                }

                is Resource.Error -> {
                    state = state.copy(
                        isLoading = false,
                        weatherData = null,
                        error = result.message
                    )
                }
            }
            Log.e("state", state.toString())
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun getAddressText(
        context: Context,
        lat: Double,
        long: Double,
    ): String = withContext(Dispatchers.IO) {
        try {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                suspendCancellableCoroutine<List<Address>> { cont ->
                    geocoder.getFromLocation(lat, long, 1, object : Geocoder.GeocodeListener {
                        override fun onGeocode(addresses: List<Address?>) {
                            cont.resume(addresses.filterNotNull()) {}
                        }

                        override fun onError(errorMessage: String?) {
                            cont.resume(emptyList()) {}
                        }
                    })
                }
            } else {
                @Suppress("DEPRECATION")
                geocoder.getFromLocation(lat, long, 1) ?: emptyList()
            }

            val address = addresses.firstOrNull()
            listOfNotNull(address?.locality, address?.subAdminArea)
                .filter { it.isNotBlank() }
                .joinToString(", ")
                .ifEmpty { "Unknown Location" }
        } catch (_: Exception) {
            "Unknown Location"
        }
    }

    fun checkLocationSetting(
        context: Context,
        onDisabled: (IntentSenderRequest) -> Unit,
        onEnabled: () -> Unit,
    ) {

        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000)
            .setWaitForAccurateLocation(false)
            .build()

        val settingsRequest = LocationSettingsRequest
            .Builder()
            .addLocationRequest(locationRequest)
            .build()

        val client = LocationServices.getSettingsClient(context)


        client.checkLocationSettings(settingsRequest)
            .addOnSuccessListener { onEnabled() }
            .addOnFailureListener { exception ->
                if (exception is ResolvableApiException) {
                    try {
                        val intentRequest = IntentSenderRequest.Builder(exception.resolution).build()
                        onDisabled(intentRequest)
                    } catch (_: IntentSender.SendIntentException) { }
                }
            }
    }

    fun hasLocationPermissions(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    context, Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = checkNotNull(extras[APPLICATION_KEY]) as WeatherApplication
                val repository = application.repository
                val locationTracker = application.locationTracker
                return WeatherViewModel(repository, locationTracker) as T
            }
        }
    }
}