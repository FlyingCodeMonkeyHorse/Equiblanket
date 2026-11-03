package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import coil.compose.AsyncImage
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.BlanketRecommendation
import com.example.data.model.BlanketType
import com.example.data.model.Horse
import com.example.data.model.WeatherForecast
import com.example.ui.MainUiState
import com.example.ui.components.AddEditHorseDialog
import com.example.ui.components.LocationPickerDialog
import com.example.ui.components.LogBlanketingDialog
import com.example.ui.theme.*

@Composable
fun HomeScreen(
    uiState: MainUiState,
    isDarkTheme: Boolean = false,
    onToggleTheme: () -> Unit = {},
    onSelectHorse: (Horse) -> Unit,
    onRefreshWeather: () -> Unit,
    onOpenLocationPicker: () -> Unit,
    onSaveHorse: (Horse) -> Unit,
    onLogBlanketing: (blanket: String, comfort: String, notes: String) -> Unit,
    onNavigateToAlerts: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddHorseDialog by remember { mutableStateOf(false) }
    var showLogDialog by remember { mutableStateOf(false) }

    val weather = uiState.weather
    val horse = uiState.selectedHorse
    val rec = uiState.recommendation

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 90.dp)
    ) {
        // Location & Barn Header
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("barn_location_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onOpenLocationPicker() }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Place,
                            contentDescription = "Location",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "BARN FORECAST LOCATION",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = uiState.locationName,
                                style = MaterialTheme.typography.titleMedium,
                                maxLines = 1
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onToggleTheme,
                            modifier = Modifier.testTag("toggle_dark_theme_button")
                        ) {
                            Icon(
                                imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                                contentDescription = if (isDarkTheme) "Switch to Daylight Mode" else "Switch to Barn Night Mode",
                                tint = if (isDarkTheme) GoldenOchre else MaterialTheme.colorScheme.primary
                            )
                        }

                        IconButton(
                            onClick = onRefreshWeather,
                            modifier = Modifier.testTag("refresh_weather_button")
                        ) {
                            if (uiState.isLoadingWeather) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                            } else {
                                Icon(Icons.Default.Refresh, contentDescription = "Refresh Forecast")
                            }
                        }
                    }
                }
            }
        }

        // Hero Weather Banner with Equestrian Farm Art
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .testTag("hero_weather_banner")
            ) {
                // Background image loaded asynchronously to ensure smooth instant first-frame render
                AsyncImage(
                    model = R.drawable.img_hero_barn,
                    contentDescription = "Equestrian pasture at dusk",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Dark gradient overlay for text readability
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.25f),
                                    Color.Black.copy(alpha = 0.85f)
                                )
                            )
                        )
                )

                // Weather overlay text
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = ForestGreenPrimary.copy(alpha = 0.85f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.NightsStay,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    text = "TONIGHT'S FORECAST",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        if (weather != null && (weather.isRaining || weather.precipitationProbMax >= 40)) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = BlanketMediumOrange.copy(alpha = 0.9f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.WaterDrop, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text(
                                        text = "${weather.precipitationProbMax}% Rain Chance",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }

                    // Temperature metrics
                    if (weather != null) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Column {
                                Text(
                                    text = "${weather.eveningLowTempF.toInt()}°F",
                                    fontFamily = CinzelFontFamily,
                                    fontSize = 44.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    lineHeight = 44.sp
                                )
                                Text(
                                    text = "Overnight Low • Feels like ${weather.overnightApparentLowF.toInt()}°F",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = weather.weatherDescription,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = GoldenOchre,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "Wind: ${weather.maxWindSpeedMph.toInt()} mph",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            }
                        }
                    } else {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.align(Alignment.CenterHorizontally))
                    }
                }
            }
        }

        // Horse Selector Row
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "YOUR HORSES",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )

                    TextButton(
                        onClick = { showAddHorseDialog = true },
                        modifier = Modifier.testTag("add_horse_header_button")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Add Horse")
                    }
                }

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    items(uiState.horses) { itemHorse ->
                        val isSelected = horse?.id == itemHorse.id
                        FilterChip(
                            selected = isSelected,
                            onClick = { onSelectHorse(itemHorse) },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Pets,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            label = {
                                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                                    Text(
                                        text = itemHorse.name,
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                    Text(
                                        text = "${itemHorse.coatType.label} • ${itemHorse.shelterType.label}",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontSize = 10.sp
                                    )
                                }
                            },
                            modifier = Modifier.testTag("horse_chip_${itemHorse.name}"),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            }
        }

        // Primary Blanket Advisor Card
        item {
            if (rec != null && horse != null) {
                BlanketRecommendationCard(
                    recommendation = rec,
                    horse = horse,
                    onLogClick = { showLogDialog = true },
                    onNavigateToAlerts = onNavigateToAlerts
                )
            } else if (uiState.horses.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No Horses Added Yet",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "Add your horse profile with coat clipping and turnout shelter to get custom evening blanket alerts.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(14.dp))
                        Button(onClick = { showAddHorseDialog = true }) {
                            Text("Add First Horse")
                        }
                    }
                }
            }
        }

        // Hourly Temperature Preview
        if (weather != null && weather.hourlyTemperatures.isNotEmpty()) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "OVERNIGHT TEMPERATURE TRACKER",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(vertical = 4.dp)
                    ) {
                        items(weather.hourlyTemperatures) { item ->
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                ),
                                modifier = Modifier.width(76.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = item.timeLabel,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        text = "${item.tempF.toInt()}°",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(Modifier.height(2.dp))
                                    Text(
                                        text = "${item.precipProb}% rain",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontSize = 10.sp,
                                        color = if (item.precipProb > 30) BlanketMediumOrange else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 5-Day Evening Outlook
        if (weather != null && weather.dailyForecasts.isNotEmpty()) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "5-DAY EVENING FORECAST OUTLOOK",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )

                    weather.dailyForecasts.take(5).forEach { daily ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1.5f)) {
                                    Text(
                                        text = daily.dayOfWeek,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = daily.weatherDescription,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Row(
                                    modifier = Modifier.weight(1f),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (daily.precipProbMax >= 30) {
                                        Icon(
                                            Icons.Default.WaterDrop,
                                            contentDescription = null,
                                            tint = BlanketSheetBlue,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(Modifier.width(2.dp))
                                        Text(
                                            text = "${daily.precipProbMax}%",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = BlanketSheetBlue
                                        )
                                    }
                                }

                                Column(
                                    horizontalAlignment = Alignment.End,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = "Low ${daily.minTempF.toInt()}°F",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = if (daily.minTempF <= 32) BlanketHeavyRed else MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "High ${daily.maxTempF.toInt()}°F",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddHorseDialog) {
        AddEditHorseDialog(
            onDismiss = { showAddHorseDialog = false },
            onSave = onSaveHorse
        )
    }

    if (showLogDialog && horse != null && rec != null && weather != null) {
        val customName = rec.customBlanketName ?: rec.blanketType.displayName
        LogBlanketingDialog(
            horses = uiState.horses,
            selectedHorse = horse,
            defaultBlanket = customName,
            eveningLowF = weather.eveningLowTempF,
            customBlanketOptions = uiState.customBlankets.map { it.name },
            onDismiss = { showLogDialog = false },
            onConfirm = { h, blanket, comfort, notes, _ ->
                onLogBlanketing(blanket, comfort, notes)
            }
        )
    }
}

@Composable
fun BlanketRecommendationCard(
    recommendation: BlanketRecommendation,
    horse: Horse,
    onLogClick: () -> Unit,
    onNavigateToAlerts: () -> Unit
) {
    val needsBlanket = recommendation.needsBlanket

    val badgeColor = when (recommendation.blanketType) {
        BlanketType.HEAVY_WITH_HOOD, BlanketType.HEAVY_TURNOUT -> BlanketHeavyRed
        BlanketType.MEDIUM_TURNOUT -> BlanketMediumOrange
        BlanketType.LIGHT_TURNOUT -> BlanketLightTeal
        BlanketType.RAIN_SHEET -> BlanketSheetBlue
        BlanketType.NO_BLANKET -> BlanketNoneGreen
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("blanket_recommendation_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Status Badge & Alert link
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = badgeColor.copy(alpha = 0.15f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(badgeColor)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = if (needsBlanket) "BLANKET REQUIRED" else "NO BLANKET NEEDED",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = badgeColor
                        )
                    }
                }

                IconButton(
                    onClick = onNavigateToAlerts,
                    modifier = Modifier.size(32.dp).testTag("alert_settings_shortcut")
                ) {
                    Icon(
                        Icons.Outlined.Notifications,
                        contentDescription = "Alert Settings",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Blanket Title
            Column {
                Text(
                    text = recommendation.headline,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = recommendation.blanketType.displayName,
                    style = MaterialTheme.typography.titleMedium,
                    color = badgeColor,
                    fontWeight = FontWeight.Bold
                )

                if (recommendation.customBlanketName != null) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = GoldenOchre.copy(alpha = 0.2f),
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = SaddleTanSecondary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = "Your Custom Rule: ${recommendation.customBlanketName} (${recommendation.customTempRange ?: ""})",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = SaddleTanSecondary
                            )
                        }
                    }
                }
            }

            // Rationale Description
            Text(
                text = recommendation.rationale,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Contributing Factors List
            if (recommendation.factorsList.isNotEmpty()) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "FACTORS FOR ${horse.name.uppercase()}:",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                        recommendation.factorsList.forEach { factor ->
                            Row(verticalAlignment = Alignment.Top) {
                                Text("• ", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                                Text(
                                    text = factor,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }

            HorizontalDivider()

            // Quick 1-tap Log Button
            Button(
                onClick = onLogClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("log_tonight_blanket_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ForestGreenPrimary
                )
            ) {
                Icon(Icons.Outlined.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(
                    text = if (needsBlanket) {
                        "Log Blanket for ${horse.name}"
                    } else {
                        "Log Night for ${horse.name} (Naked)"
                    }
                )
            }
        }
    }
}
