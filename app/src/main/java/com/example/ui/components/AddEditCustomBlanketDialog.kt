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
import com.example.data.local.CustomBlanketEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditCustomBlanketDialog(
    blanketToEdit: CustomBlanketEntity? = null,
    onDismiss: () -> Unit,
    onSave: (CustomBlanketEntity) -> Unit
) {
    var name by remember { mutableStateOf(blanketToEdit?.name ?: "") }
    var category by remember { mutableStateOf(blanketToEdit?.category ?: "MEDIUM") }
    var fillGrams by remember { mutableStateOf((blanketToEdit?.fillGrams ?: 200).toString()) }
    var minTemp by remember { mutableStateOf((blanketToEdit?.minTempF ?: 30).toString()) }
    var maxTemp by remember { mutableStateOf((blanketToEdit?.maxTempF ?: 42).toString()) }
    var isWaterproof by remember { mutableStateOf(blanketToEdit?.isWaterproof ?: true) }
    var hasNeckCover by remember { mutableStateOf(blanketToEdit?.hasNeckCover ?: false) }
    var notes by remember { mutableStateOf(blanketToEdit?.notes ?: "") }
    var errorText by remember { mutableStateOf<String?>(null) }

    val categories = listOf("NONE", "SHEET", "LIGHT", "MEDIUM", "HEAVY")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (blanketToEdit == null) "Define Custom Blanket" else "Edit ${blanketToEdit.name}",
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
                // Name
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        errorText = null
                    },
                    label = { Text("Blanket Name *") },
                    placeholder = { Text("e.g. Amigo Medium Turnout, Rain Sheet") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("custom_blanket_name_input"),
                    shape = RoundedCornerShape(12.dp)
                )

                // Category chips
                Text("Category Type", style = MaterialTheme.typography.labelMedium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    categories.forEach { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = {
                                category = cat
                                when (cat) {
                                    "NONE" -> {
                                        fillGrams = "0"
                                        minTemp = "60"
                                        maxTemp = "105"
                                        isWaterproof = false
                                        hasNeckCover = false
                                    }
                                    "SHEET" -> {
                                        fillGrams = "0"
                                        minTemp = "50"
                                        maxTemp = "59"
                                        isWaterproof = true
                                    }
                                    "LIGHT" -> {
                                        fillGrams = "100"
                                        minTemp = "43"
                                        maxTemp = "49"
                                    }
                                    "MEDIUM" -> {
                                        fillGrams = "200"
                                        minTemp = "30"
                                        maxTemp = "42"
                                    }
                                    "HEAVY" -> {
                                        fillGrams = "350"
                                        minTemp = "18"
                                        maxTemp = "29"
                                    }
                                }
                            },
                            label = { Text(cat) },
                            modifier = Modifier.testTag("blanket_cat_$cat")
                        )
                    }
                }

                // Fill Weight
                OutlinedTextField(
                    value = fillGrams,
                    onValueChange = { fillGrams = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Fill Insulation (grams)") },
                    placeholder = { Text("0, 100, 200, 360, 400") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                // Temperature Range
                Text(
                    text = "Recommended Temperature Range (°F)",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "The app will recommend this blanket when tonight's perceived temperature is within this bracket.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = minTemp,
                        onValueChange = { minTemp = it },
                        label = { Text("Min Temp (°F)") },
                        placeholder = { Text("e.g. 25") },
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("blanket_min_temp_input"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = maxTemp,
                        onValueChange = { maxTemp = it },
                        label = { Text("Max Temp (°F)") },
                        placeholder = { Text("e.g. 40") },
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("blanket_max_temp_input"),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                // Features Checkboxes
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isWaterproof,
                        onCheckedChange = { isWaterproof = it },
                        modifier = Modifier.testTag("waterproof_checkbox")
                    )
                    Text("Waterproof / Turnout Shell", style = MaterialTheme.typography.bodyMedium)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = hasNeckCover,
                        onCheckedChange = { hasNeckCover = it },
                        modifier = Modifier.testTag("neck_cover_checkbox")
                    )
                    Text("Has Detachable Neck Cover / Hood", style = MaterialTheme.typography.bodyMedium)
                }

                // Notes
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Description / Brand (Optional)") },
                    placeholder = { Text("e.g. 1200D Ripstop, breathable nylon liner") },
                    maxLines = 2,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("blanket_notes_input"),
                    shape = RoundedCornerShape(12.dp)
                )

                if (errorText != null) {
                    Text(
                        text = errorText!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isBlank()) {
                        errorText = "Please enter a blanket name."
                        return@Button
                    }
                    val min = minTemp.toIntOrNull()
                    val max = maxTemp.toIntOrNull()
                    if (min == null || max == null) {
                        errorText = "Please enter valid numbers for Min and Max temperatures."
                        return@Button
                    }
                    if (min > max) {
                        errorText = "Min temperature cannot be higher than Max temperature."
                        return@Button
                    }

                    val entity = CustomBlanketEntity(
                        id = blanketToEdit?.id ?: 0L,
                        name = name.trim(),
                        category = category,
                        fillGrams = fillGrams.toIntOrNull() ?: 0,
                        minTempF = min,
                        maxTempF = max,
                        isWaterproof = isWaterproof,
                        hasNeckCover = hasNeckCover,
                        notes = notes.trim()
                    )
                    onSave(entity)
                    onDismiss()
                },
                modifier = Modifier.testTag("save_custom_blanket_button")
            ) {
                Text("Save Blanket")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("cancel_custom_blanket_button")
            ) {
                Text("Cancel")
            }
        }
    )
}
