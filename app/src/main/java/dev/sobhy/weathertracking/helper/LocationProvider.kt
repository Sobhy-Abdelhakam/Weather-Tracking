package dev.sobhy.weathertracking.helper

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.activity.result.IntentSenderRequest
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority

class LocationProvider(private val context: Context) {
    private val fusedClient = LocationServices.getFusedLocationProviderClient(context)
    private val settingsClient = LocationServices.getSettingsClient(context)

    fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun checkLocationSettings(
        onSuccess: (Location) -> Unit,
        onResolutionRequired: (IntentSenderRequest) -> Unit,
        onFallbackToLastLocation: (Location?) -> Unit,
        onFailure: (Exception) -> Unit,
    ) {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000).build()

        val settingsRequest = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
            .setAlwaysShow(true)
            .build()

        settingsClient.checkLocationSettings(settingsRequest)
            .addOnSuccessListener {
                getCurrentLocation(onSuccess, onFailure)
            }
            .addOnFailureListener { exception ->
                if (exception is ResolvableApiException) {
                    val intentSenderRequest =
                        IntentSenderRequest.Builder(exception.resolution).build()
                    onResolutionRequired(intentSenderRequest)
                } else {
                    getLastKnownLocation(
                        onSuccess = onFallbackToLastLocation,
                        onFailure = onFailure
                    )
                }
            }
    }

    @SuppressLint("MissingPermission")
    fun getCurrentLocation(
        onSuccess: (Location) -> Unit,
        onFailure: (Exception) -> Unit,
    ) {
        fusedClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { location ->
                if (location != null) {
                    onSuccess(location)
                } else {
                    onFailure(Exception("Current location is null"))
                }
            }
            .addOnFailureListener(onFailure)

    }

    @SuppressLint("MissingPermission")
    fun getLastKnownLocation(
        onSuccess: (Location?) -> Unit,
        onFailure: (Exception) -> Unit,
    ) {
        fusedClient.lastLocation
            .addOnSuccessListener(onSuccess)
            .addOnFailureListener(onFailure)
    }



}