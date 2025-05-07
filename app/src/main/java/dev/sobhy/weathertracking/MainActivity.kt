package dev.sobhy.weathertracking

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModelProvider
import dev.sobhy.weathertracking.helper.LocationProvider
import dev.sobhy.weathertracking.helper.NetworkUtils.isNetworkAvailable
import dev.sobhy.weathertracking.presentation.AppScreen
import dev.sobhy.weathertracking.ui.theme.WeatherTrackingTheme

class MainActivity : ComponentActivity() {
    private lateinit var locationProvider: LocationProvider
    private lateinit var viewModel: MainViewModel

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            checkGpsAndFetchLocation()
        } else {
            viewModel.showError("Location permission denied")
        }
    }
    // Launcher to resolve GPS enable request
    private val resolutionLauncher =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                fetchCurrentLocation()
            } else {
                fetchLastLocation()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        locationProvider = LocationProvider(this)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        setContent {
            val state by viewModel.uiState
            WeatherTrackingTheme {
                if (!isNetworkAvailable(this)){
                    Toast.makeText(this, "No Internet connection", Toast.LENGTH_SHORT).show()
                }
                LaunchedEffect(Unit) {
                    viewModel.setLoading()
                    if (locationProvider.hasLocationPermission()) {
                        checkGpsAndFetchLocation()
                    } else {
                        requestLocationPermission()
                    }
                }
                AppScreen(state)
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
        locationProvider.checkAndGetLocation(
            onSuccess = viewModel::updateLocation,
            onResolutionRequired = { resolutionLauncher.launch(it) },
            onFallbackToLastLocation = { location ->
                location?.let {
                    viewModel.updateLocation(it)
                    viewModel.showError("GPS is off, using last known location.")
                } ?: viewModel.showError("GPS off and no last known location.")
            },
            onFailure = { viewModel.showError("Location error: ${it.message}") }
        )
    }
    private fun fetchCurrentLocation() {
        locationProvider.getCurrentLocation(
            onSuccess = viewModel::updateLocation,
            onFailure = { viewModel.showError("Location error: ${it.message}") }
        )
    }
    private fun fetchLastLocation() {
        locationProvider.getLastKnownLocation { location ->
            location?.let {
                viewModel.updateLocation(it)
            } ?: viewModel.showError("No last known location found.")
        }
    }
}