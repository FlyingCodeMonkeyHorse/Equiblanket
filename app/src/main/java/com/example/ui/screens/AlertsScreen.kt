package com.example.ui.screens

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BlanketAlertConfig
import com.example.notification.BlanketNotificationHelper
import com.example.ui.MainUiState
import com.example.ui.theme.*

@Composable
fun AlertsScreen(
    uiState: MainUiState,
    onUpdateAlertConfig: (BlanketAlertConfig) -> Unit,
    onSendTestAlert: () -> Unit,
    onSetThemeMode: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var hasNotificationPermission by remember {
        mutableStateOf(BlanketNotificationHelper.hasNotificationPermission(context))
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasNotificationPermission = isGranted
    }

    val config = uiState.alertConfig

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 90.dp)
    ) {
        item {
            Text(
                text = "Alerts & Notifications",
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Configure push notifications to ensure you never forget to blanket your horse before cold evenings.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Night Barn Mode / Theme Setting Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("theme_setting_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.DarkMode,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Night Barn Mode & Theme",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Comfortable deep dark theme for late evening checks",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            Triple("DARK", "Dark / Night", Icons.Default.DarkMode),
                            Triple("LIGHT", "Light", Icons.Default.LightMode),
                            Triple("SYSTEM", "Auto / System", Icons.Default.BrightnessAuto)
                        ).forEach { (mode, label, icon) ->
                            val isSelected = uiState.themeMode == mode
                            FilterChip(
                                selected = isSelected,
                                onClick = { onSetThemeMode(mode) },
                                leadingIcon = {
                                    Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp))
                                },
                                label = { Text(label, fontSize = 12.sp) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("theme_chip_$mode")
                            )
                        }
                    }
                }
            }
        }

        // System Permission Card if needed
        if (!hasNotificationPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("notification_permission_banner"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = BlanketMediumOrange.copy(alpha = 0.15f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Outlined.Warning,
                            contentDescription = null,
                            tint = BlanketMediumOrange,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Notification Permission Needed",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = BlanketMediumOrange
                            )
                            Text(
                                text = "Grant permission so EquiBlanket can push evening reminders to your status bar.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Spacer(Modifier.width(8.dp))
                        Button(
                            onClick = {
                                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("grant_notification_permission_button")
                        ) {
                            Text("Enable")
                        }
                    }
                }
            }
        }

        // Live Test Push Alert Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("test_alert_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = ForestGreenContainer)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = ForestGreenPrimary,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                Icons.Outlined.NotificationsActive,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Test Evening Blanket Alert",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = OnForestGreenContainer
                            )
                            Text(
                                text = "Trigger an instant alert for ${uiState.selectedHorse?.name ?: "your horse"}",
                                style = MaterialTheme.typography.bodySmall,
                                color = OnForestGreenContainer.copy(alpha = 0.8f)
                            )
                        }
                    }

                    // Notification Preview Mock
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White,
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, ForestGreenPrimary.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.Pets,
                                        contentDescription = null,
                                        tint = ForestGreenPrimary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(Modifier.width(6.dp))
                                    Text(
                                        text = "EquiBlanket",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = ForestGreenPrimary
                                    )
                                }
                                Text("Now", style = MaterialTheme.typography.bodySmall, fontSize = 10.sp)
                            }
                            Spacer(Modifier.height(6.dp))
                            val horseName = uiState.selectedHorse?.name ?: "Dakota"
                            val blanketName = uiState.recommendation?.blanketType?.shortName ?: "Medium Turnout"
                            val lowTemp = uiState.weather?.eveningLowTempF?.toInt() ?: 29
                            Text(
                                text = "🏇 Blanket Alert: Blanket $horseName Tonight!",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "$blanketName • Overnight Low: $lowTemp°F with weather advisory.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Button(
                        onClick = {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !hasNotificationPermission) {
                                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            } else {
                                onSendTestAlert()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("send_test_alert_button"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary)
                    ) {
                        Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Send Test Push Alert Now")
                    }
                }
            }
        }

        // Daily Reminder Schedule Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("daily_schedule_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Daily Evening Alert",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Push notification automatically each evening before night turnout",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = config.dailyAlertEnabled,
                            onCheckedChange = { isChecked ->
                                onUpdateAlertConfig(config.copy(dailyAlertEnabled = isChecked))
                            },
                            modifier = Modifier.testTag("daily_alert_switch")
                        )
                    }

                    if (config.dailyAlertEnabled) {
                        HorizontalDivider()

                        Text(
                            text = "Alert Time: ${formatHourMinute(config.alertHour, config.alertMinute)}",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary
                        )

                        // Quick Time presets
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(
                                Pair(16, 30) to "4:30 PM",
                                Pair(17, 0) to "5:00 PM",
                                Pair(18, 0) to "6:00 PM",
                                Pair(19, 0) to "7:00 PM"
                            ).forEach { (time, label) ->
                                val isSelected = config.alertHour == time.first && config.alertMinute == time.second
                                FilterChip(
                                    selected = isSelected,
                                    onClick = {
                                        onUpdateAlertConfig(
                                            config.copy(alertHour = time.first, alertMinute = time.second)
                                        )
                                    },
                                    label = { Text(label) },
                                    modifier = Modifier.testTag("alert_time_${time.first}_${time.second}")
                                )
                            }
                        }
                    }
                }
            }
        }

        // Custom Temperature Alert Threshold Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("temperature_alert_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Custom Low Temperature Alert",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Special alert if evening forecast falls below custom threshold",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = config.customLowTempAlertEnabled,
                            onCheckedChange = { isChecked ->
                                onUpdateAlertConfig(config.copy(customLowTempAlertEnabled = isChecked))
                            },
                            modifier = Modifier.testTag("temp_alert_switch")
                        )
                    }

                    if (config.customLowTempAlertEnabled) {
                        HorizontalDivider()

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Alert Threshold Temperature",
                                style = MaterialTheme.typography.labelLarge
                            )
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (config.customLowTempThresholdF <= 32) BlanketHeavyRed.copy(alpha = 0.15f) else MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Text(
                                    text = "${config.customLowTempThresholdF}°F (${((config.customLowTempThresholdF - 32) * 5 / 9)}°C)",
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    fontWeight = FontWeight.Bold,
                                    color = if (config.customLowTempThresholdF <= 32) BlanketHeavyRed else MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        Slider(
                            value = config.customLowTempThresholdF.toFloat(),
                            onValueChange = { newValue ->
                                onUpdateAlertConfig(config.copy(customLowTempThresholdF = newValue.toInt()))
                            },
                            valueRange = 15f..55f,
                            steps = 39,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("temp_alert_slider")
                        )

                        // Quick Presets
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(
                                25 to "Hard Freeze (25°)",
                                32 to "Freezing (32°)",
                                40 to "Chilly (40°)",
                                50 to "Cool (50°)"
                            ).forEach { (presetTemp, label) ->
                                FilterChip(
                                    selected = config.customLowTempThresholdF == presetTemp,
                                    onClick = {
                                        onUpdateAlertConfig(config.copy(customLowTempThresholdF = presetTemp))
                                    },
                                    label = { Text(label, fontSize = 11.sp) },
                                    modifier = Modifier.testTag("preset_temp_$presetTemp")
                                )
                            }
                        }
                    }
                }
            }
        }

        // Additional Weather Alerts (Rain/Snow and Wind)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("weather_conditions_alert_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Weather Condition Triggers",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    // Rain & Snow
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                Icons.Default.WaterDrop,
                                contentDescription = null,
                                tint = BlanketSheetBlue,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(Modifier.width(10.dp))
                            Column {
                                Text("Overnight Rain & Snow Warning", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                                Text("Alert when precipitation will collapse coat insulation", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        Switch(
                            checked = config.rainSnowAlertEnabled,
                            onCheckedChange = {
                                onUpdateAlertConfig(config.copy(rainSnowAlertEnabled = it))
                            },
                            modifier = Modifier.testTag("rain_snow_switch")
                        )
                    }

                    HorizontalDivider()

                    // High Wind
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                Icons.Default.Air,
                                contentDescription = null,
                                tint = BlanketMediumOrange,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(Modifier.width(10.dp))
                            Column {
                                Text("High Wind Warning (>20 mph)", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                                Text("Alert when wind chill drastically drops body comfort", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        Switch(
                            checked = config.highWindAlertEnabled,
                            onCheckedChange = {
                                onUpdateAlertConfig(config.copy(highWindAlertEnabled = it))
                            },
                            modifier = Modifier.testTag("high_wind_switch")
                        )
                    }
                }
            }
        }
    }
}

private fun formatHourMinute(hour: Int, minute: Int): String {
    val amPm = if (hour >= 12) "PM" else "AM"
    val displayHour = when {
        hour == 0 -> 12
        hour > 12 -> hour - 12
        else -> hour
    }
    return String.format("%d:%02d %s", displayHour, minute, amPm)
}
