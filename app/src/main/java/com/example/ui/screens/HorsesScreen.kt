package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CoatType
import com.example.data.model.Horse
import com.example.ui.MainUiState
import com.example.ui.components.AddEditHorseDialog
import com.example.ui.theme.*

@Composable
fun HorsesScreen(
    uiState: MainUiState,
    onSaveHorse: (Horse) -> Unit,
    onDeleteHorse: (Horse) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }
    var horseToEdit by remember { mutableStateOf<Horse?>(null) }
    var horseToDelete by remember { mutableStateOf<Horse?>(null) }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    horseToEdit = null
                    showDialog = true
                },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Add New Horse") },
                containerColor = ForestGreenPrimary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .padding(bottom = 60.dp)
                    .testTag("add_horse_fab")
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
                    text = "Horse Profiles",
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Customize each horse's coat clipping, overnight turnout shelter, age, and temperature sensitivities.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Explanatory Educational Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            Icons.Outlined.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Equine Thermoregulation",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Body clipped horses lose their natural piloerection (fluffing) thermal defense and require rugs at 15°F warmer temps. Sheltered horses stay significantly warmer than open pasture horses.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            if (uiState.horses.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                    ) {
                        Column(
                            modifier = Modifier.padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.Pets, contentDescription = null, modifier = Modifier.size(48.dp))
                            Spacer(Modifier.height(8.dp))
                            Text("No Horses Registered", style = MaterialTheme.typography.titleMedium)
                            Text("Tap 'Add New Horse' below to set up your first equine companion.", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            } else {
                items(uiState.horses, key = { it.id }) { horse ->
                    HorseProfileCard(
                        horse = horse,
                        onEdit = {
                            horseToEdit = horse
                            showDialog = true
                        },
                        onDelete = {
                            horseToDelete = horse
                        }
                    )
                }
            }
        }
    }

    if (showDialog) {
        AddEditHorseDialog(
            horseToEdit = horseToEdit,
            onDismiss = { showDialog = false },
            onSave = onSaveHorse
        )
    }

    if (horseToDelete != null) {
        AlertDialog(
            onDismissRequest = { horseToDelete = null },
            title = { Text("Delete ${horseToDelete!!.name}?") },
            text = { Text("Are you sure you want to remove this horse profile and its custom blanketing settings?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        val h = horseToDelete ?: return@TextButton
                        onDeleteHorse(h)
                        horseToDelete = null
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { horseToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun HorseProfileCard(
    horse: Horse,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("horse_profile_card_${horse.name}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Pets,
                        contentDescription = null,
                        tint = ForestGreenPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text(
                            text = horse.name,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = horse.breed,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onEdit, modifier = Modifier.size(36.dp).testTag("edit_horse_${horse.name}")) {
                        Icon(Icons.Outlined.Edit, contentDescription = "Edit Horse", modifier = Modifier.size(20.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(36.dp).testTag("delete_horse_${horse.name}")) {
                        Icon(Icons.Outlined.Delete, contentDescription = "Delete Horse", tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f), modifier = Modifier.size(20.dp))
                    }
                }
            }

            // Badges row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (horse.coatType == CoatType.CLIPPED) BlanketMediumOrange.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        text = horse.coatType.label,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = if (horse.coatType == CoatType.CLIPPED) BlanketMediumOrange else MaterialTheme.colorScheme.onSurface
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        text = horse.shelterType.label,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall
                    )
                }

                if (horse.isSenior) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = SaddleTanContainer
                    ) {
                        Text(
                            text = "Senior (+5°F)",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = OnSaddleTanContainer
                        )
                    }
                }
            }

            // Sensitivity adjustment summary
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Net Threshold Adjustment: ${if (horse.totalTempOffset > 0) "+" else ""}${horse.totalTempOffset}°F",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = ForestGreenPrimary
                )

                if (horse.customTempOffsetF != 0) {
                    Text(
                        text = "Offset: ${if (horse.customTempOffsetF > 0) "+" else ""}${horse.customTempOffsetF}°F",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (horse.notes.isNotBlank()) {
                Text(
                    text = "Care notes: ${horse.notes}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
