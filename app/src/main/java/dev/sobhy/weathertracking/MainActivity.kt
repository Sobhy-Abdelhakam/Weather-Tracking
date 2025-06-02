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
import androidx.compose.runtime.mutableStateOf
import dev.sobhy.weathertracking.data.local.SharedPreferencesManager
import dev.sobhy.weathertracking.helper.Constant.LATITUDE
import dev.sobhy.weathertracking.helper.Constant.LONGITUDE
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

    private val locationReady = mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        locationReady.value = hasStoredLocation()

        requestOrFetchLocation()
        setContent { WeatherTrackingTheme {
            if (locationReady.value) {
                WeatherNavGraph()
            }
        } }
    }
    private fun hasStoredLocation(): Boolean {
        val lat = SharedPreferencesManager.getString(LATITUDE, null)
        val lon = SharedPreferencesManager.getString(LONGITUDE, null)
        return lat?.toDoubleOrNull() != null && lon?.toDoubleOrNull() != null
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
            locationReady.value = true
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
