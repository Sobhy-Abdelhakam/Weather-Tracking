package dev.sobhy.weathertracking.helper

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat

class LocationProvider(
    private val context: Context,
    private val listener: (Double, Double) -> Unit,
    private val errorCallback: (String) -> Unit,
) {
    private val locationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    private val locationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            listener(location.latitude, location.longitude)
            locationManager.removeUpdates(this)
        }

        override fun onProviderDisabled(provider: String) {
            errorCallback("GPS is disabled.")
        }
    }

    fun getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            errorCallback("Location permissions not granted")
            return
        }

        val provider = LocationManager.GPS_PROVIDER
        val lastLocation = locationManager.getLastKnownLocation(provider)

        if (lastLocation != null) {
            listener(lastLocation.latitude, lastLocation.longitude)
        } else {
            requestUpdate()
        }

    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun requestUpdate() {
        try {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                0L,
                0f,
                locationListener,
                null
            )
        } catch (e: Exception) {
            errorCallback("Failed to get location: ${e.message}")
        }
    }

    companion object {
        const val LOCATION_PERMISSION_REQUEST_CODE = 100
    }
}