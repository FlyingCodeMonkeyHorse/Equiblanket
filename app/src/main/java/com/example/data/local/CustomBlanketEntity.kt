package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "custom_blankets")
data class CustomBlanketEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val category: String = "MEDIUM", // NONE, SHEET, LIGHT, MEDIUM, HEAVY
    val fillGrams: Int = 200,
    val minTempF: Int = 30,
    val maxTempF: Int = 42,
    val isWaterproof: Boolean = true,
    val hasNeckCover: Boolean = false,
    val notes: String = ""
) {
    val tempRangeDisplay: String
        get() = if (minTempF <= -15) "Below ${maxTempF + 1}°F" else "$minTempF°F to $maxTempF°F"
}
