package dev.sobhy.weathertracking.helper

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import androidx.activity.result.IntentSenderRequest
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import dev.sobhy.weathertracking.helper.Constant.KEY_LATITUDE
import dev.sobhy.weathertracking.helper.Constant.KEY_LONGITUDE
import dev.sobhy.weathertracking.helper.Constant.PREF_NAME

class LocationProvider(private val context: Context) {
    private val fusedClient = LocationServices.getFusedLocationProviderClient(context)
    private val settingsClient = LocationServices.getSettingsClient(context)

    private val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun checkAndGetLocation(
        onSuccess: (Location) -> Unit,
        onResolutionRequired: (IntentSenderRequest) -> Unit,
        onFallbackToLastLocation: (Location?) -> Unit,
        onFailure: (Exception) -> Unit,
    ) {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_BALANCED_POWER_ACCURACY, 1000).build()

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
//                        onFailure = onFailure
                    )
                }
            }
    }

    @SuppressLint("MissingPermission")
    fun getCurrentLocation(
        onSuccess: (Location) -> Unit,
        onFailure: (Exception) -> Unit,
    ) {
        fusedClient.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, null)
            .addOnSuccessListener { location ->
                if (location != null) {
                    onSuccess(location)
                    prefs.edit {
                        putString(KEY_LATITUDE, location.latitude.toString())
                        putString(KEY_LONGITUDE, location.longitude.toString())
                        apply()
                    }
                } else {
                    onFailure(Exception("Can't get current location"))
                }
            }
            .addOnFailureListener(onFailure)

    }

    /**
     * ohh, based on the developer.android documentation the location object may be null
     * if the location is turned off in the device settings.
     * The result could be null even if the last location was previously
     * retrieved because disabling location also clears the cache.
     * **/
    @SuppressLint("MissingPermission")
    fun getLastKnownLocation(
        onSuccess: (Location?) -> Unit,
//        onFailure: (Exception) -> Unit,
    ) {
//        fusedClient.lastLocation
//            .addOnSuccessListener(onSuccess)
//            .addOnFailureListener(onFailure)

        /**
         * I have a problem with lastKnownLocation, which is that it always returns a null value,
         * so I resorted to an alternative solution,
         * which is to save the location locally when location is active
         * , and retrieve it from the locale in case the location is not activated
        * */

        val latitude = prefs.getString(KEY_LATITUDE, null)?.toDoubleOrNull()
        val longitude = prefs.getString(KEY_LONGITUDE, null)?.toDoubleOrNull()
        if (latitude != null && longitude != null) {
            val location = Location("last_known")
            location.latitude = latitude
            location.longitude = longitude
            onSuccess(location)
        } else {
            onSuccess(null)
        }
    }



}