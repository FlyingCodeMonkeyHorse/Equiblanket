package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.model.BlanketType
import com.example.data.model.Horse

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogBlanketingDialog(
    horses: List<Horse>,
    selectedHorse: Horse?,
    defaultBlanket: String,
    eveningLowF: Double,
    customBlanketOptions: List<String> = emptyList(),
    onDismiss: () -> Unit,
    onConfirm: (horse: Horse, blanket: String, comfort: String, notes: String, tempF: Double) -> Unit
) {
    var activeHorse by remember { mutableStateOf(selectedHorse ?: horses.firstOrNull()) }
    var selectedBlanket by remember { mutableStateOf(defaultBlanket) }
    var comfortRating by remember { mutableStateOf("Warm & Comfortable") }
    var notes by remember { mutableStateOf("") }
    var tempInput by remember { mutableStateOf(eveningLowF.toInt().toString()) }

    val blanketOptions = remember(customBlanketOptions) {
        val base = mutableListOf<String>()
        if (customBlanketOptions.isNotEmpty()) {
            base.addAll(customBlanketOptions)
        }
        val defaults = listOf(
            BlanketType.NO_BLANKET.displayName,
            BlanketType.RAIN_SHEET.displayName,
            BlanketType.LIGHT_TURNOUT.displayName,
            BlanketType.MEDIUM_TURNOUT.displayName,
            BlanketType.HEAVY_TURNOUT.displayName,
            BlanketType.HEAVY_WITH_HOOD.displayName
        )
        defaults.forEach { d ->
            if (!base.contains(d)) base.add(d)
        }
        base
    }

    val comfortOptions = listOf(
        "Warm & Comfortable",
        "Slightly Chilly",
        "Too Warm / Sweating",
        "Clean & Dry",
        "Shivering (Needs Thicker)"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Log Blanketing Entry",
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Horse picker if multiple
                if (horses.size > 1) {
                    Text("Select Horse", style = MaterialTheme.typography.labelMedium)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        horses.forEach { horse ->
                            FilterChip(
                                selected = activeHorse?.id == horse.id,
                                onClick = { activeHorse = horse },
                                label = { Text(horse.name) },
                                modifier = Modifier.testTag("log_horse_chip_${horse.name}")
                            )
                        }
                    }
                } else if (activeHorse != null) {
                    Text(
                        text = "Horse: ${activeHorse!!.name}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                // Evening Temp input
                OutlinedTextField(
                    value = tempInput,
                    onValueChange = { tempInput = it },
                    label = { Text("Overnight Low Temp (°F)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                // Blanket used
                Text("Blanket Put On", style = MaterialTheme.typography.labelLarge)
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    blanketOptions.forEach { option ->
                        FilterChip(
                            selected = selectedBlanket == option,
                            onClick = { selectedBlanket = option },
                            label = { Text(option) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("log_blanket_opt_${option.take(10)}")
                        )
                    }
                }

                // Morning Comfort Check
                Text("Morning Check / Comfort Feedback", style = MaterialTheme.typography.labelLarge)
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    comfortOptions.forEach { opt ->
                        FilterChip(
                            selected = comfortRating == opt,
                            onClick = { comfortRating = opt },
                            label = { Text(opt) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // Notes
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Log Notes (Optional)") },
                    placeholder = { Text("e.g. Swapped to dry fleece before turning out") },
                    maxLines = 2,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("log_notes_input"),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val h = activeHorse ?: return@Button
                    val t = tempInput.toDoubleOrNull() ?: eveningLowF
                    onConfirm(h, selectedBlanket, comfortRating, notes.trim(), t)
                    onDismiss()
                },
                modifier = Modifier.testTag("confirm_log_button")
            ) {
                Text("Save Log Entry")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("cancel_log_button")
            ) {
                Text("Cancel")
            }
        }
    )
}
