package dev.sobhy.weathertracking.presentation.currentweather

import android.content.Context
import android.content.IntentSender
import android.location.Address
import android.location.Geocoder
import android.util.Log
import androidx.activity.result.IntentSenderRequest
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.Priority
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.tasks.Task
import dev.sobhy.weathertracking.WeatherApplication
import dev.sobhy.weathertracking.domain.location.LocationTracker
import dev.sobhy.weathertracking.domain.repository.WeatherRepository
import dev.sobhy.weathertracking.domain.util.Resource
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class WeatherViewModel(
    private val repository: WeatherRepository,
    private val locationTracker: LocationTracker,
) : ViewModel() {
    var state by mutableStateOf(WeatherUiState())
        private set
    var lat by mutableDoubleStateOf(0.0)
    var long by mutableDoubleStateOf(0.0)


    fun loadWeather() {
        viewModelScope.launch {
            // will change this logic, when location is null (or check Gps status), alert user to enable the Gps
            // and continue to get data from repository to retrieve cached data
            state = state.copy(isLoading = true, error = null)
            val location = locationTracker.getCurrentLocation()
            location?.let { lat = it.latitude; long = it.longitude }

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

    suspend fun getAddressText(
        context: Context,
        lat: Double,
        long: Double,
    ): String = suspendCoroutine { continuation ->
        val geocoder = Geocoder(context, Locale.getDefault())
        try {
            geocoder.getFromLocation(lat, long, 1, object : Geocoder.GeocodeListener {
                override fun onGeocode(addresses: List<Address?>) {
                    val address = addresses.firstOrNull()
                    val locality = address?.locality.orEmpty()
                    val subAdminArea = address?.subAdminArea.orEmpty()

                    val result = if (locality.isNotBlank() || subAdminArea.isNotBlank()) {
                        "$locality, $subAdminArea"
                    } else {
                        "Unknown Location"
                    }

                    continuation.resume(result)
                }

                override fun onError(errorMessage: String?) {
                    Log.e("Geocoder", "Geocoding failed: $errorMessage")
                    continuation.resume("Unknown Location")
                }
            })
        } catch (e: Exception) {
            Log.e("Geocoder", "Failed to get address", e)
            continuation.resume("Unknown Location")
        }
    }

    fun checkLocationSetting(
        context: Context,
        onDisabled: (IntentSenderRequest) -> Unit,
        onEnabled: () -> Unit,
    ) {

        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000)
            .setWaitForAccurateLocation(false)

        val client: SettingsClient = LocationServices.getSettingsClient(context)
        val builder: LocationSettingsRequest.Builder = LocationSettingsRequest
            .Builder()
            .addLocationRequest(locationRequest.build())

        val gpsSettingTask: Task<LocationSettingsResponse> =
            client.checkLocationSettings(builder.build())

        gpsSettingTask.addOnSuccessListener { onEnabled() }
        gpsSettingTask.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                try {
                    val intentSenderRequest = IntentSenderRequest
                        .Builder(exception.resolution)
                        .build()
                    onDisabled(intentSenderRequest)
                } catch (sendEx: IntentSender.SendIntentException) {
                    // ignore here
                }
            }
        }

    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                // Get the Application object from extras
                val application = checkNotNull(extras[APPLICATION_KEY])
                val repository = (application as WeatherApplication).repository
                val locationTracker = application.locationTracker
                return WeatherViewModel(
                    repository = repository,
                    locationTracker = locationTracker
                ) as T
            }
        }
    }
}