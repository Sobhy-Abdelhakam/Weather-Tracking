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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.sobhy.weathertracking.domain.weather.ForecastDay

@Composable
fun ForecastScreen(
    viewModel: ForecastViewModel = viewModel(factory = ForecastViewModel.Factory)
) {
    val state = viewModel.uiState
    LaunchedEffect(Unit) {
        if (!state.isLoading && state.forecast.isEmpty()){
            viewModel.loadForecast()
        }
    }

    when {
        state.isLoading -> CircularProgressIndicator()
        state.error != null -> {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Error: ${state.error}")
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { viewModel.loadForecast() }) {
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