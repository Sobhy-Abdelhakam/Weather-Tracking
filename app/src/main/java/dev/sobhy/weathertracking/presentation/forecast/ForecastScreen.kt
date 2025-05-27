package dev.sobhy.weathertracking.presentation.forecast

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.sobhy.weathertracking.domain.weather.ForecastDay
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForecastScreen(
    onNavigateBack: () -> Unit,
    viewModel: ForecastViewModel = viewModel(factory = ForecastViewModel.Factory),
) {
    val state = viewModel.uiState
    LaunchedEffect(Unit) {
        if (!state.isLoading && state.forecast.isEmpty()) {
            viewModel.loadForecast()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Forecast")
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it), contentAlignment = Alignment.Center
        ) {
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
        }

    }
}

@Composable
fun Content(forecast: List<ForecastDay>) {
    ElevatedCard(
        modifier = Modifier.padding(6.dp),
    ) {
        LazyColumn {
            items(forecast) { day ->
                ForecastItem(day)
                HorizontalDivider()
            }
        }
    }
}

@Composable
fun ForecastItem(day: ForecastDay) {
    Row(
        Modifier
            .padding(8.dp)
            .padding(vertical = 4.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = day.date, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Image(
                painter = painterResource(day.icon),
                contentDescription = null,
                modifier = Modifier
                    .padding(8.dp)
                    .size(30.dp)
            )
            Text(text = day.description)
        }
        Text(
            text = "${day.minTemp.roundToInt()}°/${day.maxTemp.roundToInt()}°",
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.End
        )
    }
}