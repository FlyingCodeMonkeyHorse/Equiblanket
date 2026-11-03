package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.CustomBlanketEntity
import com.example.ui.components.AddEditCustomBlanketDialog
import com.example.ui.theme.*

data class BlanketMatrixRow(
    val tempRangeF: String,
    val tempRangeC: String,
    val unclippedRecommendation: String,
    val clippedRecommendation: String
)

@Composable
fun GuideScreen(
    customBlankets: List<CustomBlanketEntity> = emptyList(),
    onSaveCustomBlanket: (CustomBlanketEntity) -> Unit = {},
    onDeleteCustomBlanket: (CustomBlanketEntity) -> Unit = {},
    onResetDefaultBlankets: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showBlanketDialog by remember { mutableStateOf(false) }
    var blanketToEdit by remember { mutableStateOf<CustomBlanketEntity?>(null) }
    var blanketToDelete by remember { mutableStateOf<CustomBlanketEntity?>(null) }
    var showResetConfirm by remember { mutableStateOf(false) }

    val matrixRows = listOf(
        BlanketMatrixRow(
            tempRangeF = "60°F and Above",
            tempRangeC = "15°C+",
            unclippedRecommendation = "No Blanket (Naked)",
            clippedRecommendation = "No Blanket or Scrim / Fly Sheet"
        ),
        BlanketMatrixRow(
            tempRangeF = "50°F to 59°F",
            tempRangeC = "10°C to 15°C",
            unclippedRecommendation = "No Blanket (Rain Sheet if raining)",
            clippedRecommendation = "Light Sheet (0g - 100g)"
        ),
        BlanketMatrixRow(
            tempRangeF = "40°F to 49°F",
            tempRangeC = "4°C to 9°C",
            unclippedRecommendation = "No Blanket / Light Turnout (100g)",
            clippedRecommendation = "Medium Turnout (200g - 250g)"
        ),
        BlanketMatrixRow(
            tempRangeF = "30°F to 39°F",
            tempRangeC = "-1°C to 3°C",
            unclippedRecommendation = "Medium Turnout (200g - 250g)",
            clippedRecommendation = "Heavy Turnout (300g - 360g)"
        ),
        BlanketMatrixRow(
            tempRangeF = "20°F to 29°F",
            tempRangeC = "-7°C to -2°C",
            unclippedRecommendation = "Heavy Turnout (300g - 360g)",
            clippedRecommendation = "Heavy Turnout + Neck Hood (360g - 400g)"
        ),
        BlanketMatrixRow(
            tempRangeF = "Below 20°F",
            tempRangeC = "< -7°C",
            unclippedRecommendation = "Heavy Turnout + Hood (360g - 400g)",
            clippedRecommendation = "Heavy Turnout + Under Rug / Liner + Hood"
        )
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 100.dp)
    ) {
        item {
            Text(
                text = "Blanket Wardrobe & Guide",
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Define your custom blanket types with tailored temperature ranges, or view veterinary guidelines.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Custom Blanket Types Section Header
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("custom_wardrobe_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "CUSTOM BLANKET TYPES",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Associated with specific temperature brackets for your recommendations",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Button(
                            onClick = {
                                blanketToEdit = null
                                showBlanketDialog = true
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("add_custom_blanket_button")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Add Type")
                        }
                    }

                    HorizontalDivider()

                    if (customBlankets.isEmpty()) {
                        Text(
                            text = "No custom blankets defined yet. Standard equine guidelines are active.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            customBlankets.forEach { blanket ->
                                CustomBlanketRowItem(
                                    blanket = blanket,
                                    onEdit = {
                                        blanketToEdit = blanket
                                        showBlanketDialog = true
                                    },
                                    onDelete = {
                                        blanketToDelete = blanket
                                    }
                                )
                            }
                        }
                    }

                    // Reset button
                    TextButton(
                        onClick = { showResetConfirm = true },
                        modifier = Modifier.align(Alignment.End).testTag("reset_defaults_button")
                    ) {
                        Icon(Icons.Outlined.RestartAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Reset to Equine Defaults")
                    }
                }
            }
        }

        // Standard Equine Temperature Chart
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("blanket_chart_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "VETERINARY TEMPERATURE MATRIX",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = ForestGreenPrimary
                    )

                    matrixRows.forEach { row ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = row.tempRangeF,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = row.tempRangeC,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Spacer(Modifier.height(6.dp))
                                Row(modifier = Modifier.fillMaxWidth()) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("NATURAL COAT", style = MaterialTheme.typography.labelSmall, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text(row.unclippedRecommendation, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                                    }
                                    Spacer(Modifier.width(8.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("BODY CLIPPED", style = MaterialTheme.typography.labelSmall, fontSize = 9.sp, color = SaddleTanSecondary)
                                        Text(row.clippedRecommendation, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = SaddleTanSecondary)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Golden Rules for Horse Owners
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("blanket_golden_rules_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = ForestGreenContainer)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Outlined.Lightbulb,
                            contentDescription = null,
                            tint = ForestGreenPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(
                            text = "Essential Barn Rules",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = OnForestGreenContainer
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        BarnRuleItem(
                            number = "1",
                            title = "The Ear & Shoulder Check",
                            description = "Slide your hand under the blanket behind the wither and touch the base of the ears. Warm and dry = ideal comfort. Clammy or sweaty = over-blanketed. Cold or trembling = needs more fill."
                        )
                        BarnRuleItem(
                            number = "2",
                            title = "Wet Hair Hazard",
                            description = "Rain collapses the air pockets in horse hair that naturally insulate them. A wet horse at 45°F loses heat faster than a dry horse at 25°F. Always use a waterproof rain sheet in damp weather."
                        )
                        BarnRuleItem(
                            number = "3",
                            title = "Overheating Is Worse Than Chills",
                            description = "Horses handle cold far better than heat. Over-blanketing causes sweating beneath the rug, which leads to chills when the sweat cools, as well as dermatological conditions."
                        )
                        BarnRuleItem(
                            number = "4",
                            title = "Never Blanket a Wet Horse",
                            description = "Putting an impermeable waterproof turnout on a damp horse traps moisture against the skin. Cool them out with an absorbent fleece or cooler before putting on their night blanket."
                        )
                    }
                }
            }
        }
    }

    if (showBlanketDialog) {
        AddEditCustomBlanketDialog(
            blanketToEdit = blanketToEdit,
            onDismiss = { showBlanketDialog = false },
            onSave = onSaveCustomBlanket
        )
    }

    if (blanketToDelete != null) {
        AlertDialog(
            onDismissRequest = { blanketToDelete = null },
            title = { Text("Delete ${blanketToDelete!!.name}?") },
            text = { Text("Are you sure you want to remove this custom blanket definition?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        val b = blanketToDelete ?: return@TextButton
                        onDeleteCustomBlanket(b)
                        blanketToDelete = null
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { blanketToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showResetConfirm) {
        AlertDialog(
            onDismissRequest = { showResetConfirm = false },
            title = { Text("Reset to Equine Defaults?") },
            text = { Text("This will restore the standard veterinary turnout and sheet temperature ranges.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onResetDefaultBlankets()
                        showResetConfirm = false
                    }
                ) {
                    Text("Reset")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirm = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun CustomBlanketRowItem(
    blanket: CustomBlanketEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val badgeColor = when (blanket.category.uppercase()) {
        "HEAVY" -> BlanketHeavyRed
        "MEDIUM" -> BlanketMediumOrange
        "LIGHT" -> BlanketLightTeal
        "SHEET" -> BlanketSheetBlue
        else -> BlanketNoneGreen
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
        modifier = Modifier.fillMaxWidth().testTag("custom_blanket_row_${blanket.name}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = blanket.name,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.width(8.dp))
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = badgeColor.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "${blanket.fillGrams}g",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = badgeColor
                        )
                    }
                }

                Spacer(Modifier.height(4.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Range: ${blanket.tempRangeDisplay}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    if (blanket.isWaterproof) {
                        Text("• Waterproof", style = MaterialTheme.typography.bodySmall, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    if (blanket.hasNeckCover) {
                        Text("• Neck Hood", style = MaterialTheme.typography.bodySmall, fontSize = 11.sp, color = SaddleTanSecondary)
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onEdit, modifier = Modifier.size(32.dp).testTag("edit_blanket_${blanket.name}")) {
                    Icon(Icons.Outlined.Edit, contentDescription = "Edit Blanket", modifier = Modifier.size(18.dp))
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp).testTag("delete_blanket_${blanket.name}")) {
                    Icon(Icons.Outlined.Delete, contentDescription = "Delete Blanket", tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f), modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
private fun BarnRuleItem(number: String, title: String, description: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Surface(
            shape = RoundedCornerShape(6.dp),
            color = ForestGreenPrimary,
            modifier = Modifier.size(22.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = number,
                    color = Color.White,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Spacer(Modifier.width(10.dp))
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = OnForestGreenContainer
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = OnForestGreenContainer.copy(alpha = 0.85f)
            )
        }
    }
}
