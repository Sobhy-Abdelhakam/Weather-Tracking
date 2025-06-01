package dev.sobhy.weathertracking

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions
import androidx.activity.result.contract.ActivityResultContracts.StartIntentSenderForResult
import dev.sobhy.weathertracking.helper.LocationManagerHelper
import dev.sobhy.weathertracking.presentation.navigation.WeatherNavGraph
import dev.sobhy.weathertracking.ui.theme.WeatherTrackingTheme

class MainActivity : ComponentActivity() {

    private val locationManagerHelper by lazy { LocationManagerHelper(applicationContext) }

    private val locationPermissionRequest = registerForActivityResult(
        RequestMultiplePermissions(),
        ::onPermissionResult
    )

    private val locationSettingsLauncher = registerForActivityResult(
        StartIntentSenderForResult(),
        ::onSettingsResult
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        requestOrFetchLocation()
        setContent { WeatherTrackingTheme {

            WeatherNavGraph()
        } }
    }

    private fun requestOrFetchLocation() {
        if (locationManagerHelper.hasLocationPermissions()) {
            fetchLocation()
        } else {
            locationPermissionRequest.launch(locationManagerHelper.requiredPermissions)
        }
    }

    private fun onPermissionResult(permissions: Map<String, Boolean>) {
        when {
            locationManagerHelper.permissionsGranted(permissions) -> fetchLocation()
            locationManagerHelper.shouldShowRationale() -> showRationaleDialog()
            else -> locationPermissionRequest.launch(locationManagerHelper.requiredPermissions)
        }
    }

    @SuppressLint("MissingPermission")
    private fun fetchLocation() {
        if (!locationManagerHelper.isLocationEnabled()) {
            locationManagerHelper.checkLocationSettings(
                onDisabled = locationSettingsLauncher::launch,
                onEnabled = { locationManagerHelper.getCurrentLocation(::handleLocation) }
            )
            return
        }
        locationManagerHelper.getCurrentLocation(::handleLocation)
    }


    private fun onSettingsResult(result: ActivityResult) {
        if (result.resultCode == RESULT_OK) {
            locationManagerHelper.getCurrentLocation(::handleLocation)
        }
        else Toast.makeText(this, "Please enable location to use this app.", Toast.LENGTH_SHORT)
            .show()
    }
    private fun handleLocation(location: Location?) {
        location?.let {
            locationManagerHelper.saveLocationToPrefs(it)
            locationManagerHelper.getLocationName(it)
        }
    }

    private fun showRationaleDialog() {
        AlertDialog.Builder(this)
            .setTitle("Location Permission Required")
            .setMessage("This app needs location access to fetch weather data for your area.")
            .setPositiveButton("Allow") { _, _ -> requestOrFetchLocation() }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
