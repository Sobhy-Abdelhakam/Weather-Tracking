package dev.sobhy.weathertracking

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dev.sobhy.weathertracking.presentation.navigation.WeatherNavGraph
import dev.sobhy.weathertracking.ui.theme.WeatherTrackingTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            WeatherTrackingTheme {
                    WeatherNavGraph()
            }
        }
    }
}