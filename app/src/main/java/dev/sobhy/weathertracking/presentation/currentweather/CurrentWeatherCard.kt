package dev.sobhy.weathertracking.presentation.currentweather

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import dev.sobhy.weathertracking.R
import dev.sobhy.weathertracking.domain.weather.WeatherData
import kotlin.math.roundToInt

@Composable
fun CurrentWeatherCard(
    weatherData: WeatherData,
    containerColor: Color,
    modifier: Modifier = Modifier,
) {
    val weather = weatherData.currentWeatherData
    ElevatedCard(
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 2.dp
        ),
        colors = CardDefaults.elevatedCardColors(
            containerColor = containerColor,
            contentColor = Color.Black
        ),
        modifier = modifier.padding(16.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Today, ${weather.time}",
                modifier = Modifier.align(Alignment.End)
            )
            Image(
                painter = painterResource(id = weather.icon),
                contentDescription = null,
                modifier = Modifier.size(120.dp)

            )
            Text(
                "${weather.temperature.toInt()}°",
                style = MaterialTheme.typography.displayLarge,
            )
            Text(
                "${weatherData.minTemp.roundToInt()}° / ${weatherData.maxTemp.roundToInt()}°",
                style = MaterialTheme.typography.labelLarge
            )
            Text(
                weather.description,
                style = MaterialTheme.typography.titleMedium,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                WeatherDataDisplay(
                    value = weather.pressure,
                    unit = "hpa",
                    icon = ImageVector.vectorResource(id = R.drawable.ic_pressure),
                )
                WeatherDataDisplay(
                    value = weather.humidity.roundToInt(),
                    unit = "%",
                    icon = ImageVector.vectorResource(id = R.drawable.ic_drop),
                )
                WeatherDataDisplay(
                    value = weather.windSpeed.roundToInt(),
                    unit = "km/h",
                    icon = ImageVector.vectorResource(id = R.drawable.ic_wind),
//                    textStyle = TextStyle(color = Color.White)
                )
            }
        }

    }
}

@Composable
fun WeatherDataDisplay(
    value: Int,
    unit: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = TextStyle(),
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(25.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "$value$unit",
            style = textStyle
        )
    }
}