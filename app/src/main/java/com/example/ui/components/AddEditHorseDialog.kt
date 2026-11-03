package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.model.CoatType
import com.example.data.model.Horse
import com.example.data.model.KeeperType
import com.example.data.model.ShelterType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditHorseDialog(
    horseToEdit: Horse? = null,
    onDismiss: () -> Unit,
    onSave: (Horse) -> Unit
) {
    var name by remember { mutableStateOf(horseToEdit?.name ?: "") }
    var breed by remember { mutableStateOf(horseToEdit?.breed ?: "Warmblood") }
    var coatType by remember { mutableStateOf(horseToEdit?.coatType ?: CoatType.UNCLIPPED) }
    var shelterType by remember { mutableStateOf(horseToEdit?.shelterType ?: ShelterType.STALLED) }
    var keeperType by remember { mutableStateOf(horseToEdit?.keeperType ?: KeeperType.NORMAL) }
    var isSenior by remember { mutableStateOf(horseToEdit?.isSenior ?: false) }
    var tempOffset by remember { mutableFloatStateOf((horseToEdit?.customTempOffsetF ?: 0).toFloat()) }
    var notes by remember { mutableStateOf(horseToEdit?.notes ?: "") }
    var nameError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (horseToEdit == null) "Add Horse Profile" else "Edit ${horseToEdit.name}",
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Name
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        if (it.isNotBlank()) nameError = false
                    },
                    label = { Text("Horse Name *") },
                    placeholder = { Text("e.g. Dakota, Sterling, Willow") },
                    isError = nameError,
                    supportingText = if (nameError) { { Text("Name is required") } } else null,
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("horse_name_input"),
                    shape = RoundedCornerShape(12.dp)
                )

                // Breed
                OutlinedTextField(
                    value = breed,
                    onValueChange = { breed = it },
                    label = { Text("Breed / Type") },
                    placeholder = { Text("e.g. Thoroughbred, Quarter Horse, Welsh Pony") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("horse_breed_input"),
                    shape = RoundedCornerShape(12.dp)
                )

                // Coat Type
                Text(
                    text = "Coat Condition",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    CoatType.values().forEach { type ->
                        FilterChip(
                            selected = coatType == type,
                            onClick = { coatType = type },
                            label = { Text(type.label) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("chip_coat_${type.name}")
                        )
                    }
                }
                Text(
                    text = coatType.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Shelter Type
                Text(
                    text = "Overnight Turnout / Shelter",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    ShelterType.values().forEach { shelter ->
                        FilterChip(
                            selected = shelterType == shelter,
                            onClick = { shelterType = shelter },
                            label = { Text(shelter.label) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("chip_shelter_${shelter.name}")
                        )
                    }
                }

                // Body Condition / Senior
                Text(
                    text = "Body Condition & Senior Status",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isSenior,
                        onCheckedChange = { isSenior = it },
                        modifier = Modifier.testTag("senior_checkbox")
                    )
                    Text("Senior horse (> 20 yrs old)", style = MaterialTheme.typography.bodyMedium)
                }

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    KeeperType.values().forEach { keeper ->
                        FilterChip(
                            selected = keeperType == keeper,
                            onClick = { keeperType = keeper },
                            label = { Text(keeper.label) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("chip_keeper_${keeper.name}")
                        )
                    }
                }

                // Custom Sensitivity Offset Slider
                Text(
                    text = "Personal Sensitivity Offset: ${if (tempOffset > 0) "+" else ""}${tempOffset.toInt()}°F",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Adjust if this horse naturally runs warm or cold compared to standard equine charts.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Slider(
                    value = tempOffset,
                    onValueChange = { tempOffset = it },
                    valueRange = -10f..10f,
                    steps = 19,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("temp_offset_slider")
                )

                // Notes
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Care Notes / Blanket Size") },
                    placeholder = { Text("e.g. Size 78 turnout, wears shoulder guard") },
                    maxLines = 3,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("horse_notes_input"),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isBlank()) {
                        nameError = true
                        return@Button
                    }
                    val updated = Horse(
                        id = horseToEdit?.id ?: 0L,
                        name = name.trim(),
                        breed = breed.trim().ifBlank { "Horse" },
                        coatType = coatType,
                        shelterType = shelterType,
                        keeperType = keeperType,
                        isSenior = isSenior,
                        customTempOffsetF = tempOffset.toInt(),
                        notes = notes.trim()
                    )
                    onSave(updated)
                    onDismiss()
                },
                modifier = Modifier.testTag("save_horse_button")
            ) {
                Text("Save Horse")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("cancel_horse_button")
            ) {
                Text("Cancel")
            }
        }
    )
}
