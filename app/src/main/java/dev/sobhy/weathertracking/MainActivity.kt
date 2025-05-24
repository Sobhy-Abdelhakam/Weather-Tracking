package dev.sobhy.weathertracking

import android.Manifest
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import dev.sobhy.weathertracking.presentation.navigation.WeatherNavGraph
import dev.sobhy.weathertracking.ui.theme.WeatherTrackingTheme

class MainActivity : ComponentActivity() {
//    private lateinit var locationProvider: LocationProvider
//    private lateinit var viewModel: MainViewModel

//    private val locationPermissionRequest = registerForActivityResult(
//        ActivityResultContracts.RequestMultiplePermissions()
//    ) { permissions ->
////        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
////                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
////        if (granted) {
////            checkGpsAndFetchLocation()
////        } else {
////            viewModel.showError("Location permission denied")
////        }
//    }

    // Launcher to resolve GPS enable request
//    private val resolutionLauncher =
//        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
//            if (result.resultCode == RESULT_OK) {
//                fetchCurrentLocation()
//            } else {
//                fetchLastLocation()
//            }
//        }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
//        locationProvider = LocationProvider(this)
//        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

//        requestLocationPermission()
        setContent {
//            val state by viewModel.uiState
            WeatherTrackingTheme {
//                if (!isNetworkAvailable(this)) {
//                    showToast("No Internet connection")
//                }
//                LaunchedEffect(Unit) {
//                    viewModel.setLoading()
//                    if (locationProvider.hasLocationPermission()) {
//                        checkGpsAndFetchLocation()
//                    } else {
//                        requestLocationPermission()
//                    }
//                }
//                Box(
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .background(
//                            brush = Brush.verticalGradient(
//                                listOf(Color.Blue.copy(0.6f), Color.Cyan.copy(0.6f))
//                            )
//                        )
//                        .padding(16.dp),
//                    contentAlignment = Alignment.Center
//                ) {
                    WeatherNavGraph()
//                }
            }
        }
    }

//    private fun requestLocationPermission() {
//        locationPermissionRequest.launch(
//            arrayOf(
//                Manifest.permission.ACCESS_FINE_LOCATION,
//                Manifest.permission.ACCESS_COARSE_LOCATION
//            )
//        )
//    }

//    @SuppressLint("MissingPermission")
//    private fun checkGpsAndFetchLocation() {
//        locationProvider.checkAndGetLocation(
//            onSuccess = viewModel::updateLocation,
//            onResolutionRequired = { resolutionLauncher.launch(it) },
//            onFallbackToLastLocation = { location ->
//                location?.let {
//                    viewModel.updateLocation(it)
//                    showToast("GPS is off, using last known location.")
//                } ?: viewModel.showError("GPS off and no last known location.")
//            },
//            onFailure = { viewModel.showError("Location error: ${it.message}") }
//        )
//    }
//
//    private fun fetchCurrentLocation() {
//        locationProvider.getCurrentLocation(
//            onSuccess = viewModel::updateLocation,
//            onFailure = { viewModel.showError("Location error: ${it.message}") }
//        )
//    }
//
//    private fun fetchLastLocation() {
//        locationProvider.getLastKnownLocation { location ->
//            location?.let {
//                viewModel.updateLocation(it)
//            } ?: viewModel.showError("No last known location found.")
//        }
//    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}