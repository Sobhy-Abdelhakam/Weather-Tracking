package dev.sobhy.weathertracking.presentation.currentweather

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.sobhy.weathertracking.domain.weather.HourlyWeather

@Composable
fun TodayWeather(
    weatherDuringTheDay: List<HourlyWeather>,
    forecastClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ElevatedCard(
        colors = CardDefaults.cardColors(
            contentColor = Color.White
        ),
        modifier = modifier.padding(16.dp),
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Today",
                    style = MaterialTheme.typography.headlineMedium,
                    fontSize = 20.sp,
                )
                TextButton(
                    onClick = {
                        forecastClick()
                    }
                ) {
                    Text("Next 5 days")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            LazyRow {
                items(weatherDuringTheDay) { hourlyWeather ->
                    HourlyWeatherDisplay(
                        hourlyWeather = hourlyWeather,
                        modifier = Modifier
                            .height(100.dp)
                            .padding(horizontal = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun HourlyWeatherDisplay(hourlyWeather: HourlyWeather, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(hourlyWeather.time, color = Color.Gray)
        Image(
            painter = painterResource(hourlyWeather.icon),
            contentDescription = null,
            modifier = Modifier.size(40.dp)
        )
        Text("${hourlyWeather.temperature}°", fontWeight = FontWeight.Bold)
    }
}