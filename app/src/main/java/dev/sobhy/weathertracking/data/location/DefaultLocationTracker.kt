package dev.sobhy.weathertracking.data.location

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import dev.sobhy.weathertracking.domain.location.LocationTracker
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class DefaultLocationTracker(
    private val locationClient: FusedLocationProviderClient,
    private val application: Application,
) : LocationTracker {

    @SuppressLint("MissingPermission")
    override suspend fun getCurrentLocation(): Location? {
        if (!checkPermissions() || !checkGpsEnable()) return null

        return suspendCancellableCoroutine { continuation ->
            locationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        Log.e("location", location.toString())
                        continuation.resume(location)
                    } else {
                        locationClient.getCurrentLocation(
                            Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                            CancellationTokenSource().token
                        ).addOnSuccessListener { freshLocation ->
                            Log.e("current location", freshLocation.toString())
                            continuation.resume(freshLocation)
                        }
                            .addOnFailureListener { continuation.resume(null) }
                    }
                }
                .addOnFailureListener { continuation.resume(null) }
        }
    }

    private fun checkGpsEnable(): Boolean {
        val locationManager =
            application.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGpsEnable =
            locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.GPS_PROVIDER
            )
        return isGpsEnable
    }

    private fun checkPermissions(): Boolean {
        val hasAccessFineLocationPermission = ContextCompat.checkSelfPermission(
            application,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val hasAccessCoarseLocationPermission = ContextCompat.checkSelfPermission(
            application,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        return hasAccessFineLocationPermission && hasAccessCoarseLocationPermission
    }
}