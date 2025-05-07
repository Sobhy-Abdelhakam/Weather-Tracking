package dev.sobhy.weathertracking.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.sobhy.weathertracking.MainUiState
import dev.sobhy.weathertracking.presentation.navigation.WeatherNavGraph

@Composable
fun AppScreen(state: MainUiState) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    listOf(Color.Blue.copy(0.6f), Color.Cyan.copy(0.6f))
                )
            )
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        when {
            state.latitude != null && state.longitude != null -> {
                WeatherNavGraph(state.latitude, state.longitude)
            }
            state.errorMessage != null -> {
                AlertDialog(
                    onDismissRequest = {},
                    confirmButton = {},
                    title = { Text("Error") },
                    text = {
                        Text(state.errorMessage, style = MaterialTheme.typography.headlineLarge)
                    }
                )
            }
            state.isLoading -> {
                CircularProgressIndicator()
            }
        }
    }
}
