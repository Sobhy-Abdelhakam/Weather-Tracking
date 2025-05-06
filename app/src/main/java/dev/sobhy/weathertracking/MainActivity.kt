package dev.sobhy.weathertracking

import android.Manifest
import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.sobhy.weathertracking.helper.LocationProvider
import dev.sobhy.weathertracking.presentation.navigation.WeatherNavGraph
import dev.sobhy.weathertracking.ui.theme.WeatherTrackingTheme

class MainActivity : ComponentActivity() {
    private lateinit var locationProvider: LocationProvider
    private var latitude by mutableStateOf<Double?>(null)
    private var longitude by mutableStateOf<Double?>(null)
    private var errorMessage by mutableStateOf<String?>(null)

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            checkGpsAndFetchLocation()
        } else {
            showError("Location permission denied")
        }
    }
    // Launcher to resolve GPS enable request
    private val resolutionLauncher =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                fetchLocation()
            } else {
                errorMessage = "GPS is required to fetch location"
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        locationProvider = LocationProvider(this)

        setContent {
            WeatherTrackingTheme {
                LaunchedEffect(Unit) {
                    if (locationProvider.hasLocationPermission()) {
                        checkGpsAndFetchLocation()
                    } else {
                        requestLocationPermission()
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.Blue.copy(0.6f),
                                    Color.Cyan.copy(0.6f),
                                )
                            )
                        )
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    when {
                        latitude != null && longitude != null -> {
                            WeatherNavGraph(latitude!!, longitude!!)
                        }
                        errorMessage != null -> {
                            Text(
                                text = errorMessage ?: "Error",
                                style = MaterialTheme.typography.headlineLarge
                            )
                        }
                        else -> {
                            CircularProgressIndicator()
                        }
                    }
                }

            }
        }
    }
    private fun requestLocationPermission() {
        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    @SuppressLint("MissingPermission")
    private fun checkGpsAndFetchLocation() {
        locationProvider.checkLocationSettings(
            onSuccess = { updateLocation(it) },
            onResolutionRequired = { resolutionLauncher.launch(it) },
            onFallbackToLastLocation = { location ->
                if (location != null) updateLocation(location)
                else showError("No last known location available")
            },
            onFailure = { showError("Location error: ${it.message}") }
        )
    }
    private fun fetchLocation() {
        locationProvider.getLastKnownLocation(
            onSuccess = { location ->
                if (location != null) updateLocation(location)
                else showError("No last known location found")
            },
            onFailure = { showError("error: ${it.message}") }
        )
    }
    private fun updateLocation(location: Location) {
        latitude = location.latitude
        longitude = location.longitude
        errorMessage = null
    }
    private fun showError(message: String) {
        errorMessage = message
    }
}