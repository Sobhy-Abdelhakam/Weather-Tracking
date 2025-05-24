package dev.sobhy.weathertracking.presentation.currentweather

import android.Manifest
import android.app.Activity.RESULT_OK
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.sobhy.weathertracking.domain.weather.WeatherData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(
    navigateToForecastScreen: () -> Unit,
    viewModel: WeatherViewModel = viewModel(factory = WeatherViewModel.Factory),
) {
    val context = LocalContext.current
    val state = viewModel.state
    val refreshing = state.isLoading
    val pullToRefreshState = rememberPullToRefreshState()

    val settingResultRequest = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { activityResult ->
        if (activityResult.resultCode == RESULT_OK) {
            Log.d("appDebug", "Accepted")
        } else {
            Log.d("appDebug", "Denied")
        }
        viewModel.loadWeather()
    }

    val locationPermissionRequest = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            viewModel.checkLocationSetting(
                context,
                onDisabled = { settingResultRequest.launch(it) },
                onEnabled = { viewModel.loadWeather() }
            )
        } else {
//            viewModel.showError("Location permission denied")
        }
    }

    LaunchedEffect(Unit) {
        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    val locationName by produceState(initialValue = "") {
        value = viewModel.getAddressText(context, viewModel.lat, viewModel.long)
    }

    Scaffold {
        PullToRefreshBox(
            isRefreshing = refreshing,
            state = pullToRefreshState,
            onRefresh = { viewModel.loadWeather() },
            indicator = {
                Indicator(
                    modifier = Modifier.align(Alignment.TopCenter),
                    isRefreshing = refreshing,
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    state = pullToRefreshState
                )
            },
            modifier = Modifier.padding(it)
        ) {
            when {
                refreshing -> Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }

                state.error != null -> Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Error: ${state.error}")
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { viewModel.loadWeather() }) {
                            Text("Retry")
                        }
                    }
                }

                else -> {
                    state.weatherData?.let { weather ->
                        Column {
                            Content(
                                weather = weather,
                                forecastClick = navigateToForecastScreen,
                                locationName
                            )
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun LoadingUI() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}
@Composable
fun ErrorUI(errorMessage: String, onRetry: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Error: $errorMessage", color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onRetry) {
                Text("Retry")
            }
        }
    }
}

@Composable
fun Content(weather: WeatherData, forecastClick: () -> Unit, locationName: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        LocationHeader(locationName, modifier = Modifier.align(Alignment.CenterHorizontally))
//        Spacer(modifier = Modifier.height(16.dp))
        CurrentWeatherCard(
            weatherData = weather,
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.fillMaxWidth()
        )
//        Spacer(modifier = Modifier.height(16.dp))
        TodayWeather(weather.weatherDuringTheDay, forecastClick)
    }
}
@Composable
fun LocationHeader(locationName: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = locationName,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Icon(imageVector = Icons.Default.LocationOn, contentDescription = null)
    }
}