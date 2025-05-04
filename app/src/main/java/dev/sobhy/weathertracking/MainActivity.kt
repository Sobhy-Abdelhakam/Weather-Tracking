package dev.sobhy.weathertracking

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import dev.sobhy.weathertracking.helper.LocationProvider
import dev.sobhy.weathertracking.ui.theme.WeatherTrackingTheme

class MainActivity : ComponentActivity() {
    private lateinit var locationProvider: LocationProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        locationProvider = LocationProvider(
            context = this,
            listener = { lat, long ->
                Toast.makeText(this, "lat: $lat, long: $long", Toast.LENGTH_SHORT).show()
            },
            errorCallback = { message ->
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        )

        if (checkPermission()) {
            requestPermission()
        } else {
            // Permission already granted, proceed with location access
            Toast.makeText(this, "Permission already granted", Toast.LENGTH_SHORT).show()
            locationProvider.getLastKnownLocation()
        }

        setContent {
            WeatherTrackingTheme {

            }
        }
    }
    fun checkPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    }
    fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
            LocationProvider.LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String?>,
        grantResults: IntArray,
        deviceId: Int
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId)
        if(requestCode == LocationProvider.LOCATION_PERMISSION_REQUEST_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Permission granted, proceed with location access
            Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show()
            locationProvider.getLastKnownLocation()
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }
}