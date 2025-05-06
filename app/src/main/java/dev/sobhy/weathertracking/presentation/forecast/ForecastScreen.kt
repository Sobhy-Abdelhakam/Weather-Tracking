package dev.sobhy.weathertracking.presentation.forecast

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.sobhy.weathertracking.domain.model.ForecastDay

@Composable
fun ForecastScreen(
    latitude: Double,
    longitude: Double,
    viewModel: ForecastViewModel = viewModel(factory = ForecastViewModel.Factory)
) {
    val state = viewModel.uiState
    LaunchedEffect(Unit) {
        if (!state.isLoading && state.forecast.isEmpty()){
            viewModel.loadForecast(latitude, longitude)
        }
    }

    when {
        state.isLoading -> CircularProgressIndicator()
        state.error != null -> {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Error: ${state.error}")
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { viewModel.loadForecast(latitude, longitude) }) {
                    Text("Retry")
                }
            }
        }
        else -> {
            Content(state.forecast)
        }
    }
    Content(state.forecast)
}

@Composable
fun Content(forecast: List<ForecastDay>) {
        LazyColumn {
            items(forecast) { day ->
                ForecastItem(day)
            }
        }
}

@Composable
fun ForecastItem(day: ForecastDay) {
    Card(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth()
    ) {
        Row(
            Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(text = day.date, modifier = Modifier.weight(1f))
            Text(text = "${day.minTemp}° / ${day.maxTemp}°")
        }
    }
}

@Preview
@Composable
private fun ForecastScreenPreview() {
    val forecast = listOf(
        ForecastDay("2023-10-01", 20.0, 25.0, "Sunny", "sunny"),
        ForecastDay("2023-10-02", 18.0, 22.0, "cloud", "sunny"),
        ForecastDay("2023-10-03", 19.0, 23.0, "Sunny", "sunny"),
        ForecastDay("2023-10-04", 17.0, 22.0, "Sunny", "sunny"),
        ForecastDay("2023-10-05", 22.0, 28.0, "Sunny", "sunny"),
    )
    Content(forecast)
}