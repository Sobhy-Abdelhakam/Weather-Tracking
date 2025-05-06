package dev.sobhy.weathertracking.presentation.currentweather

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import dev.sobhy.weathertracking.domain.model.WeatherInfo
import dev.sobhy.weathertracking.presentation.currentweather.WeatherViewModel
import dev.sobhy.weathertracking.presentation.navigation.Screen

@Composable
fun WeatherScreen(
    latitude: Double,
    longitude: Double,
    viewModel: WeatherViewModel = viewModel(factory = WeatherViewModel.Factory),
    navigateToForecastScreen: () -> Unit
) {
    val state = viewModel.state

    LaunchedEffect(Unit) {
        // avoid multiple calls
        if (!state.isLoading && state.weather == null) {
            viewModel.loadWeather(latitude, longitude)
            Log.d("WeatherScreen", "lat: $latitude, long: $longitude")
        }
    }

        when {
            state.isLoading -> CircularProgressIndicator()
            state.error != null -> {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Error: ${state.error}")
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { viewModel.loadWeather(latitude, longitude) }) {
                        Text("Retry")
                    }
                }
            }

            else -> {
                Log.d("WeatherScreen", "weather: ${state.weather}")
                state.weather?.let { weather ->
                    Content(weather = weather, forecastClick = navigateToForecastScreen)
                }
            }
        }
}

@Composable
fun Content(weather: WeatherInfo, forecastClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        Spacer(modifier = Modifier.weight(1f))
        WeatherContent(weather = weather)
        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = forecastClick,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White.copy(0.8f),
                contentColor = Color.Black
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 4.dp
            )
        ) {
            Row(modifier = Modifier.padding(8.dp)) {
                Text(
                    "Forecast Weather",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Icon(Icons.AutoMirrored.Default.ArrowForward, contentDescription = null)
            }

        }
    }
}

@Composable
fun WeatherContent(weather: WeatherInfo) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow( // <- Elevation applied here (outside)
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = Color(0x40888888) // More natural shadow
            )
            .padding(8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Now, ${weather.dateTime}", color = Color.White)
            Text(
                "${weather.temperature.toInt()}°",
                color = Color.White,
                style = MaterialTheme.typography.displayMedium,
                modifier = Modifier.padding(top = 8.dp)
            )
            Text(
                "${weather.minTemp}° / ${weather.maxTemp}°",
                color = Color.White,
                style = MaterialTheme.typography.labelLarge
            )
            Text(
                weather.description,
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 16.dp)
            )
            Text(
                "Wind Speed: ${weather.windSpeed.toInt()} km/h",
                color = Color.White,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Text("Humidity: ${weather.humidity.toInt()}%", color = Color.White)
        }

    }
}
//
//@Preview(showBackground = true, showSystemUi = true)
//@Composable
//fun WeatherScreenPreview() {
//    val sampleWeather = WeatherInfo(
//        temperature = 22.5,
//        minTemp = 18.0,
//        maxTemp = 26.0,
//        description = "Partly Cloudy",
//        icon = "partly-cloudy",
//        dateTime = "2023-05-15T14:00:00",
//        windSpeed = 12.3,
//        humidity = 65.0
//    )
//
//    Content(weather = sampleWeather)
//}

