package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.BlanketLogEntity
import com.example.data.model.BlanketType
import com.example.data.model.Horse
import com.example.ui.MainUiState
import com.example.ui.components.LogBlanketingDialog
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(
    uiState: MainUiState,
    onDeleteLog: (BlanketLogEntity) -> Unit,
    onAddManualLog: (horse: Horse, blanket: String, comfort: String, notes: String, tempF: Double) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedHorseFilter by remember { mutableStateOf<String?>("ALL") }
    var showAddDialog by remember { mutableStateOf(false) }

    val allLogs = uiState.historyLogs
    val filteredLogs = remember(allLogs, selectedHorseFilter) {
        if (selectedHorseFilter == null || selectedHorseFilter == "ALL") {
            allLogs
        } else {
            allLogs.filter { it.horseName == selectedHorseFilter }
        }
    }

    // Statistics calculations
    val totalLogs = allLogs.size
    val blanketedCount = allLogs.count { !it.actualBlanketUsed.contains("No Blanket", ignoreCase = true) && !it.actualBlanketUsed.contains("Naked", ignoreCase = true) }
    val avgTemp = if (allLogs.isNotEmpty()) allLogs.map { it.eveningLowTempF }.average().toInt() else 35

    val sdf = remember { SimpleDateFormat("EEE, MMM d, yyyy", Locale.US) }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddDialog = true },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Log Past Night") },
                containerColor = ForestGreenPrimary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .padding(bottom = 60.dp)
                    .testTag("add_history_log_fab")
            )
        },
        modifier = modifier.fillMaxSize()
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 100.dp)
        ) {
            item {
                Text(
                    text = "Historical Data Logs",
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Track what blanket was used, evening temperatures, and morning comfort feedback over time.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Summary Stats Cards
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = ForestGreenContainer)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("TOTAL LOGS", style = MaterialTheme.typography.labelSmall, color = OnForestGreenContainer.copy(alpha = 0.7f))
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = "$totalLogs",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = OnForestGreenContainer
                            )
                            Text("recorded nights", style = MaterialTheme.typography.bodySmall, color = OnForestGreenContainer.copy(alpha = 0.8f))
                        }
                    }

                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = SaddleTanContainer)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("BLANKETED", style = MaterialTheme.typography.labelSmall, color = OnSaddleTanContainer.copy(alpha = 0.7f))
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = "$blanketedCount / $totalLogs",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = OnSaddleTanContainer
                            )
                            Text("nights rugged", style = MaterialTheme.typography.bodySmall, color = OnSaddleTanContainer.copy(alpha = 0.8f))
                        }
                    }

                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("AVG LOW", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = "$avgTemp°F",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text("overnight low", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            // Filter Chips by Horse
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    item {
                        FilterChip(
                            selected = selectedHorseFilter == "ALL",
                            onClick = { selectedHorseFilter = "ALL" },
                            label = { Text("All Horses") },
                            modifier = Modifier.testTag("filter_all_horses")
                        )
                    }
                    val horseNames = allLogs.map { it.horseName }.distinct()
                    items(horseNames) { name ->
                        FilterChip(
                            selected = selectedHorseFilter == name,
                            onClick = { selectedHorseFilter = name },
                            label = { Text(name) },
                            modifier = Modifier.testTag("filter_horse_$name")
                        )
                    }
                }
            }

            // Historical Log Entries List
            if (filteredLogs.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                    ) {
                        Column(
                            modifier = Modifier.padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Outlined.History,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(Modifier.height(12.dp))
                            Text(
                                text = "No Blanketing Logs Yet",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "Use the 'Log Tonight' button on the Tonight screen or tap 'Log Past Night' below.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(filteredLogs, key = { it.id }) { log ->
                    BlanketLogCard(
                        log = log,
                        formattedDate = sdf.format(Date(log.timestamp)),
                        onDelete = { onDeleteLog(log) }
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        LogBlanketingDialog(
            horses = uiState.horses,
            selectedHorse = uiState.selectedHorse,
            defaultBlanket = BlanketType.MEDIUM_TURNOUT.displayName,
            eveningLowF = uiState.weather?.eveningLowTempF ?: 32.0,
            onDismiss = { showAddDialog = false },
            onConfirm = { h, b, c, n, t ->
                onAddManualLog(h, b, c, n, t)
            }
        )
    }
}

@Composable
fun BlanketLogCard(
    log: BlanketLogEntity,
    formattedDate: String,
    onDelete: () -> Unit
) {
    val isNoBlanket = log.actualBlanketUsed.contains("No Blanket", ignoreCase = true) || log.actualBlanketUsed.contains("Naked", ignoreCase = true)

    val blanketBadgeColor = when {
        log.actualBlanketUsed.contains("Heavy", ignoreCase = true) -> BlanketHeavyRed
        log.actualBlanketUsed.contains("Medium", ignoreCase = true) -> BlanketMediumOrange
        log.actualBlanketUsed.contains("Light", ignoreCase = true) -> BlanketLightTeal
        log.actualBlanketUsed.contains("Rain Sheet", ignoreCase = true) -> BlanketSheetBlue
        else -> BlanketNoneGreen
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("log_card_${log.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header: Date, Horse, Delete
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = formattedDate,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = log.horseName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Temperature Badge
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = "${log.eveningLowTempF.toInt()}°F",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("delete_log_${log.id}")
                    ) {
                        Icon(
                            Icons.Outlined.Delete,
                            contentDescription = "Delete Log",
                            tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Blanket & Weather row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = blanketBadgeColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = log.actualBlanketUsed,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = blanketBadgeColor
                    )
                }

                Text(
                    text = log.weatherDescription,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Comfort Feedback & Notes
            if (log.comfortFeedback.isNotBlank() || log.notes.isNotBlank()) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        if (log.comfortFeedback.isNotBlank()) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Morning Check: ",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = log.comfortFeedback,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                        if (log.notes.isNotBlank()) {
                            Text(
                                text = "\"${log.notes}\"",
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
