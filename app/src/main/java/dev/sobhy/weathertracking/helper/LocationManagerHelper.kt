package dev.sobhy.weathertracking.helper

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import dev.sobhy.weathertracking.data.local.SharedPreferencesManager
import dev.sobhy.weathertracking.helper.Constant.LATITUDE
import dev.sobhy.weathertracking.helper.Constant.LOCATION_NAME
import dev.sobhy.weathertracking.helper.Constant.LONGITUDE
import java.util.Locale

class LocationManagerHelper(private val context: Context) {
    private val fusedClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }
    val requiredPermissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    fun hasLocationPermissions(): Boolean = requiredPermissions.any {
        ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }

    fun permissionsGranted(map: Map<String, Boolean>) = map.any { it.value }
    fun shouldShowRationale(): Boolean = requiredPermissions.any {
        ActivityCompat.shouldShowRequestPermissionRationale(context as Activity, it)
    }

    fun isLocationEnabled(): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    fun checkLocationSettings(
        onDisabled: (IntentSenderRequest) -> Unit,
        onEnabled: () -> Unit,
    ) {
        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000).build()
        val settingsRequest = LocationSettingsRequest.Builder().addLocationRequest(request).build()
        val client = LocationServices.getSettingsClient(context)

        client.checkLocationSettings(settingsRequest)
            .addOnSuccessListener { onEnabled() }
            .addOnFailureListener {
                if (it is ResolvableApiException) {
                    try {
                        val intentRequest = IntentSenderRequest.Builder(it.resolution).build()
                        onDisabled(intentRequest)
                    } catch (_: IntentSender.SendIntentException) {
                        Toast.makeText(context, "Unable to resolve location settings", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

    @SuppressLint("MissingPermission")
    fun getCurrentLocation(callback: (Location?) -> Unit) {
        fusedClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) callback(location)
                else requestNewLocation(callback)
            }
            .addOnFailureListener { callback(null) }
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocation(callback: (Location?) -> Unit) {
        fusedClient.getCurrentLocation(
            Priority.PRIORITY_BALANCED_POWER_ACCURACY,
            CancellationTokenSource().token
        ).addOnSuccessListener(callback)
            .addOnFailureListener { callback(null) }
    }

    fun saveLocationToPrefs(location: Location) {
        SharedPreferencesManager.saveString(LATITUDE, location.latitude.toString())
        SharedPreferencesManager.saveString(LONGITUDE, location.longitude.toString())
    }

    fun getLocationName(location: Location) {
        val geocoder = Geocoder(context, Locale.getDefault())
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                geocoder.getFromLocation(location.latitude, location.longitude, 1, object :
                    Geocoder.GeocodeListener {
                    override fun onGeocode(addresses: List<Address?>) {
                        saveAddress(addresses.firstOrNull())
                    }
                })
            } else {
                @Suppress("DEPRECATION")
                saveAddress(
                    geocoder.getFromLocation(location.latitude, location.longitude, 1)
                        ?.firstOrNull()
                )
            }
        } catch (_: Exception) {
        }
    }

    private fun saveAddress(address: Address?) {
        val name = listOfNotNull(address?.locality, address?.subAdminArea)
            .filter { it.isNotBlank() }
            .joinToString(", ")
            .ifEmpty { "Unknown Location" }
        SharedPreferencesManager.saveString(LOCATION_NAME, name)
    }
}